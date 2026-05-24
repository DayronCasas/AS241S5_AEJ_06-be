# ── Stage 1: Build ──────────────────────────────────────────────────────────
# eclipse-temurin:17-jdk-alpine es compatible con Lombok + Java 17
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -q
COPY src/ src/
RUN ./mvnw clean package -DskipTests -q

# ── Stage 2: Runtime ─────────────────────────────────────────────────────────
# Alpine actualizado al último parche: elimina CVEs del OS
FROM eclipse-temurin:17-jre-alpine
# Actualizar todos los paquetes del OS para parchear CVEs conocidos
RUN apk update && apk upgrade --no-cache && rm -rf /var/cache/apk/*
# Crear usuario no-root para reducir superficie de ataque
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
COPY --from=builder /app/target/dayron.casas-0.0.1-SNAPSHOT.jar app.jar
RUN chown appuser:appgroup app.jar
USER appuser
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
