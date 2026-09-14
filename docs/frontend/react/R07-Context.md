# Context

> 专题：React 专题  
> 编号：R07

### 一句话答案

Context 用于跨层级传递数据，适合主题、语言、用户信息等全局上下文。

### 核心原理

```text
Provider 提供值
Consumer / useContext 读取值
Provider 值变化会影响消费组件
```

### 项目里怎么用

- 主题。
- 国际化。
- 当前用户。
- 全局配置。

### 常见坑

- 把频繁变化的大状态放 Context，导致大量组件重渲染。
- 用 Context 替代所有状态管理。

### 面试表达

Context 适合跨层共享稳定上下文，不适合承载所有业务状态。频繁变化的复杂状态，我会考虑 Zustand、Redux 或拆分 Context。
