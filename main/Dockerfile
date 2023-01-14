FROM maven:3 as BUILD_IMAGE
RUN mkdir -p /workspace/main
WORKDIR /workspace
COPY pom.xml /workspace
COPY main /workspace/main
RUN mvn package -f /workspace/main/pom.xml

FROM openjdk:17
COPY --from=BUILD_IMAGE /workspace/main/target/app.jar .
ENTRYPOINT ["java","-jar","/app.jar"]