import type { VoteDistribution } from "@/lib/types";

/**
 * Reusable 3-split stacked bar used for both the AI 승부 예측 bar and the
 * fan 투표 분포 bar (spec: "AI 예측 확률 바 / 팬 투표 분포 바: 동일한 3분할
 * 스택 바 패턴 재사용 가능").
 */
export function SplitBar({ home, draw, away }: VoteDistribution) {
  return (
    <div className="flex h-[10px] w-full overflow-hidden">
      <div className="bg-brand" style={{ width: `${home}%` }} />
      <div className="bg-[#E5E5E0]" style={{ width: `${draw}%` }} />
      <div className="bg-ink" style={{ width: `${away}%` }} />
    </div>
  );
}

export function SplitBarLabels({
  home,
  draw,
  away,
  homeLabel,
  awayLabel,
}: VoteDistribution & { homeLabel: string; awayLabel: string }) {
  return (
    <div className="flex justify-between text-xs font-medium text-ink/80">
      <span>
        {homeLabel} {home}%
      </span>
      <span className="text-muted">무 {draw}%</span>
      <span>
        {awayLabel} {away}%
      </span>
    </div>
  );
}
