import { MOCK_USER } from "../data/user";
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

// POST /auth/login
export async function login(payload: LoginPayload): Promise<User> {
  if (!payload.email || !payload.password) {
    throw new Error("이메일과 비밀번호를 입력해 주세요.");
  }
  return MOCK_USER;
}

// POST /auth/signup
export async function signup(payload: SignupPayload): Promise<User> {
  if (!payload.nickname || !payload.email || !payload.password) {
    throw new Error("모든 필드를 입력해 주세요.");
  }
  return { ...MOCK_USER, nickname: payload.nickname, email: payload.email };
}
