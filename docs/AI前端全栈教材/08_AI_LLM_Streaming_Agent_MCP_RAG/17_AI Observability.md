# 第十七章 AI Observability

## 一、本章具体知识点

- logging
- tracing
- token usage
- first token latency
- tool latency
- success rate
- retry rate
- cost
- model routing

## 二、各知识点详细解释

一次 Agent 请求可能经历：

```text
User Request
→ Backend
→ Agent
→ Tool
→ Database
→ LLM
→ Tool
→ LLM
→ Response
```

如果只记录总耗时，不知道慢在哪里。

所以需要 trace/span：

```text
trace
 ├── llm span
 ├── tool span
 ├── db span
 └── http span
```

同时记录 token usage、first-token latency、总延迟、错误率和 Tool 失败率。

## 三、本章面试题与答案

### 题：AI 请求慢怎么排查？

**答案：**

先拆时间：网络、网关、模型首 token、模型持续输出、Tool、数据库、后处理。通过 trace 把一次请求拆成多个 span，再结合 token 使用量和错误重试分析是模型问题、工具问题还是后端问题。
