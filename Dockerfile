# ===================================================================
# KARMA SUTRA™ — Multi-Stage Production Dockerfile
# "The universe has a ticketing system."
# ===================================================================

# STAGE 1: Frontend Build (Three.js + React + Vite)
FROM node:20-alpine AS frontend-builder
WORKDIR /app/cosmic-3d
COPY cosmic-3d/package*.json ./
RUN npm ci
COPY cosmic-3d/ ./
RUN npm run build

# STAGE 2: Java Backend Package (Maven + Temurin JDK 21)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS backend-builder
WORKDIR /app
COPY pom.xml ./
# Cache dependencies
RUN mvn dependency:go-offline -B

COPY src/ ./src/
# Copy built frontend assets into web resource directory
COPY --from=frontend-builder /app/cosmic-3d/dist/ ./src/main/resources/web/

RUN mvn package -DskipTests -B

# STAGE 3: Production Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Add unprivileged system user for security
RUN addgroup -S karmasutra && adduser -S karmasutra -G karmasutra

# Copy uber-jar
COPY --from=backend-builder /app/target/karma-sutra-app-1.0.0.jar ./karma-sutra-app.jar

# Set ownership
RUN chown -R karmasutra:karmasutra /app
USER karmasutra

EXPOSE 8108 5173

ENV JAVA_OPTS="-Xms256m -Xmx1024m -XX:+UseG1GC"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar karma-sutra-app.jar"]
