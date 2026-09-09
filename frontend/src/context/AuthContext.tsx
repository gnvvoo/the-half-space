"use client";

import { createContext, useCallback, useContext, useMemo, useSyncExternalStore } from "react";
import type { ReactNode } from "react";
import type { User } from "@/lib/types";
import { login as apiLogin, signup as apiSignup, decodeAccessToken, buildUser } from "@/lib/api/auth";
import type { LoginPayload, SignupPayload } from "@/lib/api/auth";
import { fetchCurrentUser } from "@/lib/api/user";
import {
  getAuthServerSnapshot,
  getAuthSnapshot,
  persistAuth,
  readAuth,
  subscribeAuth,
} from "@/lib/auth-storage";

interface AuthContextValue {
  user: User | null;
  accessToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (payload: LoginPayload) => Promise<void>;
  signup: (payload: SignupPayload) => Promise<void>;
  /** OAuth2 콜백(`/oauth2/callback`)에서 받은 토큰으로 로그인 상태를 만든다. */
  loginWithTokens: (accessToken: string, refreshToken: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const raw = useSyncExternalStore(subscribeAuth, getAuthSnapshot, getAuthServerSnapshot);
  const auth = readAuth(raw);

  const login = useCallback(async (payload: LoginPayload) => {
    const { user: fallbackUser, tokens } = await apiLogin(payload);
    const user = await fetchCurrentUser(tokens.accessToken).catch(() => fallbackUser);
    persistAuth({ user, accessToken: tokens.accessToken, refreshToken: tokens.refreshToken });
  }, []);

  const signup = useCallback(async (payload: SignupPayload) => {
    const { user: fallbackUser, tokens } = await apiSignup(payload);
    const user = await fetchCurrentUser(tokens.accessToken).catch(() => fallbackUser);
    persistAuth({ user, accessToken: tokens.accessToken, refreshToken: tokens.refreshToken });
  }, []);

  const loginWithTokens = useCallback(async (accessToken: string, refreshToken: string) => {
    // GET /users/me 조회가 실패할 경우를 대비해 토큰 클레임으로 만든 폴백 사용자를 준비한다.
    const claims = decodeAccessToken(accessToken);
    const fallbackUser: User = buildUser(claims, claims.email.split("@")[0]);
    const user = await fetchCurrentUser(accessToken).catch(() => fallbackUser);
    persistAuth({ user, accessToken, refreshToken });
  }, []);

  const logout = useCallback(() => persistAuth(null), []);

  const value = useMemo<AuthContextValue>(
    () => ({
      user: auth?.user ?? null,
      accessToken: auth?.accessToken ?? null,
      isAuthenticated: !!auth?.user,
      isLoading: false,
      login,
      signup,
      loginWithTokens,
      logout,
    }),
    [auth, login, signup, loginWithTokens, logout]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
