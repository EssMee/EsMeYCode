## 本文档内容重点之重点

### Kafka如何保证exactly-once？？

**（1）无幂等性发送的情况：**
producer send (a1,b1)到broker 
-->broker追加消息进分区
-->此时因为某些原因没有回复ack给producer
-->producer send retry(a1,b1)
-->broker再追加消息进分区，现在有两条(a1,b1)
-->此时回复了ack给producer
-->本次发送完成。
**（2）幂等性发送的情况**
producer send (pid=100,sn=1,(a1,b1))的消息到broker
-->broker追加(a1,b1),pid=100,sn=1的消息到分区里
-->此时因为某些原因没有回复ack给producer
-->producer send retry(pid=100,sn=1,(a1,b1))到broker
-->broker接收到之后发现序号还是1
-->拒绝这条消息，因为重复了。
-->producer throw DuplicateSequenceNumber
**Summary**： 
（1）如果消息序号比broker维护的序号**差值**大于1（即2，3，4，5。。。），则认为是中间有数据尚未写入，即乱序或数据丢失，<u>此时broker拒绝此条消息</u>，producer抛出InvalidSequenceNumber。
（2）如果消息序号小于等于broker维护的序号，说明该条消息已经被保存，即为重复消息，此时<u>broker直接丢弃该消息</u>，producer抛出DuplicateSequenceNumber。

**NOTICE**：**幂等性发送只能保证单个Producer对同一个<Topic,Partition>的exactly once语义。**

### 分布式事务

**这将允许一个producer发送一批到不同分区的消息，这些消息要么全部对任何一个消费者可以见，要么对任何一个消费者都不可见。这个特性也允许在一个事务中处理消费数据和提交消费偏移量，从而实现端到端的精准一次语义。**

为了实现这种效果，应用程序必须提供一个稳定的（重启后不变）唯一的 ID，也即Transaction ID ，Transactin ID 与 PID 可能一一对应。区别在于 Transaction ID 由用户提供，将生产者的 **transactional.id** 配置项设置为某个唯一ID。而 PID 是内部的实现对用户透明。

另外，为了保证新的 Producer 启动后，旧的具有相同 Transaction ID 的 Producer 失效，<u>每次 Producer 通过 Transaction ID 拿到 PID 的同时，还会获取一个单调递增的 epoch。由于旧的 Producer 的 epoch 比新 Producer 的 epoch 小，Kafka 可以很容易识别出该 Producer 是老的 Producer 并拒绝其请求。</u>

```java
Producer<String, String> producer = new KafkaProducer<String, String>(props);
// 初始化事务，包括结束该Transaction ID对应的未完成的事务（如果有）
// 保证新的事务在一个正确的状态下启动
producer.initTransactions();
// 开始事务
producer.beginTransaction();
// 消费数据
ConsumerRecords<String, String> records = consumer.poll(100);
try{
    // 发送数据
    producer.send(new ProducerRecord<String, String>("Topic", "Key", "Value"));
    // 发送消费数据的Offset，将上述数据消费与数据发送纳入同一个Transaction内
    producer.sendOffsetsToTransaction(offsets, "group1");
    // 数据发送及Offset发送均成功的情况下，提交事务
    producer.commitTransaction();
} catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
    // 数据发送或者Offset发送出现异常时，终止事务
    producer.abortTransaction();
} finally {
    // 关闭Producer和Consumer
    producer.close();
    consumer.close();
}
```

### Flink怎么保证exactly-once？？

（1）Flink通过checkpoint实现应用内部的exactly-once，通过TwoPhaseCommitSinkFunction保证端到端的exactly-once。
[! 两阶段提交](https://gitee.com/liurio/image_save/raw/master/flink/%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98/exactly-once%E9%A2%84%E6%8F%90%E4%BA%A4.png)
（1）总体思想就是当sink算子的checkpoint完成时（它完成checkpoint的时候也会向checkpoint coordinator返回一个checkpoint handler），同时进行预提交；
[!pre-commit](https://img-blog.csdnimg.cn/20190929192724508.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpc2VueWVhaHllYWg=,size_16,color_FFFFFF,t_70)
（2）预提交成功后，checkpoint coordinator会通知每一个算子本轮checkpoint已经完成，此时sink算子会进行真正的事务提交。
[!commit](https://img-blog.csdnimg.cn/20190929192740393.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpc2VueWVhaHllYWg=,size_16,color_FFFFFF,t_70)
**提交过程中如果失败有以下两种情况：**
（1）Pre-commit失败，那么将恢复到最近一次checkpoint的位置。
（2）一旦pre-commit完成，那么必须保证commit也要完成。
因此所有的算子必须对checkpoint的结果达成共识：即所有的operator都必须认定数据提交要么成功执行，要么被终止然后回滚。
