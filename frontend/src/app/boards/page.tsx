import { redirect } from "next/navigation";
import { fetchBoards } from "@/lib/api/boards";

// Board 목록은 백엔드에서만 얻을 수 있어 빌드 시점에 정적 생성할 수 없다.
export const dynamic = "force-dynamic";

export default async function BoardsIndexPage() {
  const boards = await fetchBoards();
  redirect(`/boards/${boards[0]?.code ?? "free"}`);
}
