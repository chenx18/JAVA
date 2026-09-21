# 第七章 watch 与 watchEffect

## 一、本章具体知识点

- watch
- watchEffect
- source
- getter
- immediate
- deep
- once
- flush
- cleanup
- onTrack
- onTrigger

## 二、各知识点详细解释

`watch` 明确指定要监听的数据源：

```js
watch(source, (newValue, oldValue) => {})
```

`watchEffect` 则执行副作用函数时自动追踪依赖：

```js
watchEffect(() => {
  console.log(state.count)
})
```

Vue 官方文档明确区分：watch 更适合显式控制 source 和新旧值，watchEffect 更适合让系统自动追踪回调中同步访问到的响应式依赖。([vuejs.org](https://vuejs.org/guide/essentials/watchers))

### cleanup

异步 watcher 可能发生竞态。旧请求返回后不应该覆盖新请求结果，因此需要清理旧副作用或使用 AbortController 等方案。

## 三、本章面试题与答案

### 题：watch 和 watchEffect 的区别？

**答案：**

watch 显式指定监听源，并可以拿到 newValue/oldValue；watchEffect 自动追踪执行过程中访问的响应式依赖，更适合“依赖什么就对什么产生副作用”的逻辑。watch 更可控，watchEffect 更自动。

### 题：watch 为什么需要 cleanup？

**答案：**

主要用于处理副作用生命周期，例如输入关键词后发请求。如果旧请求没有取消或失效控制，可能比新请求更晚返回并覆盖最新状态。cleanup 可以在下一次重新执行或 watcher 停止前清理旧副作用。

---
