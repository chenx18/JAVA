# 第九章 系统设计：RAG

## 一、本章具体知识点

- ingestion pipeline
- object storage
- parser
- chunker
- embedding worker
- vector DB
- metadata
- retrieval
- rerank
- citation

## 二、各知识点详细解释

数据导入：

```text
Upload
→ Object Storage
→ Parse
→ Chunk
→ Queue
→ Embedding
→ Vector DB
```

查询：

```text
Question
→ Query preprocessing
→ Vector / Keyword search
→ Filter
→ Rerank
→ Context
→ LLM
→ Citation
```

文档处理适合放后台 worker，避免上传接口一直阻塞。

## 三、本章面试题与答案

### 题：为什么文档 embedding 适合异步队列？

**答案：**

大文件解析、切片和 embedding 都可能耗时，而且不需要占住用户上传 HTTP 请求。队列可以把处理拆成可重试 worker，提高系统吞吐和失败恢复能力。

---
