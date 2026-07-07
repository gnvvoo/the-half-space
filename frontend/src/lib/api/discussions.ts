import type { Comment, Discussion } from "../types";
import { POPULAR_DISCUSSIONS } from "../data/discussions";
import { getCommentsForMatch } from "../data/comments";

// GET /discussions/popular
export async function fetchPopularDiscussions(): Promise<Discussion[]> {
  return POPULAR_DISCUSSIONS;
}

// GET /matches/{id}/comments
export async function fetchCommentsForMatch(matchId: string): Promise<Comment[]> {
  return getCommentsForMatch(matchId);
}
