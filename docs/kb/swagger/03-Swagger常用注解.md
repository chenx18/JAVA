# 03-Swagger常用注解

Swagger 不写注解也能自动生成接口文档。

但是自动生成的文档有时不够清楚，所以可以用 Swagger 注解补充说明。

---

## 常用注解总览

学习阶段先掌握这些：

```text
@Tag：说明 Controller 模块
@Operation：说明接口作用
@Parameter：说明参数
@Schema：说明实体类或字段
@Hidden：隐藏接口
```

---

## @Tag

`@Tag` 一般写在 Controller 类上，用来说明这个 Controller 属于哪个模块。

```java
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "部门管理", description = "部门增删改查接口")
@RestController
@RequestMapping("/departments")
public class DepartmentController {
}
```

可以理解为：

```text
给这一组接口起一个分类名称。
```

---

## @Operation

`@Operation` 一般写在 Controller 方法上，用来说明接口用途。

```java
import io.swagger.v3.oas.annotations.Operation;

@Operation(summary = "根据 ID 查询部门")
@GetMapping("/{id}")
public ApiResponse<Department> getDepartmentById(@PathVariable int id) {
  return ApiResponse.success(service.getDepartmentById(id));
}
```

可以理解为：

```text
给单个接口写一句说明。
```

---

## @Parameter

`@Parameter` 用来说明某个接口参数。

```java
import io.swagger.v3.oas.annotations.Parameter;

@Operation(summary = "根据 ID 查询部门")
@GetMapping("/{id}")
public ApiResponse<Department> getDepartmentById(
    @Parameter(description = "部门 ID") @PathVariable int id) {
  return ApiResponse.success(service.getDepartmentById(id));
}
```

它不会改变接口逻辑，只是让文档更清楚。

---

## @Schema

`@Schema` 常用于实体类和字段说明。

```java
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "部门实体")
public class Department {

  @Schema(description = "部门 ID", example = "1")
  private Integer id;

  @Schema(description = "部门名称", example = "研发部")
  private String name;
}
```

Swagger 页面会根据这些说明展示字段含义。

---

## @Hidden

`@Hidden` 可以隐藏某些接口，不在 Swagger 页面显示。

```java
import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@GetMapping("/test")
public String test() {
  return "test";
}
```

适合隐藏临时测试接口、内部接口。

---

## 注意 ApiResponse 命名冲突

你的项目里通常会有自己的统一返回类：

```java
public class ApiResponse<T> {
}
```

Swagger 注解里也有一个名字叫 `ApiResponse`：

```java
io.swagger.v3.oas.annotations.responses.ApiResponse
```

所以如果以后引入 Swagger 的 `@ApiResponse` 注解，可能会和你自己的 `ApiResponse` 类名字冲突。

学习阶段可以先不用 Swagger 的 `@ApiResponse` 注解，先掌握 `@Tag`、`@Operation`、`@Parameter`、`@Schema`。
