# 第五章 keyof、typeof、索引访问类型

## 一、本章具体知识点

- keyof
- typeof in type position
- T[K]
- indexed access type
- key constraint
- as const

## 二、各知识点详细解释

### keyof

```ts
interface User {
  id: string
  name: string
}

type Keys = keyof User
// 'id' | 'name'
```

### 索引访问

```ts
type UserName = User['name']
// string
```

### keyof + 泛型

```ts
function get<T, K extends keyof T>(obj: T, key: K): T[K] {
  return obj[key]
}
```

这个模式是 TypeScript 高级类型安全 API 的基础。

## 三、本章面试题与答案

### 题：`keyof` 是什么？

**答案：**

`keyof T` 会得到 T 的键类型联合。它经常和泛型约束一起使用，例如 `K extends keyof T` 可以保证传入 key 一定存在于对象类型中。

---
