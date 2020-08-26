package com.flink.zy;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;


import java.util.Date;
import java.util.Properties;

public class FlinkConsumerThread implements Runnable{
    private String threadName;
    private StreamExecutionEnvironment environment;
    public FlinkConsumerThread(String name, StreamExecutionEnvironment env) {
        threadName = name;
        environment = env;
    }


    @Override
    public void run() {
        try {
            Properties pros = new Properties();
            pros.setProperty("broker-list", "localhost:9092");
            pros.setProperty("zookeeper.connect","localhost:2181");
            FlinkKafkaConsumer010<String> consumer010 = new FlinkKafkaConsumer010<String>("zy-test-topic", new SimpleStringSchema(), pros);
            consumer010.setStartFromLatest();
            DataStream<String> res = environment.addSource(consumer010);
            System.out.printf("Current Time: " + new Date(), res.print());
            environment.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
