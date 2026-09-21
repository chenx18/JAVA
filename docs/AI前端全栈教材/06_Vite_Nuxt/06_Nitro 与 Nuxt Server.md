# 第六章 Nitro 与 Nuxt Server

## 一、本章具体知识点

- Nitro
- h3
- server/api
- server/routes
- server/middleware
- server plugins
- runtime config
- deployment preset
- serverless
- edge

## 二、各知识点详细解释

Nuxt 的服务器引擎是 Nitro。它负责把 server routes、API 和 server middleware 构建成可部署的 server output。Nuxt 官方介绍中明确指出，Nitro 使 Nuxt 具备 full-stack 能力，并可部署到 Node、Serverless、Edge 等环境。([nuxt.com](https://nuxt.com/docs/4.x/getting-started/server))

例如：

```text
server/api/users.get.ts
→ GET /api/users
```

这非常适合做轻量后端或 BFF。

## 三、本章面试题与答案

### 题：Nitro 是什么？

**答案：**

Nitro 是 Nuxt 的 server engine，负责服务器路由、API、middleware 和部署适配。Nuxt 的前端应用可以与 Nitro server 放在一个代码仓库中，形成 full-stack 应用。

### 题：什么时候用 Nuxt Server API，什么时候用 Java？

**答案：**

轻量 BFF、页面专属接口、聚合 API、边缘函数等可以使用 Nitro；复杂业务领域、长期维护的核心后台、事务密集型系统和企业级 Java 生态可以由 Spring Boot 承担。二者还可以组合成 BFF + Java domain service。

---
