FROM maven:3-openjdk-11
WORKDIR /ws
COPY src /ws/src
COPY pom.xml /ws/
RUN mvn clean install

FROM openjdk:11-jre-slim
LABEL maintainer="shibme"
RUN mkdir -p /app
COPY --from=build-env /ws/target/dictionbot-runner.jar /app/dictionbot.jar
WORKDIR dictionbot
EXPOSE 3428
ENTRYPOINT ["java", "-jar", "/app/dictionbot.jar"]