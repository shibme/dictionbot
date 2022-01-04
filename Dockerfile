FROM openjdk:11-jre-slim
LABEL maintainer="shibme"
WORKDIR app
ADD /target/dictionbot-jar-with-dependencies.jar /app/dictionbot.jar
EXPOSE 3428
ENTRYPOINT ["java", "-jar", "dictionbot.jar"]