@echo off
echo ���� ĳ�� �׽�Ʈ ȯ�� ����

REM 1. Docker ���� ����
echo Docker ���� ���� ��...
docker-compose -f ..\docker-compose.dev.yml up -d

REM 2. ���� �ｺüũ ���
echo ���� �غ� ��� ��...
timeout /t 10 /nobreak > nul

REM 3. MySQL ���� �׽�Ʈ
echo MySQL ���� �׽�Ʈ...
docker exec mysql-dev mysqladmin ping -h localhost -u devuser -pdev123!!
if %errorlevel% neq 0 (
    echo ? MySQL ���� ����
    exit /b 1
)

REM 4. Redis ���� �׽�Ʈ
echo Redis ���� �׽�Ʈ...
docker exec redis-dev redis-cli ping
if %errorlevel% neq 0 (
    echo ? Redis ���� ����
    exit /b 1
)

echo  ��� ���񽺰� �غ�Ǿ����ϴ�!
echo.
echo  ���� ��ɾ�� ���ø����̼��� �����ϼ���:
echo    gradlew bootRun --args="--spring.profiles.active=local"
echo.
echo  �׽�Ʈ URL:
echo    - ����Ʈ ��ǰ: http://localhost:8080/api/products/best
echo    - ī�װ�: http://localhost:8080/api/products/categories
echo    - ĳ�� ���: http://localhost:8080/api/admin/cache/stats
echo    - Redis Commander: http://localhost:8081
echo    - ���ε�� ����: http://localhost:8080/local-uploads/
echo.
echo  ĳ�� �׽�Ʈ ��ɾ�:
echo    curl http://localhost:8080/api/products/best?limit=5
echo    curl http://localhost:8080/api/products/categories
echo    curl http://localhost:8080/api/admin/cache/stats
echo.
echo  �׽�Ʈ �Ϸ� �� ����:
echo    docker-compose -f ..\docker-compose.dev.yml down

pause