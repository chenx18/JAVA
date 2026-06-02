# JOIN 查询

## 1. JOIN 查询解决什么问题

前面已经有两张表：

```text
department
department_log
```

它们的关系是：

```text
一个部门可以有多条操作日志
```

数据库里靠字段关联：

```text
department.id = department_log.department_id
```

如果只查部门表，只能拿到部门信息：

```sql
select id, name, description, status
from department
where id = 1;
```

如果只查日志表，只能拿到日志信息：

```sql
select id, department_id, operation, content, create_time
from department_log
where department_id = 1;
```

JOIN 查询解决的问题就是：

> 一次查询，把有关联的多张表数据查出来。

---

## 2. 示例项目中的表关系

主表：

```text
department
```

子表：

```text
department_log
```

关系：

```text
department.id
  ↓
department_log.department_id
```

可以理解成：

```text
department 是部门本体
department_log 是部门的操作记录
```

一个部门可能有多条日志：

```text
研发部
  - CREATE_DEPARTMENT
  - UPDATE_DEPARTMENT
  - DELETE_DEPARTMENT
```

这就是典型的一对多关系。

---

## 3. 不用 JOIN 怎么查

不用 JOIN 也可以查，只是要查两次。

第一步，查部门：

```sql
select id, name, description, status
from department
where id = 1;
```

第二步，查部门日志：

```sql
select id, department_id, operation, content, create_time
from department_log
where department_id = 1
order by id desc;
```

这种方式叫分步查询。

优点：

```text
简单
容易理解
每条 SQL 职责清楚
```

缺点：

```text
需要查询多次数据库
如果数据组合复杂，Service 里要自己组装结果
```

---

## 4. 用 JOIN 怎么查

JOIN 可以把两张表连接起来：

```sql
select
  d.id as department_id,
  d.name as department_name,
  d.description as department_description,
  d.status as department_status,
  l.id as log_id,
  l.operation as log_operation,
  l.content as log_content,
  l.create_time as log_create_time
from department d
left join department_log l on d.id = l.department_id
where d.id = 1
order by l.id desc;
```

这里的核心是：

```sql
left join department_log l on d.id = l.department_id
```

意思是：

```text
把 department 表和 department_log 表连接起来
连接条件是 department.id = department_log.department_id
```

---

## 5. JOIN 查询出来是什么样

JOIN 查询不是直接返回“一个部门对象里面套日志数组”。

数据库查出来本质上还是一张平铺表：

```text
department_id | department_name | log_id | log_operation
1             | 研发部           | 10     | CREATE_DEPARTMENT
1             | 研发部           | 11     | UPDATE_DEPARTMENT
1             | 研发部           | 12     | DELETE_DEPARTMENT
```

注意：

```text
部门信息会重复出现
日志信息每行不同
```

这是理解 JOIN 的关键。

后端最终想返回给前端的结构通常是：

```json
{
  "id": 1,
  "name": "研发部",
  "description": "负责研发",
  "status": 1,
  "logs": [
    {
      "id": 10,
      "operation": "CREATE_DEPARTMENT",
      "content": "Create department: 研发部",
      "createTime": "2026-06-01T14:00:00"
    },
    {
      "id": 11,
      "operation": "UPDATE_DEPARTMENT",
      "content": "Update department: 研发部",
      "createTime": "2026-06-01T15:00:00"
    }
  ]
}
```

所以实际开发里有两步：

```text
1. SQL JOIN 查出平铺数据
2. MyBatis resultMap 把平铺数据组装成对象嵌套结构
```

---

## 6. INNER JOIN 和 LEFT JOIN 区别

### 6.1 INNER JOIN

```sql
select ...
from department d
inner join department_log l on d.id = l.department_id
where d.id = 1;
```

意思是：

```text
两边都有匹配数据才返回
```

如果部门存在，但没有日志：

```text
INNER JOIN 查不到这个部门
```

### 6.2 LEFT JOIN

```sql
select ...
from department d
left join department_log l on d.id = l.department_id
where d.id = 1;
```

意思是：

```text
以左表 department 为主
即使没有日志，也返回部门
```

如果部门存在，但没有日志：

```text
LEFT JOIN 仍然能查到部门，只是日志字段为 null
```

---

## 7. 示例项目应该用哪种 JOIN

当前场景是：

```text
查询部门详情，同时带出日志
```

应该优先用：

```text
LEFT JOIN
```

原因：

```text
部门存在时，即使没有日志，也应该能查到部门详情
```

如果用 `INNER JOIN`，没有日志的部门会查不出来，这通常不符合“详情查询”的预期。

---

## 8. 返回对象应该怎么设计

不要直接把 `Department` 改得很复杂。

可以新增一个详情 VO：

```text
DepartmentDetailResponse
```

它表示接口返回给前端的部门详情：

```java
@Data
public class DepartmentDetailResponse {

  private Integer id;

  private String name;

  private String description;

  private Integer status;

  private List<DepartmentLog> logs;
}
```

为什么叫 Response/VO？

```text
因为它是给前端看的返回结构
不是数据库原始表结构
```

---

## 9. MyBatis resultMap 一对多映射

普通字段用：

```xml
<id property="id" column="department_id" />
<result property="name" column="department_name" />
```

一对多列表用：

```xml
<collection property="logs" ofType="com.example.week4.project.system.domain.DepartmentLog">
  ...
</collection>
```

完整示例：

```xml
<resultMap id="DepartmentDetailResultMap"
           type="com.example.week4.project.system.domain.vo.DepartmentDetailResponse">

  <id property="id" column="department_id" />
  <result property="name" column="department_name" />
  <result property="description" column="department_description" />
  <result property="status" column="department_status" />

  <collection property="logs" ofType="com.example.week4.project.system.domain.DepartmentLog">
    <id property="id" column="log_id" />
    <result property="departmentId" column="department_id" />
    <result property="operation" column="log_operation" />
    <result property="content" column="log_content" />
    <result property="createTime" column="log_create_time" />
  </collection>
</resultMap>
```

记法：

```text
property = Java 属性名
column   = SQL 查询出来的列名
```

---

## 10. JOIN SQL 和 resultMap 配合

Mapper XML 中可以这样写：

```xml
<select id="findDetailById" resultMap="DepartmentDetailResultMap">
  select
    d.id as department_id,
    d.name as department_name,
    d.description as department_description,
    d.status as department_status,
    l.id as log_id,
    l.operation as log_operation,
    l.content as log_content,
    l.create_time as log_create_time
  from department d
  left join department_log l on d.id = l.department_id
  where d.id = #{id}
  order by l.id desc
</select>
```

为什么要起这些别名？

因为两张表里可能都有 `id`：

```text
department.id
department_log.id
```

如果都叫 `id`，MyBatis 不知道哪个是部门 ID，哪个是日志 ID。

所以起别名：

```text
d.id as department_id
l.id as log_id
```

---

## 11. Mapper 接口写法

```java
DepartmentDetailResponse findDetailById(@Param("id") int id);
```

完整例子：

```java
@Mapper
public interface DepartmentMapper {

  DepartmentDetailResponse findDetailById(@Param("id") int id);
}
```

注意：

```text
接口方法名 findDetailById
必须和 XML 的 select id="findDetailById" 一致
```

---

## 12. Service 写法

```java
public DepartmentDetailResponse getDepartmentDetail(int id) {
  if (id <= 0) {
    throw new IllegalArgumentException("id must be > 0");
  }

  DepartmentDetailResponse detail = mapper.findDetailById(id);

  if (detail == null) {
    throw new DepartmentNotFoundException(id);
  }

  return detail;
}
```

这个方法只是查询，不需要 `@Transactional`。

---

## 13. Controller 写法

```java
@GetMapping("/{id}/detail")
public ApiResponse<DepartmentDetailResponse> getDepartmentDetail(
    @PathVariable int id) {
  return ApiResponse.success(service.getDepartmentDetail(id));
}
```

接口：

```http
GET /departments/{id}/detail
```

---

## 14. 代码实践步骤

建议按这个顺序写：

```text
1. 新建 DepartmentDetailResponse
2. DepartmentMapper 增加 findDetailById
3. DepartmentMapper.xml 增加 DepartmentDetailResultMap
4. DepartmentMapper.xml 增加 JOIN 查询 SQL
5. DepartmentService 增加 getDepartmentDetail
6. DepartmentController 增加 /{id}/detail 接口
7. 编译
8. 接口测试
```

不要一口气乱改，按链路走：

```text
VO -> Mapper -> XML -> Service -> Controller
```

---

## 15. 常见误区

### 15.1 忘记给重复字段起别名

不推荐写法：

```sql
select d.id, l.id
```

推荐：

```sql
select
  d.id as department_id,
  l.id as log_id
```

### 15.2 property 和 column 写反

不推荐写法：

```xml
<result property="department_id" column="departmentId" />
```

正确：

```xml
<result property="departmentId" column="department_id" />
```

记住：

```text
property 看 Java 字段
column 看 SQL 查询结果列名
```

### 15.3 一对多不用 collection

如果返回对象里有：

```java
private List<DepartmentLog> logs;
```

XML 里就应该用：

```xml
<collection property="logs" ...>
```

不是普通的：

```xml
<result property="logs" ... />
```

### 15.4 LEFT JOIN 写成 INNER JOIN

详情查询通常更适合：

```sql
left join
```

因为没有日志时，也应该能查到部门。

---

## 16. 面试怎么说

如果面试问：

> MyBatis 里一对多关联查询怎么做？

可以这样回答：

```text
一对多通常是主表和子表通过外键字段关联，比如 department.id 对应 department_log.department_id。
SQL 层可以用 left join 查询出平铺数据。
因为返回对象里通常是一个主对象包含一个 List 子对象，所以 MyBatis XML 里会用 resultMap，并通过 collection 映射子集合。
同时 SQL 里要注意给重复字段起别名，比如部门 id 叫 department_id，日志 id 叫 log_id，避免映射混乱。
```

---

## 17. 本节小结

本节重点：

```text
1. JOIN 是把有关联的表连接起来查询
2. JOIN 查出来本质上是平铺数据
3. LEFT JOIN 可以保留左表数据
4. 一对多返回结构通常需要 List
5. MyBatis 用 resultMap + collection 映射一对多
6. 多表字段重名时必须起别名
```

先不用急着掌握所有 JOIN 类型。

当前最重要的是能写出：

```text
部门详情 + 日志列表
```

这一类真实开发中常见的接口。

