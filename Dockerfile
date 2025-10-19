# -------- Build stage --------
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

# Leverage Docker layer caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw
RUN ./mvnw -q -B -DskipTests dependency:go-offline

# Copy sources and build
COPY src src
RUN ./mvnw -q -B -DskipTests package

# Find the re-packaged boot jar (single jar in target)
RUN ls -la target && \
    JAR_FILE=$(ls target | grep '.jar$' | head -n 1) && \
    echo "JAR_FILE=$JAR_FILE" && \
    cp target/$JAR_FILE app.jar

# -------- Runtime stage --------
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/app.jar /app/app.jar

# Non-root user for security
RUN useradd -u 10001 -r -s /sbin/nologin appuser && chown appuser:appuser /app/app.jar
USER appuser

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
