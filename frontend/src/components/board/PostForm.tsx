"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/context/AuthContext";
import { createPost, updatePost } from "@/lib/api/boards";
import type { Post } from "@/lib/types";

export function PostForm({
  boardId,
  boardCode,
  post,
}: {
  boardId: string;
  boardCode: string;
  post?: Post;
}) {
  const router = useRouter();
  const { accessToken } = useAuth();
  const [title, setTitle] = useState(post?.title ?? "");
  const [content, setContent] = useState(post?.content ?? "");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!accessToken) {
      setError("로그인이 필요합니다.");
      return;
    }
    if (!title.trim() || !content.trim()) {
      setError("제목과 내용을 입력해 주세요.");
      return;
    }
    setSubmitting(true);
    setError(null);
    try {
      const draft = { title: title.trim(), content: content.trim() };
      if (post) {
        await updatePost(post.id, accessToken, draft);
        router.push(`/boards/${boardCode}/${post.id}`);
      } else {
        const created = await createPost(boardId, accessToken, draft);
        router.push(`/boards/${boardCode}/${created.id}`);
      }
      router.refresh();
    } catch (err) {
      setError(err instanceof Error ? err.message : "저장에 실패했습니다.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-4">
      <label className="flex flex-col gap-2 text-sm text-ink">
        제목
        <input
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          maxLength={200}
          placeholder="제목을 입력하세요"
          className="border border-line px-[14px] py-3 text-sm outline-none focus:border-ink"
        />
      </label>
      <label className="flex flex-col gap-2 text-sm text-ink">
        내용
        <textarea
          value={content}
          onChange={(e) => setContent(e.target.value)}
          maxLength={5000}
          rows={12}
          placeholder="내용을 입력하세요"
          className="border border-line px-[14px] py-3 text-sm outline-none focus:border-ink"
        />
      </label>

      {error && <p className="text-xs text-brand">{error}</p>}

      <button
        type="submit"
        disabled={submitting}
        className="mt-2 w-full bg-brand py-3 text-sm font-bold text-white hover:opacity-90 disabled:opacity-60"
      >
        {submitting ? "저장 중..." : post ? "수정 완료" : "게시글 등록"}
      </button>
    </form>
  );
}
