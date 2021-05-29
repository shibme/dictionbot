FROM openjdk:11-jre-slim
LABEL maintainer="shibme"
WORKDIR app
ADD /target/dictionbot-runner.jar /app/dictionbot.jar
EXPOSE 3428
ENTRYPOINT ["java", "-jar", "dictionbot.jar"]