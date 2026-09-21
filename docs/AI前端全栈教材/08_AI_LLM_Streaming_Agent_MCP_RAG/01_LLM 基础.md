# 第一章 LLM 基础

## 一、本章具体知识点

- LLM
- Token
- Context Window
- Prompt
- System / Developer / User message
- Temperature
- Structured Output
- Multimodal
- Model inference

## 二、各知识点详细解释

### 1. LLM 是什么

LLM 是根据上下文预测下一段 token 的模型体系。对应用开发者而言，需要重点理解输入输出、上下文、工具、结构化结果和流式事件，而不必一开始钻到 Transformer 数学细节。

### 2. Token

模型处理的不是直接的“字符”，而是 tokenizer 切出的 token。token 数影响上下文容量、延迟和成本。

### 3. Context

模型一次推理能够看到的输入上下文存在窗口限制。聊天应用不能无上限把历史消息塞回模型，需要摘要、截断、检索或持久化记忆。

### 4. Structured Output

传统 LLM：

```text
文本
```

应用更需要：

```json
{"type":"order","id":"123","status":"paid"}
```

结构化输出便于前端渲染和 Tool Calling，但仍需要运行时 schema 验证，不能因为“模型承诺 JSON”就信任它。

## 三、本章面试题与答案

### 题：Token 为什么重要？

**答案：**

Token 是模型处理上下文的基本单位之一，直接影响上下文窗口、输入输出成本、推理延迟和长文本设计。聊天历史、RAG 文档和 Tool 输出都应控制 token 规模。

### 题：Context Window 是什么？

**答案：**

它可以理解为一次模型推理可用的上下文容量，包括消息、工具描述、检索内容等。它不是“长期记忆”，长期知识仍需要数据库、RAG、摘要或状态系统来管理。

---
