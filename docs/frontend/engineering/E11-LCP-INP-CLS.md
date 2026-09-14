# LCP / INP / CLS

> 专题：工程化与性能优化  
> 编号：E11

### 一句话答案

LCP 衡量主要内容加载速度，INP 衡量交互响应，CLS 衡量视觉稳定性。

### 核心指标

```text
LCP：Largest Contentful Paint，最大内容绘制
INP：Interaction to Next Paint，交互到下一次绘制
CLS：Cumulative Layout Shift，累计布局偏移
```

### 项目里怎么用

- LCP 慢：优化首屏图片、字体、接口和阻塞资源。
- INP 差：减少长任务、拆分 JS、优化事件处理。
- CLS 高：给图片、广告、异步内容预留尺寸。

### 常见坑

- 只看 Lighthouse 单次结果，不看真实用户数据。
- 图片没设置宽高导致布局跳动。
- JS 长任务导致点击无响应。

### 面试表达

我会把性能分成加载、交互、稳定三类。LCP 看首屏主内容，INP 看交互延迟，CLS 看页面是否跳动。优化时要结合实验室数据和真实用户数据。
