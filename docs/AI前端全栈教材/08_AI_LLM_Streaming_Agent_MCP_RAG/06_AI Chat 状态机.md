# 06 AI Chat 状态机与消息结构

聊天生成不仅有开始和结束，还可能经历排队、流式文本、工具、取消和恢复。状态与消息片段分开建模，才能让UI操作符合当前阶段。

## 一、本章目录

- [请求状态与允许动作](#k01)
- [消息、片段与工具关联](#k02)
- [取消、重试与恢复](#k03)
- [体验、可访问性与测试](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 请求状态与允许动作

常见状态可为idle、submitting、streaming、waiting-tool、completed、failed、cancelled。实际名称由协议决定，状态机定义允许转移和每种状态可用动作，而不只是展示不同文字。

```ts
type ChatState =
  | { kind: 'idle' }
  | { kind: 'running'; requestId: string; text: string }
  | { kind: 'done'; requestId: string; text: string }
  | { kind: 'failed'; requestId: string; partial: string; message: string };

type Delta = { requestId: string; text: string };
function applyDelta(state: ChatState, event: Delta): ChatState {
  if (state.kind !== 'running' || state.requestId !== event.requestId) return state;
  return { ...state, text: state.text + event.text };
}
console.log(applyDelta({ kind: 'idle' }, { requestId: 'old', text: 'late' }).kind); // idle
```

仅有loading Boolean无法表达部分成功、停止和工具等待；只用合法状态名也不解决事件归属，仍需requestId和序号。

<a id="k02"></a>

### 2. 消息、片段与工具关联

消息可包含text、tool-call、tool-result、source、file、artifact、error等片段，每段有稳定ID和类型。工具调用结果通过callId等关联，不靠文本位置猜。

供应商可能提供公开的推理摘要或其他控制事件，不能把未公开内部推理当必需展示内容，也不能把所有delta直接串成用户可见正文。适配器将允许展示的数据转为有限片段类型。

持久化要保留结构、版本和来源，渲染只使用受控组件与经过验证的字段。

<a id="k03"></a>

### 3. 取消、重试与恢复

停止生成应使当前运行失效并传播取消，保留的部分文本标为取消或未完成。重试要决定替换哪条消息、是否复用任务、工具副作用是否已执行，不能简单清空UI然后重复全部步骤。

浏览器刷新后根据服务端conversationId/taskId恢复权威状态。客户端乐观消息ID与服务端最终ID需要映射，避免重复显示；离线恢复还需处理事件缺口和版本冲突。

<a id="k04"></a>

### 4. 体验、可访问性与测试

输入框、发送/停止按钮和工具确认应按状态启用，焦点不能被每次增量更新抢走。自动滚动尊重用户是否已离开底部，长文本渲染分批并提供结束反馈。

测试每条转移、旧请求迟到、工具失败、用户取消、断线恢复、重复事件和刷新。快照测试只验证结构，仍需交互与协议测试证明行为。

<a id="summary"></a>

## 三、知识小结

状态管生命周期，片段管内容结构，ID管归属，服务端任务管恢复。UI根据明确状态提供操作，不能用一个loading或字符串承担全部协议。

参考：[JSON Schema](https://json-schema.org/understanding-json-schema/)；[MDN Fetch](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="ai06-01"></a>

### AI06-01 [P0·原理] 为什么聊天不能只有loading？

**回答：** 生成有提交、部分输出、工具等待、成功、取消与失败等阶段，可用操作和数据不同。显式状态和对应字段减少矛盾组合，也更便于测试。

对应讲解：[请求状态与允许动作](#k01)。

<a id="ai06-02"></a>

### AI06-02 [P1·原理] 消息为什么要有parts而不只role/content？

**回答：** 工具、引用、文件和错误有不同结构与关联，单字符串会丢失可追踪ID和渲染契约。有限片段类型能更可靠持久化、恢复和展示。

对应讲解：[消息、片段与工具关联](#k02)。

<a id="ai06-03"></a>

### AI06-03 [P0·工程取舍] 点击重试是否等于重新发送同一请求？

**回答：** 要先确认原任务和工具副作用状态，决定恢复、重建或替换消息，并使用幂等与版本保护。盲目重发可能重复操作或重复计费。

对应讲解：[取消、重试与恢复](#k03)。

<a id="ai06-04"></a>

### AI06-04 [P1·工程取舍] 怎样验证状态机而不是只看UI截图？

**回答：** 覆盖合法/非法转移、旧版本事件、取消后结果、重复与乱序、工具失败和刷新恢复，观察实际提交与资源清理。截图不能证明这些时序契约。

对应讲解：[体验、可访问性与测试](#k04)。
