FROM maven:3 as BUILD_IMAGE
RUN mkdir -p /workspace/worker
WORKDIR /workspace
COPY pom.xml /workspace
COPY worker /workspace/worker
RUN mvn package -f /workspace/worker/pom.xml

FROM ghcr.io/graalvm/graalvm-ce:ol9-java17-22.3.0
RUN gu install js
COPY --from=BUILD_IMAGE /workspace/worker/target/app.jar .
ENTRYPOINT ["java","-jar","./app.jar"]