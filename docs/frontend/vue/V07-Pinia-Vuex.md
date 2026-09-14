# Pinia / Vuex

> 专题：Vue 专题  
> 编号：V07

### 一句话答案

Pinia 和 Vuex 都是 Vue 状态管理库，Pinia 更轻量，写法更贴近组合式 API，Vue3 项目通常优先 Pinia。

### 核心对比

Vuex：

```text
state
getters
mutations
actions
```

Pinia：

```text
state
getters
actions
支持组合式写法
类型推导更友好
```

### 项目里怎么用

- 用户信息。
- token。
- 权限菜单。
- 全局配置。
- 字典缓存。

### 常见坑

- 把页面临时状态放进 store。
- store 之间循环依赖。
- 刷新后持久化状态恢复不当。

### 面试表达

我会把 store 用于跨页面共享状态，不把所有接口数据都塞进去。Vue3 项目优先 Pinia，因为写法更简单，类型支持也更好。
