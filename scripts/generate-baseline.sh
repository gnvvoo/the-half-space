#!/usr/bin/env bash
# dev postgres 컨테이너의 현재 스키마를 pg_dump --schema-only로 덤프하여
# backend/src/main/resources/db/migration/V1__baseline.sql로 저장한다.
#
# 전제 조건:
#   - docker-compose.yml(dev용) 의 postgres 서비스가 실행 중이어야 함 (docker compose up -d postgres)
#   - 애플리케이션을 dev 프로파일로 최소 1회 기동해서 Hibernate ddl-auto: update로 스키마를 만들어둔 상태
#
# 사용법:
#   ./scripts/generate-baseline.sh
#
# 자세한 절차는 backend/src/main/resources/db/migration/README.md 참고.
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUT_FILE="$ROOT_DIR/backend/src/main/resources/db/migration/V1__baseline.sql"
CONTAINER_NAME="${DB_CONTAINER_NAME:-ths-postgres}"
DB_NAME="${DB_NAME:-halfspace}"
DB_USERNAME="${DB_USERNAME:-admin}"

if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
  echo "postgres 컨테이너(${CONTAINER_NAME})가 실행 중이 아닙니다. 'docker compose up -d postgres' 실행 후 다시 시도하세요." >&2
  exit 1
fi

mkdir -p "$(dirname "$OUT_FILE")"

echo "pg_dump --schema-only 실행 중... (container=${CONTAINER_NAME}, db=${DB_NAME})"
docker exec "$CONTAINER_NAME" pg_dump --schema-only --no-owner --no-privileges -U "$DB_USERNAME" -d "$DB_NAME" > "$OUT_FILE"

echo "생성 완료: $OUT_FILE"
echo "다음 단계: 파일을 검토한 뒤 backend/src/main/resources/db/migration/README.md의 절차를 따르세요."
