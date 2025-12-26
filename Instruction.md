# 백엔드 개발 가이드 (Instruction)

이 문서는 `project_C_Back`의 상세 기술 스택과 개발 환경에 대한 정보를 담고 있습니다.

## 1. 기술 스택 상세

### Core & Web
- **Spring Boot 3.5.9**: 최신 스프링 부트 프레임워크 사용.
- **Java 25**: 프로젝트 설정(`pom.xml`)에 명시된 자바 버전.
- **Spring WebMVC**: 전통적인 서블릿 기반 웹 애플리케이션 개발.
- **Spring WebFlux**: 리액티브 프로그래밍 지원 (비동기/논블로킹).

### Data Access
- **Spring Data JPA**: JPA를 추상화하여 데이터베이스 접근 용이성 제공.
- **Oracle JDBC (ojdbc11)**: 오라클 데이터베이스 연결 드라이버.

### Security & Auth
- **Spring Security**: 강력한 인증 및 접근 제어 프레임워크.
- **OAuth2 Client**: 소셜 로그인 등 외부 인증 연동.
- **JJWT (0.12.6)**: JSON Web Token 생성 및 검증 라이브러리.

### Real-time Communication
- **WebSocket**: 양방향 통신 프로토콜.
- **SockJS Client**: 웹소켓 미지원 브라우저를 위한 폴백 옵션.
- **STOMP**: 메시지 브로커 프로토콜.

### Documentation
- **SpringDoc OpenAPI (2.8.14)**: API 문서 자동화 (Swagger UI 제공).

### Template Engine
- **Thymeleaf**: 서버 사이드 자바 템플릿 엔진 (Spring Security 연동 포함).

### Utilities
- **Lombok**: 보일러플레이트 코드 감소 (Getter, Setter, Builder 등).
- **Spring Boot DevTools**: 개발 생산성 향상 도구 (자동 재시작 등).

## 2. 개발 환경 설정

1. **JDK 설치**: Java 25 (또는 프로젝트 호환 버전) 설치 필요.
2. **Maven 빌드**:
   ```bash
   ./mvnw clean install
   ```
3. **애플리케이션 실행**:
   ```bash
   ./mvnw spring-boot:run
   ```

## 3. 주요 의존성 (Dependencies)

`pom.xml`에 정의된 주요 라이브러리 목록입니다.

- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-security`
- `spring-boot-starter-websocket`
- `springdoc-openapi-starter-webmvc-ui`
- `jjwt`

## 4. 백엔드 최적화 전략 (리스트/디테일 페이지)

대용량 트래픽과 데이터 전송을 효율적으로 처리하기 위한 전략입니다.

### 1. DTO (Data Transfer Object) 분리
- **ListDTO**: 리스트 조회용. 필수 데이터(ID, 제목, 썸네일, 가격, 평점, 지역)만 포함하여 페이로드 최소화.
- **DetailDTO**: 상세 조회용. 모든 상세 정보(설명, 전체 이미지, 편의시설 등) 포함.

### 2. 이미지 처리
- **외부 저장소 사용**: 이미지는 DB가 아닌 S3/GCS 등 객체 스토리지에 저장.
- **썸네일 활용**: 리스트 페이지에서는 리사이징된 썸네일 이미지를 전송하여 로딩 속도 개선.
- **WebP 포맷**: 이미지 용량을 줄이기 위해 WebP 포맷 권장.

### 3. 페이징 (Pagination)
- **Slice 사용**: 무한 스크롤 구현 시 `Page` 대신 `Slice`를 사용하여 불필요한 Count 쿼리 방지.
- **적절한 Size**: 한 번에 10~20개 정도의 데이터만 로드.

### 4. 쿼리 최적화
- **Projection**: 엔티티 전체가 아닌 필요한 컬럼만 조회.
- **Fetch Join**: N+1 문제 해결을 위해 연관 데이터 조회 시 Fetch Join 사용.