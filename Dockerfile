FROM adoptopenjdk/openjdk11-openj9:jdk-11.0.1.13-alpine-slim
COPY build/libs/account-api-*-all.jar account-api.jar
EXPOSE 8081
CMD java -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -jar account-api.jar
