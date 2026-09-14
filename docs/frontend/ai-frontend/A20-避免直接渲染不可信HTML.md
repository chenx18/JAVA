# 避免直接渲染不可信 HTML

> 专题：AI 应用前端专题  
> 编号：A20

### 一句话答案

不可信 HTML 可能包含脚本、危险链接和恶意内容，前端应默认转义或白名单过滤。

### 核心风险

```text
script
onerror / onclick
javascript: 链接
iframe
style 注入
```

### 项目里怎么用

- Markdown 禁用 raw HTML。
- 富文本 sanitize。
- AI 输出只允许安全标签。

### 常见坑

- 认为 Markdown 一定安全。
- 为了样式方便直接 innerHTML。
- 过滤不完整。

### 面试表达

我会把 AI 输出和用户输入一样看待，都属于不可信内容。默认不直接渲染 HTML，需要渲染时也必须走白名单过滤。
