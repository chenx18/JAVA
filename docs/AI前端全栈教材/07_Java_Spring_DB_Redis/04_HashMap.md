# 第四章 HashMap

## 一、本章具体知识点

- hash
- bucket
- collision
- hashCode
- equals
- load factor
- resize
- treeification
- red-black tree
- Java 8+ HashMap

## 二、各知识点详细解释

HashMap 的核心目标是根据 key 快速定位 value。

抽象结构：

```text
key
↓
hash
↓
bucket index
↓
entry
↓
key.equals()
```

发生 hash collision 后，多个 key 进入同一个 bucket，需要进一步比较。现代 JDK 的 HashMap 在碰撞较多且满足条件时可以把桶中的链表转为红黑树，使极端查找从链式线性路径改善到近似对数级。

扩容时通常需要增加桶数量并重新分配 entry，因此大表 resize 有成本，初始化容量可以根据规模合理设置。

## 三、本章面试题与答案

### 题：HashMap 为什么需要 hashCode 和 equals？

**答案：**

hashCode 用于把 key 快速映射到候选 bucket；equals 用于在同一 bucket 或冲突候选中确认是不是同一个逻辑 key。对象重写 equals 时必须遵守 hashCode 一致性约定，否则 HashMap/HashSet 会出现无法正确查找的问题。

### 题：HashMap 为什么需要红黑树？

**答案：**

当大量不同 key 发生 hash collision，单纯链表会让查找时间退化。满足树化条件后把桶转换成红黑树，可以把这类极端碰撞下的查找复杂度改善到近似 O(log n)，同时保持一般场景的低常数开销。

---
