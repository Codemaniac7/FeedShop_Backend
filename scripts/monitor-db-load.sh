#!/bin/bash

echo "📊 데이터베이스 부하 모니터링"
echo "============================"

# MySQL 쿼리 로그 활성화 (개발환경에서만)
echo "🗄️ MySQL 쿼리 실행 모니터링 시작..."
echo "   (application-local.properties에서 spring.jpa.show-sql=true 설정 필요)"
echo ""

# Redis 모니터링 시작
echo "🔴 Redis 명령어 모니터링 시작..."
echo "   다른 터미널에서 다음 명령어 실행:"
echo "   docker exec -it redis-dev redis-cli MONITOR"
echo ""

# 캐시 테스트 실행
echo "🧪 캐시 테스트 실행 중..."
echo "1. 캐시 초기화"
curl -s -X DELETE "http://localhost:8080/api/admin/cache/products" > /dev/null

echo "2. 첫 번째 요청 (MySQL 쿼리 발생 예상)"
curl -s "http://localhost:8080/api/products/best?limit=5" > /dev/null

echo "3. 두 번째 요청 (Redis에서 조회 예상)"
curl -s "http://localhost:8080/api/products/best?limit=5" > /dev/null

echo "4. 세 번째 요청 (Caffeine에서 조회 예상)"
curl -s "http://localhost:8080/api/products/best?limit=5" > /dev/null

echo ""
echo "📈 결과 분석:"
echo "- 첫 번째 요청: MySQL 쿼리 로그 + Redis SET 명령어 확인"
echo "- 두 번째 요청: Redis GET 명령어만 확인 (MySQL 쿼리 없음)"
echo "- 세 번째 요청: Redis 명령어도 없음 (Caffeine 캐시에서 조회)"