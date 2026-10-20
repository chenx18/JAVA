# Web Worker

<!-- core-mindmap:start -->
[![Web Worker 知识导图](examples/c27-mindmap.png)](examples/c27-mindmap.svg)

<details>
<summary>PlantUML 源码</summary>

```plantuml
@startmindmap
skinparam defaultFontName Microsoft YaHei
skinparam defaultFontSize 14
skinparam dpi 100
skinparam backgroundColor white
skinparam shadowing false

* Web Worker
**[#EDE7F6] 用途与运行环境
*** 独立计算与 async 的区别
**** 同步重计算不会因 async 自动离开主线程
**** 优先改善响应性，不保证总耗时更短
*** DOM 与变量访问边界
**** 独立全局环境，不能直接读页面局部变量或 DOM
**** 浏览器专用 Worker 与 Node 接口分开判断
**[#E3F2FD] 创建、通信与数据
*** 创建和结束 Worker
**** 模块 Worker 检查 URL、同源、CSP 和构建产物
**** 页面 terminate，内部 close\n任务收尾仍需协议
*** 消息协议与 Promise 结算
**** postMessage 不直接返回计算结果
**** taskId 匹配响应\n成功、失败、超时、终止都要结算
*** 结构化克隆、转移与共享
**** ArrayBuffer 转移后发送方缓冲分离
**** 资源需出现在消息中\ntransfer 不等于共享内存
left side
**[#E0F2F1] 取消与生命周期
*** 取消消息与长同步计算
**** 长同步循环阻塞取消消息处理
**** 分块让出执行机会或终止环境\n信号不会自动中止计算
*** 旧结果与终止后的任务收尾
**** 版本保护结果归属
**** 终止或错误时结算 Promise\n清理任务记录与监听
**[#FFF3E0] 性能选择与注意
*** 收益与启动通信成本
**** 同时测响应性、总耗时、内存和传输成本
**** DOM 与布局瓶颈仍在页面解决

@endmindmap
```

</details>

<!-- core-mindmap:end -->

**口述总览：** Web Worker 让计算在独立的 JavaScript 执行环境中运行，页面通过消息与它通信。它适合可以脱离 DOM 的较重任务；启动、传输和结果处理也有成本，所以要结合实际卡顿原因决定是否使用。

**记忆线：** 计算归属 → 创建与通信 → 复制或转移 → 取消与清理 → 测量收益。

本篇归纳 8 个知识点：[知识点归纳与自查](#self-check) · [试题与答案](#interview)。先尝试回答，再用正文例子核对；文末完整列出每点回答与记忆提示，也可直接顺序阅读。

## 基本用途：让页面线程有机会响应交互

页面线程既执行 JavaScript，也参与页面更新。如果长时间连续计算，点击和绘制就可能被推迟。

Worker 可以承担解析、统计、搜索等计算，页面线程负责展示结果。它有自己的全局环境，不能直接读取主页面局部变量，也不能直接操作页面 DOM。

~~~text
页面：接收交互 → 发送任务 ─────────→ 收到结果 → 更新 DOM
Worker：                收到消息 → 执行计算 → 发送结果
~~~

把函数加上 async 不会自动把 CPU 计算移到 Worker；await 主要改变等待和恢复方式。使用 Worker 也不会保证总耗时变短，主要目标可能是降低主线程阻塞。

下面讨论浏览器中的专用 Worker，需要通过 HTTP/HTTPS 页面运行；Node.js 使用的是另一套 worker_threads API。

## 创建与结束：new Worker()、terminate()

- new Worker(url, { type: 'module' })：从模块脚本创建 Worker，可在 Worker 文件中使用 import。
- worker.terminate()：从页面立即终止 Worker。
- Worker 内部可以调用 self.close() 结束自身的后续工作。
- Worker 文件地址需要满足同源、CSP 等浏览器加载规则；不要把本地 file 地址当作标准部署方式。

页面脚本是 ES 模块时，常用 new URL('./worker.js', import.meta.url) 表达文件位置。构建工具是否识别并生成独立产物，应查看实际构建结果。

## 发送与接收：postMessage()、message、error

**口述：** postMessage 发送数据，message 监听器接收数据。消息采用异步通信，不会把对方函数的返回值直接交给调用者。

下面是一个完整的两文件小例子：计算数组之和，结果返回后释放 Worker。

**文件 sum-worker.js：**

<!-- example: c23-worker-source -->
~~~js
self.addEventListener('message', event => {
  const values = event.data;
  if (!Array.isArray(values) ||
      !values.every(value => typeof value === 'number' &&
        Number.isFinite(value))) {
    self.postMessage({ ok: false, message: '需要有限数值数组' });
    return;
  }

  const sum = values.reduce((total, value) => total + value, 0);
  self.postMessage({ ok: true, sum });
});
~~~

**文件 main.js，以模块脚本加载：**

<!-- example: c23-worker-main -->
~~~js
const worker = new Worker(new URL('./sum-worker.js', import.meta.url), {
  type: 'module'
});

worker.addEventListener('message', event => {
  if (event.data.ok) {
    console.log(event.data.sum); // 6
  } else {
    console.error(event.data.message);
  }
  worker.terminate();
}, { once: true });

worker.addEventListener('error', event => {
  console.error(event.message);
  worker.terminate();
}, { once: true });

worker.postMessage([1, 2, 3]);
~~~

页面用 script type="module" 引入 main.js 即可。这个小数组只用于说明通信，不代表它适合为了性能专门创建 Worker。

**注意：** Worker 的 postMessage 返回 undefined。希望调用方 await 结果，需要自己用 Promise 包装，并处理成功、错误、超时与终止；不要留下永远无法结算的 Promise。

## 复制与转移：结构化克隆、transfer

普通消息通常通过结构化克隆传递。接收方获得自己的数据结构；函数、DOM 节点等不能直接作为普通消息复制。

ArrayBuffer 等可转移资源可以交出所有权，减少复制大块数据的成本。转移后，发送方原来的 ArrayBuffer 会被分离，不能继续按原数据使用。

下面用 structuredClone 演示同一类转移规则，在支持该 API 的环境中可独立运行：

<!-- example: c23-transfer -->
~~~js
const buffer = new ArrayBuffer(4);
new Uint8Array(buffer)[0] = 7;

const received = structuredClone(buffer, { transfer: [buffer] });
console.log(buffer.byteLength); // 0
console.log(received.byteLength); // 4
console.log(new Uint8Array(received)[0]); // 7
~~~

Worker 中的相应写法是 worker.postMessage({ buffer }, [buffer])。要让接收方使用资源，资源也应出现在消息中，不能只列入 transfer 数组。

**注意：** 转移不等于两边共享同一块可读写缓冲。SharedArrayBuffer 才涉及共享内存，它还有部署隔离和同步要求；多数业务先使用消息复制或转移即可。

## 取消与资源清理：任务终止也要处理结果归属

**口述：** 取消可以通过终止整个 Worker，或设计协作取消协议实现。页面还要忽略已经过时的消息，并让等待中的任务得到明确结局。

- 页面结束时调用 terminate，避免计算继续占用资源。
- 同一个 Worker 处理多任务时，消息带 taskId，把结果交给对应的调用方。
- 新任务取代旧任务时，检查 taskId 或版本，防止旧结果覆盖界面。
- Worker 出错或终止时，主动拒绝尚未完成的 Promise，并移除相应记录。
- 长计算若需要在 Worker 内响应“取消消息”，必须分块让出执行机会。连续同步循环没有结束时，消息监听器也无法及时执行。

只给计算加一个 AbortSignal 不会自动让 Worker 停止。要把取消行为传入自己的任务协议，或者在确认可丢弃整个执行环境时终止 Worker。

## 注意：先确认卡顿来自哪里

Worker 适合较重、可独立执行的计算。如果问题来自频繁 DOM 读写、布局或过量组件更新，应先处理对应工作。

比较方案时使用同一份数据，分别观察主线程长任务、交互延迟、总完成时间和内存；把 Worker 启动、数据传输和结果合并都算进去。一次任务可用单独 Worker，多次任务可以复用，但要管理队列与生命周期。

<a id="self-check"></a>

## 知识点归纳与自查

本节按 8 个知识点回忆和归纳。先用自查问题检查理解，再顺序阅读各点口述与记忆提示；分级试题在后面的独立章节。

### 自查问题

1. [async 能替代 Worker 执行重计算吗？Worker 主要改善什么？](#recall-01)
2. [Worker 能直接操作页面 DOM、读取页面局部变量吗？浏览器和 Node 的接口相同吗？](#recall-02)
3. [模块 Worker 怎样创建与定位脚本？terminate 和内部 close 分别用在什么位置？](#recall-03)
4. [postMessage 会返回计算结果吗？多个任务怎样匹配响应并处理失败？](#recall-04)
5. [普通消息复制、transfer 和共享内存有什么区别？转移后还能使用原缓冲吗？](#recall-05)
6. [已经发了取消消息，Worker 为什么仍可能继续计算？直接传 AbortSignal 能自动停止吗？](#recall-06)
7. [新任务替代旧任务、页面关闭或 Worker 出错时，除了 terminate 还要做什么？](#recall-07)
8. [Worker 一定提升性能吗？应该测量哪些成本，哪些卡顿不适合交给它？](#recall-08)

<a id="recall-01"></a>

### 独立计算与 async 的区别

**自查：** async 能替代 Worker 执行重计算吗？Worker 主要改善什么？

**口述：** 不能。async 函数的同步计算仍在当前线程运行，await 不会自动把计算移到另一线程。Worker 提供独立执行环境，适合解析、统计等可脱离 DOM 的计算，主要收益可能是减少页面主线程阻塞，而不是缩短任务总时间。

**记忆：** async 改变等待方式，Worker 改变合适计算的执行环境。

<a id="recall-02"></a>

### DOM 与变量访问边界

**自查：** Worker 能直接操作页面 DOM、读取页面局部变量吗？浏览器和 Node 的接口相同吗？

**口述：** 浏览器 Worker 不能直接操作页面 DOM，也不能自动读取页面函数的局部变量。它通过消息传回结果，由页面线程更新界面；需要的数据必须通过消息或明确的共享机制传递。本篇讨论浏览器专用 Worker，Node 的 worker_threads 是另一套接口。

**记忆：** Worker 负责独立计算，页面负责 DOM，通过协议交换数据。

<a id="recall-03"></a>

### 创建和结束 Worker

**自查：** 模块 Worker 怎样创建与定位脚本？terminate 和内部 close 分别用在什么位置？

**口述：** 页面可用 `new Worker(url, { type: 'module' })` 创建模块 Worker，ES 模块内常结合 new URL 和 import.meta.url 定位脚本。加载要满足同源、CSP 及部署条件，构建后还需确认独立产物。页面用 terminate 终止 Worker，Worker 内部可用 self.close 结束后续工作；结束环境不等于自动完成调用方的任务协议。

**记忆：** 创建检查脚本和部署，结束还要收口等待中的任务。

<a id="recall-04"></a>

### 消息协议与 Promise 结算

**自查：** postMessage 会返回计算结果吗？多个任务怎样匹配响应并处理失败？

**口述：** postMessage 发送异步消息，返回 undefined，不直接返回对方计算结果。希望 await 结果，需要按 taskId 建立等待记录，并处理成功、业务失败、脚本错误、超时和终止。消息格式及输入输出应校验，不能让失败或关闭后的任务 Promise 永远悬空。

**记忆：** 消息负责传输，taskId 配对结果，协议负责让等待结束。

<a id="recall-05"></a>

### 结构化克隆、转移与共享

**自查：** 普通消息复制、transfer 和共享内存有什么区别？转移后还能使用原缓冲吗？

**口述：** 普通可克隆数据通常在接收方形成副本，函数和 DOM 节点等不能这样发送。transfer 转移可转移资源的所有权，例如 ArrayBuffer 转移后发送方缓冲会分离；资源还应出现在消息内容中，不能只写进转移列表。它不同于共享内存，SharedArrayBuffer 另有部署隔离与同步要求。

**记忆：** 复制得到副本，转移交出所有权，共享需要额外约束。

<a id="recall-06"></a>

### 取消消息与长同步计算

**自查：** 已经发了取消消息，Worker 为什么仍可能继续计算？直接传 AbortSignal 能自动停止吗？

**口述：** Worker 的长同步循环没有结束时，也无法及时处理新消息。协作取消需要把计算拆分并让出接收消息的机会，再按任务协议检查取消；或者终止整个 Worker。只用 `await Promise.resolve()` 让出到微任务，不足以保证 message 任务得到执行。仅仅给业务函数加一个 AbortSignal 不会自动停止计算，需要显式把取消接到协议和执行流程中。

**记忆：** 取消消息也要等执行机会，协作取消必须在计算中配合。

<a id="recall-07"></a>

### 旧结果与终止后的任务收尾

**自查：** 新任务替代旧任务、页面关闭或 Worker 出错时，除了 terminate 还要做什么？

**口述：** 新任务取代旧任务时，要按 taskId 或版本判断每条结果是否仍有效，避免旧消息覆盖页面。Worker 出错、超时或终止时，要让等待中的任务得到明确结局，删除任务记录和多余监听。可复用 Worker 还需管理任务队列，不能把终止整个执行环境与只取消一个任务混为一谈。

**记忆：** 停止计算、过滤旧结果、结算等待任务，三件事分别完成。

<a id="recall-08"></a>

### 收益与启动通信成本

**自查：** Worker 一定提升性能吗？应该测量哪些成本，哪些卡顿不适合交给它？

**口述：** 不一定。Worker 可以减少计算占用页面线程的时间，但创建环境、数据复制传输和结果合并都有成本。若瓶颈是 DOM 读写、布局或组件更新，仍需优化页面工作。比较时使用相同数据，测量交互延迟、主线程长任务、总耗时和内存，再决定新建还是复用 Worker。

**记忆：** 先确认瓶颈，再把启动、通信和结果处理一起算成本。

<a id="interview"></a>

## 试题与答案

<a id="c27-01"></a>

### C27-01 [P0·原理] async 函数能替代 Worker 执行重计算吗？

**回答：** 不能。async 函数的同步计算仍在当前线程运行，await 不会自动把计算移到另一线程。Worker 提供独立执行环境，适合解析、统计等可脱离 DOM 的计算，主要收益可能是减少页面主线程阻塞，而不是缩短任务总时间。

<a id="c27-02"></a>

### C27-02 [P0·基础] Worker 可以直接修改 DOM 吗？

**回答：** 浏览器 Worker 不能直接操作页面 DOM，也不能自动读取页面函数的局部变量。它通过消息传回结果，由页面线程更新界面；需要的数据必须通过消息或明确的共享机制传递。本篇讨论浏览器专用 Worker，Node 的 worker_threads 是另一套接口。

<a id="c27-03"></a>

### C27-03 [P1·原理] transfer 和普通消息复制有什么区别？

**回答：** 普通可克隆数据通常在接收方形成副本，函数和 DOM 节点等不能这样发送。transfer 转移可转移资源的所有权，例如 ArrayBuffer 转移后发送方缓冲会分离；资源还应出现在消息内容中，不能只写进转移列表。它不同于共享内存，SharedArrayBuffer 另有部署隔离与同步要求。

<a id="c27-04"></a>

### C27-04 [P1·工程取舍] 为什么发送取消消息后 Worker 仍可能继续计算？

**回答：** Worker 的长同步循环没有结束时，也无法及时处理新消息。协作取消需要把计算拆分并让出接收消息的机会，再按任务协议检查取消；或者终止整个 Worker。只用 `await Promise.resolve()` 让出到微任务，不足以保证 message 任务得到执行。仅仅给业务函数加一个 AbortSignal 不会自动停止计算，需要显式把取消接到协议和执行流程中。

<a id="c27-05"></a>

### C27-05 [P1·工程取舍] Worker一定提升性能吗？

**回答：** 不一定。Worker 可以减少计算占用页面线程的时间，但创建环境、数据复制传输和结果合并都有成本。若瓶颈是 DOM 读写、布局或组件更新，仍需优化页面工作。比较时使用相同数据，测量交互延迟、主线程长任务、总耗时和内存，再决定新建还是复用 Worker。
