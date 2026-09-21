# 第五章 Web Performance

## 一、本章具体知识点

- FCP
- LCP
- INP
- CLS
- TTFB
- JS bundle
- code splitting
- lazy loading
- preload
- prefetch
- CDN
- image optimization
- Long Task

## 二、各知识点详细解释

性能优化必须从瓶颈出发。

```text
Network
→ TTFB
→ resource transfer

CPU
→ parse
→ compile
→ execute

Render
→ style
→ layout
→ paint
→ composite

Interaction
→ event
→ JS task
→ render
```

LCP 更关注主要内容何时出现；INP 关注用户交互响应；CLS 关注视觉稳定性。

### Code Splitting

把一个大 bundle 拆为多个 chunk，用户只在需要时加载。

### Lazy Loading

非首屏资源延迟加载。

### CDN

让资源更接近用户地理位置，并降低源站压力。

## 三、本章面试题与答案

### 题：页面很慢怎么排查？

**答案：**

先用 Network 看 TTFB、资源数量和大小，再用 Performance 看 Main Thread、Long Task、layout/paint 和脚本执行，然后结合 Lighthouse/Web Vitals 判断具体用户体验指标。最后根据瓶颈选择 SSR、缓存、CDN、拆包、懒加载、减少 JS 等方案。

---
