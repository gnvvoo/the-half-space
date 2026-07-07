"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/context/AuthContext";
import { SocialButtons } from "./SocialButtons";

export function SignupForm({ onSwitchToLogin }: { onSwitchToLogin: () => void }) {
  const router = useRouter();
  const { signup } = useAuth();
  const [nickname, setNickname] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [passwordConfirm, setPasswordConfirm] = useState("");
  const [agreed, setAgreed] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (password !== passwordConfirm) {
      setError("비밀번호가 일치하지 않습니다.");
      return;
    }
    if (!agreed) {
      setError("약관에 동의해 주세요.");
      return;
    }

    setSubmitting(true);
    try {
      await signup({ nickname, email, password });
      router.push("/");
    } catch (err) {
      setError(err instanceof Error ? err.message : "회원가입에 실패했습니다.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <form onSubmit={handleSubmit} className="flex flex-col gap-4">
        <label className="flex flex-col gap-2 text-sm text-ink">
          닉네임
          <input
            required
            value={nickname}
            onChange={(e) => setNickname(e.target.value)}
            placeholder="gooner_92"
            className="border border-line px-[14px] py-3 text-sm outline-none focus:border-ink"
          />
        </label>
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
            className="border border-line px-[14px] py-3 text-sm outline-none focus:border-ink"
          />
        </label>
        <label className="flex flex-col gap-2 text-sm text-ink">
          비밀번호 확인
          <input
            type="password"
            required
            value={passwordConfirm}
            onChange={(e) => setPasswordConfirm(e.target.value)}
            placeholder="••••••••"
            className="border border-line px-[14px] py-3 text-sm outline-none focus:border-ink"
          />
        </label>

        <label className="flex items-center gap-2 text-xs text-ink/80">
          <input
            type="checkbox"
            checked={agreed}
            onChange={(e) => setAgreed(e.target.checked)}
          />
          이용약관 및 개인정보 처리방침에 동의합니다.
        </label>

        {error && <p className="text-xs text-brand">{error}</p>}

        <button
          type="submit"
          disabled={submitting}
          className="mt-2 w-full bg-brand py-3 text-sm font-bold text-white hover:opacity-90 disabled:opacity-60"
        >
          {submitting ? "가입 중..." : "회원가입"}
        </button>
      </form>

      <div className="my-6 flex items-center gap-3 text-xs text-muted">
        <span className="h-px flex-1 bg-line" />
        또는
        <span className="h-px flex-1 bg-line" />
      </div>

      <SocialButtons />

      <p className="mt-6 text-center text-sm text-muted">
        이미 계정이 있으신가요?{" "}
        <button type="button" onClick={onSwitchToLogin} className="font-semibold text-brand">
          로그인
        </button>
      </p>
    </div>
  );
}
