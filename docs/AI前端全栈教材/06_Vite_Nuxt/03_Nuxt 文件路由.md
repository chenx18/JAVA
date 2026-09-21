# 第三章 Nuxt 文件路由

## 一、本章具体知识点

- pages
- route mapping
- dynamic route
- catch-all
- nested route
- route params
- query
- layouts
- middleware

## 二、各知识点详细解释

Nuxt 根据 `app/pages` 的文件结构生成路由。例如：

```text
app/pages/index.vue        → /
app/pages/users.vue        → /users
app/pages/users/[id].vue   → /users/:id
```

动态路由使用方括号表达参数；嵌套路由可以通过目录结构表达。

Nuxt 同时可以通过 layouts 与 middleware 把页面布局和导航控制抽离。

## 三、本章面试题与答案

### 题：Nuxt 文件路由有什么价值？

**答案：**

它把 URL 与页面文件结构建立 convention，减少手写 route record 的重复代码，同时让页面边界更加直观；构建时还能据此做代码拆分和路由级加载。

---
