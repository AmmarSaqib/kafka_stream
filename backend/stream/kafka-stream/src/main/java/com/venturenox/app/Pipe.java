package com.venturenox.app;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.json.*;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.streams.StreamsBuilder;

public class Pipe {

    private String STREAM_APPLICATION_ID;
    private String BOOTSTRAP_SERVER;
    private String KAFKA_USER_NAME;
    private String KAFKA_PASSWORD;

    private String READ_KAFKA_TOPIC;
    private String WRITE_KAFKA_TOPIC;

    private Topology topology;
    private KafkaStreams streams;

    public Pipe() {

        this.STREAM_APPLICATION_ID = "code-event-stream-local";
        this.BOOTSTRAP_SERVER = System.getenv("KAFKA_HOST");
        this.KAFKA_USER_NAME = System.getenv("KAFKA_USER_NAME");
        this.KAFKA_PASSWORD = System.getenv("KAFKA_PASSWORD");

        this.READ_KAFKA_TOPIC = System.getenv("KAFKA_IN_TOPIC");
        this.WRITE_KAFKA_TOPIC = System.getenv("KAFKA_OUT_TOPIC");

        // initializing kakfa streams
        this.init_stream(this.get_props());

    }

    /*
     * The function returns the properties that are required to intialize
     * a secure kafka connection between the broker.
     * 
     * @return the properties object
     */
    private Properties get_props() {
        // setting up properties for Kafka-Stream
        System.out.println("Setting up Properties");

        Properties props = new Properties();

        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, this.BOOTSTRAP_SERVER);
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, this.STREAM_APPLICATION_ID);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        // SASL Config
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 3);
        props.put(StreamsConfig.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG,
                "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + this.KAFKA_USER_NAME
                        + "\" password=\"" + this.KAFKA_PASSWORD + "\";");

        return props;
    }

    /**
     * The function initializes kafka streams KStream object
     * 
     * @param props parameters object for connecting to kafka cluster
     * 
     * @return void
     */
    public void init_stream(Properties props) {

        // setting up the stream
        System.out.println("Setting up Stream");

        // setting up streaming sources
        final StreamsBuilder builder = new StreamsBuilder();

        builder.stream(this.READ_KAFKA_TOPIC)
                .filter((k, v) -> check_event_type(k.toString(), v.toString())) // to filter messages based on some
                                                                                // logic
                .mapValues((v) -> this.process_event(v.toString())) // map messages and prep them for writing
                .peek((k, v) -> System.out.println(v.toString())) // viewing what messages are received
                .to(this.WRITE_KAFKA_TOPIC); // writing the messages to another topic

        // building topology
        this.topology = builder.build();

        // building stream
        this.streams = new KafkaStreams(this.topology, props);

    }

    /*
     * Method to check for certain events.
     * 
     * @param key key for the message
     * 
     * @param value data of the message
     * 
     * @return boolean if the message qualifies the conditions or not
     */
    private boolean check_event_type(String key, String value) {
        return true;
    }

    /*
     * The function which is called to process the message
     * 
     * @param event this string contains a stringified JSON event with the
     * relevant Code event to be processed
     * 
     * @return message to be written to the topic
     */
    private String process_event(String event) {

        return "{'hello': 'world'}";

    }

    /*
     * The function prints out the topology description defined for the
     * kafka stream
     */
    public void describe() {
        System.out.println(this.topology.describe());
    }

    /*
     * The function initiates the kafka stream
     */
    public void start() {
        try {
            System.out.println("starting server");
            System.out.println("---------------------------------------------------------------------------");

            this.streams.cleanUp();
            this.streams.start();
        } catch (Exception e) {
            System.out.println("In catch" + e.getMessage());
            System.exit(1);
        }
    }
}
