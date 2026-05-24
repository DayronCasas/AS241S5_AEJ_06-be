# ── Stage 1: Build ──────────────────────────────────────────────────────────
# Chainguard JDK: imagen minimal con 0 CVEs para compilar
FROM cgr.dev/chainguard/jdk:latest AS builder
USER root
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -q
COPY src/ src/
RUN ./mvnw clean package -DskipTests -q

# ── Stage 2: Runtime ─────────────────────────────────────────────────────────
# Chainguard JRE: imagen distroless con 0 CVEs, usuario no-root (65532)
FROM cgr.dev/chainguard/jre:latest
WORKDIR /app
COPY --from=builder /app/target/dayron.casas-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
