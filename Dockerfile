FROM maven:3.9.6-eclipse-temurin-21-alpine
WORKDIR /opt/app
COPY .mvn .mvn
COPY pom.xml mvnw ./
RUN mvn dependency:resolve
COPY ./src ./src
RUN mvn clean
RUN mvn package

ENTRYPOINT ["java", "-jar", "/opt/app/target/mosaica.jar"]
