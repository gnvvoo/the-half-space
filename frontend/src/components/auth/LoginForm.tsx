"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/context/AuthContext";
import { SocialButtons } from "./SocialButtons";

export function LoginForm({ onSwitchToSignup }: { onSwitchToSignup: () => void }) {
  const router = useRouter();
  const { login } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      await login({ email, password });
      router.push("/");
    } catch (err) {
      setError(err instanceof Error ? err.message : "로그인에 실패했습니다.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <form onSubmit={handleSubmit} className="flex flex-col gap-4">
        <label className="flex flex-col gap-2 text-sm text-ink">
          이메일
          <input
            type="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="you@example.com"
            className="border border-line px-[14px] py-3 text-sm outline-none focus:border-ink"
          />
        </label>
        <label className="flex flex-col gap-2 text-sm text-ink">
          비밀번호
          <input
            type="password"
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="••••••••"
            className="border border-line px-[14px] py-3 text-sm outline-none focus:border-ink focus:border-brand"
          />
        </label>

        {error && <p className="text-xs text-brand">{error}</p>}

        <button
          type="submit"
          disabled={submitting}
          className="mt-2 w-full bg-brand py-3 text-sm font-bold text-white hover:opacity-90 disabled:opacity-60"
        >
          {submitting ? "로그인 중..." : "로그인"}
        </button>
      </form>

      <div className="my-6 flex items-center gap-3 text-xs text-muted">
        <span className="h-px flex-1 bg-line" />
        또는
        <span className="h-px flex-1 bg-line" />
      </div>

      <SocialButtons />

      <p className="mt-6 text-center text-sm text-muted">
        계정이 없으신가요?{" "}
        <button type="button" onClick={onSwitchToSignup} className="font-semibold text-brand">
          회원가입
        </button>
      </p>
    </div>
  );
}
