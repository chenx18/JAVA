# 第十五章 Vue 性能优化

## 一、本章具体知识点

- 响应式粒度
- shallowRef
- markRaw
- computed
- v-once
- v-memo
- KeepAlive
- lazy component
- virtual list
- stable props
- large list

## 二、各知识点详细解释

性能优化首先定位瓶颈。Vue 中常见问题：

```text
响应式数据过大
→ 依赖过多
→ 计算/更新成本高
```

```text
10 万行列表
→ DOM 数量过大
→ layout/paint 与 JS 都昂贵
```

可以通过：

- Virtual List
- 分页
- shallowRef
- 拆组件
- 减少无意义响应式
- 异步组件
- 缓存派生状态

## 三、本章面试题与答案

### 题：10 万条列表 Vue 页面卡顿怎么办？

**答案：**

首先定位是数据计算、响应式追踪还是 DOM 数量导致。通常最有效的是虚拟列表，只渲染可视区域；再结合分页、数据切片、减少深层响应式、组件拆分和稳定 props。不能只说“使用 computed/nextTick”就认为解决了。
