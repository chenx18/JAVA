# 第十章 HTTP 缓存

## 一、本章具体知识点

- Cache-Control
- max-age
- no-cache
- no-store
- ETag
- If-None-Match
- Last-Modified
- If-Modified-Since
- 强缓存
- 协商缓存
- CDN

## 二、各知识点详细解释

缓存可以减少网络和服务器成本，同时提高响应速度。

### 强缓存

浏览器根据缓存策略直接使用本地响应，不发起验证请求。

### 协商缓存

浏览器带条件请求到服务器：

```text
ETag → If-None-Match
Last-Modified → If-Modified-Since
```

如果资源没变化，服务器可以返回 304。

### no-cache

不是“不缓存”，而是使用前需要重新验证。

### no-store

要求不要存储响应。

## 三、本章面试题与答案

### 题：no-cache 和 no-store 有什么区别？

**答案：**

no-cache 允许缓存，但使用前需要向服务器验证；no-store 则要求不要存储该响应。两者不能简单都理解成“不缓存”。

---
