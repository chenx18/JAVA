# 03 Nuxt 文件路由与页面状态

文件路由用目录表达URL结构，但嵌套渲染、参数校验、导航控制和部署仍需设计。约定减少样板，不会自动补全业务边界。

## 一、本章目录

- [静态、动态与捕获路由](#k01)
- [参数、query 与数据加载](#k02)
- [布局、页面元数据与导航middleware](#k03)
- [验证、错误与可恢复导航](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 静态、动态与捕获路由

| 文件示意 | 路由意图 |
| --- | --- |
| app/pages/index.vue | / |
| app/pages/users/index.vue | /users |
| app/pages/users/[id].vue | /users/:id |
| app/pages/search/[[query]].vue | 可选参数示意，按实际路由生成规则核对 |
| app/pages/[...slug].vue | 捕获剩余路径 |

文件名、目录与Nuxt版本决定具体生成关系；不要仅凭文件放进同名目录就假设父页面内容自动出现。嵌套父页面需要NuxtPage等出口，layouts则是另一层页面外壳。

静态路由、动态路由和catch-all的匹配优先级与404页面应通过生成路由和实际直达测试确认。

<a id="k02"></a>

### 2. 参数、query 与数据加载

```vue
<!-- nuxt-only: app/pages/users/[id].vue -->
<script setup lang="ts">
const route = useRoute()
const id = computed(() => String(route.params.id ?? ''))
const { data, error } = await useFetch(() => '/api/users/' + encodeURIComponent(id.value))
</script>
<template>
  <p v-if="error">加载失败</p>
  <pre v-else>{{ data }}</pre>
</template>
```

示例只演示路由来源与请求联动，真实业务还需校验id格式、接口schema、权限及错误分类。参数变化可能复用页面实例，不能只依赖mounted加载一次；响应式请求来源与取消策略要配合。

<a id="k03"></a>

### 3. 布局、页面元数据与导航middleware

layouts表达页面外壳，definePageMeta可声明页面布局、middleware等元信息。命名或全局route middleware控制导航流程，可等待登录信息并返回重定向，需避免登录页循环跳转。

SSR与客户端导航可能触发不同执行环境，middleware中不要无条件使用浏览器API或把副作用重复执行。服务端API仍必须独立鉴权，隐藏页面或改meta不构成安全控制。

<a id="k04"></a>

### 4. 验证、错误与可恢复导航

参数不合法时尽早返回明确404或校验错误，不要拼出任意内部请求。路由校验、错误页和API错误的职责不同，用户需要知道是页面不存在、无权限还是临时失败。

滚动恢复、锚点、加载占位和取消交互影响体验；深链接、刷新、后退和快速参数切换都应测试。动态导入页面的chunk失效也要有恢复策略，不能无限刷新。

<a id="summary"></a>

## 三、知识小结

文件结构表达路径，NuxtPage承接嵌套，layout承接外壳，参数驱动数据，middleware控制导航。运行环境、验证和服务器授权仍由应用明确。

参考：[Nuxt 4 Documentation](https://nuxt.com/docs/4.x/getting-started/introduction)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="build03-01"></a>

### BUILD03-01 [P0·基础] 文件路由相比手写路由有什么价值？

**回答：** 以约定连接URL和页面模块，减少重复记录并提供清晰页面边界，也便于框架分包。但嵌套出口、参数验证、错误和权限仍需实现。

对应讲解：[静态、动态与捕获路由](#k01)。

<a id="build03-02"></a>

### BUILD03-02 [P1·原理] 参数改变为何页面数据可能没更新？

**回答：** 页面实例可能复用，挂载钩子不一定重跑。应以具体参数的响应式来源触发数据更新，并处理旧请求失效，而不是只在初次mounted请求。

对应讲解：[参数、query 与数据加载](#k02)。

<a id="build03-03"></a>

### BUILD03-03 [P0·工程取舍] 路由middleware能代替API鉴权吗？

**回答：** 不能，它控制页面导航，用户仍可直接请求API。服务器必须验证身份、资源和操作权限，SSR与客户端导航也要避免重复副作用。

对应讲解：[布局、页面元数据与导航middleware](#k03)。

<a id="build03-04"></a>

### BUILD03-04 [P1·工程取舍] 路由上线前要测哪些路径？

**回答：** 直接打开、刷新、回退、参数变化、非法参数、无权限、快速切换和chunk加载失败。只从首页点一次链接不能覆盖部署和复用问题。

对应讲解：[验证、错误与可恢复导航](#k04)。
