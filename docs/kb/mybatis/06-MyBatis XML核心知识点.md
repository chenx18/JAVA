# 06-MyBatis XML核心知识点

MyBatis XML 不是普通 XML 学习，而是 MyBatis 用来描述 SQL、参数、返回结果映射的配置文件。

一句话：

```text
Mapper 接口负责定义 Java 方法，Mapper XML 负责告诉 MyBatis：这个方法执行哪条 SQL，结果怎么装回 Java 对象。
```

---

## 1. 一个 Mapper XML 的基本结构

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "https://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.example.week4.DepartmentMapper">

  <select id="findById" resultType="com.example.week4.Department">
    select id, name from department where id = #{id}
  </select>

</mapper>
```

重点先看三个东西：

```text
namespace  对应 Mapper 接口完整路径
id         对应 Mapper 接口里的方法名
SQL        这个方法真正要执行的 SQL
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

可以理解成：

```text
调用 DepartmentMapper.findById(1)
MyBatis 根据 namespace + id 找到 XML 里的 SQL
执行 select ...
把结果封装成 Department
```

---

## 2. namespace 是什么

`namespace` 表示这个 XML 文件绑定哪个 Mapper 接口。

```xml
<mapper namespace="com.example.week4.DepartmentMapper">
```

它对应：

```java
package com.example.week4;

public interface DepartmentMapper {
}
```

注意：

```text
namespace 必须写 Mapper 接口的完整包名 + 类名
```

如果写错，MyBatis 就找不到对应关系。

---

## 3. id 是什么

XML 标签上的 `id` 对应 Mapper 接口里的方法名。

Mapper：

```java
List<Department> searchByName(@Param("name") String name);
```

XML：

```xml
<select id="searchByName" resultType="com.example.week4.Department">
  select id, name from department where name like concat('%', #{name}, '%')
</select>
```

这里的关系是：

```text
searchByName 方法名 = select 标签的 id
```

不能 Mapper 里叫 `searchByName`，XML 里写成 `findByNameList`。

---

## 4. resultType 是什么

`resultType` 用来告诉 MyBatis：查询结果要封装成哪个 Java 类型。

```xml
<select id="findById" resultType="com.example.week4.Department">
  select id, name from department where id = #{id}
</select>
```

意思是：

```text
SQL 查出来一行数据
MyBatis 创建一个 Department 对象
把 id、name 填进去
```

适合场景：

```text
数据库字段名和 Java 属性名基本一致
查询结果比较简单
不需要复杂映射
```

例如：

```sql
select id, name from department
```

对应：

```java
private int id;
private String name;
```

这种情况下，`resultType` 就够用。

---

## 5. resultMap 是什么

`resultMap` 用来手动声明“数据库字段”和“Java 属性”的映射关系。

```xml
<resultMap id="DepartmentResultMap" type="com.example.week4.Department">
  <id property="id" column="id" />
  <result property="name" column="name" />
</resultMap>
```

意思接近：

```java
Department department = new Department();
department.setId(数据库里的 id);
department.setName(数据库里的 name);
```

然后查询时引用这个 resultMap：

```xml
<select id="findById" resultMap="DepartmentResultMap">
  select id, name from department where id = #{id}
</select>
```

注意：

```text
定义 resultMap 时用 id="DepartmentResultMap"
使用 resultMap 时用 resultMap="DepartmentResultMap"
```

不要写成：

```xml
<select id="findById" resultType="DepartmentResultMap">
```

这是错误的，因为 `DepartmentResultMap` 不是 Java 类型，而是一个映射规则的名字。

---

## 6. resultType 和 resultMap 的区别

| 写法 | 作用 | 适合场景 |
| --- | --- | --- |
| `resultType` | 直接指定返回 Java 类型 | 字段名和属性名一致，简单查询 |
| `resultMap` | 手动配置字段映射规则 | 字段名不一致、复杂查询、多表查询 |

简单理解：

```text
resultType：你直接告诉 MyBatis 返回什么类
resultMap：你告诉 MyBatis 每个字段怎么装进这个类
```

例如字段一致：

```sql
select id, name from department
```

```java
private int id;
private String name;
```

可以用：

```xml
resultType="com.example.week4.Department"
```

如果字段不一致：

```sql
select department_id, department_name from department
```

```java
private int id;
private String name;
```

更适合用：

```xml
<resultMap id="DepartmentResultMap" type="com.example.week4.Department">
  <id property="id" column="department_id" />
  <result property="name" column="department_name" />
</resultMap>
```

---

## 7. `<id>` 和 `<result>` 的区别

在 `resultMap` 里：

```xml
<id property="id" column="id" />
<result property="name" column="name" />
```

含义是：

```text
<id>      表示这个字段是当前对象的标识字段，通常对应数据库主键
<result>  表示普通字段
```

它们都在做字段映射：

```text
column   数据库字段名
property Java 对象属性名
```

所以：

```xml
<id property="id" column="id" />
```

表示：

```text
数据库 id 字段 -> Java 对象 id 属性
并告诉 MyBatis：这个字段是对象标识
```

```xml
<result property="name" column="name" />
```

表示：

```text
数据库 name 字段 -> Java 对象 name 属性
普通字段
```

重点：

```text
<id> 不会创建数据库主键
数据库主键由 create table ... primary key 决定
<id> 只是告诉 MyBatis：这个字段在映射结果中代表对象标识
```

一般情况下，`<id>` 应该和数据库真实主键对应。

---

## 8. 主键不叫 id 怎么写

主键字段不一定叫 `id`。

如果数据库是：

```sql
create table user (
  uid int primary key,
  name varchar(50)
);
```

Java 是：

```java
public class User {
  private int uid;
  private String name;
}
```

XML 写：

```xml
<resultMap id="UserResultMap" type="com.example.week4.User">
  <id property="uid" column="uid" />
  <result property="name" column="name" />
</resultMap>
```

如果数据库字段叫 `did`，Java 属性叫 `id`：

```sql
did int primary key
```

```java
private int id;
```

XML 写：

```xml
<id property="id" column="did" />
```

记住模板：

```xml
<id property="Java属性名" column="数据库字段名" />
<result property="Java属性名" column="数据库字段名" />
```

---

## 9. parameterType 是什么

`parameterType` 表示传入参数的类型。

```xml
<insert id="insert" parameterType="com.example.week4.Department">
  insert into department (id, name) values (#{id}, #{name})
</insert>
```

这里的参数是：

```java
int insert(Department department);
```

所以 `#{id}` 和 `#{name}` 会从 `department` 对象里取值：

```text
#{id}   -> department.getId()
#{name} -> department.getName()
```

很多时候 MyBatis 可以自动推断参数类型，所以 `parameterType` 不是每次都必须写。

学习阶段可以先写上，方便理解。

---

## 10. `#{}` 是什么

`#{}` 是 MyBatis 的安全参数占位符。

```xml
select id, name from department where id = #{id}
```

它不是字符串拼接，而是参数绑定。

可以理解成：

```sql
select id, name from department where id = ?
```

然后 MyBatis 把 Java 传进来的 `id` 放进去。

优点：

```text
安全
能防 SQL 注入
不用手动拼接字符串
```

日常开发优先使用 `#{}`。

---

## 11. @Param 和 `#{}` 的关系

Mapper：

```java
List<Department> findPage(
  @Param("offset") int offset,
  @Param("pageSize") int pageSize
);
```

XML：

```xml
<select id="findPage" resultMap="DepartmentResultMap">
  select id, name from department
  order by id
  limit #{offset}, #{pageSize}
</select>
```

对应关系：

```text
@Param("offset")   -> #{offset}
@Param("pageSize") -> #{pageSize}
```

如果 Mapper 方法有多个参数，建议都写 `@Param`。

---

## 12. select / insert / update / delete 标签

MyBatis XML 里常用四类标签：

```xml
<select id="findById">
</select>

<insert id="insert">
</insert>

<update id="updateById">
</update>

<delete id="deleteById">
</delete>
```

它们分别对应 SQL：

```sql
select
insert
update
delete
```

常见返回值：

```text
select 单条：对象或 null
select 多条：List<T>
insert：int，影响行数
update：int，影响行数
delete：int，影响行数
```

---

## 13. 分页查询示例

Mapper：

```java
long countAll();

List<Department> findPage(
  @Param("offset") int offset,
  @Param("pageSize") int pageSize
);
```

XML：

```xml
<select id="countAll" resultType="long">
  select count(*) from department
</select>

<select id="findPage" resultMap="DepartmentResultMap">
  select id, name from department
  order by id
  limit #{offset}, #{pageSize}
</select>
```

Service 里计算：

```java
int offset = (pageNum - 1) * pageSize;
```

注意：

```text
total 用 count(*) 查询总数
rows 用 limit 查询当前页数据
不能用 rows.size() 当 total
```

因为 `rows.size()` 只是当前页数量，不是数据库总数量。

---

## 14. 动态 SQL 先认识

真实项目里查询条件经常不是固定的。

例如：

```text
name 传了就按 name 查
name 没传就查全部
```

MyBatis XML 支持动态 SQL：

```xml
<select id="search" resultMap="DepartmentResultMap">
  select id, name from department
  <where>
    <if test="name != null and name != ''">
      name like concat('%', #{name}, '%')
    </if>
  </where>
</select>
```

先认识这些标签：

| 标签 | 作用 |
| --- | --- |
| `<if>` | 条件成立才拼接 SQL |
| `<where>` | 自动处理 where 和 and |
| `<set>` | 动态 update 时自动处理逗号 |
| `<foreach>` | 批量处理集合，比如批量删除 |

当前阶段不用急着背，后面做复杂查询时再深入。

---

## 15. 当前阶段最容易错的点

### 1. 把 resultMap 写成 resultType

错误：

```xml
<select id="findAll" resultType="DepartmentResultMap">
```

正确：

```xml
<select id="findAll" resultMap="DepartmentResultMap">
```

原因：

```text
DepartmentResultMap 是映射规则 id，不是 Java 类型
```

---

### 2. namespace 写错

错误：

```xml
<mapper namespace="DepartmentMapper">
```

推荐：

```xml
<mapper namespace="com.example.week4.DepartmentMapper">
```

原因：

```text
namespace 要写 Mapper 接口完整路径
```

---

### 3. id 和 Mapper 方法名不一致

错误：

```java
Department findById(int id);
```

```xml
<select id="getById">
```

正确：

```xml
<select id="findById">
```

---

### 4. 多参数不写 @Param

不推荐：

```java
List<Department> findPage(int offset, int pageSize);
```

推荐：

```java
List<Department> findPage(
  @Param("offset") int offset,
  @Param("pageSize") int pageSize
);
```

XML：

```xml
limit #{offset}, #{pageSize}
```

---

### 5. 查询列表误用单对象返回

错误：

```java
Department searchByName(String name);
```

如果模糊查询可能查出多条，应该写：

```java
List<Department> searchByName(String name);
```

---

## 16. 现在要掌握到什么程度

当前阶段不需要背完整 XML。

你要做到：

```text
能看懂 namespace 对 Mapper 接口
能看懂 id 对 Mapper 方法名
知道 resultType 是返回 Java 类型
知道 resultMap 是字段映射规则
知道 <id> 是对象标识字段，<result> 是普通字段
知道 @Param 对应 #{}
知道 insert/update/delete 返回 int 影响行数
知道分页需要 count + limit
```

能照着一个已有模块补一个新查询，就算过关。

---

## 17. 一句话总结

```text
MyBatis XML 的核心不是 XML 语法，而是四件事：方法怎么绑定 SQL，参数怎么传入，结果怎么映射，复杂 SQL 怎么维护。
```

