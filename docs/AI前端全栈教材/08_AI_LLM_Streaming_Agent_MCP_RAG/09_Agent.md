# 第九章 Agent

## 一、本章具体知识点

- Agent
- tool
- state
- loop
- memory
- planner
- executor
- observation
- workflow

## 二、各知识点详细解释

Agent 不是“更聪明的 Chat”。它通常具有：

```text
Model
+
State
+
Tools
+
Loop
+
Goal
```

典型流程：

```text
Observe
→ Decide
→ Tool
→ Observe result
→ Decide
→ ...
→ Answer
```

Agent 最大的工程复杂度来自：状态、长任务、失败恢复、权限和成本控制。

## 三、本章面试题与答案

### 题：Agent 和普通 Chat 有什么区别？

**答案：**

普通 Chat 通常是输入上下文后生成回答；Agent 还会根据目标和当前状态选择工具、执行动作、读取结果并继续下一步。因此 Agent 是一个循环/工作流系统，而不只是一次模型调用。

---
