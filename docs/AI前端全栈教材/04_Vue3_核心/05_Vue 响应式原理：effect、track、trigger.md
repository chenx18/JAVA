# 第五章 Vue 响应式原理：effect、track、trigger

## 一、本章具体知识点

- ReactiveEffect
- effect
- dependency
- track
- trigger
- targetMap
- WeakMap
- Map
- Set
- activeEffect
- cleanup

## 二、各知识点详细解释

可以把 Vue 响应式抽象成：

```text
对象属性读取
→ track(target, key)
→ 当前 activeEffect 加入 dep

对象属性修改
→ trigger(target, key)
→ 找到 dep
→ 调度 effect
```

依赖图可以理解为：

```text
WeakMap
  target
    ↓
  Map
    key
      ↓
    Set<ReactiveEffect>
```

Vue 官方的“Reactivity in Depth”文档用相同思路解释 reactive effect、依赖追踪与触发，并指出组件渲染本身可以看作一种 reactive effect。([vuejs.org](https://vuejs.org/guide/extras/reactivity-in-depth))

### 为什么需要 cleanup

一个 effect 在不同条件分支下可能依赖不同数据。如果旧依赖没有被清理，就会留下错误订阅。例如：

```js
if (ok.value) {
  state.a
} else {
  state.b
}
```

当 ok 发生变化，effect 的依赖集合也应该同步变化。

## 三、本章面试题与答案

### 题：Vue 响应式系统是怎么知道某个数据变化后谁要更新的？

**答案：**

effect 执行时 Vue 会设置当前 active effect；当 Proxy 或 ref 的 getter 被访问时，通过 track 把 active effect 收集到对应目标对象和属性的依赖集合中；当属性被修改时 trigger 找到该属性对应的 effect，再交给 scheduler 调度执行。

---
