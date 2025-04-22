FROM openjdk:21-jdk-slim AS builder

WORKDIR /build

RUN apt-get update && apt-get install -y maven

COPY pom.xml .
COPY src ./src

RUN mvn clean package


FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=builder /build/target/analyse-ventes-1.0-SNAPSHOT.jar ./app.jar

CMD ["java", "-jar", "app.jar"]