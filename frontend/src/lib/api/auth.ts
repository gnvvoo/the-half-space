import { apiFetch, authHeaders, ApiRequestError } from "./http";
import { clearStoredAuth, getStoredTokens, updateStoredTokens } from "../auth-storage";
import type { User } from "../types";

export interface LoginPayload {
  email: string;
  password: string;
}

export interface SignupPayload {
  nickname: string;
  email: string;
  password: string;
}

export interface AuthTokens {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

export interface AuthResult {
  user: User;
  tokens: AuthTokens;
}

interface AuthResponseDto {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

interface AccessTokenClaims {
  userId: number;
  email: string;
}

/**
 * 액세스 토큰의 payload(JWT 두 번째 세그먼트)를 검증 없이 디코딩한다.
 * GET /users/me 조회가 실패할 때 userId/email을 채우는 폴백으로 사용한다.
 */
function decodeAccessToken(accessToken: string): AccessTokenClaims {
  const payload = accessToken.split(".")[1];
  if (!payload) throw new Error("유효하지 않은 토큰입니다.");

  const base64 = payload.replace(/-/g, "+").replace(/_/g, "/");
  const json = typeof window === "undefined"
    ? Buffer.from(base64, "base64").toString("utf-8")
    : decodeURIComponent(
        atob(base64)
          .split("")
          .map((c) => "%" + c.charCodeAt(0).toString(16).padStart(2, "0"))
          .join("")
      );

  const claims = JSON.parse(json) as { userId: number; sub: string };
  return { userId: claims.userId, email: claims.sub };
}

function buildUser(claims: AccessTokenClaims, nickname: string): User {
  return {
    id: String(claims.userId),
    nickname,
    email: claims.email,
    favoriteTeams: [],
    predictionAccuracy: 0,
    totalPredictions: 0,
    correctPredictions: 0,
    recentPredictions: [],
  };
}

// POST /auth/login
export async function login(payload: LoginPayload): Promise<AuthResult> {
  if (!payload.email || !payload.password) {
    throw new Error("이메일과 비밀번호를 입력해 주세요.");
  }

  const res = await apiFetch<AuthResponseDto>("/auth/login", {
    method: "POST",
    body: JSON.stringify(payload),
  });

  const claims = decodeAccessToken(res.accessToken);
  return {
    user: buildUser(claims, claims.email.split("@")[0]),
    tokens: { accessToken: res.accessToken, refreshToken: res.refreshToken, expiresIn: res.expiresIn },
  };
}

// POST /auth/register
export async function signup(payload: SignupPayload): Promise<AuthResult> {
  if (!payload.nickname || !payload.email || !payload.password) {
    throw new Error("모든 필드를 입력해 주세요.");
  }

  const res = await apiFetch<AuthResponseDto>("/auth/register", {
    method: "POST",
    body: JSON.stringify(payload),
  });

  const claims = decodeAccessToken(res.accessToken);
  return {
    user: buildUser(claims, payload.nickname),
    tokens: { accessToken: res.accessToken, refreshToken: res.refreshToken, expiresIn: res.expiresIn },
  };
}

// POST /auth/refresh
export async function refresh(refreshToken: string): Promise<AuthTokens> {
  const res = await apiFetch<AuthResponseDto>("/auth/refresh", {
    method: "POST",
    body: JSON.stringify({ refreshToken }),
  });
  return { accessToken: res.accessToken, refreshToken: res.refreshToken, expiresIn: res.expiresIn };
}

let refreshPromise: Promise<string> | null = null;

/**
 * 저장된 refreshToken으로 액세스 토큰을 재발급한다. 동시에 여러 요청에서 401이
 * 발생해도 재발급은 한 번만 보내도록 in-flight Promise를 공유한다(스탬피드 방지).
 */
function refreshAccessToken(): Promise<string> {
  if (!refreshPromise) {
    refreshPromise = (async () => {
      const { refreshToken } = getStoredTokens();
      if (!refreshToken) throw new Error("리프레시 토큰이 없습니다.");
      const tokens = await refresh(refreshToken);
      updateStoredTokens(tokens.accessToken, tokens.refreshToken);
      return tokens.accessToken;
    })().finally(() => {
      refreshPromise = null;
    });
  }
  return refreshPromise;
}

/**
 * 인증이 필요한 요청 전용 wrapper. 액세스 토큰 만료(401)를 받으면 refreshToken으로
 * 한 번 재발급받아 재시도하고, 재발급도 실패하면 로그아웃 상태로 되돌린 뒤 원래 401
 * 에러를 그대로 던진다(호출부는 기존 에러 처리 로직을 그대로 쓸 수 있다).
 */
export async function authFetch<T>(
  path: string,
  accessToken: string | null,
  init?: RequestInit
): Promise<T> {
  const headers = { ...authHeaders(accessToken), ...init?.headers };

  try {
    return await apiFetch<T>(path, { ...init, headers });
  } catch (err) {
    if (!accessToken || !(err instanceof ApiRequestError) || err.status !== 401) {
      throw err;
    }

    try {
      const newAccessToken = await refreshAccessToken();
      return await apiFetch<T>(path, {
        ...init,
        headers: { ...authHeaders(newAccessToken), ...init?.headers },
      });
    } catch {
      clearStoredAuth();
      throw err;
    }
  }
}

export { decodeAccessToken, buildUser };
export type { AccessTokenClaims };
