# 第八章 React / Vue 对比

## 一、本章具体知识点

- Vue fine-grained reactivity
- React state update
- Vue compiler
- React reconciliation
- effect
- hooks
- component model
- performance model

## 二、各知识点详细解释

Vue 更依赖运行时响应式依赖追踪和编译器优化：状态属性被组件读取时建立依赖，变化时可以较细粒度地调度更新。Vue 官方也把自己的模型描述为 mutable fine-grained reactivity。([vuejs.org](https://vuejs.org/guide/extras/composition-api-faq))

React 更强调 state update → render → reconciliation 的模型，每次 render 产生新的 UI 描述。

这不代表“Vue 一定更快”或“React 一定更快”，真正性能取决于应用结构、更新频率、DOM 数量、编译优化和具体运行场景。

## 三、本章面试题与答案

### 题：Vue 和 React 最大的思想差异？

**答案：**

可以从更新模型理解：Vue 以响应式依赖追踪为核心，组件内部访问哪些 reactive state 会形成依赖；React 以 state update 驱动 render/reconciliation，每次 render 可以理解为当前状态快照的 UI 计算。两者都可以通过编译器、调度和手工优化改善性能，因此不能简单用“谁更快”概括。
