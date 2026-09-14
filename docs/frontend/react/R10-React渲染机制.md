# React 渲染机制

> 专题：React 专题  
> 编号：R10

### 一句话答案

React 通过状态变化触发组件重新执行，生成新的虚拟 DOM，再经过协调和提交阶段更新真实 DOM。

### 核心流程

```text
触发更新
render 阶段计算新树
diff / reconcile
commit 阶段更新 DOM
执行 effect
```

### 项目里怎么用

- 理解为什么父组件更新会导致子组件重新执行。
- 用 memo、useMemo、useCallback 控制不必要渲染。
- 避免 render 中写副作用。

### 常见坑

- 在 render 阶段直接发请求或修改外部状态。
- 不理解重新执行不等于真实 DOM 全部更新。

### 面试表达

React 是状态驱动 UI。组件重新执行是正常的，但真实 DOM 更新会经过 diff。优化时要区分函数重新执行和 DOM 真正更新。
