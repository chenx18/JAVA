# 第十五章 Embedding / Vector Search

## 一、本章具体知识点

- embedding
- vector
- cosine similarity
- dot product
- nearest neighbor
- ANN
- metadata filter
- hybrid search
- rerank

## 二、各知识点详细解释

Embedding 把文本/对象映射成向量表示，然后根据向量距离找语义相近内容。

常见相似度：

- cosine similarity
- dot product
- Euclidean distance

大规模向量检索通常会使用 ANN 等近似最近邻算法，以降低全量精确扫描的成本。

Hybrid Search：

```text
Keyword search
+
Vector search
→ merge/rerank
```

## 三、本章面试题与答案

### 题：为什么向量搜索不等于全文搜索？

**答案：**

全文搜索强调词项、倒排索引和精确/词法匹配；向量搜索强调语义空间中的相似度。两者对拼写、专有名词、语义改写等场景各有优势，实际 RAG 往往结合 hybrid search。

---
