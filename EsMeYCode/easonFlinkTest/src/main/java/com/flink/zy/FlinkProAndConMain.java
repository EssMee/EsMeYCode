package com.flink.zy;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class FlinkProAndConMain {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        FlinkProducerThread fpt = new FlinkProducerThread("flink-java-producer", env,"D:\\easonFlinkTest\\src\\main\\resources\\session.txt");
        fpt.run();
        FlinkConsumerThread fct = new FlinkConsumerThread("flink-java-consumer", env);
        fct.run();
    }
}
