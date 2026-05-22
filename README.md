# The Half Space

AI 기반 축구 커뮤니티 플랫폼.
경기 전 AI Preview, 경기 후 AI Review를 자동 생성하고
팬들이 토론하고 승부를 예측하는 공간입니다.

## 기술 스택

- **Backend** Spring Boot 3.5, PostgreSQL, Redis, Spring Batch
- **Frontend** Next.js
- **AI** Google Gemini API
- **Data** football-cli (자체 제작 Go CLI)

## 관련 레포

- [football-cli](https://github.com/gnvvoo/football-cli) 축구 데이터 조회 CLI
- [football-agent](https://github.com/gnvvoo/football-agent) AI 에이전트

## 브랜치 전략

`main`과 `develop` 2개의 중심 브랜치로 운영합니다.

```angular2html
main (실제 운영 서버 배포용)
▲
│ (검증 완료 시 주기적으로 Merge)
│
develop (개발 브랜치 / 평소에 기준이 되는 곳)
▲
├─ feat/analysis (AI 분석 기능 개발 브랜치)
└─ feat/community (커뮤니티 게시판 개발 브랜치)
```

1. 기능을 만들 때는 `develop` 브랜치에서 `feat/기능이름`을 파서 코딩합니다.
2. 기능이 완성되면 `develop` 브랜치로 PR을 날려 합칩니다. (여기서 로컬 테스트 진행)
3. 어느 정도 기능이 모여서 "인터넷에 실제 배포해도 되겠다" 싶을 때, `develop` 코드를 `main`으로 합치고 서버에 배포합니다.

## 커밋 메시지 컨벤션

| 타입 | 설명 |
|---|---|
| `feat` | 새로운 기능 추가 |
| `fix` | 버그 수정 |
| `docs` | 문서 수정 (README 등) |
| `style` | 코드 포맷팅, 세미콜론 누락 등 (비즈니스 로직 변경 없는 경우) |
| `refactor` | 코드 리팩토링 |
| `test` | 테스트 코드 추가 및 수정 |
| `chore` | 빌드 업무 수정, 패키지 매니저 설정 수정 (`build.gradle` 수정 등) |

**작성 예시**
```angular2html
feat: AI Match Preview 자동 생성 기능 추가
fix: 경기 종료 후 Review 트리거 누락 버그 수정
chore: build.gradle 의존성 추가
```

## 개발 현황

🚧 개발 중