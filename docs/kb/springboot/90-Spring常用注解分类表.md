# Spring 常用注解分类表

Spring Boot 项目里会出现大量 `@xxx` 写法，这些不是 Java 修饰符，而是注解。

注解的作用通常是：

```text
给 Spring / MyBatis / Swagger / Validation 等框架看的标记
```

---

## 1. 本节学习目标

学完本节应能分清：

```text
public / private / final / static 是 Java 修饰符
@Configuration / @Service / @RestController 是注解
```

并能知道常见注解大概属于哪一类：

```text
组件注册
接口路由
请求参数
参数校验
配置类
依赖注入
数据库映射
接口文档
异常处理
```

---

## 2. 注解和修饰符的区别

### 2.1 修饰符

修饰符是 Java 语言自带的关键字：

```java
public class UserService {
  private final UserMapper userMapper;

  public static void main(String[] args) {
  }
}
```

常见修饰符：

```text
public
private
protected
final
static
abstract
```

它们没有 `@`。

---

### 2.2 注解

注解以 `@` 开头：

```java
@Service
@RestController
@Configuration
@GetMapping
@RequestBody
```

注解通常是框架用来识别代码作用的标记。

最简单区分：

```text
没有 @：通常是 Java 修饰符或关键字
有 @：通常是注解
```

---

## 3. 组件注册类注解

这类注解用于把 Java 类交给 Spring 管理。

### 3.1 @Component

```java
@Component
public class LoginInterceptor {
}
```

含义：

```text
把这个类注册成 Spring Bean
```

它是最通用的组件注解。

---

### 3.2 @Service

```java
@Service
public class UserService {
}
```

含义：

```text
这是业务层组件
```

本质上也是注册 Bean，但语义更清楚。

---

### 3.3 @RestController

```java
@RestController
public class UserController {
}
```

含义：

```text
这是接口控制器，方法返回值会直接作为 HTTP 响应体
```

常用于 Controller 层。

---

### 3.4 @Configuration

```java
@Configuration
public class SwaggerConfig {
}
```

含义：

```text
这是 Spring 配置类
```

通常和 `@Bean` 搭配使用。

---

## 4. 依赖注入类注解

### 4.1 @RequiredArgsConstructor

```java
@RequiredArgsConstructor
@Service
public class UserService {
  private final UserMapper userMapper;
}
```

含义：

```text
由 Lombok 自动生成 final 字段的构造函数
Spring 再通过构造函数把依赖传进来
```

常用于替代手写构造函数。

---

### 4.2 @Autowired

```java
@Autowired
private UserMapper userMapper;
```

含义：

```text
让 Spring 自动注入依赖
```

实际开发更推荐构造函数注入，也就是 `final + @RequiredArgsConstructor`。

---

## 5. 配置对象类注解

### 5.1 @Bean

```java
@Bean
public OpenAPI openAPI() {
  return new OpenAPI();
}
```

含义：

```text
把方法返回的对象交给 Spring 管理
```

常出现在 `@Configuration` 配置类中。

关系：

```text
@Configuration：这个类是配置类
@Bean：这个方法返回一个需要交给 Spring 管理的对象
```

---

## 6. Controller 路由类注解

### 6.1 @RequestMapping

```java
@RequestMapping("/user")
public class UserController {
}
```

含义：

```text
给当前 Controller 设置统一路径前缀
```

---

### 6.2 @GetMapping

```java
@GetMapping("/list")
public ApiResponse<?> list() {
}
```

含义：

```text
处理 GET 请求
```

常用于查询。

---

### 6.3 @PostMapping

```java
@PostMapping("/login")
public ApiResponse<?> login() {
}
```

含义：

```text
处理 POST 请求
```

常用于新增、登录、提交表单。

---

### 6.4 @PutMapping

```java
@PutMapping("/update/{id}")
public ApiResponse<?> update() {
}
```

含义：

```text
处理 PUT 请求
```

常用于修改。

---

### 6.5 @DeleteMapping

```java
@DeleteMapping("/delete/{ids}")
public ApiResponse<?> delete() {
}
```

含义：

```text
处理 DELETE 请求
```

常用于删除。

---

## 7. 请求参数类注解

### 7.1 @RequestBody

```java
@PostMapping("/login")
public ApiResponse<?> login(@RequestBody LoginRequest request) {
}
```

含义：

```text
从请求 body 的 JSON 中读取参数
```

常用于 POST / PUT。

---

### 7.2 @PathVariable

```java
@GetMapping("/{id}")
public ApiResponse<?> getById(@PathVariable Integer id) {
}
```

含义：

```text
从 URL 路径中读取参数
```

例如：

```text
/user/1 -> id = 1
```

---

### 7.3 @RequestParam

```java
@GetMapping("/search")
public ApiResponse<?> search(@RequestParam String keyword) {
}
```

含义：

```text
从 query 参数中读取值
```

例如：

```text
/user/search?keyword=admin
```

---

### 7.4 @ParameterObject

```java
@GetMapping("/list")
public ApiResponse<?> list(@ParameterObject User user) {
}
```

含义：

```text
让 Swagger 更好展示 GET 查询对象里的字段
```

它主要服务 Swagger 文档展示，不是 Spring MVC 必须注解。

---

## 8. 参数校验类注解

### 8.1 @Valid

```java
@PostMapping("/login")
public ApiResponse<?> login(@Valid @RequestBody LoginRequest request) {
}
```

含义：

```text
触发参数校验
```

---

### 8.2 @NotBlank

```java
@NotBlank(message = "username is blank")
private String userName;
```

含义：

```text
字符串不能为空，且不能全是空格
```

---

### 8.3 @Min

```java
@Min(value = 1, message = "pageNum must > 0")
private Integer pageNum;
```

含义：

```text
数字不能小于指定值
```

---

## 9. MyBatis 类注解

### 9.1 @Mapper

```java
@Mapper
public interface UserMapper {
}
```

含义：

```text
告诉 MyBatis 这是 Mapper 接口
```

---

### 9.2 @Param

```java
User findByUserName(@Param("userName") String userName);
```

含义：

```text
给 Mapper 方法参数起一个名字，方便 XML 使用
```

对应 XML：

```xml
where user_name = #{userName}
```

---

## 10. 异常处理类注解

### 10.1 @RestControllerAdvice

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
}
```

含义：

```text
全局处理 Controller 抛出的异常，并返回 JSON
```

---

### 10.2 @ExceptionHandler

```java
@ExceptionHandler(IllegalArgumentException.class)
public ApiResponse<?> handleIllegalArgument(IllegalArgumentException e) {
}
```

含义：

```text
指定某个方法处理某类异常
```

---

## 11. Swagger 文档类注解

### 11.1 @Tag

```java
@Tag(name = "用户管理")
public class UserController {
}
```

含义：

```text
给接口分组
```

---

### 11.2 @Operation

```java
@Operation(summary = "用户登录")
@PostMapping("/login")
public ApiResponse<?> login() {
}
```

含义：

```text
给接口方法添加说明
```

---

### 11.3 @Schema

```java
@Schema(description = "用户名")
private String userName;
```

含义：

```text
给字段添加接口文档说明
```

---

## 12. 注解学习优先级

### 12.1 必须熟悉

```text
@RestController
@RequestMapping
@GetMapping
@PostMapping
@RequestBody
@PathVariable
@Service
@Mapper
@Param
```

这些是写 CRUD 和登录每天都会碰到的。

---

### 12.2 需要理解

```text
@Configuration
@Bean
@Component
@RequiredArgsConstructor
@Valid
@NotBlank
@RestControllerAdvice
@ExceptionHandler
```

这些不一定每天手写，但必须知道作用。

---

### 12.3 可以后查

```text
@Tag
@Operation
@Schema
@ParameterObject
```

这些主要服务 Swagger 文档，忘了可以查。

---

## 13. 面试小题

### 13.1 @Configuration 是什么？

答：`@Configuration` 是 Spring 注解，表示当前类是配置类。Spring 启动时会扫描它，并读取其中通过 `@Bean` 等方式声明的配置对象。

### 13.2 @Service 和 @Component 有什么区别？

答：两者都可以把类注册为 Spring Bean。`@Service` 语义更明确，表示业务层组件；`@Component` 更通用。

### 13.3 @RequestBody 和 @PathVariable 有什么区别？

答：`@RequestBody` 从请求体 JSON 中读取参数，常用于 POST/PUT；`@PathVariable` 从 URL 路径中读取参数，例如 `/user/{id}`。

### 13.4 @Param 是干什么的？

答：`@Param` 给 MyBatis Mapper 方法参数起名字，XML 中可以通过 `#{参数名}` 引用。

---

## 14. 小结

注解不是修饰符。

最简单判断：

```text
public / private / final / static：Java 修饰符
@Service / @Configuration / @RequestBody：注解
```

学习 Spring Boot 时，不需要一次背完所有注解，但要能判断它属于哪一类，解决什么问题。