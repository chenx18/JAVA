# 03 Flex、Grid 与响应式布局

Flex适合沿一个主要方向分配空间，Grid直接组织行列轨道。选择布局后还要处理最小尺寸、内容溢出和组件容器适配，不能只背居中代码。

## 一、本章目录

- [Flex 主轴、交叉轴与对齐](#k01)
- [basis、grow、shrink 与最小内容](#k02)
- [Grid 轨道、放置与内容约束](#k03)
- [响应式单位、媒体与容器查询](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. Flex 主轴、交叉轴与对齐

flex-direction决定主轴方向，row并不永远等于从左到右，还受writing-mode和direction影响。justify-content沿主轴分配剩余空间，align-items沿交叉轴对齐，align-content处理多行/多列内容的整体分配。

gap管理项目间距，order改变视觉顺序但不自动改变DOM阅读或键盘顺序，不能为了视觉方便损坏可访问性。

```css
.toolbar {
  display: flex;
  align-items: center;
  gap: .75rem;
}
.toolbar .search { flex: 1; min-width: 0; }
.toolbar .actions { margin-inline-start: auto; }
```

单行工具栏中自动margin可以吸收剩余空间；布局空间不足时仍需定义换行、缩略或移动端结构。

<a id="k02"></a>

### 2. basis、grow、shrink 与最小内容

flex-basis提供参与空间分配的基础尺寸，grow按增长因子分配正剩余空间，shrink按相关基础尺寸与收缩因子处理不足。它们不是简单把最终宽度按比例平均。

flex:1在实际浏览器中常展开为1 1 0%，与auto基准不同；内容最小尺寸仍可能阻止项目变小。长单词、图片和代码块常需min-width:0及合适overflow策略。

```css
.row { display: flex; }
.sidebar { flex: 0 0 15rem; }
.main { flex: 1 1 0; min-width: 0; }
.main pre { overflow: auto; }
```

width和basis的关系还取决于basis是否auto及当前轴方向。调试时先看基础尺寸、可用空间和最小约束，不把grow=1理解为无条件等宽。

<a id="k03"></a>

### 3. Grid 轨道、放置与内容约束

grid-template-columns/rows定义轨道，fr分配可用剩余空间，minmax规定上下限，repeat复用轨道声明。grid-column/row或template-areas放置项目，gap定义轨道间距。

```css
.cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(min(100%, 16rem), 1fr));
  gap: 1rem;
}
.dashboard {
  display: grid;
  grid-template-columns: 15rem minmax(0, 1fr);
}
```

minmax(0,1fr)可避免默认最小内容约束导致溢出。auto-fill倾向保留可容纳的空轨道，auto-fit会折叠相应空轨道，效果还与内容和轨道定义有关；不要只记成“一个会换行一个不会”。

subgrid允许子网格复用父轨道，适合对齐嵌套组件；按目标兼容性使用。Grid视觉重排同样不能忽略DOM语义顺序。

<a id="k04"></a>

### 4. 响应式单位、媒体与容器查询

px是CSS像素不等于物理屏幕像素；rem相对根字体，em依所在属性语境关联字体尺寸；vw/vh与动态视口单位svh/lvh/dvh适用于不同移动浏览器可视区域需求。clamp(min,preferred,max)用于有界流式尺寸。

媒体查询看视口或用户偏好，容器查询看组件容器。组件放到窄侧栏时，容器宽度比全屏宽度更能决定布局。

```css
.panel { container-type: inline-size; }
@container (min-width: 40rem) {
  .panel-content { display: grid; grid-template-columns: 1fr 1fr; }
}
@media (prefers-reduced-motion: reduce) {
  .animated { animation: none; transition: none; }
}
```

容器需要建立合适查询上下文，size containment会影响尺寸计算，不能随意应用在依赖内容撑开的布局上。断点依据内容何时失效，而不是为每个设备型号硬编码。

<a id="summary"></a>

## 三、知识小结

Flex先看轴与空间分配，Grid先看轨道与放置，溢出先看最小内容约束。响应式从内容与容器出发，同时保留阅读顺序和用户偏好。

参考：[MDN Web 开发](https://developer.mozilla.org/en-US/docs/Learn_web_development)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="web03-01"></a>

### WEB03-01 [P0·基础] Flex和Grid怎么选？

**回答：** 单一主要方向的排列与空间分配常用Flex，显式行列和二维对齐常用Grid，实际页面可以组合。选择后仍要处理最小尺寸、换行与内容溢出，不是一个绝对替代另一个。

对应讲解：[Flex 主轴、交叉轴与对齐](#k01)。

<a id="web03-02"></a>

### WEB03-02 [P0·原理] flex:1为何不一定等宽？

**回答：** grow只是空间分配因素，还受basis、内容尺寸、min/max和shrink影响，默认最小内容约束可能阻止收缩。检查计算尺寸，必要时设置合适basis与min-width:0。

对应讲解：[basis、grow、shrink 与最小内容](#k02)。

<a id="web03-03"></a>

### WEB03-03 [P1·原理] 1fr与minmax(0,1fr)差在哪里？

**回答：** 单独fr轨道可能仍受自动最小尺寸影响，长内容导致溢出；minmax(0,1fr)明确允许轨道下限为0。它不自动解决所有内容显示问题，还需决定滚动、换行或裁切。

对应讲解：[Grid 轨道、放置与内容约束](#k03)。

<a id="web03-04"></a>

### WEB03-04 [P1·工程取舍] 为什么组件适配可以用容器查询？

**回答：** 同一个组件可能放在不同宽度区域，视口宽不代表它实际可用空间。容器查询按局部尺寸调整布局，但需正确建立查询容器并理解尺寸隔离影响。

对应讲解：[响应式单位、媒体与容器查询](#k04)。
