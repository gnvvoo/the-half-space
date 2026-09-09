import { apiFetch } from "./http";
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

export { decodeAccessToken, buildUser };
export type { AccessTokenClaims };
