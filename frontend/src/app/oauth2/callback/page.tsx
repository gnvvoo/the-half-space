"use client";

import { Suspense, useEffect, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { SimpleNavbar } from "@/components/layout/SimpleNavbar";
import { useAuth } from "@/context/AuthContext";

function OAuthCallbackContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { loginWithTokens } = useAuth();
  const accessToken = searchParams.get("accessToken");
  const refreshToken = searchParams.get("refreshToken");
  const [error, setError] = useState<string | null>(
    accessToken && refreshToken ? null : "소셜 로그인에 실패했습니다. 다시 시도해 주세요."
  );

  useEffect(() => {
    if (!accessToken || !refreshToken) return;

    loginWithTokens(accessToken, refreshToken)
      .then(() => router.replace("/"))
      .catch(() => setError("로그인 처리 중 오류가 발생했습니다."));
  }, [accessToken, refreshToken, loginWithTokens, router]);

  return (
    <main className="flex flex-col items-center gap-4 px-10 py-24 text-center">
      {error ? (
        <>
          <p className="text-sm text-brand">{error}</p>
          <a href="/login" className="text-sm font-semibold text-ink underline">
            로그인 페이지로 돌아가기
          </a>
        </>
      ) : (
        <p className="text-sm text-muted">로그인 처리 중입니다...</p>
      )}
    </main>
  );
}

export default function OAuthCallbackPage() {
  return (
    <div className="mx-auto min-h-screen max-w-[1280px] bg-background">
      <SimpleNavbar />
      <Suspense
        fallback={
          <main className="flex flex-col items-center gap-4 px-10 py-24 text-center">
            <p className="text-sm text-muted">로그인 처리 중입니다...</p>
          </main>
        }
      >
        <OAuthCallbackContent />
      </Suspense>
    </div>
  );
}
