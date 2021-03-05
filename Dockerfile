FROM openjdk:11
ARG JAR_FILE=build/libs/*all.jar
COPY ${JAR_FILE} finance.jar
ENTRYPOINT ["java","-Xmx512m","-jar","/finance.jar"]