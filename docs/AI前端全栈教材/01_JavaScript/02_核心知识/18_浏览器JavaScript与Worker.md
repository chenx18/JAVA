# 18 浏览器JavaScript与Worker

DOM、Fetch、Worker等由浏览器宿主提供，不属于所有JavaScript运行环境必有的语言能力。本章把事件、请求和线程边界连到实际前端开发，浏览器专用示例需在页面环境运行。

## 一、本章目录

- [事件传播、target 与委托](#k01)
- [默认行为、监听选项与清理](#k02)
- [DOM 状态与页面加载](#k03)
- [Fetch、URL 与存储](#k04)
- [Worker 的任务与通信](#k05)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 事件传播、target 与委托

一般可冒泡事件沿捕获、目标和冒泡阶段传播。target表示事件目标，currentTarget表示当前正在执行监听的对象；Shadow DOM可能重定向target，需要时看composedPath及事件composed规则。

点击按钮内部图标时，target可能是图标而非button。委托把监听放共同祖先，通过closest找业务元素，再验证属于当前容器。

```js
// browser-only
function mountList(container, onSelect) {
  const controller = new AbortController();
  container.addEventListener('click', event => {
    if (!(event.target instanceof Element)) return;
    const button = event.target.closest('button[data-id]');
    if (!button || !container.contains(button)) return;
    onSelect(button.dataset.id);
  }, { signal: controller.signal });
  return () => controller.abort();
}
```

委托能复用动态后代监听，但并非所有事件都冒泡。focus/blur可考虑捕获或focusin/focusout等方案；复杂嵌套容器还需归属判断，不能把所有交互都无条件委托到document。

<a id="k02"></a>

### 2. 默认行为、监听选项与清理

| 方法/选项 | 作用 |
| --- | --- |
| preventDefault() | 取消可取消事件的默认动作 |
| stopPropagation() | 阻止后续传播，不自动阻止同节点其他监听 |
| stopImmediatePropagation() | 还阻止当前节点后续监听 |
| capture | 选择捕获阶段监听 |
| once | 成功触发后移除一次性监听 |
| passive | 声明不阻止默认动作，监听中preventDefault不能成功取消 |
| signal | 随AbortSignal移除监听 |

阻止冒泡不等于阻止链接跳转。移除监听需同type、同listener引用和匹配capture；不要求传回完全同一个options对象。once也不保证从未触发的监听在业务结束时自动消失。

组件卸载统一清理事件、Observer、timer和连接，避免它们继续持有页面状态。

<a id="k03"></a>

### 3. DOM 状态与页面加载

HTML attribute表示标记属性，DOM property表示运行时对象状态，某些属性会反射但不是所有都永远一致。文本input的value属性通常关联初始/默认值，input.value反映当前输入；checked/defaultChecked等也要区分默认与当前状态。

DOMContentLoaded在文档解析和相关脚本执行条件满足后触发，不等所有图片或后续异步请求；window load等待相关资源加载完成条件。async/defer/module的下载和执行规则不同，不能只背“哪个事件先”而忽略脚本类型。

插入纯文本使用textContent。innerHTML会按HTML解析，外部富文本应经过可信清洗并结合安全策略。DOM操作是否触发布局还与读写顺序有关，布局和完整安全专题在浏览器系列深入。

<a id="k04"></a>

### 4. Fetch、URL 与存储

fetch常在响应头到达后fulfilled，HTTP4xx/5xx通常不会因此自动reject。之后response.json/text或流读取仍可能失败；body一般只能消费一次，clone会增加双消费者与缓冲成本。

```js
async function fetchJson(url, signal) {
  const response = await fetch(url, { signal });
  if (!response.ok) throw new Error('HTTP ' + response.status);
  const data = await response.json();
  return data; // 调用方继续按业务schema校验。
}
```

URL/URLSearchParams负责相对地址、编码和重复参数，不要用split('?')替代全部规则。localStorage/sessionStorage是同步字符串存储，IndexedDB提供异步结构化存储，Cookie可按规则随请求发送；存储选型需考虑容量、主线程成本和认证策略。

XSS、CSRF、CORS各解决不同问题。前端校验、跨域允许或存储方式都不能替代后端授权，长期服务密钥不应放进公开前端代码。

<a id="k05"></a>

### 5. Worker 的任务与通信

Worker可以独立执行计算和解析，不能直接操作页面DOM。通过postMessage/onmessage通信，数据常按结构化克隆传递；transfer可转移资源，错误、任务ID、取消和超时也需协议。

浏览器两文件示意：

```js
// browser-only: worker.js
self.onmessage = ({ data: { id, values } }) => {
  self.postMessage({ id, sum: values.reduce((a, b) => a + b, 0) });
};
```

```js
// browser-only: main.js (module)
const worker = new Worker(new URL('./worker.js', import.meta.url), { type: 'module' });
worker.onmessage = ({ data }) => console.log(data.id, data.sum);
worker.postMessage({ id: 1, values: [1, 2, 3] });
// 所有者结束时调用worker.terminate()并处理未完成任务。
```

启动、克隆、传输和合并结果都有成本。小任务搬Worker可能更慢；先量长任务、输入延迟和内存，再决定主线程分片或Worker。rAF适合合并视觉更新，不能将DOM渲染本身搬到普通Worker。

<a id="summary"></a>

## 三、知识小结

浏览器题按事件路径、请求阶段和资源所有权解释。语言与宿主API分开；性能优化需要设备与数据量一致的证据，不能仅凭使用Worker或rAF就宣称更快。

参考：[MDN DOM events](https://developer.mozilla.org/en-US/docs/Learn_web_development/Core/Scripting/Event_bubbling)；[MDN Web Workers](https://developer.mozilla.org/en-US/docs/Web/API/Web_Workers_API/Using_web_workers)；[MDN Fetch](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch)。较新 API 按目标运行时核对支持，示例各自独立。

<a id="interview"></a>

## 四、面试题与答案

<a id="c18-01"></a>

### C18-01 [P0·原理] target与currentTarget有什么区别？委托如何处理图标点击？

**回答：** target可能是按钮内部图标，currentTarget是当前监听容器。用closest找到业务按钮并验证容器归属，利用事件传播统一处理；Shadow DOM和非冒泡事件需额外考虑。

对应讲解：[事件传播、target 与委托](#k01)。

<a id="c18-02"></a>

### C18-02 [P0·基础] preventDefault与stopPropagation能互相替代吗？

**回答：** 不能，前者控制默认动作，后者控制传播。停止传播不会自动取消导航，取消默认也不阻止祖先接收事件；passive监听中又不能成功阻止默认动作。

对应讲解：[默认行为、监听选项与清理](#k02)。

<a id="c18-03"></a>

### C18-03 [P1·基础] DOMContentLoaded是否表示页面所有内容加载完？

**回答：** 不表示。它主要关联文档解析与相关脚本条件，不等待所有资源或未来请求；window load也不等于SPA整个生命周期的异步工作完成。需要业务就绪状态时由应用自己定义。

对应讲解：[DOM 状态与页面加载](#k03)。

<a id="c18-04"></a>

### C18-04 [P0·原理] fetch返回200之后为什么还能失败？

**回答：** 响应头到达并不代表body读取完成，后续可能断网、取消、JSON解析失败或结构不合法。分别处理HTTP、传输、解析和业务校验，不能一个response.ok覆盖全部。

对应讲解：[Fetch、URL 与存储](#k04)。

<a id="c18-05"></a>

### C18-05 [P1·工程取舍] Worker一定提升性能吗？

**回答：** 不一定。主线程占用可能下降，但创建、复制、通信和合并有成本，DOM工作仍在主线程。应在相同数据和设备上测长任务、输入延迟及内存，只有合适可拆分重计算才值得迁移。

对应讲解：[Worker 的任务与通信](#k05)。
