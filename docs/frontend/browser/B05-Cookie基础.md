# Cookie 基础

> 专题：浏览器、网络、安全  
> 编号：B05

### 一句话答案

Cookie 是浏览器随请求自动携带的小段数据，常用于会话识别，但需要注意安全属性。

### 核心属性

```text
Domain
Path
Expires / Max-Age
HttpOnly
Secure
SameSite
```

### 项目里怎么用

- 后端设置登录态 Cookie。
- `HttpOnly` 防止 JS 读取。
- `Secure` 限制 HTTPS。
- `SameSite` 降低 CSRF 风险。

### 常见坑

- Cookie 自动随请求发送，容易受到 CSRF 风险影响。
- 跨域携带 Cookie 需要前后端 credentials 配置。
- Cookie 大小有限，不适合存大量信息。

### 面试表达

Cookie 适合做会话，但要配合 HttpOnly、Secure、SameSite。前端如果需要跨域带 Cookie，必须配置 credentials，后端也不能用通配符 origin。
