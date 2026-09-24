# 02 Nuxt 4 架构与目录边界

Nuxt在Vue之上提供路由、数据获取、服务端渲染和服务器能力。理解各目录的执行环境，比记住一套文件名更重要。

## 一、本章目录

- [Vue、Nuxt、Vite 与 Nitro](#k01)
- [入口、布局与自动导入](#k02)
- [SSR 执行与请求隔离](#k03)
- [runtimeConfig 与部署形态](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. Vue、Nuxt、Vite 与 Nitro

Vue负责组件和响应式，Nuxt组织应用约定与全栈渲染流程，Vite等工具参与开发构建，Nitro负责服务器路由和部署输出。它们是不同层，不能把Nuxt当另一套Vue渲染语法。

```text
app/
  app.vue
  pages/
  layouts/
  components/
  composables/
  middleware/
  plugins/
server/
  api/
  routes/
  middleware/
shared/
public/
nuxt.config.ts
```

这是Nuxt4常见结构，srcDir、layers等配置会改变实际路径。app代码可能在服务器与浏览器执行，server代码只应进入服务端，shared应避免依赖特定宿主的能力。

<a id="k02"></a>

### 2. 入口、布局与自动导入

```vue
<!-- nuxt-only: app/app.vue -->
<template>
  <NuxtLayout>
    <NuxtPage />
  </NuxtLayout>
</template>
```

pages生成路由页面，layouts提供外壳，components/composables按约定支持自动导入。自动导入减少样板但不改变模块依赖和生命周期，需要知道函数来自Nuxt、Vue还是项目文件。

plugins注入应用能力，可按.client/.server等边界区分；route middleware处理导航，server middleware处理服务器请求，二者运行环境和授权作用不同。

<a id="k03"></a>

### 3. SSR 执行与请求隔离

通用组件setup可能在服务器生成HTML时执行，也在客户端接管时执行。模块顶层单例可能跨服务器请求共享，所以用户状态应放请求隔离的应用上下文，不要放全局可变变量。

window、document、localStorage等仅在浏览器存在，使用客户端生命周期或明确客户端边界。useState可提供Nuxt管理的共享状态与序列化流程，但它保存的值仍需适合传输并避免秘密。

服务端生成HTML后，客户端使用payload等数据保持一致并hydrate，数据来源与版本要可追踪。

<a id="k04"></a>

### 4. runtimeConfig 与部署形态

runtimeConfig的私有字段用于服务器，public字段会暴露到客户端payload。运行时环境覆盖需匹配NUXT_命名规则和配置结构，不能只在构建时读一个任意.env变量后就假定部署时可改。

```ts
// nuxt-only: nuxt.config.ts
export default defineNuxtConfig({
  runtimeConfig: {
    internalApiBase: 'http://localhost:8080',
    public: { apiBase: '/api' }
  }
});
```

Node服务器、Serverless和Edge的进程、连接、文件系统和执行时长不同，Nitro适配不等于每个平台支持所有Node包。选择部署前检查运行限制与数据库连接策略。

<a id="summary"></a>

## 三、知识小结

Nuxt负责应用约定，Vue负责UI，Nitro负责服务器输出。目录和自动导入之外，必须分清通用、客户端、服务端和请求隔离边界。

参考：[Nuxt 4 Documentation](https://nuxt.com/docs/4.x/getting-started/introduction)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="build02-01"></a>

### BUILD02-01 [P0·基础] Nuxt和Vue是什么关系？

**回答：** Nuxt基于Vue，增加文件路由、SSR、数据生命周期、自动导入和服务器部署约定。它不替代Vue组件与响应式，而是补应用层能力。

对应讲解：[Vue、Nuxt、Vite 与 Nitro](#k01)。

<a id="build02-02"></a>

### BUILD02-02 [P0·原理] app代码为什么不能无条件读取window？

**回答：** 它可能先在服务器执行，那里没有浏览器DOM。应放客户端生命周期或明确客户端边界，并保证SSR与hydration的初始输出一致。

对应讲解：[SSR 执行与请求隔离](#k03)。

<a id="build02-03"></a>

### BUILD02-03 [P1·工程取舍] runtimeConfig.public能放秘密吗？

**回答：** 不能，public配置会到客户端。服务器私有配置也要避免被响应或props序列化泄露，运行时覆盖按NUXT_结构规则设置。

对应讲解：[runtimeConfig 与部署形态](#k04)。

<a id="build02-04"></a>

### BUILD02-04 [P1·基础] 路由middleware和server middleware一样吗？

**回答：** 不一样，前者参与页面导航，后者参与服务器请求流程。前端导航控制不能替代服务器对API资源的授权。

对应讲解：[入口、布局与自动导入](#k02)。
