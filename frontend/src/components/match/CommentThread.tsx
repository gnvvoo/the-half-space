"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import type { Comment } from "@/lib/types";
import { useAuth } from "@/context/AuthContext";
import { createMatchComment, createPostComment, deleteComment } from "@/lib/api/comments";
import { cx } from "@/lib/utils";

export type CommentTarget = { type: "match"; id: string } | { type: "post"; id: string };

function formatDate(iso: string): string {
  return new Date(iso).toLocaleString("ko-KR", {
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
}

async function submitComment(
  target: CommentTarget,
  accessToken: string,
  content: string,
  parentId?: string
) {
  if (target.type === "match") {
    return createMatchComment(target.id, accessToken, { content, parentId });
  }
  return createPostComment(target.id, accessToken, { content, parentId });
}

function CommentRow({
  comment,
  target,
  depth,
  currentUserId,
  accessToken,
  onChanged,
}: {
  comment: Comment;
  target: CommentTarget;
  depth: number;
  currentUserId: string | null;
  accessToken: string | null;
  onChanged: () => void;
}) {
  const [replying, setReplying] = useState(false);
  const [draft, setDraft] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const isOwn = !comment.deleted && currentUserId !== null && comment.authorId === currentUserId;

  const handleReply = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!draft.trim() || !accessToken) return;
    setSubmitting(true);
    setError(null);
    try {
      await submitComment(target, accessToken, draft.trim(), comment.id);
      setDraft("");
      setReplying(false);
      onChanged();
    } catch (err) {
      setError(err instanceof Error ? err.message : "답글 작성에 실패했습니다.");
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async () => {
    if (!accessToken) return;
    if (!window.confirm("댓글을 삭제하시겠습니까?")) return;
    try {
      await deleteComment(comment.id, accessToken);
      onChanged();
    } catch (err) {
      window.alert(err instanceof Error ? err.message : "삭제에 실패했습니다.");
    }
  };

  return (
    <div
      className={cx(
        depth > 0 && "ml-6 mt-3 border-l border-line pl-4",
        depth === 0 && "border-t border-line pt-4 first:border-t-0 first:pt-0"
      )}
    >
      <p className={cx("text-sm font-semibold", comment.deleted ? "text-muted" : "text-ink")}>
        {comment.deleted ? "삭제된 댓글" : comment.authorNickname}
      </p>
      <p className="mt-1 text-sm leading-relaxed text-ink/90">
        {comment.deleted ? "삭제된 댓글입니다." : comment.content}
      </p>
      <div className="mt-2 flex items-center gap-3 text-xs text-muted">
        <span>{formatDate(comment.createdAt)}</span>
        {!comment.deleted && depth === 0 && (
          <button
            type="button"
            onClick={() => setReplying((v) => !v)}
            className="font-medium hover:text-ink"
          >
            답글
          </button>
        )}
        {isOwn && (
          <button type="button" onClick={handleDelete} className="font-medium hover:text-brand">
            삭제
          </button>
        )}
      </div>

      {replying &&
        (accessToken ? (
          <form className="mt-3 flex gap-2" onSubmit={handleReply}>
            <input
              value={draft}
              onChange={(e) => setDraft(e.target.value)}
              placeholder="답글을 입력하세요"
              className="flex-1 border border-line px-3 py-2 text-sm outline-none focus:border-ink"
            />
            <button
              type="submit"
              disabled={submitting}
              className="border border-ink px-3 py-2 text-xs font-semibold text-ink hover:bg-ink hover:text-white disabled:opacity-60"
            >
              등록
            </button>
          </form>
        ) : (
          <p className="mt-2 text-xs text-muted">로그인 후 답글을 작성할 수 있습니다.</p>
        ))}
      {error && <p className="mt-1 text-xs text-brand">{error}</p>}

      {comment.replies.map((reply) => (
        <CommentRow
          key={reply.id}
          comment={reply}
          target={target}
          depth={depth + 1}
          currentUserId={currentUserId}
          accessToken={accessToken}
          onChanged={onChanged}
        />
      ))}
    </div>
  );
}

export function CommentThread({
  comments,
  target,
}: {
  comments: Comment[];
  target: CommentTarget;
}) {
  const router = useRouter();
  const { user, accessToken } = useAuth();
  const [draft, setDraft] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const refresh = () => router.refresh();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!draft.trim() || !accessToken) return;
    setSubmitting(true);
    setError(null);
    try {
      await submitComment(target, accessToken, draft.trim());
      setDraft("");
      refresh();
    } catch (err) {
      setError(err instanceof Error ? err.message : "댓글 작성에 실패했습니다.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="flex flex-col">
      {accessToken ? (
        <form onSubmit={handleSubmit} className="flex gap-2 border-b border-line pb-6">
          <input
            value={draft}
            onChange={(e) => setDraft(e.target.value)}
            placeholder="댓글을 입력하세요"
            className="flex-1 border border-line px-3 py-2 text-sm outline-none focus:border-ink"
          />
          <button
            type="submit"
            disabled={submitting}
            className="border border-ink px-3 py-2 text-xs font-semibold text-ink hover:bg-ink hover:text-white disabled:opacity-60"
          >
            등록
          </button>
        </form>
      ) : (
        <p className="border-b border-line pb-6 text-xs text-muted">
          로그인 후 댓글을 작성할 수 있습니다.
        </p>
      )}
      {error && <p className="mt-2 text-xs text-brand">{error}</p>}

      {comments.length === 0 ? (
        <p className="pt-4 text-sm text-muted">아직 댓글이 없습니다.</p>
      ) : (
        comments.map((comment) => (
          <CommentRow
            key={comment.id}
            comment={comment}
            target={target}
            depth={0}
            currentUserId={user?.id ?? null}
            accessToken={accessToken}
            onChanged={refresh}
          />
        ))
      )}
    </div>
  );
}
