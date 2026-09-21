# 第八章 CAS、Atomic 与并发容器

## 一、本章具体知识点

- CAS
- ABA
- AtomicInteger
- LongAdder
- ConcurrentHashMap
- lock-free
- false sharing

## 二、各知识点详细解释

CAS 是 compare-and-set：只有当前值仍然等于预期值时才更新。

```text
read current
→ compare expected
→ swap new value
```

ABA 问题是值从 A → B → A 后，CAS 只看到最终还是 A，但中间实际上发生过变化。需要版本戳/标记等方案时可以避免。

LongAdder 适合高并发计数，减少多个线程竞争同一个计数热点的成本。

## 三、本章面试题与答案

### 题：CAS 有什么问题？

**答案：**

CAS 的核心问题包括 ABA、竞争激烈时自旋成本和多变量一致性难处理。ABA 可以通过版本号等机制解决；复杂事务性状态通常仍然更适合锁或其他同步机制。

---
