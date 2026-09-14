# 最小登录流程

最小登录用于理解“用户输入账号密码后，后端如何判断是否允许登录，并返回登录凭证”。

本节直接使用 JWT 作为登录凭证，让登录流程更接近实际开发。

---

## 1. 本节目录 / 学习目标

本节按以下顺序理解：

```text
登录请求对象 LoginRequest
  ↓
Controller 接收 /user/login
  ↓
Service 校验账号密码
  ↓
Mapper 根据 userName 查用户
  ↓
XML 查询 sys_user
  ↓
Service 比较密码
  ↓
JwtUtil 生成 JWT
  ↓
Controller 返回 token 给前端
```

完成本节后，应掌握：

- 登录接口为什么不能直接返回完整用户对象
- `LoginRequest` 为什么要单独定义
- `user_name` 和 `userName` 如何通过 MyBatis 映射
- JWT 在登录流程中承担什么角色
- 登录成功后前端后续请求应该如何携带 token

后续继续进入：

```text
登录拦截器
  ↓
密码加密 BCrypt
  ↓
角色权限
  ↓
Redis 保存登录状态
```

---

## 2. 登录接口完整流程

```text
POST /user/login
  ↓
@RequestBody LoginRequest
  ↓
UserService.login(request)
  ↓
userMapper.findByUserName(userName)
  ↓
校验用户是否存在
  ↓
校验密码是否正确
  ↓
jwtUtil.generateToken(userId, userName)
  ↓
返回 ApiResponse<String>
```

成功返回的数据结构：

```json
{
  "code": 200,
  "message": "success",
  "data": "eyJhbGciOiJIUzI1NiJ9..."
}
```

其中 `data` 就是后续请求要携带的 JWT。

---

## 3. 引入 JWT 依赖

在 `pom.xml` 中加入：

```xml
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-api</artifactId>
  <version>0.13.0</version>
</dependency>

<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-impl</artifactId>
  <version>0.13.0</version>
  <scope>runtime</scope>
</dependency>

<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-jackson</artifactId>
  <version>0.13.0</version>
  <scope>runtime</scope>
</dependency>
```

三个依赖的作用：

```text
jjwt-api：写代码时使用的 JWT API
jjwt-impl：JWT 的具体实现
jjwt-jackson：处理 JWT 内部 JSON 序列化和反序列化
```

---

## 4. JWT 工具类

建议放在：

```text
framework/security/JwtUtil.java
```

完整示例：

```java
package com.example.week4.framework.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

  private static final String SECRET = "week4-jwt-secret-key-must-be-at-least-32-bytes";
  private static final long EXPIRE_TIME = 1000 * 60 * 60 * 2;

  private SecretKey getSignKey() {
    return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(Integer userId, String userName) {
    Date now = new Date();
    Date expireDate = new Date(now.getTime() + EXPIRE_TIME);

    return Jwts.builder()
        .subject(String.valueOf(userId))
        .claim("userName", userName)
        .issuedAt(now)
        .expiration(expireDate)
        .signWith(getSignKey())
        .compact();
  }

  public Claims parseToken(String token) {
    return Jwts.parser()
        .verifyWith(getSignKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}
```

说明：

```text
generateToken：登录成功时生成 token
parseToken：后续请求时解析和校验 token
subject：通常放用户 id
claim：放额外业务字段，例如 userName
issuedAt：签发时间
expiration：过期时间
signWith：使用密钥签名
```

注意：JWT 默认不是加密，而是编码加签名，所以不要把密码、身份证号等敏感信息放进 JWT。

---

## 5. 请求对象 LoginRequest

```java
package com.example.week4.project.system.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

  @NotBlank(message = "username is blank")
  private String userName;

  @NotBlank(message = "password is blank")
  private String password;
}
```

登录接口只需要账号和密码，所以单独使用 `LoginRequest`，不建议直接使用完整 `User` 对象。

原因：

```text
User 是用户实体，字段可能很多
LoginRequest 是登录请求，只表达登录需要的字段
请求对象越明确，接口边界越清晰
```

---

## 6. Controller

```java
@PostMapping("/login")
public ApiResponse<String> login(@Valid @RequestBody LoginRequest request) {
  return ApiResponse.success(userService.login(request));
}
```

这里返回 `ApiResponse<String>`，因为登录成功后返回的是 JWT 字符串。

`@Valid` 会触发 `LoginRequest` 中的 `@NotBlank` 校验。

---

## 7. Service

```java
@RequiredArgsConstructor
@Service
public class UserService {

  private final UserMapper userMapper;
  private final JwtUtil jwtUtil;

  public String login(LoginRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("Login fail");
    }

    User user = userMapper.findByUserName(request.getUserName());
    if (user == null) {
      throw new IllegalArgumentException("Username or password error");
    }

    if (!request.getPassword().equals(user.getPassword())) {
      throw new IllegalArgumentException("Username or password error");
    }

    return jwtUtil.generateToken(user.getId(), user.getUserName());
  }
}
```

登录失败时，不建议分别提示“用户名不存在”或“密码错误”，统一返回“用户名或密码错误”更安全。

第一版可以明文密码比较，目的是先理解登录和 JWT 流程。真实项目需要升级为 BCrypt 等密码加密方案。

---

## 8. Mapper

```java
User findByUserName(@Param("userName") String userName);
```

`@Param("userName")` 对应 XML 中的 `#{userName}`。

---

## 9. XML

```xml
<resultMap id="UserResultMap" type="com.example.week4.project.system.domain.User">
  <id property="id" column="id" />
  <result property="userName" column="user_name" />
  <result property="password" column="password" />
  <result property="nickName" column="nick_name" />
  <result property="email" column="email" />
  <result property="status" column="status" />
</resultMap>

<select id="findByUserName" resultMap="UserResultMap">
  select id, user_name, password, nick_name, email, status
  from sys_user
  where user_name = #{userName}
</select>
```

规则：

```text
SQL 里的 user_name 是数据库字段
Java 里的 userName 是实体属性
resultMap 负责把 user_name 映射到 userName
```

---

## 10. 数据库表

推荐使用标准下划线命名：

```sql
create table sys_user (
  id int primary key auto_increment,
  user_name varchar(50) not null unique,
  password varchar(100) not null,
  nick_name varchar(50),
  email varchar(100),
  status int default 1
);
```

测试数据：

```sql
insert into sys_user (user_name, password, nick_name, email, status)
values ('admin', '123456', '管理员', 'admin@example.com', 1);
```

---

## 11. 接口测试

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

密码错误返回：

```json
{
  "code": 400,
  "message": "Username or password error",
  "data": null
}
```

具体 `code` 取决于全局异常处理里的映射规则。

---

## 12. JWT 返回后前端怎么用

登录成功后，前端保存 token。

后续请求接口时，在请求头里携带：

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

后端登录拦截器会读取 `Authorization`，解析 JWT，校验通过后才允许进入 Controller。

---

## 13. 核心知识沉淀

登录不是一个全新结构，本质仍然是：

```text
Controller 接请求
  ↓
Service 做业务判断
  ↓
Mapper 查数据库
  ↓
XML 写 SQL
  ↓
Service 返回结果
```

登录比普通 CRUD 多了三件事：

```text
根据用户名查用户
比较请求密码和数据库密码
生成 JWT 登录凭证
```

JWT 可以理解为：

```text
后端签发的一张登录门票
```

它通常包含：

```text
用户 id
用户名
签发时间
过期时间
签名
```

---

## 14. 实际开发注意点

- 登录接口一般不直接返回完整用户对象，避免返回 password 等敏感字段。
- 登录失败提示建议统一，避免暴露系统中是否存在某个用户名。
- JWT 不要存放密码、手机号、身份证号等敏感数据。
- JWT 的密钥不能写得太短，也不应该硬编码在公开仓库中。
- 真实项目中密码必须加密存储，不能明文保存。
- 查询列表接口不要因为共用 `User` 实体而误用 `@Valid`，否则可能要求查询时也必须传用户名。

---

## 15. 常见误区

### 15.1 把 JWT 当成加密

JWT 默认不是加密，而是编码加签名。

也就是说，JWT 的内容可以被解码看到，但不能随便篡改。

所以不要在 JWT 中放敏感信息。

---

### 15.2 Controller 调用了登录但丢掉返回值

不推荐：

```java
userService.login(request);
return ApiResponse.success(null);
```

推荐：

```java
return ApiResponse.success(userService.login(request));
```

---

### 15.3 SQL 字段和 Java 属性混用

不推荐：

```sql
select userName from sys_user
```

推荐：

```sql
select user_name from sys_user
```

然后用 `resultMap` 映射：

```xml
<result property="userName" column="user_name" />
```

---

### 15.4 JWT 密钥太短

如果密钥太短，JWT 库可能会抛出异常。

推荐使用足够长的密钥：

```java
private static final String SECRET = "week4-jwt-secret-key-must-be-at-least-32-bytes";
```

真实项目中应把密钥放到配置文件或环境变量中。

---

## 16. 面试小题

### 16.1 登录接口的基本流程是什么？

答：Controller 接收账号密码，Service 校验参数，根据用户名查用户，比较密码，成功后生成 JWT 并返回，失败则抛出异常并由全局异常处理统一返回。

### 16.2 为什么登录失败不区分用户名不存在和密码错误？

答：为了避免暴露系统中是否存在某个用户名，降低账号被枚举的风险。

### 16.3 JWT 是什么？

答：JWT 是一种 token 格式，常用于登录认证。后端登录成功后签发 JWT，前端后续请求携带 JWT，后端通过签名和过期时间判断 token 是否有效。

### 16.4 JWT 和 MD5 有什么区别？

答：MD5 主要是哈希摘要，像内容指纹；JWT 是登录凭证，像门禁卡。JWT 可以携带用户身份信息，并通过签名校验是否被篡改。

---

## 17. 小结

JWT 登录主线：

```text
LoginRequest
  ↓
/user/login
  ↓
findByUserName
  ↓
compare password
  ↓
generate JWT
  ↓
return token
```

这条线跑通后，再通过登录拦截器校验每次请求携带的 JWT。

