"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/context/AuthContext";
import { deletePost } from "@/lib/api/boards";
import type { Post } from "@/lib/types";

export function PostActions({ boardCode, post }: { boardCode: string; post: Post }) {
  const router = useRouter();
  const { user, accessToken } = useAuth();

  const isOwn = accessToken !== null && user?.id === post.authorId;
  if (!isOwn) return null;

  const handleDelete = async () => {
    if (!accessToken) return;
    if (!window.confirm("게시글을 삭제하시겠습니까?")) return;
    try {
      await deletePost(post.id, accessToken);
      router.push(`/boards/${boardCode}`);
      router.refresh();
    } catch (err) {
      window.alert(err instanceof Error ? err.message : "삭제에 실패했습니다.");
    }
  };

  return (
    <div className="flex gap-2 text-xs">
      <Link
        href={`/boards/${boardCode}/${post.id}/edit`}
        className="border border-line px-3 py-2 font-semibold text-ink hover:border-ink"
      >
        수정
      </Link>
      <button
        type="button"
        onClick={handleDelete}
        className="border border-line px-3 py-2 font-semibold text-ink hover:border-brand hover:text-brand"
      >
        삭제
      </button>
    </div>
  );
}
