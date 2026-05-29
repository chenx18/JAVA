# 100-MyBatis面试知识点

MyBatis 是 Java 后端面试中常见的持久层框架问题。

---

## 1. MyBatis 是什么

推荐回答：

```text
MyBatis 是一个持久层框架，用来简化 Java 操作数据库的过程。
它可以把 Java 方法和 SQL 绑定起来，负责参数绑定、执行 SQL，并把查询结果映射成 Java 对象。
```

---

## 2. MyBatis 和 JDBC 的区别

JDBC：

```text
需要手动获取连接
手动创建 PreparedStatement
手动设置参数
手动处理 ResultSet
手动封装对象
```

MyBatis：

```text
只需要定义 Mapper 方法和 SQL
自动处理参数绑定和结果映射
```

一句话：

```text
MyBatis 是对 JDBC 的封装，减少重复数据库操作代码。
```

---

## 3. Mapper 是什么

推荐回答：

```text
Mapper 是 MyBatis 中定义数据库操作的接口。
接口方法通过注解或 XML 绑定 SQL，运行时 MyBatis 会为 Mapper 创建代理对象。
调用 Mapper 方法时，实际会执行对应 SQL。
```

---

## 4. #{} 和 ${} 的区别

`#{}`：

```text
参数占位
使用预编译
可以防止 SQL 注入
常规业务优先使用
```

`${}`：

```text
字符串拼接
有 SQL 注入风险
通常只在动态表名、字段名等特殊场景使用
```

推荐回答：

```text
#{} 是预编译参数绑定，${} 是字符串拼接。实际开发中应优先使用 #{}。
```

---

## 5. insert/update/delete 为什么返回 int

推荐回答：

```text
MyBatis 的 insert、update、delete 常返回 int，表示影响行数。
例如 update 返回 1 表示修改成功，返回 0 表示没有匹配到对应数据。
Service 层可以根据影响行数判断是否抛 NotFoundException。
```

---

## 6. MyBatis 结果如何映射成对象

查询 SQL：

```sql
select id, name from department
```

Java 对象：

```java
private int id;
private String name;
```

MyBatis 会根据字段名和属性名映射：

```text
id -> setId
name -> setName
```

如果数据库字段是下划线：

```text
created_time
```

Java 属性是驼峰：

```text
createdTime
```

可以配置：

```properties
mybatis.configuration.map-underscore-to-camel-case=true
```

---

## 7. 初级面试最低掌握

至少能回答：

```text
MyBatis 是什么
Mapper 是什么
MyBatis 和 JDBC 的区别
#{} 和 ${} 的区别
insert/update/delete 返回 int 的意义
对象参数如何绑定到 SQL
```

---

## 8. 总结回答模板

```text
MyBatis 是 Java 持久层框架，用来简化 JDBC 操作。
它通过 Mapper 接口把 Java 方法和 SQL 绑定起来，
调用 Mapper 方法时，MyBatis 会负责参数绑定、执行 SQL、结果映射。
查询通常返回对象或 List，增删改通常返回影响行数 int。
```
