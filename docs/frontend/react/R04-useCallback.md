# useCallback

> 专题：React 专题  
> 编号：R04

### 一句话答案

`useCallback` 用于缓存函数引用，常配合 memo 子组件减少不必要渲染。

### 核心原理

```text
依赖不变，返回同一个函数引用
依赖变化，返回新函数
```

### 项目里怎么用

- 传给 React.memo 子组件的事件。
- 依赖函数引用的 useEffect。
- 长列表 item 回调优化。

### 常见坑

- 不配合 memo，单独 useCallback 意义有限。
- 依赖项漏写导致闭包旧值。
- 到处包 useCallback 让代码变复杂。

### 面试表达

useCallback 主要优化函数引用稳定性。只有当子组件依赖引用比较，或者 effect 依赖函数时，它才更有价值。
