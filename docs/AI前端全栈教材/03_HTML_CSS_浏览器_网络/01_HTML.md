# 01 HTML、语义与可访问性

HTML为文档、表单和交互提供语义。原生标签的价值不仅是显示效果，还包括键盘、焦点、辅助技术、浏览器默认行为和服务器输出的可理解性。

## 一、本章目录

- [文档骨架与语义区域](#k01)
- [表单、按钮与默认行为](#k02)
- [script、defer、async 与 module](#k03)
- [键盘、焦点与 ARIA](#k04)
- [媒体、iframe 与资源提示](#k05)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 文档骨架与语义区域

```html
<!doctype html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>用户管理</title>
</head>
<body>
  <header><nav aria-label="主导航"><a href="/">首页</a></nav></header>
  <main><h1>用户管理</h1><section aria-labelledby="users-title">
    <h2 id="users-title">用户列表</h2>
  </section></main>
  <footer>帮助与反馈</footer>
</body>
</html>
```

doctype帮助进入标准模式，lang提供语言信息，charset避免错误解码，viewport影响移动布局。标题层级表达结构，不应只为字体大小选择h1/h2；main标明主内容，article适合可独立分发内容，section应有明确主题。

display是CSS布局行为，语义由元素和属性决定；把div设为inline-block不会让它变成button，HTML也不能仅按“块标签/行标签”概括全部内容模型。

<a id="k02"></a>

### 2. 表单、按钮与默认行为

```html
<form action="/search" method="get">
  <label for="query">搜索关键词</label>
  <input id="query" name="q" type="search" required maxlength="100">
  <button type="submit">搜索</button>
  <button type="button">展开筛选</button>
</form>
```

label通过for/id关联控件；name决定原生表单提交的字段名，id主要用于关联和定位。button在表单内未明确type时通常按提交按钮处理，非提交动作显式type=button。

disabled控件通常不参与原生提交，readonly与disabled行为不同；checkbox等布尔属性出现本身就表示启用，写disabled="false"仍不是关闭。前端required/pattern等校验改善体验，后端仍要执行数据验证和权限检查。

<a id="k03"></a>

### 3. script、defer、async 与 module

| 脚本方式 | 下载/执行关系 | 顺序与适用性 |
| --- | --- | --- |
| 普通经典外部script | 获取和执行可阻塞解析 | 依赖文档位置，可能拖慢解析 |
| defer经典外部script | 并行下载，解析后执行 | 通常保持文档顺序；内联经典脚本不靠defer实现同样效果 |
| async | 下载完成后择机执行 | 不保证多个脚本按文档顺序，适合独立脚本 |
| type=module | 解析依赖图，默认具有延后执行特征 | 模块作用域、严格模式、跨源规则不同 |
| module加async | 模块图准备好后执行 | 不以文档顺序作为依赖保证 |

DOMContentLoaded与相关脚本完成条件有关，但不是“所有图片和后续异步请求完成”。动态插入脚本也有自己的规则。需要依赖就用模块依赖或明确加载流程，不靠网络恰好快慢维持顺序。

<a id="k04"></a>

### 4. 键盘、焦点与 ARIA

链接表达导航，button表达动作。原生button自带键盘激活、焦点和辅助技术角色；div加click不会自动获得这些能力。tabindex=0可进入正常Tab序列，-1常用于程序聚焦，正数容易制造不自然顺序。

弹窗打开后合理移动焦点，关闭后回到触发处；避免隐藏内容仍可聚焦。保留可见focus样式，不仅用颜色表达错误。图片按用途提供alt，装饰图片可用空alt，表单错误与控件建立明确关联。

ARIA补充原生语义不足的部分，不能自动实现键盘行为或改变真实状态。aria-hidden不负责阻止焦点，role=button也不替你处理Space/Enter及disabled逻辑。

<a id="k05"></a>

### 5. 媒体、iframe 与资源提示

img提供alt、width/height或aspect-ratio帮助布局稳定，srcset/sizes适配图片选择；video/audio考虑controls和字幕。iframe要有可理解title，并按场景设置sandbox、allow和跨源消息校验。

preconnect提前建立到重要来源的连接，preload提前获取本页很快要用的关键资源，prefetch倾向预取后续可能用到的资源，modulepreload用于模块。它们是提示不是无限优先级保证，错误预加载会争用带宽，as/crossorigin等属性要与最终请求匹配。

可抓取的正文、明确标题与链接有利于搜索和辅助技术；SSR有助于及时提供HTML，但不自动保证SEO质量。

<a id="summary"></a>

## 三、知识小结

HTML优先使用正确语义和浏览器已有行为，再用CSS表现、JS增强。脚本加载看时机与依赖，资源提示看实际收益，可访问性看键盘与辅助技术能否完成同一任务。

参考：[MDN Web 开发](https://developer.mozilla.org/en-US/docs/Learn_web_development)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="web01-01"></a>

### WEB01-01 [P0·基础] 为什么动作优先用button而不是div点击？

**回答：** button已具备交互角色、键盘激活、焦点和表单行为。div需要补齐这些能力且容易遗漏；改CSS显示方式或只加role并不能替代完整原生交互。

对应讲解：[键盘、焦点与 ARIA](#k04)。

<a id="web01-02"></a>

### WEB01-02 [P0·原理] defer、async、module怎样区别？

**回答：** defer经典外部脚本在解析后按顺序执行，async更依赖就绪时机不保文档顺序，module按模块图加载且默认延后执行、使用模块作用域。内联、动态脚本和module async还要分别看规则。

对应讲解：[script、defer、async 与 module](#k03)。

<a id="web01-03"></a>

### WEB01-03 [P1·基础] disabled='false'为什么仍被禁用？

**回答：** disabled是布尔属性，属性存在本身表达启用，不按字符串false转布尔。应移除属性或通过对应property/框架布尔绑定正确设置。它和readonly的提交及交互行为也不同。

对应讲解：[表单、按钮与默认行为](#k02)。

<a id="web01-04"></a>

### WEB01-04 [P1·工程取舍] preload越多页面越快吗？

**回答：** 不一定，提前资源会争用连接和带宽，可能推迟真正关键资源。应针对首屏关键依赖使用，并匹配as、跨源等请求属性，用网络瀑布验证。

对应讲解：[媒体、iframe 与资源提示](#k05)。

<a id="web01-05"></a>

### WEB01-05 [P0·基础] 语义化HTML和设置display有什么区别？

**回答：** 语义描述文档和交互角色，display决定CSS布局方式。把div设成inline-block不会赋予button的键盘与表单行为；标题、main和label等还服务辅助技术和内容结构。

对应讲解：[文档骨架与语义区域](#k01)。
