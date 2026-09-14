# Redis 登录态

Redis 登录态用于解决“JWT 签发后如何退出登录、主动失效、控制在线状态”的问题。

单独使用 JWT 可以校验 token 是否有效，但很难做到服务端主动让某个 token 失效。接入 Redis 后，可以让服务端保存登录状态。

---

## 1. 本节目录 / 学习目标

本节按以下路线学习：

```text
用户登录成功
  ↓
后端生成 JWT
  ↓
JWT 中写入 jti 唯一标识
  ↓
Redis 保存 jti 对应的登录状态
  ↓
请求接口时先解析 JWT
  ↓
再检查 Redis 中 jti 是否存在
  ↓
存在则放行
  ↓
不存在则返回 401
```

学完本节应能掌握：

- 只有 JWT 有什么问题
- Redis 登录态解决什么问题
- `jti` 是什么
- 登录时如何写入 Redis
- 拦截器中如何检查 Redis
- 退出登录如何让 token 主动失效

---

## 2. 只有 JWT 的问题

JWT 一旦签发，在过期前通常都能通过校验。

常见问题：

```text
用户点击退出登录后，旧 JWT 仍然可能可用
管理员禁用用户后，旧 JWT 仍然可能可用
密码修改后，旧 JWT 仍然可能可用
无法方便控制单端登录或多端登录
```

原因：

```text
JWT 是无状态的
服务端默认不保存 token 状态
```

Redis 登录态的作用：

```text
让服务端保存一份 token 是否有效的状态
```

---

## 3. Redis 登录态整体流程

```text
登录成功
  ↓
生成 jti
  ↓
生成带 jti 的 JWT
  ↓
Redis 保存 login:token:{jti}
  ↓
前端保存 JWT
  ↓
请求接口携带 JWT
  ↓
拦截器解析 JWT 得到 jti
  ↓
查询 Redis 中 jti 是否存在
  ↓
存在：放行
  ↓
不存在：返回 401
```

`jti` 可以理解为：

```text
本次登录凭证的唯一编号
```

---

## 4. 引入 Redis 依赖

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

Spring Boot 会根据配置自动创建 Redis 相关对象，例如 `StringRedisTemplate`。

---

## 5. Redis 配置

`application.properties`：

```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.password=
spring.data.redis.database=0
```

Docker 示例：

```yaml
services:
  redis:
    image: redis:7
    container_name: week4-redis
    ports:
      - "6379:6379"
```

---

## 6. 改造 JwtUtil：加入 jti

```java
public String generateToken(Integer userId, String userName, String jti) {
  Date now = new Date();
  Date expireDate = new Date(now.getTime() + EXPIRE_TIME);

  return Jwts.builder()
      .id(jti)
      .subject(String.valueOf(userId))
      .claim("userName", userName)
      .issuedAt(now)
      .expiration(expireDate)
      .signWith(getSignKey())
      .compact();
}
```

解析时：

```java
Claims claims = jwtUtil.parseToken(token);
String jti = claims.getId();
```

---

## 7. 登录时写入 Redis

```java
@RequiredArgsConstructor
@Service
public class UserService {

  private final UserMapper userMapper;
  private final JwtUtil jwtUtil;
  private final PasswordEncoder passwordEncoder;
  private final StringRedisTemplate stringRedisTemplate;

  public String login(LoginRequest request) {
    User user = userMapper.findByUserName(request.getUserName());
    if (user == null) {
      throw new IllegalArgumentException("Username or password error");
    }

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new IllegalArgumentException("Username or password error");
    }

    String jti = UUID.randomUUID().toString();
    String token = jwtUtil.generateToken(user.getId(), user.getUserName(), jti);

    String redisKey = "login:token:" + jti;
    stringRedisTemplate.opsForValue().set(redisKey, String.valueOf(user.getId()), 2, TimeUnit.HOURS);

    return token;
  }
}
```

核心逻辑：

```text
JWT 里保存 jti
Redis 里保存 login:token:{jti}
两边过期时间保持一致
```

---

## 8. 拦截器中检查 Redis 登录态

```java
Claims claims = jwtUtil.parseToken(token);
String jti = claims.getId();

String redisKey = "login:token:" + jti;
Boolean exists = stringRedisTemplate.hasKey(redisKey);

if (Boolean.FALSE.equals(exists)) {
  writeUnauthorized(response, "login expired");
  return false;
}
```

校验分两层：

```text
第一层：JWT 签名和过期时间是否正确
第二层：Redis 中登录态是否仍然存在
```

---

## 9. 退出登录

退出登录的本质是删除 Redis 中的登录态。

Controller：

```java
@PostMapping("/logout")
public ApiResponse<Void> logout() {
  userService.logout();
  return ApiResponse.success(null);
}
```

Service：

```java
public void logout() {
  String jti = LoginUserContext.getJti();
  if (jti == null || jti.isBlank()) {
    return;
  }

  stringRedisTemplate.delete("login:token:" + jti);
}
```

如果需要 logout，需要在 `LoginUser` 中保存 `jti`：

```java
private String jti;
```

---

## 10. 单端登录和多端登录

多端登录：

```text
login:token:jti1
login:token:jti2
login:token:jti3
```

多个 token 同时有效。

单端登录：

```text
login:user:{userId} -> jti
```

用户新登录时删除旧 `jti`，保存新 `jti`，旧 token 就会失效。

---

## 11. Redis key 设计

常见 key：

```text
login:token:{jti} -> userId
login:user:{userId} -> jti
```

建议：

```text
key 要有业务前缀
key 要设置过期时间
key 不要保存敏感信息
```

---

## 12. 实际开发注意点

- JWT 和 Redis 的过期时间要尽量一致。
- 退出登录只删除 Redis 登录态，不需要修改 JWT 本身。
- Redis 中不要保存密码等敏感信息。
- 单端登录和多端登录是业务选择。
- 如果用户被禁用，可以删除该用户所有登录态，让 token 主动失效。

---

## 13. 面试小题

### 13.1 为什么 JWT 还需要 Redis？

答：JWT 本身是无状态的，服务端默认不保存 token 状态。接入 Redis 后，可以支持退出登录、主动失效、多端登录控制等能力。

### 13.2 jti 是什么？

答：`jti` 是 JWT 的唯一标识，可以用来标记某一次登录生成的 token。

### 13.3 退出登录如何实现？

答：解析当前 token 得到 jti，然后删除 Redis 中对应的 `login:token:{jti}`。后续同一个 token 再请求时，虽然 JWT 可能没过期，但 Redis 登录态不存在，会被拦截。

---

## 14. 小结

```text
登录成功生成 JWT
  ↓
JWT 中写入 jti
  ↓
Redis 保存 login:token:{jti}
  ↓
请求时解析 JWT
  ↓
检查 Redis 中 jti 是否存在
  ↓
存在放行，不存在拦截
```

完成本节后，登录体系就从“能校验 token”升级为“服务端能控制 token 是否仍然有效”。

