import { MainNavbar } from "@/components/layout/MainNavbar";
import { StandingsTable } from "@/components/standings/StandingsTable";
import { fetchStandings } from "@/lib/api/standings";
import { DEFAULT_LEAGUE, isLeagueId, leagueDisplayName } from "@/lib/leagues";

export default async function StandingsPage({
  searchParams,
}: {
  searchParams: Promise<{ league?: string }>;
}) {
  const params = await searchParams;
  const activeLeague = isLeagueId(params.league) ? params.league : DEFAULT_LEAGUE;
  const rows = await fetchStandings(activeLeague);

  return (
    <div className="mx-auto min-h-screen max-w-[1280px] bg-background">
      <MainNavbar activeLeague={activeLeague} mode="standings" />
      <main className="px-10 pb-[60px] pt-8">
        <h1 className="text-xl font-bold text-ink">
          {leagueDisplayName(activeLeague)} 순위표
        </h1>
        <StandingsTable rows={rows} />
      </main>
    </div>
  );
}
