package com.flink.zy;

import com.flink.zy.util.Item;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SplitStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import scala.Tuple3;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ZYFlinkProgram {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.setParallelism(2);
        env.enableCheckpointing(10 * 60 *1000L, CheckpointingMode.EXACTLY_ONCE, false);
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.33.130:9092");
        properties.setProperty("group.id","zy1");
        FlinkKafkaConsumer<String> flinkKafkaConsumer = new FlinkKafkaConsumer<String>("test1", new SimpleStringSchema(), properties);
        flinkKafkaConsumer.setStartFromEarliest();
        flinkKafkaConsumer.assignTimestampsAndWatermarks(WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(2)));
        DataStream<String>  originalStream = env.addSource(flinkKafkaConsumer);
        // 这一步其实是多此一举，防止以后同样的主题里会消费到其他类型的消息也没准儿。
        SplitStream<String> splitedStream = originalStream.split((OutputSelector<String>) value -> {
            List<String> select = new ArrayList<>();
            if (value.split("@", -1).length == 4) {
                select.add("session");
            }
            return select;
        });
        DataStream<Item> itemStream = splitedStream.select("session").map(data -> {
            String[] temp = data.split("@",-1);
            Item item = new Item(Timestamp.valueOf(temp[0]) ,temp[1],Integer.valueOf(temp[2]),Integer.valueOf(temp[3]));
            return item;
        });
        itemStream.keyBy(s -> s.getName())
                .timeWindow(Time.minutes(1));
//                .process(new KeyedProcessFunction<String, Item, Tuple3<String, Integer, Timestamp>>() {
//
//                    @Override
//                    public void processElement(Item value, Context ctx, Collector<Tuple3<String, Integer, Timestamp>> out) throws Exception {
//                        String currentItem = ctx.getCurrentKey();
//
//                    }
//                }).setParallelism(4).print();
        itemStream.print("item Stream");
        env.execute();
    }
}
