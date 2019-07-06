FROM maven:3.6.1-jdk-11 as builder

RUN apt-get update -qq; \
	apt-get -y --no-install-recommends install python3=3.5.3-1 python3-pip=9.0.1-2+deb9u1 python3-setuptools=33.1.1-1
RUN pip3 install scikit-learn==0.21.2 json-tricks==3.13.1

WORKDIR /app

COPY pom.xml pom.xml
COPY ida-ws/pom.xml ida-ws/pom.xml
COPY librarian/modules/helpers/pom.xml librarian/modules/helpers/pom.xml
COPY librarian/modules/model/pom.xml librarian/modules/model/pom.xml
COPY librarian/modules/generator/pom.xml librarian/modules/generator/pom.xml

COPY services/ida-ws/rewrite_poms.sh rewrite_poms.sh
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
