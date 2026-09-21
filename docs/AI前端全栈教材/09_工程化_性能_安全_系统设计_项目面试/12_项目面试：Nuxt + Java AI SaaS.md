# 第十二章 项目面试：Nuxt + Java AI SaaS

## 一、本章具体知识点

- Nuxt SSR
- Nitro/BFF
- Spring Boot
- PostgreSQL/MySQL
- Redis
- Auth
- RAG
- AI Gateway
- deployment

## 二、各知识点详细解释

可以采用：

```text
Browser
↓
Nuxt
↓
Nitro BFF
↓
Spring Boot
├── Auth
├── Business
├── RAG
└── AI Gateway
↓
DB / Redis / Object Storage
```

Nuxt 负责页面、SSR、BFF 聚合；Java 负责稳定的领域业务和核心数据服务。

## 三、本章面试题与答案

### 题：为什么不是所有后端都用 Nuxt Nitro？

**答案：**

Nitro 很适合 BFF、页面专属接口和轻量 server route，但核心业务长期演进、复杂事务、企业级服务治理时，Spring Boot 生态更适合承担 domain backend。因此可以做 Nuxt BFF + Java domain service 的组合。

---
