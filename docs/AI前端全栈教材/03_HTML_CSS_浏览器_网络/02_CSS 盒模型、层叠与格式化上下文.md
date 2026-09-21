# 第二章 CSS 盒模型、层叠与格式化上下文

## 一、本章具体知识点

- content-box / border-box
- margin / padding / border
- width / height
- margin collapse
- cascade
- specificity
- inheritance
- containing block
- BFC
- stacking context
- z-index

## 二、各知识点详细解释

### 1. 盒模型

标准盒模型：

```text
content
+ padding
+ border
= 实际占用尺寸
```

`box-sizing: border-box` 会让 width/height 包括 padding 和 border。

### 2. margin collapse

块级正常流中相邻垂直 margin 可能发生合并，不是简单相加。创建新的 formatting context 等手段可以改变这种行为。

### 3. BFC

BFC 是一种块级格式化上下文。进入不同 BFC 后，内部布局与外部的某些布局关系被隔离。常见用途：清除浮动、防止 margin collapse、控制浮动影响。

### 4. Stacking Context

z-index 不是全局数字大小比较。不同 stacking context 中的 z-index 不直接跨上下文比较。形成 stacking context 的条件包括 positioned + z-index、transform、opacity 等多种情况。

## 三、本章面试题与答案

### 题 1：什么是 BFC？

**答案：**

BFC 是块级格式化上下文，它提供相对独立的布局环境，使内部块盒参与自己的格式化规则。它常用于隔离浮动、解决 margin collapse、包住浮动元素等，但不应把 BFC 当成一个简单的“清除浮动属性”。

### 题 2：为什么 z-index 设置很大仍然压不过另一个元素？

**答案：**

因为 z-index 主要在各自 stacking context 内比较。如果元素处在不同 stacking context 中，先比较上下文层级，再比较上下文内部的 z-index，不能把所有数字放到一个全局坐标系比较。

---
