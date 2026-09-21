# 第七章 mapped types、template literal 与 Utility Types

## 一、本章具体知识点

- mapped types
- key remapping
- template literal types
- Partial
- Required
- Readonly
- Pick
- Omit
- Record
- Exclude
- Extract
- NonNullable
- ReturnType
- Parameters
- Awaited

## 二、各知识点详细解释

### mapped type

```ts
type Optional<T> = {
  [K in keyof T]?: T[K]
}
```

### Key Remapping

```ts
type Getters<T> = {
  [K in keyof T as `get${Capitalize<string & K>}`]: () => T[K]
}
```

### Record

```ts
type UserMap = Record<string, User>
```

### Partial

把所有属性变成可选。

```ts
type UpdateUser = Partial<User>
```

### Pick / Omit

Pick 保留指定属性；Omit 删除指定属性。

## 三、本章面试题与答案

### 题：Partial 的原理？

**答案：**

Partial 本质是 mapped type：遍历 `keyof T`，把每个属性增加 `?`，因此得到一个所有属性可选的新类型。

---
