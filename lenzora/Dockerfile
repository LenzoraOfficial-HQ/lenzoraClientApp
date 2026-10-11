FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw && ./mvnw -B -DskipTests dependency:go-offline

COPY src ./src
RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /workspace/target/lenzora-0.0.1-SNAPSHOT.jar /app/lenzora.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/lenzora.jar"]
