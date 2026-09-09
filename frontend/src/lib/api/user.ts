import { apiFetch, authHeaders } from "./http";
import type { User } from "../types";

interface UserProfileResponseDto {
  id: number;
  email: string;
  nickname: string;
  role: string;
  createdAt: string;
}

function mapUserProfile(raw: UserProfileResponseDto): User {
  return {
    id: String(raw.id),
    nickname: raw.nickname,
    email: raw.email,
    // 백엔드 GET /users/me가 아직 제공하지 않는 필드들 — 응답에 추가되면 채운다.
    favoriteTeams: [],
    predictionAccuracy: 0,
    totalPredictions: 0,
    correctPredictions: 0,
    recentPredictions: [],
  };
}

// GET /users/me
export async function fetchCurrentUser(accessToken: string): Promise<User> {
  const res = await apiFetch<UserProfileResponseDto>("/users/me", {
    headers: authHeaders(accessToken),
  });
  return mapUserProfile(res);
}
