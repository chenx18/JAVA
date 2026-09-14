# Token / JWT

> 专题：浏览器、网络、安全  
> 编号：B11

### 一句话答案

Token 是登录凭证的一种形式，JWT 是一种自包含的 Token 格式，由 header、payload、signature 三部分组成。

### 核心原理

JWT：

```text
Header：算法和类型
Payload：声明信息
Signature：签名防篡改
```

### 项目里怎么用

- 登录成功后前端保存 token。
- 请求拦截器加 Authorization。
- 后端校验 token 并识别用户。

### 常见坑

- JWT payload 只是编码，不是加密，不能放敏感信息。
- token 过期和刷新策略没有设计。
- localStorage 存 token 有 XSS 风险。

### 面试表达

我会把 JWT 理解成可被校验的登录凭证。它能防篡改，但 payload 不保密。前端主要负责保存、携带、过期处理和 401 统一跳转。
