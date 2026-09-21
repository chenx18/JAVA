# 第十七章 MySQL Transaction 与锁

## 一、本章具体知识点

- ACID
- isolation levels
- MVCC
- row lock
- gap lock
- deadlock
- optimistic locking
- pessimistic locking

## 二、各知识点详细解释

MVCC 通过版本可见性减少读写阻塞，在常见隔离级别下让读操作看到符合规则的数据版本。

死锁不是“数据库坏了”，而是两个事务互相等待锁形成环，需要统一锁顺序、缩短事务和处理重试。

## 三、本章面试题与答案

### 题：数据库为什么会死锁？

**答案：**

多个事务以不同顺序获取资源时可能互相等待，例如 T1 持有 A 等 B，T2 持有 B 等 A。数据库通常会检测死锁并回滚其中一个事务。应用层还应设计一致的加锁顺序和可重试机制。

---
