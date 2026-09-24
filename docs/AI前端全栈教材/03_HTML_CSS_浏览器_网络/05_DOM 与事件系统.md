# 05 DOM 查询、事件与生命周期

DOM是文档的对象模型，事件系统让交互沿明确路径传播。处理动态页面时，节点选择、状态读写、监听契约和清理责任需要同时考虑。

## 一、本章目录

- [节点查询与内容更新](#k01)
- [传播阶段、target 与 currentTarget](#k02)
- [监听选项与默认行为](#k03)
- [自定义事件、观察器与可访问交互](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 节点查询与内容更新

querySelector返回首个匹配元素或null，querySelectorAll通常返回静态NodeList；getElementsByClassName/TagName等常返回随DOM变化的实时集合。遍历时修改DOM要先确认集合是否会同步变化。

createElement、append/prepend、replaceChildren、remove用于建立和调整节点，classList管理类名，dataset读取data-*对应的字符串数据。attribute与property可能反射但不总相同，input.value表示当前值，默认值还有独立语义。

纯文本用textContent，innerHTML解析HTML，需要可信来源或适当清洗；DocumentFragment可组织一批插入，但不能因为用了fragment就跳过实际性能测量。

<a id="k02"></a>

### 2. 传播阶段、target 与 currentTarget

常见事件沿祖先捕获→目标→冒泡传播，但并非所有事件都冒泡。target是触发目标，currentTarget是当前运行监听的对象；Shadow DOM可重定向target，需要时检查composedPath。

```js
// browser-only
function mountList(container, onSelect) {
  const controller = new AbortController();
  container.addEventListener('click', event => {
    if (!(event.target instanceof Element)) return;
    const item = event.target.closest('[data-item-id]');
    if (!item || !container.contains(item)) return;
    onSelect(item.dataset.itemId);
  }, { signal: controller.signal });
  return () => controller.abort();
}
```

委托利用传播复用祖先监听，动态新增项也能处理。点击内部图标时用closest定位业务元素；还应处理嵌套容器、非冒泡事件和跨realm元素检查，不把所有委托都放到document。

<a id="k03"></a>

### 3. 监听选项与默认行为

addEventListener(type,listener,options)支持capture、once、passive、signal。removeEventListener需要匹配type、同一listener引用与capture；每次bind产生新函数，不能再次bind来解绑。

preventDefault取消可取消的默认动作；stopPropagation阻止后续传播，不自动取消默认动作或同节点其他监听；stopImmediatePropagation还能阻止同节点后续监听。passive承诺不阻止默认动作，调用preventDefault通常无法生效。

once在触发后清理，不代表永远未触发的监听会随业务页面自动消失。组件卸载仍需由所有者移除监听、Observer和timer。

<a id="k04"></a>

### 4. 自定义事件、观察器与可访问交互

CustomEvent可通过detail传数据，dispatchEvent通常同步执行相关监听。bubbles和composed控制传播边界；自定义事件不是网络消息，也不会自动跨iframe/Worker传播。

MutationObserver观察DOM变化，ResizeObserver观察元素尺寸，IntersectionObserver观察交叉状态；回调时机和适用对象不同，不能互相当成通用“监听变化”。停止使用时disconnect，回调修改所观察尺寸时还要避免反馈循环。

键盘、pointer和触摸事件按交互目标设计，不仅响应鼠标click。保持焦点、可见反馈及可取消操作，使用原生控件减少手工补齐的行为。

<a id="summary"></a>

## 三、知识小结

DOM操作先看集合和属性语义；事件先看路径、目标与默认动作；监听和观察器最后落实清理。动态交互不只是注册一个click回调。

参考：[MDN Web APIs](https://developer.mozilla.org/en-US/docs/Web/API)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="web05-01"></a>

### WEB05-01 [P0·原理] target和currentTarget在委托中分别是谁？

**回答：** target可能是按钮内的图标，currentTarget是正在执行回调的祖先容器。应从target定位业务元素并检查容器归属，而不是只看标签名。

对应讲解：[传播阶段、target 与 currentTarget](#k02)。

<a id="web05-02"></a>

### WEB05-02 [P0·基础] 阻止默认行为和阻止冒泡有何不同？

**回答：** 默认行为如导航或提交，传播是事件经过监听对象的路径。preventDefault与stopPropagation分别控制它们；passive、可取消性和同节点监听还各有约束。

对应讲解：[监听选项与默认行为](#k03)。

<a id="web05-03"></a>

### WEB05-03 [P1·原理] 为什么解绑监听经常失败？

**回答：** 常见原因是传入了另一个函数引用，例如再次bind，或capture不匹配。保存原引用或使用AbortSignal管理同一生命周期，不能只让页面DOM消失。

对应讲解：[监听选项与默认行为](#k03)。

<a id="web05-04"></a>

### WEB05-04 [P1·基础] querySelectorAll与实时集合有什么区别？

**回答：** querySelectorAll通常是查询时的静态结果，某些getElements方法返回实时集合会随DOM变化。边遍历边删除时行为因此不同，需要快照或明确索引策略。

对应讲解：[节点查询与内容更新](#k01)。

<a id="web05-05"></a>

### WEB05-05 [P1·工程取舍] Mutation、Resize与Intersection Observer怎样选择？

**回答：** 分别观察DOM变化、尺寸和交叉可见状态，适用对象与回调时机不同。用最贴近需求的API，避免反馈循环并在结束时disconnect；它们不是通用替代所有事件监听。

对应讲解：[自定义事件、观察器与可访问交互](#k04)。
