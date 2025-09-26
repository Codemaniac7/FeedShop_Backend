#!/bin/bash

echo "🔴 Redis 캐시 데이터 확인"
echo "========================"

# Redis에 저장된 모든 키 확인
echo "📋 Redis에 저장된 캐시 키 목록:"
docker exec redis-dev redis-cli KEYS "*"
echo ""

# 캐시 키별 데이터 타입 확인
echo "📊 캐시 키별 데이터 정보:"
for key in $(docker exec redis-dev redis-cli KEYS "*"); do
    echo "키: $key"
    echo "  타입: $(docker exec redis-dev redis-cli TYPE $key)"
    echo "  TTL: $(docker exec redis-dev redis-cli TTL $key)초"
    echo "  크기: $(docker exec redis-dev redis-cli MEMORY USAGE $key 2>/dev/null || echo 'N/A') bytes"
    echo ""
done

# Redis 메모리 사용량 확인
echo "💾 Redis 메모리 사용량:"
docker exec redis-dev redis-cli INFO memory | grep used_memory_human

echo ""
echo "🌐 Redis Commander에서 시각적으로 확인: http://localhost:8081"