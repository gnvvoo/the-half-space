"use client";

import Link from "next/link";
import { useAuth } from "@/context/AuthContext";
import { LEAGUES } from "@/lib/leagues";
import type { LeagueId } from "@/lib/types";
import { cx, underlineTabClass } from "@/lib/utils";

type Mode = "home" | "match" | "standings";

export function MainNavbar({
  activeLeague,
  mode,
}: {
  activeLeague: LeagueId;
  mode: Mode;
}) {
  const { isAuthenticated, user } = useAuth();

  return (
    <header className="border-b border-line">
      <div className="mx-auto flex max-w-[1280px] items-center justify-between px-10 py-5">
        <Link href="/" className="text-xl font-black tracking-tight text-ink">
          THE HALF SPACE
        </Link>

        <nav className="flex items-center gap-7">
          {LEAGUES.map((league) => {
            const active = league.id === activeLeague;
            const href =
              mode === "standings"
                ? `/standings?league=${league.id}`
                : `/?league=${league.id}`;
            return (
              <Link key={league.id} href={href} className={underlineTabClass(active)}>
                {league.nameKo}
                {active && (
                  <span className="absolute inset-x-0 bottom-0 h-[2px] bg-brand" />
                )}
              </Link>
            );
          })}
          {mode === "standings" && (
            <Link href="/" className="text-sm font-medium text-ink/70 hover:text-ink">
              홈
            </Link>
          )}
          <Link href="/boards" className="text-sm font-medium text-ink/70 hover:text-ink">
            게시판
          </Link>
        </nav>

        {isAuthenticated ? (
          <Link
            href="/mypage"
            className={cx(
              "border border-line px-4 py-2 text-sm font-semibold text-ink",
              "hover:border-ink"
            )}
          >
            {user?.nickname ?? "마이페이지"}
          </Link>
        ) : (
          <Link
            href="/login"
            className="border border-ink px-4 py-2 text-sm font-semibold text-ink hover:bg-ink hover:text-white"
          >
            로그인
          </Link>
        )}
      </div>
    </header>
  );
}
