# 登录拦截器

登录拦截器用于解决“接口如何判断当前请求是否已经登录”的问题。

登录接口负责生成 JWT，登录拦截器负责在后续请求进入 Controller 之前校验 JWT。

---

## 1. 本节目录 / 学习目标

本节按以下路线学习：

```text
登录接口返回 JWT
  ↓
前端后续请求携带 Authorization
  ↓
WebConfig 注册 LoginInterceptor
  ↓
LoginInterceptor 在 Controller 前执行
  ↓
读取 Authorization 请求头
  ↓
去掉 Bearer 前缀
  ↓
JwtUtil 解析并校验 JWT
  ↓
通过则放行
  ↓
失败则返回 401
```

学完本节应能掌握：

- 为什么需要登录拦截器
- `HandlerInterceptor` 是什么
- `HttpServletRequest` 和 `HttpServletResponse` 分别代表什么
- `preHandle` 返回 `true` 和 `false` 的区别
- 如何配置需要拦截和需要放行的接口
- 如何从 `Authorization` 请求头中取出 JWT
- Swagger / Apifox 如何携带 `Authorization` 请求头

---

## 2. 完整请求流程

```text
请求进入后端
  ↓
WebConfig 判断当前路径是否需要拦截
  ↓
需要拦截则进入 LoginInterceptor.preHandle
  ↓
从请求头读取 Authorization
  ↓
判断是否为空
  ↓
去掉 Bearer 前缀
  ↓
调用 jwtUtil.parseToken(token)
  ↓
解析成功：return true，进入 Controller
  ↓
解析失败：写入 401，return false
```

---

## 3. LoginInterceptor 完整示例

建议放在：

```text
framework/interceptor/LoginInterceptor.java
```

完整代码：

```java
package com.example.week4.framework.interceptor;

import com.example.week4.framework.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
public class LoginInterceptor implements HandlerInterceptor {

  private final JwtUtil jwtUtil;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
      return true;
    }

    String token = request.getHeader("Authorization");

    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7);
    }

    if (token == null || token.isBlank()) {
      writeUnauthorized(response, "please login");
      return false;
    }

    try {
      jwtUtil.parseToken(token);
      return true;
    } catch (Exception e) {
      writeUnauthorized(response, "invalid token");
      return false;
    }
  }

  private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
    response.setStatus(401);
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().write("{\"code\":401,\"message\":\"" + message + "\",\"data\":null}");
  }
}
```

这段代码的核心不是“判断字符串是不是长得像 token”，而是调用 `jwtUtil.parseToken(token)` 真正解析和校验 JWT。

---

## 4. WebConfig 完整示例

建议放在：

```text
framework/config/WebConfig.java
```

完整代码：

```java
package com.example.week4.framework.config;

import com.example.week4.framework.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final LoginInterceptor loginInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(loginInterceptor)
        .addPathPatterns("/**")
        .excludePathPatterns(
            "/user/login",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**"
        );
  }
}
```

含义：

```text
拦截所有接口 /**
  ↓
但放行 /user/login
  ↓
同时放行 Swagger 页面和接口文档
```

为什么登录接口必须放行：

```text
用户还没登录时，需要先访问登录接口拿 JWT
如果登录接口也被拦截，就永远无法登录
```

---

## 5. Swagger Authorization 配置

如果希望 Swagger 页面统一填写 `Authorization` 请求头，可以增加 OpenAPI 配置。

建议放在：

```text
framework/config/SwaggerConfig.java
```

完整代码：

```java
package com.example.week4.framework.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  private static final String SECURITY_SCHEME_NAME = "Authorization";

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Spring Boot API")
            .version("v1"))
        .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
        .components(new Components().addSecuritySchemes(
            SECURITY_SCHEME_NAME,
            new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("Input: Bearer eyJhbGciOiJIUzI1NiJ9...")));
  }
}
```

配置后，在 Swagger 页面点击 `Authorize`，输入：

```text
Bearer 登录接口返回的JWT
```

后续请求会自动带上：

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## 6. 实现步骤

### 6.1 先保证登录接口能返回 JWT

请求：

```http
POST /user/login
```

Body：

```json
{
  "userName": "admin",
  "password": "123456"
}
```

成功返回：

```json
{
  "code": 200,
  "message": "success",
  "data": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### 6.2 新建 LoginInterceptor

`LoginInterceptor` 负责“怎么校验登录态”：

```text
读取 Authorization
  ↓
提取 token
  ↓
解析 JWT
  ↓
成功放行，失败拦截
```

---

### 6.3 新建 WebConfig 注册拦截器

`WebConfig` 负责“哪些路径要拦截”：

```text
/**：全部拦截
/user/login：放行
/swagger-ui/**：放行
/v3/api-docs/**：放行
```

---

### 6.4 测试无 token 请求

请求任意需要登录的接口，例如：

```http
GET /dict/list
```

不带请求头时，应返回：

```json
{
  "code": 401,
  "message": "please login",
  "data": null
}
```

---

### 6.5 测试错误 token 请求

请求头：

```http
Authorization: Bearer abc
```

应返回：

```json
{
  "code": 401,
  "message": "invalid token",
  "data": null
}
```

---

### 6.6 测试正确 JWT 请求

请求头：

```http
Authorization: Bearer 登录接口返回的JWT
```

再次请求：

```http
GET /dict/list
```

应正常进入 Controller，返回业务数据。

---

## 7. 核心知识沉淀

### 7.1 HandlerInterceptor 是什么

`HandlerInterceptor` 是 Spring MVC 提供的拦截器接口。

它可以在请求进入 Controller 前后插入逻辑：

```text
请求进入
  ↓
preHandle：Controller 执行前
  ↓
Controller 方法
  ↓
postHandle：Controller 执行后，视图渲染前
  ↓
afterCompletion：整个请求结束后
```

登录校验通常写在 `preHandle`。

---

### 7.2 preHandle 的返回值

```java
return true;
```

表示放行，请求继续进入 Controller。

```java
return false;
```

表示拦截，请求不会进入 Controller。

登录判断的核心就是：

```text
JWT 合法 -> return true
JWT 不合法 -> 写入 401 响应 -> return false
```

---

### 7.3 HttpServletRequest 是什么

`HttpServletRequest` 代表一次 HTTP 请求。

可以从里面读取：

- 请求方式：`request.getMethod()`
- 请求路径：`request.getRequestURI()`
- 请求头：`request.getHeader("Authorization")`
- 请求参数：`request.getParameter("name")`

前端类比理解：

```text
HttpServletRequest ≈ fetch 请求对象里的 method、headers、query、body 等信息
```

---

### 7.4 HttpServletResponse 是什么

`HttpServletResponse` 代表后端要返回给浏览器或前端的响应。

可以设置：

- 状态码：`response.setStatus(401)`
- 返回类型：`response.setContentType("application/json;charset=UTF-8")`
- 响应内容：`response.getWriter().write(...)`

前端类比理解：

```text
HttpServletResponse ≈ 后端手动组装 response status、headers、body
```

---

### 7.5 Authorization 和 Bearer

常见登录请求头格式：

```http
Authorization: Bearer token字符串
```

其中：

```text
Authorization：请求头名称
Bearer：认证方案，表示后面跟的是令牌
token字符串：真正的登录凭证
```

代码中：

```java
if (token != null && token.startsWith("Bearer ")) {
  token = token.substring(7);
}
```

含义是去掉 `Bearer ` 前缀，只保留真正的 JWT。

---

### 7.6 JwtUtil.parseToken 做了什么

```java
jwtUtil.parseToken(token);
```

这一步会校验：

```text
JWT 格式是否正确
JWT 签名是否正确
JWT 是否过期
JWT 是否能被当前密钥解析
```

如果校验失败，会抛出异常，所以拦截器中用 `try...catch` 包起来。

---

### 7.7 addPathPatterns 和 excludePathPatterns

```java
.addPathPatterns("/**")
```

表示所有路径都进入拦截器。

```java
.excludePathPatterns("/user/login")
```

表示登录接口不拦截。

---

## 8. 实际开发注意点

- 登录接口、Swagger 页面、OpenAPI 文档通常需要放行。
- 浏览器跨域预检请求 `OPTIONS` 一般要放行，否则前端可能还没发真实请求就被拦截。
- JWT 校验失败时应返回 `401 Unauthorized`。
- 拦截器里直接写 JSON 时，要设置 `application/json;charset=UTF-8`，避免中文乱码。
- 拦截器只做登录态检查，不建议塞入大量业务逻辑。
- 真实项目中，解析出用户 id 后，通常还会查询用户状态或 Redis 登录态。
- 如果需要在 Controller 中知道当前登录用户，后续可以把用户 id 放入 `ThreadLocal` 或请求属性中。

---

## 9. 常见误区

### 9.1 写了 LoginInterceptor，但没有注册

只写这个类不够：

```java
@Component
public class LoginInterceptor implements HandlerInterceptor {
}
```

还必须在 `WebConfig` 中注册：

```java
registry.addInterceptor(loginInterceptor)
    .addPathPatterns("/**");
```

---

### 9.2 忘记放行登录接口

错误后果：

```text
/user/login 也被拦截
  ↓
没有 JWT
  ↓
返回 401
  ↓
用户永远无法登录
```

所以必须配置：

```java
.excludePathPatterns("/user/login")
```

---

### 9.3 token 为空时直接 startsWith

错误写法：

```java
if (!token.startsWith("Bearer ")) {
}
```

如果请求头不存在，`token` 是 `null`，会出现空指针异常。

推荐先判空：

```java
if (token == null || token.isBlank()) {
  return false;
}
```

---

### 9.4 Bearer 后面少空格

正确：

```text
Bearer eyJhbGciOiJIUzI1NiJ9...
```

错误：

```text
BearereyJhbGciOiJIUzI1NiJ9...
Bearer: eyJhbGciOiJIUzI1NiJ9...
```

`Bearer` 和 token 中间需要一个空格。

---

### 9.5 Swagger 页面被拦截

如果 Swagger 打不开，常见原因是没有放行：

```java
"/swagger-ui/**",
"/swagger-ui.html",
"/v3/api-docs/**"
```

---

### 9.6 只解析 JWT，不处理异常

不推荐：

```java
jwtUtil.parseToken(token);
return true;
```

如果 token 无效，会直接抛异常，响应格式可能不统一。

推荐：

```java
try {
  jwtUtil.parseToken(token);
  return true;
} catch (Exception e) {
  writeUnauthorized(response, "invalid token");
  return false;
}
```

---

## 10. 面试小题

### 10.1 拦截器的作用是什么？

答：拦截器可以在请求进入 Controller 前后插入统一逻辑，例如登录校验、权限校验、日志记录等。

### 10.2 `preHandle` 返回 `true` 和 `false` 有什么区别？

答：返回 `true` 表示放行，请求继续进入 Controller；返回 `false` 表示拦截，请求不会进入 Controller，需要自己写响应内容。

### 10.3 为什么登录接口要放行？

答：用户登录前还没有 JWT，如果登录接口也被拦截，用户无法获取 JWT，系统会进入无法登录的死循环。

### 10.4 拦截器和全局异常处理有什么区别？

答：拦截器处理的是请求进入 Controller 前后的统一逻辑；全局异常处理主要处理 Controller、Service 等抛出的异常。拦截器中直接写响应并返回 `false` 时，不一定会进入全局异常处理。

### 10.5 JWT 校验失败一般返回什么状态码？

答：通常返回 `401 Unauthorized`，表示当前请求没有有效登录凭证。

---

## 11. 小结

JWT 登录拦截器的核心流程是：

```text
前端请求携带 Authorization
  ↓
WebConfig 决定是否进入 LoginInterceptor
  ↓
LoginInterceptor.preHandle 读取 JWT
  ↓
JwtUtil.parseToken 校验 JWT
  ↓
合法则 return true
  ↓
非法则 response 写 401 并 return false
```

这一节掌握后，登录体系就从“能登录”升级为“能保护接口”。后续可以继续升级：

```text
JWT
  ↓
密码加密 BCrypt
  ↓
Redis 登录态
  ↓
角色权限
  ↓
菜单权限
```
