# Markdown 如何安全渲染

> 专题：AI 应用前端专题  
> 编号：A03

### 一句话答案

Markdown 渲染要把模型输出当不可信内容处理，禁用或过滤危险 HTML。

### 核心方案

```text
Markdown parser
sanitize
禁用原始 HTML
链接协议白名单
代码块只展示不执行
```

### 项目里怎么用

- AI 答案渲染。
- 文档总结展示。
- 知识库问答引用。

### 常见坑

- 直接 `v-html` 或 `dangerouslySetInnerHTML`。
- 允许 `javascript:` 链接。
- 富文本和 Markdown 混合渲染无过滤。

### 面试表达

模型输出不能默认可信。我的处理是 Markdown 白名单渲染，禁用危险 HTML，链接做协议限制，代码块只展示不执行。
