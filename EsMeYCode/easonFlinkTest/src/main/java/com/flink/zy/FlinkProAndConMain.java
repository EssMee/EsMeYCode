package com.flink.zy;


import com.flink.zy.util.ConfigFileParse;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;

import java.util.*;

public class FlinkProAndConMain {
    public static void main(String[] args) throws Exception {
        Properties properties = getParameter(args);
        StreamExecutionEnvironment env = getStreamEnvironment(properties);
        DataStream<String> sessionStream = getKafkaStream(env, properties);
        sessionStream.print();
        env.execute("test!~");

//        FlinkConsumerThread fct = new FlinkConsumerThread("flink-java-consumer", env);
//        fct.run();
    }

    private static Properties getParameter(String[] args) {
        parameterJudge(args);
        ConfigFileParse config = new ConfigFileParse(args[0]);
        return config.prop;
    }

    private static void parameterJudge(String[] args) {
        if (args.length != 1) {
            System.out.println("the length of parameter is not one! check");
            System.exit(1);
        }
    }

    private static StreamExecutionEnvironment getStreamEnvironment(Properties props) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        Set<Object> keys = props.keySet();
        Iterator<Object> it = keys.iterator();
        Map<String, String> confMap = new HashMap<>();
        while (it.hasNext()) {
            Object key = it.next();
            String value = String.valueOf(props.get(key.toString()));
            confMap.put(key.toString(), value);
        }
        ParameterTool params = ParameterTool.fromMap(confMap);
        ExecutionConfig executionConfig = env.getConfig();
        executionConfig.disableSysoutLogging();
        executionConfig.setGlobalJobParameters(params);
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(10, 10000L));
        return env;
    }

    private static DataStream<String> getKafkaStream(StreamExecutionEnvironment env, Properties properties) throws Exception {
        String topic = properties.getProperty("topic");
        Properties flinkKafkaConsumerProps = new Properties();
        String group_id = "group.id";
        String bootStrapServersKey = "bootstrap.servers";
        flinkKafkaConsumerProps.setProperty(group_id, properties.getProperty(group_id));
        flinkKafkaConsumerProps.setProperty(bootStrapServersKey, properties.getProperty(bootStrapServersKey));
        FlinkKafkaConsumer010<String> flinkKafkaConsumer010 = new FlinkKafkaConsumer010<>(topic, new SimpleStringSchema(), flinkKafkaConsumerProps);
        flinkKafkaConsumer010.setStartFromLatest();
        return env.addSource(flinkKafkaConsumer010);

    }

}
