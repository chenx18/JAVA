# flex

> 专题：HTML 与 CSS  
> 编号：C08

### 一句话答案

Flex 是一维布局模型，适合处理水平或垂直方向的排列、对齐和空间分配。

### 核心属性

容器：

```text
display: flex
flex-direction
justify-content
align-items
flex-wrap
gap
```

项目：

```text
flex-grow
flex-shrink
flex-basis
align-self
```

### 项目里怎么用

- 页面头部。
- 表单按钮行。
- 左右布局。
- 居中。

### 常见坑

- flex 子项默认 `min-width: auto` 导致文本溢出。
- 混淆主轴和交叉轴。
- 不理解 `flex: 1` 实际包含 grow、shrink、basis。

### 面试表达

Flex 适合一维布局。我的经验是复杂后台布局里，头部、工具栏、按钮组用 flex 很合适；如果是二维网格结构，则 grid 更直接。
