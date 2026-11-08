# MSSQL 전환 작업 및 시험 정비 매뉴얼

| 문서 관리 항목 | 내용 |
|---|---|
| 문서 번호 | DSPDF DB MM 001 |
| 개정 | 1.0 |
| 작성일 | 2026년 11월 8일 |
| 적용 대상 | dspdf Java 17 및 Spring Boot 3.2.3 애플리케이션 |
| 작업 목적 | MySQL 의존성을 MSSQL로 교체하고 모든 공개 업무 경로의 입출력과 저장 결과 검증 |
| 작업 형식 | 항공기 정비 작업 카드의 사전 조건, 절차, 검사 기준, 불합격 조치, 복구 형식을 적용한 소프트웨어 작업 문서 |
| 시험 DB | 운영 DB와 분리된 로컬 SQL Server 2022 Developer 컨테이너 |
| 실행 결과 | [최종 시험 결과](MSSQL_API_테스트_결과.md) 및 [기계 판독용 증적](MSSQL_API_테스트_증적.json) |
| 운영 적용 상태 | 코드 적용 및 격리 시험 완료 여부는 최종 결과서 기준이며, 운영 DB 이관과 운영 전환은 별도 승인 대상 |

이 매뉴얼은 코드 변경과 재현 가능한 시험 절차를 기록한다. 각 작업은 지정된 선행 조건을 확인한 뒤 수행하고, 판정 기준을 충족하지 못하면 다음 작업으로 진행하지 않는다. API 시험의 실제 Input, HTTP Output, DB와 파일 관찰값, Result는 연결된 결과서에서 작업 번호로 추적한다.

## 00 문서 사용과 판정 규칙

### 00 10 작업 상태 표기

| 표기 | 의미 | 후속 조치 |
|---|---|---|
| PASS | 해당 시험의 HTTP, 응답 내용 및 명시된 부작용 검사 통과 | 다음 작업 진행 |
| FAIL | 기대값과 실제값 불일치 또는 시험 중 예외 발생 | 원인 격리, 결함 수정, 영향 시험 재수행 |
| HOLD | 환경, 승인, 운영 자료 등 필수 조건 미충족 | 조건 해결 전 운영 진행 보류 |
| NOT TESTED | 이번 시험에 포함하지 않은 항목 | 검증 계획과 담당자 지정 |

HTTP 200만으로 PASS를 판정하지 않는다. POST /detail은 업무 오류도 HTTP 200과 결과 ERROR를 반환하는 기존 계약이므로 결과와 비고를 함께 판정한다. 조회와 상태 변경은 JSP가 실제로 렌더링되는지 검사한다. 저장소가 SQL 예외를 잡고 정상처럼 빈 결과를 반환할 수 있어 각 시험 종료 시 SQL_ERROR 이력이 없는지도 확인한다.

### 00 20 주의 사항

**경고**: 시험 스키마는 실제 MySQL DDL을 대체하지 않는다. src/test/resources/sqlserver-schema.sql은 격리 시험용 컬럼 길이와 키를 갖춘 샘플이며 운영 DB에 그대로 실행하지 않는다.

**경고**: 샘플 시험은 테이블을 초기화한다. 통합 시험은 전용 로컬 포트 11433과 실행마다 생성되는 dspdf_it 접두어 DB만 사용하며 운영 JDBC URL을 입력받지 않는다.

**주의**: 컨테이너의 공개된 샘플 sa 비밀번호는 로컬 시험 전용이다. 포트는 127.0.0.1에만 바인딩하고 운영 계정에 재사용하지 않는다. 운영에서는 제한된 계정과 검증 가능한 TLS 인증서를 사용한다.

**주의**: JSON 출력 인코딩을 UTF-8로 명시했다. 파일을 수신하는 외부 프로그램이 기존 Windows 기본 인코딩을 전제로 한다면 운영 전환 전에 UTF-8 수신을 검증한다.

## 01 적용성 및 시스템 경계

### 01 10 전체 공개 업무 경로

| 분류 | Method | Path | Input 형식 | Output 형식 |
|---|---|---|---|---|
| 우편 접수 | POST | /detail | multipart/form-data 및 MultipartFile | JSON 결과와 비고 |
| 작업 목록 | GET | /masters | 검색과 정렬 및 페이지 query | text/html JSP |
| 상태 변경 | POST | /masters/update | form-urlencoded statusData JSON 문자열과 검색값 | text/html JSP |
| 오류 목록 | GET | /errors | 날짜와 검색 및 정렬과 페이지 query | text/html JSP |
| 오류 상세 | GET | /error/detail | errorId query | text/html JSP |

등록된 컨트롤러 매핑과 위 목록의 일치를 API-001에서 확인한다. 실제 HTTP 호출 경로가 5개 모두 포함되었는지 API-034에서 다시 확인한다. Spring Boot 내부 오류 처리 경로는 업무 API 목록에 포함하지 않는다.

### 01 20 DB와 파일 경계

| 구성 요소 | 검증 범위 |
|---|---|
| MASTER | 작업 생성, 건수, 상태, 현재 시각, 목록과 페이징, 정기 삭제 |
| DETAIL | 한글과 앞자리 0 보존, 중복 방지, 작업 참조, PDF 경로, 삭제 |
| ERROR | 자동 증가 ID, 업무 오류, 한글 상세, 날짜와 코드 검색, 페이징 |
| STATUS | 코드 조회, 이름 매핑, AUTO 및 MANUAL 상태, 삭제 대상 코드 |
| receiving | 접수 PDF와 마감 전 파일 보관 |
| complete | 대량 완료 또는 당일 마감 후 JSON 및 PDF 이동 |

외부 출력 및 발송 프로그램은 이번 자동 시험의 실행 대상이 아니다. 파일 이동 결과를 확인하는 것으로 외부 발송 성공을 대신 판정하지 않는다.

## 02 공구와 시험 환경

| 항목 | 사용 사양 또는 위치 |
|---|---|
| 개발 언어 | Java 17 |
| 빌드 | Maven 및 기존 Spring Boot WAR 구성 |
| DB 엔진 | Microsoft SQL Server 2022 Developer, 실제 빌드 번호는 결과서 참조 |
| DB 이미지 | mcr.microsoft.com/mssql/server:2022-latest |
| 실행 이미지 digest | sha256:cf8f0ab2ebe61feec2eed2629061a07d247f791c2a8ddda74a4ac9f06a291718 |
| 이미지 고정 재현 | mcr.microsoft.com/mssql/server@sha256:cf8f0ab2ebe61feec2eed2629061a07d247f791c2a8ddda74a4ac9f06a291718 |
| JDBC | com.microsoft.sqlserver:mssql-jdbc:12.6.5.jre11 |
| HTTP 실행 | 실제 임의 포트 Tomcat과 Java HttpClient |
| 시험 프레임워크 | 기존 Spring Boot Test와 JUnit, Mock 및 H2 미사용 |
| 스키마 | src/test/resources/sqlserver-schema.sql |
| 샘플 파일 | 시험 코드가 생성하는 유효한 1페이지 PDF, 실제 파일은 실행별 target/mssql-it 아래 sample.pdf |
| 파일 저장 격리 | target/mssql-it/실행번호/receiving 및 complete |
| 응답 원문 | target/mssql-it/실행번호/작업번호와호출번호.html 또는 json |
| 자동 시험 코드 | src/test/java/com/daishin/pdf/MssqlApiIntegrationTest.java |
| 독립 단위 시험 | src/test/java/com/daishin/pdf/SearchValidationTest.java |

latest 태그는 시간이 지나면 다른 이미지가 될 수 있다. 재현 시 위 digest 또는 승인한 고정 태그를 사용하고 SQL Server 실제 버전을 결과서에 남긴다. JSON 증적과 Markdown 결과서는 시험 코드에서 자동 생성하며 수기로 PASS를 입력하지 않는다.

## 03 작업 전 격리와 접근

### TASK DB 03 001 시험 DB 격리 확인

**선행 조건**: Docker 엔진 실행, Java 17과 Maven 사용 가능, 로컬 포트 11433 미사용.

**절차**:

1. 현재 저장소와 git 변경 상태를 확인한다.
2. 운영 DB 및 파일 저장소에 대한 접근 없이 시험할 수 있는지 확인한다.
3. dspdf-mssql-test 컨테이너가 이미 있다면 기존 용도를 확인한다. 이름이 겹친다는 이유로 임의 삭제하지 않는다.
4. 컨테이너가 없으면 아래 명령으로 개발용 MSSQL을 생성한다.

```powershell
docker run --name dspdf-mssql-test -e ACCEPT_EULA=Y -e MSSQL_PID=Developer -e 'MSSQL_SA_PASSWORD=Dspdf_Test_Only!2026' -e TZ=Asia/Seoul -p 127.0.0.1:11433:1433 -d mcr.microsoft.com/mssql/server@sha256:cf8f0ab2ebe61feec2eed2629061a07d247f791c2a8ddda74a4ac9f06a291718
docker logs --tail 30 dspdf-mssql-test
```

**검사 기준**: SQL Server가 연결 가능한 상태이며 테스트 포트가 로컬에만 열려 있다. 시험 코드는 접속 엔진이 Microsoft SQL Server인지 확인한 뒤 실행마다 새 DB를 생성한다.

**불합격 조치**: 포트 충돌, 이미지 접근 실패, DB 기동 실패를 먼저 해결한다. 운영 DB URL로 대체하여 진행하지 않는다.

### TASK DB 03 002 자동 작업 격리

**선행 조건**: scheduler.enabled 조건부 구성이 코드에 적용되어 있어야 한다.

**절차**: 통합 시험은 scheduler.enabled=false로 기동한다. API-001에서 SchedulerConfiguration 빈이 등록되지 않았는지 확인한다. 정시 마감, 상태 전환, 삭제 시험은 스케줄을 기다리지 않고 격리된 인스턴스를 직접 호출한다.

**검사 기준**: API 시험 도중 자동 상태 전환이나 정기 삭제가 시작되지 않는다. SCH 작업은 직접 호출에 의해서만 수행된다.

**불합격 조치**: 임의 시간 대기나 실행 시각 회피로 우회하지 말고 빈 조건과 실행 중 앱 인스턴스를 확인한다.

## 04 제거와 교체 작업

### TASK DB 04 001 JDBC 및 연결 설정 교체

**변경 부품**: pom.xml, application.yml.

**절차**:

1. mysql-connector-java를 제거하고 mssql-jdbc 12.6.5.jre11을 추가한다.
2. 드라이버를 com.microsoft.sqlserver.jdbc.SQLServerDriver로 변경한다.
3. DB 호스트와 포트, DB명, 계정과 비밀번호를 환경 변수로 주입한다.
4. 운영 연결은 encrypt=true와 인증서 검증을 기본값으로 사용한다.
5. 저장 경로와 로그 경로를 환경 변수로 덮어쓸 수 있게 한다. receiving 및 complete 경로는 구분자로 끝나게 지정한다.

| 환경 변수 | 기본값 또는 필수 여부 |
|---|---|
| DB_HOST | localhost |
| DB_PORT | 1433 |
| DB_NAME | daishin |
| DB_USERNAME | 필수 |
| DB_PASSWORD | 필수 |
| DB_TRUST_SERVER_CERTIFICATE | false, 로컬 시험 외 운영 검증 해제 금지 |
| SCHEDULER_ENABLED | true, 이관 중에는 false |
| RECEIVING_PATH | D:/DATA/receiving/ |
| COMPLETE_PATH | D:/DATA/complete/ |
| LOG_PATH | D:/DATA/logs |

**검사 기준**: MySQL 의존성과 활성 SQL 문법이 남지 않고 MSSQL JDBC로 연결한다. 비밀번호를 저장소 설정 파일에 하드코딩하지 않는다.

**불합격 조치**: 인증서, DNS, 포트, 계정 권한을 각각 점검한다. 접속 문제를 해결하려고 운영 인증서 검증을 일괄 해제하지 않는다.

### TASK DB 04 002 SQL 문법 및 검색 교체

**변경 부품**: mapper/Master.xml, mapper/Detail.xml, mapper/Errors.xml.

| 작업 | 교체 내용 | 기능 확인 |
|---|---|---|
| 시간 함수 | NOW 5곳을 CURRENT_TIMESTAMP로 교체 | 접수 및 상태 변경 후 날짜 매핑 |
| 페이징 | LIMIT 2곳을 OFFSET 및 FETCH로 교체 | API-012, API-026 |
| 보조 정렬 | MASTER_KEY 또는 ERROR.ID로 동일 시각 순서 보완 | API-012 및 정렬 조합 시험 |
| 숫자 상태 검색 | STATUS만 NVARCHAR로 변환해 LIKE 검색 | API-010 |
| 오류 날짜 검색 | CREATE_DATE를 ISO 126 형식으로 변환 | API-025 |
| Unicode 검색 | 검색 패턴에 Unicode 리터럴 사용 | 한글 타입과 오류 검색 |

일반 문자열 검색 컬럼에는 불필요한 일괄 CAST를 적용하지 않는다. 날짜와 숫자 검색에 필요한 위치만 변환한다. STATUS.xml은 교체 없이 실제 JDBC 조회로 검증한다.

**불합격 조치**: 목록 HTTP가 200이더라도 ERROR 테이블에 SQL_ERROR가 있으면 FAIL로 판정하고 해당 MyBatis 구문과 실행 파라미터를 확인한다.

### TASK DB 04 003 입력과 상태 변경 검사 보완

**변경 부품**: Search.java, ErrorSearch.java, ViewController.java.

**절차**:

1. 동적 SQL에 삽입되는 검색 컬럼과 정렬 컬럼, ASC와 DESC를 허용 목록으로 검증한다.
2. page는 1부터 1000000, pageElement는 1부터 200, 검색 문자열은 1000자 이하로 제한한다. 기존 화면의 페이지 크기 10부터 100까지는 허용한다.
3. LocalDateTime에 부적절한 문자열 Pattern 대신 ISO 날짜 바인딩을 적용한다.
4. 날짜 범위 역전은 400으로 거부한다.
5. 상태 변경 JSON, 코드와 작업 존재 여부를 변경 실행 전에 검사한다. 비어 있거나 잘못된 코드는 400, 없는 작업은 404로 응답한다.
6. 오류 상세 ID의 잘못된 형식 및 범위는 400, 없는 오류는 404로 응답한다.

**검사 기준**: API-014부터 API-016, API-020부터 API-023, API-028부터 API-033, API-037부터 API-040이 통과한다.

**주의**: 사전 검사는 요청 형식 오류를 막는다. 여러 작업 변경 도중의 DB 장애까지 전부 원자적으로 되돌리는 트랜잭션 보장은 이번 변경에 추가하지 않았다.

### TASK DB 04 004 로그 경로와 스케줄 제어

**변경 부품**: logback-spring.xml, SchedulerConfiguration.java, application.yml.

**절차**: 로그 설정이 storage.logPath를 사용하게 연결하고 scheduler.enabled=false일 때 스케줄러 구성을 등록하지 않는다. 기본값 true로 기존 정시 작업의 운영 동작을 유지한다.

Spring의 springProperty를 사용하므로 Logback의 자체 configuration scanning은 사용하지 않는다. 로그 경로 변경은 배포 설정에 반영한 뒤 애플리케이션을 다시 기동한다.

**검사 기준**: 격리 시험이 D 드라이브 운영 경로에 로그나 파일을 기록하지 않고, 자동 스케줄러 빈이 생성되지 않는다.

### TASK DB 04 005 JSON 한글 인코딩 결함 수정

**발견 작업**: API-004.

**초기 검사 결과**: 최초 실제 실행에서 37건 중 36건 PASS, 1건 FAIL. 대량 완료 후 생성한 JSON을 UTF-8로 읽을 때 java.nio.charset.MalformedInputException이 발생했다. HTTP 접수 자체의 성공 응답만 확인하면 발견되지 않는 결함이었다.

**원인**: Utils.saveJson의 FileWriter가 운영체제 기본 인코딩에 의존했다. 시험 호스트의 기본 문자셋은 MS949이며 한글 JSON이 UTF-8로 저장되지 않았다.

**조치**: FileWriter에 StandardCharsets.UTF_8을 명시했다. JVM 기본 인코딩을 변경해 결함을 가리는 방식은 사용하지 않았다.

**재검사 기준**: API-004에서 JSON을 UTF-8로 읽고 파싱한 뒤 RECV_NM과 RECV_ADDR가 한글 원문과 일치해야 한다. PDF 2건과 JSON의 이동, DB의 최종 경로도 함께 검사한다.

### TASK DB 04 006 유일 키 정렬 중복 수정

**발견 작업**: 확장 정렬 조합 시험 API-035.

**재시험 중 검사 결과**: 인코딩 수정 후 43건 실행에서 42건 PASS, 1건 FAIL. MASTER_KEY 정렬을 선택하면 SQL Server 오류 169가 발생했다. 저장소가 SQL 예외를 잡아 빈 목록을 반환하여 HTTP 상태는 200이었지만 실제 기준 행 출력 검사에서 실패했다.

**원인**: 페이징 안정화를 위해 추가한 보조 정렬이 주 정렬 MASTER_KEY와 중복되었다. MSSQL은 ORDER BY 목록의 동일 컬럼 중복을 허용하지 않는다.

**조치**: 주 정렬이 MASTER_KEY인 경우 보조 MASTER_KEY 정렬을 추가하지 않도록 공유 MyBatis 구문에 조건을 적용했다.

**재검사 기준**: API-035의 5개 컬럼과 2개 방향 조합 10회가 모두 성공하고 SQL_ERROR가 없어야 한다. 기본 정렬의 페이징 API-012도 다시 통과해야 한다.

## 05 샘플 데이터와 시험 준비

### TASK DB 05 001 샘플 데이터 적재

**절차**: 통합 시험 시작 시 테스트 스키마와 STATUS를 생성한다. 각 시험마다 MASTER, DETAIL, ERROR와 시험용 receiving 및 complete의 내용만 초기화하고 아래 기준 데이터를 다시 적재한다. 이전 시험의 파일과 업무 행이 다음 시험의 성공 조건을 대신 충족하지 않게 한다.

| 데이터 | 샘플과 의미 |
|---|---|
| STATUS | 100 수신중 MANUAL, 200 수신완료 MANUAL, 300 출력중 AUTO 0H1M, 400 발송완료 MANUAL, 900 오류 MANUAL |
| BATCH-SEED | ARRANGEMENT, TOTAL_SEND_CNT=2, SEND_CNT=2, STATUS=200 |
| RT-SEED | REAL_TIME, TOTAL_SEND_CNT=0, SEND_CNT=1, STATUS=100 |
| PAGE-01부터 PAGE-03 | 같은 상태 시각의 페이징 시험 행 |
| ERROR ID 1 | BATCH-SEED, SAMPLE_ERROR, 샘플 오류 메시지 한글 주소 검증 |
| 수신자 | 가상수신자 |
| 주소 | 테스트시 가상로 123, 테스트동 101호 |
| 우편번호 및 고객번호 | 01234 및 00001234 |
| PDF | 빈 내용의 유효한 1페이지 PDF, 요청별 고유 파일명 |

STATUS의 끝에서 두 번째 코드인 400이 이 샘플의 삭제 대상이다. 이 값이 실제 운영 삭제 대상 코드라는 의미는 아니다. 기준 MASTER 행의 시간은 2026년 1월 1일 10시로 고정하며, 현재 시간 의존 시험은 필요한 행만 상대 시각으로 조정한다.

### TASK DB 05 002 Input 규격

POST /detail은 JSON 본문이 아니라 multipart form을 사용한다. 아래 값은 대표 정상 접수 Input이며, 파일 필드 이름은 대문자 F를 사용하는 File이다.

```text
TR_KEY=BULK-004
TOTAL_SEND_CNT=2
RECV_NUM=1
DLV_TYPE_CD=01
PRINT_TYPE_NM=양면
PAGE_CNT=1
RET_YN=N
DM_LINK_KEY=DM-BULK-004
CIFNO=00001234
DLV_CD=01
RECV_NM=가상수신자
RECV_POST_CD=01234
RECV_ADDR=테스트시 가상로 123
RECV_ADDR_DETAIL=테스트동 101호
File=BULK-004-1.pdf
```

정상 응답은 HTTP 200과 아래 JSON이다. 중복 및 필수값 누락도 HTTP 200이므로 결과 필드를 별도로 확인한다.

```json
{"결과":"OK","비고":"SUCCESS"}
```

## 06 기능 점검 작업 카드

### TASK DB 06 001 전체 자동 시험 실행

**선행 조건**: DB 03 작업 완료, 로컬 MSSQL 준비, 샘플 파일 저장 경로 쓰기 가능.

**절차**:

```powershell
mvn -B -ntp '-Ddspdf.mssql.it=true' verify
```

시험용 컨테이너 비밀번호를 변경했다면 MSSQL_TEST_PASSWORD 환경 변수로 시험 코드에 전달한다. 테스트 JDBC는 그 변수와 관계없이 로컬 11433으로 고정되어 있다. 일반 mvn test는 단위 시험만 수행하고 DB 통합 시험은 생략한다. 통합 시험을 수행했다고 보고하려면 반드시 dspdf.mssql.it=true 실행 결과가 있어야 한다.

**검사 기준**: Maven BUILD SUCCESS, 단위 시험 2건과 통합 점검 43건 모두 통과. 결과서와 증적 파일이 같은 실행의 DB명과 시각으로 갱신된다. 하나의 JUnit 동적 시험 팩터리 아래 43개의 개별 작업이 실행되므로 콘솔의 최종 상위 시험 집계만으로 작업 수를 판단하지 않는다.

### TASK DB 06 002 접수 계통 점검

| 작업 번호 | Input 및 조작 | Output 검사 | 부작용 및 Result 검사 |
|---|---|---|---|
| API-002 | 단일 1건, 한글과 앞자리 0, PDF | 200 OK 및 SUCCESS | DETAIL 원문, 날짜 묶음, MASTER 100, PDF 바이트 동일 |
| API-003 | 대량 2건 중 첫 요청 | 200 OK 및 SUCCESS | MASTER 100 및 SEND_CNT=1 |
| API-004 | 같은 그룹의 1번과 2번 요청 | 두 번 모두 200 OK | MASTER 200, SEND_CNT=2, JSON 2건과 한글, PDF 2개 이동 |
| API-005 | 동일 요청 재전송 | 200 ERROR 및 DUPLICATE | DETAIL 1건 유지, DUPLICATE 이력 1건 |
| API-006 | RECV_NM 누락 | 200 ERROR 및 MISSING_VALUE | DETAIL 미생성 |
| API-007 | File 누락 | 200 ERROR 및 누락 설명 | 성공으로 처리되지 않음 |
| API-008 | TOTAL_SEND_CNT=abc | 400 | 잘못된 바인딩 거부 |

### TASK DB 06 003 작업 목록 계통 점검

| 작업 번호 | Input 및 조작 | 검사 기준 |
|---|---|---|
| API-009 | GET /masters | JSP에 BATCH-SEED 및 RT-SEED 출력 |
| API-010 | cate=STATUS, search=200 | BATCH-SEED 행 포함, RT-SEED 행 제외 |
| API-011 | cate=TYPE, search=실시간 | RT-SEED만 해당 검색 결과에 포함 |
| API-012 | PAGE 검색, 2건씩, 2페이지 | PAGE-03 포함, PAGE-01과 PAGE-02 제외 |
| API-013 | 없는 키 검색 | HTTP 200 및 검색 건수 0 |
| API-014와 API-015 | SQL 구문이 섞인 컬럼 및 방향 | HTTP 400, 기존 테이블 보존 |
| API-016과 API-038 | 0 크기, 음수 페이지, 초과 크기 | HTTP 400 |
| API-035 | 5개 정렬 컬럼과 2개 방향의 전체 조합 | 10회 모두 HTTP 200 및 기준 행 출력 |

### TASK DB 06 004 상태 변경 계통 점검

| 작업 번호 | Input 및 조작 | 검사 기준 |
|---|---|---|
| API-017 | BATCH-SEED를 300으로 변경 | HTTP 200, DB STATUS=300, 상태 시각 갱신 |
| API-018 | 실시간 작업을 100으로 복귀 | 전체 건수 0 |
| API-019 | 단일 접수 후 실제 날짜 묶음을 200으로 변경 | 전체 건수 실제 DETAIL 수 1로 반영 |
| API-020 | 파싱할 수 없는 statusData | HTTP 400 |
| API-021 | 없는 작업 키 | HTTP 404 |
| API-022와 API-039 | 미등록 코드 777 또는 abc | HTTP 400, 원래 상태 보존 |
| API-023 | 빈 객체 | HTTP 400 |
| API-040 | 정상 작업과 없는 작업을 함께 변경 | HTTP 404, 정상 작업도 변경하지 않음 |

### TASK DB 06 005 오류 기록 계통 점검

| 작업 번호 | Input 및 조작 | 검사 기준 |
|---|---|---|
| API-024 | GET /errors 기본값 | HTTP 200 및 SAMPLE_ERROR 출력 |
| API-025 | CREATE_DATE를 ISO 날짜로 검색 | HTTP 200 및 기준 오류 출력 |
| API-026 | 오류 2건 생성 후 1건씩 2페이지 | 두 번째 코드 포함, 첫 코드 제외 |
| API-027 | 기준 데이터보다 과거 날짜 범위 | SAMPLE_ERROR 제외 |
| API-028와 API-037 | 역전 범위 또는 잘못된 날짜 | HTTP 400 |
| API-029 | 잘못된 정렬 컬럼 | HTTP 400 |
| API-030 | errorId=1 | HTTP 200 및 한글 오류 원문 |
| API-031 | 없는 ID | HTTP 404 |
| API-032와 API-033 | 숫자가 아닌 ID 또는 누락 | HTTP 400 |
| API-036 | 존재 및 미존재 ERROR_CODE 검색 | 코드에 따라 포함 또는 제외 |

### TASK DB 06 006 자동 처리 계통 점검

| 작업 번호 | 사전 설정과 조작 | 검사 기준 |
|---|---|---|
| SCH-001 | PAGE-01을 오래된 AUTO 300, PAGE-02를 대기 시간 미경과 300으로 설정 후 changeStatus 호출 | PAGE-01은 400, PAGE-02는 300 유지 |
| SCH-002 | OLD-DELETE 접수 후 상태 400과 2개월 전 시각, 최근 상태 400 및 관련 ERROR 추가 후 delete 호출 | 오래된 MASTER와 DETAIL만 삭제, 최근 MASTER와 ERROR 및 PDF 보존 |
| SCH-003 | 단일 접수, 현재가 14시 이후라면 테스트 내에서 당일 묶음으로 조정 후 run 호출 | 당일 MASTER 200, 총 1건, JSON 생성과 이동 |

직접 호출 시험은 처리 로직과 DB SQL을 확인한다. 실제 14시 3분 또는 19시 cron 발생, JVM 스케줄러 시간대 및 다중 인스턴스 중복 실행까지 증명하지는 않는다.

### TASK DB 06 007 시험 증적 검사

**절차**:

1. [결과서](MSSQL_API_테스트_결과.md)의 실행 시각, SQL Server와 JDBC 버전, 전체 PASS 및 FAIL 수를 확인한다.
2. 작업별 Input의 실제 form 및 query 값을 확인한다.
3. Output의 HTTP 상태, 콘텐츠 유형, 본문 발췌와 응답 원문 링크를 확인한다.
4. 상태 확인 항목의 DB 행과 파일 상태를 검사한다.
5. [JSON 증적](MSSQL_API_테스트_증적.json)과 결과서의 작업 번호 및 판정이 일치하는지 확인한다.
6. target/surefire-reports의 XML 결과와 최종 WAR 생성 결과를 대조한다.

**검사 기준**: 기대 결과와 실제 응답 및 부작용 검사를 혼동하지 않는다. 호출이 없는 API-001과 API-034, SCH 작업은 내부 검사로 명시되고 가상의 HTTP 응답을 기록하지 않는다.

**증적 보존**: Markdown과 JSON에는 실제 입력과 응답 정보가 남는다. target 아래 원문과 PDF, JSON 파일은 mvn clean 시 삭제되므로 장기 보존이 필요하면 해당 실행 폴더와 surefire-reports를 함께 보관한다. 샘플 데이터를 운영 개인정보로 대체하지 않는다.

최종 실행의 응답 원문과 샘플 PDF, JUnit XML은 [원본 증적 보관 파일](MSSQL_테스트_원본_증적.zip)에도 보존했다. 이 보관 파일은 아래 완료 기록의 실행 시점에 해당하며, 이후 재시험 시 최신 결과와 함께 다시 생성한다.

## 07 결함 격리와 재시험

| 증상 | 우선 확인 | 조치 후 재시험 |
|---|---|---|
| JDBC 연결 실패 | 컨테이너 상태, 11433 바인딩, 샘플 비밀번호 | DB 03과 전체 시험 |
| 목록 HTTP 200이지만 빈 결과 | ERROR.SQL_ERROR, 검색 변환과 OFFSET 문법 | 해당 검색 및 페이징, 전체 경로 시험 |
| 한글 JSON 읽기 실패 | FileWriter 문자셋과 실제 파일 바이트 | API-004와 SCH-003 |
| 대량 완료 시 파일 누락 | receiving 및 complete 권한과 경로 구분자 | API-003, API-004 |
| 자동 삭제 결과 차이 | STATUS 끝에서 두 번째 코드, STATUS_TIME, JVM과 DB 시각 | SCH-001, SCH-002 |
| 입력 검사 400 대신 500 | Valid 적용, 날짜 바인딩, JSON 예외 처리 | 해당 오류 Input과 전체 시험 |
| 중간 실패 후 일부 행만 변경 | 저장소 예외 처리와 트랜잭션 경계 | 운영 전 결함 범위와 복구 방법 판단 |

실패를 PASS로 변경하려고 기대값만 낮추지 않는다. 원인과 변경 파일, 회귀 시험을 기록하고 전체 검증을 다시 수행한다. 최초 인코딩 결함은 DB 04 005 작업에 기록되어 있다.

## 08 작업 완료와 복구

### TASK DB 08 001 시험 장비 정리

**절차**: 결과와 원문 위치를 확인한 뒤 전용 컨테이너를 중지한다. 시험 DB와 파일을 임의 삭제하지 않고 재현 자료를 유지한다.

```powershell
docker stop dspdf-mssql-test
```

재시험 시 컨테이너를 시작하고 DB 준비 상태를 확인한 뒤 DB 06 001을 다시 수행한다.

```powershell
docker start dspdf-mssql-test
mvn -B -ntp '-Ddspdf.mssql.it=true' verify
```

### TASK DB 08 002 운영 복귀 전 확인

| 검사 항목 | 현재 작업의 판정 범위 | 운영 승인 조건 |
|---|---|---|
| 코드와 WAR | 격리 시험 및 빌드 결과로 판정 | 검토한 변경본과 배포본 일치 |
| 실제 데이터 이관 | NOT TESTED | 원본 DDL 확정, 전체 건수와 키 및 값 비교 |
| 운영 보안 연결 | NOT TESTED | 실 인증서와 제한 계정, 방화벽 검증 |
| 운영 시간과 cron | 직접 호출 로직만 시험 | 서버 시간대 및 정시 실행 확인 |
| 성능과 동시성 | 소량 순차 기능 시험 | 피크 부하와 동일 작업 동시 접수 시험 |
| 외부 출력 및 발송 | NOT TESTED | UTF-8 JSON 소비와 중복 발송 방지 검증 |
| 쓰기 후 롤백 | 절차는 기획서 참조, 실제 역이관 NOT TESTED | 신규 및 수정과 삭제, 파일 처리 이력까지 복구 리허설 |
| 기존 보관 정책 | 테스트 상태와 시각에서 확인 | 운영 STATUS 실제 코드와 대상 행 사전 확인 |

**운영 전환 HOLD 조건**: 샘플 시험 PASS만으로 운영 스키마와 데이터 이관을 완료한 것으로 판정하지 않는다. 위 운영 승인 조건을 충족하고 담당자가 인수해야 한다.

기존 DETAIL 선저장 후 MASTER 생성, SEND_CNT 읽기 후 갱신, 다건 삭제의 부분 실패 가능성은 이번 DB 문법 교체로 해결되는 문제가 아니다. 오류 복구와 동시 접수에 대한 운영 검증을 남긴다. 새로운 FK나 CASCADE를 임의 추가하지 않았다.

### TASK DB 08 003 코드 복구

운영 반영 전에는 이전 WAR와 MySQL 연결 설정 및 JDBC 의존성을 가진 이전 코드 버전을 함께 보관한다. 소스 일부만 이전 상태로 바꾸지 말고 검증된 산출물 단위로 복구한다. 기존 사용자 변경이나 기획 문서를 지우는 일괄 git reset을 사용하지 않는다.

MSSQL에서 쓰기가 발생한 뒤에는 단순 연결 복원으로 복구할 수 없다. [마이그레이션 기획서](MySQL에서_MSSQL로_DB_마이그레이션_작업_기획서.md)의 쓰기 전후 롤백 절차에 따라 DB 변경분과 PDF 및 JSON, 외부 소비 이력을 함께 대조한다.

## 09 작업 완료 기록

| 구분 | 기록 |
|---|---|
| 수행 작업 | JDBC와 설정 교체, SQL 7곳 및 검색과 정렬 호환성 보완, 입력 검사, 스케줄 제어, JSON UTF-8 수정 |
| 발견 결함 | API-004 JSON 인코딩 실패, API-035 보조 정렬 중복 실패와 각각의 수정 이력 |
| 재시험 | 최종 결과서의 실행 시각과 전체 판정 참조 |
| 샘플 데이터 | 가상 이름과 주소, 임의 업무 키, 격리 테스트 DB |
| 원본 영향 | 운영 MySQL 및 운영 파일에 이관이나 데이터 변경 작업을 수행하지 않음 |
| 보존 산출물 | 변경 소스, 자동 시험, 샘플 DDL, 결과 Markdown 및 JSON, 실행별 target 원문, WAR |
| 작업자 확인 | 자동 실행 결과 기록 완료, 조직의 실명 확인은 운영 인수 시 작성 |
| 독립 검토자 | 운영 인수 시 배정 |

### 09 10 최종 시험 완료 기록

| 항목 | 실제 기록 |
|---|---|
| 최종 통합 시험 시각 | 2026년 11월 8일 19시 20분 51초 KST |
| 통합 점검 | 43건 PASS, 0건 FAIL |
| 단위 시험 | 2건 PASS, 0건 FAIL |
| 실제 HTTP 호출 | 54회, 공개 업무 경로 5개 전체 포함 |
| Maven 실행 | mvn -B -ntp -Ddspdf.mssql.it=true verify, BUILD SUCCESS |
| 전체 결과 | 45건 통과, 실패 및 오류 및 생략 0 |
| 테스트 장비 | 전용 MSSQL 컨테이너 중지 후 DB 보존 |
| 원본 증적 SHA256 | 39F447BA26B4942514C647E31715DA5B15850DC4266F23A98D9BB6FF8EE84167 |

### 09 20 최종 배포 산출물 기동 확인

통합 시험 후 Logback 자체 설정 재검색을 해제하고 최종 WAR를 다시 패키징했다. 이 설정은 시험용 logback-test.xml과 별개이므로 실제 WAR를 사용해 추가 확인했다. 이 단계의 패키징은 기존 통과 시험을 다시 실행하지 않는 mvn -B -ntp -DskipTests package로 수행했으며, 최종 WAR 검사는 아래 결과와 같다.

| 항목 | Input | 실제 Output 및 Result |
|---|---|---|
| 실행 | java -jar target/pdf-1.0.0-SNAPSHOT.war, 로컬 19090, scheduler.enabled=false, 환경 변수로 전용 MSSQL 연결 | 2026년 11월 8일 20시 3분 기동 PASS |
| 작업 목록 | GET http://127.0.0.1:19090/masters | HTTP 200, BATCH-SEED 행 포함, PASS |
| 오류 목록 | GET http://127.0.0.1:19090/errors | HTTP 200, SAMPLE_ERROR 포함, PASS |
| 로그 경로 | LOG_PATH=작업경로/target/war-smoke/logs | daesinPdf.log 생성, PASS |
| 종료 | 시험 WAR 종료 후 컨테이너 중지 | 로컬 19090 리스너 없음, 시험 데이터 보존 |
| 최종 WAR SHA256 | target/pdf-1.0.0-SNAPSHOT.war | E731130408A4A6DE4F6AFF7D59DB01158AD38AF3DD29C405E2B7CEC11D935410 |

추가 기동 확인의 [작업 목록 응답](../target/war-smoke/masters.html)과 [오류 목록 응답](../target/war-smoke/errors.html)은 target 아래에 보존했다. 이 2회 조회는 통합 시험 결과서의 54회 호출 집계와 구분한다.

## 10 기술 자료와 관련 문서

- [DB 마이그레이션 작업 기획서](MySQL에서_MSSQL로_DB_마이그레이션_작업_기획서.md)
- [Microsoft JDBC 릴리스 노트](https://learn.microsoft.com/en-us/sql/connect/jdbc/release-notes-for-the-jdbc-driver) — 12.6 계열의 Java 호환성 및 릴리스 정보.
- [Microsoft SQL Server 컨테이너 실행](https://learn.microsoft.com/en-us/sql/linux/quickstart-install-connect-docker) — 개발용 컨테이너 실행과 접속.
- [SQL Server OFFSET 및 FETCH](https://learn.microsoft.com/en-us/sql/t-sql/queries/select-order-by-clause-transact-sql) — 페이징과 정렬 요건.
