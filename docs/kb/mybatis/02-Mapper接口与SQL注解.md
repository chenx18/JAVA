# 02-Mapper接口与SQL注解

Mapper 是 MyBatis 中用来定义数据库操作的接口。

可以把 Mapper 理解成：

```text
数据库版 Repository
```

---

## 1. @Mapper

```java
@Mapper
public interface DepartmentMapper {
}
```

`@Mapper` 表示：

```text
这是一个 MyBatis Mapper 接口，交给 MyBatis 创建实现类。
```

开发者只写接口，不写实现类。

MyBatis 会在运行时生成代理对象。

---

## 2. @Select

```java
@Select("select id, name from department")
List<Department> findAll();
```

作用：执行查询 SQL。

返回：

```text
单条数据 -> Java 对象
多条数据 -> List<Java 对象>
```

---

## 3. @Insert

```java
@Insert("insert into department (id, name) values (#{id}, #{name})")
int insert(Department department);
```

作用：执行新增 SQL。

返回 `int` 通常表示影响行数。

---

## 4. @Update

```java
@Update("update department set name = #{name} where id = #{id}")
int updateById(Department department);
```

作用：执行修改 SQL。

返回：

```text
1：修改成功
0：没有匹配到数据
```

---

## 5. @Delete

```java
@Delete("delete from department where id = #{id}")
int deleteById(int id);
```

作用：执行删除 SQL。

返回：

```text
1：删除成功
0：没有匹配到数据
```

---

## 6. Mapper 方法名是否固定

不固定。

MyBatis 真正执行的是方法上的 SQL。

下面两种都可以：

```java
@Select("select id, name from department")
List<Department> findAll();
```

```java
@Select("select id, name from department")
List<Department> queryAllDepartments();
```

但建议使用统一命名：

```text
findAll
findById
insert
updateById
deleteById
```

---

## 7. 一句话总结

```text
Mapper 接口定义 Java 方法，SQL 注解决定方法执行什么数据库操作。
```
