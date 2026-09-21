# 第三章 ref

## 一、本章具体知识点

- Ref
- `.value`
- primitive
- object ref
- dependency tracking
- shallowRef
- triggerRef
- unwrap
- template auto-unwrapping

## 二、各知识点详细解释

`ref()` 用来创建一个带响应式访问入口的容器：

```js
const count = ref(0)
count.value++
```

访问 `.value` 时可以进行依赖追踪，设置 `.value` 时可以触发关联 effect。

对象作为 ref 的值时，默认会被深层响应式化；需要保留原对象或控制成本时可以考虑 shallowRef 等 API。

在模板中，顶层 ref 通常自动解包：

```vue
{{ count }}
```

不需要写 `.value`。

## 三、本章面试题与答案

### 题：ref 和 reactive 有什么区别？

**答案：**

ref 创建一个带 `.value` 访问入口的响应式引用，可以保存 primitive 或 object；reactive 直接返回对象的响应式 Proxy。ref 更适合单个值或希望显式表达“引用”的状态，reactive 更适合对象状态，但不能直接代理 primitive。

---
