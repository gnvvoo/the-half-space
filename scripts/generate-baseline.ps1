# dev postgres 컨테이너의 현재 스키마를 pg_dump --schema-only로 덤프하여
# backend/src/main/resources/db/migration/V1__baseline.sql로 저장한다.
#
# 전제 조건:
#   - docker-compose.yml(dev용) 의 postgres 서비스가 실행 중이어야 함 (docker compose up -d postgres)
#   - 애플리케이션을 dev 프로파일로 최소 1회 기동해서 Hibernate ddl-auto: update로 스키마를 만들어둔 상태
#
# 사용법:
#   ./scripts/generate-baseline.ps1
#
# 자세한 절차는 backend/src/main/resources/db/migration/README.md 참고.
$ErrorActionPreference = "Stop"

$RootDir = Split-Path -Parent $PSScriptRoot
$OutFile = Join-Path $RootDir "backend/src/main/resources/db/migration/V1__baseline.sql"
$ContainerName = if ($env:DB_CONTAINER_NAME) { $env:DB_CONTAINER_NAME } else { "ths-postgres" }
$DbName = if ($env:DB_NAME) { $env:DB_NAME } else { "halfspace" }
$DbUsername = if ($env:DB_USERNAME) { $env:DB_USERNAME } else { "admin" }

$running = docker ps --format "{{.Names}}" | Select-String -Pattern "^$ContainerName$"
if (-not $running) {
    Write-Error "postgres 컨테이너($ContainerName)가 실행 중이 아닙니다. 'docker compose up -d postgres' 실행 후 다시 시도하세요."
    exit 1
}

New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutFile) | Out-Null

Write-Host "pg_dump --schema-only 실행 중... (container=$ContainerName, db=$DbName)"
docker exec $ContainerName pg_dump --schema-only --no-owner --no-privileges -U $DbUsername -d $DbName | Out-File -Encoding utf8 $OutFile

Write-Host "생성 완료: $OutFile"
Write-Host "다음 단계: 파일을 검토한 뒤 backend/src/main/resources/db/migration/README.md의 절차를 따르세요."
