# 第六章 React 状态管理

## 一、本章具体知识点

- Local state
- Context
- Reducer
- external store
- Redux 思路
- Zustand 思路
- Server state
- URL state

## 二、各知识点详细解释

状态首先按归属分类，而不是按库分类：

```text
Local UI State
Shared Client State
Server State
URL State
Form State
```

如果状态只被一个组件使用，不需要放全局 Store。

## 三、本章面试题与答案

### 题：什么时候应该使用 Context？

**答案：**

当很多层组件需要共享稳定的上下文，例如主题、用户信息、依赖注入对象时可以用 Context。大规模高频变化的业务状态如果全部依赖 Context，可能产生较大更新范围，应考虑更细粒度的 external store 或状态切分。

---
