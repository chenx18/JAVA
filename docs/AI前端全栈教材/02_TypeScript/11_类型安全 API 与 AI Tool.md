# 第十一章 类型安全 API 与 AI Tool

## 一、本章具体知识点

- API DTO
- Result<T>
- discriminated union
- request type
- response type
- runtime validation
- schema
- Tool arguments
- Tool result

## 二、各知识点详细解释

TypeScript 类型只能解决编译期问题，不能自动验证运行时来自网络的 JSON。因此：

```text
TypeScript
→ 编译期保证
Runtime Schema
→ 运行时保证
```

AI Tool 特别需要这一层：模型返回的 arguments 是外部不可信输入，必须做 schema validation。

## 三、本章面试题与答案

### 题：TypeScript 已经有类型了，为什么 API 数据还需要运行时校验？

**答案：**

因为服务端 JSON、用户输入和 LLM 输出发生在运行时，编译器无法证明它们真的符合 TS 类型。TS 需要和运行时 schema validation 配合，才能建立完整的输入信任边界。

---
