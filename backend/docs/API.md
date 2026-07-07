# The Half Space API 명세서

Base URL: `/api/v1`

## 공통 사항

### 성공 응답 포맷

```json
{
  "data": { },
  "timestamp": "2026-07-03T12:00:00Z"
}
```

### 에러 응답 포맷

```json
{
  "code": "MATCH_NOT_FOUND",
  "message": "경기를 찾을 수 없습니다",
  "timestamp": "2026-07-03T12:00:00Z"
}
```

### 에러 코드

| Code | HTTP Status | Message |
|---|---|---|
| `NOT_FOUND` | 404 | 리소스를 찾을 수 없습니다 |
| `INVALID_INPUT` | 400 | 잘못된 입력입니다 |
| `INTERNAL_ERROR` | 500 | 서버 오류가 발생했습니다 |
| `MATCH_NOT_FOUND` | 404 | 경기를 찾을 수 없습니다 |
| `STANDING_NOT_FOUND` | 404 | 순위 정보를 찾을 수 없습니다 |
| `AUTHENTICATION_REQUIRED` | 401 | 인증이 필요합니다 |
| `FORBIDDEN` | 403 | 접근 권한이 없습니다 |
| `EMAIL_ALREADY_EXISTS` | 409 | 이미 사용 중인 이메일입니다 |
| `NICKNAME_ALREADY_EXISTS` | 409 | 이미 사용 중인 닉네임입니다 |
| `INVALID_CREDENTIALS` | 401 | 이메일 또는 비밀번호가 올바르지 않습니다 |
| `INVALID_TOKEN` | 401 | 유효하지 않은 토큰입니다 |
| `PREVIEW_NOT_READY` | 404 | AI 프리뷰가 아직 생성되지 않았습니다 |
| `REVIEW_NOT_READY` | 404 | AI 리뷰가 아직 생성되지 않았습니다 |
| `AGENT_EXECUTION_FAILED` | 503 | AI 에이전트 실행에 실패했습니다 |
| `AGENT_ALREADY_RUNNING` | 409 | 이미 실행 중인 AI 에이전트가 있습니다 |

### 인증

인증이 필요한 API는 `Authorization: Bearer {accessToken}` 헤더를 전달한다. Access Token은 발급 후 1시간, Refresh Token은 14일간 유효하며 Refresh Token은 Redis에 저장되어 로그아웃 시 즉시 무효화된다.

---

## Auth

### 회원가입

`POST /api/v1/auth/register` — 인증 불필요

**Request Body**

| Field | Type | 제약 |
|---|---|---|
| email | string | 필수, 이메일 형식 |
| password | string | 필수, 8~100자 |
| nickname | string | 필수, 2~50자 |

**Response** `201 Created`

```json
{
  "data": {
    "accessToken": "string",
    "refreshToken": "string",
    "expiresIn": 3600
  },
  "timestamp": "2026-07-03T12:00:00Z"
}
```

에러: `EMAIL_ALREADY_EXISTS`, `NICKNAME_ALREADY_EXISTS`, `INVALID_INPUT`

---

### 로그인

`POST /api/v1/auth/login` — 인증 불필요

**Request Body**

| Field | Type | 제약 |
|---|---|---|
| email | string | 필수, 이메일 형식 |
| password | string | 필수 |

**Response** `200 OK` — `AuthResponse` (회원가입과 동일 구조)

에러: `INVALID_CREDENTIALS`

---

### Access Token 재발급

`POST /api/v1/auth/refresh` — 인증 불필요

**Request Body**

| Field | Type | 제약 |
|---|---|---|
| refreshToken | string | 필수 |

**Response** `200 OK` — `AuthResponse`

에러: `INVALID_TOKEN`

---

### 로그아웃

`POST /api/v1/auth/logout` — **인증 필요**

Redis에 저장된 리프레시 토큰을 삭제한다.

**Response** `200 OK`

```json
{
  "data": { "message": "로그아웃 성공" },
  "timestamp": "2026-07-03T12:00:00Z"
}
```

---

### 소셜 로그인 (OAuth2)

Google/Kakao 로그인은 Spring Security OAuth2 플로우로 처리되며 별도 REST 엔드포인트가 아니다.

- 로그인 시작: `GET /oauth2/authorization/{registrationId}` (`google` | `kakao`)
- 로그인 성공 시 `OAUTH2_REDIRECT_URI` (기본값 `http://localhost:3000/oauth2/callback`)로 리다이렉트되며 쿼리 파라미터로 `accessToken`, `refreshToken`이 함께 전달된다.

---

## Match

### 경기 목록 조회

`GET /api/v1/matches` — 인증 불필요

**Query Parameters**

| Param | Type | 필수 | 설명 |
|---|---|---|---|
| competition | string | Y | 리그 코드 (`PL`, `PD`, `BL1`, `SA`, `FL1`) |
| from | string (`yyyy-MM-dd`) | Y | 조회 시작일 |
| to | string (`yyyy-MM-dd`) | Y | 조회 종료일 |

**Response** `200 OK` — `MatchResponse[]`

```json
{
  "data": [
    {
      "id": 1,
      "competitionId": "PL",
      "season": "2025-26",
      "matchDay": 10,
      "status": "SCHEDULED",
      "utcDate": "2026-07-10T15:00:00Z",
      "homeTeam": { "id": 1, "name": "Arsenal FC", "shortName": "Arsenal", "tla": "ARS", "crestUrl": "https://..." },
      "awayTeam": { "id": 2, "name": "Chelsea FC", "shortName": "Chelsea", "tla": "CHE", "crestUrl": "https://..." },
      "homeScore": null,
      "awayScore": null,
      "winner": null,
      "venue": "Emirates Stadium"
    }
  ],
  "timestamp": "2026-07-03T12:00:00Z"
}
```

---

### 단일 경기 조회

`GET /api/v1/matches/{id}` — 인증 불필요

**Response** `200 OK` — `MatchResponse`

에러: `MATCH_NOT_FOUND`

---

## AI 콘텐츠 (경기 상세)

경기 전 Preview / 경기 후 Review는 배치 Job(`PreviewGenerationJobConfig`, `ReviewGenerationJobConfig`)이 football-agent(Gemini)를 통해 생성한다.

### AI 프리뷰 조회

`GET /api/v1/matches/{matchId}/preview` — 인증 불필요

**Response** `200 OK` — `AiContentResponse`

```json
{
  "data": {
    "id": "uuid",
    "matchId": 1,
    "type": "PREVIEW",
    "body": "string",
    "summary": "string",
    "isPublished": true,
    "createdAt": "2026-07-03T12:00:00Z"
  },
  "timestamp": "2026-07-03T12:00:00Z"
}
```

에러: `MATCH_NOT_FOUND`, `PREVIEW_NOT_READY`

---

### AI 리뷰 조회

`GET /api/v1/matches/{matchId}/review` — 인증 불필요

**Response** `200 OK` — `AiContentResponse` (`type: "REVIEW"`)

에러: `MATCH_NOT_FOUND`, `REVIEW_NOT_READY`

---

### AI 승부 예측 조회

`GET /api/v1/matches/{matchId}/prediction` — 인증 불필요

**Response** `200 OK`

```json
{
  "data": {
    "matchId": 1,
    "homeWinPct": 45.5,
    "drawPct": 25.0,
    "awayWinPct": 29.5,
    "predictedScore": "2-1",
    "reasoning": { }
  },
  "timestamp": "2026-07-03T12:00:00Z"
}
```

에러: `MATCH_NOT_FOUND`

---

### 경기 통계(AI 예측 + 팬 투표 분포) 조회

`GET /api/v1/matches/{matchId}/stats` — 인증 불필요

**Response** `200 OK`

```json
{
  "data": {
    "aiPrediction": {
      "matchId": 1,
      "homeWinPct": 45.5,
      "drawPct": 25.0,
      "awayWinPct": 29.5,
      "predictedScore": "2-1",
      "reasoning": { }
    },
    "fanDistribution": { "home": 120, "draw": 45, "away": 80 }
  },
  "timestamp": "2026-07-03T12:00:00Z"
}
```

에러: `MATCH_NOT_FOUND`

---

## Standing

### 리그 순위 조회

`GET /api/v1/standings` — 인증 불필요

**Query Parameters**

| Param | Type | 필수 | 설명 |
|---|---|---|---|
| competition | string | Y | 리그 코드 (`PL`, `PD`, `BL1`, `SA`, `FL1`) |

현재 시즌은 `SeasonUtils`가 호출 시점 기준으로 자동 계산한다.

**Response** `200 OK` — `StandingResponse[]`

```json
{
  "data": [
    {
      "position": 1,
      "team": { "id": 1, "name": "Arsenal FC", "shortName": "Arsenal", "tla": "ARS", "crestUrl": "https://..." },
      "played": 30,
      "won": 20,
      "draw": 6,
      "lost": 4,
      "goalsFor": 60,
      "goalsAgainst": 25,
      "goalDiff": 35,
      "points": 66
    }
  ],
  "timestamp": "2026-07-03T12:00:00Z"
}
```

에러: `STANDING_NOT_FOUND`

---

## 미구현 API (설계 예정)

CLAUDE.md 기준 다음 도메인은 아직 컨트롤러가 존재하지 않는다:

- `GET/POST /api/v1/teams/**` — 팀 정보
- `GET /api/v1/leaderboard` — 예측 리더보드
- `GET/POST /api/v1/posts/**` — 게시판(Post/Comment)
- 승부 예측(Prediction) 관련 API
