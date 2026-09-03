import type { MatchDiscussion } from "../types";
import { apiFetch } from "./http";
import { mapMatch, type MatchResponseDto } from "./matches";

interface MatchDiscussionResponseDto {
  match: MatchResponseDto;
  commentCount: number;
}

// GET /matches/today/discussions
export async function fetchTodayDiscussions(): Promise<MatchDiscussion[]> {
  const rows = await apiFetch<MatchDiscussionResponseDto[]>("/matches/today/discussions", {
    next: { revalidate: 60 },
  });
  return rows.map((row) => ({ match: mapMatch(row.match), commentCount: row.commentCount }));
}
