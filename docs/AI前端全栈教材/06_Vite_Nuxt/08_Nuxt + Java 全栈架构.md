# 第八章 Nuxt + Java 全栈架构

## 一、本章具体知识点

- BFF
- frontend API
- Java API
- authentication
- SSR
- proxy
- database
- AI Gateway

## 二、各知识点详细解释

适合你的典型结构：

```text
Browser
  ↓
Nuxt
  ↓
Nitro BFF
  ↓
Spring Boot
  ↓
MySQL / Redis
```

AI：

```text
Spring Boot
  ↓
AI Gateway
  ↓
LLM Provider
```

这样前端不需要直接暴露 provider secret，也可以由 Java 统一控制业务权限。

## 三、本章面试题与答案

### 题：为什么前端不直接调用 Java 所有接口？

**答案：**

在需要 SSR、聚合多个后端服务、隐藏内部服务地址、统一鉴权和页面专属数据拼装时，BFF 可以降低前端和领域服务的耦合。但不是所有接口都必须经过 BFF，简单查询也可以直接走安全设计过的后端 API。
