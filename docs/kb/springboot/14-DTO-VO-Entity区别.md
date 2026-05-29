# 13-DTO-VO-Entity区别

DTO、VO、Entity 都是 Java 类，但它们在后端项目里的职责不同。

理解它们的关键不是背名字，而是看它们服务于哪一层。

---

## 一句话理解

```text
Entity：数据库对象
DTO：数据传输对象
VO：返回给前端展示的对象
```

更贴近当前项目的说法：

```text
Request：前端传进来
Entity：数据库使用
Response：后端传出去
```

---

## Entity 是什么

Entity 通常表示数据库表对应的对象。

例如数据库表：

```sql
create table department (
  id int primary key,
  name varchar(50) not null
);
```

对应 Java 对象：

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {
  private int id;
  private String name;
}
```

这个 `Department` 就可以理解为 Entity。

它主要给这些地方使用：

```text
Mapper
MyBatis
数据库查询结果映射
数据库新增/修改/删除
```

---

## DTO 是什么

DTO 全称是：

```text
Data Transfer Object
```

意思是数据传输对象。

在接口开发中，DTO 经常用于请求入参，也就是前端传给后端的数据。

例如新增部门：

```java
@Data
public class DepartmentCreateRequest {
  private int id;
  private String name;
}
```

修改部门：

```java
@Data
public class DepartmentUpdateRequest {
  private String name;
}
```

它们都可以理解为 DTO。

更具体的命名通常是：

```text
CreateRequest
UpdateRequest
QueryRequest
```

---

## VO 是什么

VO 常见意思是：

```text
View Object
```

在接口开发中，通常表示返回给前端展示的数据对象。

例如：

```java
@Data
public class DepartmentResponse {
  private int id;
  private String name;
}
```

这个 `DepartmentResponse` 就可以理解为 VO，也可以叫 Response DTO。

---

## 放到当前项目里怎么对应

当前 Department 模块可以这样理解：

```text
DepartmentCreateRequest：DTO，请求对象，新增时前端传给后端
DepartmentUpdateRequest：DTO，请求对象，修改时前端传给后端
DepartmentResponse：VO / Response，后端返回给前端
Department：Entity，数据库对象
```

---

## 完整请求流程

新增部门时：

```text
前端 JSON
  ↓
DepartmentCreateRequest DTO
  ↓
Service 转成 Department Entity
  ↓
Mapper 插入数据库
  ↓
Service 转成 DepartmentResponse VO
  ↓
Controller 返回给前端
```

可以简化为：

```text
DTO -> Entity -> VO
```

---

## 为什么要分开

因为这三件事不是同一件事：

```text
前端能传什么
数据库要存什么
前端能看到什么
```

例如数据库对象以后可能有：

```text
id
name
createdAt
updatedAt
deleted
createdBy
internalCode
```

但新增时前端可能只允许传：

```text
name
```

返回时前端可能只需要：

```text
id
name
```

所以不能总是一个对象用到底。

---

## 如果不分会有什么风险

如果直接用 Entity 接收前端请求，可能出现：

```json
{
  "id": 1,
  "name": "研发部",
  "deleted": true,
  "createdBy": "admin",
  "internalCode": "secret"
}
```

如果后端处理不谨慎，前端可能传入本不该传的字段。

如果直接把 Entity 返回给前端，也可能暴露内部字段。

---

## 命名不是绝对统一

不同公司、不同项目命名可能不同。

常见对应关系：

```text
Entity / DO / PO / Model / Domain：数据库对象
DTO / Request / Command：请求或传输对象
VO / Response / ViewObject：返回给前端对象
```

不要死记名字，重点看职责。

---

## 当前阶段推荐命名

学习阶段建议使用清晰命名：

```text
XxxCreateRequest
XxxUpdateRequest
XxxResponse
Xxx
```

例如：

```text
DepartmentCreateRequest
DepartmentUpdateRequest
DepartmentResponse
Department
```

这样比直接叫 `DepartmentDTO`、`DepartmentVO` 更容易理解。

---

## 和 Controller / Service / Mapper 的关系

```text
Controller：接收 Request，返回 Response
Service：处理业务，负责 Request -> Entity -> Response
Mapper：操作 Entity
```

代码对应：

```java
@PostMapping("/add")
public ApiResponse<DepartmentResponse> createDepartment(
    @Valid @RequestBody DepartmentCreateRequest request) {
  return ApiResponse.success(service.createDepartment(request));
}
```

```java
public DepartmentResponse createDepartment(DepartmentCreateRequest request) {
  Department department = new Department();
  department.setId(request.getId());
  department.setName(request.getName());

  mapper.insert(department);
  return toResponse(department);
}
```

---

## 一句话总结

```text
Request/DTO 管输入，Entity 管数据库，Response/VO 管输出。
```
