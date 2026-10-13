# Build context is the repository root; the application itself lives in lenzora/.
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

# The wrapper script downloads Maven with curl and extracts it with unzip;
# the base image ships neither.
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl unzip \
    && rm -rf /var/lib/apt/lists/*

COPY lenzora/.mvn .mvn
COPY lenzora/mvnw lenzora/pom.xml ./
RUN chmod +x ./mvnw && ./mvnw -B -DskipTests dependency:go-offline

COPY lenzora/src ./src
RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /workspace/target/lenzora-0.0.1-SNAPSHOT.jar /app/lenzora.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/lenzora.jar"]
