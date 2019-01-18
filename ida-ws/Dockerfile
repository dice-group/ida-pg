FROM maven:3.6.0-jdk-11 as builder

WORKDIR /app/ida-ws

COPY pom.xml /app/ida-ws/pom.xml
RUN mvn dependency:resolve

COPY src /app/ida-ws/src

RUN mvn package

FROM tomcat:8.5.37-jre11
COPY --from=builder /app/ida-ws/target/ida-ws.war /usr/local/tomcat/webapps
