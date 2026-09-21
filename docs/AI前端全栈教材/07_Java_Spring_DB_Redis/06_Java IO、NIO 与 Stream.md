# 第六章 Java IO、NIO 与 Stream

## 一、本章具体知识点

- InputStream / OutputStream
- Reader / Writer
- ByteBuffer
- Channel
- Selector
- Files
- CompletableFuture Stream API
- stream pipeline

## 二、各知识点详细解释

Byte 流适合原始二进制，Character Stream 适合字符数据。NIO 通过 buffer/channel 等抽象支持更灵活的 IO 模型。

Java Stream API 是集合数据处理抽象，不等同于 IO stream。它通过 pipeline 表达过滤、映射、聚合等操作。

## 三、本章面试题与答案

### 题：Java IO 和 Java Stream 是同一个 Stream 吗？

**答案：**

不是。IO Stream 是字节/字符数据流，负责数据输入输出；Java 8 Stream API 是集合/序列数据处理抽象，主要表达 filter/map/reduce 等计算 pipeline。

---
