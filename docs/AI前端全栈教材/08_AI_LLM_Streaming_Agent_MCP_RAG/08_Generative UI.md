# 第八章 Generative UI

## 一、本章具体知识点

- structured output
- component registry
- tool result
- dynamic component
- whitelist
- schema validation

## 二、各知识点详细解释

传统 AI：

```text
LLM
→ Text
```

Generative UI：

```text
LLM
→ Structured Result
→ Component Registry
→ Vue Component
```

例如：

```json
{
  "type": "order",
  "orderId": "123",
  "status": "paid"
}
```

前端：

```text
order
→ OrderCard
```

不要让模型直接生成任意 JavaScript / Vue template 并执行。正确方式是有限的组件注册表 + schema。

## 三、本章面试题与答案

### 题：如何保证 Generative UI 安全？

**答案：**

让模型只输出受约束的数据结构，前端根据白名单 component registry 映射到已注册组件，不执行模型返回的任意代码。这样模型只能选择允许的 UI 类型和参数。

---
