# MySQL 专题

MySQL 是后端开发中最常见的关系型数据库之一，常用于保存用户、角色、部门、订单、权限等业务数据。

这个专题围绕后端开发中的常用 SQL 和 MySQL 基础能力展开，目标是帮助读者建立从 Java CRUD 到数据库 CRUD 的映射关系。

---

## 内容路线

```text
SQL 基础
-> 建库建表
-> CRUD 增删改查
-> 条件查询 / 排序 / 分页
-> 约束 / 主键 / 索引
-> MyBatis 连接数据库
```

---

## 阅读顺序

1. `mysql/01-SQL基础与MySQL终端.md`
2. `mysql/02-建库建表与字段类型.md`
3. `mysql/03-CRUD增删改查.md`
4. `mysql/04-条件查询与排序分页.md`
5. `mysql/05-主键约束索引基础.md`
6. `mysql/06-SQL面试知识点.md`
7. `mysql/07-数据库约束与数据兜底.md`

---

## 学习目标

掌握基础 SQL 后，应能独立完成：

```text
查看数据库
切换数据库
查看表
创建表
插入数据
查询数据
修改数据
删除数据
按条件查询
排序和分页
```

并能把 Java Repository 方法和 SQL 对应起来：

```text
findAll       -> select * from department
findById      -> select * from department where id = ?
save          -> insert into department ...
updateById    -> update department set ... where id = ?
deleteById    -> delete from department where id = ?
existsById    -> select count(*) from department where id = ?
```
