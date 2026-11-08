# MSSQL API 테스트 실행 결과

실행 시각: 2026-11-08T19:20:51.763179500+09:00

실제 HTTP와 Tomcat JSP, Microsoft JDBC, SQL Server를 사용했다. Mock 또는 H2로 대체하지 않았다.

테스트 DB: `dspdf_it_20261108192036321574500`

결과: **43 PASS / 0 FAIL / 43건**

## 실행 환경

```text
Microsoft SQL Server 2022 (RTM-CU27-GDR) (KB5126934) - 16.0.4305.5 (X64) 
	Oct  2 2026 17:28:11 
	Copyright (C) 2022 Microsoft Corporation
	Developer Edition (64-bit) on Linux (Ubuntu 22.04.5 LTS) <X64>
JDBC 12.6.5.0
Java 17.0.15
```

## 점검 결과 목록

| 작업 번호 | 점검 내용 | 판정 | 소요 시간 |
|---|---|---|---|
| API-001 | 전체 경로와 스케줄 격리 | PASS | 135 ms |
| API-002 | 단일 우편 정상 접수 | PASS | 686 ms |
| API-003 | 대량 첫 요청 | PASS | 148 ms |
| API-004 | 대량 마지막 요청과 파일 이동 | PASS | 393 ms |
| API-005 | 중복 접수 | PASS | 216 ms |
| API-006 | 필수 수신자 누락 | PASS | 131 ms |
| API-007 | 파일 누락 | PASS | 112 ms |
| API-008 | 잘못된 숫자 바인딩 | PASS | 127 ms |
| API-009 | 작업 목록 기본 조회 | PASS | 2641 ms |
| API-010 | 숫자 상태 검색 | PASS | 75 ms |
| API-011 | 타입 한글 검색 | PASS | 78 ms |
| API-012 | 작업 페이징과 동일 시각 정렬 | PASS | 63 ms |
| API-013 | 작업 빈 결과 | PASS | 59 ms |
| API-014 | 허용되지 않은 검색 컬럼 | PASS | 115 ms |
| API-015 | 잘못된 정렬 | PASS | 51 ms |
| API-016 | 페이지 크기 0 | PASS | 49 ms |
| API-017 | 일반 상태 변경 | PASS | 104 ms |
| API-018 | 실시간 수신중 복귀 | PASS | 88 ms |
| API-019 | 실시간 수신 완료 | PASS | 169 ms |
| API-020 | 잘못된 상태 JSON | PASS | 43 ms |
| API-021 | 없는 작업 상태 변경 | PASS | 61 ms |
| API-022 | 미등록 상태 코드 | PASS | 50 ms |
| API-023 | 빈 상태 변경 | PASS | 43 ms |
| API-024 | 오류 목록 | PASS | 614 ms |
| API-025 | 오류 날짜 문자열 검색 | PASS | 61 ms |
| API-026 | 오류 페이징 | PASS | 53 ms |
| API-027 | 오류 날짜 범위 | PASS | 52 ms |
| API-028 | 역전된 날짜 범위 | PASS | 38 ms |
| API-029 | 오류 검색 SQL 입력 차단 | PASS | 46 ms |
| API-030 | 오류 상세 정상 | PASS | 374 ms |
| API-031 | 없는 오류 상세 | PASS | 61 ms |
| API-032 | 잘못된 오류 ID | PASS | 55 ms |
| API-033 | 필수 파라미터 누락 | PASS | 47 ms |
| SCH-001 | 시간 경과 자동 상태 전환 | PASS | 73 ms |
| SCH-002 | 1개월 경과 삭제 | PASS | 194 ms |
| SCH-003 | 당일 단일 마감 | PASS | 239 ms |
| API-035 | 전체 작업 정렬 조합 | PASS | 357 ms |
| API-036 | 오류 코드 검색 | PASS | 70 ms |
| API-037 | 잘못된 날짜 형식 | PASS | 43 ms |
| API-038 | 페이지 입력 범위 | PASS | 58 ms |
| API-039 | 숫자가 아닌 상태 코드 | PASS | 47 ms |
| API-040 | 여러 작업 변경 사전 검사 | PASS | 52 ms |
| API-034 | 모든 공개 경로의 HTTP 실행 확인 | PASS | 34 ms |

## 작업별 Input Output Result

### API-001 전체 경로와 스케줄 격리

**검사 기준**: 업무 경로 5개 및 자동 스케줄 비활성

**Input**: 격리 테스트 DB 기준 데이터를 적재한 뒤 해당 내부 점검 작업을 직접 실행한다.

**상태 확인**: 등록 경로=[GET /error/detail, GET /errors, GET /masters, POST /detail, POST /masters/update]; scheduler.enabled=false

**Result**: PASS

### API-002 단일 우편 정상 접수

**검사 기준**: HTTP 200 및 OK/SUCCESS, 한글 DB 및 PDF 보존

**Input**: `POST /detail`

```json
{
  "CIFNO" : "00001234",
  "DLV_CD" : "01",
  "DLV_TYPE_CD" : "01",
  "DM_LINK_KEY" : "DM-SINGLE-001",
  "File" : "SINGLE-001-1.pdf (application/pdf, 413 bytes)",
  "PAGE_CNT" : "1",
  "PRINT_TYPE_NM" : "양면",
  "RECV_ADDR" : "테스트시 가상로 123",
  "RECV_ADDR_DETAIL" : "테스트동 101호",
  "RECV_NM" : "가상수신자",
  "RECV_NUM" : "1",
  "RECV_POST_CD" : "01234",
  "RET_YN" : "N",
  "TOTAL_SEND_CNT" : "1",
  "TR_KEY" : "SINGLE-001"
}
```

**Output**: HTTP 200 / application/json

```text
{"결과":"OK","비고":"SUCCESS"}
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-002-1.json)

**상태 확인**: DB={PK=SINGLE-001_1, TR_KEY=SINGLE-001, TOTAL_SEND_CNT=1, DLV_TYPE_CD=01, PRINT_TYPE_NM=양면, PAGE_CNT=1, RET_YN=N, RECV_NUM=1, DM_LINK_KEY=DM-SINGLE-001, CIFNO=00001234, DLV_CD=01, RECV_NM=가상수신자, RECV_POST_CD=01234, RECV_ADDR=테스트시 가상로 123, RECV_ADDR_DETAIL=테스트동 101호, PDF_PATH=C:\project\ds-pdf\dspdf\target\mssql-it\20261108192036321574500\receiving\2026-11-09\, SAVE_DATE=2026-11-08 19:20:44.15, MASTER=2026-11-09, PDF_NM=SINGLE-001-1.pdf}; PDF 바이트 동일; MASTER.STATUS=100

**Result**: PASS

### API-003 대량 첫 요청

**검사 기준**: HTTP 200 및 상태 100, SEND_CNT=1

**Input**: `POST /detail`

```json
{
  "CIFNO" : "00001234",
  "DLV_CD" : "01",
  "DLV_TYPE_CD" : "01",
  "DM_LINK_KEY" : "DM-BULK-003",
  "File" : "BULK-003-1.pdf (application/pdf, 413 bytes)",
  "PAGE_CNT" : "1",
  "PRINT_TYPE_NM" : "양면",
  "RECV_ADDR" : "테스트시 가상로 123",
  "RECV_ADDR_DETAIL" : "테스트동 101호",
  "RECV_NM" : "가상수신자",
  "RECV_NUM" : "1",
  "RECV_POST_CD" : "01234",
  "RET_YN" : "N",
  "TOTAL_SEND_CNT" : "2",
  "TR_KEY" : "BULK-003"
}
```

**Output**: HTTP 200 / application/json

```text
{"결과":"OK","비고":"SUCCESS"}
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-003-1.json)

**상태 확인**: MASTER={MASTER_KEY=BULK-003, TOTAL_SEND_CNT=2, STATUS=100, SEND_CNT=1, RECEIVED_TIME=2026-11-08 19:20:44.41, STATUS_TIME=2026-11-08 19:20:44.423, TYPE=ARRANGEMENT}

**Result**: PASS

### API-004 대량 마지막 요청과 파일 이동

**검사 기준**: 2건 접수, 상태 200, JSON 및 PDF 2개 complete 이동

**Input**: `POST /detail`

```json
{
  "CIFNO" : "00001234",
  "DLV_CD" : "01",
  "DLV_TYPE_CD" : "01",
  "DM_LINK_KEY" : "DM-BULK-004",
  "File" : "BULK-004-1.pdf (application/pdf, 413 bytes)",
  "PAGE_CNT" : "1",
  "PRINT_TYPE_NM" : "양면",
  "RECV_ADDR" : "테스트시 가상로 123",
  "RECV_ADDR_DETAIL" : "테스트동 101호",
  "RECV_NM" : "가상수신자",
  "RECV_NUM" : "1",
  "RECV_POST_CD" : "01234",
  "RET_YN" : "N",
  "TOTAL_SEND_CNT" : "2",
  "TR_KEY" : "BULK-004"
}
```

**Output**: HTTP 200 / application/json

```text
{"결과":"OK","비고":"SUCCESS"}
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-004-1.json)

**Input**: `POST /detail`

```json
{
  "CIFNO" : "00001234",
  "DLV_CD" : "01",
  "DLV_TYPE_CD" : "01",
  "DM_LINK_KEY" : "DM-BULK-004",
  "File" : "BULK-004-2.pdf (application/pdf, 413 bytes)",
  "PAGE_CNT" : "1",
  "PRINT_TYPE_NM" : "양면",
  "RECV_ADDR" : "테스트시 가상로 123",
  "RECV_ADDR_DETAIL" : "테스트동 101호",
  "RECV_NM" : "가상수신자",
  "RECV_NUM" : "2",
  "RECV_POST_CD" : "01234",
  "RET_YN" : "N",
  "TOTAL_SEND_CNT" : "2",
  "TR_KEY" : "BULK-004"
}
```

**Output**: HTTP 200 / application/json

```text
{"결과":"OK","비고":"SUCCESS"}
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-004-2.json)

**상태 확인**: JSON={"detail":[{"PK":"BULK-004_1","TR_KEY":"BULK-004","TOTAL_SEND_CNT":2,"DLV_TYPE_CD":"01","PRINT_TYPE_NM":"양면","PAGE_CNT":1,"RET_YN":"N","RECV_NUM":1,"DM_LINK_KEY":"DM-BULK-004","CIFNO":"00001234","DLV_CD":"01","RECV_NM":"가상수신자","RECV_POST_CD":"01234","RECV_ADDR":"테스트시 가상로 123","RECV_ADDR_DETAIL":"테스트동 101호","PDF_PATH":"C:\\project\\ds-pdf\\dspdf\\target\\mssql-it\\20261108192036321574500\\complete\\BULK-004","SAVE_DATE":[2026,11,8,19,20,44,570000000],"MASTER":"BULK-004","PDF_NM":"BULK-004-1.pdf","File":null},{"PK":"BULK-004_2","TR_KEY":"BULK-004","TOTAL_SEND_CNT":2,"DLV_TYPE_CD":"01","PRINT_TYPE_NM":"양면","PAGE_CNT":1,"RET_YN":"N","RECV_NUM":2,"DM_LINK_KEY":"DM-BULK-004","CIFNO":"00001234","DLV_CD":"01","RECV_NM":"가상수신자","RECV_POST_CD":"01234","RECV_ADDR":"테스트시 가상로 123","RECV_ADDR_DETAIL":"테스트동 101호","PDF_PATH":"C:\\project\\ds-pdf\\dspdf\\target\\mssql-it\\20261108192036321574500\\complete\\BULK-004","SAVE_DATE":[2026,11,8,19,20,44,667000000],"MASTER":"BULK-004","PDF_NM":"BULK-004-2.pdf","File":null}],"master":[{"MASTER_KEY":"BULK-004","TOTAL_SEND_CNT":2,"SEND_CNT":2,"STATUS":200,"RECEIVED_TIME":[2026,11,8,19,20,44,667000000],"STATUS_TIME":[2026,11,8,19,20,44,687000000],"TYPE":"ARRANGEMENT"}]}; receiving 디렉터리 제거; complete PDF 2건 존재

**상태 확인**: MASTER={MASTER_KEY=BULK-004, TOTAL_SEND_CNT=2, STATUS=200, SEND_CNT=2, RECEIVED_TIME=2026-11-08 19:20:44.667, STATUS_TIME=2026-11-08 19:20:44.687, TYPE=ARRANGEMENT}

**Result**: PASS

### API-005 중복 접수

**검사 기준**: HTTP 200 및 ERROR/DUPLICATE, 원본 1건 유지

**Input**: `POST /detail`

```json
{
  "CIFNO" : "00001234",
  "DLV_CD" : "01",
  "DLV_TYPE_CD" : "01",
  "DM_LINK_KEY" : "DM-DUP-005",
  "File" : "DUP-005-1.pdf (application/pdf, 413 bytes)",
  "PAGE_CNT" : "1",
  "PRINT_TYPE_NM" : "양면",
  "RECV_ADDR" : "테스트시 가상로 123",
  "RECV_ADDR_DETAIL" : "테스트동 101호",
  "RECV_NM" : "가상수신자",
  "RECV_NUM" : "1",
  "RECV_POST_CD" : "01234",
  "RET_YN" : "N",
  "TOTAL_SEND_CNT" : "2",
  "TR_KEY" : "DUP-005"
}
```

**Output**: HTTP 200 / application/json

```text
{"결과":"OK","비고":"SUCCESS"}
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-005-1.json)

**Input**: `POST /detail`

```json
{
  "CIFNO" : "00001234",
  "DLV_CD" : "01",
  "DLV_TYPE_CD" : "01",
  "DM_LINK_KEY" : "DM-DUP-005",
  "File" : "DUP-005-1.pdf (application/pdf, 413 bytes)",
  "PAGE_CNT" : "1",
  "PRINT_TYPE_NM" : "양면",
  "RECV_ADDR" : "테스트시 가상로 123",
  "RECV_ADDR_DETAIL" : "테스트동 101호",
  "RECV_NM" : "가상수신자",
  "RECV_NUM" : "1",
  "RECV_POST_CD" : "01234",
  "RET_YN" : "N",
  "TOTAL_SEND_CNT" : "2",
  "TR_KEY" : "DUP-005"
}
```

**Output**: HTTP 200 / application/json

```text
{"결과":"ERROR","비고":"DUPLICATE 중복된 요청 : Detail(TR_KEY=DUP-005, TOTAL_SEND_CNT=2, DLV_TYPE_CD=01, PRINT_TYPE_NM=양면, PAGE_CNT=1, RET_YN=N, RECV_NUM=1, DM_LINK_KEY=DM-DUP-005, CIFNO=00001234, DLV_CD=01, RECV_NM=가상수신자, RECV_POST_CD=01234, RECV_ADDR=테스트시 가상로 123, RECV_ADDR_DETAIL=테스트동 101호, File=org.springframework.web.multipart.support.StandardMultipartHttpServletRequest$StandardMultipartFile@7a4a74bb)"}
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-005-2.json)

**상태 확인**: DETAIL=1; DUPLICATE 오류 이력=1

**Result**: PASS

### API-006 필수 수신자 누락

**검사 기준**: HTTP 200 및 ERROR/MISSING_VALUE, 저장 없음

**Input**: `POST /detail`

```json
{
  "CIFNO" : "00001234",
  "DLV_CD" : "01",
  "DLV_TYPE_CD" : "01",
  "DM_LINK_KEY" : "DM-MISSING-006",
  "File" : "MISSING-006-1.pdf (application/pdf, 413 bytes)",
  "PAGE_CNT" : "1",
  "PRINT_TYPE_NM" : "양면",
  "RECV_ADDR" : "테스트시 가상로 123",
  "RECV_ADDR_DETAIL" : "테스트동 101호",
  "RECV_NUM" : "1",
  "RECV_POST_CD" : "01234",
  "RET_YN" : "N",
  "TOTAL_SEND_CNT" : "2",
  "TR_KEY" : "MISSING-006"
}
```

**Output**: HTTP 200 / application/json

```text
{"결과":"ERROR","비고":"MISSING_VALUE : RECV_NM"}
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-006-1.json)

**상태 확인**: DETAIL=0; 누락 오류 이력=1

**Result**: PASS

### API-007 파일 누락

**검사 기준**: HTTP 200 및 ERROR/MISSING_VALUE

**Input**: `POST /detail`

```json
{
  "CIFNO" : "00001234",
  "DLV_CD" : "01",
  "DLV_TYPE_CD" : "01",
  "DM_LINK_KEY" : "DM-NOFILE-007",
  "PAGE_CNT" : "1",
  "PRINT_TYPE_NM" : "양면",
  "RECV_ADDR" : "테스트시 가상로 123",
  "RECV_ADDR_DETAIL" : "테스트동 101호",
  "RECV_NM" : "가상수신자",
  "RECV_NUM" : "1",
  "RECV_POST_CD" : "01234",
  "RET_YN" : "N",
  "TOTAL_SEND_CNT" : "2",
  "TR_KEY" : "NOFILE-007"
}
```

**Output**: HTTP 200 / application/json

```text
{"결과":"ERROR","비고":"MISSING_VALUE : pdf file"}
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-007-1.json)

**Result**: PASS

### API-008 잘못된 숫자 바인딩

**검사 기준**: HTTP 400

**Input**: `POST /detail`

```json
{
  "TOTAL_SEND_CNT" : "abc"
}
```

**Output**: HTTP 400 / text/html;charset=UTF-8

```text
Whitelabel Error Page This application has no explicit mapping for /error, so you are seeing this as a fallback. Sun Nov 08 19:20:45 KST 2026 There was an unexpected error (type=Bad Request, status=400).
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-008-1.html)

**Result**: PASS

### API-009 작업 목록 기본 조회

**검사 기준**: HTTP 200 및 JSP 실제 렌더링

**Input**: `GET /masters`

```json
{ }
```

**Output**: HTTP 200 / text/html;charset=UTF-8

```text
master_list 상태 검색시에는 코드로 검색해주세요 / [�닔�떊以� : 100] / [�닔�떊�셿猷� : 200] / [異쒕젰以� : 300] / [諛쒖넚�셿猷� : 400] / [�삤瑜� : 900] 그룹키 현재 상태 타입 그룹키 현재 수신 건수 / 총 건수 마지막 건수 수신 시각 현재 상태 시작 시각 현재 상태 상태 수정 타입 BATCH-SEED 2 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊�셿猷� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-01 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-02 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-03 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 RT-SEED 1 / 0 2026-01-01T10:00 20 ...
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-009-1.html)

**Result**: PASS

### API-010 숫자 상태 검색

**검사 기준**: HTTP 200, STATUS=200 작업만 표시

**Input**: `GET /masters`

```json
{
  "cate" : "STATUS",
  "search" : "200"
}
```

**Output**: HTTP 200 / text/html;charset=UTF-8

```text
master_list 상태 검색시에는 코드로 검색해주세요 / [�닔�떊以� : 100] / [�닔�떊�셿猷� : 200] / [異쒕젰以� : 300] / [諛쒖넚�셿猷� : 400] / [�삤瑜� : 900] 그룹키 현재 상태 타입 그룹키 현재 수신 건수 / 총 건수 마지막 건수 수신 시각 현재 상태 시작 시각 현재 상태 상태 수정 타입 BATCH-SEED 2 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊�셿猷� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 총 검색 건수 : 1 그룹키 현재 상태 마지막 건수 수신 시각 상태 변경 시각 타입 오름차순 내림차순 &nbsp;&nbsp;&nbsp;&nbsp; 10 20 30 40 50 60 70 80 90 100 &nbsp;&nbsp;&nbsp;&nbsp; 에러 확인 1 > >>
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-010-1.html)

**Result**: PASS

### API-011 타입 한글 검색

**검사 기준**: 실시간을 REAL_TIME으로 검색

**Input**: `GET /masters`

```json
{
  "cate" : "TYPE",
  "search" : "실시간"
}
```

**Output**: HTTP 200 / text/html;charset=UTF-8

```text
master_list 상태 검색시에는 코드로 검색해주세요 / [�닔�떊以� : 100] / [�닔�떊�셿猷� : 200] / [異쒕젰以� : 300] / [諛쒖넚�셿猷� : 400] / [�삤瑜� : 900] 그룹키 현재 상태 타입 그룹키 현재 수신 건수 / 총 건수 마지막 건수 수신 시각 현재 상태 시작 시각 현재 상태 상태 수정 타입 RT-SEED 1 / 0 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 실시간 총 검색 건수 : 1 그룹키 현재 상태 마지막 건수 수신 시각 상태 변경 시각 타입 오름차순 내림차순 &nbsp;&nbsp;&nbsp;&nbsp; 10 20 30 40 50 60 70 80 90 100 &nbsp;&nbsp;&nbsp;&nbsp; 에러 확인 1 > >>
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-011-1.html)

**Result**: PASS

### API-012 작업 페이징과 동일 시각 정렬

**검사 기준**: 2페이지 PAGE-03만 표시

**Input**: `GET /masters`

```json
{
  "page" : "2",
  "pageElement" : "2",
  "search" : "PAGE-",
  "sort" : "ASC"
}
```

**Output**: HTTP 200 / text/html;charset=UTF-8

```text
master_list 상태 검색시에는 코드로 검색해주세요 / [�닔�떊以� : 100] / [�닔�떊�셿猷� : 200] / [異쒕젰以� : 300] / [諛쒖넚�셿猷� : 400] / [�삤瑜� : 900] 그룹키 현재 상태 타입 그룹키 현재 수신 건수 / 총 건수 마지막 건수 수신 시각 현재 상태 시작 시각 현재 상태 상태 수정 타입 PAGE-03 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 총 검색 건수 : 3 그룹키 현재 상태 마지막 건수 수신 시각 상태 변경 시각 타입 오름차순 내림차순 &nbsp;&nbsp;&nbsp;&nbsp; 10 20 30 40 50 60 70 80 90 100 &nbsp;&nbsp;&nbsp;&nbsp; 에러 확인 1 2 > >>
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-012-1.html)

**Result**: PASS

### API-013 작업 빈 결과

**검사 기준**: HTTP 200, 총 검색 건수 0

**Input**: `GET /masters`

```json
{
  "search" : "NOT-FOUND"
}
```

**Output**: HTTP 200 / text/html;charset=UTF-8

```text
master_list 상태 검색시에는 코드로 검색해주세요 / [�닔�떊以� : 100] / [�닔�떊�셿猷� : 200] / [異쒕젰以� : 300] / [諛쒖넚�셿猷� : 400] / [�삤瑜� : 900] 그룹키 현재 상태 타입 그룹키 현재 수신 건수 / 총 건수 마지막 건수 수신 시각 현재 상태 시작 시각 현재 상태 상태 수정 타입 총 검색 건수 : 0 그룹키 현재 상태 마지막 건수 수신 시각 상태 변경 시각 타입 오름차순 내림차순 &nbsp;&nbsp;&nbsp;&nbsp; 10 20 30 40 50 60 70 80 90 100 &nbsp;&nbsp;&nbsp;&nbsp; 에러 확인 > >>
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-013-1.html)

**Result**: PASS

### API-014 허용되지 않은 검색 컬럼

**검사 기준**: HTTP 400, 테이블 보존

**Input**: `GET /masters`

```json
{
  "cate" : "MASTER_KEY; DROP TABLE MASTER;--"
}
```

**Output**: HTTP 400 / text/html;charset=UTF-8

```text
Whitelabel Error Page This application has no explicit mapping for /error, so you are seeing this as a fallback. Sun Nov 08 19:20:48 KST 2026 There was an unexpected error (type=Bad Request, status=400).
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-014-1.html)

**Result**: PASS

### API-015 잘못된 정렬

**검사 기준**: HTTP 400

**Input**: `GET /masters`

```json
{
  "sort" : "DESC;--"
}
```

**Output**: HTTP 400 / text/html;charset=UTF-8

```text
Whitelabel Error Page This application has no explicit mapping for /error, so you are seeing this as a fallback. Sun Nov 08 19:20:48 KST 2026 There was an unexpected error (type=Bad Request, status=400).
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-015-1.html)

**Result**: PASS

### API-016 페이지 크기 0

**검사 기준**: HTTP 400

**Input**: `GET /masters`

```json
{
  "pageElement" : "0"
}
```

**Output**: HTTP 400 / text/html;charset=UTF-8

```text
Whitelabel Error Page This application has no explicit mapping for /error, so you are seeing this as a fallback. Sun Nov 08 19:20:48 KST 2026 There was an unexpected error (type=Bad Request, status=400).
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-016-1.html)

**Result**: PASS

### API-017 일반 상태 변경

**검사 기준**: HTTP 200, 상태 300 및 STATUS_TIME 갱신

**Input**: `POST /masters/update`

```json
{
  "statusData" : "{\"BATCH-SEED\":\"300\"}"
}
```

**Output**: HTTP 200 / text/html;charset=UTF-8

```text
master_list 상태 검색시에는 코드로 검색해주세요 / [�닔�떊以� : 100] / [�닔�떊�셿猷� : 200] / [異쒕젰以� : 300] / [諛쒖넚�셿猷� : 400] / [�삤瑜� : 900] 그룹키 현재 상태 타입 그룹키 현재 수신 건수 / 총 건수 마지막 건수 수신 시각 현재 상태 시작 시각 현재 상태 상태 수정 타입 BATCH-SEED 2 / 2 2026-01-01T10:00 2026-11-08T19:20:48.643 異쒕젰以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-01 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-02 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-03 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 RT-SEED 1 / 0 2026-01-01T10:0 ...
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-017-1.html)

**상태 확인**: MASTER={MASTER_KEY=BATCH-SEED, TOTAL_SEND_CNT=2, STATUS=300, SEND_CNT=2, RECEIVED_TIME=2026-01-01 10:00:00.0, STATUS_TIME=2026-11-08 19:20:48.643, TYPE=ARRANGEMENT}

**Result**: PASS

### API-018 실시간 수신중 복귀

**검사 기준**: TOTAL_SEND_CNT가 0으로 초기화

**Input**: `POST /masters/update`

```json
{
  "statusData" : "{\"RT-SEED\":\"100\"}"
}
```

**Output**: HTTP 200 / text/html;charset=UTF-8

```text
master_list 상태 검색시에는 코드로 검색해주세요 / [�닔�떊以� : 100] / [�닔�떊�셿猷� : 200] / [異쒕젰以� : 300] / [諛쒖넚�셿猷� : 400] / [�삤瑜� : 900] 그룹키 현재 상태 타입 그룹키 현재 수신 건수 / 총 건수 마지막 건수 수신 시각 현재 상태 시작 시각 현재 상태 상태 수정 타입 RT-SEED 1 / 0 2026-01-01T10:00 2026-11-08T19:20:48.737 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 실시간 BATCH-SEED 2 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊�셿猷� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-01 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-02 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-03 1 / 2 2026-01-01T ...
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-018-1.html)

**상태 확인**: MASTER={MASTER_KEY=RT-SEED, TOTAL_SEND_CNT=0, STATUS=100, SEND_CNT=1, RECEIVED_TIME=2026-01-01 10:00:00.0, STATUS_TIME=2026-11-08 19:20:48.737, TYPE=REAL_TIME}

**Result**: PASS

### API-019 실시간 수신 완료

**검사 기준**: 전체 건수를 실제 DETAIL 수로 갱신

**Input**: `POST /detail`

```json
{
  "CIFNO" : "00001234",
  "DLV_CD" : "01",
  "DLV_TYPE_CD" : "01",
  "DM_LINK_KEY" : "DM-RT-019",
  "File" : "RT-019-1.pdf (application/pdf, 413 bytes)",
  "PAGE_CNT" : "1",
  "PRINT_TYPE_NM" : "양면",
  "RECV_ADDR" : "테스트시 가상로 123",
  "RECV_ADDR_DETAIL" : "테스트동 101호",
  "RECV_NM" : "가상수신자",
  "RECV_NUM" : "1",
  "RECV_POST_CD" : "01234",
  "RET_YN" : "N",
  "TOTAL_SEND_CNT" : "1",
  "TR_KEY" : "RT-019"
}
```

**Output**: HTTP 200 / application/json

```text
{"결과":"OK","비고":"SUCCESS"}
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-019-1.json)

**Input**: `POST /masters/update`

```json
{
  "statusData" : "{\"2026-11-09\":\"200\"}"
}
```

**Output**: HTTP 200 / text/html;charset=UTF-8

```text
master_list 상태 검색시에는 코드로 검색해주세요 / [�닔�떊以� : 100] / [�닔�떊�셿猷� : 200] / [異쒕젰以� : 300] / [諛쒖넚�셿猷� : 400] / [�삤瑜� : 900] 그룹키 현재 상태 타입 그룹키 현재 수신 건수 / 총 건수 마지막 건수 수신 시각 현재 상태 시작 시각 현재 상태 상태 수정 타입 2026-11-09 1 / 1 2026-11-08T19:20:48.873 2026-11-08T19:20:48.913 �닔�떊�셿猷� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 실시간 BATCH-SEED 2 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊�셿猷� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-01 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-02 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-03 1 / 2 ...
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-019-2.html)

**상태 확인**: MASTER={MASTER_KEY=2026-11-09, TOTAL_SEND_CNT=1, STATUS=200, SEND_CNT=1, RECEIVED_TIME=2026-11-08 19:20:48.873, STATUS_TIME=2026-11-08 19:20:48.913, TYPE=REAL_TIME}

**Result**: PASS

### API-020 잘못된 상태 JSON

**검사 기준**: HTTP 400

**Input**: `POST /masters/update`

```json
{
  "statusData" : "not-json"
}
```

**Output**: HTTP 400 / 

```text

```

[응답 원문](../target/mssql-it/20261108192036321574500/API-020-1.html)

**Result**: PASS

### API-021 없는 작업 상태 변경

**검사 기준**: HTTP 404

**Input**: `POST /masters/update`

```json
{
  "statusData" : "{\"UNKNOWN\":\"300\"}"
}
```

**Output**: HTTP 404 / text/html;charset=UTF-8

```text
Whitelabel Error Page This application has no explicit mapping for /error, so you are seeing this as a fallback. Sun Nov 08 19:20:49 KST 2026 There was an unexpected error (type=Not Found, status=404).
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-021-1.html)

**Result**: PASS

### API-022 미등록 상태 코드

**검사 기준**: HTTP 400, 기존 상태 보존

**Input**: `POST /masters/update`

```json
{
  "statusData" : "{\"BATCH-SEED\":\"777\"}"
}
```

**Output**: HTTP 400 / text/html;charset=UTF-8

```text
Whitelabel Error Page This application has no explicit mapping for /error, so you are seeing this as a fallback. Sun Nov 08 19:20:49 KST 2026 There was an unexpected error (type=Bad Request, status=400).
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-022-1.html)

**Result**: PASS

### API-023 빈 상태 변경

**검사 기준**: HTTP 400

**Input**: `POST /masters/update`

```json
{
  "statusData" : "{}"
}
```

**Output**: HTTP 400 / text/html;charset=UTF-8

```text
Whitelabel Error Page This application has no explicit mapping for /error, so you are seeing this as a fallback. Sun Nov 08 19:20:49 KST 2026 There was an unexpected error (type=Bad Request, status=400).
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-023-1.html)

**Result**: PASS

### API-024 오류 목록

**검사 기준**: HTTP 200, SAMPLE_ERROR 렌더링

**Input**: `GET /errors`

```json
{ }
```

**Output**: HTTP 200 / text/html;charset=UTF-8

```text
error_list 일자 에러 코드 그룹 NUM 내용 일자 에러 코드 그룹 1 내용 확인 2026-11-08T19:20:49.190 SAMPLE_ERROR BATCH-SEED 일자 오름차순 내림차순 &nbsp;&nbsp;&nbsp;&nbsp; 10 20 30 40 50 60 70 80 90 100 &nbsp;&nbsp;&nbsp;&nbsp; 목록으로 1 > >>
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-024-1.html)

**Result**: PASS

### API-025 오류 날짜 문자열 검색

**검사 기준**: ISO 날짜 검색 성공

**Input**: `GET /errors`

```json
{
  "cate" : "CREATE_DATE",
  "search" : "2026-11-08"
}
```

**Output**: HTTP 200 / text/html;charset=UTF-8

```text
error_list 일자 에러 코드 그룹 NUM 내용 일자 에러 코드 그룹 1 내용 확인 2026-11-08T19:20:49.807 SAMPLE_ERROR BATCH-SEED 일자 오름차순 내림차순 &nbsp;&nbsp;&nbsp;&nbsp; 10 20 30 40 50 60 70 80 90 100 &nbsp;&nbsp;&nbsp;&nbsp; 목록으로 1 > >>
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-025-1.html)

**Result**: PASS

### API-026 오류 페이징

**검사 기준**: 2페이지의 두 번째 오류만 표시

**Input**: `GET /errors`

```json
{
  "page" : "2",
  "pageElement" : "1",
  "sort" : "ASC"
}
```

**Output**: HTTP 200 / text/html;charset=UTF-8

```text
error_list 일자 에러 코드 그룹 NUM 내용 일자 에러 코드 그룹 2 내용 확인 2026-11-08T19:20:49.863 SECOND_CODE SECOND-ERROR 일자 오름차순 내림차순 &nbsp;&nbsp;&nbsp;&nbsp; 10 20 30 40 50 60 70 80 90 100 &nbsp;&nbsp;&nbsp;&nbsp; 목록으로 1 2 > >>
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-026-1.html)

**Result**: PASS

### API-027 오류 날짜 범위

**검사 기준**: 범위 밖 데이터 제외

**Input**: `GET /errors`

```json
{
  "endDate" : "2000-01-02T00:00",
  "startDate" : "2000-01-01T00:00"
}
```

**Output**: HTTP 200 / text/html;charset=UTF-8

```text
error_list 일자 에러 코드 그룹 NUM 내용 일자 에러 코드 그룹 일자 오름차순 내림차순 &nbsp;&nbsp;&nbsp;&nbsp; 10 20 30 40 50 60 70 80 90 100 &nbsp;&nbsp;&nbsp;&nbsp; 목록으로 > >>
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-027-1.html)

**Result**: PASS

### API-028 역전된 날짜 범위

**검사 기준**: HTTP 400

**Input**: `GET /errors`

```json
{
  "endDate" : "2026-01-01T00:00",
  "startDate" : "2026-02-01T00:00"
}
```

**Output**: HTTP 400 / text/html;charset=UTF-8

```text
Whitelabel Error Page This application has no explicit mapping for /error, so you are seeing this as a fallback. Sun Nov 08 19:20:49 KST 2026 There was an unexpected error (type=Bad Request, status=400).
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-028-1.html)

**Result**: PASS

### API-029 오류 검색 SQL 입력 차단

**검사 기준**: HTTP 400

**Input**: `GET /errors`

```json
{
  "sortCate" : "CREATE_DATE; DROP TABLE ERROR;--"
}
```

**Output**: HTTP 400 / text/html;charset=UTF-8

```text
Whitelabel Error Page This application has no explicit mapping for /error, so you are seeing this as a fallback. Sun Nov 08 19:20:50 KST 2026 There was an unexpected error (type=Bad Request, status=400).
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-029-1.html)

**Result**: PASS

### API-030 오류 상세 정상

**검사 기준**: HTTP 200 및 한글 원문

**Input**: `GET /error/detail`

```json
{
  "errorId" : "1"
}
```

**Output**: HTTP 200 / text/html;charset=UTF-8

```text
error_list 샘플 오류 메시지 한글 주소 검증
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-030-1.html)

**Result**: PASS

### API-031 없는 오류 상세

**검사 기준**: HTTP 404

**Input**: `GET /error/detail`

```json
{
  "errorId" : "999999"
}
```

**Output**: HTTP 404 / text/html;charset=UTF-8

```text
Whitelabel Error Page This application has no explicit mapping for /error, so you are seeing this as a fallback. Sun Nov 08 19:20:50 KST 2026 There was an unexpected error (type=Not Found, status=404).
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-031-1.html)

**Result**: PASS

### API-032 잘못된 오류 ID

**검사 기준**: HTTP 400

**Input**: `GET /error/detail`

```json
{
  "errorId" : "abc"
}
```

**Output**: HTTP 400 / text/html;charset=UTF-8

```text
Whitelabel Error Page This application has no explicit mapping for /error, so you are seeing this as a fallback. Sun Nov 08 19:20:50 KST 2026 There was an unexpected error (type=Bad Request, status=400).
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-032-1.html)

**Result**: PASS

### API-033 필수 파라미터 누락

**검사 기준**: HTTP 400

**Input**: `GET /error/detail`

```json
{ }
```

**Output**: HTTP 400 / text/html;charset=UTF-8

```text
Whitelabel Error Page This application has no explicit mapping for /error, so you are seeing this as a fallback. Sun Nov 08 19:20:50 KST 2026 There was an unexpected error (type=Bad Request, status=400).
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-033-1.html)

**Result**: PASS

### SCH-001 시간 경과 자동 상태 전환

**검사 기준**: 오래된 AUTO 300만 400으로 전환

**Input**: 격리 테스트 DB 기준 데이터를 적재한 뒤 해당 내부 점검 작업을 직접 실행한다.

**상태 확인**: MASTER={MASTER_KEY=PAGE-01, TOTAL_SEND_CNT=2, STATUS=400, SEND_CNT=1, RECEIVED_TIME=2026-01-01 10:00:00.0, STATUS_TIME=2026-11-08 19:20:50.623, TYPE=ARRANGEMENT}

**상태 확인**: MASTER={MASTER_KEY=PAGE-02, TOTAL_SEND_CNT=2, STATUS=300, SEND_CNT=1, RECEIVED_TIME=2026-01-01 10:00:00.0, STATUS_TIME=2026-11-08 19:30:50.61, TYPE=ARRANGEMENT}

**Result**: PASS

### SCH-002 1개월 경과 삭제

**검사 기준**: 대상 상태의 오래된 MASTER와 DETAIL만 삭제, ERROR 및 PDF 보존

**Input**: `POST /detail`

```json
{
  "CIFNO" : "00001234",
  "DLV_CD" : "01",
  "DLV_TYPE_CD" : "01",
  "DM_LINK_KEY" : "DM-OLD-DELETE",
  "File" : "OLD-DELETE-1.pdf (application/pdf, 413 bytes)",
  "PAGE_CNT" : "1",
  "PRINT_TYPE_NM" : "양면",
  "RECV_ADDR" : "테스트시 가상로 123",
  "RECV_ADDR_DETAIL" : "테스트동 101호",
  "RECV_NM" : "가상수신자",
  "RECV_NUM" : "1",
  "RECV_POST_CD" : "01234",
  "RET_YN" : "N",
  "TOTAL_SEND_CNT" : "2",
  "TR_KEY" : "OLD-DELETE"
}
```

**Output**: HTTP 200 / application/json

```text
{"결과":"OK","비고":"SUCCESS"}
```

[응답 원문](../target/mssql-it/20261108192036321574500/SCH-002-1.json)

**상태 확인**: 오래된 MASTER/DETAIL=0; 최근 MASTER=1; ERROR=1; PDF 존재

**Result**: PASS

### SCH-003 당일 단일 마감

**검사 기준**: 당일 묶음 상태 200, 전체 건수 1, JSON과 PDF 이동

**Input**: `POST /detail`

```json
{
  "CIFNO" : "00001234",
  "DLV_CD" : "01",
  "DLV_TYPE_CD" : "01",
  "DM_LINK_KEY" : "DM-CLOSE-TODAY",
  "File" : "CLOSE-TODAY-1.pdf (application/pdf, 413 bytes)",
  "PAGE_CNT" : "1",
  "PRINT_TYPE_NM" : "양면",
  "RECV_ADDR" : "테스트시 가상로 123",
  "RECV_ADDR_DETAIL" : "테스트동 101호",
  "RECV_NM" : "가상수신자",
  "RECV_NUM" : "1",
  "RECV_POST_CD" : "01234",
  "RET_YN" : "N",
  "TOTAL_SEND_CNT" : "1",
  "TR_KEY" : "CLOSE-TODAY"
}
```

**Output**: HTTP 200 / application/json

```text
{"결과":"OK","비고":"SUCCESS"}
```

[응답 원문](../target/mssql-it/20261108192036321574500/SCH-003-1.json)

**상태 확인**: MASTER={MASTER_KEY=2026-11-08, TOTAL_SEND_CNT=1, STATUS=200, SEND_CNT=1, RECEIVED_TIME=2026-11-08 19:20:50.97, STATUS_TIME=2026-11-08 19:20:51.017, TYPE=REAL_TIME}

**Result**: PASS

### API-035 전체 작업 정렬 조합

**검사 기준**: 허용 컬럼 5개와 방향 2개 모두 조회 성공

**Input**: `GET /masters`

```json
{
  "sort" : "ASC",
  "sortCate" : "MASTER_KEY"
}
```

**Output**: HTTP 200 / text/html;charset=UTF-8

```text
master_list 상태 검색시에는 코드로 검색해주세요 / [�닔�떊以� : 100] / [�닔�떊�셿猷� : 200] / [異쒕젰以� : 300] / [諛쒖넚�셿猷� : 400] / [�삤瑜� : 900] 그룹키 현재 상태 타입 그룹키 현재 수신 건수 / 총 건수 마지막 건수 수신 시각 현재 상태 시작 시각 현재 상태 상태 수정 타입 BATCH-SEED 2 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊�셿猷� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-01 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-02 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-03 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 RT-SEED 1 / 0 2026-01-01T10:00 20 ...
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-035-1.html)

**Input**: `GET /masters`

```json
{
  "sort" : "DESC",
  "sortCate" : "MASTER_KEY"
}
```

**Output**: HTTP 200 / text/html;charset=UTF-8

```text
master_list 상태 검색시에는 코드로 검색해주세요 / [�닔�떊以� : 100] / [�닔�떊�셿猷� : 200] / [異쒕젰以� : 300] / [諛쒖넚�셿猷� : 400] / [�삤瑜� : 900] 그룹키 현재 상태 타입 그룹키 현재 수신 건수 / 총 건수 마지막 건수 수신 시각 현재 상태 시작 시각 현재 상태 상태 수정 타입 RT-SEED 1 / 0 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 실시간 PAGE-03 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-02 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-01 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 BATCH-SEED 2 / 2 2026-01-01T10:00 202 ...
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-035-2.html)

**Input**: `GET /masters`

```json
{
  "sort" : "ASC",
  "sortCate" : "STATUS"
}
```

**Output**: HTTP 200 / text/html;charset=UTF-8

```text
master_list 상태 검색시에는 코드로 검색해주세요 / [�닔�떊以� : 100] / [�닔�떊�셿猷� : 200] / [異쒕젰以� : 300] / [諛쒖넚�셿猷� : 400] / [�삤瑜� : 900] 그룹키 현재 상태 타입 그룹키 현재 수신 건수 / 총 건수 마지막 건수 수신 시각 현재 상태 시작 시각 현재 상태 상태 수정 타입 PAGE-01 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-02 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-03 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 RT-SEED 1 / 0 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 실시간 BATCH-SEED 2 / 2 2026-01-01T10:00 202 ...
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-035-3.html)

**Input**: `GET /masters`

```json
{
  "sort" : "DESC",
  "sortCate" : "STATUS"
}
```

**Output**: HTTP 200 / text/html;charset=UTF-8

```text
master_list 상태 검색시에는 코드로 검색해주세요 / [�닔�떊以� : 100] / [�닔�떊�셿猷� : 200] / [異쒕젰以� : 300] / [諛쒖넚�셿猷� : 400] / [�삤瑜� : 900] 그룹키 현재 상태 타입 그룹키 현재 수신 건수 / 총 건수 마지막 건수 수신 시각 현재 상태 시작 시각 현재 상태 상태 수정 타입 BATCH-SEED 2 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊�셿猷� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-01 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-02 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-03 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 RT-SEED 1 / 0 2026-01-01T10:00 20 ...
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-035-4.html)

**Input**: `GET /masters`

```json
{
  "sort" : "ASC",
  "sortCate" : "RECEIVED_TIME"
}
```

**Output**: HTTP 200 / text/html;charset=UTF-8

```text
master_list 상태 검색시에는 코드로 검색해주세요 / [�닔�떊以� : 100] / [�닔�떊�셿猷� : 200] / [異쒕젰以� : 300] / [諛쒖넚�셿猷� : 400] / [�삤瑜� : 900] 그룹키 현재 상태 타입 그룹키 현재 수신 건수 / 총 건수 마지막 건수 수신 시각 현재 상태 시작 시각 현재 상태 상태 수정 타입 BATCH-SEED 2 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊�셿猷� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-01 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-02 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-03 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 RT-SEED 1 / 0 2026-01-01T10:00 20 ...
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-035-5.html)

**Input**: `GET /masters`

```json
{
  "sort" : "DESC",
  "sortCate" : "RECEIVED_TIME"
}
```

**Output**: HTTP 200 / text/html;charset=UTF-8

```text
master_list 상태 검색시에는 코드로 검색해주세요 / [�닔�떊以� : 100] / [�닔�떊�셿猷� : 200] / [異쒕젰以� : 300] / [諛쒖넚�셿猷� : 400] / [�삤瑜� : 900] 그룹키 현재 상태 타입 그룹키 현재 수신 건수 / 총 건수 마지막 건수 수신 시각 현재 상태 시작 시각 현재 상태 상태 수정 타입 BATCH-SEED 2 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊�셿猷� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-01 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-02 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-03 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 RT-SEED 1 / 0 2026-01-01T10:00 20 ...
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-035-6.html)

**Input**: `GET /masters`

```json
{
  "sort" : "ASC",
  "sortCate" : "STATUS_TIME"
}
```

**Output**: HTTP 200 / text/html;charset=UTF-8

```text
master_list 상태 검색시에는 코드로 검색해주세요 / [�닔�떊以� : 100] / [�닔�떊�셿猷� : 200] / [異쒕젰以� : 300] / [諛쒖넚�셿猷� : 400] / [�삤瑜� : 900] 그룹키 현재 상태 타입 그룹키 현재 수신 건수 / 총 건수 마지막 건수 수신 시각 현재 상태 시작 시각 현재 상태 상태 수정 타입 BATCH-SEED 2 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊�셿猷� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-01 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-02 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-03 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 RT-SEED 1 / 0 2026-01-01T10:00 20 ...
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-035-7.html)

**Input**: `GET /masters`

```json
{
  "sort" : "DESC",
  "sortCate" : "STATUS_TIME"
}
```

**Output**: HTTP 200 / text/html;charset=UTF-8

```text
master_list 상태 검색시에는 코드로 검색해주세요 / [�닔�떊以� : 100] / [�닔�떊�셿猷� : 200] / [異쒕젰以� : 300] / [諛쒖넚�셿猷� : 400] / [�삤瑜� : 900] 그룹키 현재 상태 타입 그룹키 현재 수신 건수 / 총 건수 마지막 건수 수신 시각 현재 상태 시작 시각 현재 상태 상태 수정 타입 BATCH-SEED 2 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊�셿猷� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-01 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-02 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-03 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 RT-SEED 1 / 0 2026-01-01T10:00 20 ...
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-035-8.html)

**Input**: `GET /masters`

```json
{
  "sort" : "ASC",
  "sortCate" : "TYPE"
}
```

**Output**: HTTP 200 / text/html;charset=UTF-8

```text
master_list 상태 검색시에는 코드로 검색해주세요 / [�닔�떊以� : 100] / [�닔�떊�셿猷� : 200] / [異쒕젰以� : 300] / [諛쒖넚�셿猷� : 400] / [�삤瑜� : 900] 그룹키 현재 상태 타입 그룹키 현재 수신 건수 / 총 건수 마지막 건수 수신 시각 현재 상태 시작 시각 현재 상태 상태 수정 타입 BATCH-SEED 2 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊�셿猷� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-01 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-02 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-03 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 RT-SEED 1 / 0 2026-01-01T10:00 20 ...
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-035-9.html)

**Input**: `GET /masters`

```json
{
  "sort" : "DESC",
  "sortCate" : "TYPE"
}
```

**Output**: HTTP 200 / text/html;charset=UTF-8

```text
master_list 상태 검색시에는 코드로 검색해주세요 / [�닔�떊以� : 100] / [�닔�떊�셿猷� : 200] / [異쒕젰以� : 300] / [諛쒖넚�셿猷� : 400] / [�삤瑜� : 900] 그룹키 현재 상태 타입 그룹키 현재 수신 건수 / 총 건수 마지막 건수 수신 시각 현재 상태 시작 시각 현재 상태 상태 수정 타입 RT-SEED 1 / 0 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 실시간 BATCH-SEED 2 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊�셿猷� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-01 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-02 1 / 2 2026-01-01T10:00 2026-01-01T10:00 �닔�떊以� �닔�떊以� �닔�떊�셿猷� 異쒕젰以� 諛쒖넚�셿猷� �삤瑜� 배치 PAGE-03 1 / 2 2026-01-01T10:00 2 ...
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-035-10.html)

**Result**: PASS

### API-036 오류 코드 검색

**검사 기준**: SAMPLE_ERROR 검색과 다른 코드 제외

**Input**: `GET /errors`

```json
{
  "cate" : "ERROR_CODE",
  "search" : "SAMPLE_ERROR"
}
```

**Output**: HTTP 200 / text/html;charset=UTF-8

```text
error_list 일자 에러 코드 그룹 NUM 내용 일자 에러 코드 그룹 1 내용 확인 2026-11-08T19:20:51.470 SAMPLE_ERROR BATCH-SEED 일자 오름차순 내림차순 &nbsp;&nbsp;&nbsp;&nbsp; 10 20 30 40 50 60 70 80 90 100 &nbsp;&nbsp;&nbsp;&nbsp; 목록으로 1 > >>
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-036-1.html)

**Input**: `GET /errors`

```json
{
  "cate" : "ERROR_CODE",
  "search" : "NO_MATCH"
}
```

**Output**: HTTP 200 / text/html;charset=UTF-8

```text
error_list 일자 에러 코드 그룹 NUM 내용 일자 에러 코드 그룹 일자 오름차순 내림차순 &nbsp;&nbsp;&nbsp;&nbsp; 10 20 30 40 50 60 70 80 90 100 &nbsp;&nbsp;&nbsp;&nbsp; 목록으로 > >>
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-036-2.html)

**Result**: PASS

### API-037 잘못된 날짜 형식

**검사 기준**: HTTP 400

**Input**: `GET /errors`

```json
{
  "startDate" : "not-a-date"
}
```

**Output**: HTTP 400 / text/html;charset=UTF-8

```text
Whitelabel Error Page This application has no explicit mapping for /error, so you are seeing this as a fallback. Sun Nov 08 19:20:51 KST 2026 There was an unexpected error (type=Bad Request, status=400).
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-037-1.html)

**Result**: PASS

### API-038 페이지 입력 범위

**검사 기준**: 음수 페이지와 200 초과 크기 HTTP 400

**Input**: `GET /masters`

```json
{
  "page" : "-1"
}
```

**Output**: HTTP 400 / text/html;charset=UTF-8

```text
Whitelabel Error Page This application has no explicit mapping for /error, so you are seeing this as a fallback. Sun Nov 08 19:20:51 KST 2026 There was an unexpected error (type=Bad Request, status=400).
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-038-1.html)

**Input**: `GET /errors`

```json
{
  "pageElement" : "201"
}
```

**Output**: HTTP 400 / text/html;charset=UTF-8

```text
Whitelabel Error Page This application has no explicit mapping for /error, so you are seeing this as a fallback. Sun Nov 08 19:20:51 KST 2026 There was an unexpected error (type=Bad Request, status=400).
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-038-2.html)

**Result**: PASS

### API-039 숫자가 아닌 상태 코드

**검사 기준**: HTTP 400 및 상태 보존

**Input**: `POST /masters/update`

```json
{
  "statusData" : "{\"BATCH-SEED\":\"abc\"}"
}
```

**Output**: HTTP 400 / text/html;charset=UTF-8

```text
Whitelabel Error Page This application has no explicit mapping for /error, so you are seeing this as a fallback. Sun Nov 08 19:20:51 KST 2026 There was an unexpected error (type=Bad Request, status=400).
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-039-1.html)

**Result**: PASS

### API-040 여러 작업 변경 사전 검사

**검사 기준**: 없는 작업이 섞이면 HTTP 404 및 정상 작업도 보존

**Input**: `POST /masters/update`

```json
{
  "statusData" : "{\"BATCH-SEED\":\"300\",\"UNKNOWN\":\"300\"}"
}
```

**Output**: HTTP 404 / text/html;charset=UTF-8

```text
Whitelabel Error Page This application has no explicit mapping for /error, so you are seeing this as a fallback. Sun Nov 08 19:20:51 KST 2026 There was an unexpected error (type=Not Found, status=404).
```

[응답 원문](../target/mssql-it/20261108192036321574500/API-040-1.html)

**Result**: PASS

### API-034 모든 공개 경로의 HTTP 실행 확인

**검사 기준**: 등록 업무 경로 5개 모두 실제 호출

**Input**: 격리 테스트 DB 기준 데이터를 적재한 뒤 해당 내부 점검 작업을 직접 실행한다.

**상태 확인**: HTTP 실행 경로=[GET /error/detail, GET /errors, GET /masters, POST /detail, POST /masters/update]

**Result**: PASS

## 적용 범위와 잔여 점검

운영 원본 DDL 및 데이터 이관, 운영 TLS 인증서와 제한 계정, 실제 피크 부하 및 외부 출력과 발송 연계는 이 결과에 포함되지 않는다. 샘플 스키마와 가상 개인정보를 사용했다. 정시 작업은 테스트 코드에서 직접 호출했으며 실제 시각을 기다린 실행은 아니다. 응답 원문은 target 아래에 있어 clean 작업 시 삭제된다.
