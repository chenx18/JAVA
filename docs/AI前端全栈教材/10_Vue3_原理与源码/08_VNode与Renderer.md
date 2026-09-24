# 08 VNode与Renderer

VNode是描述，Renderer执行更新策略，宿主操作真正创建或修改节点。把这三层拆开，可以理解Vue为何支持自定义渲染器，也能避免把VNode直接当DOM。

源码基线：**Vue v3.5.43**。区分公开行为、这个版本的内部实现与本文教学模型；代码块各自独立，使用 Vue 包的例子在安装相应版本的 Node ESM 或前端工程中运行。

## 一、本章目录

- [VNode 的身份与字段](#k01)
- [Renderer 的宿主接口](#k02)
- [mount、patch、unmount](#k03)
- [runtime-dom 与属性补丁](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. VNode 的身份与字段

常见字段有type、props、key、children、el、component、shapeFlag、patchFlag、dynamicChildren。type/shapeFlag帮助区分元素、组件、文本或Fragment；el联系已挂载的宿主节点，component联系组件实例。

```js
import { h, isVNode } from 'vue';
const vnode = h('p', { id: 'message', key: 'one' }, 'hello');
console.log(isVNode(vnode), vnode.type, vnode.key, vnode.el); // true p one null
```

创建VNode不立即创建DOM。已挂载VNode包含运行状态，同一VNode对象不能随意在多处复用而期待el等字段代表多个节点；需要由框架正常克隆或新建。

源码：[createVNode/normalizeChildren/isSameVNodeType](https://github.com/vuejs/core/blob/v3.5.43/packages/runtime-core/src/vnode.ts)。key通常配合type判断是否同一节点种类，key本身不是HTML属性。

<a id="k02"></a>

### 2. Renderer 的宿主接口

[createRenderer/baseCreateRenderer](https://github.com/vuejs/core/blob/v3.5.43/packages/runtime-core/src/renderer.ts)接收createElement、insert、remove、setElementText、patchProp等宿主函数。算法决定做什么，宿主负责怎么做。

```js
import { createRenderer, h } from 'vue';
const renderer = createRenderer({
  createElement: type => ({ type, children: [], text: '', parent: null, props: {} }),
  insert(node, parent, anchor = null) {
    if (node.parent) node.parent.children.splice(node.parent.children.indexOf(node), 1);
    const index = anchor ? parent.children.indexOf(anchor) : parent.children.length;
    parent.children.splice(index, 0, node); node.parent = parent;
  },
  remove(node) {
    if (node.parent) node.parent.children.splice(node.parent.children.indexOf(node), 1);
    node.parent = null;
  },
  setElementText(node, text) { node.text = text; node.children = []; },
  createText: text => ({ type: '#text', text, parent: null }),
  setText(node, text) { node.text = text; },
  createComment: text => ({ type: '#comment', text, parent: null }),
  parentNode: node => node.parent,
  nextSibling: node => node.parent?.children[node.parent.children.indexOf(node) + 1] ?? null,
  patchProp(node, key, oldValue, value) { node.props[key] = value; }
});
const root = { children: [] };
renderer.render(h('p', null, 'A'), root);
const original = root.children[0];
renderer.render(h('p', null, 'B'), root);
console.log(root.children[0] === original, original.text); // true B
```

这是有限宿主模型，只验证简单文本元素复用；非完整DOM和布局实现。生产宿主还需正确处理复杂子树、命名空间与事件。

<a id="k03"></a>

### 3. mount、patch、unmount

首次patch(null,n2)挂载；同类型身份更新进入对应处理路径；不匹配则卸载旧节点再挂新节点。元素patch处理props与children，组件patch可能复用实例并更新其subTree。

children转换要覆盖空、文本、数组各组合：数组变文本先卸载旧子树，文本变数组先清空文本再挂载。直接innerHTML覆盖可能跳过组件effect和事件资源清理，所以运行时unmount不只是removeChild。

Fragment、组件和Teleport不一定对应一个简单DOM节点，移动/锚点必须按节点类别处理。

<a id="k04"></a>

### 4. runtime-dom 与属性补丁

[ensureRenderer](https://github.com/vuejs/core/blob/v3.5.43/packages/runtime-dom/src/index.ts)把runtime-core算法与DOM操作拼接。真实patchProp需要区分class、style、事件、DOM property和attribute；input.value/checked等不能总用setAttribute。

事件通常可用稳定invoker包装更新回调值，避免每次props变化都重复解绑绑定；具体实现还处理事件时间戳等边界。第15章仅支持常见事件和属性并写明限制，不冒充完整runtime-dom。

阅读路线：VNode创建→renderer.patch分流→元素/组件处理→宿主操作。

<a id="summary"></a>

## 三、知识小结

VNode描述身份和结构，Renderer选择挂载/更新/卸载，宿主接口执行平台操作。复用要同时保持节点身份与资源清理，不是只比较文本是否相同。

<a id="interview"></a>

## 四、面试题与答案

<a id="vsr08-01"></a>

### VSR08-01 [P0·原理] 创建VNode是否就创建了真实DOM？

**回答：** 没有，VNode先描述UI，renderer根据挂载或更新流程调用宿主操作才创建/修改节点。el在挂载后才关联宿主实体。

对应讲解：[VNode 的身份与字段](#k01)。

<a id="vsr08-02"></a>

### VSR08-02 [P1·源码] 自定义Renderer为什么能不依赖document？

**回答：** runtime-core通过宿主接口表达节点操作，调用方可用其他结构实现。只要接口语义正确，算法不必硬编码浏览器DOM；平台布局和事件仍需自己负责。

对应讲解：[Renderer 的宿主接口](#k02)。

<a id="vsr08-03"></a>

### VSR08-03 [P0·原理] 数组children切成文本为何不能只设textContent就结束？

**回答：** 旧子树可能有组件effect、钩子或其他资源，需要先走相应unmount清理，再改宿主内容。节点从屏幕消失不代表生命周期结束逻辑已执行。

对应讲解：[mount、patch、unmount](#k03)。

<a id="vsr08-04"></a>

### VSR08-04 [P0·原理] patchProp为何不能全部用setAttribute？

**回答：** class/style、事件、DOM property和HTML attribute的语义不同，表单当前状态也不等于初始属性。真实runtime-dom要分类处理，简化renderer必须声明限制。

对应讲解：[runtime-dom 与属性补丁](#k04)。
