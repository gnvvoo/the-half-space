# syntax=docker/dockerfile:1

# ---- Build stage -----------------------------------------------------------
FROM gradle:jdk21 AS build
WORKDIR /workspace

# 의존성 캐싱: gradle 설정 파일만 먼저 복사
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./
RUN chmod +x gradlew

COPY src ./src
COPY cli ./cli

RUN ./gradlew bootJar --no-daemon -x test

# ---- Go agent/cli builder stage ---------------------------------------------
# football-agent(github.com/gnvvoo/football-agent)와 football-cli(github.com/gnvvoo/football-cli)는
# 이 백엔드 리포지토리 밖에서 관리되는 별도 Go 프로젝트다. 소스를 이 리포에 커밋하는 대신 이미지
# 빌드 시점에 clone + build한다. ARG로 브랜치/태그/커밋 SHA를 지정해 재현 가능하게 고정할 수 있다.
# 기본값(main)은 각 리포의 main 브랜치를 가리킨다 — football-agent의 비대화형 모드
# (--type/--match/--output, AgentService가 호출하는 형태)는 feat/non-interactive-mode PR이 main에
# 병합된 이후에만 main 기준 빌드로 동작한다. 병합 전에 이 기능을 테스트하려면
# `--build-arg FOOTBALL_AGENT_REF=feat/non-interactive-mode`로 오버라이드할 것.
FROM golang:1.26-alpine AS agent-build
ARG FOOTBALL_AGENT_REF=main
ARG FOOTBALL_CLI_REF=main
RUN apk add --no-cache git

WORKDIR /src/football-agent
RUN git clone https://github.com/gnvvoo/football-agent.git . \
    && git checkout ${FOOTBALL_AGENT_REF}
RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -trimpath -ldflags="-s -w" -o /out/football-agent .

WORKDIR /src/football-cli
RUN git clone https://github.com/gnvvoo/football-cli.git . \
    && git checkout ${FOOTBALL_CLI_REF}
RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -trimpath -ldflags="-s -w" -o /out/football-cli .

# ---- Runtime stage ----------------------------------------------------------
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# 비root 사용자로 실행
RUN groupadd --system spring && useradd --system --gid spring spring

COPY --from=build /workspace/build/libs/*.jar app.jar

# football-agent / football-cli: agent-build 스테이지에서 소스로부터 새로 빌드한 바이너리를 동봉한다.
# backend/cli/football-cli(리포에 커밋된 리눅스 바이너리, 과거 dead code인 CliRunner 검증용)는 git
# 히스토리에는 남겨두되 이미지에는 더 이상 사용하지 않는다.
COPY --from=agent-build /out/football-agent /app/bin/football-agent
COPY --from=agent-build /out/football-cli /app/cli/football-cli
RUN chmod +x /app/bin/football-agent /app/cli/football-cli && chown -R spring:spring /app
USER spring

ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
