# Fetch、请求取消与竞态

<!-- core-mindmap:start -->
[![Fetch、请求取消与竞态 知识导图](examples/c23-mindmap.png)](examples/c23-mindmap.svg)

<details>
<summary>PlantUML 源码</summary>

```plantuml
@startmindmap
skinparam defaultFontName Microsoft YaHei
skinparam defaultFontSize 14
skinparam dpi 100
skinparam backgroundColor white
skinparam shadowing false

* Fetch、请求取消与竞态
**[#EDE7F6] 请求过程
*** 发出请求 → 响应头 → 读取响应体
*** JSON解析 → 字段校验 → 提交界面
*** URL / URLSearchParams 构造参数
**[#E3F2FD] 调用与控制
*** fetch：method / headers / body / signal
*** Response：ok / status / json / text
*** AbortController：取消不再需要的等待
*** 版本检查：只让当前请求提交
**[#E0F2F1] 注意
*** HTTP 404/500 通常不会自动reject
*** body通常只能读取一次
*** 旧catch和finally也要判断归属
*** abort不保证撤销服务端修改

@endmindmap
```

</details>

<!-- core-mindmap:end -->

**口述总览：** Fetch 用于发起请求并取得响应。拿到响应不等于读完数据，HTTP 成功也不等于业务成功。取消用于停止不再需要的等待，版本检查用于保证只有当前请求能更新界面，两者需要配合。

## 基本用途：把请求分成几个阶段

一次请求可以按这个顺序理解：

~~~text
发出请求 → 收到响应头 → 读取响应体 → 解析数据 → 校验结构 → 提交界面
~~~

fetch 返回的 Promise 通常在响应头可用时完成，此时得到 Response。response.json() 等方法继续读取响应体并解析，因此还可能失败。

下面的请求示例用于浏览器页面；独立的 URL、Response 和任务控制例子也可以在支持相关 API 的现代 Node.js 中运行。示例地址只是接口占位，需要替换成自己的服务。

## 构造请求地址：URL、URLSearchParams

- new URL(path, base)：组合地址，返回 URL 对象。
- url.searchParams.set(name, value)：设置参数，已有同名参数会被替换。
- append(name, value)：追加参数，允许重复键。
- get(name) / getAll(name)：读取一个值或全部同名值。
- 参数值由 API 编码，避免手动拼接时遗漏特殊字符。

<!-- example: c22-url -->
~~~js
const url = new URL('/api/search', 'https://example.test');
url.searchParams.set('q', 'a&b');
url.searchParams.append('tag', 'js');
url.searchParams.append('tag', 'vue');

console.log(url.search); // ?q=a%26b&tag=js&tag=vue
console.log(url.searchParams.get('q')); // a&b
console.log(JSON.stringify(url.searchParams.getAll('tag'))); // ["js","vue"]
~~~

**注意：** URL 编码负责表达参数，不能代替服务端校验。敏感信息也不应因为经过编码就被放进公开 URL。

## 发起请求与读取结果：fetch()、Response

fetch(url, options) 返回 Promise<Response>。常用选项有 method、headers、body、signal 和 credentials。

- response.status：HTTP 状态码。
- response.ok：状态是否在 200～299。
- response.headers.get(name)：读取允许访问的响应头。
- response.json() / text() / blob() / arrayBuffer()：按需要读取响应体，返回 Promise。
- body 通常只能消费一次，不要先 text() 再 json() 读取同一份响应体。

<!-- example: c22-response -->
~~~js
const response = new Response('{"name":"Alice"}', {
  status: 200,
  headers: { 'Content-Type': 'application/json' }
});
console.log(response.ok, response.status); // true 200
console.log((await response.json()).name); // Alice
console.log(response.bodyUsed); // true

try {
  await response.text();
} catch (error) {
  console.log(error.name); // TypeError
}
~~~

**注意：** HTTP 404/500 通常不会让 fetch 自动拒绝；网络错误、取消等才可能使请求 Promise 拒绝。mode: 'no-cors' 也不是读取跨域响应的解决办法，它会得到受限的 opaque 响应。

## 封装 JSON 请求：检查 HTTP，再解析与校验

先做一个明确支持范围的小函数：读取有 JSON 响应体的接口。它处理 HTTP 状态和解析，具体业务字段由调用方校验。

<!-- example: c22-fetch-json -->
~~~js
async function fetchJson(url, options = {}) {
  const response = await fetch(url, options);
  if (!response.ok) {
    const error = new Error('HTTP ' + response.status);
    error.status = response.status;
    throw error;
  }
  return response.json();
}

async function saveUser(user, signal) {
  const data = await fetchJson('/api/users', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(user),
    signal
  });
  if (data === null || typeof data !== 'object' ||
      typeof data.id !== 'string') {
    throw new Error('响应缺少字符串 id');
  }
  return data;
}
~~~

这个例子有三层检查：

- response.ok 回答 HTTP 是否成功。
- json() 回答响应体能否解析为 JSON。
- id 检查回答数据是否符合本次业务约定。

**注意：** 204 或其他空响应不应该盲目调用 json()。跨域携带 Cookie 常需 credentials: 'include' 和服务端正确配置，还受到 Cookie 策略限制；前端配置不能替代后端鉴权。

## 取消和超时：AbortController、AbortSignal

**口述：** AbortController 发出取消信号，支持该信号的操作决定如何响应。它可以中断 Fetch 的等待和响应体读取，但不能保证撤销服务端已经完成的修改。

<!-- example: c22-abort -->
~~~js
const controller = new AbortController();
const reasons = [];
controller.signal.addEventListener('abort', () => {
  reasons.push(controller.signal.reason);
}, { once: true });

controller.abort('用户取消');
controller.abort('再次取消');
console.log(controller.signal.aborted); // true
console.log(reasons.join(',')); // 用户取消
~~~

请求时把 signal 传给 fetch；用户离开页面或新请求取代旧请求时调用 abort。已经取消的 signal 不能恢复，新一轮任务要创建新的 Controller。

支持时，可以用 AbortSignal.timeout(ms) 产生超时信号，用 AbortSignal.any([...]) 合并用户取消与超时信号。旧环境可使用定时器和 Controller，并在整个读取流程结束后清除计时器。

**注意：** 仅用 Promise.race 等到超时，并不会自动停止另一个请求。处理取消时要结合本次 signal 和取消原因；不要把所有异常都当作“用户取消”吞掉。

## 只允许最新请求提交：版本检查配合取消

用户先搜索 A，再搜索 B。如果 B 先返回、A 后返回，直接赋值就会让旧结果覆盖新结果，这叫请求竞态。

~~~text
发起 A：记录版本 1
发起 B：当前版本改为 2，尝试取消 A
B 完成：版本 2 仍有效，提交
A 完成：版本 1 已过时，忽略
~~~

取消节省资源；即使某段操作不响应取消，版本检查仍能拦住旧结果。

<!-- example: c22-latest -->
~~~js
function createLatestRunner() {
  let version = 0;
  let controller = null;

  return {
    async run(task, commit) {
      const current = ++version;
      const previous = controller;
      const own = new AbortController();
      controller = own;
      previous?.abort();
      try {
        if (current !== version || own.signal.aborted) return 'stale';
        const value = await task(own.signal);
        if (current !== version || own.signal.aborted) return 'stale';
        commit(value); // 本例约定同步提交
        return 'committed';
      } catch (error) {
        if (current !== version || own.signal.aborted) return 'stale';
        throw error;
      } finally {
        if (controller === own) controller = null;
      }
    },
    cancel() {
      version++;
      const previous = controller;
      controller = null;
      previous?.abort();
    }
  };
}

const runner = createLatestRunner();
const shown = [];
let finishA;
const first = runner.run(
  () => new Promise(resolve => { finishA = resolve; }),
  value => shown.push(value)
);
const second = runner.run(
  async () => 'B',
  value => shown.push(value)
);

console.log(await second); // committed
finishA('A');
console.log(await first); // stale
console.log(shown.join(',')); // B
~~~

这里故意让 A 忽略 signal，验证版本检查仍然生效。先安装新 Controller，再取消旧请求，因为 abort 会同步触发监听，监听中也可能再次发起请求。启动任务前和提交时都检查版本；finally 只清理自己拥有的 Controller，避免旧请求结束时破坏新请求的状态。

在页面中，task 可调用 fetchJson(url, { signal })，commit 更新结果；卸载时调用 runner.cancel()。多块独立界面各自创建 runner，避免相互取消。

**注意：** loading、错误提示和流式增量也要检查归属。这个控制器处理“只要最新结果”的场景；支付、保存等必须处理每次结果的操作，需要另外定义协议和服务端幂等规则。

## 注意：把失败定位到具体阶段

排查时先分清请求没发出、HTTP 失败、响应体读取失败、JSON 解析失败、字段不合法，还是旧结果误提交。可以用请求标识关联日志，但应避免记录敏感请求体。

大批量请求的并发限制与重试见[异步控制](05_异步控制与任务调度.md)；持续接收文本或 AI 输出见[流式响应与 SSE](../06_选读扩展/03_流式响应与SSE.md)。

<a id="interview"></a>

## 试题与答案

<a id="c23-01"></a>

### C23-01 [P0·原理] 为什么 fetch 遇到 404 不一定进入 catch？

**回答：** Fetch 得到了一个正常的 HTTP 响应，状态码是 404。要检查 response.ok 或 status 并主动抛错；网络故障和取消等才可能让请求 Promise 直接拒绝。

<a id="c23-02"></a>

### C23-02 [P0·基础] response.json() 已经成功，能直接使用数据吗？

**回答：** JSON 解析只保证语法合法，结果可能是 null、数组或字段类型错误。业务代码还要检查需要的字段、范围和状态；HTTP、解析和业务校验是三个阶段。

<a id="c23-03"></a>

### C23-03 [P0·原理] 为什么取消旧请求后还要检查版本？

**回答：** 旧操作可能已经完成、正在后续处理，或根本不响应 signal。版本检查在提交时再次确认归属，保护结果、loading 和错误状态；取消主要用于减少无用工作。

<a id="c23-04"></a>

### C23-04 [P1·编码] 怎样验证只提交最新结果的实现？

**回答：** 用可控 Promise 按 B 先完成、A 后完成的顺序触发，确认只显示 B；还应测试取消后完成、旧请求失败、当前请求失败，以及旧 finally 不影响新请求。无需依赖真实网络延迟碰运气。

<a id="c23-05"></a>

### C23-05 [P1·工程取舍] abort 能保证服务端操作没有执行吗？

**回答：** 不能。客户端停止等待时，服务端可能已经提交修改。涉及写入和重试时，要靠服务端幂等键、状态查询或业务补偿明确结果，不能根据前端取消直接判定“没有执行”。

<a id="c23-06"></a>

### C23-06 [P0·原理] fetch返回200之后为什么还能失败？

**回答：** 响应头成功后，响应体仍可能读取失败、被取消或不是合法 JSON，解析后的数据也可能不满足业务要求。
