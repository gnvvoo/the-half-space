import { notFound } from "next/navigation";
import { SimpleNavbar } from "@/components/layout/SimpleNavbar";
import { PostActions } from "@/components/board/PostActions";
import { CommentThread } from "@/components/match/CommentThread";
import { fetchPost } from "@/lib/api/boards";
import { fetchPostComments } from "@/lib/api/comments";

function formatDate(iso: string): string {
  return new Date(iso).toLocaleString("ko-KR", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
}

export default async function PostDetailPage({
  params,
}: {
  params: Promise<{ code: string; postId: string }>;
}) {
  const { code, postId } = await params;
  const post = await fetchPost(postId);
  if (!post) notFound();

  const commentPage = await fetchPostComments(post.id);

  return (
    <div className="mx-auto min-h-screen max-w-[1280px] bg-background">
      <SimpleNavbar />
      <main className="mx-auto max-w-[820px] px-10 pb-[60px] pt-8">
        <h1 className="text-xl font-bold text-ink">{post.title}</h1>
        <div className="mt-3 flex items-center justify-between border-b border-line pb-4 text-xs text-muted">
          <span>
            {post.authorNickname ?? "탈퇴한 사용자"} · {formatDate(post.createdAt)} · 조회{" "}
            {post.viewCount}
          </span>
          <PostActions boardCode={code} post={post} />
        </div>

        <div className="whitespace-pre-wrap py-8 text-sm leading-relaxed text-ink/90">
          {post.content}
        </div>

        <div className="border-t border-line pt-6">
          <h2 className="mb-4 text-sm font-bold text-ink">댓글</h2>
          <CommentThread comments={commentPage.content} target={{ type: "post", id: post.id }} />
        </div>
      </main>
    </div>
  );
}
