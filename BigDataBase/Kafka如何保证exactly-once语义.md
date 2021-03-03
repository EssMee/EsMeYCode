###Kafka如何保证exactly-once？？
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

**NOTICE**：幂等性发送只能保证单个Producer对同一个<Topic,Partition>的exactly once语义。
