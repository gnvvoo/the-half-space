"use client";

import { useState } from "react";
import { LoginForm } from "./LoginForm";
import { SignupForm } from "./SignupForm";
import { underlineTabClass } from "@/lib/utils";

type Tab = "login" | "signup";

export function AuthTabs() {
  const [tab, setTab] = useState<Tab>("login");

  return (
    <div className="mx-auto w-[380px] py-20">
      <div className="flex gap-6 border-b border-line">
        <button
          type="button"
          onClick={() => setTab("login")}
          className={underlineTabClass(tab === "login")}
        >
          로그인
          {tab === "login" && (
            <span className="absolute inset-x-0 bottom-0 h-[2px] bg-brand" />
          )}
        </button>
        <button
          type="button"
          onClick={() => setTab("signup")}
          className={underlineTabClass(tab === "signup")}
        >
          회원가입
          {tab === "signup" && (
            <span className="absolute inset-x-0 bottom-0 h-[2px] bg-brand" />
          )}
        </button>
      </div>

      <div className="pt-8">
        {tab === "login" ? (
          <LoginForm onSwitchToSignup={() => setTab("signup")} />
        ) : (
          <SignupForm onSwitchToLogin={() => setTab("login")} />
        )}
      </div>
    </div>
  );
}
