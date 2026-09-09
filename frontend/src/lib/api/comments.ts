import type { Comment, Page } from "../types";
import { apiFetch } from "./http";
import { authFetch } from "./auth";

interface CommentResponseDto {
  id: number;
  matchId: number | null;
  postId: number | null;
  parentId: number | null;
  authorId: number | null;
  authorNickname: string | null;
  content: string | null;
  deleted: boolean;
  createdAt: string;
  replies: CommentResponseDto[];
}

function mapComment(raw: CommentResponseDto): Comment {
  return {
    id: String(raw.id),
    matchId: raw.matchId !== null ? String(raw.matchId) : null,
    postId: raw.postId !== null ? String(raw.postId) : null,
    parentId: raw.parentId !== null ? String(raw.parentId) : null,
    authorId: raw.authorId !== null ? String(raw.authorId) : null,
    authorNickname: raw.authorNickname,
    content: raw.content,
    deleted: raw.deleted,
    createdAt: raw.createdAt,
    replies: raw.replies.map(mapComment),
  };
}

interface PageResponseDto<T> {
  content: T[];
  totalElements: number;
}

function mapPage<T, R>(raw: PageResponseDto<T>, mapItem: (item: T) => R): Page<R> {
  return { content: raw.content.map(mapItem), totalElements: raw.totalElements };
}

export interface CommentDraft {
  content: string;
  parentId?: string;
}

// GET /matches/{matchId}/comments
export async function fetchMatchComments(
  matchId: string,
  page = 0,
  size = 20
): Promise<Page<Comment>> {
  const params = new URLSearchParams({ page: String(page), size: String(size) });
  const raw = await apiFetch<PageResponseDto<CommentResponseDto>>(
    `/matches/${matchId}/comments?${params}`
  );
  return mapPage(raw, mapComment);
}

// POST /matches/{matchId}/comments
export async function createMatchComment(
  matchId: string,
  accessToken: string,
  draft: CommentDraft
): Promise<Comment> {
  const raw = await authFetch<CommentResponseDto>(`/matches/${matchId}/comments`, accessToken, {
    method: "POST",
    body: JSON.stringify(draft),
  });
  return mapComment(raw);
}

// GET /posts/{postId}/comments
export async function fetchPostComments(
  postId: string,
  page = 0,
  size = 20
): Promise<Page<Comment>> {
  const params = new URLSearchParams({ page: String(page), size: String(size) });
  const raw = await apiFetch<PageResponseDto<CommentResponseDto>>(
    `/posts/${postId}/comments?${params}`
  );
  return mapPage(raw, mapComment);
}

// POST /posts/{postId}/comments
export async function createPostComment(
  postId: string,
  accessToken: string,
  draft: CommentDraft
): Promise<Comment> {
  const raw = await authFetch<CommentResponseDto>(`/posts/${postId}/comments`, accessToken, {
    method: "POST",
    body: JSON.stringify(draft),
  });
  return mapComment(raw);
}

// DELETE /comments/{id}
export async function deleteComment(id: string, accessToken: string): Promise<void> {
  await authFetch<void>(`/comments/${id}`, accessToken, { method: "DELETE" });
}

/** 현재 페이지에 로드된 댓글(대댓글 포함) 총 개수. */
export function countComments(comments: Comment[]): number {
  return comments.reduce((total, c) => total + 1 + countComments(c.replies), 0);
}
