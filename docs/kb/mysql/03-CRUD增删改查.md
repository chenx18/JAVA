# 03-CRUD增删改查

CRUD 是后端最核心的基础能力。

```text
Create：新增
Read：查询
Update：修改
Delete：删除
```

在入门练习中，常会先用 Java `List` 模拟数据存储。

接入数据库后，CRUD 操作会转换成 SQL。

---

## 1. 新增数据：insert

标准语法：

```sql
insert into 表名 (字段1, 字段2) values (值1, 值2);
```

示例：

```sql
insert into department (id, name) values (1, 'test');
```

含义：

```text
往 department 表插入一条数据
id = 1
name = test
```

注意：

```text
数字可以不加引号
字符串要加单引号
```

---

## 2. 查询全部：select *

```sql
select * from department;
```

含义：

```text
查询 department 表中的所有字段、所有数据
```

`*` 表示所有字段。

---

## 3. 查询指定字段

```sql
select id, name from department;
```

含义：

```text
只查询 id 和 name 字段
```

在真实开发中，不一定每次都用 `select *`，有时只查询需要的字段。

---

## 4. 按 id 查询

```sql
select * from department where id = 1;
```

含义：

```text
只查询 id 等于 1 的部门
```

`where` 表示查询条件。

---

## 5. 修改数据：update

标准语法：

```sql
update 表名 set 字段 = 新值 where 条件;
```

示例：

```sql
update department set name = 'dev' where id = 1;
```

含义：

```text
把 id 为 1 的部门 name 改成 dev
```

重要提醒：

```text
update 一定要写 where
```

如果不写：

```sql
update department set name = 'dev';
```

会把整张表所有数据都改掉。

---

## 6. 删除数据：delete

标准语法：

```sql
delete from 表名 where 条件;
```

示例：

```sql
delete from department where id = 1;
```

含义：

```text
删除 id 为 1 的部门
```

重要提醒：

```text
delete 一定要写 where
```

如果不写：

```sql
delete from department;
```

会删除整张表所有数据。

---

## 7. SQL 和 Java CRUD 的对应关系

```text
Repository.findAll()
-> select * from department;

Repository.findById(id)
-> select * from department where id = ?;

Repository.save(department)
-> insert into department ...;

Repository.updateById(id, department)
-> update department set name = ? where id = ?;

Repository.deleteById(id)
-> delete from department where id = ?;
```

这里的 `?` 以后会由 MyBatis 参数传入。

---

## 8. 当前必须记住

```sql
insert into department (id, name) values (1, 'test');
select * from department;
select * from department where id = 1;
update department set name = 'dev' where id = 1;
delete from department where id = 1;
```

一句话：

```text
SQL 的 CRUD，就是 Java Repository 方法真正要执行的数据库操作。
```
