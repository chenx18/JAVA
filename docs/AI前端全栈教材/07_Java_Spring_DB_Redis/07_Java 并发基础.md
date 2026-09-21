# 第七章 Java 并发基础

## 一、本章具体知识点

- Thread
- Runnable
- Callable
- Future
- Executor
- ExecutorService
- synchronized
- volatile
- Lock
- Atomic
- ConcurrentHashMap
- happens-before

## 二、各知识点详细解释

并发面试不能只背“线程安全”。要区分三个问题：

```text
原子性
可见性
有序性
```

`synchronized` 可以提供互斥与内存可见性；`volatile` 主要解决可见性和一定范围内的有序性，但不能把复合操作变成原子操作；CAS 常用于 lock-free/low-lock 数据结构。

### happens-before

它定义线程之间哪些写操作对哪些读操作具有可见性保证，是理解 Java Memory Model 的核心。

## 三、本章面试题与答案

### 题：volatile 能保证 i++ 安全吗？

**答案：**

不能。i++ 包含读取、加一、写回多个步骤，volatile 主要保证可见性和某些内存排序语义，不保证这个复合操作的原子性。需要锁、AtomicInteger 等机制。

### 题：synchronized 与 Lock 区别？

**答案：**

synchronized 是语言级锁，语义简单、自动释放；Lock 是 API，提供 tryLock、可中断获取、多个条件队列等更丰富能力。选择依据是是否需要这些高级控制，而不是简单认为 Lock 一定更快。

---
