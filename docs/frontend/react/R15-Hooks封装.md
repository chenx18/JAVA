# Hooks 封装

> 专题：React 专题  
> 编号：R15

### 一句话答案

自定义 Hooks 用于复用状态逻辑和副作用逻辑，而不是复用 UI。

### 核心原则

```text
以 use 开头
内部可调用其他 Hooks
保持输入输出清晰
处理清理逻辑
避免隐藏复杂副作用
```

### 项目里怎么用

- `useRequest`
- `useDebounce`
- `usePermission`
- `useSSE`
- `useLocalStorage`

### 常见坑

- Hook 内部副作用不透明。
- 返回值结构复杂。
- 条件调用 Hook。

### 面试表达

自定义 Hook 抽的是逻辑，不是视图。我会把请求、权限、流式连接、防抖这类可复用逻辑封成 Hook，同时保持返回值简单。
