FROM amazoncorretto:26-jdk
WORKDIR /workspace
RUN dnf install -y tar gzip && dnf clean all
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -B -DskipTests dependency:go-offline
COPY src ./src
RUN ./mvnw -B -DskipTests package
RUN mkdir -p /app && cp target/*.jar /app/app.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","/app/app.jar"]
