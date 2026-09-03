import Link from "next/link";
import type { Post } from "@/lib/types";

function formatDate(iso: string): string {
  return new Date(iso).toLocaleDateString("ko-KR", { year: "2-digit", month: "2-digit", day: "2-digit" });
}

export function PostList({ boardCode, posts }: { boardCode: string; posts: Post[] }) {
  if (posts.length === 0) {
    return <p className="border-t border-line py-10 text-center text-sm text-muted">게시글이 없습니다.</p>;
  }

  return (
    <div className="mt-4 border-t border-line">
      <div className="grid grid-cols-[1fr_100px_90px_60px] gap-3 border-b border-line px-4 py-3 text-xs font-bold text-muted">
        <span>제목</span>
        <span>작성자</span>
        <span>작성일</span>
        <span className="text-right">조회</span>
      </div>
      {posts.map((post) => (
        <Link
          key={post.id}
          href={`/boards/${boardCode}/${post.id}`}
          className="grid grid-cols-[1fr_100px_90px_60px] items-center gap-3 border-b border-line px-4 py-3 text-sm hover:bg-line/20"
        >
          <span className="truncate font-medium text-ink">{post.title}</span>
          <span className="truncate text-ink/80">{post.authorNickname ?? "탈퇴한 사용자"}</span>
          <span className="text-ink/60">{formatDate(post.createdAt)}</span>
          <span className="text-right text-ink/60">{post.viewCount}</span>
        </Link>
      ))}
    </div>
  );
}
