# 第十三章 项目面试：AI Agent 数据分析

## 一、本章具体知识点

- SQL Tool
- authorization
- Agent state
- chart generation
- streaming
- long-running task
- audit

## 二、各知识点详细解释

```text
User
→ Agent
→ Query Tool
→ Policy Check
→ Database
→ Analyze
→ Chart
→ Report
```

最重要的不是模型会写 SQL，而是后端必须限制它能访问哪些表、字段和数据范围。

例如：

```text
tenant_id
user_id
allowed columns
read-only SQL
query timeout
row limit
```

## 三、本章面试题与答案

### 题：允许 AI 自己写 SQL 安全吗？

**答案：**

不能直接把模型生成的 SQL 当成可信 SQL 执行。需要限制连接的数据库权限、只读账号、SQL AST/规则校验、表列 allowlist、tenant filter、超时和结果行数，并记录审计日志。

---
