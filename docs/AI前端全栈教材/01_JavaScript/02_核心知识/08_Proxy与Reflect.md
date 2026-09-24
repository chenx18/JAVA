# 08 Proxy与Reflect

Proxy重新定义对象操作的入口，Reflect提供对应的标准转发方式。理解receiver和依赖关系后，才能解释响应式系统，而不只是背get收集、set触发。

## 一、本章目录

- [代理与原对象](#k01)
- [13个 trap 与 Reflect 对照](#k02)
- [receiver 与 getter 的 this](#k03)
- [响应式依赖与清理](#k04)
- [不变量、内部槽与撤销](#k05)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 代理与原对象

```js
const target = { count: 0 };
const seen = [];
const proxy = new Proxy(target, {
  get(target, key, receiver) {
    seen.push(key);
    return Reflect.get(target, key, receiver);
  }
});
console.log(proxy === target); // false
console.log(proxy.count, target.count, seen); // 0 0 ['count']
```

代理不是克隆，也不会影响所有直接针对target的操作。proxy.count经过get，target.count可绕过代理；身份比较也不同。递归代理嵌套对象需要另行实现，常用WeakMap缓存原对象到代理以保持身份稳定。

<a id="k02"></a>

### 2. 13个 trap 与 Reflect 对照

| 操作 | trap/Reflect方法 |
| --- | --- |
| 属性读取/写入 | get / set |
| in检查/删除 | has / deleteProperty |
| 定义属性/取描述符 | defineProperty / getOwnPropertyDescriptor |
| 自有键列表 | ownKeys |
| 读/写原型 | getPrototypeOf / setPrototypeOf |
| 是否可扩展/禁止扩展 | isExtensible / preventExtensions |
| 函数普通/构造调用 | apply / construct |

Reflect是静态工具对象，不是构造器。Reflect.get(target,key,receiver?)、set(target,key,value,receiver?)保留接收者语义；set/defineProperty等通常用Boolean表达一般操作是否成功，但非法参数或不变量等仍可抛错。

ownKeys只提供键列表，Object.keys还会检查属性描述符与枚举性，不是同一层操作。不能通过apply trap让不可调用对象变成函数，construct目标也必须有构造能力。

<a id="k03"></a>

### 3. receiver 与 getter 的 this

```js
const accessed = [];
const target = {
  value: 2,
  get doubled() { return this.value * 2; }
};
const proxy = new Proxy(target, {
  get(target, key, receiver) {
    accessed.push(key);
    return Reflect.get(target, key, receiver);
  }
});
console.log(proxy.doubled, accessed); // 4 ['doubled','value']
```

读取doubled→进入get→Reflect用receiver作为getter的this→getter内部读proxy.value→再次经过代理。如果改成target[key]，getter的this变成target，value读取绕过代理，响应式依赖可能漏收集。

receiver还会影响原型链访问与写入落点，因此不是一个仅为Vue准备的额外参数，而是对象访问语义的一部分。

<a id="k04"></a>

### 4. 响应式依赖与清理

最小依赖图通常是WeakMap<目标,Map<键,Set<effect>>>。effect运行时标记当前函数；get把当前函数登记到目标键；set成功且值变化后重新调度对应函数。

为什么不能只有全局Set？不同对象的同名属性必须隔离。为什么要清理旧依赖？条件分支变化后，effect可能不再读取原属性。为什么触发时取快照？运行会删除并重建订阅，不应一边遍历原集合一边无限重新加入。

文末的可运行模型支持浅层普通数据属性、条件清理与stop，不覆盖深层代理、删除/枚举、数组、Map、computed、异步调度或框架完整错误策略。

<a id="k05"></a>

### 5. 不变量、内部槽与撤销

目标对象上不可配置且不可写的数据属性，get不能随意谎报不一致值；不可扩展对象的ownKeys/原型等也有一致性限制。不变量约束代理必须尊重目标的某些事实，违反会抛TypeError。

Map等方法依赖内部槽，class私有访问依赖品牌，proxy作为this未必满足这些条件。不能认为一个空handler就对所有对象完全透明。

Proxy.revocable(target,handler)返回proxy和revoke；撤销后代理操作通常抛TypeError。它提供代理生命周期控制，不是后端权限或安全隔离机制。

<a id="summary"></a>

## 三、知识小结

Proxy负责拦截，Reflect表达默认操作，receiver保留访问器接收者。响应式需要依赖图、清理和调度，不变量及内建对象边界决定代理能做到什么。

参考：[MDN Proxy](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Proxy)；[MDN Reflect](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Reflect)。较新 API 按目标运行时核对支持，示例各自独立。

<a id="interview"></a>

## 四、面试题与答案

<a id="c08-01"></a>

### C08-01 [P1·原理] Proxy与defineProperty的区别是什么？

**回答：** defineProperty在具体属性上设置描述符，Proxy代理对象操作，可拦截读取、删除、枚举、调用等更广操作。二者都不会自动成为完整深层响应式系统，Proxy还有独立身份和不变量限制。

对应讲解：[13个 trap 与 Reflect 对照](#k02)。

<a id="c08-02"></a>

### C08-02 [P1·原理] get里为何用Reflect.get而不是target[key]？

**回答：** Reflect.get可把原receiver作为getter的this保留，getter内部继续读取属性时仍可能经过代理。直接target[key]改变接收者，可能绕过代理而漏收集依赖，也会影响原型访问语义。

对应讲解：[receiver 与 getter 的 this](#k03)。

<a id="c08-03"></a>

### C08-03 [P1·编码] 实现一个可解释的浅层reactive/effect。

**回答：** 下例按对象和键收集依赖，每次运行清理旧订阅，嵌套执行后恢复active，通知时使用快照，并提供stop。只演示普通浅层数据属性；不是Vue源码替代，也未实现调度队列、集合和完整重入控制。

```js
const dependencies = new WeakMap();
let active = null;
function effect(fn) {
  function clean() {
    for (const set of run.deps) set.delete(run);
    run.deps.clear();
  }
  let stopped = false;
  function run() {
    if (stopped) return;
    clean();
    const previous = active;
    active = run;
    try { fn(); } finally { active = previous; }
  }
  run.deps = new Set();
  run.stop = () => { stopped = true; clean(); };
  run();
  return run;
}
function reactive(target) {
  return new Proxy(target, {
    get(target, key, receiver) {
      if (active) {
        let byKey = dependencies.get(target);
        if (!byKey) dependencies.set(target, byKey = new Map());
        let set = byKey.get(key);
        if (!set) byKey.set(key, set = new Set());
        set.add(active);
        active.deps.add(set);
      }
      return Reflect.get(target, key, receiver);
    },
    set(target, key, value, receiver) {
      const before = target[key];
      const ok = Reflect.set(target, key, value, receiver);
      if (ok && !Object.is(before, value)) {
        for (const run of new Set(dependencies.get(target)?.get(key) ?? [])) {
          if (run !== active) run();
        }
      }
      return ok;
    }
  });
}
const state = reactive({ enabled: true, count: 0 });
const output = [];
const runner = effect(() => output.push(state.enabled ? state.count : 'off'));
state.count = 1;
state.enabled = false;
state.count = 2;
runner.stop();
console.log(output); // [0,1,'off']
```

**追问与回答：** 验证不同目标不串依赖、分支关闭后取消旧订阅、stop后不再触发、失败的写入不触发。

对应讲解：[响应式依赖与清理](#k04)。

<a id="c08-04"></a>

### C08-04 [P2·原理] 为什么不能把任何对象都透明代理？

**回答：** 内建方法可能需要目标的内部槽，私有字段检查this品牌，代理对象不一定满足。同时get/ownKeys等必须遵守不可写、不可配置、不可扩展目标的不变量。需要按对象类别设计适配策略。

对应讲解：[不变量、内部槽与撤销](#k05)。
