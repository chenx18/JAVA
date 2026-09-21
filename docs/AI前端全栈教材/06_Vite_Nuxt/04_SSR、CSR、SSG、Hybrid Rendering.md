# 第四章 SSR、CSR、SSG、Hybrid Rendering

## 一、本章具体知识点

- CSR
- SSR / Universal Rendering
- SSG / prerender
- ISR / revalidation 思路
- hybrid rendering
- route rules
- hydration
- streaming SSR

## 二、各知识点详细解释

### CSR

主要由浏览器下载 JS 后渲染页面。

### SSR

请求到达服务器后，服务器执行 Vue 代码生成 HTML，再交给浏览器 hydration。

### SSG

构建阶段生成静态 HTML，运行时不需要每次请求重新渲染。

### Hybrid

不同路由可以采用不同策略。

Nuxt 默认以 universal rendering 为核心能力，也可以切换为客户端渲染、预渲染或混合策略。([nuxt.com](https://nuxt.com/docs/4.x/guide/concepts/rendering))

### Hydration

服务器已经生成 HTML，浏览器需要加载 JS，把事件和组件运行能力“接回”到现有 DOM，而不是简单重复绘制。

## 三、本章面试题与答案

### 题：SSR 和 CSR 的区别？

**答案：**

CSR 主要由浏览器执行 JavaScript 生成 UI；SSR 在服务器先生成 HTML，再由客户端 hydration 变成可交互应用。SSR 可以改善首屏内容可见性与 SEO，但同时引入服务器渲染成本和 hydration 约束。

### 题：SSR 为什么还需要 hydration？

**答案：**

服务器生成的 HTML 主要解决首屏内容，而按钮、事件、客户端状态等交互能力仍需要 JavaScript 接管。因此客户端需要根据服务端渲染结果建立组件运行时连接，这个过程就是 hydration。

---
