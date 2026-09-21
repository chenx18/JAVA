# 第十一章 模块化、ESM、CommonJS 与 Tree Shaking

## 一、本章具体知识点

- ESM
- import / export
- default / named export
- live binding
- module scope
- CommonJS
- require / module.exports
- ESM 与 CJS 差异
- Tree Shaking
- 动态 import
- 循环依赖

## 二、各知识点详细解释

### 1. ESM

ES Module 是 JavaScript 标准模块系统，具有静态结构，构建工具可以在编译分析阶段知道 import/export 关系。

### 2. Live Binding

ESM 导入的是绑定而不是简单复制值：

```js
export let count = 0
export function inc() {
  count++
}
```

其他模块导入 count 时看到的是同一个绑定的最新值。

### 3. CommonJS

Node 历史上广泛使用：

```js
const x = require('./x')
module.exports = x
```

加载模型、执行时机与 ESM 不同。

### 4. Tree Shaking

Tree Shaking 依赖模块静态结构分析，构建器可以删除没有被使用的导出，但前提还包括副作用分析等条件。

## 三、本章面试题与答案

### 题 1：ESM 和 CommonJS 的主要区别？

**答案：**

ESM 是标准模块系统，具有静态 import/export 和 live binding，适合构建工具分析；CommonJS 使用 require/module.exports，加载语义更偏运行时。现代前端构建工具大量基于 ESM 的静态结构做依赖分析和优化。

### 题 2：为什么 ESM 更适合 Tree Shaking？

**答案：**

因为 import/export 是静态语法结构，构建工具在编译阶段可以分析模块依赖和哪些导出被使用；CommonJS 的 require 可以出现在运行时表达式中，静态分析难度更高。

---
