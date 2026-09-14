# Redux / Zustand

> 专题：React 专题  
> 编号：R08

### 一句话答案

Redux 和 Zustand 都能做全局状态管理，Redux 更规范、生态完整，Zustand 更轻量、写法简单。

### 核心对比

Redux：

```text
单向数据流
action
reducer
store
中间件生态强
```

Zustand：

```text
轻量
API 简洁
样板代码少
选择器订阅
```

### 项目里怎么用

- 大型团队和复杂状态可用 Redux Toolkit。
- 中小项目或轻量全局状态可用 Zustand。

### 常见坑

- 全局状态过多。
- action 粒度混乱。
- 异步逻辑散落。

### 面试表达

我会根据团队和项目复杂度选。Redux 更适合规范强、协作多人、状态复杂的项目；Zustand 更轻量，适合快速开发和较少样板代码的场景。
