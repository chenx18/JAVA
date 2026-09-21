# 第十三章 Pinia

## 一、本章具体知识点

- store
- state
- getters
- actions
- setup store
- options store
- plugin
- persistence
- SSR
- local/shared/server state

## 二、各知识点详细解释

Pinia 是 Vue 官方生态推荐的状态管理方案之一。它把共享状态组织为 store。

```text
Component
→ Store
→ State / Getter / Action
```

关键不是“所有状态放 Store”，而是区分：

```text
Local UI State
Shared Client State
URL State
Server State
```

例如一个 Dialog 是否打开通常不需要进入全局 store。

## 三、本章面试题与答案

### 题：Pinia 和 Vuex 有什么主要区别？

**答案：**

Pinia API 更简洁，强调 state/getter/action，去掉了传统 Vuex 中独立 mutations 的强制分层；同时 TypeScript 推导更自然。Vue 官方迁移建议也推荐新 Vue 3 应用优先考虑 Pinia。

---
