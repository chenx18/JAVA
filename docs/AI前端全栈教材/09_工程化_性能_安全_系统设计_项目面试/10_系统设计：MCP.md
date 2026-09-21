# 第十章 系统设计：MCP

## 一、本章具体知识点

- host
- client
- server
- tool discovery
- resources
- prompts
- authorization
- tasks
- transport
- audit

## 二、各知识点详细解释

```text
AI Host
 ↓
MCP Client
 ↓
MCP Server
 ├── Tools
 ├── Resources
 └── Prompts
```

安全上要特别关注：

```text
who is calling?
which tenant?
which scopes?
which tool?
which resource?
what arguments?
```

2026-07-28 规范强调无状态协议核心、多轮请求、header routing、缓存和授权强化等演进方向，因此实际系统设计时要把协议版本和实现能力明确记录，不要只凭旧教程理解 MCP。([blog.modelcontextprotocol.io](https://blog.modelcontextprotocol.io/posts/2026-07-28/))

## 三、本章面试题与答案

### 题：MCP Server 如何做权限控制？

**答案：**

首先认证 MCP client，再把身份映射到用户/租户/权限 scope；每个 tool invocation 都做授权和参数验证，访问资源时还要做资源级权限控制。高风险写操作增加人工确认和审计日志，而不是因为“请求来自 AI”就默认可信。

---
