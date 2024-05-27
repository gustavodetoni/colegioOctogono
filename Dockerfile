FROM maven:3.8.3-openjdk-17 AS build
COPY . .
RUN mvn clean install

FROM eclipse-temurin:17-jdk
COPY --from=build /target/colegioOctogono-0.0.1-SNAPSHOT.jar demo.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","demo.jar"]