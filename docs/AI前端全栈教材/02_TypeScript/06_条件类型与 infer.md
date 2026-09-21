# 第六章 条件类型与 infer

## 一、本章具体知识点

- conditional type
- distributive conditional type
- infer
- never
- any / unknown 对条件类型的影响

## 二、各知识点详细解释

基本形式：

```ts
T extends U ? X : Y
```

### infer

`infer` 用于条件类型中“提取”某个位置的类型。

```ts
type Return<T> = T extends (...args: any[]) => infer R ? R : never
```

这里 R 就是函数返回值类型。

### 分布式条件类型

当条件类型直接作用于裸类型参数并传入联合类型时，会对联合成员分别计算，因此：

```ts
type ToArray<T> = T extends any ? T[] : never
type A = ToArray<string | number>
```

会得到 `string[] | number[]`。

## 三、本章面试题与答案

### 题：infer 是做什么的？

**答案：**

infer 用于条件类型中从某个类型结构里推导并提取一个新的类型变量，比如从函数签名中提取返回值，从 Promise 中提取其内部类型。

---
