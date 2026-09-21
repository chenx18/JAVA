# 第四章 React 闭包、Effect 与依赖

## 一、本章具体知识点

- render snapshot
- stale closure
- dependency array
- effect cleanup
- event handler
- async callback
- functional update

## 二、各知识点详细解释

每次 render 都会形成一套新的 JavaScript 闭包环境，因此异步回调可能捕获某次 render 的 state 快照。

```js
function Counter() {
  const [count, setCount] = useState(0)

  function delayed() {
    setTimeout(() => {
      console.log(count)
    }, 1000)
  }
}
```

这里打印的是创建这个回调时对应 render 的 count。

解决方案之一是 functional update：

```js
setCount(c => c + 1)
```

## 三、本章面试题与答案

### 题：React 为什么有 stale closure？

**答案：**

函数组件每次 render 都会形成新的闭包环境，事件回调和 Effect 捕获的是当次 render 的变量快照。因此异步回调可能继续读取旧 state。应该根据场景使用正确依赖、functional update、ref 或重新设计数据流，而不是简单地给 dependency array 加空数组。

---
