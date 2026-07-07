import type { PredictionHistoryItem } from "@/lib/types";
import { cx } from "@/lib/utils";

const PREDICTION_LABEL: Record<PredictionHistoryItem["prediction"], string> = {
  home: "홈 승",
  draw: "무",
  away: "원정 승",
};

export function PredictionHistory({ items }: { items: PredictionHistoryItem[] }) {
  return (
    <section>
      <h2 className="text-sm font-bold text-ink">최근 예측 기록</h2>
      <div className="mt-3 flex flex-col">
        {items.map((item, i) => (
          <div
            key={item.id}
            className={cx(
              "flex items-center justify-between py-3 text-sm",
              i > 0 && "border-t border-line"
            )}
          >
            <span className="text-ink">
              {item.homeTeam} {item.homeScore} - {item.awayScore} {item.awayTeam}
            </span>
            <span className="text-muted">예측: {PREDICTION_LABEL[item.prediction]}</span>
            <span className={cx("font-semibold", item.correct ? "text-success" : "text-brand")}>
              {item.correct ? "적중" : "실패"}
            </span>
          </div>
        ))}
      </div>
    </section>
  );
}
