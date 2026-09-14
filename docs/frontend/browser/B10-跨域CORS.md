# 跨域 CORS

> 专题：浏览器、网络、安全  
> 编号：B10

### 一句话答案

CORS 是浏览器允许跨域访问的一套机制，需要服务端返回允许跨域的响应头。

### 核心原理

简单请求直接发；复杂请求会先发 `OPTIONS` 预检请求。

常见响应头：

```text
Access-Control-Allow-Origin
Access-Control-Allow-Methods
Access-Control-Allow-Headers
Access-Control-Allow-Credentials
```

### 项目里怎么用

- 开发环境用代理。
- 生产环境由 Nginx 或后端配置。
- 携带 Cookie 时配置 credentials。

### 常见坑

- credentials 和 `*` origin 冲突。
- 预检请求未处理。
- 自定义 header 没加到允许列表。

### 面试表达

跨域是浏览器拦截，不是服务器真的不能处理请求。CORS 的核心在服务端响应头，带 Cookie 时前后端都要配置，而且 origin 必须明确。
