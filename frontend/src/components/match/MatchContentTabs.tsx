"use client";

import { useState } from "react";
import { ArticleView } from "./ArticleView";
import { CommentThread } from "./CommentThread";
import type { Comment, Match } from "@/lib/types";
import { underlineTabClass } from "@/lib/utils";
import { countComments } from "@/lib/api/comments";

type TabKey = "preview" | "review" | "discussion";

export function MatchContentTabs({
  match,
  comments,
}: {
  match: Match;
  comments: Comment[];
}) {
  const [tab, setTab] = useState<TabKey>("preview");
  const commentCount = countComments(comments);

  const tabs: { key: TabKey; label: string }[] = [
    { key: "preview", label: "AI Preview" },
    { key: "review", label: "AI Review" },
    { key: "discussion", label: `토론 ${commentCount}` },
  ];

  return (
    <div>
      <div className="flex gap-7 border-b border-line">
        {tabs.map(({ key, label }) => (
          <button
            key={key}
            type="button"
            onClick={() => setTab(key)}
            className={underlineTabClass(tab === key)}
          >
            {label}
            {tab === key && (
              <span className="absolute inset-x-0 bottom-0 h-[2px] bg-brand" />
            )}
          </button>
        ))}
      </div>

      <div className="pt-8">
        {tab === "preview" && (
          <ArticleView
            title={match.aiPreviewTitle}
            body={match.aiPreviewBody}
            quote={match.aiPreviewQuote}
          />
        )}
        {tab === "review" && (
          <ArticleView
            title={match.aiReviewTitle}
            body={match.aiReviewBody}
            quote={match.aiReviewQuote}
          />
        )}
        {tab === "discussion" && (
          <CommentThread comments={comments} target={{ type: "match", id: match.id }} />
        )}
      </div>
    </div>
  );
}
