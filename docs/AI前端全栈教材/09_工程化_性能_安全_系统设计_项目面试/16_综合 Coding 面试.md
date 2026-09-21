# 第十六章 综合 Coding 面试

## 一、本章具体知识点

- debounce
- throttle
- Promise.all
- retry
- concurrency pool
- EventEmitter
- LRU
- deepClone
- deepEqual
- Vue reactivity mini version
- SSE parser
- state machine

## 二、各知识点详细解释

Coding 题重点不是背模板，而是：

```text
输入约束
→ 数据结构
→ 边界条件
→ 时间/空间复杂度
→ 错误处理
→ 测试
```

AI 方向尤其建议手写一个简化 Streaming parser：输入连续 chunk，可能把一条 SSE event 拆成多个 chunk，因此必须使用 buffer，不应假设一次 read 就得到完整事件。

## 三、本章面试题与答案

### 题：实现一个并发控制器，最多同时执行 3 个任务。

**答案思路：**

维护任务队列和 activeCount；每次 activeCount < limit 就取出任务执行；任务 resolve/reject/finally 后 activeCount--，继续调度；全部任务完成后 resolve 总结果。必须保证 rejected task 不让调度器永久卡死。

### 题：为什么 SSE parser 需要 buffer？

**答案：**

网络 chunk 边界与 SSE event 边界没有一一对应关系，一个事件可能被拆到多个 chunk，多个事件也可能出现在一个 chunk。因此必须把不完整尾部暂存在 buffer 中，等下一次 chunk 拼接后再解析完整事件。

---
