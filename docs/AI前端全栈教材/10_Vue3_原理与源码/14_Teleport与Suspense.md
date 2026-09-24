# 14 Teleport与Suspense

Teleport改变真实节点的位置，Suspense协调纳入边界的异步依赖。两者都参与renderer，但影响的是不同维度：DOM落点与异步分支状态。

源码基线：**Vue v3.5.43**。区分公开行为、这个版本的内部实现与本文教学模型；代码块各自独立，使用 Vue 包的例子在安装相应版本的 Node ESM 或前端工程中运行。

## 一、本章目录

- [Teleport 的锚点与目标容器](#k01)
- [组件关系与原生事件](#k02)
- [Suspense 的 pending 与 active 分支](#k03)
- [失败、取消和组合测试](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. Teleport 的锚点与目标容器

[TeleportImpl.process/resolveTarget](https://github.com/vuejs/core/blob/v3.5.43/packages/runtime-core/src/components/Teleport.ts)在逻辑位置保留占位锚点，解析to目标并维护目标范围锚点，children按disabled状态挂到原位置或目标。

更新时复用节点与目标信息，to/disabled变化需要移动正确范围；删除时清理两边锚点和子树。它不是把HTML复制两份，也不是创建另一个Vue应用。

Vue3.5的defer延后本次挂载周期中目标解析，可应对同次render稍后出现的目标，不是无限等待未来几秒才出现的容器。

<a id="k02"></a>

### 2. 组件关系与原生事件

Teleport保留parentComponent等逻辑关联，因此props、provide/inject与组件事件契约仍按组件树工作；原生click沿实际DOM父链冒泡，祖先CSS选择器、布局和focus也受真实位置影响。

弹窗示例移到body可绕过部分祖先布局限制，但仍需焦点圈定、背景不可操作、关闭和滚动管理。不能把Teleport等同完整可访问Modal或绝对解决所有z-index问题。

<a id="k03"></a>

### 3. Suspense 的 pending 与 active 分支

[SuspenseImpl/createSuspenseBoundary/registerDep](https://github.com/vuejs/core/blob/v3.5.43/packages/runtime-core/src/components/Suspense.ts)管理hiddenContainer、pendingBranch、activeBranch及异步依赖计数。未完成分支先在相应隐藏环境推进，有未就绪依赖时显示fallback；依赖完成再resolve切换。

被识别的async setup/异步组件可参与，任意mounted里启动fetch不自动成为Suspense依赖。边界需区分请求身份/pendingId等，旧异步结果不能错误提交到新分支。

在本系列Vue3.5语境下，官方仍将Suspense作为实验性能力说明，工程上锁定版本与框架支持，不宣称稳定覆盖所有组合。

<a id="k04"></a>

### 4. 失败、取消和组合测试

Suspense不是完整错误边界，异步失败需onErrorCaptured等合适处理和产品恢复。fallback不是服务端任务状态，也不自动abort底层请求。

与Teleport/KeepAlive/Transition组合时，挂载目标、缓存激活和过渡时机相互影响；按官方推荐结构并测试pending期间切换、卸载和目标变化。SSR水合也有对应额外路径，不能从客户端一次演示推断所有环境。

基本验证包括目标容器实际位置、注入仍可用、原生事件真实冒泡、fallback切到内容、取消/卸载后不提交旧分支。

<a id="summary"></a>

## 三、知识小结

Teleport保留逻辑关系并移动宿主范围，Suspense跟踪特定异步分支与依赖。两者都不替代完整弹窗、错误恢复或请求取消协议。

<a id="interview"></a>

## 四、面试题与答案

<a id="vsr14-01"></a>

### VSR14-01 [P0·原理] Teleport会让provide/inject关系断开吗？

**回答：** 通常不会，它保留Vue逻辑父子关系，但真实DOM已移动，原生事件和CSS祖先关系按真实位置变化。需要区分组件树与DOM树。

对应讲解：[组件关系与原生事件](#k02)。

<a id="vsr14-02"></a>

### VSR14-02 [P0·原理] defer是否等待任意未来时刻出现的目标？

**回答：** 不是，它在支持版本中延后相应挂载周期的解析，不能当异步轮询查目标。必须按实际渲染时机保证容器存在。

对应讲解：[Teleport 的锚点与目标容器](#k01)。

<a id="vsr14-03"></a>

### VSR14-03 [P0·原理] Suspense为什么不能监听所有fetch？

**回答：** 它协调注册到边界的特定异步依赖，不会自动跟踪任何普通Promise或mounted请求。数据加载要接入支持机制并处理错误/取消。

对应讲解：[Suspense 的 pending 与 active 分支](#k03)。

<a id="vsr14-04"></a>

### VSR14-04 [P1·工程取舍] fallback消失是否证明后台任务成功？

**回答：** 不代表所有业务成功，边界就绪与接口业务结局、权限、部分失败不同。还需明确错误展示、版本归属和真实服务状态。

对应讲解：[失败、取消和组合测试](#k04)。
