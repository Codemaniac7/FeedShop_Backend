# nGrinder 성능 테스트 가이드

## 🎯 운영 준비를 위한 성능 테스트

### 1. nGrinder 설치 및 설정

#### Docker로 nGrinder 실행
```bash
# nGrinder Controller 실행
docker run -d --name ngrinder-controller \
  -p 16001:16001 -p 12000-12009:12000-12009 \
  ngrinder/controller:3.5.8

# nGrinder Agent 실행  
docker run -d --name ngrinder-agent \
  ngrinder/agent:3.5.8 controller_ip:controller_ip
```

#### 접속
- URL: http://localhost:16001
- 기본 계정: admin / admin

### 2. 캐시 성능 테스트 시나리오

#### 시나리오 1: 베스트 상품 조회 부하 테스트
```groovy
// nGrinder 스크립트 (Groovy)
@Test
public void testBestProducts() {
    // 캐시 워밍업
    HTTPResponse response = request.GET("http://localhost:8080/api/products/best?limit=20")
    
    if (response.statusCode == 200) {
        grinder.statistics.forLastTest.success = 1
    } else {
        grinder.statistics.forLastTest.success = 0
    }
}
```

#### 시나리오 2: 캐시 무효화 후 성능 테스트
```groovy
@Test  
public void testCacheInvalidation() {
    // 1. 캐시 클리어
    request.DELETE("http://localhost:8080/api/admin/cache/products")
    
    // 2. 첫 번째 요청 (캐시 미스)
    long startTime = System.currentTimeMillis()
    HTTPResponse response1 = request.GET("http://localhost:8080/api/products/best?limit=10")
    long firstRequestTime = System.currentTimeMillis() - startTime
    
    // 3. 두 번째 요청 (캐시 히트)
    startTime = System.currentTimeMillis()
    HTTPResponse response2 = request.GET("http://localhost:8080/api/products/best?limit=10")
    long secondRequestTime = System.currentTimeMillis() - startTime
    
    // 4. 성능 개선 비율 계산
    double improvement = ((double)(firstRequestTime - secondRequestTime) / firstRequestTime) * 100
    grinder.logger.info("성능 개선: {}%", improvement)
}
```

### 3. 테스트 계획

#### Phase 1: 기본 성능 측정
- **목표**: 캐시 없이 기본 성능 측정
- **사용자**: 10명 동시 접속
- **시간**: 5분
- **API**: `/api/products/best`, `/api/products/categories`

#### Phase 2: 캐시 효과 측정  
- **목표**: 캐시 적용 후 성능 개선 확인
- **사용자**: 50명 동시 접속
- **시간**: 10분
- **측정 지표**: 응답시간, TPS, 에러율

#### Phase 3: 고부하 테스트
- **목표**: 시스템 한계 측정
- **사용자**: 100명 → 500명 → 1000명 점진적 증가
- **시간**: 30분
- **모니터링**: CPU, 메모리, DB 커넥션, Redis 메모리

### 4. 예상 결과

#### 캐시 적용 전
- **평균 응답시간**: 200-500ms
- **TPS**: 50-100
- **DB 부하**: 높음

#### 캐시 적용 후
- **평균 응답시간**: 10-50ms (90% 개선)
- **TPS**: 500-1000 (10배 개선)
- **DB 부하**: 낮음

### 5. 모니터링 지표

#### 캐시 관련 지표
- **L1 히트율**: 80% 이상 목표
- **L2 히트율**: 15% 이상 목표  
- **전체 캐시 히트율**: 95% 이상 목표

#### 시스템 지표
- **응답시간**: 95% 요청이 100ms 이내
- **TPS**: 최소 500 이상
- **에러율**: 1% 이하

### 6. 문제 해결 가이드

#### 캐시 히트율이 낮을 때
- 캐시 TTL 조정
- 캐시 크기 증가
- 캐시 키 전략 재검토

#### Redis 연결 문제
- 커넥션 풀 설정 조정
- Redis 메모리 증설
- 네트워크 지연 확인

#### DB 병목 발생 시
- 쿼리 최적화
- 인덱스 추가
- 커넥션 풀 조정

## 🔄 **단계별 접근법**

### 현재 (개발 단계)
- ✅ 웹 대시보드로 기본 검증
- ✅ 스크립트로 빠른 테스트

### 다음 단계 (운영 준비)
- 🔄 nGrinder로 정밀 성능 테스트
- 🔄 실제 트래픽 패턴 시뮬레이션
- 🔄 장애 상황 테스트

### 운영 단계
- 🔄 실시간 모니터링 (Grafana + Prometheus)
- 🔄 알림 시스템 구축
- 🔄 자동 스케일링 설정