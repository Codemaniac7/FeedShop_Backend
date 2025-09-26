#!/bin/bash

echo "🧪 캐시 성능 테스트 스크립트"
echo "================================"

BASE_URL="http://localhost:8080"

# 1. 캐시 초기화
echo "1️⃣ 캐시 초기화..."
curl -s -X DELETE "$BASE_URL/api/admin/cache/products" > /dev/null
curl -s -X DELETE "$BASE_URL/api/admin/cache/categories" > /dev/null
echo "✅ 캐시 초기화 완료"
echo ""

# 2. 첫 번째 요청 (캐시 미스 - DB에서 조회)
echo "2️⃣ 첫 번째 요청 (캐시 미스 예상)..."
echo "⏱️ 베스트 상품 조회 시간 측정:"
time curl -s "$BASE_URL/api/products/best?limit=10" > /dev/null
echo ""

echo "⏱️ 카테고리 조회 시간 측정:"
time curl -s "$BASE_URL/api/products/categories" > /dev/null
echo ""

# 3. 두 번째 요청 (캐시 히트 - 캐시에서 조회)
echo "3️⃣ 두 번째 요청 (캐시 히트 예상)..."
echo "⏱️ 베스트 상품 조회 시간 측정:"
time curl -s "$BASE_URL/api/products/best?limit=10" > /dev/null
echo ""

echo "⏱️ 카테고리 조회 시간 측정:"
time curl -s "$BASE_URL/api/products/categories" > /dev/null
echo ""

# 4. 캐시 통계 확인
echo "4️⃣ 캐시 통계 확인..."
curl -s "$BASE_URL/api/admin/cache/stats"
echo ""

# 5. 연속 요청으로 성능 차이 확인
echo "5️⃣ 연속 10회 요청 성능 테스트..."
echo "베스트 상품 10회 연속 요청:"
for i in {1..10}; do
    echo -n "요청 $i: "
    time curl -s "$BASE_URL/api/products/best?limit=5" > /dev/null
done

echo ""
echo "🎯 테스트 완료! 위 결과에서 두 번째 요청부터 응답시간이 현저히 빨라졌다면 캐시가 정상 동작하는 것입니다."