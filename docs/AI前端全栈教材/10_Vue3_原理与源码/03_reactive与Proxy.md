# 03 reactive与Proxy

reactive主要解决对象访问的响应式入口和身份缓存。沿创建代理、get、set、delete/枚举四条路径阅读，才能解释新增字段、receiver和数组行为。

源码基线：**Vue v3.5.43**。区分公开行为、这个版本的内部实现与本文教学模型；代码块各自独立，使用 Vue 包的例子在安装相应版本的 Node ESM 或前端工程中运行。

## 一、本章目录

- [创建代理与身份缓存](#k01)
- [get 与 receiver](#k02)
- [set、delete、has 与 ownKeys](#k03)
- [raw、readonly、浅层与排错](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 创建代理与身份缓存

```js
import { reactive, toRaw, markRaw } from 'vue';
const raw = { nested: { count: 1 } };
const a = reactive(raw), b = reactive(raw);
console.log(a === b, a === raw, toRaw(a) === raw); // true false true
console.log(a.nested === raw.nested); // false
const external = markRaw({ token: 1 });
console.log(reactive(external) === external); // true
```

[createReactiveObject](https://github.com/vuejs/core/blob/v3.5.43/packages/reactivity/src/reactive.ts)按非对象、已有代理、SKIP/不可扩展、缓存及目标类别处理，再选择普通对象handler或集合handler。WeakMap缓存让同一模式下原对象复用相应代理，不等于所有readonly/shallow变体都是同一个代理。深层代理常在读取嵌套对象时建立，而不是必须初始化时递归改写所有属性。

<a id="k02"></a>

### 2. get 与 receiver

普通get取得值，过滤不应追踪的内部入口，按可变/只读模式追踪，再处理浅层、ref解包和嵌套代理等规则。[BaseReactiveHandler.get](https://github.com/vuejs/core/blob/v3.5.43/packages/reactivity/src/baseHandlers.ts)是主入口；Reflect.get保留本次接收者语义，具体对ref目标还有版本内的特殊处理。

```js
const reads = [];
const raw = { n: 2, get double() { return this.n * 2; } };
const proxy = new Proxy(raw, {
  get(target, key, receiver) {
    reads.push(key);
    return Reflect.get(target, key, receiver);
  }
});
console.log(proxy.double, reads); // 4 ['double','n']
```

若改成target[key]，getter以raw为this读取n，内部访问绕过代理。proxy不只是“在外面多打一行日志”，接收者决定后续操作从哪里经过。

<a id="k03"></a>

### 3. set、delete、has 与 ownKeys

set需要区分旧键是否存在、旧新值是否变化、写入是否成功及receiver对应的真实目标，再通知ADD或SET等相关依赖。错误地对所有set都触发会产生重复更新或反馈循环。

deleteProperty只在成功删除已有属性时通知；has供in操作依赖；ownKeys让Object.keys等枚举可对键集合变化反应。新增与删除不只是影响某键值，也影响“有哪些键”。

数组添加索引、截短length、迭代与includes等不能仅复用普通对象单键逻辑，框架有数组方法适配和长度/迭代依赖。Map/Set则有集合handler，原生内部槽和迭代行为需专门处理。

<a id="k04"></a>

### 4. raw、readonly、浅层与排错

直接改raw通常绕过代理通知；解构原始值属性保存快照；shallowReactive只处理根层，不保证内部对象更新；readonly限制该访问视图，不阻止外部原对象变化。

markRaw适合不应代理的外部实例，但嵌套对象另行进入响应式图时可能产生身份差异。不可配置/不可写属性和不变量仍由JS约束，Vue不能随意返回违背Proxy不变量的值。

源码阅读按[创建/缓存](https://github.com/vuejs/core/blob/v3.5.43/packages/reactivity/src/reactive.ts)→[读写拦截](https://github.com/vuejs/core/blob/v3.5.43/packages/reactivity/src/baseHandlers.ts)→[track/trigger](https://github.com/vuejs/core/blob/v3.5.43/packages/reactivity/src/dep.ts)。排错先看实际读写路径，再查调度。

<a id="summary"></a>

## 三、知识小结

创建时管理代理身份，读取时保留receiver并收集相关关系，写入按真实变化和操作类型通知。数组/集合/浅层/raw有自己的边界，不能只靠一个get/set万能模型。

<a id="interview"></a>

## 四、面试题与答案

<a id="vsr03-01"></a>

### VSR03-01 [P0·原理] reactive重复调用为什么通常返回相同代理？

**回答：** 对应模式用WeakMap缓存原对象到代理，避免每次创建新身份。raw与proxy仍不同，readonly和shallow等变体的缓存也有区别。

对应讲解：[创建代理与身份缓存](#k01)。

<a id="vsr03-02"></a>

### VSR03-02 [P0·原理] Reflect.get的receiver如何影响依赖追踪？

**回答：** 它决定访问器getter的this，保留代理接收者可让getter内部属性读取继续被拦截；target[key]可能改用raw而漏记内部读取。

对应讲解：[get 与 receiver](#k02)。

<a id="vsr03-03"></a>

### VSR03-03 [P0·原理] 新增字段为何可能让Object.keys相关计算更新？

**回答：** 枚举依赖键集合，ADD/DELETE要通知相应迭代依赖，而非只通知读取该键值的订阅者。has、ownKeys与get属于不同操作入口。

对应讲解：[set、delete、has 与 ownKeys](#k03)。

<a id="vsr03-04"></a>

### VSR03-04 [P1·排查] 对象改了却没更新应先看什么？

**回答：** 确认是否改raw、解构为原始快照、处于shallow/markRaw范围、写入失败或观察已停止，再看是否尚未flush。先定位访问路径比盲加nextTick有效。

对应讲解：[raw、readonly、浅层与排错](#k04)。
