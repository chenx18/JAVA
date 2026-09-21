# 第一章 React 的核心思想

## 一、本章具体知识点

- JSX
- Component
- Props
- State
- Render
- Pure rendering
- Event
- Conditional rendering
- List rendering
- Immutability

## 二、各知识点详细解释

React 的核心模型可以概括为：组件根据输入 props 和 state 计算 UI。状态更新后，React 再执行 rendering/reconciliation，最后 commit 到宿主环境。

React 中 state 更接近“某次 render 的快照”。不要把 state 变量理解为一个随时被原地修改的普通变量。

## 三、本章面试题与答案

### 题：React 为什么强调不可变更新？

**答案：**

React 大量优化依赖引用身份和状态快照模型。创建新的对象引用可以清晰表达状态发生变化，也更容易让 memo、reconciliation 等判断更新边界。直接修改原对象会让变化来源不清晰，并可能造成旧快照被篡改。

---
