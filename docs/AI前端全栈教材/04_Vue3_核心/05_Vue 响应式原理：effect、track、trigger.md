# 05 Vue 响应式原理：effect、track 与 trigger

响应式的核心是记录“哪段计算读了哪个数据”，再在变化时使相关计算失效或重跑。下面用依赖图解释机制，具体Vue版本内部的数据结构与优化实现可能变化。

## 一、本章目录

- [依赖图与当前 effect](#k01)
- [动态分支与依赖清理](#k02)
- [读写之外的操作与集合](#k03)
- [调度、作用域与调试](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 依赖图与当前 effect

```text
执行effect并标记当前订阅者
  → 读取target[key]或ref.value
  → track登记数据与effect的关系
修改数据
  → trigger找到受影响订阅者
  → 按类型使缓存失效或交给调度器
```

教学上可用`WeakMap<target,Map<key,Set<effect>>>`理解依赖图：按对象分开、按属性分开、对同一订阅去重。不能因此声称当前Vue源码所有内部关系都固定用这一份简单结构，版本可能采用更精细的链接与版本标记。

组件渲染、computed和watcher都能参与依赖关系，但它们的求值和调度语义不同。

<a id="k02"></a>

### 2. 动态分支与依赖清理

```ts
import { reactive, watchEffect } from 'vue';
const state = reactive({ enabled: true, count: 0 });
const values: Array<number | string> = [];
const stop = watchEffect(() => {
  values.push(state.enabled ? state.count : 'off');
}, { flush: 'sync' });
state.count = 1;
state.enabled = false;
state.count = 2;
console.log(values); // [0,1,'off']
stop();
```

enabled变false后，新的执行路径不再读count，旧count订阅应被清理；否则count变化仍触发无用计算。示例用sync展示顺序，普通组件更新仍有批处理。

嵌套effect需要恢复外层当前订阅者，异常路径也要清理或恢复状态；触发时直接遍历一个正在增删的集合还可能产生重入问题。

<a id="k03"></a>

### 3. 读写之外的操作与集合

依赖不只来自get。in检查、键枚举、数组length、新增/删除属性和Map/Set操作都可能影响不同计算。完整框架需要追踪操作类型和对应的迭代依赖，不能用只有get/set的十几行Proxy覆盖全部Vue行为。

Reflect.get的receiver保留getter中的this，使getter内部读取仍走正确路径；直接target[key]可能绕过依赖收集。集合方法依赖内部槽，也要专门处理代理调用关系。

比较旧新值、避免不必要通知和调度去重属于不同优化层次，不能把它们都说成“Proxy自动做到”。

<a id="k04"></a>

### 4. 调度、作用域与调试

trigger不必立即重做所有DOM。computed可能先标记失效，组件更新进入队列，watcher根据flush策略运行。effectScope/onScopeDispose等帮助将一组响应式副作用绑定到所有者，停止时移除订阅和清理资源。

开发环境的onTrack/onTrigger可辅助观察依赖，调试时从“读取是否发生、何时发生、读了谁、是否仍订阅”入手。异步函数await后的读取通常不再处于最初自动收集的同步阶段。

理解机制可参考JavaScript核心篇的浅层演示，但真实Vue还需处理数组、集合、嵌套和调度等语义，不能把教学模型当源码逐行替代。

<a id="summary"></a>

## 三、知识小结

依赖图记录读关系，分支变化清理旧关系，写操作定位影响，调度器决定何时执行，作用域决定何时结束。读清机制后再看具体版本源码优化。

参考：[Vue Reactivity in Depth](https://vuejs.org/guide/extras/reactivity-in-depth.html)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="vue05-01"></a>

### VUE05-01 [P0·原理] Vue如何知道数据变化后更新谁？

**回答：** 执行计算时记录当前effect，读取响应式入口将它登记到相应目标和属性关系中。修改时找到相关订阅者，再按computed、组件或watcher的规则失效或调度。

对应讲解：[依赖图与当前 effect](#k01)。

<a id="vue05-02"></a>

### VUE05-02 [P0·原理] 为什么需要清理旧依赖？

**回答：** 计算的条件分支可能变化，下一次执行不再读某字段。若旧订阅保留，会产生无关更新并延长引用生命周期；应让依赖反映最新执行路径。

对应讲解：[动态分支与依赖清理](#k02)。

<a id="vue05-03"></a>

### VUE05-03 [P1·原理] 只有Proxy的get/set就能完整实现Vue响应式吗？

**回答：** 不能，删除、枚举、in、数组长度和集合操作有不同依赖语义，还涉及receiver、缓存身份、调度和清理。简单模型只能解释限定范围。

对应讲解：[读写之外的操作与集合](#k03)。

<a id="vue05-04"></a>

### VUE05-04 [P1·工程取舍] 数据变了却没有副作用执行，如何排查？

**回答：** 确认读写路径、读取是否处于追踪阶段、是否解构快照或绕过raw、是否浅层边界、订阅是否停止及任务是否尚未flush。用开发调试钩子和最小复现验证，而非只猜Proxy失效。

对应讲解：[调度、作用域与调试](#k04)。
