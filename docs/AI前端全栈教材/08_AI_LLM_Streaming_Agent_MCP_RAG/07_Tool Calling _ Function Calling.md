# 07 Tool Calling 与受控执行

工具调用让模型提出一个结构化动作，真正执行仍由应用程序负责。把决策、参数、身份、业务权限和结果反馈分开，是从聊天接到真实系统的关键。

## 一、本章目录

- [调用循环与工具描述](#k01)
- [schema 与资源授权](#k02)
- [幂等、确认与并行](#k03)
- [结果契约与评估](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 调用循环与工具描述

```text
用户目标 + 允许工具的schema
  → 模型提出tool name/arguments/callId
  → 应用解析与验证
  → 根据当前用户授权执行
  → 以callId关联工具结果
  → 模型继续决策或给最终回答
```

工具描述解释功能与参数，不是权限声明。模型可产生多个调用或分片参数，必须等完整且验证后的调用再执行，不把半段JSON当可执行命令。

工具结果属于外部数据，可能包含错误或诱导文本，不自动升级为系统规则。模型需要知道成功、失败和可重试状态，而不仅一串含糊文本。

<a id="k02"></a>

### 2. schema 与资源授权

```ts
type Context = { tenantId: string; permissions: readonly string[] };
type Order = { id: string; status: string };
interface Orders {
  findForTenant(tenantId: string, id: string): Promise<Order | null>;
}
async function lookupOrder(context: Context, raw: unknown, orders: Orders) {
  if (!context.permissions.includes('orders:read')) throw new Error('forbidden');
  if (raw === null || typeof raw !== 'object' || Array.isArray(raw) ||
      !('id' in raw) || typeof raw.id !== 'string' || raw.id.length > 64 || !raw.id) {
    throw new TypeError('invalid arguments');
  }
  return orders.findForTenant(context.tenantId, raw.id);
}
```

context必须来自服务端可信身份，而非模型传入的tenantId。schema证明结构，资源查询仍按租户/用户过滤，工具名也必须属于注册表。不要让模型提供任意SQL、路径或URL交给高权限执行器。

<a id="k03"></a>

### 3. 幂等、确认与并行

读取类操作可在已有授权范围执行；有外部副作用或缺少授权的动作按产品策略确认，确认界面应展示具体对象、参数和影响，不能只问“允许吗”。

callId帮助关联不必然等于业务幂等键，写操作需服务端幂等记录与结果查询。模型重试、网络重连或工作流恢复都可能重复提出调用。

只有依赖独立、权限和资源允许的工具可并行，修改同一资源或后续依赖前一步结果的调用应按顺序或事务策略处理。

<a id="k04"></a>

### 4. 结果契约与评估

工具返回有限结构：成功数据、稳定错误码、是否需要输入、任务句柄等。内部异常和秘密不应直接进入模型上下文或用户页面，必要信息脱敏并控制大小。

测试工具选择准确率、参数合法率、授权拒绝、重复执行、部分失败和最终回答是否忠实于工具结果。模型决策评估与工具函数单元测试分开，不能只看模型说“已完成”。

<a id="summary"></a>

## 三、知识小结

模型提出调用，程序验证与授权后执行，结果再回到循环。schema、权限、幂等、确认和追踪各负一层责任，callId不能替代所有这些机制。

参考：[JSON Schema](https://json-schema.org/understanding-json-schema/)；[MCP Specification](https://modelcontextprotocol.io/specification/2026-07-28)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="ai07-01"></a>

### AI07-01 [P0·原理] Tool Calling与普通API调用的区别是什么？

**回答：** 普通程序通常确定调用哪个API，工具调用让模型参与选择名称与参数；真正执行仍由受控代码完成，多了模型决策与验证边界，不代表模型获得任意权限。

对应讲解：[调用循环与工具描述](#k01)。

<a id="ai07-02"></a>

### AI07-02 [P0·原理] 参数符合schema为什么还可能越权？

**回答：** schema主要约束形状与部分取值，不证明当前用户能访问该订单或租户。身份来自可信后端上下文，查询和动作还需资源授权。

对应讲解：[schema 与资源授权](#k02)。

<a id="ai07-03"></a>

### AI07-03 [P1·工程取舍] callId能直接作为所有幂等保证吗？

**回答：** 它主要关联模型调用与结果，业务是否跨重试复用该ID和是否原子记录需另设计。写操作应有服务端幂等和结果查询，而非假定网络只送一次。

对应讲解：[幂等、确认与并行](#k03)。

<a id="ai07-04"></a>

### AI07-04 [P1·工程取舍] 工具调用成功如何验收？

**回答：** 检查实际工具结果、权限、副作用与错误，再评估最终回答是否忠实。模型生成“成功”文字不构成执行证据，决策质量和执行正确性分层测试。

对应讲解：[结果契约与评估](#k04)。
