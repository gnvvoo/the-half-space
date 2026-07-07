import type { Comment } from "../types";

const ARS_MCI_COMMENTS: Comment[] = [
  {
    id: "c1",
    author: "gooner_92",
    content: "사카 오늘 인사이드로 좁혀 들어오는 타이밍이 진짜 좋다. 2골 다 그 패턴에서 나옴.",
    likes: 58,
    createdAt: "12분 전",
    replies: [
      {
        id: "c1-r1",
        author: "citizen_blue",
        content: "인정. 근데 후반 들어 시티가 그 공간을 슬슬 잠그기 시작한 느낌도 있음.",
        likes: 14,
        createdAt: "9분 전",
        replies: [],
      },
    ],
  },
  {
    id: "c2",
    author: "citizen_blue",
    content: "홀란드 추격골 나온 이후 분위기 완전히 바뀜. 남은 20분이 진짜 중요할듯.",
    likes: 41,
    createdAt: "20분 전",
    replies: [],
  },
  {
    id: "c3",
    author: "tactics_nerd",
    content: "AI 프리뷰에서 언급한 미드블록 붕괴가 그대로 재현되고 있네요. 예측 모델 꽤 정확한 듯.",
    likes: 33,
    createdAt: "35분 전",
    replies: [
      {
        id: "c3-r1",
        author: "gooner_92",
        content: "동의합니다. 다음 경기 프리뷰도 기대되네요.",
        likes: 6,
        createdAt: "30분 전",
        replies: [],
      },
    ],
  },
  {
    id: "c4",
    author: "half_space_fan",
    content: "이 정도면 오늘 하이라이트는 사카-오데가드 스위칭이다.",
    likes: 19,
    createdAt: "41분 전",
    replies: [],
  },
];

const DEFAULT_COMMENTS: Comment[] = [
  {
    id: "gc1",
    author: "football_fan01",
    content: "경기 시작 전이라 기대되네요. AI 예측대로 흘러갈지 지켜봐야죠.",
    likes: 8,
    createdAt: "1시간 전",
    replies: [],
  },
];

const COMMENTS_BY_MATCH: Record<string, Comment[]> = {
  "ars-mci": ARS_MCI_COMMENTS,
};

export function getCommentsForMatch(matchId: string): Comment[] {
  return COMMENTS_BY_MATCH[matchId] ?? DEFAULT_COMMENTS;
}

export function countComments(comments: Comment[]): number {
  return comments.reduce((total, c) => total + 1 + countComments(c.replies), 0);
}
