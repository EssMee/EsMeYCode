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
