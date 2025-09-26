@echo off
chcp 65001 > nul
echo Cache Performance Test Script
echo =============================

REM 1. Cache Clear
echo 1. Cache Clear...
curl.exe -s -X DELETE "http://localhost:8080/api/admin/cache/products" > nul 2>&1
curl.exe -s -X DELETE "http://localhost:8080/api/admin/cache/categories" > nul 2>&1
echo Cache cleared successfully
echo.

REM 2. First Request (Cache Miss)
echo 2. First Request (Cache Miss Expected)...
echo Best Products Query Time:
powershell -Command "Measure-Command { Invoke-WebRequest -Uri 'http://localhost:8080/api/products/best?limit=10' -UseBasicParsing | Out-Null } | Select-Object TotalMilliseconds"
echo.

echo Categories Query Time:
powershell -Command "Measure-Command { Invoke-WebRequest -Uri 'http://localhost:8080/api/products/categories' -UseBasicParsing | Out-Null } | Select-Object TotalMilliseconds"
echo.

REM 3. Second Request (Cache Hit)
echo 3. Second Request (Cache Hit Expected)...
echo Best Products Query Time:
powershell -Command "Measure-Command { Invoke-WebRequest -Uri 'http://localhost:8080/api/products/best?limit=10' -UseBasicParsing | Out-Null } | Select-Object TotalMilliseconds"
echo.

echo Categories Query Time:
powershell -Command "Measure-Command { Invoke-WebRequest -Uri 'http://localhost:8080/api/products/categories' -UseBasicParsing | Out-Null } | Select-Object TotalMilliseconds"
echo.

REM 4. Cache Stats
echo 4. Cache Statistics...
curl.exe -s "http://localhost:8080/api/admin/cache/stats"
echo.
echo.

REM 5. Multiple Requests Test
echo 5. Multiple Requests Performance Test...
echo Best Products 5 consecutive requests:
for /L %%i in (1,1,5) do (
    echo Request %%i:
    powershell -Command "Measure-Command { Invoke-WebRequest -Uri 'http://localhost:8080/api/products/best?limit=5' -UseBasicParsing | Out-Null } | Select-Object TotalMilliseconds"
)

echo.
echo Test Complete! If response times are significantly faster from the second request onwards, cache is working properly.

pause