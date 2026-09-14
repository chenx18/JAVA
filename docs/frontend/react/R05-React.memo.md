# React.memo

> 专题：React 专题  
> 编号：R05

### 一句话答案

`React.memo` 用于让组件在 props 没变化时跳过重新渲染。

### 核心原理

React 会浅比较新旧 props。如果没有变化，则复用上次渲染结果。

### 项目里怎么用

- 复杂子组件。
- 长列表项。
- 配合 useMemo/useCallback 保持 props 引用稳定。

### 常见坑

- props 里每次传新对象或新函数，memo 失效。
- 组件本身渲染很轻，memo 收益不大。
- 忽略 context 变化仍会触发更新。

### 面试表达

memo 适合渲染成本较高且 props 相对稳定的组件。它不是越多越好，通常要结合性能分析使用。
