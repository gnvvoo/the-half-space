"use client";

import { createContext, useCallback, useContext, useMemo, useSyncExternalStore } from "react";
import type { ReactNode } from "react";
import type { User } from "@/lib/types";
import { login as apiLogin, signup as apiSignup, decodeAccessToken, buildUser } from "@/lib/api/auth";
import type { LoginPayload, SignupPayload } from "@/lib/api/auth";
import { fetchCurrentUser } from "@/lib/api/user";

interface StoredAuth {
  user: User;
  accessToken: string | null;
  refreshToken: string | null;
}

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

const STORAGE_KEY = "half-space:auth";
const listeners = new Set<() => void>();

function emitChange() {
  for (const listener of listeners) listener();
}

function subscribe(callback: () => void) {
  listeners.add(callback);
  window.addEventListener("storage", callback);
  return () => {
    listeners.delete(callback);
    window.removeEventListener("storage", callback);
  };
}

function getSnapshot(): string | null {
  try {
    return window.localStorage.getItem(STORAGE_KEY);
  } catch {
    return null;
  }
}

function getServerSnapshot(): string | null {
  return null;
}

function readAuth(raw: string | null): StoredAuth | null {
  if (!raw) return null;
  try {
    const parsed = JSON.parse(raw) as Partial<StoredAuth>;
    return parsed.user ? (parsed as StoredAuth) : null;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const raw = useSyncExternalStore(subscribe, getSnapshot, getServerSnapshot);
  const auth = readAuth(raw);

  const persist = useCallback((next: StoredAuth | null) => {
    if (next) {
      window.localStorage.setItem(STORAGE_KEY, JSON.stringify(next));
    } else {
      window.localStorage.removeItem(STORAGE_KEY);
    }
    emitChange();
  }, []);

  const login = useCallback(
    async (payload: LoginPayload) => {
      const { user: fallbackUser, tokens } = await apiLogin(payload);
      const user = await fetchCurrentUser(tokens.accessToken).catch(() => fallbackUser);
      persist({ user, accessToken: tokens.accessToken, refreshToken: tokens.refreshToken });
    },
    [persist]
  );

  const signup = useCallback(
    async (payload: SignupPayload) => {
      const { user: fallbackUser, tokens } = await apiSignup(payload);
      const user = await fetchCurrentUser(tokens.accessToken).catch(() => fallbackUser);
      persist({ user, accessToken: tokens.accessToken, refreshToken: tokens.refreshToken });
    },
    [persist]
  );

  const loginWithTokens = useCallback(
    async (accessToken: string, refreshToken: string) => {
      // GET /users/me 조회가 실패할 경우를 대비해 토큰 클레임으로 만든 폴백 사용자를 준비한다.
      const claims = decodeAccessToken(accessToken);
      const fallbackUser: User = buildUser(claims, claims.email.split("@")[0]);
      const user = await fetchCurrentUser(accessToken).catch(() => fallbackUser);
      persist({ user, accessToken, refreshToken });
    },
    [persist]
  );

  const logout = useCallback(() => persist(null), [persist]);

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
