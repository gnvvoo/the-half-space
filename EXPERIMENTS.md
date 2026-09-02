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

<!-- 다음 실험 후보: 퓨어 Claude Code vs OMC autopilot으로 게시판 백엔드 구현 비교 -->
