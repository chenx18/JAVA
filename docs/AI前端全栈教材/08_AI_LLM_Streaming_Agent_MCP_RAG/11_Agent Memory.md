# 第十一章 Agent Memory

## 一、本章具体知识点

- conversation history
- short-term state
- long-term memory
- summary
- vector memory
- user profile
- task memory

## 二、各知识点详细解释

不要把所有历史都叫 memory。

```text
当前上下文
→ short-term memory

持久化用户偏好/事实
→ long-term memory

知识库
→ RAG
```

短期上下文受到 token window 限制；长期信息通常存储在数据库，必要时再检索回来。

## 三、本章面试题与答案

### 题：Agent Memory 和 RAG 一样吗？

**答案：**

不完全一样。RAG 主要解决从外部知识源检索上下文；Agent memory 更强调跨任务或跨会话保存与恢复状态/事实。实际系统可以使用同一数据库或向量存储，但概念和生命周期不同。

---
