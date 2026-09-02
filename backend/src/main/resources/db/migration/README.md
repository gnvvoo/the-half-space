# Flyway Migrations

이 디렉토리는 운영(prod) 스키마를 관리하는 Flyway 마이그레이션 파일(`V{n}__description.sql`)이 위치할
곳이다. **dev 프로파일에서는 Flyway가 비활성화되어 있다** (`application-dev.yaml: spring.flyway.enabled: false`)
— 아직 마이그레이션이 하나도 없는 상태에서 dev 부팅이 깨지지 않도록 하기 위함이며, dev는 계속
`spring.jpa.hibernate.ddl-auto: update`로 스키마를 관리한다.

## 왜 아직 V1__baseline.sql이 없는가

이 커밋 시점에는 운영 DB가 아직 존재하지 않는다. Flyway 베이스라인은 **실제 운영 배포 직전**,
dev 컨테이너(또는 그와 동일한 스키마를 가진 DB)에서 스키마를 덤프해 생성해야 하며, 이 파일을
지금 미리 손으로 작성(fabricate)하면 실제 엔티티/배치 스키마와 어긋날 위험이 크다. 대신 아래
절차와 헬퍼 스크립트(`scripts/generate-baseline.sh` / `.ps1`)를 여기 남겨둔다.

## 베이스라인 생성 절차 (Phase 1 배포 직전 1회 실행)

1. dev 프로파일로 애플리케이션을 최소 1회 기동해서 Hibernate `ddl-auto: update`로 전체 스키마
   (엔티티 테이블 + `spring.batch.jdbc.initialize-schema: always`로 생성된 `BATCH_*` 테이블 포함)가
   dev postgres 컨테이너에 만들어져 있는지 확인한다.
2. 저장소 루트에서 헬퍼 스크립트를 실행한다:
   ```bash
   ./scripts/generate-baseline.sh
   # 또는 Windows PowerShell:
   ./scripts/generate-baseline.ps1
   ```
   내부적으로 `pg_dump --schema-only`를 dev postgres 컨테이너에 대해 실행하고, 결과를
   `backend/src/main/resources/db/migration/V1__baseline.sql`로 저장한다.
3. 생성된 `V1__baseline.sql`을 검토한다 — 소유자/권한(`OWNER TO`, `GRANT`) 구문 등 환경 종속적인
   줄은 제거하거나 정리한다.
4. `application-prod.yaml`에서 `spring.flyway.baseline-on-migrate` 주석을 1회 해제하고,
   운영 DB에 대해 애플리케이션을 부팅해 Flyway가 `flyway_schema_history`에 V1을 "베이스라인으로
   기록"만 하고 (테이블을 다시 만들지 않고) 정상 기동되는지 확인한다.
5. 정상 기동을 확인한 즉시 `baseline-on-migrate` 줄을 다시 주석 처리(비활성)한다. 이 옵션은
   최초 1회 베이스라인 적용 전용이며 상시 켜두면 안 된다.
6. 이후부터는 `spring.jpa.hibernate.ddl-auto: validate` 상태로 운영이 부팅되며, 스키마 변경은
   반드시 `V2__*.sql`부터 새 마이그레이션 파일로 추가한다.

## Forward-only 원칙

- 모든 `V{n}__*.sql`은 **직전에 배포된 이미지(SHA)와 하위 호환**되어야 한다 — 즉, 새 마이그레이션이
  적용된 DB에서 이전 SHA의 애플리케이션 코드도 최소한 기동/조회는 가능해야 한다 (컬럼 삭제 대신
  deprecate, NOT NULL 추가 시 기본값 지정 등).
- 마이그레이션을 되돌리는 "다운" 스크립트는 작성하지 않는다. 롤백이 필요하면 이전 SHA로
  `docker compose up`하고, 스키마 자체를 되돌려야 하는 경우에만 `pg_dump` 백업을 복원하는 경로를
  사용한다 (`docs/DEPLOY.md`의 백업·복구 절차 참고).
- `V1__baseline.sql`은 절대 수정하지 않는다. 베이스라인 이후 모든 변경은 새 버전 파일로 추가한다.
