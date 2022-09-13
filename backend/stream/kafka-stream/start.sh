# mvn dependency:resolve
# # mvn assembly:assembly -DdescriptorId=jar-with-dependencies
mvn clean compile assembly:single
# # mvn clean package
# java -jar target/kafka-stream-1.0-SNAPSHOT.jar
# chmod +x target/kafka-stream-1.0-SNAPSHOT-jar-with-dependencies.jar
java -jar target/kafka-stream-1.0-SNAPSHOT-jar-with-dependencies.jar