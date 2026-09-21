# 第九章 Thread Pool

## 一、本章具体知识点

- corePoolSize
- maximumPoolSize
- workQueue
- keepAlive
- ThreadFactory
- RejectedExecutionHandler
- bounded queue
- graceful shutdown

## 二、各知识点详细解释

线程池的价值不是“省线程”，而是统一控制并发资源、复用线程、排队任务和拒绝策略。

典型流程：

```text
提交任务
→ 核心线程未满？创建
→ 核心线程满，入队
→ 队列满，是否达到 maxPoolSize？
→ 否则创建非核心线程
→ 仍无法接收 → rejection
```

生产环境一般要明确队列上限、拒绝策略、命名、监控和优雅关闭，避免无界队列导致内存积压。

## 三、本章面试题与答案

### 题：线程池为什么比不断 new Thread 更好？

**答案：**

线程创建、调度和销毁有成本，而且无限创建会导致 CPU 和内存竞争。线程池把并发数量、任务排队和拒绝策略集中管理，还能复用线程并统一监控。

---
