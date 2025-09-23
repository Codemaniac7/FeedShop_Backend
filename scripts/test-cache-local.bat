@echo off
echo 로컬 캐시 테스트 환경 설정

REM 1. Docker 서비스 시작
echo Docker 서비스 시작 중...
docker-compose -f ..\docker-compose.dev.yml up -d

REM 2. 서비스 헬스체크 대기
echo 서비스 준비 대기 중...
timeout /t 10 /nobreak > nul

REM 3. MySQL 연결 테스트
echo MySQL 연결 테스트...
docker exec mysql-dev mysqladmin ping -h localhost -u devuser -pdev123!!
if %errorlevel% neq 0 (
    echo ? MySQL 연결 실패
    exit /b 1
)

REM 4. Redis 연결 테스트
echo Redis 연결 테스트...
docker exec redis-dev redis-cli ping
if %errorlevel% neq 0 (
    echo ? Redis 연결 실패
    exit /b 1
)

echo  모든 서비스가 준비되었습니다!
echo.
echo  다음 명령어로 애플리케이션을 실행하세요:
echo    gradlew bootRun --args="--spring.profiles.active=local"
echo.
echo  테스트 URL:
echo    - 베스트 상품: http://localhost:8080/api/products/best
echo    - 카테고리: http://localhost:8080/api/products/categories
echo    - 캐시 통계: http://localhost:8080/api/admin/cache/stats
echo    - Redis Commander: http://localhost:8081
echo    - 업로드된 파일: http://localhost:8080/local-uploads/
echo.
echo  캐시 테스트 명령어:
echo    curl http://localhost:8080/api/products/best?limit=5
echo    curl http://localhost:8080/api/products/categories
echo    curl http://localhost:8080/api/admin/cache/stats
echo.
echo  테스트 완료 후 정리:
echo    docker-compose -f ..\docker-compose.dev.yml down

pause