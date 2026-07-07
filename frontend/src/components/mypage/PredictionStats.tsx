export function PredictionStats({
  accuracy,
  total,
  correct,
}: {
  accuracy: number;
  total: number;
  correct: number;
}) {
  const stats = [
    { label: "적중률", value: `${accuracy}%`, emphasize: true },
    { label: "참여 경기", value: `${total}` },
    { label: "적중", value: `${correct}` },
  ];

  return (
    <section>
      <h2 className="text-sm font-bold text-ink">내 승부예측 적중률</h2>
      <div className="mt-3 grid grid-cols-3 gap-[14px]">
        {stats.map((stat) => (
          <div key={stat.label} className="border border-line p-4 text-center">
            <p
              className={
                stat.emphasize
                  ? "text-2xl font-black text-brand"
                  : "text-2xl font-black text-ink"
              }
            >
              {stat.value}
            </p>
            <p className="mt-1 text-xs text-muted">{stat.label}</p>
          </div>
        ))}
      </div>
    </section>
  );
}
