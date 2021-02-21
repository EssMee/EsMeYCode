package com.flink.zy;

import org.apache.commons.collections.CollectionUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * 往我自己本地的虚机192.168.33.130上发消息的一个kafka生产者的程序；
 * 之后再在本地写一个FlinkKafkaConsumer，用来大概统计某个分组中某段时间内商品的总价格和购买频率；另外如果可以统计某段时间之内没有用户
 * 购买过该商品，可以发出warning。
 *
 * @author eason
 * @note （1）最好用eventTime+watermark来做，因为我造的数据流本身自带时间戳属性。
 * @note （2）最好用localEnvironmentWithUI来方便我看并发。
 * @time 2020-02-15
 */

public class ZyKafkaProducer {
    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.33.130:9092");
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);
        new Thread(() -> {
            while (true) {
                String output;
                Timestamp time = new Timestamp(System.currentTimeMillis());
                String name = "Item" + (char) ('A' + new Random().nextInt(3));
                Integer price = new Random().nextInt(100);
                Integer groupId = new Random().nextInt(4);
                output = time + "@" + name + "@" + price + "@" + groupId;
                System.out.println("generate msg: " + output);
                ProducerRecord record = new ProducerRecord("test1", output);
                kafkaProducer.send(record);
                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        })
                .start();
    }
}
