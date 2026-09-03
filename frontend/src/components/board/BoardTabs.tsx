import Link from "next/link";
import type { Board } from "@/lib/types";
import { underlineTabClass } from "@/lib/utils";

export function BoardTabs({ boards, activeCode }: { boards: Board[]; activeCode?: string }) {
  return (
    <nav className="flex flex-wrap gap-7 border-b border-line">
      {boards.map((board) => {
        const active = board.code === activeCode;
        return (
          <Link key={board.id} href={`/boards/${board.code}`} className={underlineTabClass(active)}>
            {board.name}
            {active && <span className="absolute inset-x-0 bottom-0 h-[2px] bg-brand" />}
          </Link>
        );
      })}
    </nav>
  );
}
