# 11 async与await

async/await是组织Promise控制流的语法。重点是明确任务何时启动、函数何时暂停、拒绝回到哪一层，而不是把async当作后台线程标记。

## 一、本章目录

- [返回值与同步前半段](#k01)
- [顺序依赖与并发启动](#k02)
- [return await 与 try/finally](#k03)
- [不要使用 async executor](#k04)
- [取消与CPU计算](#k05)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 返回值与同步前半段

```js
const events = [];
async function work() {
  events.push('start');
  await 0;
  events.push('resume');
  return 7;
}
const result = work();
events.push('caller');
console.log(await result, events); // 7 ['start','caller','resume']
```

async调用总返回Promise，普通return值成为成功结果，throw成为拒绝。函数先同步执行到实际经过的第一个await。await右侧表达式也先求值，所以load()里面的重计算仍先占当前线程。

await普通值也会让后续恢复经过异步调度。async函数返回一个Promise时会采用其结果，但返回对象不必与那个Promise是同一个引用，不能把结果同化误认为引用恒等。

<a id="k02"></a>

### 2. 顺序依赖与并发启动

const user=await loadUser(); await loadOrders(user.id)有数据依赖，应顺序执行。独立请求可同时调用并用Promise.all或allSettled接管。是否并发看启动点，而不只是看await数量。

```js
const tasks = [() => Promise.resolve(1), () => Promise.resolve(2)];
const results = await Promise.all(tasks.map(task => task()));
console.log(results); // [1,2]
```

forEach忽略async回调结果；map得到Promise数组并不会自动等待。先启动多个Promise再逐个await时，后面的任务可能提前拒绝而尚未接处理器，应用应及时接管全部错误。大量任务需要并发池，不应把所有网络请求一次发出。

<a id="k03"></a>

### 3. return await 与 try/finally

```js
async function catchHere() {
  try { return await Promise.reject(new Error('failed')); }
  catch (error) { return error.message; }
}
console.log(await catchHere()); // failed
```

return promise直接把最终结局交给返回的Promise，当前try中的catch不一定接住未来拒绝；return await让拒绝在此处恢复为可捕获异常。

finally也受等待位置影响：try{return work()}finally{cleanup()}可能在work的Promise完成前就清理；return await work()会等到结果或失败后再清理。不能机械为“性能”删掉所有return await，也不要在finally里return掩盖原结果。

<a id="k04"></a>

### 4. 不要使用 async executor

new Promise的executor返回值会被忽略。写new Promise(async resolve=>{...})时，async函数自己产生的Promise不会自动成为外层Promise的结局；内部失败可能成为未处理拒绝，外层却一直pending。

已有Promise异步流程直接用async函数即可。只有适配回调/事件等来源时才考虑new Promise，并确保成功、失败、超时、取消和清理路径能让等待有明确结局。

<a id="k05"></a>

### 5. 取消与CPU计算

await只暂停本函数后续逻辑，不强制停止等待的任务。AbortController发出协作信号，底层API或自定义task必须响应；不响应的计算可能照常完成。提交结果还需请求版本检查。

await Promise.resolve()通常只让出到微任务，不保证浏览器绘制。重计算要分片到后续任务或放Worker；rAF只是安排视觉更新时机，回调中的重计算仍阻塞主线程。

<a id="summary"></a>

## 三、知识小结

先求值并启动，再await等待，最后恢复函数。同步计算不因async消失；错误与清理跟await位置走；取消需要操作本身支持。

参考：[MDN async function](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/async_function)。较新 API 按目标运行时核对支持，示例各自独立。

<a id="interview"></a>

## 四、面试题与答案

<a id="c11-01"></a>

### C11-01 [P0·原理] async函数调用后立刻异步执行吗？

**回答：** 不会，调用先同步运行到第一个实际await，右侧表达式也先求值，后续才挂起并在结果就绪后恢复。async保证返回Promise，不保证其中计算并行。

对应讲解：[返回值与同步前半段](#k01)。

<a id="c11-02"></a>

### C11-02 [P0·原理] await会阻塞整个线程吗？

**回答：** 等待时暂停当前async函数，调用者和其他可调度代码能继续；但await前后执行的同步重计算仍占用线程。循环await已兑现Promise也不保证渲染机会。

对应讲解：[取消与CPU计算](#k05)。

<a id="c11-03"></a>

### C11-03 [P0·原理] return await是否总是多余？

**回答：** 不是。需要当前catch接管拒绝、或在异步操作完成后才finally清理时，它有控制流意义。没有本地处理需求时可直接return Promise，但不能机械套规则。

对应讲解：[return await 与 try/finally](#k03)。

<a id="c11-04"></a>

### C11-04 [P1·原理] new Promise(async resolve=>...)为什么危险？

**回答：** 外层构造器忽略executor返回值，async executor产生的Promise与外层结局没有自动连接。未调用resolve/reject时外层可能悬挂，内部拒绝却未处理。直接async或明确的回调适配更可靠。

对应讲解：[不要使用 async executor](#k04)。

<a id="c11-05"></a>

### C11-05 [P1·工程取舍] 如何选择串行、Promise.all和并发池？

**回答：** 先看数据依赖；依赖前一步结果就串行。少量独立任务可并发汇总，数量大或资源受限时用并发池控制启动。失败汇总和取消又是独立策略，不只比较语法简洁。

对应讲解：[顺序依赖与并发启动](#k02)。
