# 第八章 系统设计：AI Agent

## 一、本章具体知识点

- Agent runtime
- tool registry
- task state
- queue
- worker
- memory
- authorization
- retry
- idempotency
- checkpoint
- human approval

## 二、各知识点详细解释

Agent 不应只放在一个同步 HTTP 请求里。

```text
Request
→ Create Task
→ Agent Runtime
→ Tool
→ Worker
→ Checkpoint
→ Result
```

任务状态持久化：

```text
pending
running
waiting
failed
cancelled
completed
```

每个工具都要经过授权，并对可能重复执行的写操作设计幂等。

## 三、本章面试题与答案

### 题：Agent 为什么需要持久化 checkpoint？

**答案：**

Agent 可能执行很长时间，并且包含多个外部工具。一旦进程崩溃，如果所有状态只在内存中就无法恢复。checkpoint 可以保存当前任务、已完成步骤、工具结果和下一步状态，让系统从已确认的位置继续。

---
