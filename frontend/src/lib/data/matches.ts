import type { FormResult, Match } from "../types";
import { getTeam } from "./teams";

function genericPreview(homeName: string, awayName: string): string[] {
  return [
    `${homeName}와(과) ${awayName}의 이번 경기는 최근 흐름을 감안할 때 중원 싸움이 승부를 가를 전망이다. 양 팀 모두 최근 5경기에서 점유율보다 전환 속도에 초점을 맞춘 축구를 보여주고 있어, 세컨드볼 장악력이 중요한 변수가 될 것으로 보인다.`,
    `AI 모델은 최근 6경기 기대득점(xG)과 압박 성공률, 상대 전적 데이터를 종합해 이번 매치업의 균형이 근소하게 기울어 있다고 평가한다. 다만 부상 변수나 로테이션에 따라 전망은 경기 전까지 조정될 수 있다.`,
  ];
}

function genericReview(homeName: string, awayName: string): string[] {
  return [
    `경기는 예상대로 중원에서의 밀도 높은 공방전으로 시작됐다. ${homeName}는 초반부터 라인을 끌어올리며 주도권을 가져가려 했고, ${awayName}는 낮은 블록을 유지하며 역습 기회를 엿봤다.`,
    `데이터로 돌아본 이번 경기는 예측 모델이 제시했던 흐름과 크게 다르지 않았다. 다음 라운드를 앞두고 두 팀 모두 세부 전술 조정이 필요해 보인다.`,
  ];
}

function defaultForm(seed: number): FormResult[] {
  const pool: FormResult[] = ["W", "D", "L"];
  return Array.from({ length: 5 }, (_, i) => pool[(seed + i * 2) % 3]);
}

const MATCHES: Match[] = [
  // Featured LIVE match — Premier League
  {
    id: "ars-mci",
    league: "EPL",
    status: "LIVE",
    minute: 67,
    matchweek: 34,
    venue: "Emirates Stadium",
    homeTeam: getTeam("ars"),
    awayTeam: getTeam("mci"),
    homeScore: 2,
    awayScore: 1,
    aiPrediction: { home: 52, draw: 23, away: 25 },
    fanPrediction: { home: 61, draw: 14, away: 25, totalVotes: 4128 },
    recentForm: {
      home: ["W", "W", "D", "W", "L"],
      away: ["W", "W", "W", "D", "W"],
    },
    headToHead: "최근 5경기 아스날 2승 2무 1패 — 홈에서는 최근 3경기 무패.",
    aiPreviewTitle: "아스날의 하프스페이스, 시티의 미드블록을 갈라놓을 것인가",
    aiPreviewBody: [
      "아스날은 이번 시즌 하프스페이스를 활용한 3인 조합 침투로 리그 최다 기대득점(xG) 상위권을 기록하고 있다. 사카가 안쪽으로 좁혀 들어오며 만드는 오버로드는 맨시티의 미드블록이 가장 까다로워하는 패턴 중 하나로 꼽힌다.",
      "반면 맨시티는 최근 5경기 연속 실점 없이 막아낸 두 번의 클린시트를 포함해 안정적인 수비 밸런스를 되찾은 모습이다. 과르디올라 특유의 비대칭 빌드업이 에미레이츠에서도 통할지가 이번 경기의 핵심 변수다.",
      "AI 모델은 두 팀의 최근 6경기 압박 성공률과 전환 속도를 종합해 근소하게 아스날 쪽으로 기운 확률을 제시했지만, 원정 팀의 개인 기량 변수는 여전히 예측 모델이 완전히 담아내지 못하는 영역이다.",
    ],
    aiPreviewQuote:
      "“사카가 왜 계속 인사이드로 좁혀 들어오는지 이해하면, 아스날의 공격 설계 전체가 보인다.”",
    aiReviewTitle: "하프스페이스는 열렸지만, 승부는 아직 끝나지 않았다",
    aiReviewBody: [
      "전반 초반부터 아스날은 예고했던 대로 사카와 오데가드가 번갈아 하프스페이스로 좁혀 들어오며 시티의 미드블록에 균열을 만들었다. 선제골은 바로 그 패턴에서 나왔다.",
      "맨시티는 후반 들어 홀란드를 활용한 직선적인 공격으로 전환하며 추격골을 만들어냈지만, 아스날의 후방 커버 셰이프가 추가 실점을 허용하지 않았다.",
      "67분 현재 스코어는 2-1. 남은 시간 동안 시티가 소유권을 얼마나 되찾아오는지가 승부의 마지막 변수로 남아 있다.",
    ],
    aiReviewQuote: "“67분, 경기는 여전히 하프스페이스 안에서 결정되고 있다.”",
  },
  {
    id: "liv-che",
    league: "EPL",
    status: "SCHEDULED",
    kickoffTime: "18:30",
    matchweek: 34,
    venue: "Anfield",
    homeTeam: getTeam("liv"),
    awayTeam: getTeam("che"),
    homeScore: null,
    awayScore: null,
    aiPrediction: { home: 48, draw: 26, away: 26 },
    fanPrediction: { home: 0, draw: 0, away: 0, totalVotes: 0 },
    recentForm: { home: defaultForm(1), away: defaultForm(2) },
    headToHead: "최근 5경기 리버풀 3승 1무 1패.",
    aiPreviewTitle: "안필드에서 첼시의 역습을 시험하는 리버풀의 하이라인",
    aiPreviewBody: genericPreview("리버풀", "첼시"),
    aiReviewTitle: "경기 종료 후 업데이트 예정",
    aiReviewBody: ["경기 종료 후 AI Review가 자동으로 생성됩니다."],
  },
  {
    id: "tot-mun",
    league: "EPL",
    status: "FINISHED",
    matchweek: 34,
    venue: "Tottenham Hotspur Stadium",
    homeTeam: getTeam("tot"),
    awayTeam: getTeam("mun"),
    homeScore: 0,
    awayScore: 0,
    aiPrediction: { home: 40, draw: 32, away: 28 },
    fanPrediction: { home: 38, draw: 34, away: 28, totalVotes: 2210 },
    recentForm: { home: defaultForm(0), away: defaultForm(1) },
    headToHead: "최근 5경기 2승 2무 1패로 균형.",
    aiPreviewTitle: "골 없는 승부를 예고했던 두 팀의 수비 조직력",
    aiPreviewBody: genericPreview("토트넘", "맨유"),
    aiReviewTitle: "예측대로 흘러간 0-0, 양 팀 모두 결정력이 아쉬웠다",
    aiReviewBody: genericReview("토트넘", "맨유"),
  },

  // La Liga
  {
    id: "rma-bar",
    league: "LALIGA",
    status: "SCHEDULED",
    kickoffTime: "21:00",
    matchweek: 30,
    venue: "Santiago Bernabeu",
    homeTeam: getTeam("rma"),
    awayTeam: getTeam("bar"),
    homeScore: null,
    awayScore: null,
    aiPrediction: { home: 41, draw: 24, away: 35 },
    fanPrediction: { home: 0, draw: 0, away: 0, totalVotes: 0 },
    recentForm: { home: defaultForm(2), away: defaultForm(0) },
    headToHead: "최근 5경기 레알 마드리드 2승, 바르셀로나 2승, 1무.",
    aiPreviewTitle: "엘 클라시코, 이번엔 미드필드 숫자 싸움이 변수",
    aiPreviewBody: genericPreview("레알 마드리드", "바르셀로나"),
    aiReviewTitle: "경기 종료 후 업데이트 예정",
    aiReviewBody: ["경기 종료 후 AI Review가 자동으로 생성됩니다."],
  },
  {
    id: "atm-rso",
    league: "LALIGA",
    status: "SCHEDULED",
    kickoffTime: "23:00",
    matchweek: 30,
    venue: "Civitas Metropolitano",
    homeTeam: getTeam("atm"),
    awayTeam: getTeam("rso"),
    homeScore: null,
    awayScore: null,
    aiPrediction: { home: 55, draw: 24, away: 21 },
    fanPrediction: { home: 0, draw: 0, away: 0, totalVotes: 0 },
    recentForm: { home: defaultForm(1), away: defaultForm(2) },
    headToHead: "최근 5경기 아틀레티코 4승 1패.",
    aiPreviewTitle: "아틀레티코의 세트피스 vs 소시에다드의 빌드업",
    aiPreviewBody: genericPreview("아틀레티코 마드리드", "레알 소시에다드"),
    aiReviewTitle: "경기 종료 후 업데이트 예정",
    aiReviewBody: ["경기 종료 후 AI Review가 자동으로 생성됩니다."],
  },

  // Bundesliga
  {
    id: "bay-bvb",
    league: "BUNDESLIGA",
    status: "SCHEDULED",
    kickoffTime: "20:30",
    matchweek: 28,
    venue: "Allianz Arena",
    homeTeam: getTeam("bay"),
    awayTeam: getTeam("bvb"),
    homeScore: null,
    awayScore: null,
    aiPrediction: { home: 58, draw: 22, away: 20 },
    fanPrediction: { home: 0, draw: 0, away: 0, totalVotes: 0 },
    recentForm: { home: defaultForm(0), away: defaultForm(1) },
    headToHead: "최근 5경기 바이에른 4승 1무, 도르트문트 무승.",
    aiPreviewTitle: "데어 클라시커, 바이에른의 압박 라인이 관건",
    aiPreviewBody: genericPreview("바이에른 뮌헨", "도르트문트"),
    aiReviewTitle: "경기 종료 후 업데이트 예정",
    aiReviewBody: ["경기 종료 후 AI Review가 자동으로 생성됩니다."],
  },
  {
    id: "b04-rbl",
    league: "BUNDESLIGA",
    status: "SCHEDULED",
    kickoffTime: "18:30",
    matchweek: 28,
    venue: "BayArena",
    homeTeam: getTeam("b04"),
    awayTeam: getTeam("rbl"),
    homeScore: null,
    awayScore: null,
    aiPrediction: { home: 47, draw: 27, away: 26 },
    fanPrediction: { home: 0, draw: 0, away: 0, totalVotes: 0 },
    recentForm: { home: defaultForm(2), away: defaultForm(0) },
    headToHead: "최근 5경기 레버쿠젠 3승 1무 1패.",
    aiPreviewTitle: "레버쿠젠 풀백들의 오버래핑이 다시 통할까",
    aiPreviewBody: genericPreview("레버쿠젠", "RB 라이프치히"),
    aiReviewTitle: "경기 종료 후 업데이트 예정",
    aiReviewBody: ["경기 종료 후 AI Review가 자동으로 생성됩니다."],
  },

  // Serie A
  {
    id: "int-juv",
    league: "SERIEA",
    status: "FINISHED",
    matchweek: 32,
    venue: "San Siro",
    homeTeam: getTeam("int"),
    awayTeam: getTeam("juv"),
    homeScore: 1,
    awayScore: 1,
    aiPrediction: { home: 46, draw: 30, away: 24 },
    fanPrediction: { home: 44, draw: 32, away: 24, totalVotes: 3110 },
    recentForm: { home: defaultForm(1), away: defaultForm(2) },
    headToHead: "최근 5경기 인테르 2승 2무 1패.",
    aiPreviewTitle: "이탈리아 더비, 스코어리스 흐름을 깰 변수는",
    aiPreviewBody: genericPreview("인테르", "유벤투스"),
    aiReviewTitle: "치열했던 무승부, 양 팀 모두 결정력 아쉬워",
    aiReviewBody: genericReview("인테르", "유벤투스"),
  },
  {
    id: "mil-nap",
    league: "SERIEA",
    status: "SCHEDULED",
    kickoffTime: "20:45",
    matchweek: 32,
    venue: "San Siro",
    homeTeam: getTeam("mil"),
    awayTeam: getTeam("nap"),
    homeScore: null,
    awayScore: null,
    aiPrediction: { home: 39, draw: 28, away: 33 },
    fanPrediction: { home: 0, draw: 0, away: 0, totalVotes: 0 },
    recentForm: { home: defaultForm(0), away: defaultForm(2) },
    headToHead: "최근 5경기 나폴리 3승 2패.",
    aiPreviewTitle: "선두 나폴리를 상대하는 밀란의 역습 설계",
    aiPreviewBody: genericPreview("AC 밀란", "나폴리"),
    aiReviewTitle: "경기 종료 후 업데이트 예정",
    aiReviewBody: ["경기 종료 후 AI Review가 자동으로 생성됩니다."],
  },

  // Ligue 1
  {
    id: "psg-mar",
    league: "LIGUE1",
    status: "SCHEDULED",
    kickoffTime: "21:00",
    matchweek: 28,
    venue: "Parc des Princes",
    homeTeam: getTeam("psg"),
    awayTeam: getTeam("mar"),
    homeScore: null,
    awayScore: null,
    aiPrediction: { home: 63, draw: 21, away: 16 },
    fanPrediction: { home: 0, draw: 0, away: 0, totalVotes: 0 },
    recentForm: { home: defaultForm(0), away: defaultForm(1) },
    headToHead: "최근 5경기 PSG 4승 1패.",
    aiPreviewTitle: "르 클라시크, PSG의 측면 로테이션이 승부처",
    aiPreviewBody: genericPreview("PSG", "마르세유"),
    aiReviewTitle: "경기 종료 후 업데이트 예정",
    aiReviewBody: ["경기 종료 후 AI Review가 자동으로 생성됩니다."],
  },
  {
    id: "mon-lyo",
    league: "LIGUE1",
    status: "FINISHED",
    matchweek: 28,
    venue: "Stade Louis II",
    homeTeam: getTeam("mon"),
    awayTeam: getTeam("lyo"),
    homeScore: 2,
    awayScore: 2,
    aiPrediction: { home: 44, draw: 27, away: 29 },
    fanPrediction: { home: 41, draw: 28, away: 31, totalVotes: 1870 },
    recentForm: { home: defaultForm(2), away: defaultForm(0) },
    headToHead: "최근 5경기 모나코 2승 2무 1패.",
    aiPreviewTitle: "모나코 vs 리옹, 공격 축구의 향연이 될까",
    aiPreviewBody: genericPreview("모나코", "리옹"),
    aiReviewTitle: "골 잔치로 끝난 2-2, 양 팀 수비 조직력은 숙제",
    aiReviewBody: genericReview("모나코", "리옹"),
  },
];

export function getAllMatches(): Match[] {
  return MATCHES;
}

export function getMatchesByLeague(league: Match["league"]): Match[] {
  return MATCHES.filter((m) => m.league === league);
}

export function getMatchById(id: string): Match | undefined {
  return MATCHES.find((m) => m.id === id);
}
