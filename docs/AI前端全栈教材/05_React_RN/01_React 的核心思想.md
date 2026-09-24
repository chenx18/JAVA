# 01 React 的组件、快照与单向数据流

React函数组件根据当次props和state计算UI描述。状态更新请求下一次计算，提交阶段再改变宿主界面；把这三个时刻分开，才能理解闭包和不可变更新。

## 一、本章目录

- [JSX 与纯渲染](#k01)
- [state 是一次 render 的快照](#k02)
- [不可变更新与身份](#k03)
- [条件、列表与状态归属](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. JSX 与纯渲染

```tsx
import { useState } from 'react';
export function Counter() {
  const [count, setCount] = useState(0);
  return <button type="button" onClick={() => setCount(n => n + 1)}>
    次数 {count}
  </button>;
}
```

JSX会被构建工具转成元素描述，不是HTML字符串。组件函数应根据输入计算输出，避免在render中发请求、写外部变量或修改DOM，因为render可能重复执行或被放弃。

组件以大写名称区分宿主标签，props是输入契约，children表达组合。文本插值有默认转义，但dangerouslySetInnerHTML等入口仍需可信内容处理，不代表所有URL和业务操作都自动安全。

<a id="k02"></a>

### 2. state 是一次 render 的快照

setState安排更新，不会立刻修改当前调用闭包中的state变量。同一个事件里连续setCount(count+1)多次，可能都依据同一旧快照；函数式更新根据队列前一结果递推。

```tsx
import { useState } from 'react';
export function AddThree() {
  const [count, setCount] = useState(0);
  function add() {
    setCount(n => n + 1);
    setCount(n => n + 1);
    setCount(n => n + 1);
  }
  return <button type="button" onClick={add}>{count}</button>;
}
```

函数式更新器应保持纯，开发检查可能额外调用它来发现副作用。不要把“setState异步”背成精确队列时序的全部解释，更应指出当前快照不变和更新如何组合。

<a id="k03"></a>

### 3. 不可变更新与身份

对对象/数组state建立新外层和需要变化的路径，使新旧快照保持语义并利于引用比较。直接改原对象再set同一引用，可能不触发预期更新，也会篡改过去快照。

```ts
type User = { id: string; profile: { name: string } };
function rename(user: User, name: string): User {
  return { ...user, profile: { ...user.profile, name } };
}
const old = { id: '1', profile: { name: 'A' } };
const next = rename(old, 'B');
console.log(old.profile.name, next.profile.name); // A B
```

不可变更新不等于每次深拷贝全部状态。只复制变化路径并复用未变结构，通常更易维护和更省成本；内部本地临时变量也不是都禁止修改。

<a id="k04"></a>

### 4. 条件、列表与状态归属

条件渲染使用普通JS表达式，注意0&&<Component/>可能渲染0。列表key在同级中表达稳定身份，插入删除时不宜随意用index；随机key会破坏复用。

共享状态提升到合适共同父级，局部UI保留本地，复杂更新可用reducer。不要在不同组件各存同一份可推导状态再靠Effect同步。

默认React组件事件、DOM事件和服务端业务授权是不同层次；UI禁用只改善交互，后端仍要校验实际操作。

<a id="summary"></a>

## 三、知识小结

React主线是输入快照→纯render→commit。更新器描述下一状态，不修改当前闭包；不可变路径表达变化，状态归属和稳定key保持正确复用。

参考：[React Learn](https://react.dev/learn)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="react01-01"></a>

### REACT01-01 [P0·原理] 为什么setState后立刻读取还是旧值？

**回答：** 当前函数闭包属于本次render快照，更新安排后续render，不原地替换这个局部绑定。需要基于前值递推时使用函数式更新，而不是猜什么时候日志会变新。

对应讲解：[state 是一次 render 的快照](#k02)。

<a id="react01-02"></a>

### REACT01-02 [P0·原理] 为什么React强调不可变更新？

**回答：** 新引用表达变化并保留旧快照语义，便于更新边界和memo判断。直接改共享对象会污染旧快照，set同引用也可能被视为无变化；通常只复制变化路径即可。

对应讲解：[不可变更新与身份](#k03)。

<a id="react01-03"></a>

### REACT01-03 [P1·基础] JSX是不是HTML字符串？

**回答：** 不是，它被编译成React元素描述，组件通过这些描述计算宿主UI。渲染时机和副作用仍受React规则约束，不能在render中任意操作外部系统。

对应讲解：[JSX 与纯渲染](#k01)。

<a id="react01-04"></a>

### REACT01-04 [P0·工程取舍] 多个组件需要同一状态应如何处理？

**回答：** 找到合适共同所有者并下传数据/事件，或在确实跨层时使用Context/Store。避免重复保存可推导副本，再用Effect互相同步制造竞态。

对应讲解：[条件、列表与状态归属](#k04)。
