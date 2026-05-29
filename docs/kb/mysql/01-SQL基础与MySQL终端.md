# 01-SQL基础与MySQL终端

SQL 是用来和数据库说话的语言。

在后端开发中，数据通常不会只保存在内存中，而是会持久化到 MySQL 这类关系型数据库。

---

## 1. SQL 是什么

SQL 全称是 Structured Query Language，结构化查询语言。

它的作用是：

```text
告诉数据库我要做什么
```

例如：

```text
创建数据库
创建表
插入数据
查询数据
修改数据
删除数据
```

在 Java 后端里，MyBatis 最后执行的也是 SQL。

---

## 2. 进入 MySQL 终端

如果使用 Docker 启动 MySQL，可以通过以下命令进入 MySQL 终端。

进入容器里的 MySQL：

```bash
docker exec -it week4-mysql mysql -uroot -p
```

输入密码：

```text
123456
```

看到：

```text
mysql>
```

说明已经进入 MySQL 命令行。

---

## 3. SQL 必须用分号结束

正确：

```sql
show databases;
```

如果忘记分号：

```sql
show databases
```

MySQL 会显示：

```text
->
```

意思是：

```text
这句 SQL 还没结束，请继续输入。
```

这时补一个分号即可：

```sql
;
```

---

## 4. 查看数据库

```sql
show databases;
```

作用：查看当前 MySQL 中有哪些数据库。

常见结果：

```text
information_schema
mysql
performance_schema
sys
week4_db
```

`week4_db` 是本文示例使用的数据库。

---

## 5. 选择数据库

```sql
use week4_db;
```

如果看到：

```text
Database changed
```

说明已经切换到 `week4_db`。

后续建表、插入、查询，默认都在这个数据库里执行。

---

## 6. 查看当前库里的表

```sql
show tables;
```

如果已经创建了 `department` 表，会看到：

```text
department
```

---

## 7. 退出 MySQL

```sql
exit;
```

或者：

```sql
quit;
```

---

## 8. 当前必须记住

```sql
show databases;
use week4_db;
show tables;
exit;
```

一句话：

```text
show 是查看，use 是切换，分号表示 SQL 结束。
```
