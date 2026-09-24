# 03 Rendering、Reconciliation 与 Commit

组件函数执行不代表DOM已经更新。React把计算候选UI与提交宿主变化分开，这决定了render必须纯、key必须稳定，以及副作用应放在什么位置。

## 一、本章目录

- [更新流水线](#k01)
- [批处理、优先级与快照](#k02)
- [身份由位置、类型与 key 协调](#k03)
- [memo、Context 与证据](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 更新流水线

```text
props/state/context或外部订阅更新
  → 安排工作
  → render计算候选树并协调身份
  → commit应用最终宿主变化
  → 按相应时机执行/清理Effects
```

render可能被重新执行、暂停或放弃，不能在其中完成不可撤销副作用。commit是接受结果并更新DOM或Native宿主的阶段，布局Effect等与提交时机有关；浏览器实际绘制又是另一层。

Fiber帮助保存和组织可调度工作，但不是给业务代码开多线程的API。具体内部字段和调度策略随版本演进，不应仅背数据结构名。

<a id="k02"></a>

### 2. 批处理、优先级与快照

React可批处理多个状态更新，函数式更新器描述按前一个队列结果计算。高优先级交互与非紧急更新可有不同调度策略，所谓并发渲染主要是协调可中断工作，不是同时执行多个JS组件线程。

同值更新是否跳过和组件是否再次执行有上下文与优化边界，不能把一次日志数量当永久渲染保证。开发StrictMode额外检查纯度和Effect清理，生产行为不应简单按开发日志次数推断。

flushSync可强制某些同步刷新需求，但会影响调度与性能，只在明确集成场景采用，不用来普遍修复状态理解问题。

<a id="k03"></a>

### 3. 身份由位置、类型与 key 协调

同一位置上相同组件类型和合适key通常保留状态；改变类型或key可导致重建。列表key在同级中稳定唯一，帮助重排后把状态归还给正确业务项。

```tsx
type Item = { id: string; name: string };
export function Users({ items }: { items: Item[] }) {
  return <ul>{items.map(item => <li key={item.id}>{item.name}</li>)}</ul>;
}
```

随机key会反复重建，index在可重排列表中可能错复用。把组件定义在另一个组件函数内部还可能每次产生新的类型身份，应优先在稳定模块作用域声明组件。

<a id="k04"></a>

### 4. memo、Context 与证据

memo可在props比较满足条件时跳过某些重渲染，但自身state、Context更新和其他规则仍会触发。对象/函数每次新建会影响浅比较，自定义比较器若漏字段可导致陈旧UI。

性能分析区分组件函数执行时间、提交次数、DOM量与布局绘制，不要只看console.log有没有出现。Profiler和浏览器Performance互补，优化后验证交互、列表身份和状态正确性。

<a id="summary"></a>

## 三、知识小结

render计算候选UI，commit提交宿主变化，Effects处理同步。状态身份看位置/类型/key，优化看实际比较和更新来源，不能把所有函数执行都等同DOM重建。

参考：[React Learn](https://react.dev/learn)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="react03-01"></a>

### REACT03-01 [P0·原理] Render和Commit区别是什么？

**回答：** Render计算候选UI并协调树，可以重复或放弃；Commit将接受的变化应用到宿主。render中的日志或函数执行不证明DOM已更新，因此副作用不能随意放在render。

对应讲解：[更新流水线](#k01)。

<a id="react03-02"></a>

### REACT03-02 [P0·原理] key为什么影响组件内部state？

**回答：** key参与同级身份协调，稳定key让状态随业务项复用，变化key可能重建。index随位置变化，重排时可能把旧状态交给另一条记录。

对应讲解：[身份由位置、类型与 key 协调](#k03)。

<a id="react03-03"></a>

### REACT03-03 [P1·原理] 并发渲染是多个线程同时跑组件吗？

**回答：** 通常不是，它强调调度、优先级和可中断的候选计算。JS同步计算仍会占线程，真正跨线程需要Worker等能力。

对应讲解：[批处理、优先级与快照](#k02)。

<a id="react03-04"></a>

### REACT03-04 [P1·工程取舍] memo组件为什么还会更新？

**回答：** memo主要关注特定props比较，不屏蔽自身state或Context等更新。还要检查传入引用是否稳定，不能把memo当永不执行的缓存。

对应讲解：[memo、Context 与证据](#k04)。
