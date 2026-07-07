import type { User } from "../types";
import { MOCK_USER } from "../data/user";

// GET /users/me
export async function fetchCurrentUser(): Promise<User> {
  return MOCK_USER;
}
