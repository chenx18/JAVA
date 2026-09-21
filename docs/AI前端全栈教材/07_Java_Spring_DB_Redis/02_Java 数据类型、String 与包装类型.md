# 第二章 Java 数据类型、String 与包装类型

## 一、本章具体知识点

- 8 种 primitive
- reference
- auto boxing
- unboxing
- Integer cache
- String immutability
- String pool
- StringBuilder
- BigDecimal

## 二、各知识点详细解释

Java primitive 包括 byte、short、int、long、float、double、char、boolean。对象变量保存的是对象引用。

String 是不可变对象。字符串常量可以进入字符串池；运行时 `new String()` 会创建新的 String 对象。String 不可变的价值包括线程安全、hash 缓存、字符串池复用和安全性方面的好处。

金额运算通常使用 BigDecimal，而不是直接依赖 float/double 的二进制浮点结果。

## 三、本章面试题与答案

### 题：String 为什么不可变？

**答案：**

String 的不可变性意味着创建后的字符序列不能被修改。这样可以支持字符串池复用、哈希缓存以及线程安全的共享使用，也可以减少作为不可变 key 时发生内容改变导致的错误。

### 题：== 和 equals 在 Java 中区别？

**答案：**

对于 primitive，== 比较值；对于对象，== 比较引用身份。equals 是对象方法，具体比较规则由类实现，例如 String.equals 比较字符内容。实际判断对象内容通常使用 equals，而不是 ==。

---
