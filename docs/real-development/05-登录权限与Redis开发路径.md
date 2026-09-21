# 登录、权限与 Redis 开发路径

## 本节目录与路线

1. 用已有用户表完成真实密码登录。
2. 校验 JWT，提供当前用户信息。
3. 开发角色、菜单、关联关系和后端权限判断。
4. 用 Redis 完成退出登录和会话失效。

前置：[04-用户模块完整开发](04-用户模块完整开发.md)。BCrypt 入库已经完成，本节在现有项目上增加功能，不重建一套 User CRUD。

本篇是新项目与 [Auth 专题](../kb/springboot/19-auth/README.md) 之间的执行指南。专题提供各组件实现和原理；这里确定接入顺序、适配点和完成标准。标为“在现有类中添加”的代码不能拿来覆盖整个类。

## 一、先看最终效果

```text
POST /system/users/login
  → 返回 token
GET /system/users/info       Authorization: Bearer <token>
  → 用户资料 + roles + permissions
GET /system/users/menus      Authorization: Bearer <token>
  → 当前用户可访问的菜单树
GET /system/users/list       Authorization: Bearer <token>
  → 有 system:user:list 权限才返回列表
POST /system/users/logout    Authorization: Bearer <token>
  → 当前会话失效；旧 token 再请求返回 401
```

本项目把角色和权限标识合并进 `/info`，不再为了同一批数据额外请求 `/perms`。菜单树单独返回，前端用于生成路由和侧边栏。

## 二、先适配已有专题

| 专题中的示例 | 新项目采用 |
| --- | --- |
| `com.example.week4` | `com.example.admin`，包括 import 和 XML namespace/type |
| `Integer userId` / int 主键 | `Long userId` / bigint；关联表外键类型也一致 |
| `/user/login` 等旧路径 | `/system/users/login`，拦截器放行路径同步更新 |
| 示例硬编码 JWT SECRET | 环境变量 `JWT_SECRET_BASE64`，禁止沿用公开示例密钥 |
| 明文密码比较 | 已有 `PasswordEncoder.matches` |
| 任意 BusinessException 构造方式 | 本项目 `new BusinessException(HttpStatus, message)` |
| `.properties` 片段 | 合并到本项目 YAML，避免重复根节点 |
| 返回完整 User | 返回 UserResponse/UserInfoResponse，不能返回 password |

## 三、登录生成 JWT

阅读：[JWT 生成](../kb/springboot/19-auth/01-登录流程-JWT生成.md)、[BCrypt 校验](../kb/springboot/19-auth/03-密码加密BCrypt.md)。

实施顺序：

1. 向 POM 增加专题中的三个 JJWT 依赖，版本统一为 `0.13.0`；impl、jackson 使用 runtime scope。
2. 创建 `domain/request/LoginRequest`，包含 `userName`、`password`，均使用 `@NotBlank`；不要新增前端可控制的 role/id 字段。
3. 创建 `framework/security/JwtUtil`，使用下面配置化的实现。
4. 在 UserService 添加登录方法：查用户 → matches → 检查启用状态 → 签发 token。
5. 在 UserController 添加 `@PostMapping("/login")`，返回 `ApiResponse<LoginResponse>`；LoginResponse 至少包含 token 和过期秒数。

### 密钥与 JwtUtil

生成一份本地随机密钥，命令在项目目录执行一次；输出追加到忽略的 `.env`，不要加入 `.env.example` 的实际值：

```powershell
$jwtKeyBytes = New-Object byte[] 32
$jwtRng = [Security.Cryptography.RandomNumberGenerator]::Create()
$jwtRng.GetBytes($jwtKeyBytes)
$jwtRng.Dispose()
Add-Content -LiteralPath '.env' -Value ('JWT_SECRET_BASE64=' + [Convert]::ToBase64String($jwtKeyBytes))
```

如果 `.env` 已有这个变量，继续使用原值，不重复追加。换密钥会使旧 token 验签失败。

在公共 `application.yml` 顶层添加：

```yaml
app:
  jwt:
    secret-base64: ${JWT_SECRET_BASE64}
    expire-seconds: 7200
```

文件：`src/main/java/com/example/admin/framework/security/JwtUtil.java`

```java
package com.example.admin.framework.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private final SecretKey key;
    private final long expireSeconds;

    public JwtUtil(@Value("${app.jwt.secret-base64}") String secret,
                   @Value("${app.jwt.expire-seconds}") long expireSeconds) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expireSeconds = expireSeconds;
    }

    public String generateToken(Long userId, String userName, String jti) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId.toString())
                .claim("userName", userName)
                .id(jti)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expireSeconds)))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
        if (claims.getExpiration() == null || claims.getSubject() == null
                || claims.getId() == null || claims.getId().isBlank()) {
            throw new IllegalArgumentException("Missing required token claims");
        }
        return claims;
    }
}
```

每次登录用 `UUID.randomUUID().toString()` 创建独立 jti，即使还没接 Redis 也先保留这个会话标识。

登录校验的关键部分如下，在现有 Service 中结合 LoginResponse 添加，不替换已有 CRUD：

```java
User user = mapper.findByUserName(request.getUserName());
if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
    throw new BusinessException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
}
if (!Integer.valueOf(1).equals(user.getStatus())) {
    throw new BusinessException(HttpStatus.FORBIDDEN, "账号已停用");
}
```

验收：正确密码有 token；错误密码或不存在的账号得到同样的 401 提示；停用账号不能登录；密码和哈希不进入 token。生产还需增加失败次数限制、审计和相应防爆破策略。

## 四、拦截器与当前用户

阅读：[JWT 校验](../kb/springboot/19-auth/02-登录拦截器-JWT校验.md)、[当前用户](../kb/springboot/19-auth/04-获取当前登录用户.md)。

按顺序创建 `LoginUser`、`LoginUserContext`、`LoginInterceptor`、`WebConfig`，然后实现 `/info`：

1. 拦截需要认证的业务路径，明确放行 `/system/users/login`、`/health` 和开发环境 Swagger。不能为了测试放行整个 `/system/users/**`。
2. 从 Authorization 读取 Bearer token，验签并验证过期时间。
3. `Long.valueOf(claims.getSubject())` 取 userId；格式错误、缺失、非正数都返回 401。
4. 查询当前用户是否仍存在且启用，通过后才写入上下文；尚无 Redis 时这样可以让停用及时生效。
5. `afterCompletion` 中 `remove()`；若设置上下文后 preHandle 自身失败，也必须清理。不要在校验未完成时提前写入。
6. `/info` 从已认证的上下文拿 ID 查询 UserResponse，不接收前端自行提交的 userId 来代表“当前用户”。

JWT 异常转换为本项目 `BusinessException(HttpStatus.UNAUTHORIZED, "登录已失效")`，保持真实 HTTP 401。自己写 response 时也要设置状态和 JSON Content-Type。

ThreadLocal 只服务于当前同步请求线程，不是跨请求缓存。异步线程不会自然继承身份；这套教学实现先覆盖同步 Spring MVC。更复杂的认证与授权应评估 Spring Security 的过滤器链、SecurityContext 和方法安全，不能认为一个拦截器就覆盖所有服务入口。

验收：无 token、伪造 token、过期 token 都是 401；合法 token 能访问 `/info`；连续使用两个用户 token 不串号。Swagger 的 HTTP Bearer 输入框通常只填 token，Apifox Header 则填完整 `Bearer <token>`。

### 同步调整 Web 测试

新增 WebConfig 后，第 02 篇的 `@WebMvcTest` 可能加载这个配置，但不会自动创建拦截器的全部业务依赖。在健康接口测试中提供替身：添加 `org.springframework.test.context.bean.override.mockito.MockitoBean` 和本项目 `LoginInterceptor` 的 import，在测试类内声明：

```java
@MockitoBean
private LoginInterceptor loginInterceptor;
```

`/health` 应在 WebConfig 中明确放行，因此健康测试仍能返回 200。这个替身仅服务于健康接口切片测试；JWT 验签、过期和拒绝访问应另写使用真实拦截器的测试，不要靠全局关闭认证让所有测试通过。

## 五、角色、菜单和后端授权

阅读：[角色权限](../kb/springboot/19-auth/05-角色权限基础.md)、[菜单权限](../kb/springboot/19-auth/06-菜单权限基础.md)。

### 开发顺序与数据库变更

将新增表与迁移保存在 `sql/002-rbac.sql`，对已有库显式执行一次，不修改 001 后期待容器自动升级。

```text
sys_role（role_key 唯一、status）
sys_menu（parent_id、menu_type、path、component、perms、排序、status）
  ↓
sys_user_role（user_id + role_id 联合主键）
sys_role_menu（role_id + menu_id 联合主键）
  ↓
角色 CRUD → 菜单 CRUD → 分配角色 → 分配菜单 → 查询有效权限
```

所有 ID 与关联列使用 bigint。组合唯一约束防止重复授权；关联操作检查用户、角色、菜单是否存在。树结构不允许把自己或后代设为父节点。

沿用第 04 篇的开发流程完成基础 CRUD。分配角色可以约定 `PUT /system/users/{id}/roles`，请求体是角色 ID 数组；分配菜单是 `PUT /system/roles/{id}/menus`。Service 在事务中校验所有目标、删除旧关联、写入新关联，空数组明确表示清空。

### 真正执行权限判断

约定用户模块权限：

| 操作 | 所需权限 |
| --- | --- |
| 分页 | `system:user:list` |
| 详情 | `system:user:query` |
| 新增 | `system:user:add` |
| 修改 | `system:user:edit` |
| 删除 | `system:user:remove` |
| 分配角色 | `system:user:assignRole` |

新增 PermissionService，通过当前 userId JOIN 用户角色、启用角色、角色菜单和启用菜单，得到去重后的 perms。在敏感业务执行之前调用 `requirePermission(...)`，不具备权限抛 HTTP 403。初期可以在 Controller 方法入口显式调用，理解流程后再统一成注解或方法安全机制；**仅定义一个权限注解而没有执行它的逻辑，不会自动生效**。

初始管理员应通过受控的本地数据库种子/初始化流程关联角色，不写 `userId == 1` 判断。不得开放“给自己分配管理员”的匿名接口。

修改角色关联前还要检查操作者可授予的范围，避免普通管理员把自己升级；普通用户的 `/info` 与管理接口权限分开。访问具体业务记录时，必要时再检查数据所属范围，不能用“已登录”代替记录级授权。

### 验收

- [ ] 管理员可以写，只读账号只能查；匿名请求是 401，只读账号写入是 403。
- [ ] 前端隐藏按钮后，直接用 Apifox 调接口也不能越权。
- [ ] `/info` 返回真实 roles/permissions，不是固定数组。
- [ ] `/menus` 只返回可访问页面，按钮类型作为权限标识处理。
- [ ] 删除角色/用户有明确的关联清理或拒绝策略，不产生孤儿关联。
- [ ] 最后一个管理员不能被停用或解除所有管理角色，并考虑并发操作下的保护。

## 六、Redis 登录态

阅读：[Redis 登录态](../kb/springboot/19-auth/07-Redis登录态.md)。将专题中的端口和 key 前缀按本项目调整。

在本地 Compose 的 `services` 下添加：

```yaml
  redis:
    image: redis:7.4
    ports:
      - "127.0.0.1:6380:6379"
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 5s
      timeout: 3s
      retries: 10
```

这是只绑定本机的开发 Redis，没有配置密码和持久卷。重启 Redis 后现有会话全部失效，本教程接受该策略；生产使用受保护的网络和认证，见下一篇。

执行 `docker compose up -d redis`，随后 `docker compose exec redis redis-cli ping` 应返回 PONG。增加 `spring-boot-starter-data-redis`，在已有 dev 配置的 `spring` 节点内合并：

```yaml
  data:
    redis:
      host: localhost
      port: 6380
```

业务链路改为：

```text
登录：密码校验 → 生成 jti/JWT → Redis 写 login:token:<jti> = userId → 返回 token
请求：JWT 验签与过期校验 → 查询 Redis → 比较 userId → 检查用户状态 → 设置上下文
退出：删除当前 jti 的 session → 同一个 JWT 再请求返回 401
```

Redis TTL 使用 JWT 剩余有效时间，不能单独延长 Redis 就声称 JWT 已续期。Redis 写失败时不返回成功登录；Redis 不可用时不能悄悄跳过校验放行，可返回服务不可用并记录故障。

停用用户、改密、管理员强制退出需要失效该用户的所有 session，可维护“用户 ID → jti 集合”并妥善清理过期成员。缓存了角色权限时，权限变更还要主动失效缓存或使用版本校验；只退出一个会话不会自动解决所有权限更新。

验收：退出后旧 token 是 401；两个并行登录会话符合已约定的多端策略；Redis 重启后需重新登录；普通请求不能通过伪造 jti 绕过检查。

## 七、知识沉淀与面试小题

1. **JWT 与 Redis 是否重复？** JWT 提供签名与过期校验，Redis 提供服务端主动撤销能力；接入 Redis 后便依赖服务端会话状态。
2. **401 和 403 如何区分？** 没有有效身份是 401，身份有效但没有所需权限是 403。
3. **登录通过就能访问所有接口吗？** 不能；认证、接口权限和数据范围是不同层面的检查。
4. **数据库事务可以回滚 Redis 吗？** 不能；跨存储的一致性需要失败处理、重试/补偿或事件机制，不能仅靠 `@Transactional`。

下一篇：[06-前后端联调与部署交付](06-前后端联调与部署交付.md)。
