# 09 Agent 循环、状态与预算

Agent通常根据目标和当前观察多次选择行动。工程重点是有限、可观测、可恢复的控制循环，而不是让模型无限自我对话。

## 一、本章目录

- [模型决策与程序控制](#k01)
- [有界循环的契约](#k02)
- [停止、失败与确认](#k03)
- [恢复与评估](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 模型决策与程序控制

系统通常包含目标、状态、模型、允许工具和循环。模型提出下一步，程序验证并执行，结果进入下一轮；某些步骤可以是确定性规则，不必每一步都问模型。

普通Chat也能调用工具，Agent与Chat不是两种完全互斥技术，差别常在是否自主组织多步行动和持久任务。应先判断任务是否需要这种灵活性，固定流程可能更稳定和便宜。

<a id="k02"></a>

### 2. 有界循环的契约

```ts
type Decision =
  | { kind: 'final'; text: string }
  | { kind: 'tool'; name: string; arguments: unknown };
interface AgentPort {
  decide(history: readonly string[], signal: AbortSignal): Promise<Decision>;
  execute(decision: Extract<Decision, { kind: 'tool' }>, signal: AbortSignal): Promise<string>;
}
async function runAgent(port: AgentPort, signal: AbortSignal, maxSteps = 5) {
  const history: string[] = [];
  for (let step = 0; step < maxSteps; step++) {
    signal.throwIfAborted();
    const decision = await port.decide(history, signal);
    if (decision.kind === 'final') return decision.text;
    const result = await port.execute(decision, signal);
    history.push(result);
  }
  throw new Error('step budget exhausted');
}
```

这里假定decide适配器已验证结构，execute负责白名单和授权；不是完整生产Agent。步数上限之外还需总时长、token、费用、工具次数和内容大小预算，底层操作也必须响应signal。

<a id="k03"></a>

### 3. 停止、失败与确认

终止条件可以是已完成目标、证据不足需用户输入、预算耗尽、不可恢复错误或取消。不要让模型自己一句“继续努力”绕过程序预算。

多次相同失败应进入恢复/降级或停止，而不是无限重试。高影响工具动作按授权策略形成可审查提案，再执行；普通已授权步骤可连续推进，不必每步重复确认。

模型最终回答应基于真实观察和执行记录，未完成要明确剩余状态，不能用总结文字伪装成功。

<a id="k04"></a>

### 4. 恢复与评估

长循环保存taskId、stepId、状态版本、工具输入结果和关键checkpoint，崩溃后从确定边界恢复。敏感内容与保留策略要明确，日志不是无限聊天全文仓库。

评估任务完成率、正确工具选择、重复/无效步骤、取消与恢复质量、总成本和延迟。更自主不一定更好，需与固定workflow基线比较。

<a id="summary"></a>

## 三、知识小结

Agent是模型决策与程序控制共同组成的有界工作系统。目标、状态、权限、预算和真实结果决定是否完成，循环次数和华丽计划不能代替交付。

参考：[JSON Schema](https://json-schema.org/understanding-json-schema/)；[MCP Specification](https://modelcontextprotocol.io/specification/2026-07-28)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="ai09-01"></a>

### AI09-01 [P0·基础] Agent比普通聊天多了什么？

**回答：** 通常多了围绕目标的多步决策、工具执行、状态和恢复循环。但边界不是绝对，聊天也可调用工具；关键看系统承担多少自主流程控制。

对应讲解：[模型决策与程序控制](#k01)。

<a id="ai09-02"></a>

### AI09-02 [P0·工程取舍] 为什么必须程序侧限制预算？

**回答：** 模型可能反复选择失败或昂贵步骤，不能可靠地只靠提示词控制成本。程序应限制步数、时间、token和工具资源，并让底层请求支持取消。

对应讲解：[有界循环的契约](#k02)。

<a id="ai09-03"></a>

### AI09-03 [P1·原理] Agent说完成了就算完成吗？

**回答：** 不算，应检查真实工具结果、状态和验收条件，未执行或失败的步骤要如实保留。生成文本不能替代数据库或外部系统的执行证据。

对应讲解：[停止、失败与确认](#k03)。

<a id="ai09-04"></a>

### AI09-04 [P1·工程取舍] 什么时候不用Agent循环？

**回答：** 需求流程清晰、步骤固定、可靠性和成本更重要时，确定性workflow可能更合适。通过任务样本与基线比较，而不是为“智能”增加不必要自主决策。

对应讲解：[恢复与评估](#k04)。
