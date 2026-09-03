import type { Board, BoardCode, Page, Post } from "../types";
import { apiFetch, authHeaders, ApiRequestError } from "./http";

interface BoardResponseDto {
  id: number;
  code: BoardCode;
  name: string;
  description: string;
}

function mapBoard(raw: BoardResponseDto): Board {
  return { id: String(raw.id), code: raw.code, name: raw.name, description: raw.description };
}

// GET /boards
export async function fetchBoards(): Promise<Board[]> {
  const rows = await apiFetch<BoardResponseDto[]>("/boards", { next: { revalidate: 3600 } });
  return rows.map(mapBoard);
}

export async function fetchBoardByCode(code: string): Promise<Board | undefined> {
  const boards = await fetchBoards();
  return boards.find((b) => b.code === code);
}

interface PostResponseDto {
  id: number;
  boardId: number;
  boardCode: BoardCode;
  authorId: number | null;
  authorNickname: string | null;
  title: string;
  content: string;
  viewCount: number;
  deleted: boolean;
  createdAt: string;
  updatedAt: string | null;
}

function mapPost(raw: PostResponseDto): Post {
  return {
    id: String(raw.id),
    boardId: String(raw.boardId),
    boardCode: raw.boardCode,
    authorId: raw.authorId !== null ? String(raw.authorId) : null,
    authorNickname: raw.authorNickname,
    title: raw.title,
    content: raw.content,
    viewCount: raw.viewCount,
    deleted: raw.deleted,
    createdAt: raw.createdAt,
    updatedAt: raw.updatedAt,
  };
}

interface PageResponseDto<T> {
  content: T[];
  totalElements: number;
}

export interface PostListOptions {
  page?: number;
  size?: number;
  title?: string;
}

// GET /boards/{boardId}/posts
export async function fetchBoardPosts(
  boardId: string,
  { page = 0, size = 20, title }: PostListOptions = {}
): Promise<Page<Post>> {
  const params = new URLSearchParams({ page: String(page), size: String(size) });
  if (title) params.set("title", title);
  const raw = await apiFetch<PageResponseDto<PostResponseDto>>(
    `/boards/${boardId}/posts?${params}`
  );
  return { content: raw.content.map(mapPost), totalElements: raw.totalElements };
}

export interface PostDraft {
  title: string;
  content: string;
}

// POST /boards/{boardId}/posts
export async function createPost(
  boardId: string,
  accessToken: string,
  draft: PostDraft
): Promise<Post> {
  const raw = await apiFetch<PostResponseDto>(`/boards/${boardId}/posts`, {
    method: "POST",
    headers: authHeaders(accessToken),
    body: JSON.stringify(draft),
  });
  return mapPost(raw);
}

// GET /posts/{id}
export async function fetchPost(id: string): Promise<Post | undefined> {
  try {
    const raw = await apiFetch<PostResponseDto>(`/posts/${id}`, { cache: "no-store" });
    return mapPost(raw);
  } catch (err) {
    if (err instanceof ApiRequestError && err.status === 404) return undefined;
    throw err;
  }
}

// PUT /posts/{id}
export async function updatePost(
  id: string,
  accessToken: string,
  draft: PostDraft
): Promise<Post> {
  const raw = await apiFetch<PostResponseDto>(`/posts/${id}`, {
    method: "PUT",
    headers: authHeaders(accessToken),
    body: JSON.stringify(draft),
  });
  return mapPost(raw);
}

// DELETE /posts/{id}
export async function deletePost(id: string, accessToken: string): Promise<void> {
  await apiFetch<void>(`/posts/${id}`, {
    method: "DELETE",
    headers: authHeaders(accessToken),
  });
}
