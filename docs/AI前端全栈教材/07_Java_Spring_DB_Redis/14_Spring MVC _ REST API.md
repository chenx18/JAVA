# 第十四章 Spring MVC / REST API

## 一、本章具体知识点

- DispatcherServlet
- Controller
- RequestMapping
- JSON serialization
- DTO
- validation
- exception handling
- interceptor
- filter

## 二、各知识点详细解释

典型请求链：

```text
HTTP request
→ Filter
→ DispatcherServlet
→ Handler Mapping
→ Controller
→ Service
→ Repository
→ response
```

DTO 用于 API 边界建模，避免把数据库实体直接当外部契约。

## 三、本章面试题与答案

### 题：为什么不要直接把 Entity 返回给前端？

**答案：**

Entity 是持久化模型，内部结构、字段敏感性和业务关系未必适合外部 API；直接返回容易泄露字段并耦合数据库结构。使用 DTO 可以控制 API 契约、版本和序列化结构。

---
