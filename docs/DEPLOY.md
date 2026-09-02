# 배포 가이드 (Phase 1 — 첫 배포)

4GB RAM VPS 1대에 Docker Compose로 backend + postgres + redis + caddy(리버스 프록시/자동 HTTPS)를
띄우고, 프론트는 Vercel에 배포한다. 아래 각 단계에 **[USER ACTION]** 표시가 있으면 로컬 에이전트가
대신 실행할 수 없고 사람이 직접 해야 하는 작업이다 (콘솔 로그인, 실제 서버 SSH, 결제 등).

## 0. 사전 준비 — 크리덴셜 로테이션 (필수, 선행)

과거 커밋(`backend/src/main/resources/application.yaml`)에 DB 크리덴셜 `admin/1245`가 평문으로
커밋된 이력이 있다. git 히스토리에서 완전히 제거하기는 어려우므로(이미 push됨), **로테이션**으로
대응한다.

- [USER ACTION] 운영 DB 사용자/비밀번호를 새로 발급 (`.env`의 `DB_USERNAME`/`DB_PASSWORD`에만 사용,
  git에는 절대 커밋하지 않음)
- [USER ACTION] `FOOTBALL_DATA_API_KEY`, `GEMINI_API_KEY`, `JWT_SECRET`, OAuth 클라이언트 시크릿 등
  다른 키도 이번 기회에 재발급/재확인 (기존 값이 로컬 `.env`나 다른 곳에 평문으로 남아있었는지 점검)
- 로컬 dev DB(`admin/1245`, `application-dev.yaml`)는 로컬 전용이며 운영과 절대 동일한 값을 쓰지 않는다

## 1. VPS 준비 [USER ACTION]

1. VPS 제공업체에서 4GB RAM / Ubuntu LTS 인스턴스 생성 (예산: 월 1~3만원대)
2. SSH로 접속 후 Docker + Docker Compose 플러그인 설치:
   ```bash
   curl -fsSL https://get.docker.com | sh
   sudo usermod -aG docker $USER   # 배포용으로 비root 사용자를 docker 그룹에 추가 (재로그인 필요)
   ```
3. 방화벽 설정 (ufw 예시) — 22(SSH), 80(HTTP), 443(HTTPS)만 개방:
   ```bash
   sudo ufw allow 22/tcp
   sudo ufw allow 80/tcp
   sudo ufw allow 443/tcp
   sudo ufw enable
   ```
4. (선택, Phase 2 대비) 배포 전용 비root 사용자 생성 + docker 그룹 부여, SSH 배포 키(scope 제한) 등록

## 2. 저장소 배치

```bash
git clone <repo-url> the-half-space
cd the-half-space
cp .env.example .env
```

- `.env`를 열어 `.env.example`에 나열된 모든 값을 채운다 (DB_URL/PASSWORD, JWT_SECRET, OAuth 키,
  FOOTBALL_DATA_API_KEY, GEMINI_API_KEY, BACKEND_IMAGE, DOMAIN 등)
- Phase 1 시점에는 CI/CD(Phase 2)가 아직 없으므로 `BACKEND_IMAGE`는 로컬에서 빌드한 이미지를
  직접 태그하거나, `backend/Dockerfile`로 로컬 빌드 후 그 이미지 이름을 지정한다:
  ```bash
  docker build -t ths-backend:manual ./backend
  # .env의 BACKEND_IMAGE=ths-backend:manual 로 설정
  ```

## 3. Flyway 베이스라인 (1회) — [USER ACTION 포함]

운영 DB에 스키마를 처음 적용하기 전, `backend/src/main/resources/db/migration/README.md`의 절차대로
`V1__baseline.sql`을 생성한다 (dev DB에서 `pg_dump --schema-only`). 이 문서에서 미리 SQL을 작성해두지
않는 이유도 README에 설명되어 있다. 베이스라인 검증(`baseline-on-migrate` 임시 활성화 → 정상 기동
확인 → 비활성화 복귀)까지 완료한 뒤에만 다음 단계로 진행한다.

## 4. 컨테이너 기동

```bash
docker compose -f docker-compose.prod.yml --env-file .env up -d
docker compose -f docker-compose.prod.yml logs -f backend
```

- postgres/redis는 호스트 포트를 열지 않으므로 외부에서 직접 접근 불가 (backend/내부망에서만 접근)
- caddy가 `.env`의 `DOMAIN`으로 Let's Encrypt 인증서를 자동 발급한다. 도메인이 아직 없다면 우선
  IP 기반으로 HTTP만 확인하고, 도메인 연결 후 `infra/caddy/Caddyfile`과 `.env`의 `DOMAIN`을 갱신해
  재기동한다.

## 5. OAuth Redirect URL 등록 [USER ACTION]

배포 주소가 확정되면 Google/Kakao 개발자 콘솔에서 리다이렉트 URL을 등록해야 한다 (로컬 에이전트가
대신 로그인/등록할 수 없음):

- **Google Cloud Console** → OAuth 2.0 클라이언트 → 승인된 리디렉션 URI에
  `https://<도메인>/login/oauth2/code/google` 추가
- **Kakao Developers** → 카카오 로그인 → Redirect URI에
  `https://<도메인>/login/oauth2/code/kakao` 추가
- `.env`의 `OAUTH2_REDIRECT_URI`를 프론트 콜백 주소(`https://<도메인>/oauth2/callback`)로 갱신 후
  backend 재기동

**도메인 변경 시 갱신 절차**: 위 두 콘솔의 Redirect URI를 새 도메인으로 갱신 → `.env`의
`OAUTH2_REDIRECT_URI`/`DOMAIN` 갱신 → `infra/caddy/Caddyfile` 재확인 → 컨테이너 재기동.

## 6. 프론트 Vercel 배포 [USER ACTION]

1. Vercel에 GitHub 저장소 연동 (frontend 디렉토리를 루트로 지정)
2. 환경변수 `NEXT_PUBLIC_API_URL`을 배포된 백엔드 주소(`https://<도메인>`)로 설정
3. 백엔드 CORS 허용 origin에 Vercel 배포 도메인을 반영해야 함 (`backend/.../config/SecurityConfig.java`의
   `corsConfigurationSource()` — 현재 `http://localhost:3000`으로 하드코딩되어 있어 배포 도메인 반영은
   별도 변경 필요. 이 문서의 Phase 1 범위 밖이므로 코드 변경 시 별도 커밋으로 처리할 것)

## 7. 백업 & 복구 리허설 [USER ACTION 포함, Phase 3 시작 전 필수]

정기 백업 (cron 예시, VPS crontab):
```bash
# 매일 03:00 KST에 스키마+데이터 덤프
0 3 * * * docker exec ths-postgres pg_dump -U <DB_USERNAME> <DB_NAME> | gzip > /backups/halfspace-$(date +\%F).sql.gz
```
- [USER ACTION] `/backups` 디렉토리를 VPS 로컬 디스크 외 별도 위치(예: 오브젝트 스토리지, 다른 서버)에도
  주기적으로 옮겨 단일 서버 장애에 대비한다.

복구 리허설 (최소 1회 성공 확인 — Phase 3 시작 전제조건):
```bash
gunzip -c /backups/halfspace-<date>.sql.gz | docker exec -i ths-postgres psql -U <DB_USERNAME> -d <DB_NAME>
```
- 별도 임시 DB(또는 임시 컨테이너)에 복원해서 애플리케이션이 정상 기동하는지 확인하고 결과를
  기록해둔다 (운영 DB에 직접 복원 테스트하지 말 것).

## 8. 검증

- [USER ACTION] 배포된 실제 URL에서 회원가입 → 로그인(이메일 + Google/Kakao) → 경기 조회 → AI
  프리뷰/리뷰 콘텐츠 표시까지 수동으로 확인
- 스케줄러(football-cli 기반 배치)가 다음날 경기 데이터를 자동 갱신하는지 24시간 관찰
  (`docker compose -f docker-compose.prod.yml logs backend`에서 배치 실행 로그 확인)

## 참고: football-agent 바이너리 미결 사항

`backend/Dockerfile`에 TODO로 남겨둔 대로, `app.agent.binary-path`가 가리키는 `football-agent`
Go CLI 바이너리는 아직 이 저장소에 커밋되어 있지 않다 (`backend/cli/football-cli`와 달리
`backend/bin/football-agent` 경로 없음). 이미지 동봉(binary를 리포에 추가) 또는 사이드카(별도
프로세스/컨테이너로 분리) 중 방식을 결정해 `backend/Dockerfile`의 TODO 블록을 채워야 AI 프리뷰/리뷰
자동 생성이 동작한다.
