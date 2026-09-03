# EXPERIMENTS

하네스/워크플로우 비교 실험 기록. 정밀 벤치마크가 아니라 "무엇이 가치 있었고 무엇이 오버헤드였는지"의 서술.
항목당: 작업 내용 / 조건(도구·설정·프롬프트) / 개입 횟수 / 완료조건 충족 여부 / 결론 한 줄.

---

## [2026-09-02] OMC deep-interview — 요구사항 크리스털라이즈
- 작업: 막연한 "배포까지 해보고 싶다"를 실행 가능한 스펙으로 변환
- 조건: oh-my-claudecode deep-interview 스킬, 모호도 임계값 20%(기본값), 1질문/라운드
- 개입 횟수: 11라운드 (사용자 답변 11회, 재질문 1회 — 배포 확정 질문에서 답변 전달 오류)
- 완료조건 충족: 모호도 100% → ~13%, 스펙 산출(.omc/specs/deep-interview-halfspace-launch.md)
- 결론: Contrarian/Simplifier 모드가 실제로 범위를 줄임(AI 분석 신규 개발 → 기존 기능 연결). 다만 AskUserQuestion UI에서 직전 답변 텍스트가 안 보여 같은 질문을 두 번 한 마찰 있었음.

## [2026-09-03] OMC team — 커뮤니티 웨이브 병렬 실행
- 작업: Phase 1~4 + 프론트 연동 (인프라 2워커 병렬 → 백엔드 순차 2워커 → 프론트 1워커 → auth fix 1워커 → verifier)
- 조건: oh-my-claudecode team 스킬, 파일 소유권 기반 충돌 방지(설정 파일은 worker-1 전담, .github은 worker-2 전담), 작업 단위별 커밋 규칙
- 개입 횟수: 사용자 개입 3회(커밋 규칙, 브랜치 전략, Co-Authored 제거) — 전부 프로세스 지시였고 구현 개입은 0회
- 완료조건 충족: verifier PASS (백엔드 32테스트 중 신규 전부 통과, 프론트 빌드 클린, 서버측 권한·soft delete·시더 멱등성 코드 검사 통과)
- 결론: 웨이브 간 순차 + 웨이브 내 병렬 구조가 build.gradle/yaml 충돌을 실제로 방지함. verifier가 워커 보고를 재검증해 DTO 마스킹 의존 등 2건의 저위험 갭을 추가로 발견 — 검증 단계의 가치 확인.

<!-- 다음 실험 후보: 퓨어 Claude Code vs OMC autopilot으로 게시판 백엔드 구현 비교 -->
