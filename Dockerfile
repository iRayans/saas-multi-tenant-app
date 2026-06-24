FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/saas-multi-tenant-app-0.0.1-SNAPSHOT.jar app.jar
COPY --from=build /app/target/classes/certs/ certs/
EXPOSE 8080
LABEL authors="rayan"
CMD ["java", "-jar", "app.jar"]