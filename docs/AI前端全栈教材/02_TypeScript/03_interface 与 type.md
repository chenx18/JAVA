# 第三章 interface 与 type

## 一、本章具体知识点

- interface
- type alias
- declaration merging
- extends
- intersection
- union
- generic interface
- 工程选型

## 二、各知识点详细解释

interface 更强调对象结构和可扩展契约，支持 declaration merging：

```ts
interface User {
  id: string
}

interface User {
  name: string
}
```

最终会合并。

type 可以表达更广泛的类型表达式：

```ts
type Status = 'idle' | 'loading'
type Result<T> = { data: T } | { error: string }
```

实际工程中二者很多场景都能互换，不要把“interface 一定优于 type”当绝对结论。

## 三、本章面试题与答案

### 题：interface 和 type 的区别？

**答案：**

interface 更适合描述可扩展对象契约，并支持 declaration merging；type 可以表达联合、交叉、条件类型、元组等更广泛的类型表达式。对象结构场景二者通常都可以使用，具体应看代码风格和需求。

---
