# 第七章 系统设计：AI Chat

## 一、本章具体知识点

- gateway
- auth
- conversation
- message
- streaming
- persistence
- cache
- rate limit
- observability
- provider fallback

## 二、各知识点详细解释

推荐架构：

```text
Browser / Vue
       ↓
CDN / Edge
       ↓
API Gateway
       ↓
AI Gateway
  ┌────┼────┐
  ↓    ↓    ↓
Model A B    C
       ↓
PostgreSQL
       ↓
Redis
```

Streaming 通常由 AI Gateway 维持向前端的数据流；消息最终持久化到数据库，Redis 可承担热点会话、rate limit 或短期状态。

## 三、本章面试题与答案

### 题：设计一个百万用户 AI Chat，你会怎么做？

**答案：**

先把请求路径拆成 CDN/API Gateway/AI Gateway/Provider 和数据层。认证和权限在 Gateway/后端；会话、消息持久化到 PostgreSQL；Redis 做热点数据、限流和部分短状态；长任务和非实时任务通过队列异步处理；Streaming 通过 SSE/streaming response 返回。再补 observability、成本控制、provider fallback 和租户隔离。

---
