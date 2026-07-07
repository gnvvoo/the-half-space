"use client";

import { useState } from "react";
import { CrestPlaceholder } from "@/components/ui/CrestPlaceholder";
import type { FavoriteTeamRef } from "@/lib/types";
import { cx } from "@/lib/utils";

export function FavoriteTeams({ teams }: { teams: FavoriteTeamRef[] }) {
  const [selected, setSelected] = useState<string | null>(teams[0]?.id ?? null);

  return (
    <section>
      <h2 className="text-sm font-bold text-ink">관심 팀</h2>
      <div className="mt-3 flex flex-wrap gap-2">
        {teams.map((team) => {
          const active = team.id === selected;
          return (
            <button
              key={team.id}
              type="button"
              onClick={() => setSelected(team.id)}
              className={cx(
                "flex items-center gap-2 border px-3 py-2 text-sm font-medium",
                active
                  ? "border-brand bg-brand/10 text-brand"
                  : "border-line text-ink hover:border-ink"
              )}
            >
              <CrestPlaceholder code={team.code} size="sm" />
              {team.name}
            </button>
          );
        })}
        <button
          type="button"
          className="border border-dashed border-line px-3 py-2 text-sm font-medium text-muted hover:border-ink hover:text-ink"
        >
          + 팀 추가
        </button>
      </div>
    </section>
  );
}
