# Token 存储风险

> 专题：浏览器、网络、安全  
> 编号：B17

### 一句话答案

Token 放 localStorage 容易受 XSS 影响，放 Cookie 需要注意 CSRF，具体方案要结合业务安全要求选择。

### 核心对比

```text
localStorage：前端易操作，但 XSS 可读
HttpOnly Cookie：JS 不可读，但需防 CSRF
内存存储：刷新丢失，体验和续期复杂
```

### 项目里怎么用

- 普通后台常见 localStorage + XSS 防护。
- 安全要求高时用 HttpOnly Cookie + SameSite + CSRF Token。
- 短 token + refresh token。

### 常见坑

- 把长期 token 放 localStorage。
- JWT payload 存敏感信息。
- 过期后没有统一刷新或退出。

### 面试表达

Token 存储没有绝对完美方案。localStorage 方便但怕 XSS，Cookie 可 HttpOnly 但要防 CSRF。实际选型要看系统风险等级和后端认证方案。
