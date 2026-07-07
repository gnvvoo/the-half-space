"use client";

import { useState } from "react";
import Link from "next/link";
import { useAuth } from "@/context/AuthContext";
import type { FanPrediction } from "@/lib/types";
import { cx, formatNumber } from "@/lib/utils";

type Choice = "home" | "draw" | "away";

export function FanPredictionWidget({
  initial,
  homeLabel,
  awayLabel,
}: {
  initial: FanPrediction;
  homeLabel: string;
  awayLabel: string;
}) {
  const { isAuthenticated } = useAuth();
  const [prediction, setPrediction] = useState(initial);
  const [selected, setSelected] = useState<Choice | null>(null);

  const hasVoted = selected !== null;

  const vote = (choice: Choice) => {
    if (!isAuthenticated || hasVoted) return;
    setPrediction((prev) => ({
      ...prev,
      [choice]: prev[choice] + 1,
      totalVotes: prev.totalVotes + 1,
    }));
    setSelected(choice);
  };

  const pct = (choice: Choice) =>
    prediction.totalVotes === 0
      ? 0
      : Math.round((prediction[choice] / prediction.totalVotes) * 100);

  const options: { choice: Choice; label: string }[] = [
    { choice: "home", label: homeLabel },
    { choice: "draw", label: "무" },
    { choice: "away", label: awayLabel },
  ];

  return (
    <div className="border border-line p-4">
      <p className="text-xs font-bold text-muted">팬 승부예측</p>

      <div className="mt-3 flex gap-[6px]">
        {options.map(({ choice, label }) => {
          const isSelected = selected === choice;
          const percentage = pct(choice);
          return (
            <button
              key={choice}
              type="button"
              onClick={() => vote(choice)}
              disabled={hasVoted || !isAuthenticated}
              className={cx(
                "relative flex-1 overflow-hidden border py-2 text-xs font-semibold transition-colors",
                isSelected ? "border-brand text-brand" : "border-line text-ink",
                !hasVoted && isAuthenticated && "hover:border-ink"
              )}
            >
              {hasVoted && (
                <span
                  className="absolute inset-y-0 left-0 bg-[#F3F2EF]"
                  style={{ width: `${percentage}%` }}
                  aria-hidden
                />
              )}
              <span className="relative">
                {label}
                {hasVoted && ` ${percentage}%`}
              </span>
            </button>
          );
        })}
      </div>

      <p className="mt-3 text-xs text-muted">
        {formatNumber(prediction.totalVotes)}명 참여
      </p>

      {!isAuthenticated && (
        <p className="mt-2 text-xs text-brand">
          <Link href="/login" className="underline">
            로그인
          </Link>
          하면 승부예측에 참여할 수 있어요.
        </p>
      )}
    </div>
  );
}
