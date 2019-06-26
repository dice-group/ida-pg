FROM maven:3.6.0-jdk-11 as builder

WORKDIR /app

COPY pom.xml pom.xml
COPY ida-ws/pom.xml ida-ws/pom.xml
COPY librarian/pom.xml librarian/pom.xml
COPY librarian/modules/helpers/pom.xml librarian/helpers/pom.xml
COPY librarian/modules/model/pom.xml librarian/model/pom.xml
COPY librarian/modules/scraper/pom.xml librarian/scraper/pom.xml
COPY librarian/modules/generator/pom.xml librarian/generator/pom.xml

WORKDIR /app/librarian
RUN mvn install
WORKDIR /app
RUN mvn dependency:go-offline

COPY librarian librarian
RUN mvn install
COPY ida-ws ida-ws

WORKDIR /app/ida-ws
RUN mvn package

FROM tomcat:8.5.37-jre11

EXPOSE 8080
COPY --from=builder /app/ida-ws/target/ida-ws.war /usr/local/tomcat/webapps
