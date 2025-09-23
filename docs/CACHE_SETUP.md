# 캐시 시스템 설정 가이드

## 🚀 로컬에서 완전 독립 캐시 테스트하기

### 🎯 Quick Start (클라우드 DB 없이 로컬 완전 독립)

```bash
# Windows
scripts\test-cache-local.bat

# Linux/Mac
chmod +x scripts/test-cache-local.sh
./scripts/test-cache-local.sh

# 애플리케이션 실행
./gradlew bootRun --args='--spring.profiles.active=local'
```

### 1. 로컬 완전 독립 환경 (추천)

클라우드 DB가 죽어있어도 테스트 가능한 완전 독립 환경:

```bash
# 1단계: 로컬 MySQL + Redis 실행
docker-compose -f docker-compose.dev.yml up -d

# 2단계: 로컬 프로파일로 애플리케이션 실행
./gradlew bootRun --args='--spring.profiles.active=local'
```

**포함 서비스:**
- MySQL (포트 3308)
- Redis (포트 6379)  
- Redis Commander (포트 8081)

### 2. 기존 개발환경에서 Redis 추가 테스트

기존 클라우드 DB + Redis 조합:

```bash
# 1단계: Redis만 실행
docker run -d --name redis-test -p 6379:6379 redis:7-alpine

# 2단계: Redis 활성화하여 실행
REDIS_ENABLED=true ./gradlew bootRun --args='--spring.profiles.active=dev'
```

### 4. 캐시 동작 확인

#### 4.1 캐시 통계 확인
```bash
curl http://localhost:8443/api/admin/cache/stats
```

#### 4.2 베스트 상품 조회 (캐시 생성)
```bash
curl http://localhost:8443/api/products/best?limit=10
```

#### 4.3 캐시 무효화 테스트
```bash
curl -X DELETE http://localhost:8443/api/admin/cache/products
```

### 5. Redis Commander로 캐시 데이터 확인

Redis Commander가 자동으로 실행됩니다:
- URL: http://localhost:8081
- Redis 서버: local (redis-dev:6379)

## 🔧 환경별 캐시 설정

### 개발환경 (application-dev.properties)
```properties
# Caffeine Cache (L1)
spring.cache.caffeine.maximum-size=500
spring.cache.caffeine.expire-after-write=15
spring.cache.caffeine.record-stats=true

# Redis Cache (L2) - 선택적 활성화
spring.cache.redis.enabled=${REDIS_ENABLED:false}
spring.cache.redis.time-to-live=30
```

### 테스트환경 (application-test.properties)
```properties
# Caffeine Cache (L1) - 테스트용 최소값
spring.cache.caffeine.maximum-size=100
spring.cache.caffeine.expire-after-write=5
spring.cache.caffeine.record-stats=true

# Redis Cache (L2) - 비활성화
spring.cache.redis.enabled=false
```

### 운영환경 (application.properties)
```properties
# Caffeine Cache (L1)
spring.cache.caffeine.maximum-size=${CAFFEINE_MAX_SIZE:1000}
spring.cache.caffeine.expire-after-write=${CAFFEINE_EXPIRE_MINUTES:30}
spring.cache.caffeine.record-stats=${CAFFEINE_RECORD_STATS:true}

# Redis Cache (L2)
spring.cache.redis.enabled=${REDIS_ENABLED:false}
spring.cache.redis.time-to-live=${REDIS_TTL_MINUTES:60}
```

## 📊 캐시 성능 모니터링

### 캐시 히트율 확인
```bash
# 캐시 통계 로그 출력
curl http://localhost:8443/api/admin/cache/stats
```

### 예상 로그 출력
```
=== 캐시 통계 정보 ===
캐시 이름: categories
  - 요청 수: 150
  - 히트 수: 145
  - 미스 수: 5
  - 히트율: 96.67%
  - 평균 로드 시간: 12.34ms
  - 캐시 크기: 8

캐시 이름: bestProducts
  - 요청 수: 89
  - 히트 수: 76
  - 미스 수: 13
  - 히트율: 85.39%
  - 평균 로드 시간: 45.67ms
  - 캐시 크기: 12
```

## 🛠 트러블슈팅

### Redis 연결 실패 시
1. Docker 컨테이너 상태 확인: `docker ps`
2. Redis 로그 확인: `docker logs redis-dev`
3. 포트 충돌 확인: `netstat -an | grep 6379`

### 캐시가 동작하지 않을 때
1. `@EnableCaching` 어노테이션 확인
2. 캐시 매니저 빈 등록 확인
3. 메서드에 `@Cacheable` 어노테이션 확인
4. 프록시 호출 여부 확인 (같은 클래스 내부 호출은 캐시 안됨)

## 🔄 캐시 무효화 전략

### 자동 무효화 (AOP 기반)
- 상품 생성/수정/삭제 시 자동으로 관련 캐시 무효화
- `ProductCacheService`에서 처리

### 수동 무효화 (관리자용)
```bash
# 모든 상품 캐시 무효화
curl -X DELETE http://localhost:8443/api/admin/cache/products

# 카테고리 캐시 무효화
curl -X DELETE http://localhost:8443/api/admin/cache/categories

# 특정 카테고리 베스트 상품 캐시 무효화
curl -X DELETE http://localhost:8443/api/admin/cache/products/best/category/1
```