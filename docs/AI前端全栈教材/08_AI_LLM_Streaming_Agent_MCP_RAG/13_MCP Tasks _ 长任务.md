# 第十三章 MCP Tasks / 长任务

## 一、本章具体知识点

- task
- status
- polling
- progress
- cancellation
- result
- persistence
- recovery

## 二、各知识点详细解释

长任务不能依赖一次 HTTP 请求一直挂着：

```text
Create Task
→ taskId
→ Running
→ progress
→ Completed
→ result
```

浏览器可以通过 polling、SSE/WebSocket 等方式获得状态。

如果页面刷新：

```text
taskId
→ query task
→ restore UI
```

## 三、本章面试题与答案

### 题：AI Agent 运行 5 分钟，浏览器刷新后怎么办？

**答案：**

不能把 Agent 状态只放在前端内存里。应创建持久化 taskId 和执行状态，后端/worker 独立运行任务；页面重新加载后根据 taskId 查询或订阅状态，再恢复 UI。

---
