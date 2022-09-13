# Kafka Stream Java Boilerplate

## Multi-Stage Docker file

```
FROM maven:3.6-jdk-11 AS build  or FROM maven:3.6.0-jdk-11-slim AS build
COPY kafka-stream/src usr/src/app/src  
COPY kafka-stream/pom.xml /usr/src/app  
RUN mvn -f /usr/src/app/pom.xml clean package

FROM gcr.io/distroless/java  
COPY --from=build /usr/src/app/target/kafka_stream.jar /usr/app/kafka_stream.jar  
EXPOSE 3090  
ENTRYPOINT ["java","-jar","/usr/app/kafka_stream.jar"]  
```

The following command is pulling the maven slim version and the jdk is also specified and the version of Maven and JDK is also specified here.

```FROM maven:3.6-jdk-11 AS build```

The pom.xml and the source ```.java``` files are copied to the container. Then the app is build via maven.

The ```.jar``` build is then forwared to the next step where the distroless java is used to run the app.

## Creating Maven App
The following Maven command can be used to create a project:
```
mvn -B archetype:generate -DgroupId=com.venturenox.app -DartifactId=kafka-stream -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4
```

```-DartifactId``` will be the name of the name of the app.
```-DarchetypeArtifactId``` specifies some templates for the maven application `maven-archetype-quickstart` is for this project.

## Setting up Docker Build Kit
Run the following to set environment variables:
```
export COMPOSE_DOCKER_CLI_BUILD=1
export DOCKER_BUILDKIT=1
```

## Self deployed kafka commands
- Create Topics:
    ```
    kafka-topics.sh --create --bootstrap-server localhost:9092 --topic mytopic --partitions 3 --replication-factor 3
    ```

- List Topics:
    ```
    kafka-topics.sh --list --bootstrap-server localhost:9092
    ```

- Start Producer Client:
    ```
    kafka-console-producer.sh --broker-list 127.0.0.1:9093 --topic <topic name>
    ```
- Start Consumer Client:
    ```
    kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9093 --topic <topic name> --from-beginning
    ```

## Lambda Not Supported on Source 7 error:
had to the change the pom.xml properties for `maven.compiler.source` and `maven.compiler.target` to 11 to support Java 11. For Java 8 we will use 1.8 and for Java 9 and plus the exact java version.

## Connecting to Kafka Streams SASL References
- https://docs.confluent.io/cloud/current/cp-component/streams-cloud-config.html

- **Make sure to not use `System.exit(0)` in the code**: it causes the program to exit immediately after execution. 

## Streaming from a Multi Partition Topic (Observations):
- on exact data streaming from one topic to another: i have observed that the target topic receives messages from both the partitions. so yayyy