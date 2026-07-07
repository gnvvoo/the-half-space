import { MainNavbar } from "@/components/layout/MainNavbar";
import { LeagueMatchSection } from "@/components/home/LeagueMatchSection";
import { DiscussionSidebar } from "@/components/home/DiscussionSidebar";
import { fetchAllMatches } from "@/lib/api/matches";
import { fetchPopularDiscussions } from "@/lib/api/discussions";
import { LEAGUES, DEFAULT_LEAGUE, isLeagueId } from "@/lib/leagues";

export default async function HomePage({
  searchParams,
}: {
  searchParams: Promise<{ league?: string }>;
}) {
  const params = await searchParams;
  const activeLeague = isLeagueId(params.league) ? params.league : DEFAULT_LEAGUE;

  const [matches, discussions] = await Promise.all([
    fetchAllMatches(),
    fetchPopularDiscussions(),
  ]);

  return (
    <div className="mx-auto min-h-screen max-w-[1280px] bg-background">
      <MainNavbar activeLeague={activeLeague} mode="home" />

      <main className="flex gap-10 px-10 py-8">
        <div className="flex flex-1 flex-col gap-9">
          {LEAGUES.map((league) => (
            <LeagueMatchSection
              key={league.id}
              league={league}
              matches={matches.filter((m) => m.league === league.id)}
            />
          ))}
        </div>
        <DiscussionSidebar discussions={discussions} />
      </main>
    </div>
  );
}
