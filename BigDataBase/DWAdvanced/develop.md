### 总线矩阵

https://mp.weixin.qq.com/s?__biz=Mzg3NjIyNjQwMg==&mid=2247519639&idx=1&sn=ee88c2dbaa7e84d0ce49c008677eec77

实际设计过程中，我们通常把总线架构列表成矩阵的形式，其中列为一致性维度，行为不同的业务处理过程，即事实。
在交叉点上打上标记表示该业务处理过程与该维度相关，这个矩阵也称为总线矩阵（Bus Matrix）。

![zongxianjuzhen](https://mmbiz.qpic.cn/mmbiz_png/zWSuIP8rdu2aqlXlq9HyI3jOfRqRgHtVuzMicBH7LsTEP7nPRjib5FrubvQQOkxO5Tx3bqys3abUeVVfJ0yOoYag/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

总线矩阵和一致性维度、一致性事实共同组成了 Kimball 的多维体系结构基础，
如何设计总线矩阵？
1. 首先完成横向，即数据域划分；
2. 其次完成纵向，即一致性公共列维度的划分以及度量值的确定。

### 全量和增量数据同步策略

1. 上周期全量 union all 本周期增量 基于主键对更新时间排序 --> 获取最新的全量数据（常规的，目前正在使用）
时延高、吞吐大

2. 结合Flink CDC，基于Mysql的binlog实时记录收集数据新增、更新等信息，实时更新数据到最新状态。
结合Flink CDC，基于Mysql的binlog实时记录收集数据新增、更新等信息，实时更新数据到最新状态。
    在初始化时，以离线模式批量从数据库中拉取全量数据，初始化到Hudi表中；订阅数据库的增量数据，增量更新到Hudi表中。数据以分钟级的延迟和数据库保持完全一致。
    
    ![pic](https://mmbiz.qpic.cn/mmbiz_png/1BMf5Ir754RaM96aWZJbc3JW5dsDzZBiaAic7zx4YW7EoM5cFbu5med86GtRCuic0NFhQjBFOZ6nQ7XYqq9pYzqxA/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

3. 拉链表 + Hudi

    Hudi+Flink CDC用于支持新型准实时需求类需求，对于时效性要求高的需求，比如需要分钟级的延迟，以Hudi+Flink CDC进行支持。

    拉链表方案做存量全量分区表的无缝迁移，和支持离线T-1类的时效性要求较低的需求，以及需要历史所有变更的全版本下的支持。

![拉链表](https://mmbiz.qpic.cn/mmbiz_png/1BMf5Ir754RaM96aWZJbc3JW5dsDzZBiag4lcNkoxDMFvMOibwR7aBvbjx6eZ26JwwEDjbZAD2287zsXIRK3jibMA/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

伪代码
```sql
insert overwrite table 拉链表
select n1.id, n1.昵称,n1.start_date 
case when 
n1.start_date = "9999-12-31" 
and n2.id is not null 
then "业务时间-1" 
else n1.end_date
end as end_date
from 拉链表 n1
left outer join 
(select id from 用户表 where 昨日新注册 or 昨日变更昵称) n2 
on n1.id = n2.id
union all
select id,昵称，"业务日期" as start_date,"9999-12-31" as end_date 
from 用户表
where 昨日新注册 or 昨日变更昵称
```



