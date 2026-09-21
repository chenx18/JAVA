# 第十章 JVM

## 一、本章具体知识点

- JVM
- Heap
- Java Stack
- Metaspace
- PC Register
- Native Method Stack
- class loading
- JIT
- interpreter

## 二、各知识点详细解释

JVM 是运行 Java 字节码的虚拟机规范实现集合。HotSpot 中常见模型包含堆、线程私有栈、元空间等。

Java 代码运行时可能经历：

```text
class loading
→ bytecode verification
→ interpretation
→ profiling
→ JIT compilation
```

不同 JDK/JVM 实现的细节会不同。

## 三、本章面试题与答案

### 题：JVM 内存区有哪些？

**答案：**

从常见 HotSpot/JVM 学习模型讲，可以关注 Heap、每线程 Java Stack、PC Register、Metaspace、Native Method Stack 等。不同 JVM 实现和版本细节可能不同，面试时不要把某一实现的布局说成 Java 语言规范本身。

---
