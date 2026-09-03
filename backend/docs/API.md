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
| `COMMENT_NOT_FOUND` | 404 | 댓글을 찾을 수 없습니다 |
| `COMMENT_FORBIDDEN` | 403 | 본인이 작성한 댓글만 삭제할 수 있습니다 |
| `INVALID_PARENT_COMMENT` | 400 | 대댓글에는 답글을 달 수 없습니다 |
| `BOARD_NOT_FOUND` | 404 | 게시판을 찾을 수 없습니다 |
| `POST_NOT_FOUND` | 404 | 게시글을 찾을 수 없습니다 |
| `POST_FORBIDDEN` | 403 | 본인이 작성한 게시글만 수정·삭제할 수 있습니다 |

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

## Comment (경기 토론)

`Comment`는 `matchId`·`postId` 두 nullable FK 컬럼 중 정확히 하나만 채워 경기 토론과
게시판(Post) 댓글을 함께 담는다. 별도 스레드 테이블 없이 첫 댓글이 곧 해당 경기의 토론
시작이다(lazy thread). 대댓글은 1단계까지만 허용한다. 게시글 댓글 API는 아래
"커뮤니티 게시판" 섹션 참고.

### 경기 댓글 목록 조회

`GET /api/v1/matches/{matchId}/comments` — 인증 불필요

**Query Parameters**: `page`, `size`, `sort` (Spring `Pageable` 표준 파라미터)

최상위 댓글만 페이지네이션하고, 각 댓글의 대댓글은 `replies` 배열에 함께 담아 반환한다.

**Response** `200 OK` — `Page<CommentResponse>`

```json
{
  "data": {
    "content": [
      {
        "id": 1,
        "matchId": 10,
        "postId": null,
        "parentId": null,
        "authorId": 5,
        "authorNickname": "축구팬",
        "content": "오늘 라인업 어떻게 보세요?",
        "deleted": false,
        "createdAt": "2026-09-02T10:00:00Z",
        "replies": [
          {
            "id": 2,
            "matchId": 10,
            "postId": null,
            "parentId": 1,
            "authorId": 6,
            "authorNickname": "감독",
            "content": "선발이 무난해 보여요",
            "deleted": false,
            "createdAt": "2026-09-02T10:05:00Z",
            "replies": []
          }
        ]
      }
    ],
    "totalElements": 1
  },
  "timestamp": "2026-09-02T10:10:00Z"
}
```

에러: `MATCH_NOT_FOUND`(경기 자체가 없어도 빈 목록을 반환하며 별도 에러는 없음)

### 경기 댓글 작성

`POST /api/v1/matches/{matchId}/comments` — 인증 필요

**Request Body**

| Field | Type | 제약 |
|---|---|---|
| content | string | 필수, 최대 1000자 |
| parentId | number | 선택, 대댓글 작성 시 부모 댓글 id |

**Response** `201 Created` — `CommentResponse`

에러: `MATCH_NOT_FOUND`, `COMMENT_NOT_FOUND`(존재하지 않는 parentId), `INVALID_PARENT_COMMENT`(대댓글에 답글 시도), `INVALID_INPUT`

### 댓글 삭제 (soft delete)

`DELETE /api/v1/comments/{id}` — 인증 필요, 작성자 본인만 가능

삭제된 댓글은 `deleted: true`로 표시되며 `content`·`authorNickname`은 `null`로 마스킹되어 반환된다(레코드는 물리적으로 삭제하지 않음).

**Response** `200 OK`

에러: `COMMENT_NOT_FOUND`, `COMMENT_FORBIDDEN`(본인 댓글이 아님)

### 오늘의 토론장

`GET /api/v1/matches/today/discussions` — 인증 불필요

UTC 기준 오늘 예정/진행/종료된 전 리그 경기 목록에 댓글 수를 붙여 반환한다. 프론트 홈 화면에서
경기일을 부각시키는 데 사용한다.

**Response** `200 OK` — `MatchDiscussionResponse[]`

```json
{
  "data": [
    {
      "match": { "id": 10, "competitionId": "PL", "...": "MatchResponse 동일 구조" },
      "commentCount": 4
    }
  ],
  "timestamp": "2026-09-02T00:00:00Z"
}
```

---

## 커뮤니티 게시판 (Board / Post)

`Board`는 7개(epl, laliga, bundesliga, seriea, ligue1, free, transfer)를 앱 기동 시
`BoardSeeder`(`CommandLineRunner`)가 code 존재 여부를 확인해 idempotent하게 시딩한다.
`Post`는 게시판별 글이며 soft delete·조회수를 지원한다. 게시글 댓글은 위 Comment 섹션의
`Comment` 엔티티를 postId FK로 재사용한다(1단계 대댓글, 작성자 본인만 soft delete 가능 — 동일 정책).

### 게시판 목록 조회

`GET /api/v1/boards` — 인증 불필요

**Response** `200 OK` — `BoardResponse[]`

```json
{
  "data": [
    { "id": 1, "code": "epl", "name": "프리미어리그", "description": "잉글랜드 프리미어리그 게시판" }
  ],
  "timestamp": "2026-09-03T00:00:00Z"
}
```

### 게시판별 글 목록 조회

`GET /api/v1/boards/{boardId}/posts` — 인증 불필요

**Query Parameters**

| Param | Type | 필수 | 설명 |
|---|---|---|---|
| title | string | N | 제목 부분 검색(대소문자 무시) |
| page, size, sort | - | N | Spring `Pageable` 표준 파라미터 |

soft delete된 글은 목록에서 제외된다. 정렬은 최신순 고정(`createdAt desc`).

**Response** `200 OK` — `Page<PostResponse>`

```json
{
  "data": {
    "content": [
      {
        "id": 1,
        "boardId": 1,
        "boardCode": "epl",
        "authorId": 5,
        "authorNickname": "축구팬",
        "title": "이번 시즌 우승은?",
        "content": "본문",
        "viewCount": 12,
        "deleted": false,
        "createdAt": "2026-09-02T10:00:00Z",
        "updatedAt": null
      }
    ],
    "totalElements": 1
  },
  "timestamp": "2026-09-03T00:00:00Z"
}
```

### 게시글 작성

`POST /api/v1/boards/{boardId}/posts` — 인증 필요

**Request Body**

| Field | Type | 제약 |
|---|---|---|
| title | string | 필수, 최대 200자 |
| content | string | 필수, 최대 5000자 |

**Response** `201 Created` — `PostResponse`

에러: `BOARD_NOT_FOUND`, `INVALID_INPUT`

### 게시글 상세 조회

`GET /api/v1/posts/{id}` — 인증 불필요

조회할 때마다 `viewCount`가 1 증가한다. soft delete된 글은 `POST_NOT_FOUND`.

**Response** `200 OK` — `PostResponse`

에러: `POST_NOT_FOUND`

### 게시글 수정

`PUT /api/v1/posts/{id}` — 인증 필요, 작성자 본인만 가능

**Request Body**: 작성과 동일(`title`, `content`)

**Response** `200 OK` — `PostResponse`

에러: `POST_NOT_FOUND`, `POST_FORBIDDEN`, `INVALID_INPUT`

### 게시글 삭제 (soft delete)

`DELETE /api/v1/posts/{id}` — 인증 필요, 작성자 본인만 가능

**Response** `200 OK`

에러: `POST_NOT_FOUND`, `POST_FORBIDDEN`

### 게시글 댓글 목록 조회

`GET /api/v1/posts/{postId}/comments` — 인증 불필요

Match 댓글과 동일하게 최상위 댓글만 페이지네이션하고 대댓글은 `replies`에 담긴다.
응답 구조는 위 Comment 섹션의 `CommentResponse`와 동일(`matchId: null`, `postId`가 채워짐).

**Response** `200 OK` — `Page<CommentResponse>`

### 게시글 댓글 작성

`POST /api/v1/posts/{postId}/comments` — 인증 필요

**Request Body**: Match 댓글과 동일(`content`, `parentId`)

**Response** `201 Created` — `CommentResponse`

에러: `POST_NOT_FOUND`, `COMMENT_NOT_FOUND`(존재하지 않는 parentId), `INVALID_PARENT_COMMENT`, `INVALID_INPUT`

댓글 삭제는 Comment 섹션의 `DELETE /api/v1/comments/{id}`를 공용으로 사용한다.

---

## 미구현 API (설계 예정)

CLAUDE.md 기준 다음 도메인은 아직 컨트롤러가 존재하지 않는다:

- `GET/POST /api/v1/teams/**` — 팀 정보
- `GET /api/v1/leaderboard` — 예측 리더보드
- 승부 예측(Prediction) 관련 API
