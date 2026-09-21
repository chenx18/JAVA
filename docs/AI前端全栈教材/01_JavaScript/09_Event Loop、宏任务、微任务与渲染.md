# 第九章 Event Loop、宏任务、微任务与渲染

## 一、本章具体知识点

- Call Stack
- Task
- Microtask
- Promise Job
- setTimeout
- MessageChannel
- requestAnimationFrame
- Rendering opportunity
- 浏览器 Event Loop
- Node.js Event Loop
- 微任务饥饿
- 长任务

## 二、各知识点详细解释

### 1. Event Loop 解决什么问题

JavaScript 执行本身是单线程执行模型之一，但浏览器还需要处理网络、用户输入、定时器和渲染。Event Loop 负责在不同任务之间调度 JavaScript 执行机会。

### 2. Call Stack

同步函数调用进入栈；函数返回后出栈。当前栈没有可继续同步执行的代码时，运行环境才可能继续处理下一批任务。

### 3. Task 与 Microtask

典型 Task 来源包括 timer、用户交互等；Promise 回调属于 microtask。microtask 会在适当的 JavaScript 执行边界之后尽快清空。

### 4. 为什么微任务会造成卡顿

如果不断添加 microtask：

```js
queueMicrotask(loop)
```

可能长时间无法让浏览器获得渲染机会，形成 microtask starvation。

### 5. requestAnimationFrame

用于在浏览器准备绘制下一帧前运行视觉更新代码，适合动画和高频 UI 更新。

## 三、本章面试题与答案

### 题 1：Promise.then 和 setTimeout 谁先执行？

**答案：**

在典型浏览器场景中，同一轮同步代码结束后，Promise 回调作为 microtask 会优先于下一轮 timer task，因此常见结果是 Promise.then 先、setTimeout 后。但实际调度仍应结合宿主环境具体任务模型回答，不能把“微任务永远先于宏任务”当成绝对法则。

### 题 2：为什么大量 Promise 会卡页面？

**答案：**

因为大量 microtask 可能在浏览器获得渲染机会前持续执行。如果单次任务或连续微任务执行时间过长，就会挤压渲染与用户输入处理，造成掉帧和 INP 变差。

---
