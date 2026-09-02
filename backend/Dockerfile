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

# ---- Runtime stage ----------------------------------------------------------
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# 비root 사용자로 실행
RUN groupadd --system spring && useradd --system --gid spring spring

COPY --from=build /workspace/build/libs/*.jar app.jar

# football-cli (Go CLI, 리포지토리에 리눅스 바이너리로 커밋되어 있음) 동봉
COPY --from=build /workspace/cli/football-cli /app/cli/football-cli
RUN chmod +x /app/cli/football-cli

# TODO(football-agent): app.agent.binary-path(./bin/football-agent)에 해당하는 Go CLI 바이너리가
# 아직 이 리포지토리에 커밋되어 있지 않다 (backend/cli/football-cli와 달리 backend/bin/football-agent
# 경로가 존재하지 않음). 패키징 방식 결정 필요:
#   (a) 이미지 동봉: football-cli처럼 리눅스 바이너리를 리포에 커밋하고 아래 COPY 주석 해제, 또는
#       CI에서 함께 빌드하는 별도 스테이지 추가
#   (b) 사이드카: 별도 컨테이너/프로세스로 분리하고 이 앱은 그 엔드포인트를 호출하도록 AgentService 변경
# 바이너리가 없으면 AgentService.runAgent() 호출 시 ProcessBuilder가 실패한다 (AI 프리뷰/리뷰 생성 불가).
# 결정 및 실제 반영은 Phase 1.1 실행 시점(football-agent 소스/빌드 산출물 확보 후)으로 미룬다.
# COPY --from=build /workspace/bin/football-agent /app/bin/football-agent
# RUN chmod +x /app/bin/football-agent

RUN mkdir -p /app/bin && chown -R spring:spring /app
USER spring

ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
