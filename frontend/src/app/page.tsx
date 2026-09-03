import { MainNavbar } from "@/components/layout/MainNavbar";
import { UpcomingMatches } from "@/components/home/LeagueMatchSection";
import { LeagueInfoCard } from "@/components/home/LeagueInfoCard";
import { DiscussionSidebar } from "@/components/home/DiscussionSidebar";
import { StandingsTable } from "@/components/standings/StandingsTable";
import { fetchMatchesByLeague } from "@/lib/api/matches";
import { fetchStandings } from "@/lib/api/standings";
import { fetchTodayDiscussions } from "@/lib/api/discussions";
import { DEFAULT_LEAGUE, isLeagueId, leagueDisplayName } from "@/lib/leagues";

export default async function HomePage({
  searchParams,
}: {
  searchParams: Promise<{ league?: string }>;
}) {
  const params = await searchParams;
  const activeLeague = isLeagueId(params.league) ? params.league : DEFAULT_LEAGUE;

  const [matches, standings, discussions] = await Promise.all([
    fetchMatchesByLeague(activeLeague),
    fetchStandings(activeLeague),
    fetchTodayDiscussions(),
  ]);

  return (
    <div className="mx-auto min-h-screen max-w-[1280px] bg-background">
      <MainNavbar activeLeague={activeLeague} mode="home" />

      <main className="flex gap-10 px-10 py-8">
        <div className="flex flex-1 flex-col gap-9">
          <h1 className="text-xl font-bold text-ink">{leagueDisplayName(activeLeague)}</h1>

          <UpcomingMatches matches={matches} />

          <section>
            <h2 className="text-lg font-bold text-ink">순위표</h2>
            <StandingsTable rows={standings} />
          </section>

          <LeagueInfoCard standings={standings} matches={matches} />
        </div>
        <DiscussionSidebar discussions={discussions} />
      </main>
    </div>
  );
}
