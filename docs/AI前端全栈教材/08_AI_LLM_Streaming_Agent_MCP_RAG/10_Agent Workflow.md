# 第十章 Agent Workflow

## 一、本章具体知识点

- sequential
- parallel
- routing
- retry
- fallback
- human-in-the-loop
- supervisor
- sub-agent
- timeout

## 二、各知识点详细解释

### Sequential

```text
A → B → C
```

### Parallel

```text
      → A →
Start      → Merge
      → B →
```

### Routing

```text
Question
→ classifier
→ Agent A / B / C
```

### Human-in-the-loop

高风险动作需要人工确认：

```text
Agent
→ Tool proposal
→ UI confirmation
→ execute
```

## 三、本章面试题与答案

### 题：为什么 Agent Workflow 需要状态机？

**答案：**

不同节点可能有 pending、running、success、failed、retrying、cancelled 等状态，并且可能涉及长任务和恢复。显式状态机可以让每个步骤的输入输出和错误路径可预测，也便于持久化和 UI 展示。

---
