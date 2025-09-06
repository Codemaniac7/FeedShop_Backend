# 🖥️ FeedShop 개발 환경 설정 가이드

## 📋 개요

FeedShop 백엔드 프로젝트의 개발 환경 설정 및 개발 가이드입니다. 이 문서를 통해 새로운 개발자도 빠르게 개발 환경을 구축하고 프로젝트에 참여할 수 있습니다.

---

## 🛠️ 개발 환경 요구사항

### 필수 소프트웨어

- **Java**: 17 이상
- **Gradle**: 8.0 이상 (Wrapper 포함)
- **MySQL**: 8.0 이상
- **Git**: 2.30 이상
- **IDE**: IntelliJ IDEA (권장) 또는 Eclipse

### 권장 사양

- **RAM**: 8GB 이상
- **Storage**: 10GB 이상의 여유 공간
- **OS**: Windows 10/11, macOS, Linux

---

## 🖥️ IDE 및 포맷터 설정

### 권장 IDE

- **Backend**: IntelliJ IDEA (Ultimate 또는 Community)
- **Frontend**: VSCode (별도 프로젝트)

### IntelliJ IDEA 설정

#### 1. 프로젝트 가져오기

```
File → Open → FeedShop_Backend 폴더 선택
```

#### 2. Gradle 설정

```
Settings → Build, Execution, Deployment → Gradle
- Gradle JVM: Java 17 선택
- Build and run using: Gradle
- Run tests using: Gradle
```

#### 3. 코드 포맷터 설정

```
Settings → Editor → Code Style → Java
- Tab size: 4
- Indent: 4 spaces
- Continuation indent: 8 spaces
- Line separator: Unix and OS X (\n)
```

#### 4. 자동 저장 및 포맷팅

```
Settings → Tools → Actions on Save
- Reformat code: ✓
- Optimize imports: ✓
- Rearrange code: ✓
```

---

## 💡 API 문서화 (Swagger/OpenAPI)

### Swagger UI 접근 방법

FeedShop 프로젝트에서는 RESTful API의 명세 확인 및 테스트를 위해 **Swagger (OpenAPI)**를 활용합니다.

#### 접근 URL

- **개발 환경**: `https://localhost:8443/swagger-ui.html`
- **운영 환경**: `https://api.feedshop.store/swagger-ui.html`

#### 주요 기능

##### 1. API 명세 확인

- 프로젝트에서 정의된 모든 RESTful API 엔드포인트 목록
- HTTP 메서드, 요청 URL, 파라미터, 요청/응답 데이터 형식
- HTTP 상태 코드 및 에러 응답 형식

##### 2. API 테스트 (Try it out)

- Swagger UI에서 직접 API 요청 테스트
- 프론트엔드 연동 전 API 동작 검증
- 백엔드 개발 중 기능 테스트

##### 3. 인증 설정 (JWT 기반)

```
1. Swagger UI 상단의 "Authorize" 버튼 클릭
2. JWT 토큰을 "Bearer YOUR_TOKEN_VALUE" 형식으로 입력
3. 이후 모든 API 요청에 자동으로 인증 헤더 포함
```

#### API 그룹별 분류

- **사용자 인증**: 회원가입, 로그인, MFA, 소셜 로그인
- **사용자 관리**: 프로필 관리, 포인트, 리워드
- **상품 관리**: 상품 조회, 카테고리, 검색
- **주문 관리**: 장바구니, 주문, 결제
- **리뷰 관리**: 리뷰 작성, 조회, 관리
- **피드 관리**: 피드 작성, 조회, 상호작용

---

## 📂 Backend 디렉토리 구조

### 패키지 구조: `com.cMall.feedShop`

```
com.cMall.feedShop
├── ai                          # AI 서비스 (상품 추천, 챗봇)
│   ├── application            # AI 서비스 로직
│   ├── domain                 # AI 도메인 모델
│   └── presentation           # AI API 컨트롤러
├── annotation                 # 커스텀 어노테이션 및 AOP
├── cart                       # 장바구니 도메인
├── common                     # 공통 모듈
│   ├── aop                   # AOP (로깅, 응답 포맷)
│   ├── captcha               # reCAPTCHA 검증
│   ├── dto                   # 공통 DTO
│   ├── exception             # 전역 예외 처리
│   └── util                  # 유틸리티 클래스
├── config                     # Spring 설정
│   ├── DevSecurityConfig     # 개발 환경 보안 설정
│   ├── ProdSecurityConfig    # 운영 환경 보안 설정
│   └── SwaggerConfig         # Swagger 설정
├── event                      # 이벤트 도메인
│   ├── application           # 이벤트 서비스
│   ├── domain                # 이벤트 도메인 모델
│   └── presentation          # 이벤트 API
├── feed                       # 피드 도메인
├── order                      # 주문 도메인
├── product                    # 상품 도메인
├── review                     # 리뷰 도메인
├── store                      # 스토어 도메인
├── user                       # 사용자 도메인
│   ├── application           # 사용자 서비스
│   │   ├── dto               # 요청/응답 DTO
│   │   └── service           # 서비스 클래스
│   ├── domain                # 도메인 모델
│   │   ├── enums             # 열거형
│   │   ├── exception         # 도메인 예외
│   │   ├── model             # 엔티티
│   │   └── repository        # 리포지토리 인터페이스
│   ├── infrastructure        # 인프라스트럭처
│   │   ├── oauth             # OAuth2 설정
│   │   ├── repository        # JPA 리포지토리 구현
│   │   └── security          # 보안 관련
│   └── presentation          # API 컨트롤러
└── FeedShopApplication.java  # 메인 애플리케이션
```

### 디렉토리 설계 철학

| 폴더             | 역할                                               |
| ---------------- | -------------------------------------------------- |
| `application`    | 서비스/유즈케이스, 트랜잭션, 비즈니스 로직 담당    |
| `domain`         | 도메인 모델, 엔티티, 인터페이스 (port) 등          |
| `infrastructure` | DB 연동, 외부 시스템과의 연결 구현체 (adapter)     |
| `presentation`   | REST API 컨트롤러, 요청/응답 DTO 처리 등           |
| `common`         | 전역 예외 처리, 응답 포맷, 상수, 유틸 등 공통 모듈 |

### 패키징 전략

- **계층형 + 기능 모듈 혼합형**
- 기능별로 루트 디렉토리를 나누되, 도메인 내부는 계층화
- DDD(Domain-Driven Design) 스타일 구조를 일부 반영

---

## 🌿 Git 브랜치 전략

### 전략: GitHub Flow

### 브랜치 명명 규칙

- **기능 개발**: `feat/기능명` (예: `feat/user-signup`)
- **버그 수정**: `fix/버그설명` (예: `fix/login-error`)
- **리팩토링**: `refactor/설명` (예: `refactor/point-service`)
- **문서**: `docs/설명` (예: `docs/api-documentation`)
- **테스트**: `test/설명` (예: `test/user-service-test`)

### PR 규칙

1. 작업 완료 후 `main` 또는 `dev` 브랜치로 Pull Request
2. PR 제목에 관련 이슈 번호 명시: `[#12] 사용자 회원가입 기능 구현`
3. PR 설명에 변경 사항 상세 기술
4. 리뷰어 지정 후 승인 후 머지

### 커밋 메시지 규칙

```
feat: 사용자 회원가입 기능 추가
fix: 로그인 시 MFA 검증 오류 수정
refactor: PointService 리팩토링
docs: API 문서 업데이트
test: UserService 테스트 코드 추가
```

---

## 🔐 보안 파일 관리

### 환경 변수 설정

#### 1. `.env` 파일 생성

프로젝트 루트에 `.env` 파일을 생성하고 다음 내용을 추가:

```env
# Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_NAME=feedshop_dev
DB_USERNAME=feedshop_user
DB_PASSWORD=your_secure_password

# Server Configuration
SERVER_PORT=8443
SSL_ENABLED=true
SSL_KEY_STORE_PASSWORD=your_keystore_password

# JWT Configuration
JWT_SECRET_KEY=your_jwt_secret_key_here_make_it_long_and_secure
JWT_EXPIRATION_TIME=86400000

# External API Keys
OPENAI_API_KEY=your_openai_api_key
MAILGUN_API_KEY=your_mailgun_api_key
RECAPTCHA_SECRET_KEY=your_recaptcha_secret_key

# Cloud Storage
GCP_PROJECT_ID=your_gcp_project_id
GCP_STORAGE_BUCKET=feedshop-storage
GCP_CREDENTIALS_PATH=path/to/service-account-key.json

# CDN Configuration
CDN_BASE_URL=https://dev.cdn-feedshop.store
```

#### 2. `application-dev.properties` 설정

```properties
# Application
spring.application.name=feedShop

# Database
spring.datasource.url=jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.devtools.restart.enabled=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Server
server.port=${SERVER_PORT:8443}
server.ssl.enabled=${SSL_ENABLED:true}
server.ssl.key-store=${SSL_KEY_STORE:classpath:keystore.p12}
server.ssl.key-store-password=${SSL_KEY_STORE_PASSWORD:}
server.ssl.key-store-type=${SSL_KEY_STORE_TYPE:PKCS12}
server.ssl.key-alias=${SSL_KEY_ALIAS:springboot}

# JWT
jwt.secret-key=${JWT_SECRET_KEY}
jwt.expiration-time=${JWT_EXPIRATION_TIME:86400000}

# External APIs
app.openai.api-key=${OPENAI_API_KEY}
app.mailgun.api-key=${MAILGUN_API_KEY}
app.recaptcha.secret-key=${RECAPTCHA_SECRET_KEY}

# Cloud Storage
app.gcp.project-id=${GCP_PROJECT_ID}
app.gcp.storage.bucket=${GCP_STORAGE_BUCKET}
app.gcp.credentials.path=${GCP_CREDENTIALS_PATH}

# CDN
app.cdn.base-url=${CDN_BASE_URL}

# Logging
logging.level.com.cMall.feedShop=DEBUG
logging.file.name=logs/shopping-mall.log
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
```

### 보안 파일 관리 규칙

1. **`.env` 파일은 절대 Git에 커밋하지 않음**
2. **`.gitignore`에 `.env` 추가**
3. **팀원들과 공유할 때는 `.env.example` 파일 사용**
4. **운영 환경의 민감 정보는 별도 보안 저장소 사용**

---

## 🔒 로컬 개발 환경 HTTPS 설정

### 배경 및 필요성

- OAuth 2.0 콜백 URL이 HTTPS를 요구
- 실제 배포 환경과 동일한 조건에서 API 통신 테스트
- 보안 관련 기능 테스트를 위한 HTTPS 환경

### 사용 기술

- **OpenSSL**: 자체 서명(Self-Signed) 인증서 발급
- **Spring Boot SSL 설정**: 인증서 적용

### 인증서 생성 과정

#### 1. OpenSSL 설치 및 개인 키 생성

```bash
# OpenSSL 설치 (Windows의 경우 Git Bash 또는 WSL 사용)
openssl genrsa -out private.key 2048
```

#### 2. 인증서 서명 요청(CSR) 파일 생성

```bash
openssl req -new -key private.key -out certificate.csr
# Common Name: localhost
# Country Name: KR
# State: Seoul
# Organization: FeedShop
```

#### 3. 자체 서명 인증서 발급

```bash
openssl x509 -req -days 365 -in certificate.csr -signkey private.key -out certificate.crt
```

#### 4. PKCS12(.p12) 키스토어 생성

```bash
openssl pkcs12 -export -in certificate.crt -inkey private.key -out keystore.p12 -name springboot -password pass:your_keystore_password
```

#### 5. 키스토어 파일 배치

```
src/main/resources/keystore.p12
```

### Spring Boot SSL 설정

```properties
server.port=${SERVER_PORT:8443}
server.ssl.enabled=${SSL_ENABLED:true}
server.ssl.key-store=${SSL_KEY_STORE:classpath:keystore.p12}
server.ssl.key-store-password=${SSL_KEY_STORE_PASSWORD:}
server.ssl.key-store-type=${SSL_KEY_STORE_TYPE:PKCS12}
server.ssl.key-alias=${SSL_KEY_ALIAS:springboot}
```

### 결과 및 효과

- 로컬 개발 환경에서 `https://localhost:8443`으로 접근 가능
- 자체 서명 인증서이므로 브라우저 경고 발생 (개발 목적으로 무시)
- OAuth 2.0 및 보안 기능 정상 테스트 가능

---

## 🐳 Docker 개발 환경

### docker-compose.yml

```yaml
version: "3.8"

services:
  mysql:
    image: mysql:8.0
    container_name: feedshop-mysql
    ports:
      - "${DB_PORT:-3306}:3306"
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: ${MYSQL_DATABASE}
      MYSQL_USER: ${MYSQL_USER}
      MYSQL_PASSWORD: ${MYSQL_PASSWORD}
    volumes:
      - mysql_data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - feedshop-network
    restart: always
    command: --default-authentication-plugin=mysql_native_password

  redis:
    image: redis:7-alpine
    container_name: feedshop-redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - feedshop-network
    restart: always

volumes:
  mysql_data:
  redis_data:

networks:
  feedshop-network:
    driver: bridge
```

### Docker 실행 명령

```bash
# 개발 환경 시작
docker-compose up -d

# 로그 확인
docker-compose logs -f

# 개발 환경 중지
docker-compose down

# 볼륨까지 삭제 (데이터 초기화)
docker-compose down -v
```

---

## 🚀 애플리케이션 실행

### 1. 환경 설정

```bash
# .env 파일 생성 및 설정
cp .env.example .env
# .env 파일 편집하여 실제 값 입력
```

### 2. 데이터베이스 설정

```bash
# MySQL 실행 (Docker 사용 시)
docker-compose up -d mysql

# 또는 로컬 MySQL 설정
mysql -u root -p
CREATE DATABASE feedshop_dev;
CREATE USER 'feedshop_user'@'%' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON feedshop_dev.* TO 'feedshop_user'@'%';
FLUSH PRIVILEGES;
```

### 3. 애플리케이션 실행

```bash
# Gradle Wrapper 사용
./gradlew bootRun

# 또는 IntelliJ에서 실행
# FeedShopApplication.java 우클릭 → Run
```

### 4. 접속 확인

- **애플리케이션**: `https://localhost:8443`
- **Swagger UI**: `https://localhost:8443/swagger-ui.html`
- **Health Check**: `https://localhost:8443/actuator/health`

---

## 🧪 테스트 실행

### 단위 테스트

```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests UserServiceTest

# 테스트 커버리지 확인
./gradlew jacocoTestReport
```

### 통합 테스트

```bash
# 통합 테스트 실행
./gradlew integrationTest
```

### API 테스트

```bash
# Postman Collection 사용
# 또는 Swagger UI에서 직접 테스트
```

---

## 📊 모니터링 및 로깅

### 로그 확인

```bash
# 애플리케이션 로그
tail -f logs/shopping-mall.log

# Docker 로그 (Docker 사용 시)
docker-compose logs -f
```

### Health Check

```bash
# 애플리케이션 상태 확인
curl https://localhost:8443/actuator/health
```

### 메트릭 확인

```bash
# 애플리케이션 메트릭
curl https://localhost:8443/actuator/metrics
```

---

## 🔧 문제 해결

### 일반적인 문제들

#### 1. 포트 충돌

```bash
# 포트 사용 중인 프로세스 확인
netstat -ano | findstr :8443

# 프로세스 종료
taskkill /PID <process_id> /F
```

#### 2. SSL 인증서 문제

```bash
# 키스토어 재생성
# 위의 인증서 생성 과정 재실행
```

#### 3. 데이터베이스 연결 실패

```bash
# MySQL 서비스 상태 확인
docker-compose ps

# MySQL 로그 확인
docker-compose logs mysql
```

#### 4. 빌드 실패

```bash
# Gradle 캐시 정리
./gradlew clean

# 의존성 새로고침
./gradlew --refresh-dependencies
```

---

## 📚 추가 자료

### 문서

- [API 문서 (Swagger)](https://localhost:8443/swagger-ui.html)
- [PointService 기능 명세서](docs/PointService_기능_명세서.md)
- [RewardService 기능 명세서](docs/RewardService_기능_명세서.md)

### 외부 링크

- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [Spring Security 공식 문서](https://spring.io/projects/spring-security)
- [JPA/Hibernate 공식 문서](https://hibernate.org/orm/)

---

**문서 버전**: 1.0  
**최종 업데이트**: 2025-08-28  
**작성자**: FeedShop 개발팀
