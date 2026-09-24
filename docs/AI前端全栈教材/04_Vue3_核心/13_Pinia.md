# 13 Pinia 与状态归属

Pinia组织共享客户端状态、派生值和业务动作。它的价值来自明确数据所有者和更新入口，不是把所有页面变量都搬到一个Store。

## 一、本章目录

- [Options Store 与 Setup Store](#k01)
- [读取、解构与更新 API](#k02)
- [本地、共享、URL 与服务器状态](#k03)
- [SSR、持久化与调试](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. Options Store 与 Setup Store

Options Store按state/getters/actions组织，Setup Store用ref/computed/function组合，返回的成员形成公开Store接口。两者都需要稳定ID，不应为每次组件渲染随意生成不同Store。

```ts
import { createPinia, defineStore } from 'pinia';
import { ref, computed } from 'vue';
const useCounter = defineStore('counter-demo', () => {
  const count = ref(0);
  const doubled = computed(() => count.value * 2);
  function increment() { count.value++; }
  return { count, doubled, increment };
});
const store = useCounter(createPinia());
store.increment();
console.log(store.count, store.doubled); // 1 2
```

示例显式创建独立Pinia。SSR应用应按请求创建应用/Store环境，避免模块单例共享用户状态。

<a id="k02"></a>

### 2. 读取、解构与更新 API

store上的state/getter读取通常已解包，直接解构可能丢失关联，storeToRefs用于取得保持响应式的state/getter ref；actions是方法契约，可按Pinia支持方式解构调用。

$patch支持对象或函数批量变更，$subscribe订阅状态，$onAction观察动作前后和错误；订阅是否随组件结束或detached由使用方式决定，应保存取消函数。

Options Store有$reset等支持，Setup Store通常需要自己实现对应重置逻辑。持久化和插件也不是自动正确，应定义序列化字段、版本和恢复验证。

<a id="k03"></a>

### 3. 本地、共享、URL 与服务器状态

弹窗开关或输入暂存可留组件本地；跨页面共享的客户端选择可用Store；筛选和分页若需要分享/刷新恢复，URL可能更合适；服务器数据还需要加载、缓存、重试、失效和权限边界。

不要把同一份服务器数据在多个Store与组件中各复制一份再手工同步。可以将查询缓存或服务层作为权威读取入口，Store负责必要客户端状态。

actions可异步执行业务，但应处理取消、竞态和错误，不能因为写在Store里就忽略requestId和生命周期。

<a id="k04"></a>

### 4. SSR、持久化与调试

SSR序列化状态到客户端时，不应包含密钥、内部权限依据或不可安全序列化对象，注入HTML还要防转义问题。客户端恢复的状态仍可能陈旧或被篡改，后端保持权威授权。

持久化插件不等于数据库事务，存储可能失败、版本可能不兼容，退出登录应清理相关用户状态。多标签同步要定义冲突与过期行为。

调试时追踪哪个action或patch改变了数据、数据来源是否重复、订阅是否残留，再看响应式或组件更新，不用全局watch深监听所有Store弥补设计。

<a id="summary"></a>

## 三、知识小结

Store先划清状态类型，再定义state/getter/action与重置、持久化和订阅生命周期。SSR按请求隔离，服务器数据与权限不由前端Store决定。

参考：[Pinia Core Concepts](https://pinia.vuejs.org/core-concepts/)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="vue13-01"></a>

### VUE13-01 [P0·基础] Pinia与传统Vuex有哪些主要API差异？

**回答：** Pinia围绕state/getters/actions，不强制独立mutations层，支持Setup Store并有自然类型推断。选择时仍要关注数据归属、插件和迁移，而不是认为换库自动改善结构。

对应讲解：[Options Store 与 Setup Store](#k01)。

<a id="vue13-02"></a>

### VUE13-02 [P0·原理] 为什么解构Store状态要用storeToRefs？

**回答：** 直接取出已解包的值可能失去后续关联，storeToRefs提供与原Store关联的ref；actions与状态的读取机制不同，不应一概用同样方式处理。

对应讲解：[读取、解构与更新 API](#k02)。

<a id="vue13-03"></a>

### VUE13-03 [P1·工程取舍] 是否所有接口数据都该放Pinia？

**回答：** 不必。服务器数据有缓存、失效和权限契约，本地UI与URL状态也各有适合位置。避免多份数据副本需要人工同步，Store只承担明确共享状态。

对应讲解：[本地、共享、URL 与服务器状态](#k03)。

<a id="vue13-04"></a>

### VUE13-04 [P1·原理] SSR为什么要按请求创建Pinia环境？

**回答：** 服务端模块可跨请求复用，共享可变用户状态会串用户。每请求隔离并只序列化允许公开的状态，客户端恢复也不能替代后端授权。

对应讲解：[SSR、持久化与调试](#k04)。
