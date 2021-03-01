package flink.h3c.com.AnyTest.Main;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.sql.Timestamp;
import java.util.Properties;
/*
处理迟到数据
使能每3min一次的checkpoint
采用事件事件，用数据的时间戳做水印。*/
public class ProcessLateData {
    static class SensorReading {
        String sensorId;
        Long timestamp;
        Double temperature;

        public SensorReading(String id, Long time, Double t) {
            this.sensorId = id;
            this.timestamp = time;
            this.temperature = t;
        }
    }

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.setProperty("bootstarp.servers", "192.168.129.155:50091");
        properties.setProperty("group.id", "zyTest");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        StreamExecutionEnvironment env =
            StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        /**************************************************************************************/
        env.enableCheckpointing(3 * 60 * 1000L, CheckpointingMode.EXACTLY_ONCE);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        /**************************************************************************************/
        DataStream<SensorReading> stream =
            env.addSource(new FlinkKafkaConsumer010<String>("sensor", new SimpleStringSchema(), properties))
                .map(data -> {
                    String[] dataArr = data.split(",", -1);
                    return new SensorReading(dataArr[0].trim(), Long.valueOf(dataArr[1].trim()),
                        Double.valueOf(dataArr[2].trim()));
                }).assignTimestampsAndWatermarks(
                new BoundedOutOfOrdernessTimestampExtractor<SensorReading>(Time.seconds(5)) {
                    @Override public long extractTimestamp(SensorReading element) {
                        return element.timestamp * 1000L;
                    }
                });
        SingleOutputStreamOperator<String> sideTestStream =
            stream.keyBy(s -> s.sensorId).timeWindow(Time.seconds(10)).sideOutputLateData(new OutputTag<>("late_data"))
                .process(new ProcessWindowFunction<SensorReading, String, String, TimeWindow>() {
                    @Override public void process(String s, Context context, Iterable<SensorReading> elements,
                        Collector<String> out) {
                        int count = 0;
                        while (elements.iterator().hasNext()) {
                            count++;
                        }
                        out.collect("Window End: " + new Timestamp(context.window().getEnd()) + " , count: " + count);
                    }
                });

        DataStream<String> lateStream = sideTestStream.getSideOutput(new OutputTag<>("late_data"));
        sideTestStream.print("主数据");
        lateStream.print("迟到数据");
        env.execute("迟到数据Test");
    }
}
