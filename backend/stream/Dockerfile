    FROM maven:3.8.6-jdk-11 AS build  

    WORKDIR /usr/src/app 
    COPY kafka-stream/pom.xml .
    RUN mvn dependency:resolve -U

    # COPY kafka-stream/src /usr/src/app/src/
    COPY kafka-stream /usr/src/app
    RUN --mount=type=cache,target=/root/.m2 mvn -f /usr/src/app/pom.xml clean compile assembly:single

    FROM gcr.io/distroless/java11-debian11
    COPY --from=build /usr/src/app/src/resources/log4j.properties /usr/app/log4j.properties
    COPY --from=build /usr/src/app/target/kafka-stream-1.0-SNAPSHOT-jar-with-dependencies.jar /usr/app/kafka-stream-1.0-SNAPSHOT-jar-with-dependencies.jar  
    ENTRYPOINT ["java","-jar","/usr/app/kafka-stream-1.0-SNAPSHOT-jar-with-dependencies.jar"]