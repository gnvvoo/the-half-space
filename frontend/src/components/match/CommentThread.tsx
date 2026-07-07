"use client";

import { useState } from "react";
import type { Comment } from "@/lib/types";
import { cx } from "@/lib/utils";

function updateTree(
  comments: Comment[],
  id: string,
  updater: (c: Comment) => Comment
): Comment[] {
  return comments.map((c) => {
    if (c.id === id) return updater(c);
    if (c.replies.length > 0) {
      return { ...c, replies: updateTree(c.replies, id, updater) };
    }
    return c;
  });
}

function CommentRow({
  comment,
  depth,
  onLike,
  onReply,
}: {
  comment: Comment;
  depth: number;
  onLike: (id: string) => void;
  onReply: (id: string, content: string) => void;
}) {
  const [liked, setLiked] = useState(false);
  const [replying, setReplying] = useState(false);
  const [draft, setDraft] = useState("");

  return (
    <div
      className={cx(
        depth > 0 && "ml-6 mt-3 border-l border-line pl-4",
        depth === 0 && "border-t border-line pt-4 first:border-t-0 first:pt-0"
      )}
    >
      <p className="text-sm font-semibold text-ink">{comment.author}</p>
      <p className="mt-1 text-sm leading-relaxed text-ink/90">{comment.content}</p>
      <div className="mt-2 flex items-center gap-3 text-xs text-muted">
        <button
          type="button"
          onClick={() => {
            setLiked((v) => !v);
            onLike(comment.id);
          }}
          className={cx("font-medium hover:text-ink", liked && "text-brand")}
        >
          좋아요 {comment.likes + (liked ? 1 : 0)}
        </button>
        <button
          type="button"
          onClick={() => setReplying((v) => !v)}
          className="font-medium hover:text-ink"
        >
          답글
        </button>
        <span>{comment.createdAt}</span>
      </div>

      {replying && (
        <form
          className="mt-3 flex gap-2"
          onSubmit={(e) => {
            e.preventDefault();
            if (!draft.trim()) return;
            onReply(comment.id, draft.trim());
            setDraft("");
            setReplying(false);
          }}
        >
          <input
            value={draft}
            onChange={(e) => setDraft(e.target.value)}
            placeholder="답글을 입력하세요"
            className="flex-1 border border-line px-3 py-2 text-sm outline-none focus:border-ink"
          />
          <button
            type="submit"
            className="border border-ink px-3 py-2 text-xs font-semibold text-ink hover:bg-ink hover:text-white"
          >
            등록
          </button>
        </form>
      )}

      {comment.replies.map((reply) => (
        <CommentRow
          key={reply.id}
          comment={reply}
          depth={depth + 1}
          onLike={onLike}
          onReply={onReply}
        />
      ))}
    </div>
  );
}

export function CommentThread({ initialComments }: { initialComments: Comment[] }) {
  const [comments, setComments] = useState(initialComments);

  const handleLike = (id: string) => {
    setComments((prev) =>
      updateTree(prev, id, (c) => ({ ...c, likes: c.likes + 1 }))
    );
  };

  const handleReply = (id: string, content: string) => {
    setComments((prev) =>
      updateTree(prev, id, (c) => ({
        ...c,
        replies: [
          ...c.replies,
          {
            id: `${id}-${Date.now()}`,
            author: "나",
            content,
            likes: 0,
            createdAt: "방금 전",
            replies: [],
          },
        ],
      }))
    );
  };

  return (
    <div className="flex flex-col">
      {comments.map((comment) => (
        <CommentRow
          key={comment.id}
          comment={comment}
          depth={0}
          onLike={handleLike}
          onReply={handleReply}
        />
      ))}
    </div>
  );
}
