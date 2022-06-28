FROM openjdk:11-oracle

ARG PROJECT_HOME=/app
ARG JAR_FILE=main-application/target/main-application-*.jar

RUN mkdir -p ${PROJECT_HOME}

COPY ${JAR_FILE} ${PROJECT_HOME}/intruder.jar

ENTRYPOINT ["java","-jar","/app/intruder.jar"]
