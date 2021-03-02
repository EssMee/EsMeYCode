### HIVE

Hive是一个数据仓库基础工具，建立在Hadoop之上（MapReduce+HDFS）。
<u>它是用来开发SQL类型脚本用于做MapReduce操作的平台。</u>
<u>使得查询和分析非常方便</u>
**但是它不是一个关系型数据库；可以用于联机事务处理（OLTP）；可以实时查询和行级更新语句。**

#### HIVE架构

[!HIVE ](https://www.yiibai.com/uploads/allimg/141228/1-14122R10152108.jpg)

| 单元           | 注释                                                         |
| -------------- | ------------------------------------------------------------ |
| 用户接口       | 提供操作的方式                                               |
| 元存储         | Hive选择各自的数据库服务器，用以储存表，数据库，列模式或元数据表。<br />它们的数据类型和HDFS映射。 |
| HiveQL处理引擎 | HiveQL类似于SQL的查询上Metastore模式信息。这是传统的方式进行MapReduce程序的替代品之一。<br />相反，使用Java编写的MapReduce程序，可以编写为MapReduce工作，并处理它的查询。 |
| 执行引擎       | HiveQL处理引擎和MapReduce的结合部分是由Hive执行引擎。执行引擎处理查询并产生结果和MapReduce的结果一样。它采用MapReduce方法。 |
| HDFS 或 HBASE  | Hadoop的分布式文件系统或者HBASE数据存储技术是用于将数据存储到文件系统。 |

#### HIVE执行流程

[!HIVE执行流程](https://www.yiibai.com/uploads/allimg/141228/1-14122R10220b9.jpg)

#### 创建数据库

```sql
CREATE DATABASE [IF NOT EXISTS] userdb;
```

#### 删除数据库

```sql
DROP DATABASE IF EXISTS uderdb;
```

#### 创建数据表/删除表

```sql
CREATE TABLE IF NOT EXISTS employee (eid int, name String,
salary String, destination String)
COMMENT ‘Employee details’
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t'
LINES TERMINATED BY '\n'
STORED AS TEXTFILE；

DROP TABLE IF EXISTS employee
```

#### 加载数据 LOAD DATA

```sql
LOAD DATA [LOCAL] INPATH 'filePath' [OVERWRITE] INTO TABLE tableName
[PARTITION (partcol1=val1, partcol2=val2 ...)]
```


example：

```sql
LOAD DATA INPATH '/home/user/sample.txt' OVERWRITE INTO TABLE employee;
```

#### 修改表

```sql
ALTER TABLE name RENAME TO new_name 
ALTER TABLE name ADD COLUMNS (col_spec[, col_spec ...]) 
ALTER TABLE name DROP [COLUMN] column_name 
ALTER TABLE name CHANGE column_name new_name new_type 
ALTER TABLE name REPLACE COLUMNS (col_spec[, col_spec ...])
```

#### HIVE的分区

 可以根据某些属性将元素分发到不同的文件(分区），提供高速查询。比如一张雇员表可以根据年份，划分到不同的文件中：
/tab1/employdata/2012/file2
/tab1/employdata/2013/file 

##### 创建分区

```sql
ALTER TABLE table_name ADD [IF NOT EXISTS] PARTITION partition_spec [LOCATION 'location1'] 
partition_spec [LOCATION 'location2'] ...; 
partition_spec: : (p_column = p_col_value, p_column = p_col_value, ...)
```

**examble**

```sql
ALTER TABLE employee ADD PARTITION (year='2013') location '/2012/part2012'
```

   ???? <u>这是啥玩意儿</u>

##### 重命名分区

```sql
ALTER TABLE employee PARTITION(year='1203') RENAME TO PARTITION(Yoj='1203');
```

##### 删除分区

```sql
ALTER TABLE employee DROP [IF EXISTS] PARTITION (year='1203');
```

#### HIVE内置运算符

|  关系    |  算术    |  逻辑   | 复杂    |   
| --------| --------| --------| -------|

#### 视图和索引

##### VIEW

```sql
CREATE VIEW [IF NOT EXISTS] view_name [(column_name [COMMENT column_comment], ...)]
[COMMENT table_comment
AS SELECT ...
```

| ID   | Name        | Salary | Designation       | Dept  |
| ---- | ----------- | ------ | ----------------- | ----- |
| 1201 | Gopal       | 45000  | Technical manager | TP    |
| 1202 | Manisha     | 45000  | Prooreader        | PR    |
| 1203 | Masthanvali | 40000  | Technical writer  | TP    |
| 1204 | Krian       | 40000  | Hr Admin          | HR    |
| 1205 | Kranthi     | 30000  | Op Admin          | Admin |

要求生成一个查询检索工资超过30000的员工的详细信息，把结果存储在视图emp_30000中。

```sql
CREATE VIEW emp_30000 AS 
SELECT * FROM EMPLOYEE
WHERE SALARY > 30000;
```

##### INDEX: 创建表上的一个特定列的指针。

```sql
CREATE INDEX index_salary ON TABLE employee(salary) AS 'org.apache.hadoop.hive.ql.index.compact.CompactIndexHandler';
```



##### 删除INDEX

```sql
DROP INDEX <index_name> ON < table_name>
```

#### SELECT 同sql

#### SELECT ORDER BY同sql

#### SELECT GROUP BY同sql。

GROUP BY子句用于分类所有记录结果的特定集合列。它被用来查询一组记录。
example： select dept, count(*) from employee group by dept;

| dept  | Count(*) |
| ----- | -------- |
| Admin | 1        |
| PR    | 2        |
| TR    | 3        |

#### SELECT JOIN 类似于sql

下面给一个例子：
tableA:

| id   | name     | age  | address   | salary |
| ---- | -------- | ---- | --------- | ------ |
| 1    | Remesh   | 32   | Ahmedabad | 2000   |
| 2    | Khilan   | 25   | Delhi     | 15000  |
| 3    | kaushik  | 23   | Kota      | 2000   |
| 4    | Chaitali | 25   | Mumbai    | 6500   |
| 5    | Hardik   | 27   | Bhopal    | 8500   |
| 6    | Komal    | 22   | MP        | 4500   |
| 7    | Muffy    | 24   | Indore    | 10000  |

| oid  | date       | customer_id | amount |
| ---- | ---------- | ----------- | ------ |
| 102  | 2021-03-01 | 3           | 3000   |
| 100  | 2021-03-01 | 3           | 1500   |
| 101  | 2021-03-04 | 2           | 1560   |
| 103  | 2020-03-01 | 4           | 2060   |

**直接join,相当于把两张表全部连起来：**

```sql
select * from "tableA" join "tableB" on "tableA".id = "tableB".customer_id;
```

3,kaushik,23,Kota,2000,102,2021-03-01,3,3000
3,kaushik,23,Kota,2000,100,2021-03-01,3,1500
2,Khilan,25,Delhi,1500,101,2021-03-04,2,1560
4,Chaitali,25,Mumbai,6500,103,2020-03-01,4,2060
**Left Join(Left Outer Join)，选取左表所有的行，即使条件在右表中不存在：**

```sql
-- LEFT OUTER JOIN
select
a.id,a.name,b.amount,b.date
from "tableA" a left outer join "tableB" b
on a.id = b.customer_id;
```

3,kaushik,3000,2021-03-01
3,kaushik,1500,2021-03-01
2,Khilan,1560,2021-03-04
4,Chaitali,2060,2020-03-01
5,Hardik,<null>,<null>
6,Komal,<null>,<null>
1,Ramesh,<null>,<null>
7,Muffy,<null>,<null>
**Right Join(Right Outer Join)，选取右表所有的行，即使条件在左表中没有匹配。**

```sql
-- RIGHT OUTER JOIN
SELECT
a.id,a.name,b.amount,b.date
from "tableA" a right outer join "tableB" b
on a.id = b.customer_id;
```

3,kaushik,3000,2021-03-01
3,kaushik,1500,2021-03-01
2,Khilan,1560,2021-03-04
4,Chaitali,2060,2020-03-01
**FULL JOIN(FULL OUTER JOIN)，相当于把left join和right join的结果拼在一起。**

```sql
-- FULL OUTER JOIN
SELECT
a.id,a.name,b.amount,b.date
from "tableA" a full outer join "tableB" b
on a.id = b.customer_id;
```

3,kaushik,3000,2021-03-01
3,kaushik,1500,2021-03-01
2,Khilan,1560,2021-03-04
4,Chaitali,2060,2020-03-01
5,Hardik,<null>,<null>
6,Komal,<null>,<null>
1,Ramesh,<null>,<null>
7,Muffy,<null>,<null>3,kaushik,3000,2021-03-01
3,kaushik,1500,2021-03-01
2,Khilan,1560,2021-03-04
4,Chaitali,2060,2020-03-01
