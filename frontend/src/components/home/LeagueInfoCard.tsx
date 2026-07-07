import type { Match, StandingRow } from "@/lib/types";

export function LeagueInfoCard({
  standings,
  matches,
}: {
  standings: StandingRow[];
  matches: Match[];
}) {
  const leader = standings[0];
  const liveCount = matches.filter((m) => m.status === "LIVE").length;

  const items = [
    { label: "참가 팀 수", value: standings.length > 0 ? `${standings.length}팀` : "-" },
    { label: "현재까지 진행", value: leader ? `${leader.played}라운드` : "-" },
    {
      label: "선두",
      value: leader ? `${leader.team.name} (${leader.points}점)` : "-",
    },
    { label: "진행 중인 경기", value: `${liveCount}경기` },
  ];

  return (
    <section>
      <h2 className="text-lg font-bold text-ink">리그 정보</h2>
      <div className="mt-3 grid grid-cols-4 gap-[14px]">
        {items.map((item) => (
          <div key={item.label} className="border border-line p-4">
            <p className="text-xs text-muted">{item.label}</p>
            <p className="mt-1 text-sm font-bold text-ink">{item.value}</p>
          </div>
        ))}
      </div>
    </section>
  );
}
