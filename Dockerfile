FROM alpine
LABEL maintainer="shibme"
RUN mkdir dictionbot-workspace
WORKDIR dictionbot-workspace
RUN apk add --no-cache openjdk8-jre
ADD /target/dictionbot-runner.jar /dictionbot-workspace/dictionbot-runner.jar
EXPOSE 3428
CMD ["java", "-jar", "dictionbot-runner.jar"]