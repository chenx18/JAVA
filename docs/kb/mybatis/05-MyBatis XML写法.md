# 05-MyBatis XML写法

MyBatis 有两种常见写 SQL 的方式：注解和 XML。

注解适合简单 SQL，XML 更适合真实项目中的复杂 SQL。

---

## 一句话理解

```text
Mapper.java 定义 Java 方法，Mapper.xml 定义这些方法对应的 SQL。
```

以前注解写法：

```java
@Select("select id, name from department where id = #{id}")
Department findById(int id);
```

XML 写法：

```java
Department findById(@Param("id") int id);
```

```xml
<select id="findById" resultType="com.example.week4.Department">
  select id, name from department where id = #{id}
</select>
```

---

## 为什么真实项目常用 XML

注解 SQL 写简单查询很方便。

但真实项目常见 SQL 会更复杂：

```text
多条件查询
动态 where
分页
join 多表
foreach 批量
resultMap 字段映射
统计查询
```

这些都写在注解里会很难维护。

XML 更适合长 SQL 和动态 SQL。

---

## 文件位置

常见结构：

```text
src/main/java/com/example/week4/DepartmentMapper.java
src/main/resources/mapper/DepartmentMapper.xml
```

Java 代码放在 `src/main/java`。

XML SQL 放在 `src/main/resources/mapper`。

---

## application.properties 配置

需要告诉 MyBatis 去哪里找 XML：

```properties
mybatis.mapper-locations=classpath:mapper/*.xml
```

这表示：

```text
去 resources/mapper/ 下面找所有 .xml 文件
```

---

## Mapper.java 写法

迁移到 XML 后，Mapper 接口只保留方法声明。

```java
@Mapper
public interface DepartmentMapper {

  List<Department> findAll();

  Department findById(@Param("id") int id);

  Department findByName(@Param("name") String name);

  List<Department> searchByName(@Param("name") String name);

  int insert(Department department);

  int updateById(Department department);

  int deleteById(@Param("id") int id);
}
```

不再写：

```java
@Select
@Insert
@Update
@Delete
```

---

## Mapper.xml 基础结构

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "https://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.example.week4.DepartmentMapper">

</mapper>
```

重点是：

```text
namespace 必须对应 Mapper 接口完整路径
```

---

## namespace 和 id 的对应关系

必须记住：

```text
namespace 对接口
id 对方法名
```

Java：

```java
Department findById(@Param("id") int id);
```

XML：

```xml
<select id="findById" resultType="com.example.week4.Department">
  select id, name from department where id = #{id}
</select>
```

如果方法叫 `searchByName`，XML 也必须叫：

```xml
<select id="searchByName">
```

不能一个叫 `searchByName`，另一个叫 `findByNameList`。

---

## 查询单条数据

```java
Department findById(@Param("id") int id);
```

```xml
<select id="findById" resultType="com.example.week4.Department">
  select id, name from department where id = #{id}
</select>
```

查不到时通常返回 `null`，Service 再决定是否抛业务异常。

---

## 查询列表

```java
List<Department> findAll();
```

```xml
<select id="findAll" resultType="com.example.week4.Department">
  select id, name from department
</select>
```

返回多个对象时使用 `List<Department>`。

---

## 模糊查询

Java 方法：

```java
List<Department> searchByName(@Param("name") String name);
```

XML：

```xml
<select id="searchByName" resultType="com.example.week4.Department">
  select id, name from department where name like concat('%', #{name}, '%')
</select>
```

如果传入 `研`，SQL 效果类似：

```sql
where name like '%研%'
```

---

## 新增数据

```java
int insert(Department department);
```

```xml
<insert id="insert" parameterType="com.example.week4.Department">
  insert into department (id, name) values (#{id}, #{name})
</insert>
```

`#{id}`、`#{name}` 会从 `Department` 对象中取值。

---

## 修改数据

```java
int updateById(Department department);
```

```xml
<update id="updateById" parameterType="com.example.week4.Department">
  update department set name = #{name} where id = #{id}
</update>
```

返回值 `int` 表示影响行数：

```text
1：修改成功
0：没有匹配到数据
```

---

## 删除数据

```java
int deleteById(@Param("id") int id);
```

```xml
<delete id="deleteById">
  delete from department where id = #{id}
</delete>
```

返回值 `int` 同样表示影响行数。

---

## @Param 和 #{name}

Java：

```java
List<Department> searchByName(@Param("name") String name);
```

XML：

```xml
where name like concat('%', #{name}, '%')
```

这里的 `@Param("name")` 对应 XML 里的 `#{name}`。

如果有多个参数，建议都写 `@Param`。

---

## 常见错误

### 1. XML 的 id 和 Mapper 方法名不一致

错误：

```java
List<Department> searchByName(...);
```

```xml
<select id="findByNameList">
```

应该统一：

```xml
<select id="searchByName">
```

### 2. namespace 写错

`namespace` 必须对应 Mapper 接口完整路径。

### 3. 忘记配置 mapper-locations

如果 XML 放在 `resources/mapper`，需要：

```properties
mybatis.mapper-locations=classpath:mapper/*.xml
```

### 4. like 没有加百分号

错误：

```xml
where name like #{name}
```

推荐：

```xml
where name like concat('%', #{name}, '%')
```

### 5. 文件出现 BOM 隐藏字符

如果编译报：

```text
非法字符: '\ufeff'
```

说明 Java 文件开头混入了 UTF-8 BOM。

处理：保存为 UTF-8 无 BOM，然后重新编译。

---

## 当前阶段要掌握到什么程度

不需要现在盲写完整 XML 模板。

要能做到：

```text
看懂 Mapper.java 和 Mapper.xml 的对应关系
能照着已有模板新增一个查询方法
知道 namespace 对接口
知道 id 对方法名
知道 @Param 对 #{参数名}
知道查询多条返回 List
知道 update/delete 返回 int 影响行数
```

---

## 一句话总结

```text
MyBatis XML 的核心是：Java 接口定义方法，XML 用 namespace + id 找到对应 SQL。
```