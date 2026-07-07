"use client";

import { oauthAuthorizationUrl } from "@/lib/api/config";
import type { OAuthProvider } from "@/lib/api/config";

function startOAuthLogin(provider: OAuthProvider) {
  window.location.href = oauthAuthorizationUrl(provider);
}

export function SocialButtons() {
  return (
    <div className="flex flex-col gap-3">
      <button
        type="button"
        onClick={() => startOAuthLogin("google")}
        className="flex items-center justify-center gap-2 border border-line py-3 text-sm font-medium text-ink hover:border-ink"
      >
        <span className="h-4 w-4 rounded-full border border-line" aria-hidden />
        Google로 계속하기
      </button>
      <button
        type="button"
        onClick={() => startOAuthLogin("kakao")}
        className="flex items-center justify-center gap-2 py-3 text-sm font-medium text-ink"
        style={{ background: "#FEE500" }}
      >
        <span className="h-4 w-4 rounded-full bg-ink/70" aria-hidden />
        카카오로 계속하기
      </button>
    </div>
  );
}
