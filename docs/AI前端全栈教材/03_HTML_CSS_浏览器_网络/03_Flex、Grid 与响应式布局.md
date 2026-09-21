# 第三章 Flex、Grid 与响应式布局

## 一、本章具体知识点

- Flex 容器与项目
- main/cross axis
- flex-basis
- grow / shrink
- align / justify
- Grid tracks
- grid areas
- auto placement
- responsive design
- media query
- container query

## 二、各知识点详细解释

Flex 更适合一维布局，Grid 更擅长二维布局。

`flex-basis` 决定参与剩余空间计算的基础尺寸；`flex-grow` 决定剩余空间如何增长；`flex-shrink` 决定空间不足时如何收缩。

Grid 用 track、line、area 表达二维空间，可以通过 `grid-template-columns` 等属性建立明确结构。

Container Query 根据组件容器而不是整个 viewport 做适配，对设计系统和可复用组件非常重要。

## 三、本章面试题与答案

### 题：Flex 和 Grid 怎么选？

**答案：**

单一方向排列优先 Flex，例如导航条、按钮组；二维布局和复杂网格优先 Grid。现代组件设计中还可以结合 Container Query，让组件根据自己的容器尺寸决定布局。

---
