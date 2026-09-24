# 03 AI API、适配层与可靠请求

浏览器调用自己的业务API，由受控后端持有供应商凭据并执行权限、配额和协议适配。下面的接口是应用层契约，不绑定未核对的供应商SDK参数。

## 一、本章目录

- [浏览器、业务后端与供应商](#k01)
- [可测试的应用契约](#k02)
- [错误、超时、重试与成本](#k03)
- [日志、配置与上线验证](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 浏览器、业务后端与供应商

```text
Browser：提交用户意图，显示进度
  → Backend：认证、输入验证、预算与任务ID
  → Provider Adapter：供应商请求/事件转换
  → Model Provider
  → 领域结果/事件 → Browser
```

长期API密钥不能放进公开前端产物。后端不能仅隐藏密钥，还应限制允许模型、输入大小、用户额度和工具能力；不能让客户端随意指定任意上游URL。

供应商的model名称、参数、响应、拒绝和用量字段会演进，适配器按已选版本实现，并用录制/脱敏样本及契约测试验证，不把不同供应商事件直接当完全相同。

<a id="k02"></a>

### 2. 可测试的应用契约

```ts
type ModelReply = {
  text: string;
  finish: 'completed' | 'length' | 'refused';
};
interface ModelProvider {
  generate(input: { question: string; maxOutputTokens: number; signal?: AbortSignal }): Promise<ModelReply>;
}
async function ask(provider: ModelProvider, raw: unknown): Promise<ModelReply> {
  if (typeof raw !== 'string' || !raw.trim() || raw.length > 4000) {
    throw new TypeError('invalid question');
  }
  return provider.generate({ question: raw.trim(), maxOutputTokens: 1000 });
}
const fake: ModelProvider = {
  async generate({ question }) { return { text: '收到：' + question, finish: 'completed' }; }
};
console.log((await ask(fake, ' hello ')).text); // 收到：hello
```

这是适配器契约与假实现的独立测试示例，不是真实供应商端点。产品长度/输出预算是本例规则，不是模型固定上限。真正接入时还需认证、usage、错误映射与取消传播。

<a id="k03"></a>

### 3. 错误、超时、重试与成本

区分网络失败、429、供应商5xx、输入/schema错误、拒绝、长度截断和工具执行失败。HTTP成功也可能是应用不完整结果，不能统一当正常答案。

分别控制连接、首数据、空闲和总截止时间；重试遵守Retry-After及总预算，对有副作用工具不可盲目重做。超时不证明供应商未执行或未计费，需任务/请求ID和可查询状态。

fallback应解释何时改模型及质量/成本变化，并行竞速可能同时消耗多个请求费用，不能等同于失败后才启动的顺序回退。

<a id="k04"></a>

### 4. 日志、配置与上线验证

配置模型路由与预算，记录请求ID、阶段耗时、使用量、结束原因和错误类别；聊天内容与个人数据按最小需要和保留策略处理，不默认全量打日志。

先用假适配器验证业务状态，再对真实供应商进行受控契约测试，最后验证超时、限流、取消和版本变化。供应商文档未核对时不复制看似完整的SDK代码冒充可运行集成。

本教材展示可移植应用层，实际参数、事件与计费以所选供应商官方版本为准。

<a id="summary"></a>

## 三、知识小结

AI API的工程核心是可信后端、稳定应用契约、供应商适配、预算与失败策略。mock验证业务，不等于已验证外部服务；两层证据分别记录。

参考：[JSON Schema](https://json-schema.org/understanding-json-schema/)；[MDN Fetch](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="ai03-01"></a>

### AI03-01 [P0·原理] 后端代理AI请求只为了藏Key吗？

**回答：** 还要验证用户、额度、输入和允许模型/工具，适配协议并管理超时、日志与成本。仅转发任意客户端请求会把秘密隐藏成一个不受控网关。

对应讲解：[浏览器、业务后端与供应商](#k01)。

<a id="ai03-02"></a>

### AI03-02 [P0·工程取舍] 怎样避免供应商接口变化传遍前端？

**回答：** 定义应用领域结果和事件，由适配器转换供应商协议，保留有意义的错误、用量和结束原因，并做契约测试。不能为了统一而丢掉关键差异。

对应讲解：[可测试的应用契约](#k02)。

<a id="ai03-03"></a>

### AI03-03 [P1·原理] 请求超时后立即重试有什么风险？

**回答：** 原请求可能仍执行或已计费，有副作用工具还可能重复操作。应有截止时间、请求ID、结果查询和幂等策略，按错误类别和预算决定重试。

对应讲解：[错误、超时、重试与成本](#k03)。

<a id="ai03-04"></a>

### AI03-04 [P1·工程取舍] mock示例通过能证明真实AI接入完成吗？

**回答：** 不能，它证明应用契约和状态逻辑，真实接入还需验证官方参数、事件、认证、限流、取消和错误。两种测试范围应明确区分。

对应讲解：[日志、配置与上线验证](#k04)。
