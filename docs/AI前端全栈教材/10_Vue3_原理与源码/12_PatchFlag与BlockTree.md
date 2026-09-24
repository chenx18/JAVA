# 12 PatchFlag与BlockTree

Patch Flag说明节点可能变哪部分，Block Tree组织哪些动态节点需要关注。这些是编译器与运行时共享的优化契约，不能只看成两个提速开关。

源码基线：**Vue v3.5.43**。区分公开行为、这个版本的内部实现与本文教学模型；代码块各自独立，使用 Vue 包的例子在安装相应版本的 Node ESM 或前端工程中运行。

## 一、本章目录

- [变化维度与特殊标志](#k01)
- [在产物中观察 TEXT、CLASS 与 FULL_PROPS](#k02)
- [openBlock、dynamicChildren 与稳定结构](#k03)
- [静态复用、缓存与正确性](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 变化维度与特殊标志

在[PatchFlags](https://github.com/vuejs/core/blob/v3.5.43/packages/shared/src/patchFlags.ts)中，TEXT、CLASS、STYLE、PROPS等正值可按位组合；FULL_PROPS用于动态键等需要完整属性比较的场景，dynamicProps帮助固定动态键列表。

CACHED=-1、BAIL=-2是特殊值，按相等判断，不与普通正标志随意按位混用。旧教程可能把-1称为HOISTED，阅读当前版本要匹配名称和实现，避免死记常量表。

shapeFlag主要描述VNode类别/children形态，patchFlag描述可用更新提示，二者不是同义位标记。

<a id="k02"></a>

### 2. 在产物中观察 TEXT、CLASS 与 FULL_PROPS

```js
import { compile } from '@vue/compiler-dom';
import * as Vue from 'vue';
function compiled(template, context) {
  return new Function('Vue', compile(template, {
    mode: 'function', prefixIdentifiers: true
  }).code)(Vue)(context, []);
}
const fixed = compiled('<p :class="tone">{{ text }}</p>', { tone: 'a', text: 'b' });
const dynamic = compiled('<p v-bind="attrs">x</p>', { attrs: { title: 't' } });
console.log(fixed.patchFlag, dynamic.patchFlag); // 3 16
```

已知只变class/text时，runtime可以按提示走局部路径；动态props键集合可能增删，需要更完整比较。不是用了FULL_PROPS就完全关闭所有其他优化。编译选项和上下文也会影响标记。

<a id="k03"></a>

### 3. openBlock、dynamicChildren 与稳定结构

[openBlock/setupBlock](https://github.com/vuejs/core/blob/v3.5.43/packages/runtime-core/src/vnode.ts)维护blockStack/currentBlock。创建动态VNode时按条件收集，block结束把列表赋给根VNode.dynamicChildren，再恢复父block。

一个稳定结构block可直接patchBlockChildren对应动态节点，避免反复走过大量静态祖先。v-if分支和v-for带结构不稳定性，需要各自block/fragment及keyed更新策略，不能拿一份固定dynamicChildren列表处理任意增删。

```js
import { compile } from '@vue/compiler-dom';
import * as Vue from 'vue';
const render = new Function('Vue', compile(
  '<div><span>static</span><p>{{ text }}</p></div>',
  { mode: 'function', prefixIdentifiers: true }
).code)(Vue);
const root = render({ text: 'A' }, []);
console.log(root.dynamicChildren.map(node => node.type)); // ['p']
```

源码再看[patchBlockChildren](https://github.com/vuejs/core/blob/v3.5.43/packages/runtime-core/src/renderer.ts)如何消费编译结果。

<a id="k04"></a>

### 4. 静态复用、缓存与正确性

静态提升/缓存减少重复创建，不代表共享一份已挂载可变VNode在任意位置。v-once表示有意只算一次，v-memo按显式依赖控制复用，漏依赖会变成陈旧UI。

手写render或动态slot可能需要BAIL等回退完整比较；运行时不能假定所有VNode都有可靠编译提示。调试时确认模板是否经编译、当前flag与结构是否匹配，再评价优化。

省掉框架比较不等于省掉浏览器layout/paint，真实收益仍受DOM规模与业务计算影响。

<a id="summary"></a>

## 三、知识小结

shapeFlag分类，patchFlag提示变化，dynamicChildren提供结构内的动态列表。静态/动态协作成立依赖正确编译信息，特殊标志与回退保护通用路径。

<a id="interview"></a>

## 四、面试题与答案

<a id="vsr12-01"></a>

### VSR12-01 [P0·原理] Patch Flag和shapeFlag有什么不同？

**回答：** shapeFlag描述节点及children类别，patchFlag告知更新时可利用的动态维度。一个用于分流形态，一个用于优化比较，不能互换。

对应讲解：[变化维度与特殊标志](#k01)。

<a id="vsr12-02"></a>

### VSR12-02 [P0·原理] 为什么动态属性名需要FULL_PROPS？

**回答：** 键集合可能增删，不能只按预先已知动态键检查。运行时要比较更完整props，处理旧键移除；其他独立优化仍可能存在。

对应讲解：[在产物中观察 TEXT、CLASS 与 FULL_PROPS](#k02)。

<a id="vsr12-03"></a>

### VSR12-03 [P0·原理] Block Tree是不是把所有VNode拍平成一维数组？

**回答：** 不是，它在合适结构边界收集相关动态节点，结构变化仍有分支和列表处理。稳定block中的对应patch不能覆盖任意树增删。

对应讲解：[openBlock、dynamicChildren 与稳定结构](#k03)。

<a id="vsr12-04"></a>

### VSR12-04 [P1·源码] 为何不能把CACHED/BAIL当普通位标记？

**回答：** 它们是特殊负值，源码明确按相等分支解释。盲目按位可能误命中普通标志；名称和行为也需匹配版本。

对应讲解：[变化维度与特殊标志](#k01)。

<a id="vsr12-05"></a>

### VSR12-05 [P1·工程取舍] v-memo是否总能安全减少更新？

**回答：** 依赖必须覆盖真实变化条件，遗漏会留下陈旧UI。先有瓶颈证据，再检验正确性，不能只为减少render日志增加memo。

对应讲解：[静态复用、缓存与正确性](#k04)。
