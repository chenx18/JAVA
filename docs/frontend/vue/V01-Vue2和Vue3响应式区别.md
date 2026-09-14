# Vue2 和 Vue3 响应式区别

> 专题：Vue 专题  
> 编号：V01

### 一句话答案

Vue2 主要基于 `Object.defineProperty` 劫持属性，Vue3 基于 `Proxy` 代理对象，能力更完整，性能和类型支持也更好。

### 核心原理

Vue2：

```text
Object.defineProperty
初始化时递归劫持已有属性
数组需要特殊处理
新增/删除属性不易追踪
```

Vue3：

```text
Proxy
代理整个对象
可监听新增、删除、in、索引等操作
配合 Reflect
```

### 项目里怎么用

- Vue2 中新增对象属性要用 `Vue.set`。
- Vue3 中响应式能力更自然。
- 迁移项目时注意响应式对象解构问题。

### 常见坑

- Vue3 里直接解构 reactive 对象导致响应性丢失。
- Vue2 中对象新增属性页面不更新。
- 把响应式对象传来传去导致数据来源不清晰。

### 面试表达

Vue3 用 Proxy 代理整个对象，解决了 Vue2 defineProperty 对新增属性、删除属性和数组索引监听不完整的问题。但 Vue3 也要注意解构会丢失响应式，需要用 toRefs 或保持对象访问。
