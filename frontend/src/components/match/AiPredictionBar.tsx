import { SplitBar, SplitBarLabels } from "@/components/ui/SplitBar";
import type { VoteDistribution } from "@/lib/types";

export function AiPredictionBar({
  prediction,
  homeLabel,
  awayLabel,
}: {
  prediction: VoteDistribution;
  homeLabel: string;
  awayLabel: string;
}) {
  return (
    <section className="border-b border-line px-10 py-6">
      <p className="text-xs font-bold text-muted">AI 승부 예측</p>
      <div className="mt-3">
        <SplitBar {...prediction} />
      </div>
      <div className="mt-2">
        <SplitBarLabels {...prediction} homeLabel={`${homeLabel} 승`} awayLabel={`${awayLabel} 승`} />
      </div>
    </section>
  );
}
