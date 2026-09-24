# 10 Promise

Promise表达一次最终结果，链式调用把这个结果继续变换。学习时同时画状态变化和回调调度，不把整个Promise都笼统称为异步。含await的例子使用module或async包装。

## 一、本章目录

- [状态、executor 与命运锁定](#k01)
- [每次 then 都产生新 Promise](#k02)
- [catch 与 finally](#k03)
- [resolve、reject 与 thenable](#k04)
- [组合方法：全成、全结、先结、先成](#k05)
- [新辅助 API 与取消边界](#k06)
- [链式推导与手写范围](#k07)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 状态、executor 与命运锁定

状态只有pending、fulfilled、rejected，pending可变为后两者之一，定局后不可逆。settled统称fulfilled/rejected；resolved描述结局已确定或锁定，不是第四种状态。

```js
const order = [];
const promise = new Promise(resolve => {
  order.push('executor');
  resolve(1);
});
promise.then(value => order.push(value));
order.push('sync');
await promise;
console.log(order); // ['executor','sync',1]
```

executor同步执行，then回调通过后续调度执行。resolve另一个pending Promise会锁定跟随它的结局，但当前仍可pending；之后再次resolve/reject不能随意改变这次决定。

executor中同步throw会转成拒绝；它注册的未来timer回调中throw不会自动被当初的构造器捕获。Promise是结果机制，不会把计算移到线程，也不自带强制取消。

<a id="k02"></a>

### 2. 每次 then 都产生新 Promise

then(onFulfilled?,onRejected?)返回新Promise，当前实际执行回调的结果决定新Promise：

| 回调行为 | 下游结果 |
| --- | --- |
| 返回普通值 | fulfilled该值 |
| 返回Promise/thenable | 跟随其解决过程 |
| 抛异常 | rejected该异常 |
| 对应处理器未提供或非函数 | 透传当前值或拒绝原因 |

```js
const value = await Promise.resolve(2)
  .then(x => x * 3)
  .then(() => { throw new Error('bad'); })
  .catch(() => 10);
console.log(value); // 10
```

过程是2→6→拒绝→新Promise恢复10，原先失败的那个Promise没有改回成功。不写return相当于返回undefined；在回调内启动异步操作却不return，下游就不会等待它。

p.then(success,failure)里的failure处理p的拒绝，不处理success自己抛错；p.then(success).catch(failure)能处理前一段成功回调新产生的拒绝。

<a id="k03"></a>

### 3. catch 与 finally

catch(fn)相当于then(undefined,fn)，正常return表示恢复。仅记录日志但不重新抛错，会让下游收到成功的undefined。

finally(fn)主要用于清理，不接收原成功值/失败原因。普通返回值不覆盖原结局；返回Promise会等待其完成，若清理抛错或拒绝，则新失败覆盖原结局。

```js
console.log(await Promise.resolve(1).finally(() => 99)); // 1
try {
  await Promise.reject('original').finally(() => { throw 'cleanup'; });
} catch (error) { console.log(error); } // cleanup
```

因此清理也可能成为根因被遮蔽的来源。应明确清理错误如何记录、是否应盖过业务错误，而不是无条件在finally里return。

<a id="k04"></a>

### 4. resolve、reject 与 thenable

Promise.resolve(value)将普通值转为成功结果，遇thenable会同化，遇同构造器的Promise通常返回其自身。Promise.reject(reason)则直接把reason作为拒绝原因，不等待reason是不是Promise。

```js
const original = Promise.resolve(1);
console.log(Promise.resolve(original) === original); // true
console.log(await Promise.resolve({ then(resolve) { resolve(2); } })); // 2
try { await Promise.reject(original); }
catch (reason) { console.log(reason === original); } // true
```

then属性读取可能抛错，then调用也可能抛错或重复决议。完整实现要处理只接受第一次决定、自解析循环和递归同化。不要把“有then方法”简单等同于经过instanceof Promise。

<a id="k05"></a>

### 5. 组合方法：全成、全结、先结、先成

| 方法 | 成功条件/结果 | 拒绝与空输入 |
| --- | --- | --- |
| all(iterable) | 全部成功，按输入顺序数组 | 首个拒绝使整体拒绝；空输入成功[] |
| allSettled(iterable) | 全部定局，按输入顺序状态记录 | 单项拒绝也收集；空输入成功[] |
| race(iterable) | 第一个定局的值或错误 | 空输入永远pending |
| any(iterable) | 第一个成功值 | 全失败AggregateError；空输入拒绝 |

输入是iterable，不限数组，可包含普通值。组合方法不控制任务何时启动：构造[fetchA(),fetchB()]时两个函数已调用，也不自动取消落败任务。迭代输入本身抛错仍可导致组合拒绝，allSettled不是“任何情况下都不失败”。

Promise.all结果按输入位置而非完成时间排列。all失败后其他请求可能继续写服务器，因此需要独立取消与幂等策略。

<a id="k06"></a>

### 6. 新辅助 API 与取消边界

Promise.withResolvers()返回{promise,resolve,reject}，便于事件适配，但不会保证最终结束、超时或清理。Promise.try(fn,...args)同步调用fn，把返回/抛错转换为Promise结果；Promise.resolve().then(fn)则把调用放到后续reaction。较新API要核对目标支持。

race超时只决定等待结果，不自动停止底层任务；AbortSignal要传到支持的API。fetch获得响应头后可能fulfilled，响应体仍可能读取失败或卡住，不能在头部到达就错误地清掉所有读取超时。

<a id="k07"></a>

### 7. 链式推导与手写范围

读题逐段记录“当前Promise结局→选哪个处理器→回调返回什么→哪个新Promise因此定局→何时有回调入队”。不要只按源码上下顺序猜所有then。

手写all可借原生Promise完成调度和同化，重点验证计数、顺序、空输入和失败。完整Promise还需实现更多规范语义，几十行组合函数不等于Promises/A+或原生Promise全实现。

<a id="summary"></a>

## 三、知识小结

三个状态，一次定局；每段then创建新对象，回调返回决定下游。四种组合按全成/全结/先结/先成记，取消、启动并发和业务重试另行设计。

参考：[MDN Promise](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise)。较新 API 按目标运行时核对支持，示例各自独立。

<a id="interview"></a>

## 四、面试题与答案

<a id="c10-01"></a>

### C10-01 [P0·原理] resolved与fulfilled是否相同？

**回答：** 不相同。fulfilled是成功状态，resolved表示结局已确定或锁定；resolve一个pending Promise后可以继续pending，但已跟随那个Promise。状态仍只有三种。

对应讲解：[状态、executor 与命运锁定](#k01)。

<a id="c10-02"></a>

### C10-02 [P0·原理] then返回的Promise由什么决定？

**回答：** 由实际执行处理器的返回或抛错决定：普通值成功、thenable按解决过程跟随、抛错拒绝，缺省处理器透传。不return异步任务会让下游提前得到undefined。

对应讲解：[每次 then 都产生新 Promise](#k02)。

<a id="c10-03"></a>

### C10-03 [P0·原理] catch与finally怎样改变链？

**回答：** catch正常返回表示恢复，新Promise成功；finally普通返回通常保留原结局，但其异步清理会被等待，抛错或拒绝会覆盖原结局。它们都创建新Promise，不是修改旧Promise状态。

对应讲解：[catch 与 finally](#k03)。

<a id="c10-04"></a>

### C10-04 [P0·基础] all、allSettled、race、any怎么选？

**回答：** 需要全成功用all，独立结果汇总用allSettled，最先定局用race，首个成功用any。再考虑空输入、失败行为和落败任务资源；组合本身不提供自动取消或并发上限。

对应讲解：[组合方法：全成、全结、先结、先成](#k05)。

<a id="c10-05"></a>

### C10-05 [P0·编码] 实现Promise.all的核心行为。

**回答：** 下例用输入索引存结果，remaining中的初始1代表迭代尚未结束，解决空输入；Promise.resolve处理普通值和thenable。构造器捕获迭代期间的同步异常。它不实现构造器子类等完整原生协议，也不取消其他任务。

```js
function allDemo(iterable) {
  return new Promise((resolve, reject) => {
    const output = [];
    let remaining = 1, index = 0;
    for (const item of iterable) {
      const current = index++;
      remaining++;
      Promise.resolve(item).then(value => {
        output[current] = value;
        if (--remaining === 0) resolve(output);
      }, reject);
    }
    if (--remaining === 0) resolve(output);
  });
}
console.log(await allDemo([Promise.resolve(1), 2])); // [1,2]
console.log(await allDemo([])); // []
```

**追问与回答：** 测试乱序完成、空输入、同步值、thenable、拒绝及抛错迭代器。

对应讲解：[链式推导与手写范围](#k07)。

<a id="c10-06"></a>

### C10-06 [P1·原理] Promise.try(fn)与resolve().then(fn)的调用时机相同吗？

**回答：** 不同。try同步调用fn并把返回或异常包装为Promise，then版本把fn安排为后续处理器。二者都可统一结果接口，但有副作用时调用时机可观察，不能无条件互换。

对应讲解：[新辅助 API 与取消边界](#k06)。
