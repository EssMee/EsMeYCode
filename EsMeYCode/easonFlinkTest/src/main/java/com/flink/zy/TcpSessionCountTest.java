package com.flink.zy;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import scala.Option;
import scala.collection.immutable.Stream;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;


public class TcpSessionCountTest {
    public static Properties props = new Properties();
    public static StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    private String threadName;
    volatile static boolean flag = true;

    public static void main(String[] args) throws Exception {
        try {
            props.setProperty("group.id", "tt");
            props.setProperty("bootstrap.servers", "3.2.12.36:30091, 3.2.12.37:30092, 3.2.12.38:30093");
            props.put("key.deserializer", StringDeserializer.class.getName());
            props.put("value.deserializer", StringDeserializer.class.getName());
            AtomicLong counter = new AtomicLong();
            for (int i = 0; i < 12; i++) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        KafkaConsumer<String, String> KafkaConsumer010 = new KafkaConsumer<String, String>(props);
                        KafkaConsumer010.subscribe(Arrays.asList("zy-test-topic"));
                        while (flag) {
                            ConsumerRecords<String, String> consumerRecords = KafkaConsumer010.poll(10);
                            consumerRecords.forEach(re -> {
                                        counter.addAndGet(1);
                                        int l = re.value().split(";", -1).length;
                                        if (l != 66) {
                                            System.out.println("stream error" + re.value());
                                            throw new RuntimeException();
                                        }
                                    }


                            );
                        }
                        KafkaConsumer010.close();
                    }
                }).start();
            }
            long beginTime = System.currentTimeMillis();
            System.out.println("beginTime is " + new Date(beginTime));
            while (flag) {
                long current = System.currentTimeMillis();
                long sleepTime = 30 * 1000 - current % (30 * 1000);
                Thread.sleep(sleepTime);
                long printTime = current + sleepTime;
                System.out.println(new Date(printTime) + " total msgs from tcp-session-topic in 30s: " + counter.get());
                counter.set(0L);
                if (current - beginTime >= 60 * 60 * 1000) {
                    flag = false;
                    System.out.println("endTime is " + new Date(current));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }

    }


}
