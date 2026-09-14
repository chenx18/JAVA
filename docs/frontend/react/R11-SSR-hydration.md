# SSR / hydration

> 专题：React 专题  
> 编号：R11

### 一句话答案

SSR 是服务端生成 HTML，hydration 是客户端接管这份 HTML 并绑定事件，使页面变成可交互状态。

### 核心原理

```text
服务端渲染 HTML
浏览器先看到内容
客户端下载 JS
React 在客户端重新构建组件树
绑定事件并接管页面
```

### 项目里怎么用

- SEO 页面。
- 首屏性能要求高的页面。
- Next.js 项目。

### 常见坑

- 服务端和客户端渲染结果不一致导致 hydration mismatch。
- 在服务端访问 window/document。
- 首屏 HTML 有了但 JS 未加载前不可交互。

### 面试表达

SSR 提升首屏内容可见和 SEO，但要处理服务端环境和客户端环境差异。hydration mismatch 常见原因是时间、随机数、客户端专属数据导致首屏结果不一致。
