# 05 useFetch、useAsyncData 与 $fetch

三个API都能参与取数，但只有部分会与Nuxt的SSR payload、响应式状态和共享key联动。选择取决于数据属于页面初始渲染，还是一次用户动作。

## 一、本章目录

- [职责与返回](#k01)
- [页面数据与响应式来源](#k02)
- [自定义 handler、取消与 key](#k03)
- [SSR 凭据与缓存边界](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 职责与返回

| API | 主要用途 | 返回/边界 |
| --- | --- | --- |
| $fetch | 直接执行请求 | Promise结果；不自动把setup结果转为Nuxt页面共享数据 |
| useAsyncData(key,handler,options) | 管理自定义异步数据 | data/error/status等ref及refresh/execute/clear |
| useFetch(url,options) | 与Nuxt数据生命周期集成的常见请求 | 结合请求与AsyncData机制，处理相关key/payload |

setup在SSR与客户端接管时都可能运行，直接$fetch而未把结果纳入Nuxt数据流程，可能重复初次请求。用户点击后提交、下载等动作通常不需伪装成页面初始AsyncData。

不能说“useFetch永远只请求一次”：参数变化、重新验证、刷新、缓存失效和配置都可能触发后续请求。

<a id="k02"></a>

### 2. 页面数据与响应式来源

```vue
<!-- nuxt-only -->
<script setup lang="ts">
type User = { id: string; name: string }
const page = ref(1)
const { data, status, error, refresh } = await useFetch<User[]>('/api/users', {
  query: { page },
  default: () => []
})
</script>
<template>
  <button type="button" @click="refresh()">刷新</button>
  <p v-if="status === 'pending'">加载中</p>
  <p v-else-if="error">加载失败</p>
  <ul v-else><li v-for="user in data" :key="user.id">{{ user.name }}</li></ul>
</template>
```

类型实参不校验真实JSON，应用仍要在边界验证数据。响应式URL/query可驱动重新取数，具体watch和immediate配置决定时机；lazy主要改变导航等待行为，不等于滚动到视口才请求。

server:false表示不在服务端执行相应取数，初始data状态和客户端hydration时机就不同，模板需处理空值/等待。

<a id="k03"></a>

### 3. 自定义 handler、取消与 key

```ts
// nuxt-only
const { data, status } = await useAsyncData(
  'dashboard-summary',
  async (_nuxtApp, { signal }) => {
    const [users, orders] = await Promise.all([
      $fetch('/api/user-count', { signal }),
      $fetch('/api/order-count', { signal })
    ]);
    return { users, orders };
  }
);
```

handler应产生可缓存的数据，避免把扣款、发消息或不受控Store副作用塞进去。需要在重跑时安全取消，signal必须传给实际支持的请求。

相同key可能共享data/error/status等状态，handler、deep、transform、pick、default和缓存读取等相关选项需保持一致。key要表达数据身份，不能让不同用户或查询条件错误共用。返回undefined/null等缺失结果可能影响初始重用策略，按API契约返回明确值。

<a id="k04"></a>

### 4. SSR 凭据与缓存边界

相对URL的useFetch在服务端可借Nuxt请求上下文转发相应头/Cookie，直接$fetch或自定义外部请求不应假定自动带上浏览器凭据。只向可信目标转发必要头，不能接受用户任意URL再带内部认证请求。

较新Nuxt4数据层的深浅响应式默认值和选项与旧资料可能不同，按目标版本选择deep并理解成本；浅数据内部修改不等于自动深追踪。

AsyncData复用、浏览器HTTP缓存、Nitro缓存和CDN不是同一层。明确取消、刷新、错误、重试和用户切换，再决定缓存策略。

<a id="summary"></a>

## 三、知识小结

页面取数用Nuxt生命周期管理，动作请求用直接请求工具；key表达身份，payload服务初始重用，signal负责协作取消，类型和缓存都不能替代数据与权限校验。

参考：[Nuxt Data Fetching](https://nuxt.com/docs/4.x/getting-started/data-fetching)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="build05-01"></a>

### BUILD05-01 [P0·原理] setup直接$fetch为什么可能双请求？

**回答：** setup可能在服务器渲染和客户端接管阶段各执行，普通$fetch没有自动把结果接入Nuxt页面payload复用。useFetch/useAsyncData提供相关生命周期机制，但后续刷新等仍会请求。

对应讲解：[职责与返回](#k01)。

<a id="build05-02"></a>

### BUILD05-02 [P0·基础] useFetch和useAsyncData怎么选？

**回答：** 常见HTTP取数用useFetch方便表达URL和选项；自定义SDK或多请求聚合可用useAsyncData。两者都需要正确key和纯数据handler，用户动作不必全部包进去。

对应讲解：[自定义 handler、取消与 key](#k03)。

<a id="build05-03"></a>

### BUILD05-03 [P1·原理] 相同key为什么要保持选项一致？

**回答：** 它可能代表共享的一组AsyncData状态，不同handler或转换/默认值会使同一身份的数据契约矛盾。应让key反映真实数据范围并统一影响结果的选项。

对应讲解：[自定义 handler、取消与 key](#k03)。

<a id="build05-04"></a>

### BUILD05-04 [P1·工程取舍] SSR转发Cookie有什么边界？

**回答：** 需理解Nuxt相对请求和普通外部fetch的区别，只向可信目标传必要凭据。BFF不应接受任意目标URL并转发认证信息，服务端仍要授权和防串用户缓存。

对应讲解：[SSR 凭据与缓存边界](#k04)。

<a id="build05-05"></a>

### BUILD05-05 [P1·原理] useFetch的lazy与server:false是否表示同一件事？

**回答：** 不是，lazy主要影响导航等待方式，server:false控制是否在服务端取数，初始data和hydration过程会不同。响应式URL/query还可能触发后续请求，模板要处理真实状态。

对应讲解：[页面数据与响应式来源](#k02)。
