import { notFound } from "next/navigation";
import { MainNavbar } from "@/components/layout/MainNavbar";
import { Scoreboard } from "@/components/match/Scoreboard";
import { AiPredictionBar } from "@/components/match/AiPredictionBar";
import { MatchContentTabs } from "@/components/match/MatchContentTabs";
import { MatchSidebar } from "@/components/match/MatchSidebar";
import { fetchMatchById } from "@/lib/api/matches";
import { fetchStandings } from "@/lib/api/standings";
import { fetchMatchComments } from "@/lib/api/comments";

export default async function MatchDetailPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;
  const match = await fetchMatchById(id);
  if (!match) notFound();

  const [standings, commentPage] = await Promise.all([
    fetchStandings(match.league),
    fetchMatchComments(match.id),
  ]);
  const comments = commentPage.content;

  return (
    <div className="mx-auto min-h-screen max-w-[1280px] bg-background">
      <MainNavbar activeLeague={match.league} mode="match" />
      <Scoreboard match={match} />
      <AiPredictionBar
        prediction={match.aiPrediction}
        homeLabel={match.homeTeam.name}
        awayLabel={match.awayTeam.name}
      />

      <main className="flex gap-10 px-10 py-8">
        <div className="flex-1">
          <MatchContentTabs match={match} comments={comments} />
        </div>
        <MatchSidebar match={match} standings={standings} />
      </main>
    </div>
  );
}
