# 第六章 computed

## 一、本章具体知识点

- computed
- lazy
- cache
- dirty
- getter
- writable computed
- invalidation
- reactive effect

## 二、各知识点详细解释

computed 是“派生状态”，不是普通函数。

```js
const price = ref(100)
const count = ref(2)
const total = computed(() => price.value * count.value)
```

它的关键特点：

```text
依赖没有变化
→ 继续使用缓存值

依赖变化
→ 失效 / dirty
→ 下次读取时重新计算
```

Vue 官方文档也将 computed 描述为派生值，并在响应式系统内部通过 effect 管理失效与重新计算。([vuejs.org](https://vuejs.org/guide/essentials/computed))

## 三、本章面试题与答案

### 题：computed 为什么有缓存？

**答案：**

因为 computed 表达的是由响应式依赖派生出的值。只要依赖没变，重复读取不需要重新计算；依赖变化时将缓存标记为失效，下一次读取重新计算。这样可以避免无意义的重复计算。

### 题：computed 和 methods 有什么区别？

**答案：**

computed 建立依赖并缓存结果，适合派生状态；methods 每次调用都会执行。依赖稳定且需要重复读取时 computed 更合适。

---
