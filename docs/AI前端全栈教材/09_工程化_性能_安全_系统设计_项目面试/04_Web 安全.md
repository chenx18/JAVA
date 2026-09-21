# 第四章 Web 安全

## 一、本章具体知识点

- XSS
- CSRF
- CORS
- CSP
- Trusted Types
- SSRF
- SQL Injection
- Clickjacking
- file upload security
- secret management

## 二、各知识点详细解释

### XSS

攻击者让脚本进入页面执行。核心防守：正确上下文输出编码、避免危险 HTML、sanitization、CSP、Trusted Types。

### CSRF

利用浏览器自动携带身份凭据发请求。常见防护：SameSite Cookie、CSRF token、Origin/Referer 校验等。

### CORS

控制浏览器脚本能否读取跨源响应。它不等于后端的身份认证。

### SSRF

服务器端请求被攻击者间接控制，可能访问内部网络。防护需要 URL allowlist、DNS/IP 校验、网络隔离等。

### 文件上传

需要限制扩展名、MIME、文件大小、内容签名、存储路径以及恶意文件扫描，上传后最好使用不可执行对象存储。

## 三、本章面试题与答案

### 题：XSS 和 CSRF 怎么区分？

**答案：**

XSS 是攻击脚本在受害页面上下文中执行；CSRF 是利用用户浏览器自动携带身份凭据发起非预期请求。XSS 主要关注输入进入 HTML/JS 执行上下文，CSRF 主要关注请求真实性与认证凭据。

### 题：CORS 能防止后端接口被攻击吗？

**答案：**

不能。CORS 主要限制浏览器脚本读取跨源响应，攻击者仍可能通过自己的服务器、curl 或其他客户端直接调用 API，因此 API 必须自己做认证和授权。

---
