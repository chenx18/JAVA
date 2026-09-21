# 第十四章 RAG

## 一、本章具体知识点

- Retrieval Augmented Generation
- document ingestion
- chunking
- embedding
- vector database
- retrieval
- rerank
- context assembly
- citation

## 二、各知识点详细解释

RAG 的核心：

```text
问题
→ Retrieval
→ 找相关知识
→ 注入 Context
→ LLM 生成回答
```

完整数据流：

```text
Document
→ Parse
→ Chunk
→ Embedding
→ Vector Store

Question
→ Embedding
→ Retrieval
→ Rerank
→ Context
→ LLM
```

### 为什么需要 chunk

整份文档直接 embedding 往往粒度过大；拆成语义合理的小块，可以提高相关检索精度，同时控制上下文规模。

### Citation

RAG 应尽量保留 source metadata，让最终答案能够展示来源而不是只有生成文本。

## 三、本章面试题与答案

### 题：RAG 为什么能减少模型幻觉？

**答案：**

RAG 给模型提供了与问题相关的外部证据，使生成可以基于检索结果。但它不是自动消除幻觉：如果检索错误、上下文缺失、模型误读仍然可能产生错误。因此还需要检索质量、rerank、引用和答案校验。

### 题：RAG 和微调的区别？

**答案：**

RAG 主要改变推理时可获得的上下文，适合需要频繁更新的外部知识；微调主要改变模型参数，使模型学习特定行为、格式或任务模式。知识频繁更新的场景通常更适合 RAG，而不是每次知识更新都重新训练。

---
