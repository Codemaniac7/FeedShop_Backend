@echo off
chcp 65001 > nul
echo Tiered Cache Test Script
echo ========================

echo 1. Clear all caches...
curl.exe -s -X DELETE "http://localhost:8080/api/admin/cache/products" > nul 2>&1
curl.exe -s -X DELETE "http://localhost:8080/api/admin/cache/categories" > nul 2>&1

echo 2. First request (DB -> L2 -> L1)...
curl.exe -s "http://localhost:8080/api/products/best?limit=5" > nul 2>&1

echo 3. Second request (L1 hit)...
curl.exe -s "http://localhost:8080/api/products/best?limit=5" > nul 2>&1

echo 4. Check cache stats...
curl.exe -s "http://localhost:8080/api/admin/cache/stats" | findstr "bestProducts"

echo.
echo 5. Check Redis data...
docker exec redis-dev redis-cli KEYS "*bestProducts*"

echo.
echo Test complete! Check the web dashboard: http://localhost:8080/cache-monitor.html

pause