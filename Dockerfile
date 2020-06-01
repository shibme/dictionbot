FROM openjdk:11-jre-slim
LABEL maintainer="shibme"
WORKDIR dictionbot
ADD /target/dictionbot-runner.jar /dictionbot/dictionbot-runner.jar
EXPOSE 3428
ENTRYPOINT ["java", "-jar", "dictionbot-runner.jar"]