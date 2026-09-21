# 第九章 VNode、Patch、Diff

## 一、本章具体知识点

- VNode
- h
- shapeFlag
- key
- mount
- patch
- unmount
- keyed diff
- component VNode
- children
- LIS

## 二、各知识点详细解释

VNode 是对真实 DOM/组件结构的 JavaScript 描述。

更新过程可以抽象成：

```text
新 VNode
vs
旧 VNode
↓
patch
↓
判断节点类型
↓
复用 / 创建 / 删除
↓
更新真实 DOM
```

对于同层 children，key 提供节点身份信息，使框架知道哪些节点应该复用、移动或删除。Vue 的 keyed children diff 中会结合最长递增子序列减少实际 DOM 移动。

## 三、本章面试题与答案

### 题：key 为什么不能随便使用 index？

**答案：**

key 表示列表项的稳定身份。如果列表中间插入、删除或排序，index 会随着位置变化，导致框架错误复用节点和状态；如果列表顺序永久稳定且没有插入删除，index 的风险才较低。

---
