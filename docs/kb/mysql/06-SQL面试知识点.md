# 06-SQL面试知识点

本文用于面试前快速复习 SQL 基础问题。

不需要一开始全部背熟，但应先建立问题地图。

---

## 1. SQL 的增删改查怎么写

新增：

```sql
insert into department (id, name) values (1, 'test');
```

查询：

```sql
select * from department;
```

修改：

```sql
update department set name = 'dev' where id = 1;
```

删除：

```sql
delete from department where id = 1;
```

回答重点：

```text
update 和 delete 必须注意 where 条件，否则会影响整张表。
```

---

## 2. primary key 和 unique 的区别

`primary key`：

```text
主键
唯一
不能为空
一张表通常只有一个主键
用于标识一条记录
```

`unique`：

```text
唯一约束
保证字段值不重复
一张表可以有多个 unique 字段
```

一句话：

```text
主键是记录身份，unique 是字段不重复规则。
```

---

## 3. delete、truncate、drop 区别

`delete`：

```text
删除表中的数据
可以带 where
表结构还在
```

示例：

```sql
delete from department where id = 1;
```

`truncate`：

```text
清空整张表数据
表结构还在
通常不能带 where
```

示例：

```sql
truncate table department;
```

`drop`：

```text
删除整张表
表结构和数据都没了
```

示例：

```sql
drop table department;
```

一句话：

```text
delete 删数据，truncate 清空表，drop 删除表。
```

---

## 4. where 和 having 区别

可以先简单理解：

```text
where：分组前过滤普通数据
having：分组后过滤聚合结果
```

示例：

```sql
select name, count(*) from department group by name having count(*) > 1;
```

基础阶段只要知道：

```text
没有 group by 时，大多数场景用 where。
```

---

## 5. count(*) 和 count(字段) 区别

```sql
select count(*) from department;
```

统计所有行数。

```sql
select count(name) from department;
```

统计 name 不为 null 的行数。

区别：

```text
count(*) 统计行
count(字段) 不统计该字段为 null 的行
```

---

## 6. 索引是什么

索引可以理解成书的目录。

作用：

```text
提高查询速度
```

代价：

```text
占用空间
新增、修改、删除时需要维护索引
```

一句话回答：

```text
索引是一种帮助数据库快速定位数据的数据结构，能提升查询效率，但会增加存储和写入成本。
```

---

## 7. 索引为什么会失效

常见原因：

```text
在索引字段上做函数计算
like 以 % 开头
类型不一致
or 使用不当
查询条件不符合联合索引最左前缀
```

基础阶段先记两个：

```sql
select * from user where name like '%tom';
```

这种 `%` 开头可能导致索引效果变差。

```sql
select * from user where id + 1 = 2;
```

对索引字段做计算，也可能影响索引使用。

---

## 8. SQL 和 MyBatis 的关系

MyBatis 不是替代 SQL。

MyBatis 的作用是：

```text
让 Java 方法和 SQL 绑定起来
```

例如：

```java
Department findById(int id);
```

背后对应：

```sql
select * from department where id = #{id}
```

所以面试里问 MyBatis，本质上也会涉及 SQL。

---

## 9. 初级面试最低掌握

至少要会：

```text
1. 写 insert / select / update / delete
2. 知道 update/delete 要加 where
3. 知道主键是什么
4. 知道唯一约束是什么
5. 知道索引是提高查询速度的
6. 知道 delete / truncate / drop 的区别
7. 知道 count(*) 和 count(字段) 的区别
```

---

## 10. 面试回答模板

问：索引是什么？

```text
索引可以理解成数据库表的目录，用来帮助数据库更快定位数据。
比如按 id 查询时，如果 id 是主键，MySQL 会通过主键索引快速找到数据。
但索引不是越多越好，因为它会占用空间，并且新增、修改、删除数据时也要维护索引。
```

问：delete、truncate、drop 区别？

```text
delete 是删除数据，可以带 where，表结构还在。
truncate 是清空整张表，表结构还在，通常不能按条件删除。
drop 是删除整张表，数据和表结构都没了。
```
