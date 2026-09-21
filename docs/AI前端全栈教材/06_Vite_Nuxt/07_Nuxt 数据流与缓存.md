# 第七章 Nuxt 数据流与缓存

## 一、本章具体知识点

- payload
- SSR data
- client hydration
- server cache
- browser cache
- stale data
- invalidation
- route rules
- CDN

## 二、各知识点详细解释

缓存不能只看“有没有 cache”。要区分：

```text
Browser Cache
CDN Cache
Server Cache
Application Cache
Database Cache
```

Nuxt 的 SSR data 还涉及服务端结果如何序列化到页面 payload，再被客户端 hydration 使用。

## 三、本章面试题与答案

### 题：为什么 Nuxt SSR 页面可能出现双请求？

**答案：**

如果 setup 中直接使用 `$fetch` 做初次数据获取，服务器渲染时会执行一次，浏览器 hydration 时又可能执行一次。useFetch/useAsyncData 会配合 Nuxt payload 机制把服务器结果传递给客户端，从而减少这类重复初次请求。([nuxt.com](https://nuxt.com/docs/4.x/getting-started/data-fetching))

---
