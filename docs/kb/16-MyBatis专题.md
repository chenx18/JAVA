# 16-MyBatis专题

MyBatis 是 Java 后端常用的持久层框架，用来把 Java 方法和 SQL 绑定起来。

一句话：

```text
MyBatis 让 Java 方法可以方便地执行 SQL，并把数据库结果映射成 Java 对象。
```

---

## 阅读顺序

1. `mybatis/01-MyBatis是什么.md`
2. `mybatis/02-Mapper接口与SQL注解.md`
3. `mybatis/03-参数绑定.md`
4. `mybatis/04-增删改查返回值.md`
5. `mybatis/05-MyBatis XML写法.md`
6. `mybatis/06-MyBatis XML核心知识点.md`
7. `mybatis/07-动态SQL之where和set.md`
8. `mybatis/08-自增主键与useGeneratedKeys.md`
9. `mybatis/100-MyBatis面试知识点.md`

---

## 和 JDBC 的关系

不用 MyBatis 时，Java 查询数据库需要写：

```text
Connection
PreparedStatement
ResultSet
手动取字段
手动封装对象
```

使用 MyBatis 后，可以写：

```java
Department findById(@Param("id") int id);
```

再在 XML 里写：

```xml
<select id="findById" resultType="com.example.week4.Department">
  select id, name from department where id = #{id}
</select>
```

MyBatis 会负责：

```text
执行 SQL
绑定参数
读取结果
封装对象
```

---

## 和 Repository 的关系

内存版项目：

```text
Repository 操作 List
```

数据库版项目：

```text
Mapper 操作 MySQL
```

所以可以先这样理解：

```text
Mapper 是数据库版 Repository。
```
