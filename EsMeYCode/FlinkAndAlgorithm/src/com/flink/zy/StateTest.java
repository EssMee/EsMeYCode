package com.flink.zy;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.apache.flink.clients;

public class StateTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.enableCheckpointing(1 * 60 * 1000L, CheckpointingMode.EXACTLY_ONCE);
        DataStream<Tuple2<Long, Long>> source = env.fromElements(Tuple2.of(1L, 3L), Tuple2.of(1L, 5L), Tuple2.of(1L, 7L), Tuple2.of(1L, 4L),
                Tuple2.of(1L, 2L))
                .keyBy(0)
                .flatMap(new RichFlatMapFunction<Tuple2<Long, Long>, Tuple2<Long, Long>>() {
                    private transient ValueState<Tuple2<Long, Long>> sum;
                    private transient Counter counter;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        super.open(parameters);
                        ValueStateDescriptor<Tuple2<Long, Long>> descriptor = new ValueStateDescriptor<Tuple2<Long, Long>>("sum", TypeInformation.of(new TypeHint<Tuple2<Long, Long>>() {
                        }), new Tuple2<>(0L, 0L));
                        ValueState<Tuple2<Long, Long>> sum = getRuntimeContext().getState(descriptor);
                        getRuntimeContext().getMetricGroup().counter("myCounter");
                    }

                    @Override
                    public void flatMap(Tuple2<Long, Long> value, Collector<Tuple2<Long, Long>> out) throws Exception {
                        Tuple2<Long, Long> currentSum = sum.value();
                        currentSum.f0 += 1;
                        currentSum.f1 += value.f1;
                        sum.update(currentSum);
                        if (currentSum.f0 >= 2) {
                            out.collect(new Tuple2<>(currentSum.f0, currentSum.f1));
                            sum.clear();
                        }
                        this.counter.inc();
                        System.out.println("Counter metrics: " + counter.getCount());
                    }
                });
        source.print("test");
        env.execute("test");
    }
}
