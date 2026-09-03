"use client";

import { use, useEffect, useState } from "react";
import Link from "next/link";
import { SimpleNavbar } from "@/components/layout/SimpleNavbar";
import { PostForm } from "@/components/board/PostForm";
import { useAuth } from "@/context/AuthContext";
import { fetchPost } from "@/lib/api/boards";
import type { Post } from "@/lib/types";

export default function EditPostPage({
  params,
}: {
  params: Promise<{ code: string; postId: string }>;
}) {
  const { code, postId } = use(params);
  const { user, accessToken } = useAuth();
  const [post, setPost] = useState<Post | undefined | null>(null);

  useEffect(() => {
    fetchPost(postId).then((p) => setPost(p ?? undefined));
  }, [postId]);

  const isOwn = post && accessToken && user?.id === post.authorId;

  return (
    <div className="mx-auto min-h-screen max-w-[1280px] bg-background">
      <SimpleNavbar />
      <main className="mx-auto max-w-[720px] px-10 pb-[60px] pt-8">
        <h1 className="text-xl font-bold text-ink">게시글 수정</h1>

        {!accessToken ? (
          <p className="mt-6 text-sm text-muted">
            로그인 후 이용해 주세요.{" "}
            <Link href="/login" className="font-semibold text-brand">
              로그인하기
            </Link>
          </p>
        ) : post === null ? (
          <p className="mt-6 text-sm text-muted">불러오는 중...</p>
        ) : post === undefined ? (
          <p className="mt-6 text-sm text-muted">존재하지 않는 게시글입니다.</p>
        ) : !isOwn ? (
          <p className="mt-6 text-sm text-muted">본인이 작성한 게시글만 수정할 수 있습니다.</p>
        ) : (
          <div className="mt-6">
            <PostForm boardId={post.boardId} boardCode={code} post={post} />
          </div>
        )}
      </main>
    </div>
  );
}
