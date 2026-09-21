# 第五章 Nuxt useFetch / useAsyncData / $fetch

## 一、本章具体知识点

- $fetch
- useFetch
- useAsyncData
- SSR data transfer
- dedupe
- lazy
- server option
- hydration
- error / status

## 二、各知识点详细解释

`$fetch` 是直接发请求的工具；`useFetch` 和 `useAsyncData` 是与 Nuxt SSR 数据生命周期结合的 composables。

Nuxt 官方文档特别说明：如果在 setup 中直接使用 `$fetch`，相同请求可能在服务器执行一次、客户端 hydration 时再执行一次；`useFetch` 可以把服务端获取的数据正确传递到客户端，避免重复初次请求。([nuxt.com](https://nuxt.com/docs/4.x/getting-started/data-fetching))

### useFetch

适合常见的响应式 fetch：

```ts
const { data, status, error } = await useFetch('/api/users')
```

### useAsyncData

需要更细控制时使用，例如自定义异步函数、key、缓存策略。

## 三、本章面试题与答案

### 题：useFetch 和 $fetch 的区别？

**答案：**

$fetch 是通用请求函数；useFetch 是与 Nuxt SSR/响应式数据管理结合的封装，会处理服务端获取结果向客户端 payload 的传递和相关状态。因此页面 setup 中的数据获取通常优先考虑 useFetch/useAsyncData，而单纯客户端事件请求可以直接使用 $fetch。

---
