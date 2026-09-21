# 第四章 reactive

## 一、本章具体知识点

- Proxy
- Reflect
- deep reactivity
- readonly
- shallowReactive
- reactive identity
- toRaw
- markRaw

## 二、各知识点详细解释

`reactive(obj)` 返回对象的响应式 Proxy，而不是把原对象原地改造成 Proxy：

```js
const raw = { count: 0 }
const state = reactive(raw)

state !== raw // true
```

读取属性时通过 Proxy 拦截并追踪依赖，设置属性时触发对应 effect。

Vue 官方文档明确说明 `reactive()` 返回的是原对象的 Proxy，并建议避免依赖原始对象与代理对象严格身份相等。([vuejs.org](https://vuejs.org/guide/essentials/reactivity-fundamentals))

## 三、本章面试题与答案

### 题：Vue 3 为什么使用 Proxy？

**答案：**

Proxy 可以代理整个对象并拦截更广泛的对象内部操作，例如属性读取、设置、删除、枚举等，相比 Vue 2 基于 Object.defineProperty 的逐属性拦截，模型更完整，也更适合深层和动态属性响应式。

---
