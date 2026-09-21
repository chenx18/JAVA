# 第十二章 MCP

## 一、本章具体知识点

- Model Context Protocol
- Host
- Client
- Server
- Tool
- Resource
- Prompt
- discovery
- authorization
- Tasks
- MCP Apps

## 二、各知识点详细解释

MCP 可以理解为 AI 应用与外部能力之间的标准协议层。

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

2026-07-28 正式规范将协议核心进一步推进为更偏无状态的 request/response 模型，并增加多轮请求、header routing、cacheable list results、授权强化和扩展框架。([blog.modelcontextprotocol.io](https://blog.modelcontextprotocol.io/posts/2026-07-28/))

### Tool

提供可执行能力。

### Resource

提供可读取上下文。

### Prompt

提供可复用提示模板。

### Tasks

适合长时间运行的任务和任务状态管理。

## 三、本章面试题与答案

### 题：MCP 是不是 API？

**答案：**

MCP 是协议，不是某个业务 API。它定义了 AI Host/Client 与 MCP Server 之间如何发现和使用 Tools、Resources、Prompts 等能力。业务 API 可以被 MCP Server 封装后提供给 AI 使用。

### 题：MCP 与 Function Calling 的关系？

**答案：**

Function Calling 是模型与应用之间表达“我要调用某个工具”的机制；MCP 是更完整的外部能力协议，可以标准化工具发现、资源、提示、任务等。MCP Tool 最终仍可能映射到具体的函数或 HTTP/API 执行。

---
