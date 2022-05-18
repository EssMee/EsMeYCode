##  BroadcastConnectStream

streamB被广播时，带上状态描述符：

```java
BroadcastStream<Tuple2<String, String>> broadcastStreamB = streamb.broadcast(new MapStateDescriptor<>("streamb", BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO));
```

```java
@PublicEvolving
public BroadcastStream<T> broadcast(final MapStateDescriptor<?, ?>... broadcastStateDescriptors) {
   Preconditions.checkNotNull(broadcastStateDescriptors);
   final DataStream<T> broadcastStream = setConnectionType(new BroadcastPartitioner<>());
   return new BroadcastStream<>(environment, broadcastStream, broadcastStateDescriptors);
}
```

根据一个广播分区器决定连接状态，但是广播流会被分发到所有的下游算子，因此不需要选择通道，形成一条DataStream。

然后根据environment和状态描述符形成一条BroadcastStream。

当一条non-keyed datastream去连接广播流时：

```java
streamA.connect(broadcastStreamB)
```

```java
@PublicEvolving
public <R> BroadcastConnectedStream<T, R> connect(BroadcastStream<R> broadcastStream) {
   return new BroadcastConnectedStream<>(
         environment,
         this,
         Preconditions.checkNotNull(broadcastStream),
         broadcastStream.getBroadcastStateDescriptor());
}
```

类BroadcastConnectedStream有四个成员：

```java
private final StreamExecutionEnvironment environment;
private final DataStream<IN1> inputStream1;
private final BroadcastStream<IN2> inputStream2;
private final List<MapStateDescriptor<?, ?>> broadcastStateDescriptors;
```

然后这个类里有一个process方法：

```java
@PublicEvolving
public <OUT> SingleOutputStreamOperator<OUT> process(final BroadcastProcessFunction<IN1, IN2, OUT> function) {

   TypeInformation<OUT> outTypeInfo = TypeExtractor.getBinaryOperatorReturnType(
         function,
         BroadcastProcessFunction.class,
         0,
         1,
         2,
         TypeExtractor.NO_INDEX,
         getType1(),
         getType2(),
         Utils.getCallLocationName(),
         true);

   return process(function, outTypeInfo);
}
```

实际上调用的是 process(function, outTypeInfo)

```java
@PublicEvolving
public <OUT> SingleOutputStreamOperator<OUT> process(
      final BroadcastProcessFunction<IN1, IN2, OUT> function,
      final TypeInformation<OUT> outTypeInfo) {

   Preconditions.checkNotNull(function);
   Preconditions.checkArgument(!(inputStream1 instanceof KeyedStream),
         "A BroadcastProcessFunction can only be used on a non-keyed stream.");

   TwoInputStreamOperator<IN1, IN2, OUT> operator =
         new CoBroadcastWithNonKeyedOperator<>(clean(function), broadcastStateDescriptors);
   return transform("Co-Process-Broadcast", outTypeInfo, operator);
}
```

它会生成一个CoBroadcastWithNonKeyedOperator的对象，作为操作符。这个类里open方法在开启生命周期的时候，会注册一个可读写上下文实例和一个只读的上下文实例。

```java
@Override
public void open() throws Exception {
   super.open();

   collector = new TimestampedCollector<>(output);

   this.broadcastStates = new HashMap<>(broadcastStateDescriptors.size());
   for (MapStateDescriptor<?, ?> descriptor: broadcastStateDescriptors) {
      broadcastStates.put(descriptor, getOperatorStateBackend().getBroadcastState(descriptor));
   }

   rwContext = new ReadWriteContextImpl(getExecutionConfig(), userFunction, broadcastStates, getProcessingTimeService());
   rContext = new ReadOnlyContextImpl(getExecutionConfig(), userFunction, broadcastStates, getProcessingTimeService());
}
```

```java
@Override
public void processElement1(StreamRecord<IN1> element) throws Exception {
   collector.setTimestamp(element);
   rContext.setElement(element);
   userFunction.processElement(element.getValue(), rContext, collector);
   rContext.setElement(null);
}

@Override
public void processElement2(StreamRecord<IN2> element) throws Exception {
   collector.setTimestamp(element);
   rwContext.setElement(element);
   userFunction.processBroadcastElement(element.getValue(), rwContext, collector);
   rwContext.setElement(null);
}
```

显然处理广播流的房里里传入是”可读写“的。


### WaterMark机制的理解

假如现在有一个10size，5slide的滑动窗口，按道理是10、15、20。。。的时候触发计算。

假设现在使用的是事件事件，设置水印的时候允许了5的延迟，因此准确是触发时间是15、20、25。。。

数据流（自带时间戳）进入系统的时候，不断地根据数据本身的时间去更新watermar，始终保持是最大值：

比如第一条数据带的是10，第二条是9，那么现在watermark是10。又来了一条带的是14，现在watermark是14。

又来了一条带的是15，现在watermark是15，该水印现在已经大于等于窗口的触发时间了，这个时候就应该触发第一个窗口的计算，这个15的watermark表示的含义就是，我认为已经没有时间戳比15还小的数据会来了。因为理想状态下，事件携带的时间本应该就是单调递增的，15的都到了，那么就是认为1，2，3，4，5，6，7。。。。15时间的数据都已经到了，我该把窗口触发了，况且还等了5，本来10的时候就触发了。

假如有一条元素是9产生的，但是由于网络关系，实际上14的时候才到，如果不使用延迟5的水印（或是处理时间），那么它就不会进入第一个窗口，因为窗口在10的时候就已经触发掉了。然而如果使用水印的话，它还是会在第一个窗口被计算，这就是处理了迟到数据。


### checkpoint理解
由jobManager的CheckpointCoordinator发起，首先发向source，之后遍及所有task。checkpointCoordinator全权负责本应用的快照制作。
#### 大概流程
协调器周期性地向source发送barrier，当某个算子接收到barrier时，并停止这条流上相关的数据处理，进行快照制作保存到持久化存储中，然后向协调器返回checkpoint handler来报告自己制作快照的情况，同时向自己下游的所有算子广播该barrier（一个发出的barrier在全流程中都是不变的。），随后恢复数据处理。

下游算子收到barrier后，也暂停数据处理，制作快照，同上。。。
当协调器收到所有算子（任务）的checkpoint handler之后，认为这个周期内的快照制作完成；如果到了超时时间还没有收集全，则快照制作失败。

#### 快照里保存了什么？
举个例子，source任务（source task）里就保存了消费到某个主题的某个偏移量offset。带状态的计算算子（如聚合，pv task）就保存了某个key的此时的value。
比如正在制作的chk-100：
source task制作快照为(0,10000)
pv task制作快照为(a, 5000),(b,6000)。代表第100次快照发生时，source消费到了offset为10000的位置，pv的统计结果为a的值是5000，b的值是6000。 那么下次故障恢复时，就从最近的一次checkpoint恢复。
假如checkpoint是每3分钟做一次，在10：00：00的时候做了第100次chk，但是10s之后任务挂了，可能这时source task是（0，10010），pv task是（a，5500），（b，7000）。此时只能从chk100的地方恢复，source和pv的值也要从(0,10000)，(a, 5000),(b,6000)重新开始计算。
从（0，10010），pv task是（a，5500），（b，7000）是做不到的，因为这个状态没有保存进快照里。

#### barrier
[! barrier](D:\LearningMaterials\个人的FLink理解\barrier.png)
所有的任务，只要在碰到了barrier，就要进行快照制作。 barrier n处做的快照就是从job开始处理到barrier n的所有状态数据。

#### barrier对齐
[！barrier对齐](D:\LearningMaterials\个人的FLink理解\barrier对齐.png)
（1）就是说数字流的barrier n先到了这个算子，但是这个算子知道上游应该送来两个barrier n。 
（2）此时数字流再进来的数据就会放进算子的input buffer中，不计算也不发送；（因为这部分数据被认为是下一次checkpoint的时候应该保存的状态）
（3）同时字母流的barrier n晚到，在到达之前，字母流的数据会被继续计算和发送。
（4）等到字母流的barrier n也到了，此时算子停止处理来自上游算子的数据，同时会把缓冲区的数据发送到下游，同时把barrier n广播到下游，对自身进行快照，保存的状态是不包含缓冲区数据的计算状态（返回checkpoint handler，状态写进持久化存储）。然后恢复数据处理，先处理缓冲区的数据，该加加该减减，这个影响的是下一次chk时保存的状态，然后恢复处理数据流里的数据。

#### chk总结
什么时候会出现barrier 对齐？
（1）flink checkpoint的语气是exactly once。
（2）算子实例有多个输入流。
如果不对齐的话，就是at least once（至少一次）。
在不启用barrier对齐的算子上，那么在制作快照的过程中，可能还会消费barrier到来之后的一些数据的消息；那么从上一次快照恢复的时候，这部分数据又会被消费一遍。
