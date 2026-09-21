# 第十四章 项目面试：MCP Workspace

## 一、本章具体知识点

- MCP client
- tool discovery
- tool UI
- task
- generative UI
- approval
- authorization

## 二、各知识点详细解释

例如：

```text
查订单
→ getOrder tool
→ 展示 OrderCard
→ 判断可退款
→ 创建 refund request
→ 用户确认
→ execute
```

前端应该把 Tool 事件转换成可观察 UI，而不是只显示一行“AI 正在思考”。

## 三、本章面试题与答案

### 题：为什么 Tool 调用需要 UI 状态？

**答案：**

工具可能耗时、失败、需要授权或需要用户确认。把它建模成状态后，用户可以知道当前执行到哪一步，并且系统可以正确处理重试、取消和最终结果。

---
