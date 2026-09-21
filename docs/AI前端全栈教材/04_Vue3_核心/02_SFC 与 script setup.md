# 第二章 SFC 与 script setup

## 一、本章具体知识点

- Single File Component
- script
- script setup
- template
- style
- scoped
- CSS Modules
- 编译期宏

## 二、各知识点详细解释

`.vue` 文件把一个组件的逻辑、模板和样式放在一起。

`<script setup>` 是编译器语法，让 Composition API 写法更直接：顶层声明会暴露给模板，不需要手动返回对象。

`defineProps`、`defineEmits`、`defineExpose`、`defineModel` 等在特定语境下属于编译器宏，不是普通运行时函数调用。

## 三、本章面试题与答案

### 题：script setup 和普通 setup 有什么区别？

**答案：**

script setup 是编译器提供的更简洁的 SFC 写法。顶层变量、函数和导入可以直接用于模板，props、emits 等可以使用编译器宏表达。普通 setup 则是运行时组件选项中的一个 setup 函数。

---
