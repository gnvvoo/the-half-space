import Link from "next/link";
import { notFound } from "next/navigation";
import { SimpleNavbar } from "@/components/layout/SimpleNavbar";
import { BoardTabs } from "@/components/board/BoardTabs";
import { PostList } from "@/components/board/PostList";
import { PostSearch } from "@/components/board/PostSearch";
import { Pagination } from "@/components/board/Pagination";
import { fetchBoardByCode, fetchBoardPosts, fetchBoards } from "@/lib/api/boards";

const PAGE_SIZE = 20;

export default async function BoardPage({
  params,
  searchParams,
}: {
  params: Promise<{ code: string }>;
  searchParams: Promise<{ page?: string; title?: string }>;
}) {
  const { code } = await params;
  const query = await searchParams;
  const page = Number(query.page ?? "0") || 0;

  const [boards, board] = await Promise.all([fetchBoards(), fetchBoardByCode(code)]);
  if (!board) notFound();

  const posts = await fetchBoardPosts(board.id, { page, size: PAGE_SIZE, title: query.title });

  return (
    <div className="mx-auto min-h-screen max-w-[1280px] bg-background">
      <SimpleNavbar />
      <main className="px-10 pb-[60px] pt-8">
        <div className="flex items-center justify-between">
          <h1 className="text-xl font-bold text-ink">{board.name}</h1>
          <Link
            href={`/boards/${board.code}/write`}
            className="border border-ink px-4 py-2 text-sm font-semibold text-ink hover:bg-ink hover:text-white"
          >
            글쓰기
          </Link>
        </div>
        <p className="mt-2 text-sm text-muted">{board.description}</p>

        <div className="mt-6">
          <BoardTabs boards={boards} activeCode={board.code} />
        </div>

        <div className="mt-4 flex justify-end">
          <PostSearch boardCode={board.code} defaultTitle={query.title} />
        </div>

        <PostList boardCode={board.code} posts={posts.content} />
        <Pagination
          boardCode={board.code}
          page={page}
          totalElements={posts.totalElements}
          size={PAGE_SIZE}
          title={query.title}
        />
      </main>
    </div>
  );
}
