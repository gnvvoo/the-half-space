"use client";

import Link from "next/link";
import { SimpleNavbar } from "@/components/layout/SimpleNavbar";
import { ProfileCard } from "@/components/mypage/ProfileCard";
import { FavoriteTeams } from "@/components/mypage/FavoriteTeams";
import { PredictionStats } from "@/components/mypage/PredictionStats";
import { PredictionHistory } from "@/components/mypage/PredictionHistory";
import { useAuth } from "@/context/AuthContext";

export default function MyPage() {
  const { user, isAuthenticated, isLoading } = useAuth();

  return (
    <div className="mx-auto min-h-screen max-w-[1280px] bg-background">
      <SimpleNavbar />

      {isLoading ? null : !isAuthenticated || !user ? (
        <main className="flex flex-col items-center gap-4 px-10 py-24 text-center">
          <p className="text-sm text-muted">마이페이지를 보려면 로그인이 필요합니다.</p>
          <Link
            href="/login"
            className="border border-ink px-4 py-2 text-sm font-semibold text-ink hover:bg-ink hover:text-white"
          >
            로그인하러 가기
          </Link>
        </main>
      ) : (
        <main className="flex gap-10 px-10 py-10">
          <ProfileCard user={user} />
          <div className="flex flex-1 flex-col gap-7">
            <FavoriteTeams teams={user.favoriteTeams} />
            <PredictionStats
              accuracy={user.predictionAccuracy}
              total={user.totalPredictions}
              correct={user.correctPredictions}
            />
            <PredictionHistory items={user.recentPredictions} />
          </div>
        </main>
      )}
    </div>
  );
}
