# 第八章 unknown、any、never 与类型收窄

## 一、本章具体知识点

- any
- unknown
- never
- void
- narrowing
- typeof guard
- in guard
- instanceof guard
- user-defined type guard
- exhaustive check

## 二、各知识点详细解释

### unknown

unknown 表示“现在不知道类型”，但使用前必须先收窄。

```ts
function print(value: unknown) {
  if (typeof value === 'string') {
    console.log(value.toUpperCase())
  }
}
```

### any

any 关闭很多类型检查，应限制使用。

### never

表示不可能出现的值，也常用于不会正常返回的函数或 exhaustiveness check：

```ts
function assertNever(x: never): never {
  throw new Error('Unexpected value')
}
```

## 三、本章面试题与答案

### 题：unknown 和 any 的区别？

**答案：**

二者都表示未知类型，但 unknown 更安全：你不能直接对 unknown 做任意操作，必须先通过类型守卫缩小范围；any 会直接关闭大量检查。处理 API、JSON、用户输入时更推荐 unknown。

---
