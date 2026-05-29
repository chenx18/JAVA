# 06-Validation面试知识点

Validation 是 Spring Boot 项目中常见的参数校验能力。

面试中重点不是背所有注解，而是说明白它解决什么问题、如何使用、和 Service 校验有什么区别。

---

## Validation 是什么

可以回答：

```text
Validation 是参数校验机制，用来在请求进入业务逻辑前校验字段是否合法，比如不能为空、长度限制、数字范围、邮箱格式等。
```

---

## Spring Boot 中如何使用参数校验

可以回答：

```text
在实体类字段上添加校验注解，比如 @NotBlank、@NotNull、@Min、@Size，然后在 Controller 的 @RequestBody 参数前添加 @Valid 触发校验。校验失败后由全局异常处理统一返回错误结果。
```

示例：

```java
@PostMapping("/add")
public ApiResponse<Department> createDepartment(@Valid @RequestBody Department d) {
  return ApiResponse.success(service.createDepartment(d));
}
```

---

## 常用校验注解有哪些

常见回答：

```text
@NotNull：不能为 null
@NotBlank：字符串不能为空或空格
@NotEmpty：不能为 null，长度不能为 0
@Min / @Max：数字范围
@Size：长度或集合大小
@Email：邮箱格式
```

---

## @Valid 的作用是什么

可以回答：

```text
@Valid 用来触发对象字段上的校验规则。如果不加 @Valid，字段上的 @NotBlank、@Min 等注解通常不会在 Controller 请求体接收时自动生效。
```

---

## 校验失败会发生什么

可以回答：

```text
如果 @RequestBody 对象校验失败，Spring 通常会抛出 MethodArgumentNotValidException。项目可以通过 @RestControllerAdvice 和 @ExceptionHandler 统一捕获，然后返回统一错误格式。
```

---

## Validation 和 Service 校验有什么区别

可以回答：

```text
Validation 负责基础参数合法性，比如非空、长度、格式、范围；Service 负责业务规则，比如数据是否存在、名称是否重复、是否有权限、状态是否允许操作。
```

举例：

```text
name 不能为空：Validation
name 不能重复：Service
```

---

## 后端已经有前端校验，为什么还要后端校验

可以回答：

```text
前端校验主要提升用户体验，但不能保证安全。请求可以绕过前端直接调用后端接口，所以后端必须再次校验，避免非法数据进入业务逻辑或数据库。
```
