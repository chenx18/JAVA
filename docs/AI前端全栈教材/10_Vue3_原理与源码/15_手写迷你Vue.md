# 15 手写迷你Vue

把前面机制连成一个可运行闭环：普通对象→依赖追踪→computed→队列→VNode→DOM。实现用于实验与面试推导，文件就在本专题examples目录，不需要复制一个不完整的代码片段拼装。

源码基线：**Vue v3.5.43**。区分公开行为、这个版本的内部实现与本文教学模型；代码块各自独立，使用 Vue 包的例子在安装相应版本的 Node ESM 或前端工程中运行。

## 一、本章目录

- [文件、运行与支持范围](#k01)
- [从 effect 到 computed](#k02)
- [队列、根渲染与卸载](#k03)
- [VNode、key与DOM事件](#k04)
- [最小编译与验证路线](#k05)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 文件、运行与支持范围

完整代码：[mini-vue.mjs](examples/mini-vue.mjs)，交互页面：[index.html](examples/index.html)。在examples目录用已有静态服务器启动，例如安装了Python时运行 `python -m http.server 5174 --bind 127.0.0.1`，浏览器访问 `http://127.0.0.1:5174/`；也可用编辑器本地预览服务。浏览器ES模块应经HTTP加载，不依赖file双击策略。

支持：可扩展普通对象的嵌套代理、get/set/delete/has/枚举追踪、effect及stop、ref容器、只读computed、微任务去重队列、HTML元素/文本、有限事件与属性、同层keyed/unkeyed子节点、一个根setup/render和简单文本插值编译。

不支持：响应式数组/Map/Set、原型继承/特殊描述符/运行中冻结、watch、完整组件props/slots/子组件生命周期、SVG、Fragment、Teleport、Suspense、SSR/hydration以及Vue所有DOM属性语义。响应式数组被显式拒绝，示例列表使用静态记录与响应式反转开关。

<a id="k02"></a>

### 2. 从 effect 到 computed

先读effect→track→trigger：effect每次运行清理旧Set关系并保存/恢复活动effect；get向目标键登记，写入通过变化判断通知；stop解除自动订阅，手动runner仍可运行。

ref在此直接用reactive({value})演示容器，不同于真实RefImpl独立Dep。computed内部lazy effect缓存结果，源变化时先置dirty并通知使用者，下次读取才重算。教学版本不实现3.5的Dep/Link版本优化；复杂同步菱形依赖也不承诺与Vue完全相同的去重次数。

```js
import { reactive, effect, computed } from './examples/mini-vue.mjs';
const state = reactive({ n: 1 });
let calculations = 0;
const double = computed(() => { calculations++; return state.n * 2; });
console.log(double.value, double.value, calculations); // 2 2 1
state.n = 2;
console.log(double.value, calculations); // 4 2
const values = [];
const run = effect(() => values.push(state.n));
state.n = 3;
run.stop();
state.n = 4;
console.log(values); // [2,3]
```

先能手推依赖关系再改代码，比逐行默写变量名更有用。

<a id="k03"></a>

### 3. 队列、根渲染与卸载

createApp(setup)在mount时执行setup一次，要求返回render函数；同步创建的effect纳入本实现的局部scope。渲染effect以稳定job排队，多个同步写入由Set去重，nextTick等待本轮flush。

job检查mounted，unmount先让已排队任务失效，停止scope内效果，再清理子树事件并删除节点。队列异常会记录并在完成本批其他任务后拒绝flush Promise，finally恢复调度状态；调用者负责处理错误。

示例没有父子uid排序、pre/post队列、完整错误边界和组件树。停止root scope的效果不等于自动关闭用户创建的外部timer或网络，实际接入必须由所有者清理。

<a id="k04"></a>

### 4. VNode、key与DOM事件

h把文本归一化为文本VNode，元素VNode存props/key/children/el。patch按type/key复用或替换，事件使用稳定listener包装，更新其value而不反复注册；value/checked/disabled走简化DOM property分支，其余普通属性按声明范围处理。

无key列表按位置patch；全key列表先清理删除项，再按新列表倒序复用/插入，以右侧节点为anchor。它保持业务节点身份，**不实现LIS最少移动策略**。混合key与无key的同级列表被拒绝，同一VNode对象多处复用和保留宿主DOM引用后自行修改不在契约内。

在页面的甲输入框键入内容再反转，内容应随甲节点移动，而不是留在第一个位置。这比只检查最终文字序列更能证明复用。unmount会移除事件，留存的旧button引用也不应再修改应用。

<a id="k05"></a>

### 5. 最小编译与验证路线

compileText只识别纯文本与{{ identifier }}，返回创建span文本的render函数；不解析HTML、不支持任意表达式、不执行eval。未知绑定抛错，模板中的HTML样文本按文字显示，所以它只是parse/闭包生成的最小演示。

```js
import { compileText } from './examples/mini-vue.mjs';
const render = compileText('Hello {{ name }}');
const node = render({ name: 'Alice' });
console.log(node.type, node.children[0].text); // span Hello Alice
```

按顺序验证：同值赋值不重跑→分支清理→嵌套恢复→computed重复读与失效→同轮job去重→同key保留节点→删属性/删节点/换事件→排队后卸载→错误后队列可再用。观察操作与身份，不能只看页面最后显示正确。

完成后回看第5章的Link版本机制、第9章LIS、第10章组件实例、第11章AST与第12章block，逐项说明本实现为了可读性省略了什么。

<a id="summary"></a>

## 三、知识小结

迷你实现负责把机制连通并验证，真实Vue负责更完整语义与优化。能从状态写入追到DOM、证明资源停止，并说明实现限制，才算完成这次练习。

<a id="interview"></a>

## 四、面试题与答案

<a id="vsr15-01"></a>

### VSR15-01 [P1·编码] 实现时为什么先确定支持范围？

**回答：** 数组、集合、继承、描述符和组件树都有额外协议，未定义范围就容易把几行演示误称通用框架。明确普通对象和有限DOM契约后，测试才能验证具体承诺。

对应讲解：[文件、运行与支持范围](#k01)。

<a id="vsr15-02"></a>

### VSR15-02 [P1·编码] computed为什么不能直接每次调用getter？

**回答：** 那样失去缓存；需要记录依赖变化使结果失效，下次读时才求值，并让使用者订阅computed自身。教学dirty模型与真实版本优化要区分。

对应讲解：[从 effect 到 computed](#k02)。

<a id="vsr15-03"></a>

### VSR15-03 [P1·编码] stop了effect为何还要检查mounted？

**回答：** 旧更新可能已进入任务队列，移除依赖不自动删除过去排队的闭包。执行前检查失效状态才能防止卸载后再次写DOM，外部资源也需清理。

对应讲解：[队列、根渲染与卸载](#k03)。

<a id="vsr15-04"></a>

### VSR15-04 [P1·编码] 没有LIS的keyed patch能正确吗？

**回答：** 可以按映射和锚点得到正确顺序并复用节点，但可能比优化算法多移动。正确性和最少移动是不同验收点，不能声称与Vue性能等价。

对应讲解：[VNode、key与DOM事件](#k04)。

<a id="vsr15-05"></a>

### VSR15-05 [P1·编码] compileText是否可以加载任意Vue模板？

**回答：** 不能，它只识别文本与简单标识符插值，没有HTML AST、指令、作用域和平台编译。它用来说明解析与生成阶段，升级支持必须补语法和测试。

对应讲解：[最小编译与验证路线](#k05)。
