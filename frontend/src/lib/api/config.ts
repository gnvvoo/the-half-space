/**
 * Base URL for the Spring Boot REST API backend.
 * Every function in `src/lib/api/*` is written as an async function that
 * currently resolves mock data from `src/lib/data/*`. Once the backend is
 * available, swap the function bodies for `fetch(`${API_BASE_URL}/...`)`
 * calls — call sites elsewhere in the app do not need to change.
 */
export const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080/api/v1";

/**
 * Origin the OAuth2 flow runs against. `/oauth2/authorization/{provider}` and
 * the Spring Security OAuth2 login flow live outside `/api/v1`, so this
 * strips that suffix off `API_BASE_URL` rather than reusing it directly.
 */
export const OAUTH_BASE_URL =
  process.env.NEXT_PUBLIC_OAUTH_BASE_URL ?? API_BASE_URL.replace(/\/api\/v1\/?$/, "");

export type OAuthProvider = "google" | "kakao";

// GET /oauth2/authorization/{registrationId} — browser redirect, not a REST call
export function oauthAuthorizationUrl(provider: OAuthProvider): string {
  return `${OAUTH_BASE_URL}/oauth2/authorization/${provider}`;
}
