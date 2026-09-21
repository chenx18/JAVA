# 第九章 Discriminated Union 与类型安全状态机

## 一、本章具体知识点

- discriminant
- union narrowing
- state machine
- API result modeling
- AI UI state
- exhaustive switch

## 二、各知识点详细解释

经典模型：

```ts
type Result =
  | { type: 'success'; data: User }
  | { type: 'error'; message: string }
  | { type: 'loading' }
```

通过 `type` 判断，TypeScript 可以自动收窄：

```ts
switch (result.type) {
  case 'success':
    result.data
    break
  case 'error':
    result.message
    break
  case 'loading':
    break
}
```

这尤其适合 Vue/React UI 状态，以及 AI Streaming / Tool Calling / Agent 状态。

## 三、本章面试题与答案

### 题：为什么前端状态推荐使用 Discriminated Union？

**答案：**

因为它能把“状态 + 当前状态允许的数据”绑定起来。例如 loading 状态不应该访问 data，error 状态必须有 message。通过统一 discriminant，编译器可以自动收窄并帮助发现非法状态组合。

---
