# 12 KeepAlive、Teleport、Suspense 与 Transition

这些内建组件分别管理实例缓存、DOM位置、异步依赖与过渡过程。它们可能组合使用，但生命周期和事件边界需要分别理解。

## 一、本章目录

- [KeepAlive：缓存实例与激活](#k01)
- [Teleport：逻辑树与真实DOM](#k02)
- [Suspense 与异步组件](#k03)
- [Transition、TransitionGroup 与可访问性](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. KeepAlive：缓存实例与激活

KeepAlive缓存可切换组件实例及其状态，include/exclude按组件名等规则筛选，max限制缓存数量并采用相应淘汰行为。它不是简单给所有DOM加display:none。

激活/停用使用onActivated/onDeactivated，真正移出缓存还会发生卸载。停用不应假定所有watcher、轮询、连接都自动暂停；需要根据业务在停用时暂停、激活时恢复，最终卸载清理。

列表路由参数、key和缓存身份影响实例是否复用；缓存用户敏感页面时还需处理退出登录、权限变化与数据过期，不能无限保留旧会话。

<a id="k02"></a>

### 2. Teleport：逻辑树与真实DOM

Teleport把内容挂到指定DOM目标，Vue逻辑父子关系仍保留，所以props、组件事件和provide/inject按组件关系工作；原生DOM事件则沿实际DOM路径传播，不能把两种事件体系混为一谈。

```vue
<script setup lang="ts">
const props = defineProps<{ open: boolean }>()
</script>
<template>
  <Teleport to="body">
    <div v-if="props.open" role="dialog" aria-modal="true" aria-label="说明">
      <slot />
    </div>
  </Teleport>
</template>
```

这只是挂载位置示例，完整弹窗还需焦点管理、关闭交互、背景可操作性和滚动策略。目标元素需在合适时机存在，Vue3.5的defer等能力也有具体解析时机限制。

<a id="k03"></a>

### 3. Suspense 与异步组件

Suspense协调子树中的异步依赖与fallback，常与async setup或异步组件相关；当前Vue3官方文档仍将其标为实验性，应锁定版本理解行为。

异步组件的加载、超时、错误、重试与Suspense边界要明确。不是任意fetch放进组件就自动被Suspense识别；请求状态是否属于该异步依赖链取决于使用方式。

在实际产品中，局部骨架屏与错误恢复常比整页挂起更合适；SSR与hydration还需配合一致的数据与错误策略。

<a id="k04"></a>

### 4. Transition、TransitionGroup 与可访问性

Transition处理单个条件节点/组件的进入离开，TransitionGroup处理带key列表的过渡和移动。CSS类或JS钩子描述阶段，key稳定性和真实节点尺寸影响动画。

过渡时长与完成信号要与实际动画一致，避免先销毁节点却还想执行动画。动画不应妨碍操作和焦点，尊重prefers-reduced-motion；大量布局属性动画也可能增加主线程成本。

组合KeepAlive、RouterView、Transition等时，应按官方支持结构确认缓存与过渡的先后，不靠组件嵌套外观猜生命周期。

<a id="summary"></a>

## 三、知识小结

KeepAlive管实例复用，Teleport管DOM落点，Suspense管特定异步依赖，Transition管进入离开。逻辑组件关系、实际DOM和资源生命周期分别分析。

参考：[Vue Built-in Components](https://vuejs.org/api/built-in-components.html)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="vue12-01"></a>

### VUE12-01 [P0·原理] KeepAlive停用是否等于卸载？

**回答：** 不是，实例和状态可保留以便再次激活。不能默认轮询和所有副作用都被业务意义上暂停，需设计activated/deactivated与最终卸载的资源行为。

对应讲解：[KeepAlive：缓存实例与激活](#k01)。

<a id="vue12-02"></a>

### VUE12-02 [P0·原理] Teleport是否改变组件事件和原生事件的路径？

**回答：** Vue逻辑父子关系保留，组件契约按组件树工作；真实DOM已移动，原生事件沿真实DOM路径传播。必须区分两者才能正确设计委托和弹层交互。

对应讲解：[Teleport：逻辑树与真实DOM](#k02)。

<a id="vue12-03"></a>

### VUE12-03 [P1·工程取舍] Suspense能自动管理组件中的任意异步请求吗？

**回答：** 不能，只协调纳入其支持机制的异步依赖，且当前Vue3仍有实验性与版本边界。请求失败、重试和部分展示仍需业务设计。

对应讲解：[Suspense 与异步组件](#k03)。

<a id="vue12-04"></a>

### VUE12-04 [P1·基础] TransitionGroup为什么需要稳定key？

**回答：** 列表移动和复用依赖项目身份，key不稳定会造成错误匹配或反复创建销毁，动画和内部状态都可能异常。性能与可访问性也不能只靠动画类解决。

对应讲解：[Transition、TransitionGroup 与可访问性](#k04)。
