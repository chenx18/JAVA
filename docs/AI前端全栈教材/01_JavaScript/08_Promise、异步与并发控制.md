# 第八章 Promise、异步与并发控制

## 一、本章具体知识点

- 同步与异步
- Promise 状态
- executor
- resolve / reject
- then / catch / finally
- Promise chain
- Promise resolution procedure
- thenable
- async / await
- Promise.all
- allSettled
- race
- any
- 并发控制
- 重试
- 取消

## 二、各知识点详细解释

### 1. Promise 解决什么问题

Promise 用一个对象表示未来某个异步结果，使异步控制流可以通过链式组合表达成功、失败以及后续操作。

### 2. Promise 三态

```text
pending
→ fulfilled
→ rejected
```

状态一旦从 pending 进入 settled，就不能再改变。

### 3. then 为什么返回新的 Promise

因为它要支持链式异步组合：

```js
fetch('/api')
  .then(res => res.json())
  .then(data => render(data))
  .catch(handleError)
```

每个 then 根据回调返回值或抛出的异常决定下一个 Promise 的状态。

### 4. Promise resolution procedure

如果 then 回调返回普通值，下一个 Promise fulfilled；返回 Promise/thenable，则要“跟随”它；抛异常则 rejected。

### 5. async / await

async 函数始终返回 Promise。await 不会阻塞 JS 线程，而是暂停当前 async 函数的后续执行，把后续恢复安排到异步流程中。

### 6. 并发控制

不要为了“异步”而顺序等待：

```js
const a = await fetchA()
const b = await fetchB()
```

如果两个请求互不依赖，可以：

```js
const [a, b] = await Promise.all([fetchA(), fetchB()])
```

## 三、本章面试题与答案

### 题 1：Promise 为什么可以解决回调地狱？

**答案：**

Promise 把异步结果抽象成可组合的对象，并通过 then/catch/finally 组织后续步骤，使控制流从嵌套回调转成链式结构。它并没有让异步执行变成同步，而是改善了组合模型。

### 题 2：async/await 会阻塞线程吗？

**答案：**

不会。await 会暂停当前 async 函数的后续执行，让出当前调用栈；异步操作完成后再恢复继续执行。主线程仍然可以处理其他任务。

### 题 3：Promise.all 和 Promise.allSettled 的区别？

**答案：**

Promise.all 任何一个输入 rejected 就会整体 rejected；allSettled 会等待所有输入完成，并返回每一个任务的 fulfilled/rejected 结果，适合需要收集部分成功部分失败结果的场景。

### 题 4：Promise.race 和 Promise.any 的区别？

**答案：**

race 关注“谁先 settled”，第一个 fulfilled 或 rejected 都会决定结果；any 关注“谁先 fulfilled”，只有全部 rejected 时才失败。

---
