# 08-自增主键与useGeneratedKeys

真实项目里，新增数据时通常不让前端传 `id`。

更常见的流程是：

```text
前端只传业务字段
数据库自动生成主键 id
MyBatis 把生成后的 id 回填到 Java 对象
Service 返回带 id 的响应数据
```

---

## 1. 为什么新增时通常不传 id

学习阶段可能会这样新增：

```json
{
  "id": 1,
  "name": "研发部"
}
```

但真实项目更常见的是：

```json
{
  "name": "研发部",
  "description": "负责研发",
  "status": 1
}
```

原因：

```text
id 是数据库主键，用来唯一标识一条记录
前端不应该决定主键
主键通常由数据库或后端统一生成
```

如果每个前端都能自己传 id，很容易出现：

```text
id 冲突
数据覆盖风险
主键规则混乱
```

---

## 2. 数据库表需要 auto_increment

MySQL 表里主键一般这样设计：

```sql
create table department (
  id int primary key auto_increment,
  name varchar(50) not null,
  description varchar(255),
  status int default 1
);
```

如果表已经存在，可以查看结构：

```sql
desc department;
```

`id` 那一行的 `Extra` 应该包含：

```text
auto_increment
```

如果没有，可以修改：

```sql
alter table department modify column id int not null auto_increment;
```

前提是：

```text
id 已经是主键
已有数据的 id 没有重复
```

---

## 3. CreateRequest 不需要 id

新增请求 DTO 不再写 `id`：

```java
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Department create request")
public class DepartmentCreateRequest {

  @Schema(description = "Department name", example = "Research")
  @NotBlank(message = "department name cannot be blank")
  private String name;

  @Schema(description = "Department description", example = "Responsible for product research")
  private String description;

  @Schema(description = "Department status", example = "1")
  private Integer status;
}
```

前端新增时只传业务字段：

```json
{
  "name": "研发部",
  "description": "负责研发",
  "status": 1
}
```

---

## 4. Service 不再 setId

新增时不设置 `id`：

```java
public DepartmentResponse createDepartment(DepartmentCreateRequest request) {
  if (request == null) {
    throw new IllegalArgumentException("Department is null");
  }

  if (mapper.existsByName(request.getName())) {
    throw new IllegalArgumentException("department name already exists");
  }

  validateStatus(request.getStatus());

  Department department = new Department();
  department.setName(request.getName());
  department.setDescription(request.getDescription());
  department.setStatus(request.getStatus());

  mapper.insert(department);

  return toResponse(department);
}
```

注意：

```text
mapper.insert(department) 执行后，department.id 会被 MyBatis 回填
```

---

## 5. MyBatis XML 配置 useGeneratedKeys

Mapper XML：

```xml
<insert
  id="insert"
  parameterType="com.example.week4.Department"
  useGeneratedKeys="true"
  keyProperty="id">
  insert into department (name, description, status)
  values (#{name}, #{description}, #{status})
</insert>
```

重点是：

```text
useGeneratedKeys="true"
keyProperty="id"
```

含义：

```text
useGeneratedKeys="true"：使用数据库生成的主键
keyProperty="id"：把生成的主键回填到 Java 对象的 id 属性
```

---

## 6. 新增后的对象发生了什么

执行前：

```java
Department department = new Department();
department.setName("研发部");
department.setDescription("负责研发");
department.setStatus(1);
```

此时：

```text
department.id = 0
```

执行：

```java
mapper.insert(department);
```

数据库生成主键，例如：

```text
id = 5
```

MyBatis 回填：

```text
department.id = 5
```

所以返回：

```java
return toResponse(department);
```

前端可以拿到：

```json
{
  "id": 5,
  "name": "研发部",
  "description": "负责研发",
  "status": 1
}
```

---

## 7. Mapper 接口不用特殊写

Mapper 接口可以保持普通写法：

```java
int insert(Department department);
```

返回值 `int` 表示影响行数：

```text
1：插入成功
0：没有插入
```

自增主键不是通过返回值拿，而是回填到传入的 `department` 对象里。

---

## 8. 常见错误

### 1. 忘记设置数据库 auto_increment

如果数据库 id 不是自增，但 insert 又不插入 id：

```xml
insert into department (name, description, status)
values (#{name}, #{description}, #{status})
```

可能报错：

```text
Field 'id' doesn't have a default value
```

处理：

```sql
alter table department modify column id int not null auto_increment;
```

---

### 2. useGeneratedKeys 大小写写错

错误：

```xml
useGeneratedkeys="true"
```

正确：

```xml
useGeneratedKeys="true"
```

`Keys` 的 `K` 要大写。

---

### 3. keyProperty 写错

如果 Java 对象属性叫：

```java
private int id;
```

XML 应该写：

```xml
keyProperty="id"
```

如果写成：

```xml
keyProperty="departmentId"
```

但 Java 对象没有这个属性，就无法正确回填。

---

### 4. insert 里仍然插入 id

新增不传 id 时，insert 不要写：

```xml
insert into department (id, name)
values (#{id}, #{name})
```

应该写：

```xml
insert into department (name, description, status)
values (#{name}, #{description}, #{status})
```

---

### 5. 误以为 insert 返回值就是 id

错误理解：

```java
int id = mapper.insert(department);
```

实际上：

```text
insert 返回的是影响行数，不是生成的 id
生成的 id 在 department.getId() 里
```

---

## 9. 和 Service 校验的关系

新增时通常还会做业务校验：

```text
name 必填
name 不能重复
status 必须是合法值
```

例如：

```java
if (mapper.existsByName(request.getName())) {
  throw new IllegalArgumentException("department name already exists");
}
```

数据库也可以加唯一索引兜底：

```sql
alter table department add unique index uk_department_name (name);
```

推荐理解：

```text
Service 提前判断，给前端友好错误
数据库约束最后兜底，保证数据不脏
```

---

## 10. 一句话总结

```text
新增不传 id 时，让数据库 auto_increment 生成主键，再用 MyBatis 的 useGeneratedKeys + keyProperty 把 id 回填到 Java 对象。
```

