# Tree Shaking

> 专题：工程化与性能优化  
> 编号：E02

### 一句话答案

Tree Shaking 是在打包时移除未使用代码，通常依赖 ES Module 的静态结构。

### 核心原理

```text
静态分析 import/export
标记被使用的导出
压缩阶段删除未使用代码
```

### 项目里怎么用

- 使用 ESM 版本依赖。
- 按需引入组件库。
- 避免整个库全量导入。
- package.json 配置 `sideEffects`。

### 常见坑

- CommonJS 不利于 tree shaking。
- 模块有副作用时不能安全删除。
- 按需引入写法不对导致包变大。

### 面试表达

Tree Shaking 依赖静态模块分析。项目里我会关注依赖是否提供 ESM、组件库是否按需引入，以及 sideEffects 配置是否正确。
