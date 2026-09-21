# 第八章 Scheduler 与 nextTick

## 一、本章具体知识点

- scheduler
- job queue
- batching
- deduplication
- microtask
- queueJob
- flushJobs
- nextTick
- DOM update timing

## 二、各知识点详细解释

响应式数据变化后，Vue 不一定立即执行完整 DOM 更新，而是把任务进入调度队列并批量处理。

简化模型：

```text
state.value++
state.value++
state.value++
↓
多个 job
↓
queue + dedupe
↓
microtask flush
↓
render effect
↓
DOM update
```

`nextTick` 的价值是在 Vue 的异步更新队列完成到合适时机后执行后续代码，因此适合在状态修改后读取最新 DOM。

## 三、本章面试题与答案

### 题：为什么 Vue 修改多次数据不会同步更新多次 DOM？

**答案：**

Vue 会把组件更新任务放进调度队列，通过队列去重和批量 flush，把多个同步状态修改合并到一次更新过程中，从而减少重复渲染。

### 题：nextTick 是什么？

**答案：**

nextTick 用于等待 Vue 当前一轮异步更新队列完成到可观察 DOM 更新的时机，然后执行回调。它不是简单的 setTimeout。

---
