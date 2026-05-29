# 12-列表查询DTO与分页参数

DTO 不只用于 `POST` 和 `PUT` 的 JSON 请求体。

在后台管理系统里，列表查询经常也会有很多参数，例如：

```text
pageNum
pageSize
name
status
createBegin
createEnd
```

这些参数可以封装成一个专门的查询 DTO，也常叫 QueryRequest。

---

## 1. 一句话理解

```text
CreateRequest / UpdateRequest 用来接收新增和修改的请求体；
QueryRequest 用来接收列表查询的 query 参数；
Response / VO 用来返回给前端。
```

例如部门模块可以拆成：

```text
DepartmentCreateRequest  新增部门
DepartmentUpdateRequest  修改部门
DepartmentQueryRequest   查询部门列表
DepartmentResponse       返回部门数据
Department               数据库实体
```

---

## 2. 为什么列表查询也要 DTO

参数少时，可以直接写：

```java
@GetMapping("/list")
public ApiResponse<PageResponse<DepartmentResponse>> list(
  @RequestParam int pageNum,
  @RequestParam int pageSize,
  @RequestParam(required = false) String name
) {
  return ApiResponse.success(service.getDepartmentPages(pageNum, pageSize, name));
}
```

这种写法没错。

但真实后台列表页参数会越来越多：

```text
页码
每页数量
名称
状态
类型
创建开始时间
创建结束时间
排序字段
排序方式
```

如果全部写在 Controller 方法参数里，方法会越来越长。

所以可以封装成：

```java
DepartmentQueryRequest request
```

让 Controller 更干净，也方便统一写 Swagger 注解和校验规则。

---

## 3. Query DTO 示例

```java
package com.example.week4;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Schema(description = "部门分页查询参数")
public class DepartmentQueryRequest {

  @Schema(description = "页码", example = "1")
  @Min(value = 1, message = "pageNum must > 0")
  private int pageNum = 1;

  @Schema(description = "每页数量", example = "10")
  @Min(value = 1, message = "pageSize must > 0")
  private int pageSize = 10;

  @Schema(description = "部门名称", example = "研发")
  private String name;
}
```

这里有三个重点：

```text
pageNum / pageSize 给默认值，避免不传时变成 0
@Min 用于校验分页参数不能小于 1
name 是可选查询条件，可以不传
```

---

## 4. Controller 怎么接 Query DTO

推荐写法：

```java
import org.springdoc.core.annotations.ParameterObject;

@GetMapping("/list")
public ApiResponse<PageResponse<DepartmentResponse>> getDepartmentList(
  @Valid @ParameterObject DepartmentQueryRequest request
) {
  return ApiResponse.success(service.getDepartmentPages(request));
}
```

前端请求：

```text
GET /departments/list?pageNum=1&pageSize=10&name=研发
```

Spring 会自动把 query 参数放进对象：

```java
request.setPageNum(1);
request.setPageSize(10);
request.setName("研发");
```

---

## 5. @RequestParam 和 @ParameterObject 的区别

### 单个参数用 @RequestParam

```java
@RequestParam int pageNum
```

表示：

```text
从 query 参数里取 pageNum
```

前端：

```text
?pageNum=1
```

### 多个查询参数封装成对象用 @ParameterObject

```java
@ParameterObject DepartmentQueryRequest request
```

表示：

```text
这个对象的字段来自 query 参数
并让 Swagger 把字段展开显示
```

前端仍然是：

```text
?pageNum=1&pageSize=10&name=研发
```

注意：

```text
@ParameterObject 主要影响 Swagger 展示，不改变前端传参格式。
```

---

## 6. 不要这样写

不推荐：

```java
public ApiResponse<?> list(
  @RequestParam DepartmentQueryRequest request
) {
}
```

这样 Swagger 可能会认为：

```text
有一个 query 参数叫 request，类型是 object
```

页面上会显示成一个整体对象，而不是展开：

```text
pageNum
pageSize
name
```

正确写法：

```java
public ApiResponse<?> list(
  @Valid @ParameterObject DepartmentQueryRequest request
) {
}
```

---

## 7. GET 查询一般不用 @RequestBody

列表查询通常是：

```http
GET /departments/list?pageNum=1&pageSize=10&name=研发
```

不是：

```http
GET /departments/list
Content-Type: application/json

{
  "pageNum": 1,
  "pageSize": 10,
  "name": "研发"
}
```

所以普通列表查询不推荐用：

```java
@RequestBody DepartmentQueryRequest request
```

更常见的是：

```java
@ParameterObject DepartmentQueryRequest request
```

如果查询条件特别复杂，也可能用 `POST /xxx/search` 加请求体，但这是另一种设计。

---

## 8. Service 里的分页处理

Controller 只负责接收参数，Service 负责处理分页业务。

```java
public PageResponse<DepartmentResponse> getDepartmentPages(DepartmentQueryRequest request) {
  int pageNum = request.getPageNum();
  int pageSize = request.getPageSize();
  String name = request.getName();

  int offset = (pageNum - 1) * pageSize;

  List<DepartmentResponse> rows = mapper.findPage(name, offset, pageSize)
    .stream()
    .map(this::toResponse)
    .toList();

  long total = mapper.countQuery(name);

  return new PageResponse<>(total, rows);
}
```

重点：

```text
offset = (pageNum - 1) * pageSize
rows 是当前页数据
total 是符合条件的数据总数
```

---

## 9. count 和 list 条件必须一致

分页查询通常需要两条 SQL：

```text
countQuery  查总数
findPage    查当前页数据
```

如果列表按 `name` 查询：

```sql
where name like '%研发%'
```

那么总数也必须按 `name` 查询。

否则会出现：

```text
当前页只有 2 条研发部门
total 却显示全部部门数量
```

所以 Mapper 应该类似：

```java
long countQuery(@Param("name") String name);

List<Department> findPage(
  @Param("name") String name,
  @Param("offset") int offset,
  @Param("pageSize") int pageSize
);
```

---

## 10. MyBatis XML 动态 SQL

```xml
<select id="countQuery" resultType="long">
  select count(*) from department
  <where>
    <if test="name != null and name != ''">
      name like concat('%', #{name}, '%')
    </if>
  </where>
</select>

<select id="findPage" resultMap="DepartmentResultMap">
  select id, name from department
  <where>
    <if test="name != null and name != ''">
      name like concat('%', #{name}, '%')
    </if>
  </where>
  order by id
  limit #{offset}, #{pageSize}
</select>
```

`<if>` 表示：

```text
name 有值时，才拼接 name like ...
```

`<where>` 表示：

```text
里面有条件时自动加 where
里面没有条件时不加 where
```

---

## 11. 什么时候直接写 @RequestParam

参数少时可以直接写：

```text
1 到 3 个简单参数
不会复用
不会继续扩展
Controller 方法不长
```

例如：

```java
@GetMapping("/name")
public ApiResponse<DepartmentResponse> getByName(
  @RequestParam String name
) {
  return ApiResponse.success(service.getDepartmentByName(name));
}
```

---

## 12. 什么时候单独建 QueryRequest

推荐建 Query DTO 的场景：

```text
列表页筛选参数较多
有分页参数
需要 Swagger 注解
需要参数校验
多个接口复用同一组查询条件
后续可能继续扩展筛选字段
```

后台管理系统的列表接口，一般都适合用 QueryRequest。

---

## 13. 和 CreateRequest / UpdateRequest 的区别

| 类型 | 常见场景 | 前端传参 |
| --- | --- | --- |
| `CreateRequest` | 新增 | JSON body |
| `UpdateRequest` | 修改 | JSON body |
| `QueryRequest` | 列表查询 | query 参数 |
| `Response` / `VO` | 返回给前端 | 后端响应 |
| `Entity` | 数据库表对象 | 不直接暴露给前端 |

一句话：

```text
新增修改多用 @RequestBody DTO；
列表查询多用 QueryRequest 接 query 参数；
响应结果用 Response / VO；
数据库映射用 Entity。
```

---

## 14. 当前阶段要掌握到什么程度

你不用背注解组合。

要能理解：

```text
为什么列表查询要有 QueryRequest
为什么 GET 查询参数不是 @RequestBody
为什么 @ParameterObject 能让 Swagger 展开字段
为什么 count 和 findPage 要使用同一套查询条件
为什么 pageNum/pageSize 最好给默认值
```

能照着一个模块写出分页 + 条件查询，就算过关。

