FROM amazoncorretto:26-jdk
LABEL maintainer="LechTheMitch" description="Notification Service Microservice."

RUN dnf install -y tar gzip && dnf clean all

WORKDIR /workspace
COPY mvnw pom.xml ./
COPY .mvn .mvn

RUN chmod +x mvnw \
 && ./mvnw -B -DskipTests dependency:go-offline
COPY src ./src

RUN ./mvnw -B -DskipTests package
RUN mkdir -p /app
RUN cp target/*.jar /app/app.jar

EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
