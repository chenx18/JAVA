# 第五章 DOM 与事件系统

## 一、本章具体知识点

- EventTarget
- addEventListener
- capture
- target
- bubble
- stopPropagation
- preventDefault
- delegation
- passive
- once
- CustomEvent

## 二、各知识点详细解释

事件传播通常抽象成：

```text
Capture
→ Target
→ Bubble
```

事件委托利用冒泡，在父元素上监听多个子元素的事件，从而减少大量 listener，并方便动态节点处理。

`preventDefault()` 阻止浏览器默认行为；`stopPropagation()` 阻止事件继续传播，两者完全不同。

`passive: true` 告诉浏览器监听器不会调用 preventDefault，典型用于滚动相关事件优化。

## 三、本章面试题与答案

### 题：事件委托为什么能减少事件监听？

**答案：**

子元素事件可以冒泡到共同父节点，因此父节点可以根据 event.target 判断实际触发元素。这样多个动态子元素可以共享一个监听器。

### 题：preventDefault 与 stopPropagation 的区别？

**答案：**

preventDefault 是阻止默认行为，例如链接跳转；stopPropagation 是阻止事件传播到其他阶段/祖先。阻止传播不等于阻止默认行为。

---
