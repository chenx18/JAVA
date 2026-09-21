# 第十二章 KeepAlive、Teleport、Suspense、Transition

## 一、本章具体知识点

- KeepAlive
- instance cache
- activated/deactivated
- include/exclude/max
- Teleport
- Suspense
- async dependency
- Transition
- TransitionGroup

## 二、各知识点详细解释

KeepAlive 缓存组件实例，而不是简单隐藏 DOM；再次进入时可以恢复既有状态。

Teleport 改变的是 DOM 挂载位置，不改变 Vue 逻辑组件关系，非常适合 Modal、Drawer 等。

Suspense 用于协调异步依赖与 fallback；Transition/TransitionGroup 为进入、离开、移动提供动画生命周期。

## 三、本章面试题与答案

### 题：KeepAlive 缓存的是什么？

**答案：**

主要缓存组件实例及其相关渲染状态，使组件切换时可以从缓存恢复，而不是每次销毁后重建。它与简单的 CSS display:none 不是同一概念。

### 题：Teleport 改变了什么？

**答案：**

Teleport 改变 DOM 节点的实际挂载位置，但 Vue 的逻辑组件树关系并没有因此被简单打散，因此事件、provide/inject 等逻辑关系仍按组件树工作。

---
