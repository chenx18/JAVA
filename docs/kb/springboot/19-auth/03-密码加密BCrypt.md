# 密码加密 BCrypt

密码加密用于解决“数据库不能保存明文密码”的问题。

JWT 解决的是登录凭证，BCrypt 解决的是密码存储安全。完整登录基础链路应是：

```text
账号密码登录
  ↓
BCrypt 校验密码
  ↓
生成 JWT
  ↓
拦截器校验 JWT
```

---

## 1. 本节目录 / 学习目标

本节按以下路线学习：

```text
新增用户 / 注册用户
  ↓
明文密码
  ↓
BCrypt 加密
  ↓
数据库保存密文
  ↓
登录时提交明文密码
  ↓
BCrypt matches 校验
  ↓
校验成功后生成 JWT
```

学完本节应能掌握：

- 为什么不能保存明文密码
- BCrypt 和 MD5 的区别
- `encode` 和 `matches` 分别什么时候用
- 新增用户时如何保存加密密码
- 登录时如何校验加密密码
- 旧明文密码如何迁移

---

## 2. 为什么不能保存明文密码

不推荐：

```text
user_name = admin
password = 123456
```

如果数据库泄露，所有用户密码都会直接暴露。

推荐：

```text
user_name = admin
password = $2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

数据库只保存 BCrypt 密文。用户登录时，后端用 `matches` 判断“用户输入的明文密码”和“数据库中的密文”是否匹配。

---

## 3. BCrypt 和 MD5 的区别

| 对比 | MD5 | BCrypt |
| --- | --- | --- |
| 主要用途 | 摘要、文件指纹 | 密码加密存储 |
| 是否适合保存密码 | 不适合 | 适合 |
| 同样密码结果是否固定 | 固定 | 不固定 |
| 是否自带盐值 | 否 | 是 |
| 计算速度 | 很快 | 故意较慢 |

BCrypt 的重要特点：

```text
同一个密码，每次加密结果都不一样
但 matches 仍然可以判断密码是否正确
```

所以不能用 `equals` 比较 BCrypt 密文。

---

## 4. 引入依赖

如果项目没有引入完整 Spring Security，可以只引入密码加密模块：

```xml
<dependency>
  <groupId>org.springframework.security</groupId>
  <artifactId>spring-security-crypto</artifactId>
</dependency>
```

学习阶段推荐先只用 `spring-security-crypto`，避免过早进入完整 Spring Security 认证体系。

---

## 5. 配置 PasswordEncoder

建议放在：

```text
framework/config/PasswordConfig.java
```

完整代码：

```java
package com.example.week4.framework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
```

含义：

```text
PasswordEncoder：密码加密和校验接口
BCryptPasswordEncoder：BCrypt 实现
@Bean：把对象交给 Spring 管理，Service 中可以自动注入
```

---

## 6. 新增用户时加密密码

Service 示例：

```java
@RequiredArgsConstructor
@Service
public class UserService {

  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  public void createUser(User request) {
    if (request == null) {
      throw new IllegalArgumentException("User is null");
    }
    if (request.getPassword() == null || request.getPassword().isBlank()) {
      throw new IllegalArgumentException("Password is blank");
    }

    request.setPassword(passwordEncoder.encode(request.getPassword()));

    int rows = userMapper.insert(request);
    if (rows == 0) {
      throw new IllegalArgumentException("User add error");
    }
  }
}
```

核心代码：

```java
request.setPassword(passwordEncoder.encode(request.getPassword()));
```

意思是：

```text
前端传入明文密码
  ↓
后端转成 BCrypt 密文
  ↓
数据库只保存密文
```

---

## 7. 登录时校验密码

明文时代的写法：

```java
if (!request.getPassword().equals(user.getPassword())) {
  throw new IllegalArgumentException("Username or password error");
}
```

接入 BCrypt 后要改成：

```java
if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
  throw new IllegalArgumentException("Username or password error");
}
```

完整登录示例：

```java
@RequiredArgsConstructor
@Service
public class UserService {

  private final UserMapper userMapper;
  private final JwtUtil jwtUtil;
  private final PasswordEncoder passwordEncoder;

  public String login(LoginRequest request) {
    User user = userMapper.findByUserName(request.getUserName());
    if (user == null) {
      throw new IllegalArgumentException("Username or password error");
    }

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new IllegalArgumentException("Username or password error");
    }

    return jwtUtil.generateToken(user.getId(), user.getUserName());
  }
}
```

参数顺序：

```text
matches(用户输入的明文密码, 数据库保存的 BCrypt 密文)
```

---

## 8. 数据库字段

BCrypt 密文通常约 60 个字符，字段不能太短。

推荐：

```sql
password varchar(100) not null
```

不推荐：

```sql
password varchar(20)
```

否则密文会被截断，登录永远失败。

---

## 9. 已有明文密码如何处理

如果数据库已有：

```text
admin / 123456
```

接入 BCrypt 后需要迁移成密文。

方式一：通过新增用户接口重新创建用户。

方式二：临时生成密文后更新数据库：

```java
public class PasswordDemo {
  public static void main(String[] args) {
    PasswordEncoder encoder = new BCryptPasswordEncoder();
    System.out.println(encoder.encode("123456"));
  }
}
```

然后执行：

```sql
update sys_user
set password = '$2a$10$生成出来的密文'
where user_name = 'admin';
```

---

## 10. 实际开发注意点

- 数据库永远不要保存明文密码。
- 新增用户、注册用户、修改密码时用 `encode`。
- 登录时用 `matches`。
- 不要使用 MD5 保存密码。
- 不要自己设计密码加密算法。
- BCrypt 密文长度较长，数据库字段不能太短。
- 密码校验成功后再生成 JWT。

---

## 11. 面试小题

### 11.1 为什么不能用 MD5 保存密码？

答：MD5 速度太快，且结果固定，容易被彩虹表或暴力破解攻击。密码存储应使用 BCrypt、PBKDF2、Argon2 等专门算法。

### 11.2 BCrypt 为什么每次加密结果不同？

答：BCrypt 会自动加入随机盐值，所以同一个密码每次生成的密文不同，但可以通过 `matches` 校验。

### 11.3 `encode` 和 `matches` 分别什么时候用？

答：新增用户或修改密码时用 `encode`；登录时用 `matches` 校验明文密码和数据库密文是否匹配。

---

## 12. 小结

```text
新增用户：明文密码 -> encode -> 数据库存密文
登录用户：明文密码 + 数据库密文 -> matches -> 生成 JWT
```

完成本节后，登录基础闭环就具备了密码安全能力。

