# 11-DTO请求对象与响应对象

DTO 用来在接口层传输数据。

在 Spring Boot 项目中，常见做法是把请求对象、数据库对象、响应对象分开。

---

## 一句话理解

```text
DTO = 接口传输对象，用来定义前端能传什么、后端返回什么。
```

以部门模块为例：

```text
DepartmentCreateRequest：新增时前端能传什么
DepartmentUpdateRequest：修改时前端能传什么
DepartmentResponse：后端返回给前端什么
Department：数据库对象
```

---

## 为什么不直接使用数据库对象

学习阶段可以直接用：

```java
@RequestBody Department department
```

但真实项目不推荐一直这样做。

因为数据库对象可能包含很多字段：

```text
id
name
createdAt
updatedAt
deleted
createdBy
internalCode
```

前端新增时不一定应该传这些字段。

接口返回时也不一定应该把这些字段全部暴露给前端。

---

## 常见对象分工

```text
CreateRequest：新增请求对象
UpdateRequest：修改请求对象
Response：接口响应对象
Entity / Model：数据库对象
```

例如：

```text
DepartmentCreateRequest
DepartmentUpdateRequest
DepartmentResponse
Department
```

---

## 新增请求对象

新增请求对象定义前端新增时允许传什么。

```java
public class DepartmentCreateRequest {

  @Min(value = 1, message = "部门ID必须大于0")
  private int id;

  @NotBlank(message = "部门名称不能为空")
  private String name;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
```

请求示例：

```json
{
  "id": 1,
  "name": "研发部"
}
```

---

## 修改请求对象

修改时，`id` 通常来自路径，不需要放在请求体里。

```java
public class DepartmentUpdateRequest {

  @NotBlank(message = "部门名称不能为空")
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
```

请求示例：

```text
PUT /departments/update/1
```

请求体：

```json
{
  "name": "研发中心"
}
```

---

## 响应对象

响应对象定义后端返回给前端什么。

```java
public class DepartmentResponse {
  private int id;
  private String name;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
```

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "研发部"
  }
}
```

---

## Controller 中使用 DTO

Controller 接收 Request，返回 Response。

```java
@PostMapping("/add")
public ApiResponse<DepartmentResponse> createDepartment(
    @Valid @RequestBody DepartmentCreateRequest request) {
  return ApiResponse.success(service.createDepartment(request));
}
```

```java
@PutMapping("/update/{id}")
public ApiResponse<DepartmentResponse> updateDepartment(
    @PathVariable int id,
    @Valid @RequestBody DepartmentUpdateRequest request) {
  return ApiResponse.success(service.updateDepartment(id, request));
}
```

---

## Service 中转换对象

Service 负责把请求对象转成数据库对象。

```java
public DepartmentResponse createDepartment(DepartmentCreateRequest request) {
  Department department = new Department();
  department.setId(request.getId());
  department.setName(request.getName());

  mapper.insert(department);
  return toResponse(department);
}
```

修改时：

```java
public DepartmentResponse updateDepartment(int id, DepartmentUpdateRequest request) {
  Department department = new Department();
  department.setId(id);
  department.setName(request.getName());

  int rows = mapper.updateById(department);
  if (rows == 0) {
    throw new DepartmentNotFoundException(id);
  }

  return toResponse(department);
}
```

---

## Entity 转 Response

可以写一个私有转换方法：

```java
private DepartmentResponse toResponse(Department department) {
  DepartmentResponse response = new DepartmentResponse();
  response.setId(department.getId());
  response.setName(department.getName());
  return response;
}
```

这表示：

```text
数据库对象 Department -> 接口响应对象 DepartmentResponse
```

---

## 列表转换

Mapper 查询出来的是：

```text
List<Department>
```

接口要返回：

```text
List<DepartmentResponse>
```

可以这样转换：

```java
public List<DepartmentResponse> getAllDepartments() {
  return mapper.findAll()
      .stream()
      .map(this::toResponse)
      .toList();
}
```

理解：

```text
stream：把列表变成处理流水线
map：把每个 Department 转成 DepartmentResponse
toList：重新收集成列表
```

---

## 分层后的完整关系

```text
Controller
  接收 DepartmentCreateRequest / DepartmentUpdateRequest
  返回 DepartmentResponse

Service
  处理业务
  Request -> Department
  Department -> Response

Mapper
  操作 Department

Department
  数据库对象
```

---

## 为什么这样更接近真实开发

这样做可以避免：

```text
前端传入不该传的字段
后端返回不该暴露的字段
数据库结构变化直接影响接口结构
一个对象承担太多职责
```

虽然类会变多，但边界更清楚。

真实项目常见：

```text
XxxCreateRequest
XxxUpdateRequest
XxxQueryRequest
XxxResponse
XxxVO
XxxEntity
```

---

## 一句话总结

```text
DTO 的核心不是多写类，而是把“前端能传什么、数据库存什么、接口返回什么”分清楚。
```
