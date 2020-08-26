package com.flink.zy;


import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;

import java.util.Properties;


public class FlinkProducerThread implements Runnable{
    private String threadName;
    private StreamExecutionEnvironment environment;
    private String fileName;



    public FlinkProducerThread(String name, StreamExecutionEnvironment env, String fName) {
        threadName = name;
        environment = env;
        fileName = fName;
    }
    @Override
    public void run() {
        try {
            System.out.println("Job " + threadName + " started~~");
            DataStreamSource<String> text = environment.addSource(new MySource(fileName)).setParallelism(1);
            Properties props = new Properties();
            props.setProperty("--broker-list", "localhost:9092");
            FlinkKafkaProducer010<String> producer010 = new FlinkKafkaProducer010<String>("zy-test-topic", new SimpleStringSchema(), props);
            text.addSink(producer010);
            environment.execute();
        } catch (RuntimeException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
