FROM maven:3.6.0-jdk-11 as builder

RUN apt-get update -qq; \
	apt-get -y install python3 python3-pip python3-setuptools
RUN pip3 install sklearn json-tricks

WORKDIR /app

COPY pom.xml pom.xml
COPY ida-ws/pom.xml ida-ws/pom.xml
COPY librarian/modules/helpers/pom.xml librarian/modules/helpers/pom.xml
COPY librarian/modules/model/pom.xml librarian/modules/model/pom.xml
COPY librarian/modules/generator/pom.xml librarian/modules/generator/pom.xml

ADD services/ida-ws/rewrite_poms.sh rewrite_poms.sh
RUN bash rewrite_poms.sh

WORKDIR /app/librarian/modules/helpers
RUN mvn dependency:go-offline
WORKDIR /app/librarian/modules/model
RUN mvn dependency:go-offline -DexcludeGroupIds=librarian
WORKDIR /app/librarian/modules/generator
RUN mvn dependency:go-offline -DexcludeGroupIds=librarian

WORKDIR /app
COPY librarian librarian
WORKDIR /app/librarian/modules/helpers
RUN mvn install
WORKDIR /app/librarian/modules/model
RUN mvn install
WORKDIR /app/librarian/modules/generator
RUN mvn install

WORKDIR /app/ida-ws
RUN mvn dependency:go-offline

COPY ida-ws ida-ws

WORKDIR /app/ida-ws
RUN mvn package

FROM tomcat:8.5.37-jre11

EXPOSE 8080
COPY --from=builder /app/ida-ws/target/ida-ws.war /usr/local/tomcat/webapps
