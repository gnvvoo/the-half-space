"use client";

import { use } from "react";
import Link from "next/link";
import { SimpleNavbar } from "@/components/layout/SimpleNavbar";
import { PostForm } from "@/components/board/PostForm";
import { useAuth } from "@/context/AuthContext";
import { fetchBoardByCode } from "@/lib/api/boards";
import { useEffect, useState } from "react";
import type { Board } from "@/lib/types";

export default function WritePostPage({ params }: { params: Promise<{ code: string }> }) {
  const { code } = use(params);
  const { accessToken } = useAuth();
  const [board, setBoard] = useState<Board | undefined | null>(null);

  useEffect(() => {
    fetchBoardByCode(code).then((b) => setBoard(b ?? undefined));
  }, [code]);

  return (
    <div className="mx-auto min-h-screen max-w-[1280px] bg-background">
      <SimpleNavbar />
      <main className="mx-auto max-w-[720px] px-10 pb-[60px] pt-8">
        <h1 className="text-xl font-bold text-ink">게시글 작성</h1>

        {!accessToken ? (
          <p className="mt-6 text-sm text-muted">
            로그인 후 이용해 주세요.{" "}
            <Link href="/login" className="font-semibold text-brand">
              로그인하기
            </Link>
          </p>
        ) : board === null ? (
          <p className="mt-6 text-sm text-muted">불러오는 중...</p>
        ) : board === undefined ? (
          <p className="mt-6 text-sm text-muted">존재하지 않는 게시판입니다.</p>
        ) : (
          <div className="mt-6">
            <PostForm boardId={board.id} boardCode={board.code} />
          </div>
        )}
      </main>
    </div>
  );
}
