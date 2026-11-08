package com.daishin.pdf;

import com.daishin.pdf.SchedulerConfig.SchedulerConfiguration;
import com.daishin.pdf.service.*;
import com.daishin.pdf.util.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.DriverManager;
import java.time.*;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"scheduler.enabled=false", "spring.main.banner-mode=off"})
@EnabledIfSystemProperty(named = "dspdf.mssql.it", matches = "true")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MssqlApiIntegrationTest {
    private static final String RUN = LocalDateTime.now().toString().replaceAll("[^0-9]", "");
    private static final String DATABASE = "dspdf_it_" + RUN;
    private static final Path ROOT = Path.of("target", "mssql-it", RUN).toAbsolutePath();
    private static final String PASSWORD = System.getenv().getOrDefault("MSSQL_TEST_PASSWORD", "Dspdf_Test_Only!2026");
    // Only the dedicated local container is used; never accept a production JDBC URL here.
    private static final String SERVER = "jdbc:sqlserver://127.0.0.1:11433;encrypt=true;trustServerCertificate=true;loginTimeout=10;";
    private static final ObjectMapper JSON = new ObjectMapper().findAndRegisterModules();

    @LocalServerPort int port;
    @Autowired JdbcTemplate jdbc;
    @Autowired ApplicationContext context;
    @Autowired Utils utils;
    @Autowired MasterSaveService masterSave;
    @Autowired MasterInfoService masterInfo;
    @Autowired MasterDeleteService masterDelete;
    @Autowired StatusInfoService statusInfo;
    @Autowired DetailDeleteService detailDelete;
    @Autowired DetailInfoService detailInfo;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private final List<Map<String, Object>> results = new ArrayList<>();
    private final Set<String> exercisedRoutes = new TreeSet<>();
    private Map<String, Object> current;
    private List<Map<String, Object>> exchanges;
    private List<String> observations;
    private SchedulerConfiguration scheduler;

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) throws Exception {
        Files.createDirectories(ROOT.resolve("receiving"));
        Files.createDirectories(ROOT.resolve("complete"));
        try (var connection = DriverManager.getConnection(SERVER + "databaseName=master", "sa", PASSWORD);
             var statement = connection.createStatement()) {
            assertEquals("Microsoft SQL Server", connection.getMetaData().getDatabaseProductName());
            assertTrue(DATABASE.matches("dspdf_it_[0-9]+"));
            statement.execute("CREATE DATABASE [" + DATABASE + "]");
        }
        registry.add("spring.datasource.url", () -> SERVER + "databaseName=" + DATABASE);
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> PASSWORD);
        registry.add("storage.receivingPath", () -> ROOT.resolve("receiving") + "\\");
        registry.add("storage.completePath", () -> ROOT.resolve("complete") + "\\");
        registry.add("storage.logPath", () -> ROOT.resolve("logs").toString());
    }

    @BeforeAll
    void schema() throws Exception {
        new ResourceDatabasePopulator(new ClassPathResource("sqlserver-schema.sql"))
                .execute(Objects.requireNonNull(jdbc.getDataSource()));
        scheduler = new SchedulerConfiguration(utils, masterSave, masterInfo, masterDelete,
                statusInfo, detailDelete, detailInfo);
        ReflectionTestUtils.setField(scheduler, "receivingPath", ROOT.resolve("receiving") + "\\");
        ReflectionTestUtils.setField(scheduler, "completePath", ROOT.resolve("complete") + "\\");
        Files.write(ROOT.resolve("sample.pdf"), samplePdf());
    }

    private void seed() throws Exception {
        for (String name : List.of("receiving", "complete")) {
            Path directory = ROOT.resolve(name);
            try (var paths = Files.walk(directory)) {
                for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    if (!path.equals(directory)) Files.delete(path);
                }
            }
        }
        jdbc.execute("TRUNCATE TABLE DETAIL");
        jdbc.execute("TRUNCATE TABLE MASTER");
        jdbc.execute("TRUNCATE TABLE ERROR");
        insertMaster("BATCH-SEED", 2, 2, 200, "ARRANGEMENT");
        insertMaster("RT-SEED", 0, 1, 100, "REAL_TIME");
        for (int i = 1; i <= 3; i++) insertMaster("PAGE-0" + i, 2, 1, 100, "ARRANGEMENT");
        jdbc.update("INSERT INTO ERROR(ERROR_MESSAGE, CREATE_DATE, MASTER_KEY, ERROR_CODE) " +
                "VALUES (?,CURRENT_TIMESTAMP,?,?)", "샘플 오류 메시지 한글 주소 검증", "BATCH-SEED", "SAMPLE_ERROR");
    }

    private void insertMaster(String key, int total, int sent, int status, String type) {
        jdbc.update("INSERT INTO MASTER VALUES (?,?,?,?,?,?,?)", key, total, status, sent,
                LocalDateTime.of(2026, 1, 1, 10, 0), LocalDateTime.of(2026, 1, 1, 10, 0), type);
    }

    private DynamicTest task(String id, String title, String expected, Executable action) {
        return DynamicTest.dynamicTest(id + " " + title, () -> {
            current = new LinkedHashMap<>();
            exchanges = new ArrayList<>();
            observations = new ArrayList<>();
            current.put("id", id);
            current.put("title", title);
            current.put("expected", expected);
            current.put("exchanges", exchanges);
            current.put("observations", observations);
            long started = System.nanoTime();
            try {
                seed();
                action.execute();
                assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM ERROR WHERE ERROR_CODE='SQL_ERROR'", Integer.class),
                        "Repositories can swallow SQL failures; verify the ERROR table as well");
                current.put("result", "PASS");
            } catch (Throwable failure) {
                current.put("result", "FAIL");
                current.put("failure", failure.toString());
                throw failure;
            } finally {
                current.put("elapsedMs", (System.nanoTime() - started) / 1_000_000);
                results.add(current);
            }
        });
    }

    @TestFactory
    Stream<DynamicTest> allApis() {
        return Stream.of(
            task("API-001", "전체 경로와 스케줄 격리", "업무 경로 5개 및 자동 스케줄 비활성", () -> {
                Set<String> routes = new TreeSet<>();
                context.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class)
                    .getHandlerMethods().forEach((mapping, method) -> {
                        if (method.getBeanType().getPackageName().equals("com.daishin.pdf.controller"))
                            mapping.getMethodsCondition().getMethods().forEach(verb ->
                                mapping.getPatternValues().forEach(path -> routes.add(verb + " " + path)));
                    });
                assertEquals(Set.of("POST /detail", "GET /masters", "POST /masters/update", "GET /errors", "GET /error/detail"), routes);
                assertTrue(context.getBeansOfType(SchedulerConfiguration.class).isEmpty());
                observations.add("등록 경로=" + routes + "; scheduler.enabled=false");
            }),
            task("API-002", "단일 우편 정상 접수", "HTTP 200 및 OK/SUCCESS, 한글 DB 및 PDF 보존", () -> {
                var response = upload("SINGLE-001", 1, 1, true, true);
                success(response);
                var detail = jdbc.queryForMap("SELECT * FROM DETAIL WHERE PK='SINGLE-001_1'");
                assertEquals("가상수신자", detail.get("RECV_NM"));
                assertEquals("01234", detail.get("RECV_POST_CD"));
                String expectedKey = LocalTime.now().isBefore(LocalTime.of(14, 0)) ? LocalDate.now().toString() : LocalDate.now().plusDays(1).toString();
                assertEquals(expectedKey, detail.get("MASTER"));
                assertArrayEquals(samplePdf(), Files.readAllBytes(Path.of(detail.get("PDF_PATH").toString()).resolve("SINGLE-001-1.pdf")));
                assertEquals(100, jdbc.queryForObject("SELECT STATUS FROM MASTER WHERE MASTER_KEY=?", Integer.class, expectedKey));
                observations.add("DB=" + detail + "; PDF 바이트 동일; MASTER.STATUS=100");
            }),
            task("API-003", "대량 첫 요청", "HTTP 200 및 상태 100, SEND_CNT=1", () -> {
                success(upload("BULK-003", 2, 1, true, true));
                assertEquals(1, jdbc.queryForObject("SELECT SEND_CNT FROM MASTER WHERE MASTER_KEY='BULK-003'", Integer.class));
                assertEquals(100, jdbc.queryForObject("SELECT STATUS FROM MASTER WHERE MASTER_KEY='BULK-003'", Integer.class));
                observeMaster("BULK-003");
            }),
            task("API-004", "대량 마지막 요청과 파일 이동", "2건 접수, 상태 200, JSON 및 PDF 2개 complete 이동", () -> {
                success(upload("BULK-004", 2, 1, true, true));
                success(upload("BULK-004", 2, 2, true, true));
                assertEquals(2, jdbc.queryForObject("SELECT SEND_CNT FROM MASTER WHERE MASTER_KEY='BULK-004'", Integer.class));
                assertEquals(200, jdbc.queryForObject("SELECT STATUS FROM MASTER WHERE MASTER_KEY='BULK-004'", Integer.class));
                Path dir = ROOT.resolve("complete/BULK-004");
                assertTrue(Files.exists(dir.resolve("BULK-004-1.pdf")));
                assertTrue(Files.exists(dir.resolve("BULK-004-2.pdf")));
                var json = JSON.readTree(Files.readString(dir.resolve("BULK-004.json"), StandardCharsets.UTF_8));
                assertEquals(2, json.path("detail").size());
                assertEquals("가상수신자", json.path("detail").get(0).path("RECV_NM").asText());
                assertEquals("테스트시 가상로 123", json.path("detail").get(0).path("RECV_ADDR").asText());
                assertFalse(Files.exists(ROOT.resolve("receiving/BULK-004")));
                assertEquals(2, jdbc.queryForObject("SELECT COUNT(*) FROM DETAIL WHERE MASTER='BULK-004' AND PDF_PATH=?", Integer.class, dir.toString()));
                observations.add("JSON=" + json + "; receiving 디렉터리 제거; complete PDF 2건 존재");
                observeMaster("BULK-004");
            }),
            task("API-005", "중복 접수", "HTTP 200 및 ERROR/DUPLICATE, 원본 1건 유지", () -> {
                success(upload("DUP-005", 2, 1, true, true));
                var response = upload("DUP-005", 2, 1, true, true);
                businessError(response, "DUPLICATE");
                assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM DETAIL WHERE TR_KEY='DUP-005'", Integer.class));
                assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM ERROR WHERE ERROR_CODE='DUPLICATE'", Integer.class));
                observations.add("DETAIL=1; DUPLICATE 오류 이력=1");
            }),
            task("API-006", "필수 수신자 누락", "HTTP 200 및 ERROR/MISSING_VALUE, 저장 없음", () -> {
                businessError(upload("MISSING-006", 2, 1, false, true), "MISSING_VALUE : RECV_NM");
                assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM DETAIL", Integer.class));
                observations.add("DETAIL=0; 누락 오류 이력=" + jdbc.queryForObject("SELECT COUNT(*) FROM ERROR WHERE ERROR_CODE LIKE 'MISSING_VALUE%'", Integer.class));
            }),
            task("API-007", "파일 누락", "HTTP 200 및 ERROR/MISSING_VALUE", () ->
                businessError(upload("NOFILE-007", 2, 1, true, false), "pdf file")),
            task("API-008", "잘못된 숫자 바인딩", "HTTP 400", () ->
                status(form("/detail", Map.of("TOTAL_SEND_CNT", "abc")), 400)),
            task("API-009", "작업 목록 기본 조회", "HTTP 200 및 JSP 실제 렌더링", () -> {
                var response = get("/masters", Map.of()); status(response, 200);
                assertTrue(response.body().contains("BATCH-SEED"));
                assertTrue(response.body().contains("RT-SEED"));
            }),
            task("API-010", "숫자 상태 검색", "HTTP 200, STATUS=200 작업만 표시", () -> {
                var response = get("/masters", Map.of("cate", "STATUS", "search", "200")); status(response, 200);
                assertTrue(response.body().contains("<td>BATCH-SEED</td>"));
                assertFalse(response.body().contains("<td>RT-SEED</td>"));
            }),
            task("API-011", "타입 한글 검색", "실시간을 REAL_TIME으로 검색", () -> {
                var response = get("/masters", Map.of("cate", "TYPE", "search", "실시간")); status(response, 200);
                assertTrue(response.body().contains("<td>RT-SEED</td>"));
                assertFalse(response.body().contains("<td>BATCH-SEED</td>"));
            }),
            task("API-012", "작업 페이징과 동일 시각 정렬", "2페이지 PAGE-03만 표시", () -> {
                var response = get("/masters", Map.of("search", "PAGE-", "pageElement", "2", "page", "2", "sort", "ASC")); status(response, 200);
                assertTrue(response.body().contains("<td>PAGE-03</td>"));
                assertFalse(response.body().contains("<td>PAGE-01</td>"));
                assertFalse(response.body().contains("<td>PAGE-02</td>"));
            }),
            task("API-013", "작업 빈 결과", "HTTP 200, 총 검색 건수 0", () -> {
                var response = get("/masters", Map.of("search", "NOT-FOUND")); status(response, 200);
                assertTrue(response.body().contains("총 검색 건수 : 0"));
            }),
            task("API-014", "허용되지 않은 검색 컬럼", "HTTP 400, 테이블 보존", () -> {
                status(get("/masters", Map.of("cate", "MASTER_KEY; DROP TABLE MASTER;--")), 400);
                assertEquals(5, jdbc.queryForObject("SELECT COUNT(*) FROM MASTER", Integer.class));
            }),
            task("API-015", "잘못된 정렬", "HTTP 400", () ->
                status(get("/masters", Map.of("sort", "DESC;--")), 400)),
            task("API-016", "페이지 크기 0", "HTTP 400", () ->
                status(get("/masters", Map.of("pageElement", "0")), 400)),
            task("API-017", "일반 상태 변경", "HTTP 200, 상태 300 및 STATUS_TIME 갱신", () -> {
                status(form("/masters/update", Map.of("statusData", "{\"BATCH-SEED\":\"300\"}")), 200);
                assertEquals(300, jdbc.queryForObject("SELECT STATUS FROM MASTER WHERE MASTER_KEY='BATCH-SEED'", Integer.class));
                assertTrue(jdbc.queryForObject("SELECT STATUS_TIME FROM MASTER WHERE MASTER_KEY='BATCH-SEED'", LocalDateTime.class).isAfter(LocalDateTime.now().minusMinutes(2)));
                observeMaster("BATCH-SEED");
            }),
            task("API-018", "실시간 수신중 복귀", "TOTAL_SEND_CNT가 0으로 초기화", () -> {
                jdbc.update("UPDATE MASTER SET STATUS=300,TOTAL_SEND_CNT=4 WHERE MASTER_KEY='RT-SEED'");
                status(form("/masters/update", Map.of("statusData", "{\"RT-SEED\":\"100\"}")), 200);
                assertEquals(0, jdbc.queryForObject("SELECT TOTAL_SEND_CNT FROM MASTER WHERE MASTER_KEY='RT-SEED'", Integer.class));
                observeMaster("RT-SEED");
            }),
            task("API-019", "실시간 수신 완료", "전체 건수를 실제 DETAIL 수로 갱신", () -> {
                success(upload("RT-019", 1, 1, true, true));
                String key = jdbc.queryForObject("SELECT MASTER FROM DETAIL WHERE PK='RT-019_1'", String.class);
                status(form("/masters/update", Map.of("statusData", JSON.writeValueAsString(Map.of(key, "200")))), 200);
                assertEquals(1, jdbc.queryForObject("SELECT TOTAL_SEND_CNT FROM MASTER WHERE MASTER_KEY=?", Integer.class, key));
                observeMaster(key);
            }),
            task("API-020", "잘못된 상태 JSON", "HTTP 400", () ->
                status(form("/masters/update", Map.of("statusData", "not-json")), 400)),
            task("API-021", "없는 작업 상태 변경", "HTTP 404", () ->
                status(form("/masters/update", Map.of("statusData", "{\"UNKNOWN\":\"300\"}")), 404)),
            task("API-022", "미등록 상태 코드", "HTTP 400, 기존 상태 보존", () -> {
                status(form("/masters/update", Map.of("statusData", "{\"BATCH-SEED\":\"777\"}")), 400);
                assertEquals(200, jdbc.queryForObject("SELECT STATUS FROM MASTER WHERE MASTER_KEY='BATCH-SEED'", Integer.class));
            }),
            task("API-023", "빈 상태 변경", "HTTP 400", () ->
                status(form("/masters/update", Map.of("statusData", "{}")), 400)),
            task("API-024", "오류 목록", "HTTP 200, SAMPLE_ERROR 렌더링", () -> {
                var response = get("/errors", Map.of()); status(response, 200);
                assertTrue(response.body().contains("SAMPLE_ERROR"));
            }),
            task("API-025", "오류 날짜 문자열 검색", "ISO 날짜 검색 성공", () -> {
                var response = get("/errors", Map.of("cate", "CREATE_DATE", "search", LocalDate.now().toString())); status(response, 200);
                assertTrue(response.body().contains("SAMPLE_ERROR"));
            }),
            task("API-026", "오류 페이징", "2페이지의 두 번째 오류만 표시", () -> {
                jdbc.update("INSERT INTO ERROR VALUES (?,CURRENT_TIMESTAMP,?,?)", "두 번째 오류", "SECOND-ERROR", "SECOND_CODE");
                var response = get("/errors", Map.of("pageElement", "1", "page", "2", "sort", "ASC")); status(response, 200);
                assertTrue(response.body().contains("SECOND_CODE"));
                assertFalse(response.body().contains("SAMPLE_ERROR"));
            }),
            task("API-027", "오류 날짜 범위", "범위 밖 데이터 제외", () -> {
                var response = get("/errors", Map.of("startDate", "2000-01-01T00:00", "endDate", "2000-01-02T00:00")); status(response, 200);
                assertFalse(response.body().contains("SAMPLE_ERROR"));
            }),
            task("API-028", "역전된 날짜 범위", "HTTP 400", () ->
                status(get("/errors", Map.of("startDate", "2026-02-01T00:00", "endDate", "2026-01-01T00:00")), 400)),
            task("API-029", "오류 검색 SQL 입력 차단", "HTTP 400", () ->
                status(get("/errors", Map.of("sortCate", "CREATE_DATE; DROP TABLE ERROR;--")), 400)),
            task("API-030", "오류 상세 정상", "HTTP 200 및 한글 원문", () -> {
                var response = get("/error/detail", Map.of("errorId", "1")); status(response, 200);
                assertTrue(response.body().contains("샘플 오류 메시지 한글 주소 검증"));
            }),
            task("API-031", "없는 오류 상세", "HTTP 404", () ->
                status(get("/error/detail", Map.of("errorId", "999999")), 404)),
            task("API-032", "잘못된 오류 ID", "HTTP 400", () ->
                status(get("/error/detail", Map.of("errorId", "abc")), 400)),
            task("API-033", "필수 파라미터 누락", "HTTP 400", () ->
                status(get("/error/detail", Map.of()), 400)),
            task("SCH-001", "시간 경과 자동 상태 전환", "오래된 AUTO 300만 400으로 전환", () -> {
                jdbc.update("UPDATE MASTER SET STATUS=300 WHERE MASTER_KEY='PAGE-01'");
                jdbc.update("UPDATE MASTER SET STATUS=300,STATUS_TIME=DATEADD(minute,10,CURRENT_TIMESTAMP) WHERE MASTER_KEY='PAGE-02'");
                scheduler.changeStatus();
                assertEquals(400, jdbc.queryForObject("SELECT STATUS FROM MASTER WHERE MASTER_KEY='PAGE-01'", Integer.class));
                assertEquals(300, jdbc.queryForObject("SELECT STATUS FROM MASTER WHERE MASTER_KEY='PAGE-02'", Integer.class));
                observeMaster("PAGE-01"); observeMaster("PAGE-02");
            }),
            task("SCH-002", "1개월 경과 삭제", "대상 상태의 오래된 MASTER와 DETAIL만 삭제, ERROR 및 PDF 보존", () -> {
                success(upload("OLD-DELETE", 2, 1, true, true));
                jdbc.update("UPDATE MASTER SET STATUS=400,STATUS_TIME=DATEADD(month,-2,CURRENT_TIMESTAMP) WHERE MASTER_KEY='OLD-DELETE'");
                jdbc.update("UPDATE MASTER SET STATUS=400,STATUS_TIME=CURRENT_TIMESTAMP WHERE MASTER_KEY='PAGE-02'");
                jdbc.update("INSERT INTO ERROR VALUES (?,CURRENT_TIMESTAMP,?,?)", "삭제 후 남는 오류", "OLD-DELETE", "RETAINED");
                scheduler.delete();
                assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM MASTER WHERE MASTER_KEY='OLD-DELETE'", Integer.class));
                assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM DETAIL WHERE MASTER='OLD-DELETE'", Integer.class));
                assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM MASTER WHERE MASTER_KEY='PAGE-02'", Integer.class));
                assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM ERROR WHERE ERROR_CODE='RETAINED'", Integer.class));
                assertTrue(Files.exists(ROOT.resolve("receiving/OLD-DELETE/OLD-DELETE-1.pdf")));
                observations.add("오래된 MASTER/DETAIL=0; 최근 MASTER=1; ERROR=1; PDF 존재");
            }),
            task("SCH-003", "당일 단일 마감", "당일 묶음 상태 200, 전체 건수 1, JSON과 PDF 이동", () -> {
                String today = LocalDate.now().toString();
                success(upload("CLOSE-TODAY", 1, 1, true, true));
                String original = jdbc.queryForObject("SELECT MASTER FROM DETAIL WHERE PK='CLOSE-TODAY_1'", String.class);
                if (!today.equals(original)) {
                    Files.move(ROOT.resolve("receiving/" + original), ROOT.resolve("receiving/" + today));
                    jdbc.update("UPDATE DETAIL SET MASTER=?,PDF_PATH=? WHERE PK='CLOSE-TODAY_1'", today, ROOT.resolve("receiving/" + today) + "\\");
                    jdbc.update("UPDATE MASTER SET MASTER_KEY=? WHERE MASTER_KEY=?", today, original);
                }
                scheduler.run();
                assertEquals(200, jdbc.queryForObject("SELECT STATUS FROM MASTER WHERE MASTER_KEY=?", Integer.class, today));
                assertEquals(1, jdbc.queryForObject("SELECT TOTAL_SEND_CNT FROM MASTER WHERE MASTER_KEY=?", Integer.class, today));
                assertTrue(Files.exists(ROOT.resolve("complete/" + today + "/" + today + ".json")));
                observeMaster(today);
            }),
            task("API-035", "전체 작업 정렬 조합", "허용 컬럼 5개와 방향 2개 모두 조회 성공", () -> {
                for (String column : List.of("MASTER_KEY", "STATUS", "RECEIVED_TIME", "STATUS_TIME", "TYPE"))
                    for (String direction : List.of("ASC", "DESC")) {
                        var response = get("/masters", Map.of("sortCate", column, "sort", direction));
                        status(response, 200);
                        assertTrue(response.body().contains("<td>BATCH-SEED</td>"));
                    }
            }),
            task("API-036", "오류 코드 검색", "SAMPLE_ERROR 검색과 다른 코드 제외", () -> {
                var response = get("/errors", Map.of("cate", "ERROR_CODE", "search", "SAMPLE_ERROR"));
                status(response, 200); assertTrue(response.body().contains("SAMPLE_ERROR"));
                response = get("/errors", Map.of("cate", "ERROR_CODE", "search", "NO_MATCH"));
                status(response, 200); assertFalse(response.body().contains("SAMPLE_ERROR"));
            }),
            task("API-037", "잘못된 날짜 형식", "HTTP 400", () ->
                status(get("/errors", Map.of("startDate", "not-a-date")), 400)),
            task("API-038", "페이지 입력 범위", "음수 페이지와 200 초과 크기 HTTP 400", () -> {
                status(get("/masters", Map.of("page", "-1")), 400);
                status(get("/errors", Map.of("pageElement", "201")), 400);
            }),
            task("API-039", "숫자가 아닌 상태 코드", "HTTP 400 및 상태 보존", () -> {
                status(form("/masters/update", Map.of("statusData", "{\"BATCH-SEED\":\"abc\"}")), 400);
                assertEquals(200, jdbc.queryForObject("SELECT STATUS FROM MASTER WHERE MASTER_KEY='BATCH-SEED'", Integer.class));
            }),
            task("API-040", "여러 작업 변경 사전 검사", "없는 작업이 섞이면 HTTP 404 및 정상 작업도 보존", () -> {
                status(form("/masters/update", Map.of("statusData", "{\"BATCH-SEED\":\"300\",\"UNKNOWN\":\"300\"}")), 404);
                assertEquals(200, jdbc.queryForObject("SELECT STATUS FROM MASTER WHERE MASTER_KEY='BATCH-SEED'", Integer.class));
            }),
            task("API-034", "모든 공개 경로의 HTTP 실행 확인", "등록 업무 경로 5개 모두 실제 호출", () -> {
                assertEquals(Set.of("POST /detail", "GET /masters", "POST /masters/update", "GET /errors", "GET /error/detail"), exercisedRoutes);
                observations.add("HTTP 실행 경로=" + exercisedRoutes);
            })
        );
    }

    private void observeMaster(String key) {
        observations.add("MASTER=" + jdbc.queryForMap("SELECT * FROM MASTER WHERE MASTER_KEY=?", key));
    }

    private HttpResponse<String> get(String path, Map<String, String> fields) throws Exception {
        return exchange("GET", path, fields, null, null);
    }

    private HttpResponse<String> form(String path, Map<String, String> fields) throws Exception {
        return exchange("POST", path, fields, encode(fields).getBytes(StandardCharsets.UTF_8), "application/x-www-form-urlencoded;charset=UTF-8");
    }

    private HttpResponse<String> upload(String key, int total, int number, boolean receiver, boolean file) throws Exception {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("TR_KEY", key); fields.put("TOTAL_SEND_CNT", "" + total);
        fields.put("DLV_TYPE_CD", "01"); fields.put("PRINT_TYPE_NM", "양면");
        fields.put("PAGE_CNT", "1"); fields.put("RET_YN", "N"); fields.put("RECV_NUM", "" + number);
        fields.put("DM_LINK_KEY", "DM-" + key); fields.put("CIFNO", "00001234"); fields.put("DLV_CD", "01");
        if (receiver) fields.put("RECV_NM", "가상수신자");
        fields.put("RECV_POST_CD", "01234"); fields.put("RECV_ADDR", "테스트시 가상로 123");
        fields.put("RECV_ADDR_DETAIL", "테스트동 101호");
        String boundary = "DspdfBoundary" + UUID.randomUUID().toString().replace("-", "");
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        for (var field : fields.entrySet()) {
            body.write(("--" + boundary + "\r\nContent-Disposition: form-data; name=\"" + field.getKey() + "\"\r\n\r\n" + field.getValue() + "\r\n").getBytes(StandardCharsets.UTF_8));
        }
        if (file) {
            String filename = key + "-" + number + ".pdf";
            body.write(("--" + boundary + "\r\nContent-Disposition: form-data; name=\"File\"; filename=\"" + filename + "\"\r\nContent-Type: application/pdf\r\n\r\n").getBytes(StandardCharsets.UTF_8));
            body.write(samplePdf()); body.write("\r\n".getBytes(StandardCharsets.UTF_8));
            fields.put("File", filename + " (application/pdf, " + samplePdf().length + " bytes)");
        }
        body.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
        return exchange("POST", "/detail", fields, body.toByteArray(), "multipart/form-data; boundary=" + boundary);
    }

    private HttpResponse<String> exchange(String method, String path, Map<String, String> fields,
                                          byte[] body, String contentType) throws Exception {
        String route = path + (method.equals("GET") && !fields.isEmpty() ? "?" + encode(fields) : "");
        var request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + route))
                .timeout(Duration.ofSeconds(30)).header("Accept", "application/json, text/html");
        if (body != null) request.header("Content-Type", contentType).POST(HttpRequest.BodyPublishers.ofByteArray(body));
        else request.GET();
        var response = http.send(request.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        exercisedRoutes.add(method + " " + path);
        String extension = response.headers().firstValue("content-type").orElse("").contains("json") ? ".json" : ".html";
        String filename = current.get("id") + "-" + (exchanges.size() + 1) + extension;
        Files.writeString(ROOT.resolve(filename), response.body(), StandardCharsets.UTF_8);
        Map<String, Object> record = new LinkedHashMap<>();
        record.put("method", method); record.put("path", path); record.put("input", new TreeMap<>(fields));
        record.put("status", response.statusCode());
        record.put("contentType", response.headers().firstValue("content-type").orElse(""));
        record.put("bodyFile", Path.of("").toAbsolutePath().relativize(ROOT.resolve(filename)).toString().replace('\\', '/'));
        record.put("bodyPreview", extension.equals(".json") ? response.body() : response.body().replaceAll("(?s)<script.*?</script>|<style.*?</style>", " ").replaceAll("<[^>]+>", " ").replaceAll("\\s+", " ").trim());
        exchanges.add(record);
        return response;
    }

    private static String encode(Map<String, String> fields) {
        return fields.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .reduce((a, b) -> a + "&" + b).orElse("");
    }

    private static void status(HttpResponse<String> response, int expected) {
        assertEquals(expected, response.statusCode(), response.body());
    }

    private static void success(HttpResponse<String> response) throws Exception {
        status(response, 200);
        var json = JSON.readTree(response.body());
        assertEquals("OK", json.path("결과").asText(), response.body());
        assertEquals("SUCCESS", json.path("비고").asText(), response.body());
    }

    private static void businessError(HttpResponse<String> response, String remark) throws Exception {
        status(response, 200);
        var json = JSON.readTree(response.body());
        assertEquals("ERROR", json.path("결과").asText());
        assertTrue(json.path("비고").asText().contains(remark), response.body());
    }

    private static byte[] samplePdf() {
        String[] objects = {"<< /Type /Catalog /Pages 2 0 R >>", "<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
                "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 200 200] /Contents 4 0 R >>", "<< /Length 0 >>\nstream\nendstream"};
        StringBuilder pdf = new StringBuilder("%PDF-1.4\n");
        List<Integer> offsets = new ArrayList<>();
        for (int i = 0; i < objects.length; i++) {
            offsets.add(pdf.length()); pdf.append(i + 1).append(" 0 obj\n").append(objects[i]).append("\nendobj\n");
        }
        int xref = pdf.length();
        pdf.append("xref\n0 5\n0000000000 65535 f \n");
        offsets.forEach(offset -> pdf.append(String.format(Locale.ROOT, "%010d 00000 n \n", offset)));
        pdf.append("trailer\n<< /Size 5 /Root 1 0 R >>\nstartxref\n").append(xref).append("\n%%EOF\n");
        return pdf.toString().getBytes(StandardCharsets.US_ASCII);
    }

    @AfterAll
    void writeEvidence() throws Exception {
        Files.createDirectories(Path.of("docs"));
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("executedAt", OffsetDateTime.now().toString()); report.put("database", DATABASE);
        report.put("httpBaseUrl", "http://127.0.0.1:" + port);
        report.put("sqlServerVersion", jdbc.queryForObject("SELECT @@VERSION", String.class));
        try (var connection = jdbc.getDataSource().getConnection()) {
            report.put("jdbcVersion", connection.getMetaData().getDriverVersion());
        }
        report.put("javaVersion", System.getProperty("java.version"));
        report.put("testMode", "Real HTTP + embedded Tomcat/JSP + Microsoft JDBC + real SQL Server; no mocks");
        report.put("results", results);
        JSON.writerWithDefaultPrettyPrinter().writeValue(Path.of("docs", "MSSQL_API_테스트_증적.json").toFile(), report);
        long passed = results.stream().filter(r -> r.get("result").equals("PASS")).count();
        StringBuilder md = new StringBuilder("# MSSQL API 테스트 실행 결과\n\n");
        md.append("실행 시각: ").append(report.get("executedAt")).append("\n\n")
          .append("실제 HTTP와 Tomcat JSP, Microsoft JDBC, SQL Server를 사용했다. Mock 또는 H2로 대체하지 않았다.\n\n")
          .append("테스트 DB: `").append(DATABASE).append("`\n\n")
          .append("결과: **").append(passed).append(" PASS / ").append(results.size() - passed).append(" FAIL / ").append(results.size()).append("건**\n\n")
          .append("## 실행 환경\n\n```text\n").append(report.get("sqlServerVersion")).append("\nJDBC ").append(report.get("jdbcVersion")).append("\nJava ").append(report.get("javaVersion")).append("\n```\n\n")
          .append("## 점검 결과 목록\n\n| 작업 번호 | 점검 내용 | 판정 | 소요 시간 |\n|---|---|---|---|\n");
        for (var row : results) md.append("| ").append(row.get("id")).append(" | ").append(row.get("title")).append(" | ").append(row.get("result")).append(" | ").append(row.get("elapsedMs")).append(" ms |\n");
        md.append("\n## 작업별 Input Output Result\n\n");
        for (var row : results) {
            md.append("### ").append(row.get("id")).append(" ").append(row.get("title")).append("\n\n")
              .append("**검사 기준**: ").append(row.get("expected")).append("\n\n");
            @SuppressWarnings("unchecked") var calls = (List<Map<String, Object>>) row.get("exchanges");
            if (calls.isEmpty()) md.append("**Input**: 격리 테스트 DB 기준 데이터를 적재한 뒤 해당 내부 점검 작업을 직접 실행한다.\n\n");
            for (var call : calls) {
                md.append("**Input**: `").append(call.get("method")).append(" ").append(call.get("path")).append("`\n\n```json\n")
                  .append(JSON.writerWithDefaultPrettyPrinter().writeValueAsString(call.get("input"))).append("\n```\n\n")
                  .append("**Output**: HTTP ").append(call.get("status")).append(" / ").append(call.get("contentType")).append("\n\n");
                String preview = call.get("bodyPreview").toString();
                md.append("```text\n").append(preview.length() > 600 ? preview.substring(0, 600) + " ..." : preview).append("\n```\n\n")
                  .append("[응답 원문](../").append(call.get("bodyFile")).append(")\n\n");
            }
            @SuppressWarnings("unchecked") var facts = (List<String>) row.get("observations");
            for (String fact : facts) md.append("**상태 확인**: ").append(fact).append("\n\n");
            md.append("**Result**: ").append(row.get("result"));
            if (row.containsKey("failure")) md.append(" / ").append(row.get("failure"));
            md.append("\n\n");
        }
        md.append("## 적용 범위와 잔여 점검\n\n운영 원본 DDL 및 데이터 이관, 운영 TLS 인증서와 제한 계정, 실제 피크 부하 및 외부 출력과 발송 연계는 이 결과에 포함되지 않는다. 샘플 스키마와 가상 개인정보를 사용했다. 정시 작업은 테스트 코드에서 직접 호출했으며 실제 시각을 기다린 실행은 아니다. 응답 원문은 target 아래에 있어 clean 작업 시 삭제된다.\n");
        Files.writeString(Path.of("docs", "MSSQL_API_테스트_결과.md"), md, StandardCharsets.UTF_8);
    }
}
