#!/usr/bin/env bash
# Deploy the backend service on the VPS.
#
# Run from the repo root on the VPS (the CI workflow SSHes in and calls this
# script). Pulls the image tagged with the given SHA, recreates the backend
# container, prunes dangling images, then health-checks /actuator/health
# with retries. Exits non-zero on any failure so the CI SSH step fails loudly.
#
# Usage: deploy.sh <image-tag>
#   image-tag  Git commit SHA used to tag the backend image in GHCR.
#              Required. Falls back to $IMAGE_TAG if not passed positionally.

set -euo pipefail

IMAGE_TAG="${1:-${IMAGE_TAG:-}}"
COMPOSE_FILE="${COMPOSE_FILE:-docker-compose.prod.yml}"
HEALTH_URL="${HEALTH_URL:-http://localhost:8080/actuator/health}"
HEALTH_RETRIES="${HEALTH_RETRIES:-10}"
HEALTH_DELAY_SECONDS="${HEALTH_DELAY_SECONDS:-5}"

if [ -z "$IMAGE_TAG" ]; then
  echo "ERROR: image tag not provided. Usage: deploy.sh <image-tag>" >&2
  exit 1
fi

export IMAGE_TAG

echo "==> Deploying backend image tag: ${IMAGE_TAG}"

echo "==> Pulling image"
docker compose -f "$COMPOSE_FILE" pull backend

echo "==> Recreating backend container"
docker compose -f "$COMPOSE_FILE" up -d backend

echo "==> Pruning dangling images"
docker image prune -f

echo "==> Health check: ${HEALTH_URL}"
attempt=1
until curl --fail --silent --show-error "$HEALTH_URL" > /dev/null; do
  if [ "$attempt" -ge "$HEALTH_RETRIES" ]; then
    echo "ERROR: backend failed health check after ${HEALTH_RETRIES} attempts" >&2
    docker compose -f "$COMPOSE_FILE" logs --tail=100 backend >&2 || true
    exit 1
  fi
  echo "    attempt ${attempt}/${HEALTH_RETRIES} failed, retrying in ${HEALTH_DELAY_SECONDS}s..."
  attempt=$((attempt + 1))
  sleep "$HEALTH_DELAY_SECONDS"
done

echo "==> Deploy successful: backend is healthy on tag ${IMAGE_TAG}"
