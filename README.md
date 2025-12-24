# PlanIt Backend 

PlanIt 프로젝트의 백엔드 워크스페이스입니다. Spring Boot를 기반으로 구축되었으며, REST API 제공 및 데이터 처리를 담당합니다.

## 날짜별 작업 기록

| 날짜 | 담당 | 작업 내용 | 관련 파일/링크 | 비고 |
| --- | --- | --- | --- | --- |
| 2025-12-22 | 김관범 | 백엔드 프로젝트 문서 초기화, 설정 파일 수정 및 유틸리티 추가 | [`README.md`](README.md)<br>[`Instruction.md`](Instruction.md)<br>[`src/main/resources/application.properties`](src/main/resources/application.properties)<br>[`.gitignore`](.gitignore) |  |
| 2025-12-23 | 김관범 | 투어 데이터 전체 캐싱 및 페이지네이션 구현<br> - API 키 보안 처리 (secret.properties)<br> - 전체 데이터 로딩 및 캐싱 로직 추가<br> - 페이지네이션 API 응답 구조 변경 (TourPageDTO) | [`src/main/java/cteam/planit/main/services/TourApiService.java`](src/main/java/cteam/planit/main/services/TourApiService.java)<br>[`src/main/java/cteam/planit/main/controller/TourController.java`](src/main/java/cteam/planit/main/controller/TourController.java)<br>[`src/main/java/cteam/planit/main/dto/TourPageDTO.java`](src/main/java/cteam/planit/main/dto/TourPageDTO.java)<br>[`src/main/resources/secret.properties`](src/main/resources/secret.properties) | API 키 분리 및 캐싱 적용 완료 |
| 2025-12-24 | 김관범 | 숙박 가격 조회 API 구현<br> - `/api/tours/{contentId}/price` 엔드포인트 추가<br> - 숙박 타입 필터링 로직 개선<br> - 최저가 계산 로직 구현 | [`src/main/java/cteam/planit/main/controller/TourController.java`](src/main/java/cteam/planit/main/controller/TourController.java)<br>[`src/main/java/cteam/planit/main/services/TourApiService.java`](src/main/java/cteam/planit/main/services/TourApiService.java) | 가격 조회 API 완료 |

### 새 항목 추가 템플릿

복사/붙여넣기용 1줄:

`| YYYY-MM-DD | 이름 | 작업 내용 | 관련 파일/링크 | 비고 |`

입력 규칙:

- 날짜는 `YYYY-MM-DD` 형식으로 작성합니다. (예: `2025-12-19`)
- 담당은 실명(또는 합의된 표기)으로 통일합니다.
- 작업 내용은 한 줄 요약 + 필요 시 `<br>`로 세부 항목을 추가합니다.
- 관련 파일/링크는 가능한 범위에서 1~5개를 [`src/main/java/...`](src/main/java) 형태로 연결합니다.

## 기술 스택 ([`pom.xml`](pom.xml) 기반)

| 구분 | 기술 | 버전 | 비고 |
| --- | --- | --- | --- |
| **Framework** | Spring Boot | 4.0.0 | Core Framework |
| **Language** | Java | 25 | `pom.xml` 기준 |
| **Database** | Oracle | 11g+ | `ojdbc11` |
| **ORM** | Spring Data JPA | - | |
| **Security** | Spring Security | - | OAuth2, JWT 포함 |
| **Template** | Thymeleaf | - | 서버 사이드 렌더링 |
| **Build** | Maven | - | |
| **API Docs** | SpringDoc OpenAPI | 2.8.6 | Swagger UI |
| **Real-time** | WebSocket | - | SockJS, STOMP |
| **Auth** | JJWT | 0.12.6 | JWT Token |

## 폴더 구조

```
project_C_Back/
├── src/
│   ├── main/
│   │   ├── java/       # Java 소스 코드 (com.example.demo 등)
│   │   └── resources/  # 설정 파일 (application.properties/yml), 템플릿, 정적 리소스
│   └── test/           # 테스트 코드
├── pom.xml             # Maven 프로젝트 설정 및 의존성 관리
├── README.md           # 프로젝트 문서
└── Instruction.md      # 상세 개발 가이드 및 기술 스택
```

## 주요 기능

- **REST API**: 프론트엔드와의 통신을 위한 API 제공
- **인증/인가**: Spring Security 및 JWT, OAuth2 Client를 이용한 사용자 인증
- **실시간 통신**: WebSocket (SockJS, STOMP) 지원
- **API 문서화**: SpringDoc OpenAPI (Swagger UI)