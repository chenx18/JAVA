# 第十一章 项目面试：Vue AI Chat

## 一、本章具体知识点

- 项目背景
- 技术选型
- Streaming
- SSE
- Vue state
- Pinia
- message model
- error recovery
- security
- performance

## 二、各知识点详细解释

项目讲解顺序：

```text
业务问题
→ 架构
→ 数据模型
→ 核心流程
→ 技术难点
→ 性能
→ 安全
→ 结果
```

核心数据模型：

```text
Conversation
Message
MessagePart
ToolCall
Citation
Attachment
```

Streaming 不要直接把字符串 append 到一个全局大对象再导致整个页面更新，应设计“当前 assistant message + parts”的局部更新。

## 三、本章面试题与答案

### 题：你在 AI Chat 项目里最大的技术难点是什么？

**答案模板：**

可以讲 Streaming UI。后端持续返回 chunk，前端需要解决流解析、取消、错误恢复和高频更新。我把网络事件转换成内部 message events，再通过状态机维护 submitting/streaming/tool/done/error，渲染层只更新当前消息，避免整个历史列表频繁重新计算。

### 题：为什么选择 SSE？

**答案：**

项目主要是用户发送一次请求，服务器持续向客户端推送生成事件，SSE 与这种单向流式模型匹配；同时我们需要标准 HTTP 基础设施。若以后变成双向实时协作，再考虑 WebSocket。

---
