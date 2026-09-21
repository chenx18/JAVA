# 第七章 Proxy、Reflect 与元编程

## 一、本章具体知识点

- Proxy
- handler
- target
- get
- set
- has
- deleteProperty
- ownKeys
- getOwnPropertyDescriptor
- apply
- construct
- Reflect
- Proxy 不变量
- 递归代理
- 响应式中的依赖追踪

## 二、各知识点详细解释

### 1. Proxy 是什么

Proxy 用一个代理对象拦截目标对象的内部操作：

```js
const target = { count: 0 }
const proxy = new Proxy(target, {
  get(target, key, receiver) {
    console.log('get', key)
    return Reflect.get(target, key, receiver)
  },
  set(target, key, value, receiver) {
    console.log('set', key, value)
    return Reflect.set(target, key, value, receiver)
  }
})
```

### 2. Reflect 为什么经常与 Proxy 成对出现

Reflect 提供与对象内部操作对应的标准化 API，使用 `Reflect.get/set` 可以更好地保留 receiver 语义，并明确返回操作是否成功。

### 3. 与 Vue 的关系

Vue 3 的 `reactive()` 就建立在 Proxy 等机制之上：读取时 track，修改时 trigger。

## 三、本章面试题与答案

### 题 1：Proxy 和 Object.defineProperty 有什么区别？

**答案：**

defineProperty 主要拦截具体属性的 getter/setter；Proxy 代理整个对象，可以拦截 get、set、has、deleteProperty、ownKeys、apply、construct 等更广泛的对象内部操作。Vue 3 因此可以更自然地处理新增属性、删除属性、数组索引等情况。

### 题 2：Reflect 为什么通常和 Proxy 一起用？

**答案：**

Reflect 提供与对象内部操作对应的统一 API，尤其 `Reflect.get/Reflect.set` 的 receiver 语义适合 Proxy 场景，可以减少手工实现对象语义时的边界错误。

---
