# 03-Controller中使用Valid

字段上写了校验注解后，还需要在 Controller 参数上使用 `@Valid` 才会触发校验。

---

## @Valid 的作用

一句话：

```text
@Valid = 请 Spring 检查这个对象里的字段规则。
```

例如：

```java
@PostMapping("/add")
public ApiResponse<Department> createDepartment(@Valid @RequestBody Department d) {
  return ApiResponse.success(service.createDepartment(d));
}
```

这里的执行过程是：

```text
@RequestBody 把 JSON 转成 Department
@Valid 检查 Department 字段上的注解
校验通过才调用 service.createDepartment(d)
```

---

## 常用导入

```java
import jakarta.validation.Valid;
```

注意不是旧版的：

```java
javax.validation.Valid
```

Spring Boot 3 使用的是 `jakarta.validation.Valid`。

---

## 新增接口使用 @Valid

```java
@PostMapping("/add")
public ApiResponse<Department> createDepartment(@Valid @RequestBody Department d) {
  return ApiResponse.success(service.createDepartment(d));
}
```

适合校验请求体中的字段。

---

## 修改接口使用 @Valid

```java
@PutMapping("/update/{id}")
public ApiResponse<Department> updateDepartment(
    @PathVariable int id,
    @Valid @RequestBody Department d) {
  return ApiResponse.success(service.updateDepartment(id, d));
}
```

这里要注意：

```text
id 来自路径
name 等修改字段来自请求体
```

如果请求体中的字段需要校验，就给 `@RequestBody` 前加 `@Valid`。

---

## @Valid 不会改变接口路径

`@Valid` 只影响参数校验。

它不会改变：

```text
接口地址
请求方式
返回 JSON 格式
数据库操作
```

它只决定：

```text
请求数据不合法时，要不要在进入 Service 前拦下来。
```
