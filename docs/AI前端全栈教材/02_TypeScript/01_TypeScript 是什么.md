# 第一章 TypeScript 是什么

## 一、本章具体知识点

- TypeScript 的定位
- 静态检查与 JavaScript 运行时
- 编译与类型擦除
- structural typing
- 类型注解与类型推断
- 类型收窄
- TS 不改变 JS 运行时语义

## 二、各知识点详细解释

TypeScript 是 JavaScript 的类型系统和开发工具增强层。TS 类型主要服务于开发期检查、编辑器提示、重构和 API 设计，运行到浏览器/Node 前通常会被编译成 JavaScript；普通 TS 类型不会作为运行时对象自动存在。

### 类型推断

```ts
const count = 1
```

TypeScript 可以推断 count 为 number，而不需要显式写：

```ts
const count: number = 1
```

### Structural Typing

TS 主要采用结构类型系统：只要结构满足目标类型，就可以赋值。

```ts
interface User {
  name: string
}

const obj = { name: 'Tom', age: 18 }
const user: User = obj
```

额外属性并不一定构成失败，具体还要区分变量赋值和对象字面量的 excess property check。

## 三、本章面试题与答案

### 题 1：TypeScript 是不是另一门语言？

**答案：**

TS 是 JavaScript 的超集，主要增加类型系统等开发期能力。最终仍然要编译成 JavaScript 在 JS runtime 中执行，普通类型信息不会直接成为运行时逻辑。

### 题 2：TypeScript 和 Java 的类型系统有什么核心区别？

**答案：**

Java 是运行时参与应用执行的静态类型语言，编译后运行在 JVM；TypeScript 主要提供编译期类型检查，运行时仍然是 JavaScript，因此很多 TS 类型不会出现在运行时。

---
