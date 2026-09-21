# 第七章 同源策略、CORS、CSRF、XSS

## 一、本章具体知识点

- Origin
- Same-Origin Policy
- CORS
- preflight
- Access-Control-Allow-Origin
- credentials
- CSRF
- SameSite
- XSS
- CSP
- Trusted Types

## 二、各知识点详细解释

Origin 通常由 scheme、host、port 构成。同源策略限制一个 origin 的脚本访问另一个 origin 的受保护资源。

CORS 是服务器通过 HTTP 响应头授予其他 origin 访问权限的一套机制，不是“浏览器关闭跨域”。预检通常使用 OPTIONS，询问实际请求是否被允许。

CSRF 利用的是浏览器会自动带上某些认证凭据的特点；XSS 则是攻击代码进入页面并在受害者上下文中执行。

防护：

```text
XSS → 输出编码 / Sanitization / CSP / Trusted Types
CSRF → SameSite / CSRF Token / Origin 校验
```

## 三、本章面试题与答案

### 题：CORS 是怎么工作的？

**答案：**

浏览器检测到跨源请求后，根据请求类型判断是否需要预检；服务器通过 Access-Control-Allow-* 等响应头声明允许的 origin、method、headers 和 credentials。浏览器根据这些响应头决定是否把响应暴露给前端脚本。CORS 主要是浏览器侧的跨源访问控制机制。

### 题：XSS 和 CSRF 的区别？

**答案：**

XSS 的核心是攻击脚本在受害页面上下文中执行；CSRF 的核心是攻击者诱导浏览器向目标站点发起用户凭据自动附带的请求。两者的攻击路径和防护重点不同。

---
