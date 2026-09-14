# CSRF

> 专题：浏览器、网络、安全  
> 编号：B16

### 一句话答案

CSRF 是攻击网站利用用户在目标站点的登录态，诱导浏览器发送伪造请求。

### 核心原理

发生条件：

```text
用户已登录目标站
认证信息自动携带
攻击站能触发请求
服务端只依赖 Cookie 判断身份
```

### 项目里怎么用

防护方式：

```text
SameSite Cookie
CSRF Token
校验 Origin / Referer
关键操作二次确认
使用 Authorization Header
```

### 常见坑

- Cookie 登录但 SameSite 未设置。
- 只靠前端隐藏按钮防护。
- GET 接口执行删除等副作用操作。

### 面试表达

CSRF 和 XSS 不一样，CSRF 不一定能读取响应，它是利用浏览器自动带 Cookie 的能力伪造请求。防护重点是让请求带上攻击站拿不到的凭证。
