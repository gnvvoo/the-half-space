import type { User } from "./types";

export interface StoredAuth {
  user: User;
  accessToken: string | null;
  refreshToken: string | null;
}

export const AUTH_STORAGE_KEY = "half-space:auth";

const listeners = new Set<() => void>();

function emitChange() {
  for (const listener of listeners) listener();
}

/** `AuthContext`의 `useSyncExternalStore`가 구독하는 리스너 등록 함수. */
export function subscribeAuth(callback: () => void) {
  listeners.add(callback);
  window.addEventListener("storage", callback);
  return () => {
    listeners.delete(callback);
    window.removeEventListener("storage", callback);
  };
}

export function getAuthSnapshot(): string | null {
  try {
    return window.localStorage.getItem(AUTH_STORAGE_KEY);
  } catch {
    return null;
  }
}

export function getAuthServerSnapshot(): string | null {
  return null;
}

export function readAuth(raw: string | null): StoredAuth | null {
  if (!raw) return null;
  try {
    const parsed = JSON.parse(raw) as Partial<StoredAuth>;
    return parsed.user ? (parsed as StoredAuth) : null;
  } catch {
    return null;
  }
}

/** localStorage에 인증 정보를 쓰고(또는 지우고) 같은 탭의 구독자에게 알린다. */
export function persistAuth(next: StoredAuth | null): void {
  if (next) {
    window.localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(next));
  } else {
    window.localStorage.removeItem(AUTH_STORAGE_KEY);
  }
  emitChange();
}

/** 저장된 accessToken/refreshToken을 읽는다. API 계층(예: 401 재시도)에서 사용. */
export function getStoredTokens(): { accessToken: string | null; refreshToken: string | null } {
  const auth = readAuth(getAuthSnapshot());
  return { accessToken: auth?.accessToken ?? null, refreshToken: auth?.refreshToken ?? null };
}

/** 토큰 재발급 성공 시 저장된 인증 정보의 토큰만 갱신한다. 로그인 상태가 아니면 무시한다. */
export function updateStoredTokens(accessToken: string, refreshToken: string): void {
  const auth = readAuth(getAuthSnapshot());
  if (!auth) return;
  persistAuth({ ...auth, accessToken, refreshToken });
}

/** 토큰 재발급 실패 시 로그아웃 상태로 되돌린다. */
export function clearStoredAuth(): void {
  persistAuth(null);
}
