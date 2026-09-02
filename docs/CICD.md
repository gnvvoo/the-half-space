# CI/CD

Backend: GitHub Actions builds and deploys on every push to `develop` that
touches `backend/**` (`.github/workflows/backend.yml`). Frontend: Vercel Git
integration, no GitHub Actions workflow needed.

## Pipeline overview

```
push to develop (backend/**)
  -> test            (JDK 21, ./gradlew test against a postgres service + Testcontainers)
  -> build-and-push  (docker build, tag = git SHA, push to GHCR)
  -> deploy          (SSH to VPS, pull + up -d, prune, health check)
```

- Images are tagged with the commit SHA only. `latest` is never pushed.
  Rollback is done by redeploying a previous SHA (see below).
- The `deploy` job checks whether `VPS_HOST` / `VPS_USER` / `VPS_SSH_KEY`
  secrets are set. If they are not (e.g. before the VPS exists), the job
  skips deploy with a warning instead of failing, so CI stays green.

## Required GitHub Secrets

Set these under Repo Settings -> Secrets and variables -> Actions.

| Secret | Purpose |
|---|---|
| `VPS_HOST` | VPS hostname or IP for SSH deploy |
| `VPS_USER` | Non-root SSH user, must be in the `docker` group |
| `VPS_SSH_KEY` | Private key for a deploy key scoped to this task only (not a personal key) |
| `VPS_SSH_PORT` | Optional, defaults to 22 |
| `VPS_DEPLOY_PATH` | Optional, path to the repo checkout on the VPS (defaults to `~/the-half-space`) |

`GITHUB_TOKEN` (GHCR login/push) is provided automatically by Actions and
does not need to be created manually — the workflow just needs
`packages: write` permission, already set in `backend.yml`.

Any `.env` values the backend needs on the VPS (DB credentials, Redis URL,
OAuth client secrets, API keys) live only in the VPS's own `.env` file next
to `docker-compose.prod.yml` — they are never passed through GitHub Actions
and never committed to the repo.

### Setup steps

1. Provision the VPS (Phase 1): Docker + Docker Compose installed, firewall
   allows 22/80/443, a non-root user in the `docker` group.
2. Generate a dedicated SSH keypair for deploys (do not reuse a personal
   key): `ssh-keygen -t ed25519 -f deploy_key -N ""`. Add `deploy_key.pub`
   to the VPS user's `~/.ssh/authorized_keys`, and paste the private key
   `deploy_key` into the `VPS_SSH_KEY` secret.
3. Clone this repo on the VPS at `VPS_DEPLOY_PATH`, and create
   `docker-compose.prod.yml`'s `.env` file there with production secrets.
4. Add `VPS_HOST`, `VPS_USER`, and any optional secrets above.
5. Push to `develop` and watch the Actions tab — the deploy job should run
   for real once the secrets exist.

## Vercel (frontend)

No workflow file is needed; Vercel's own Git integration handles this.

1. Import the repo into Vercel, set the project root to `frontend/`.
2. Set the environment variable `NEXT_PUBLIC_API_URL` to the backend's
   public URL (e.g. `https://api.<domain>`) for the Production environment,
   and to the appropriate value for Preview if different.
3. Enable auto-deploy on push to `develop` (Vercel's default for the
   production branch) — every push then gets its own deployment without any
   GitHub Actions involvement.
4. When the backend's public URL or CORS-allowed origin changes, update
   `NEXT_PUBLIC_API_URL` in Vercel and the backend's CORS allowed-origins
   config together.

## Rollback procedure

1. Identify the last known-good commit SHA (from the Actions run history or
   `git log`).
2. On the VPS, in the repo directory:
   ```bash
   IMAGE_TAG=<previous-sha> docker compose -f docker-compose.prod.yml pull backend
   IMAGE_TAG=<previous-sha> docker compose -f docker-compose.prod.yml up -d backend
   ```
   or simply re-run `scripts/deploy/deploy.sh <previous-sha>`.
3. **Forward-only migration constraint**: Flyway migrations in this project
   are forward-only — each `V-N` migration must stay backward-compatible
   with the immediately preceding deployed version. Rolling back the
   application to a previous SHA is safe as long as no migration newer than
   that SHA has run against the database.
   - If the SHA you are rolling back to predates a migration that has
     already applied (schema changed in a way the older code can't handle),
     an image rollback alone is **not** sufficient — you must restore the
     database from a `pg_dump` backup taken before that migration ran, then
     deploy the matching application SHA.
   - This is why migrations must be written to tolerate the previous app
     version whenever possible: it keeps plain image rollback viable.

## Secrets hygiene

- Never commit `.env` files, private keys, or credentials to the repository.
- `.env` on the VPS and all GitHub Secrets are the only places production
  secrets live.
- Rotate the deploy SSH key and any leaked credentials immediately if they
  are ever exposed (e.g. printed in logs, committed by mistake).
