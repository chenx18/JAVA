# 02 CSS 盒模型、层叠与格式化上下文

CSS问题通常同时涉及尺寸、规则胜出、布局上下文和绘制层级。把这些维度分开，才能解释为什么宽度不对、margin合并或z-index失效。

## 一、本章目录

- [盒模型与尺寸约束](#k01)
- [层叠、优先级与继承](#k02)
- [正常流、定位与包含块](#k03)
- [margin 合并、浮动与 BFC](#k04)
- [层叠上下文与 z-index](#k05)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 盒模型与尺寸约束

content-box的width/height描述内容区，border-box将padding和border算入声明尺寸；margin位于边框外，不计入border-box。实际布局还受min/max尺寸、内容、可用空间和格式化规则影响。

```css
*, *::before, *::after { box-sizing: border-box; }
.card {
  width: min(100%, 40rem);
  padding: 1rem;
  border: 1px solid #ccc;
  overflow-wrap: anywhere;
}
```

百分比尺寸依赖包含块，height百分比是否能解析也与父级确定高度等条件有关。min-width:auto在Flex/Grid项目中可能保留最小内容尺寸，导致“明明有flex却不收缩”，必要时明确min-width:0。

<a id="k02"></a>

### 2. 层叠、优先级与继承

层叠先考虑来源与重要性、层叠层等因素，再在可比较范围内看选择器specificity和出现顺序；不能只算id比class高。!important、动画/过渡和cascade layers也有专门优先关系。

选择器常见有类型、类、ID、属性、后代/子代/相邻、伪类和伪元素。:where()自身特异性为0，:is/:not/:has通常由相应参数决定特异性；它们不会让所有复杂选择器都变得相同。

继承是属性未在元素上明确取到值时从父级传播的机制，不是父选择器与子选择器直接竞争。字体颜色常继承，margin/border通常不继承。CSS自定义属性常参与继承，var(--x,fallback)只处理相应缺失/无效变量情形，不自动验证所有业务颜色配置。

<a id="k03"></a>

### 3. 正常流、定位与包含块

normal flow决定普通块和行内布局，position:relative仍占原位置，absolute通常脱离正常流并根据包含块定位，fixed常相对视口，但transform等祖先可能改变其包含块。sticky需要满足滚动容器、偏移阈值和布局空间等条件，不是设置后必然吸顶。

包含块用于计算尺寸和位置，格式化上下文组织布局，stacking context组织绘制层级，三者不是同义词。排查position问题时先找真正包含块和滚动容器，再调top/left。

<a id="k04"></a>

### 4. margin 合并、浮动与 BFC

普通块级正常流中，部分垂直margin可能合并；正负margin的组合也不是简单相加。水平margin不按同样规则合并，Flex/Grid布局也有不同规则。

BFC提供一组相对独立的块布局规则，可包含内部浮动，并限制某些内外margin合并关系。display:flow-root是表达创建独立块格式化上下文的明确方式；overflow等也可能建立BFC，但伴随裁切/滚动副作用。

```css
.article { display: flow-root; }
.avatar { float: left; width: 4rem; margin-inline-end: 1rem; }
```

BFC不是一种专门“清除浮动属性”，也不能用一句“内部不影响外部”解释所有布局依赖；尺寸仍可能影响父布局。

<a id="k05"></a>

### 5. 层叠上下文与 z-index

z-index不是全页面统一数字榜。元素属于各自层叠上下文，上下文作为整体参与上层排序，再在内部比较。定位元素配非auto z-index、transform、opacity小于1等可创建上下文；Flex/Grid项目等还有特定规则。

一个子元素z-index再大，也可能无法跨过其父上下文在兄弟上下文后面的整体顺序。排查时沿祖先找到创建上下文的条件，确认弹层应挂载在哪层，而不是不停增加数字。

top layer中的dialog/popover等有另外的顶层绘制机制，不能机械套普通z-index；它们仍需正确交互、焦点和兼容设计。

<a id="summary"></a>

## 三、知识小结

尺寸看盒模型与约束；样式胜出看层叠；位置看包含块；块布局看格式化上下文；遮挡看层叠上下文。分别定位，避免一个z-index或overflow解决所有问题。

参考：[MDN Web 开发](https://developer.mozilla.org/en-US/docs/Learn_web_development)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="web02-01"></a>

### WEB02-01 [P0·基础] content-box与border-box如何计算宽度？

**回答：** 前者声明宽度通常对应内容区，padding和border额外增加；后者把padding和border算入声明宽度，margin仍在外。最终尺寸还要看min/max和布局约束。

对应讲解：[盒模型与尺寸约束](#k01)。

<a id="web02-02"></a>

### WEB02-02 [P0·原理] z-index很大仍被盖住的原因是什么？

**回答：** 它可能被限制在较低的祖先层叠上下文中。浏览器先比较上下文整体顺序，再比较其内部元素；应检查祖先transform、opacity、定位等触发条件。

对应讲解：[层叠上下文与 z-index](#k05)。

<a id="web02-03"></a>

### WEB02-03 [P1·原理] BFC如何影响浮动与margin？

**回答：** BFC按独立块格式化规则处理布局，可包含内部浮动并隔离特定margin合并。应选择flow-root等明确方式，注意overflow创建BFC还可能带裁切或滚动副作用。

对应讲解：[margin 合并、浮动与 BFC](#k04)。

<a id="web02-04"></a>

### WEB02-04 [P1·原理] CSS优先级只看id、class和顺序吗？

**回答：** 不够，先有来源、重要性、层叠层等比较，再在相应范围看specificity和顺序。继承也不是与直接指定样式同一层竞争，现代选择器还有特异性规则。

对应讲解：[层叠、优先级与继承](#k02)。

<a id="web02-05"></a>

### WEB02-05 [P1·工程取舍] 为什么fixed元素有时不相对视口？

**回答：** 某些祖先的transform等属性可建立fixed定位包含块。需确认真实包含块、滚动环境和层叠上下文，单改top或z-index未必能解决。

对应讲解：[正常流、定位与包含块](#k03)。
