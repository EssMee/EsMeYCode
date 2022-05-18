### Redis

Redis的读性能是11w/s，写性能是8.1w/s的一个NoSql数据库。
Redis运行在内存中但是可以将数据写入磁盘，重启的时候可以再次加载进行使用。
#### Redis数据类型
String（最大512MB），hash，list，set，zset（sorted set：有序集合）
#### 基本命令
**String类型：一个key对应一个value，String可以包含任何对象，包括序列化后的对象。**

```redis
set key value...
set runoob "zhangyi"
get runoob -> "zhangyi"
```

**Hash类型： hmset key field value[field value, field value...] 这个field理解成字段即可，一个key类里有多个field，适合存储对象**，**每个hash可以存储2^32-1，40多亿的键值对**。

```redis
hmset runoob field1 "Hello" field2 "World"
hget runoob field1 -> Hello
hget runoob field2 -> World
```
**List列表：按照插入顺序排序。你可以添加一个元素到列表的头部（左边）或者尾部（右边）。列表最多存储2^32-1条数据。**
```redis
lpush key value; lpush key value.....
localhost:6379> lpush runoob redis
(integer) 1
localhost:6379> lpush runoob mongodb
(integer) 2
localhost:6379> lpush runoob rabbitmq
(integer) 3
localhost:6379> lrange runoob 0 10 // 这个range的start和stop是可以取到stop的。
1) "rabbitmq"
2) "mongodb"
3) "redis"
```
**集合：即集合基本概念，最多存储2^32-1个String元素，但是是无序的**
```redis
sadd key member; sadd key member...
localhost:6379> sadd runoob zhangyi1
(integer) 1
localhost:6379> sadd runoob zhangyi2
(integer) 1
localhost:6379> sadd runoob zhangyi3
(integer) 1
localhost:6379> sadd runoob zhangyi3
(integer) 0
localhost:6379> smembers runoob
1) "zhangyi3"
2) "zhangyi1"
3) "zhangyi2"
```
**zset,sorted set（有序集合）：不同的是每个元素都会关联一个double类型的分数。redis正是通过分数来为集合中的成员进行从小到大的排序。<u>zset的成员是唯一的,但分数(score)却可以重复。</u>**
```redis
zadd key score member; zadd key score member...
localhost:6379> zadd runoob 0 zhangyi1
(integer) 1
localhost:6379> zadd runoob 1 zhangyi2
(integer) 1
localhost:6379> zadd runoob 2 zhangyi3
(integer) 1
localhost:6379> zadd runoob 3 zhangyi4
(integer) 1
localhost:6379> zadd runoob 3 zhangyi4
(integer) 0
localhost:6379> zadd runoob 3 zhangyi5
(integer) 1
localhost:6379> zrangebyscore runoob 0 3 [zrangebyscore key min max]
1) "zhangyi1"
2) "zhangyi2"
3) "zhangyi3"
4) "zhangyi4"
5) "zhangyi5"
```
#### Redis log in: redis-cli (-raw避免中文乱码) -h xxx -p 6379 -a passwd
#### HyperLogLog： 用来做基数统计的算法，每个HyperLogLog键只需要花费12kb内存，就可以计算接近2^64个不同元素的基数。
那什么是基数呢？ ----->> 比如数据集{1,3,5,7,5,7,8}，基数集就是{1,3,5,7,8}，基数为5。<u>**基数统计</u>**就是在可以接受的范围之内快速计算基数的算法。
```redis
localhost:6379> pfadd runoobkey reids
(integer) 1
localhost:6379> pfadd runoobkey mongodb
(integer) 1
localhost:6379> pfadd runoobkey mysql
(integer) 1
localhost:6379> pfcount runoobkey
(integer) 3
```
#### Redis发布订阅模型（Redis客户端可以订阅任意数量的Channel）
**订阅之后，一旦发布的客户端publish消息之后，subscribe的客户端就立马会收到消息（我的意思是不需要"主动拉取"的动作）**
SUBSCRIBE CLIENT: 
```redis
localhost:6379> subscribe runoobChat
Reading messages... (press Ctrl-C to quit)
1) "subscribe"
2) "runoobChat"
3) (integer) 1
```
PUBLISHER CLIENT:
```redis
localhost:6379> PUBLISH runoobChat "Redis PUBLISH test"
(integer) 1
localhost:6379> PUBLISH runoobChat "Redis PUBLISH test222"
(integer) 1
```
我每输入一条发布命令之后，订阅端就会立马回显，以下是最终结果：
```redis
1) "message"
2) "runoobChat"
3) "Redis PUBLISH test"
1) "message"
2) "runoobChat"
3) "Redis PUBLISH test222"
```
#### Redis事务特点：
（1）批量操作在发送exec之前会被放进缓存队列
（2）收到exec之后进入事务执行，事务中任意命令执行失败，其余的命令依然被执行。
（3）事务执行过程中，其他客户端提交的命令请求不会插入到事务执行命令序列中。
开始事务---命令入队---执行事务
**注意： 单个Redis命令执行是原子性的，但是Redis事务的执行并不是原子性的，可以理解事务为一个打包的<u>批量执行脚本</u>**

<u>It's important to note that even when a command fails, all the other commands in the queue are processed – Redis will not stop the processing of commands.</u>
```redis
localhost:6379> MULTI
OK
localhost:6379> set book-name "C++ in 21 days"
QUEUED
localhost:6379> get book-name
QUEUED
localhost:6379> sadd tag "C++" "Programing" "Mastering Series"
QUEUED
localhost:6379> smembers tag
QUEUED
localhost:6379> exec
1) OK
2) "C++ in 21 days"
3) (integer) 1
4) 1) "Programing"
   2) "Programming"
   3) "C++"
   4) "Mastering Series"
```
#### Redis客户端连接：在网络事件处理上采用的是非阻塞多路复用模型
#### Redis管道
通常情况下，一个请求会遵循如下规则：
（1）客户端向服务端发送一个查询请求，并监听Socket返回，通常是以阻塞模式，等待服务端响应。
（2）服务端处理命令，并将结果返回给客户端。
**管道计数**可以在服务端未响应时，客户端也可以继续向服务端发送请求，并最终一次性读取所有服务端的相应。
#### Redis分区：https://www.runoob.com/redis/redis-partitioning.html
#### Jedis：如果只是使用的话会就很简单，生成一个jedis对象然后redis-cli的命令操作类型就行了。
