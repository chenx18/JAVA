# 06 Nitro、服务器路由与运行配置

Nitro将Nuxt服务器能力组织成适合目标平台的输出。教学要把路由处理、应用身份、配置与部署限制接起来，避免把server目录当成无需设计的万能代理。

## 一、本章目录

- [文件路由与请求处理](#k01)
- [配置、上下文与下游请求](#k02)
- [轻量后端与 BFF 职责](#k03)
- [Node、Serverless 与 Edge](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 文件路由与请求处理

server/api通常映射到/api，server/routes用于其他服务器路由，文件方法后缀可限定GET/POST等。server/middleware参与服务器请求处理，不能等同页面导航middleware。

```ts
// nuxt-server-only: server/api/health.get.ts
export default defineEventHandler(() => ({
  status: 'ok',
  checkedAt: new Date().toISOString()
}));
```

真实接口还需参数解析、schema校验、身份授权、状态码和错误格式。路径或请求体里的userId不自动代表当前用户身份；服务端应从可信认证上下文建立用户。

<a id="k02"></a>

### 2. 配置、上下文与下游请求

useRuntimeConfig在相应服务端上下文读取私有配置，public字段才会暴露给客户端。使用运行时覆盖规则，避免把密钥在构建时写进公开产物或日志。

下游请求的headers/context不一定自动继承；event.$fetch等有其转发机制，仍应明确目标和必要字段。不要盲目复制Host、Connection或所有客户端头到内部服务。

超时、取消和错误状态映射应由BFF契约定义；把所有下游失败都转200或统一502会丢失可恢复业务信息。

<a id="k03"></a>

### 3. 轻量后端与 BFF 职责

Nitro可承担页面聚合、适配、SSR数据、轻量API和受控网关。核心事务、复杂领域规则可以由Java等服务负责，但不是语言决定一切；关键是团队、生命周期和边界。

BFF不要再复制一套与领域服务不一致的权限/价格/事务规则。它可做入口认证和UI适配，领域服务仍验证资源操作。多服务聚合还要定义部分失败、延迟预算和请求追踪。

<a id="k04"></a>

### 4. Node、Serverless 与 Edge

部署预设会影响输出和可用API。Serverless可能有冷启动和执行时长，Edge可能限制Node模块、TCP连接或文件系统，传统Node服务又需要进程、端口和资源管理。

数据库连接池与无服务器实例扩缩容要协调，不能每次请求无上限建立连接。长任务不应强依赖一个短生命周期请求处理函数，适合持久化任务与队列。

健康检查需区分进程存活和服务就绪，日志/trace、错误恢复与优雅关闭也属于上线能力。

<a id="summary"></a>

## 三、知识小结

Nitro提供服务器组织与部署适配，业务仍需验证、授权、取消和错误契约。BFF聚合与领域服务分工明确，目标平台限制决定可用实现。

参考：[Nuxt Server Directory](https://nuxt.com/docs/4.x/guide/directory-structure/server)；[Nuxt Runtime Config](https://nuxt.com/docs/4.x/guide/going-further/runtime-config)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="build06-01"></a>

### BUILD06-01 [P0·基础] Nitro在Nuxt中负责什么？

**回答：** 它是服务器引擎，组织API、服务器路由、中间件和目标平台输出。不是Vue渲染器本身，也不自动实现业务认证、数据库事务或安全代理。

对应讲解：[文件路由与请求处理](#k01)。

<a id="build06-02"></a>

### BUILD06-02 [P1·工程取舍] 什么时候用Nitro BFF，什么时候用Java服务？

**回答：** 按页面适配/聚合与核心领域事务的边界分工，再考虑团队与部署。BFF可降低UI耦合，但不应重复不一致业务规则；不是所有接口都必须多一层。

对应讲解：[轻量后端与 BFF 职责](#k03)。

<a id="build06-03"></a>

### BUILD06-03 [P1·原理] server目录的代码能无条件用任意Node模块吗？

**回答：** 不能，最终运行平台可能是Edge或Serverless，有模块、连接、文件系统和时长限制。需要按部署预设及依赖兼容性验证，目录名不提供无限运行能力。

对应讲解：[Node、Serverless 与 Edge](#k04)。

<a id="build06-04"></a>

### BUILD06-04 [P1·工程取舍] 转发请求为什么不能直接复制所有头？

**回答：** 某些头是逐跳或目标相关的，凭据也有信任范围。应明确下游目标、用户上下文和允许转发字段，避免泄露认证信息或破坏协议。

对应讲解：[配置、上下文与下游请求](#k02)。
