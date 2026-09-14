# Markdown 白名单渲染

> 专题：AI 应用前端专题  
> 编号：A21

### 一句话答案

Markdown 白名单渲染是只允许安全标签和属性，过滤脚本、事件属性和危险协议。

### 核心策略

```text
允许 p、ul、ol、li、pre、code、table 等
限制 a href 协议
禁止事件属性
禁止 script / iframe
代码块只展示
```

### 项目里怎么用

- AI 回答。
- 文档预览。
- 知识库片段。

### 常见坑

- 只过滤 script，忽略事件属性。
- 允许 javascript: 链接。
- sanitize 后又拼接危险 HTML。

### 面试表达

白名单比黑名单更可靠。AI Markdown 渲染我会只允许必要标签和属性，链接协议也要限制。
