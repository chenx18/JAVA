# 04-Controller与路由

Controller 是 Spring Boot 接口的入口。

前端、Apifox、Swagger 请求接口时，最先进入 Controller。

---

## @RestController

```java
@RestController
public class DepartmentController {
}
```

作用：

```text
告诉 Spring：这个类是接口控制器
方法返回值直接作为响应体返回
```

如果返回对象，Spring Boot 通常会把对象转成 JSON。

---

## @RequestMapping

```java
@RequestMapping("/departments")
public class DepartmentController {
}
```

作用：

```text
给当前 Controller 里的接口加统一前缀
```

例如类上有 `/departments`，方法上有 `/list`，最终接口是：

```text
/departments/list
```

---

## @GetMapping

定义 GET 接口。

```java
@GetMapping("/list")
public ApiResponse<List<Department>> list() {
  return ApiResponse.success(service.getAllDepartments());
}
```

常用于查询。

---

## @PostMapping

定义 POST 接口。

```java
@PostMapping("/add")
public ApiResponse<Department> create(@RequestBody Department d) {
  return ApiResponse.success(service.createDepartment(d));
}
```

常用于新增。

---

## @PutMapping

定义 PUT 接口。

```java
@PutMapping("/update/{id}")
public ApiResponse<Department> update(@PathVariable int id, @RequestBody Department d) {
  return ApiResponse.success(service.updateDepartment(id, d));
}
```

常用于修改。

---

## @DeleteMapping

定义 DELETE 接口。

```java
@DeleteMapping("/delete/{id}")
public ApiResponse<Void> delete(@PathVariable int id) {
  service.deleteDepartment(id);
  return ApiResponse.success(null);
}
```

常用于删除。

---

## Controller 的边界

Controller 应该保持薄一点。

它主要做：

```text
定义路由
接收参数
调用 Service
返回 ApiResponse
```

复杂业务判断应该放到 Service。
