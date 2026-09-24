# 11 类型安全 API 与工具调用边界

接口类型描述应用希望收到什么，运行时解析器证明这一次输入是否真的满足。把unknown、校验、领域结果和错误处理连起来，才是可用的类型安全边界。

## 一、本章目录

- [请求、响应与领域对象](#k01)
- [unknown 到已验证数据](#k02)
- [成功、错误与取消的类型](#k03)
- [AI Tool 参数与 schema 来源](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 请求、响应与领域对象

请求DTO只包含调用允许提交的字段，响应DTO表达接口实际返回结构，领域/展示模型按应用需要转换。它们可以共享稳定片段，但不能为了少写类型把数据库对象、写入请求和公开响应混成同一契约。

比如用户修改请求不应允许客户端提交权限或内部审计字段；响应类型去掉password也需要服务端真的不输出该字段。TypeScript类型不能替代后端白名单与授权。

接口中的时间、金额、大整数ID和可空值要有明确编码约定，不把Date类或BigInt类型直接假定成JSON传输格式。

<a id="k02"></a>

### 2. unknown 到已验证数据

```ts
type User = { id: string; name: string };
function parseUser(value: unknown): User {
  if (value === null || typeof value !== 'object' || Array.isArray(value) ||
      !('id' in value) || typeof value.id !== 'string' ||
      !('name' in value) || typeof value.name !== 'string') {
    throw new TypeError('invalid user payload');
  }
  return { id: value.id, name: value.name };
}
async function loadUser(id: string): Promise<User> {
  const response = await fetch('/api/users/' + encodeURIComponent(id));
  if (!response.ok) throw new Error('HTTP ' + response.status);
  const raw: unknown = await response.json();
  return parseUser(raw);
}
console.log(parseUser({ id: '1', name: 'A', extra: true }));
```

示例明确构造返回值，只保留两个字段。实际schema还应验证长度、格式和可接受范围；HTTP成功、JSON语法正确、结构正确和业务成功是不同层次。泛型`fetch<T>`若只是as T，并没有完成这一步。

<a id="k03"></a>

### 3. 成功、错误与取消的类型

```ts
type Result<T> =
  | { ok: true; data: T }
  | { ok: false; code: 'NOT_FOUND' | 'FORBIDDEN'; message: string };

function show(result: Result<{ name: string }>): string {
  return result.ok ? result.data.name : result.message;
}
console.log(show({ ok: false, code: 'NOT_FOUND', message: '用户不存在' }));
```

可以用异常表示不可恢复技术失败，用Result表示预期业务分支，但必须在团队中保持一致。不要catch所有异常后返回{ok:true,data:undefined}，也不要把用户取消都转成服务器错误。

请求版本、取消signal和幂等键属于运行协议，即使类型覆盖所有字段，也仍要执行提交归属、超时和权限检查。

<a id="k04"></a>

### 4. AI Tool 参数与 schema 来源

模型产生的工具名称和参数都是外部输入。服务端应只允许注册工具，校验参数schema，执行用户授权及资源范围检查，再调用真正业务函数。类型系统帮助开发者接线，不能授权模型执行任意操作。

schema可以作为运行时校验与静态类型生成的共同来源，但要区分库支持的schema子集和服务端约束；模型侧结构化输出限制不能代替应用验证。工具结果也要定义成功、失败、重试/等待等结构，不把所有结果压成无类型字符串。

开发流程应包含合法/缺字段/错误类型/越权资源/重复执行/取消等测试；模型不确定性与工具执行确定性分层验证。

<a id="summary"></a>

## 三、知识小结

边界路径是未知输入→语法解析→schema验证→领域转换→授权与业务动作。静态类型贯穿开发，但不能越过运行时边界替数据作保证。

参考：[TypeScript Handbook](https://www.typescriptlang.org/docs/handbook/intro.html)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="ts11-01"></a>

### TS11-01 [P0·原理] `fetch<User>`为什么不等于验证了User？

**回答：** 泛型通常只把开发期期望传给检查器，若实现只是断言，就没有检查实际JSON。应把响应视为unknown，通过解析器/schema建立可信结构，再返回User。

对应讲解：[unknown 到已验证数据](#k02)。

<a id="ts11-02"></a>

### TS11-02 [P0·工程取舍] DTO、Entity、展示模型能否总用一个类型？

**回答：** 不宜。读写权限、敏感字段、编码和展示派生信息不同。可以复用稳定片段，但请求白名单、服务端响应裁剪和实际转换不能因为类型复用而省略。

对应讲解：[请求、响应与领域对象](#k01)。

<a id="ts11-03"></a>

### TS11-03 [P1·原理] `Result<T>`能代替所有异常吗？

**回答：** 它适合明确的业务分支，不自动解决网络中断、代码缺陷和取消。需要规定哪些情况返回Result、哪些抛错，以及调用方如何收窄和处理，避免伪装成功。

对应讲解：[成功、错误与取消的类型](#k03)。

<a id="ts11-04"></a>

### TS11-04 [P1·工程取舍] 模型工具参数通过schema后可以直接执行吗？

**回答：** 还需要用户和资源授权、业务约束、幂等和必要确认策略。schema主要证明形状与部分约束，不能证明调用者拥有权限或操作适合当前状态。

对应讲解：[AI Tool 参数与 schema 来源](#k04)。
