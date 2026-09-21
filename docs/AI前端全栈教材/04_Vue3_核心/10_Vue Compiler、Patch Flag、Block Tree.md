# 第十章 Vue Compiler、Patch Flag、Block Tree

## 一、本章具体知识点

- Template AST
- parser
- transform
- codegen
- static hoist
- patch flag
- dynamic children
- block tree
- runtime compiler cooperation

## 二、各知识点详细解释

Vue 不是把模板简单地当字符串执行，而是：

```text
Template
→ Parse
→ AST
→ Transform
→ Codegen
→ Render Function
→ VNode
```

Compiler 可以知道某些节点是静态的，并对动态区域做标记。

### Patch Flag

编译器告诉 runtime：这个节点哪些部分可能变化，例如 class、text、props 等。Runtime 就不用每次对所有属性做完整比较。

### Block Tree

Block 收集动态子节点，让 runtime 更快速地定位真正可能变化的部分。

## 三、本章面试题与答案

### 题：Vue 为什么需要 Compiler？

**答案：**

因为模板在编译阶段已经包含大量静态信息，例如哪些节点不会变、哪些属性是动态的。Compiler 可以提前把这些信息编码进 render function，让 Runtime 在更新时只关注真正动态的部分，减少运行时工作。

### 题：Patch Flag 解决什么问题？

**答案：**

Patch Flag 是编译器提供给运行时的动态信息标记。例如某节点只有 class 动态变化，runtime 就可以只检查 class，而不必对所有 props 做完整 diff。

---
