# ES Module 与 CommonJS

> 专题：JavaScript 核心  
> 编号：J09

### 一句话答案

ES Module 是语言标准的静态模块系统，CommonJS 是 Node.js 早期常用的运行时模块系统。

### 核心原理

ES Module：

```text
import / export
静态分析
支持 tree shaking
输出是 live binding
```

CommonJS：

```text
require / module.exports
运行时加载
导出的是值的拷贝或对象引用
```

### 项目里怎么用

- 前端工程主要使用 ESM。
- Node.js 老项目常见 CommonJS。
- Vite 基于 ESM 开发体验更快。

### 常见坑

- ESM 不能随意写在条件语句里。
- 混用模块格式导致默认导出和命名导出不一致。
- CommonJS 不利于静态 tree shaking。

### 面试表达

ESM 最大特点是静态结构，构建工具可以提前分析依赖并做 tree shaking。CommonJS 更偏运行时加载，灵活但不利于静态优化。
