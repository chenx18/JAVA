# 第一章 HTML

## 一、本章具体知识点

- HTML 文档结构
- 语义化标签
- block / inline / inline-block
- form / input / button
- label
- script 加载
- async / defer / module
- meta
- accessibility
- SEO
- iframe
- preconnect / preload / prefetch

## 二、各知识点详细解释

### 1. HTML 的作用

HTML 描述文档结构和语义，CSS 描述表现，JavaScript 负责行为。现代前端不是“HTML 不重要”，而是语义化 HTML 会直接影响可访问性、SEO、事件行为与 SSR 输出。

### 2. 语义化

例如：

```html
<header>
<nav>
<main>
<article>
<section>
<footer>
<button>
```

`button` 与 `<div @click>` 的区别不仅是样式。button 自带键盘、焦点、表单语义和无障碍语义。

### 3. script

普通 script 会阻塞 HTML 解析；`defer` 在文档解析期间下载，解析完成后执行；`async` 下载完成就执行，执行时可能打断解析；ES module 默认具有 defer-like 行为。

### 4. Accessibility

核心是让不同用户代理和辅助技术都能理解页面。

重点：

- 正确语义
- label 与表单控件关联
- 键盘可操作
- focus 管理
- ARIA 只在原生语义不足时使用

## 三、本章面试题与答案

### 题 1：script 的 defer 和 async 区别？

**答案：**

两者都允许脚本在 HTML 解析期间下载。defer 会等待 HTML 解析完成后按文档顺序执行，适合有依赖顺序的脚本；async 下载完成后立即执行，多个 async 脚本完成顺序不确定，适合彼此独立的脚本。

### 题 2：为什么优先用 button 而不是 div 点击？

**答案：**

button 自带交互语义、键盘行为、焦点和辅助技术支持，而 div 需要自己补齐这些行为，容易漏掉无障碍和可用性细节。

---
