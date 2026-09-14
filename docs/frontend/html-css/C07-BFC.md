# BFC

> 专题：HTML 与 CSS  
> 编号：C07

### 一句话答案

BFC 是块级格式化上下文，内部布局与外部互不影响，可用于清除浮动、阻止 margin 合并和实现两栏布局。

### 核心触发方式

```text
overflow 非 visible
display: flow-root
float
position: absolute / fixed
display: inline-block
```

### 项目里怎么用

- 清除浮动。
- 避免 margin 折叠。
- 防止文字环绕浮动元素。

### 常见坑

- 为了触发 BFC 用 overflow hidden，意外裁剪内容。
- 不理解父子 margin 合并。

### 面试表达

BFC 可以理解成独立布局区域。现在更推荐用 `display: flow-root` 清晰表达意图，而不是滥用 overflow hidden。
