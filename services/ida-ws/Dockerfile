FROM maven:3.6.0-jdk-11 as builder

WORKDIR /app

COPY pom.xml pom.xml
COPY ida-ws/pom.xml ida-ws/pom.xml
COPY librarian/pom.xml librarian/pom.xml
COPY librarian/helpers/pom.xml librarian/helpers/pom.xml
COPY librarian/model/pom.xml librarian/model/pom.xml
COPY librarian/scraper/pom.xml librarian/scraper/pom.xml

WORKDIR /app/librarian
RUN mvn install
WORKDIR /app
RUN mvn dependency:go-offline

COPY librarian librarian
COPY ida-ws ida-ws

RUN mvn install

WORKDIR /app/ida-ws
RUN mvn package

FROM tomcat:8.5.37-jre11

EXPOSE 8080
COPY --from=builder /app/ida-ws/target/ida-ws.war /usr/local/tomcat/webapps
