# 백엔드 개발 가이드 (Instruction)

이 문서는 `project_C_Back`의 상세 기술 스택과 개발 환경에 대한 정보를 담고 있습니다.

## 1. 기술 스택 상세

### Core & Web
- **Spring Boot 4.0.0**: 최신 스프링 부트 프레임워크 사용.
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
- **SpringDoc OpenAPI (2.8.6)**: API 문서 자동화 (Swagger UI 제공).

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