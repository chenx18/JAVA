# 06 React 状态归属、Context 与外部 Store

先按生命周期与权威来源划分状态，再选库。组件本地、跨层上下文、服务器缓存和URL状态的职责不同，混在同一个全局对象里会增加同步成本。

## 一、本章目录

- [状态分类与最小来源](#k01)
- [Context 的用途与更新范围](#k02)
- [Reducer 与外部 Store](#k03)
- [服务器缓存、持久化与SSR](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 状态分类与最小来源

本地UI状态如临时展开项可用useState，共享编辑流程可提升到共同父级，复杂事件转换可用useReducer，搜索和分页若要分享恢复可放URL，服务器数据则需要请求和缓存失效机制。

能从已有输入计算的值通常不要另存state再Effect同步。表单draft可以独立于服务器数据，但要明确重置、保存、冲突和取消时机。

选择全局Store前先问谁写、谁读、保存多久、刷新后怎么办，以及哪一份是权威数据。

<a id="k02"></a>

### 2. Context 的用途与更新范围

```tsx
import { createContext, useContext } from 'react';
const ThemeContext = createContext<'light' | 'dark'>('light');
export function ThemeLabel() {
  const theme = useContext(ThemeContext);
  return <span>{theme}</span>;
}
export function ThemeRoot() {
  return <ThemeContext value="dark"><ThemeLabel /></ThemeContext>;
}
```

Context适合主题、服务和跨层共享值。Provider值变化可影响消费者，外层memo不自动屏蔽所读Context变化。把所有高频业务字段放一个大Context，会扩大更新范围。

可按职责拆Context、保持必要引用稳定或采用选择器型外部Store；但不要为避免一次render过度拆分到无法理解。

<a id="k03"></a>

### 3. Reducer 与外部 Store

reducer是纯函数，按state+action返回下一状态，适合有明确事件和转移的逻辑。Redux强调可追踪更新与中间件等生态，Zustand等提供另一种外部Store组织方式；库选择应依据状态模型、团队调试和SSR需求。

useSyncExternalStore提供React读取外部Store的订阅和快照契约，subscribe应返回取消函数，getSnapshot在未变化时应保持一致结果，SSR还需相应server snapshot。

随意在render里读一个可变全局变量，或只用useEffect订阅后强制刷新，可能在并发与服务端场景中产生不一致。

<a id="k04"></a>

### 4. 服务器缓存、持久化与SSR

服务器状态工具关注请求去重、陈旧时间、失效、重试和乐观更新；它不是仅把fetch结果放全局Map。缓存键要包含影响结果的参数与用户范围，退出登录清理对应数据。

持久化到浏览器需版本、验证、容量和失败恢复，不能包含长期秘密。SSR共享模块状态可能跨用户，按请求建立隔离实例并安全序列化允许数据。

状态测试应覆盖事件转移、订阅取消、重复请求、用户切换、恢复旧版本和数据冲突，而不只看一个setter执行。

<a id="summary"></a>

## 三、知识小结

状态按所有者与生命周期分类；Context传上下文，Reducer组织转换，外部Store遵守快照订阅，服务器缓存管理远端数据。减少重复来源比更换库更重要。

参考：[React Documentation](https://react.dev/)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="react06-01"></a>

### REACT06-01 [P0·工程取舍] 什么时候用Context而不是全局状态库？

**回答：** 稳定跨层上下文如主题或依赖服务适合Context；高频复杂共享状态可能需要更细订阅、选择器和调试能力。先看状态归属和变化范围，再选工具。

对应讲解：[Context 的用途与更新范围](#k02)。

<a id="react06-02"></a>

### REACT06-02 [P1·原理] memo子组件为什么仍因Context变化更新？

**回答：** memo主要比较特定props，组件实际读取的Context是另一更新来源。Provider值改变会影响相应消费者，需按职责拆分或使用合适订阅结构。

对应讲解：[Context 的用途与更新范围](#k02)。

<a id="react06-03"></a>

### REACT06-03 [P1·原理] useSyncExternalStore解决什么问题？

**回答：** 它规定React与外部可变Store之间的订阅、稳定快照和SSR快照契约，让并发读取更一致。不是简单强制render的快捷键，getSnapshot与取消逻辑必须正确。

对应讲解：[Reducer 与外部 Store](#k03)。

<a id="react06-04"></a>

### REACT06-04 [P1·工程取舍] 服务器数据能否只用useState长期保存？

**回答：** 局部简单请求可以，但跨页面复用、失效、重试和用户切换会增加管理需求。应明确缓存和权威来源，不在各组件维护多份无协调副本。

对应讲解：[服务器缓存、持久化与SSR](#k04)。

<a id="react06-05"></a>

### REACT06-05 [P0·工程取舍] 为什么先分类状态再选Store？

**回答：** 本地UI、URL、服务器缓存和共享客户端状态的所有者与生命周期不同。先明确谁写、谁读和哪里权威，能减少重复数据和同步Effect；换库本身不解决状态归属。

对应讲解：[状态分类与最小来源](#k01)。
