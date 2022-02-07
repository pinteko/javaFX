#FROM bellsoft/liberica-openjdk-debian:11.0.12-7 as builder
FROM maven:3.8.4-eclipse-temurin-17 as builder
WORKDIR /app

COPY ../pom.xml .
COPY src src

RUN mvn clean package



FROM adoptopenjdk/openjdk11:jre-11.0.11_9-alpine as production
LABEL maintainer="Kirill Korsuk"
VOLUME /tmp
WORKDIR /app
#
COPY --from=builder /app/target/*jar /app/
RUN mkdir /app/log
RUN mkdir /app/config
ENV JAVA_TOOL_OPTIONS=""

ENTRYPOINT ["sh", "-c", "java  ${JAVA_TOOL_OPTIONS} -jar chat-server-1.0-SNAPSHOT-jar-with-dependencies.jar"]