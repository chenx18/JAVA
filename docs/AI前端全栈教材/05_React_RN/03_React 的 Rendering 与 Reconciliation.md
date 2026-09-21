# 第三章 React 的 Rendering 与 Reconciliation

## 一、本章具体知识点

- Render
- Reconciliation
- Commit
- Fiber
- State update
- Batching
- Priority
- Key
- Re-render

## 二、各知识点详细解释

React 更新可以抽象为：

```text
State update
→ Render phase
→ Reconciliation
→ Commit phase
→ DOM / Native host update
```

Render phase 计算“应该是什么 UI”；Commit phase 把必要变化应用到宿主环境。

React 可以在 render 阶段进行调度和中断，这是现代 React 并发能力的重要基础。

## 三、本章面试题与答案

### 题：Render 和 Commit 有什么区别？

**答案：**

Render 阶段计算新的 React tree，可能执行组件函数和 reconciliation；Commit 阶段把最终需要应用的变更提交到 DOM 或其他 host environment。把“组件函数执行”理解成“DOM 已经更新”是不正确的。

### 题：Key 的作用？

**答案：**

key 用于表达列表元素的稳定身份，让 reconciliation 在列表变化时正确复用、插入、删除和移动节点。index 在可排序、可插入删除的列表中会导致身份不稳定。

---
