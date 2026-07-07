import { API_BASE_URL } from "./config";

interface ApiSuccess<T> {
  data: T;
  timestamp: string;
}

interface ApiErrorBody {
  code: string;
  message: string;
  timestamp: string;
}

export class ApiRequestError extends Error {
  code: string;
  status: number;

  constructor(code: string, message: string, status: number) {
    super(message);
    this.name = "ApiRequestError";
    this.code = code;
    this.status = status;
  }
}

/**
 * Calls the Spring Boot backend and unwraps the `{ data, timestamp }` /
 * `{ code, message, timestamp }` envelope described in backend/docs/API.md.
 */
export async function apiFetch<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${API_BASE_URL}${path}`, {
    ...init,
    headers: { "Content-Type": "application/json", ...init?.headers },
  });

  const body = await res.json().catch(() => null);

  if (!res.ok) {
    const err = body as ApiErrorBody | null;
    throw new ApiRequestError(
      err?.code ?? "UNKNOWN",
      err?.message ?? `요청에 실패했습니다 (${res.status})`,
      res.status
    );
  }

  return (body as ApiSuccess<T>).data;
}
