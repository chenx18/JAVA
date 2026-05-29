# 01-MyBatis是什么

MyBatis 是 Java 后端常用的持久层框架。

它的核心作用是：

```text
帮 Java 执行 SQL，并把数据库结果转换成 Java 对象。
```

---

## 1. 为什么需要 MyBatis

如果不用 MyBatis，Java 直接操作数据库通常要写 JDBC 代码：

```text
获取数据库连接
创建 PreparedStatement
设置参数
执行 SQL
读取 ResultSet
手动创建 Java 对象
手动 set 字段
关闭资源
```

这些代码很重复。

MyBatis 把这些重复工作封装起来，让开发者主要关注：

```text
Java 方法
SQL 语句
参数映射
结果映射
```

---

## 2. MyBatis 最小示例

```java
@Mapper
public interface DepartmentMapper {

  @Select("select id, name from department where id = #{id}")
  Department findById(int id);
}
```

调用：

```java
Department department = mapper.findById(1);
```

背后执行：

```sql
select id, name from department where id = 1;
```

---

## 3. MyBatis 做了什么

```text
1. 找到 Mapper 方法
2. 读取方法上的 SQL
3. 把 Java 参数绑定进 SQL
4. 执行 SQL
5. 把数据库结果映射成 Java 对象
```

---

## 4. MyBatis 和 SQL 的关系

MyBatis 不会替代 SQL。

MyBatis 的作用是：

```text
让 Java 更方便地执行 SQL。
```

所以使用 MyBatis 仍然需要学习 SQL。

---

## 5. 一句话总结

```text
MyBatis 是 Java 方法和 SQL 之间的桥。
```
