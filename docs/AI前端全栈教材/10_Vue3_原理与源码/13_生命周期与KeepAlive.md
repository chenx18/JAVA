# 13 生命周期与KeepAlive

生命周期钩子是注册到实例的函数，由渲染和调度路径在对应阶段调用。KeepAlive改变了切换时是否真正卸载，因此资源的暂停与销毁需要分别设计。

源码基线：**Vue v3.5.43**。区分公开行为、这个版本的内部实现与本文教学模型；代码块各自独立，使用 Vue 包的例子在安装相应版本的 Node ESM 或前端工程中运行。

## 一、本章目录

- [钩子注册到谁](#k01)
- [同步父子挂载、更新与卸载](#k02)
- [KeepAlive如何保留实例](#k03)
- [缓存容量、清理与测试](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 钩子注册到谁

[injectHook/createHook](https://github.com/vuejs/core/blob/v3.5.43/packages/runtime-core/src/apiLifecycle.ts)以currentInstance或显式目标找到实例钩子数组，包装错误处理、当前实例和追踪暂停后登记。

通常在setup同步阶段注册；把onMounted放普通timer里，没有活动实例就无法按预期关联。script setup顶层await有编译恢复上下文等支持，不能推广为任意async函数await后都可随时注册。

钩子内部读取一般不应误收集进当时包围它的渲染effect，所以包装会pauseTracking并恢复。

<a id="k02"></a>

### 2. 同步父子挂载、更新与卸载

无异步组件/Suspense的典型父子挂载：父setup/beforeMount先进入，挂载子树时子先完成mounted相关路径，再完成父mounted。更新按队列和依赖推进，卸载则先执行相应beforeUnmount、停scope、卸载子树，再安排unmounted。

这是受场景限定的过程，不是所有异步组合都符合一条万能顺序表。SSR不执行客户端DOM挂载钩子；onServerPrefetch有不同用途。

源码在[setupRenderEffect/unmountComponent](https://github.com/vuejs/core/blob/v3.5.43/packages/runtime-core/src/renderer.ts)找bm/m/bu/u/bum/um这些实例字段消费点，再回到注册入口确认含义。

<a id="k03"></a>

### 3. KeepAlive如何保留实例

[cache/keys/activate/deactivate](https://github.com/vuejs/core/blob/v3.5.43/packages/runtime-core/src/components/KeepAlive.ts)通过cache保存可复用VNode/实例关联，以keys管理访问顺序和max淘汰。切走时将子树移动到storageContainer并标记停用；返回时移回、patch可能变化的props并调用activated相关钩子。

停用不是stop整个组件scope，不应假设watcher、timer和连接全暂停。max淘汰或真正卸载才有相应销毁路径。include/exclude按组件名等规则筛选，key影响缓存身份，不能无限缓存用户敏感页面。

<a id="k04"></a>

### 4. 缓存容量、清理与测试

cache命中会刷新访问顺序，超max淘汰旧项；当前仍活跃的项处理还需避免当场错误卸载。源码pruneCacheEntry与resetShapeFlag说明“从缓存移除”和“此刻卸载”需区分。

测试A→B→A观察setup次数、activated/deactivated，max=1时检查A是否真正卸载和重建。再验证停用期间watcher是否仍工作，主动决定轮询暂停策略。

缓存提升切换体验却增加内存，退出登录、权限变化和数据过期需要明确失效；KeepAlive不会自动清理所有业务缓存。

<a id="summary"></a>

## 三、知识小结

钩子登记在实例，运行时和调度器决定调用阶段。KeepAlive移动并保留实例，停用与卸载分开，缓存容量、业务失效和资源所有者一起管理。

<a id="interview"></a>

## 四、面试题与答案

<a id="vsr13-01"></a>

### VSR13-01 [P0·原理] 为什么onMounted通常要在setup同步注册？

**回答：** 注册需要明确活动组件实例，将回调加入它的钩子数组。任意异步回调中可能已无该上下文，不能靠函数名称自动找到组件。

对应讲解：[钩子注册到谁](#k01)。

<a id="vsr13-02"></a>

### VSR13-02 [P0·原理] 父mounted能否保证所有异步后代已完成？

**回答：** 不能，典型同步子树顺序不能推广给异步组件或Suspense；需相应边界或完成信号。客户端mounted也不在SSR中照常执行。

对应讲解：[同步父子挂载、更新与卸载](#k02)。

<a id="vsr13-03"></a>

### VSR13-03 [P0·原理] KeepAlive停用时为什么不等于卸载？

**回答：** 它保留实例关联并把子树移到存储容器，返回时复用；scope和业务资源未必停止。真正淘汰/卸载才走销毁逻辑，暂停工作由业务明确。

对应讲解：[KeepAlive如何保留实例](#k03)。

<a id="vsr13-04"></a>

### VSR13-04 [P1·验证] 如何证明KeepAlive缓存和淘汰正确？

**回答：** 观察切换中的实例建立、激活、停用和卸载次数，改变max与key验证复用，再查保留资源和登录失效。只看页面内容能回来不足以证明生命周期。

对应讲解：[缓存容量、清理与测试](#k04)。
