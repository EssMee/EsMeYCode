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
