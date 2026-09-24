# 02 React Hooks API 与使用边界

Hooks把状态、外部同步和其他React能力接入函数组件。先按用途选择，再遵守调用规则与依赖契约；不是每个Hook都用来减少渲染。

## 一、本章目录

- [调用规则与自定义 Hook](#k01)
- [状态与引用：useState、useReducer、useRef](#k02)
- [Effect、LayoutEffect 与清理](#k03)
- [Context、Memo、Callback 与 ID](#k04)
- [并发体验与外部 Store](#k05)
- [Actions 与新 Hook 的职责](#k06)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 调用规则与自定义 Hook

普通Hooks在组件或自定义Hook顶层调用，不能随条件和循环改变调用顺序。自定义Hook复用逻辑，不自动让多个调用共享同一state；共享需要明确的外部所有者。

特殊API use可在一些条件/循环位置读取Promise或Context，但有自身规则，不能把它的例外推广给useState/useEffect，也不在try/catch中随意吞掉Suspense过程。lint规则和React版本要配套。

Hook名称以use开头帮助规则检查，不能把普通任意函数改名后就获得生命周期管理。

<a id="k02"></a>

### 2. 状态与引用：useState、useReducer、useRef

useState返回值和setter，可用惰性初始化函数；useReducer把state+action的转换集中到纯reducer。两者更新都需按快照模型理解。

useRef返回跨render稳定的容器，改.current不触发render，适合DOM、timer或外部实例；UI依赖的数据不应只藏在ref里。不要随意在render中读写ref构建隐式状态流，初始化等允许模式也应保持可预测。

```tsx
import { useRef, useState } from 'react';
export function NameInput() {
  const input = useRef<HTMLInputElement | null>(null);
  const [name, setName] = useState('');
  return <>
    <input ref={input} aria-label="名称" value={name} onChange={e => setName(e.target.value)} />
    <button type="button" onClick={() => input.current?.focus()}>聚焦</button>
  </>;
}
```

<a id="k03"></a>

### 3. Effect、LayoutEffect 与清理

useEffect与外部系统同步，返回函数清理订阅、timer或旧请求。useLayoutEffect在commit后、浏览器绘制前的相关阶段同步执行，适合必要布局读取，但会阻塞绘制；不要为“更早”把所有Effect都换成它。

useEffect通常不阻塞绘制，但交互和调度条件会影响实际时机，不能承诺永远在屏幕绘制之后。useInsertionEffect主要供样式库插入等用途，普通业务不优先选择。

Effect不在服务器渲染阶段按客户端方式运行；只计算派生值通常直接render计算或memo，无需另存state再Effect同步。

<a id="k04"></a>

### 4. Context、Memo、Callback 与 ID

| Hook | 作用 | 关键边界 |
| --- | --- | --- |
| useContext | 读取最近Provider值 | 值变化可能使消费者更新，非任意细粒度选择器 |
| useMemo | 缓存计算结果的优化 | 不是业务正确性持久存储 |
| useCallback | 缓存函数身份 | 仍会捕获相应闭包，依赖必须正确 |
| useId | 生成可配合SSR的关联ID | 不用作列表业务key |
| useImperativeHandle | 定制暴露的ref接口 | 优先声明式契约，暴露少量命令 |
| useDebugValue | 自定义Hook调试标签 | 不是业务日志系统 |

memo组件与这些缓存需结合实际props稳定性使用，缓存多也有比较与维护成本。React Compiler在采用相应工具链时可能自动处理部分记忆优化，但升级React包不等于所有工程自动启用编译器。

<a id="k05"></a>

### 5. 并发体验与外部 Store

useTransition提供isPending和startTransition，将合适更新标成非紧急；useDeferredValue让某些值的消费滞后以保留输入响应。它们不自动减少网络请求，也不是防抖或后台线程。

当前文档要求某些await之后的状态更新再次包入startTransition才保持Transition标记；需跟随目标版本说明。受控文本输入的值应及时更新，不把输入本身全变成低优先级。

useSyncExternalStore用subscribe/getSnapshot以及SSR快照契约读取外部状态，避免随意用Effect手接Store时的并发一致性问题；快照必须按约定稳定。

<a id="k06"></a>

### 6. Actions 与新 Hook 的职责

useActionState管理Action结果与pending，useOptimistic表达乐观UI，React DOM的useFormStatus观察所属表单状态；失败恢复、权限和幂等仍由业务负责。

useEffectEvent用于在Effect相关逻辑中读取需要的最新值等特定场景，不能作为绕过依赖规则的万能入口，也不应随意传给子组件或普通事件回调。新Hook的可用性和具体限制按当前React/框架版本核对。

<a id="summary"></a>

## 三、知识小结

按用途记Hooks：状态、ref、外部同步、上下文、优化、并发体验和Action。调用位置、依赖、清理和快照契约比API数量更关键。

参考：[React Hooks Reference](https://react.dev/reference/react/hooks)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="react02-01"></a>

### REACT02-01 [P0·原理] useRef为什么不触发重新渲染？

**回答：** 它是跨render保存非渲染状态的可变容器，修改current不安排state更新。需要反映在UI的数据应使用state/reducer等机制，不能靠ref期待界面自动刷新。

对应讲解：[状态与引用：useState、useReducer、useRef](#k02)。

<a id="react02-02"></a>

### REACT02-02 [P0·原理] useEffect与useLayoutEffect怎么选？

**回答：** 普通外部同步优先Effect，确需在绘制前测布局并同步调整时才考虑LayoutEffect，因为它可阻塞绘制。两者都要清理，且不是派生数据计算的默认工具。

对应讲解：[Effect、LayoutEffect 与清理](#k03)。

<a id="react02-03"></a>

### REACT02-03 [P1·工程取舍] useMemo/useCallback要不要到处使用？

**回答：** 不应默认堆满。它们用于特定性能或引用契约，依赖管理和比较本身有成本，也不能修复错误数据流。先测瓶颈，再看组件memo和传参稳定性是否真正受益。

对应讲解：[Context、Memo、Callback 与 ID](#k04)。

<a id="react02-04"></a>

### REACT02-04 [P1·原理] Transition等于debounce或Worker吗？

**回答：** 都不等同。Transition标记更新优先级，DeferredValue延后消费，不能自动合并网络调用或把计算放到另一个线程。需要防抖、取消和Worker时仍应独立设计。

对应讲解：[并发体验与外部 Store](#k05)。

<a id="react02-05"></a>

### REACT02-05 [P1·原理] useId为什么不应当列表key？

**回答：** 它用于组件内部关联ID及SSR匹配，列表key需要来自业务项的稳定身份。两者服务不同边界，不能因为都叫ID就互换。

对应讲解：[Context、Memo、Callback 与 ID](#k04)。

<a id="react02-06"></a>

### REACT02-06 [P0·原理] 自定义Hook是否让多个组件共享同一份state？

**回答：** 不自动共享，每次Hook调用仍属于对应组件的状态与调用序列。共享需要共同父级、Context或外部Store；普通Hooks也必须遵守顶层调用规则，特殊use API的例外不能推广。

对应讲解：[调用规则与自定义 Hook](#k01)。

<a id="react02-07"></a>

### REACT02-07 [P1·工程取舍] useActionState、useOptimistic与useFormStatus分别管理什么？

**回答：** 分别帮助管理Action结果/pending、乐观显示及所属表单提交状态。它们服务UI流程，不替代服务端验证、幂等和失败回滚，使用时还要确认React/框架支持。

对应讲解：[Actions 与新 Hook 的职责](#k06)。
