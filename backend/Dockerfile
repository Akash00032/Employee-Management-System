
# Stage 1: Build Spring Boot application
FROM maven:3.9.11-eclipse-temurin-17 AS build

WORKDIR /workspace

COPY pom.xml ./

RUN mvn --batch-mode --no-transfer-progress dependency:go-offline

COPY backend/src ./backend/src

RUN mvn --batch-mode --no-transfer-progress -DskipTests package \
    && artifact="$(find target -maxdepth 1 -type f -name '*.jar' ! -name '*.jar.original' -print -quit)" \
    && test -n "$artifact" \
    && cp "$artifact" /workspace/app.jar

# Stage 2: Production runtime
FROM eclipse-temurin:17-jre-jammy

RUN apt-get update \
    && apt-get install --yes --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system --gid 10001 ems \
    && useradd --system --uid 10001 --gid ems --no-create-home ems

WORKDIR /app

COPY --from=build --chown=ems:ems /workspace/app.jar /app/ems.jar

ENV JAVA_TOOL_OPTIONS="-XX:InitialRAMPercentage=25.0 -XX:MaxRAMPercentage=75.0"

USER ems

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD curl --fail --silent http://127.0.0.1:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/ems.jar"]
