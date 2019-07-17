FROM alpine
LABEL maintainer="shibme"
RUN mkdir dictionbot-workspace
WORKDIR dictionbot-workspace
RUN apk update && apk upgrade
RUN apk add openjdk8-jre
ADD /target/dictionbot-runner.jar /dictionbot-workspace/dictionbot-runner.jar
CMD ["java", "-jar", "dictionbot-runner.jar"]