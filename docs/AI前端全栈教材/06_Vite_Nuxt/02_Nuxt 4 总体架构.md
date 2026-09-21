# 第二章 Nuxt 4 总体架构

## 一、本章具体知识点

- Nuxt 是什么
- app/pages
- layouts
- components
- composables
- plugins
- middleware
- server
- shared
- Nitro
- Vite
- SSR

## 二、各知识点详细解释

Nuxt 是基于 Vue 的全栈框架，目标是提供文件路由、SSR、数据获取、代码拆分、自动导入以及服务器能力。当前 Nuxt 4 文档线明确把它定位为生产级 full-stack Vue framework。([nuxt.com](https://nuxt.com/docs/4.x/getting-started/introduction))

典型结构：

```text
app/
  pages/
  components/
  layouts/
  composables/
  middleware/
server/
shared/
```

Nuxt 负责应用层约定；Nitro 负责 server runtime 与部署抽象。

## 三、本章面试题与答案

### 题：Nuxt 和 Vue 的关系？

**答案：**

Vue 是 UI framework，负责组件、响应式和渲染；Nuxt 在 Vue 之上提供应用级能力，例如文件路由、SSR、数据获取、自动导入、服务器 API、部署抽象等。Nuxt 不是替代 Vue 的另一套 UI 框架。

---
