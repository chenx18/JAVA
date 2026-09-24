# 04 reactive、readonly 与代理边界

reactive返回对象代理，响应式建立在经过代理的操作上。原对象、代理身份、嵌套结构、解构和只读视图的区别，是排查状态不更新的主要线索。

## 一、本章目录

- [代理身份与访问路径](#k01)
- [深层、浅层与 ref 解包](#k02)
- [readonly、toRaw、markRaw 与检测](#k03)
- [工程选择与常见排错路径](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 代理身份与访问路径

```ts
import { reactive, isReactive, toRaw } from 'vue';
const raw = { count: 0 };
const state = reactive(raw);
console.log(state === raw, isReactive(state), toRaw(state) === raw); // false true true
state.count++;
console.log(raw.count); // 1
```

修改代理通常写到目标对象，但直接操作raw会绕过相应追踪/触发入口。应稳定地使用响应式代理，避免长期保留raw再混用两种身份。对同一目标重复reactive通常返回缓存的代理，而不是无限生成新代理。

reactive适用于可支持的对象类别，不直接代理number等原始值，Date或第三方内部槽类也不能假定按普通对象一概深追踪。

<a id="k02"></a>

### 2. 深层、浅层与 ref 解包

reactive对嵌套可支持对象提供深层响应式访问；普通对象属性中的ref可自动解包，数组索引和Map等集合元素中的ref通常仍需.value，避免混为一谈。

```ts
import { reactive, ref } from 'vue';
const value = ref(1);
const object = reactive({ value });
const list = reactive([value]);
console.log(object.value, list[0]!.value); // 1 1
```

shallowReactive只追踪根属性，不深转换，也不做同样的深层ref解包。将浅层代理嵌进深层代理树容易产生不一致理解，应明确边界用途。

解构reactive的原始值属性会保存快照，整体替换局部state变量也可能让其他持有旧代理的代码继续读旧对象；需要整体替换时ref容器通常更直观。

<a id="k03"></a>

### 3. readonly、toRaw、markRaw 与检测

readonly/shallowReadonly提供禁止相应路径写入的代理视图，不是Object.freeze，也不是权限机制；原对象若经其他可写引用变化，只读视图仍可观察相关变化。

toRaw临时取出原对象适合某些集成操作，不建议作为长期绕过响应式的状态来源。markRaw将对象标记为不被代理，适合某些第三方实例，但嵌套对象若另行进入响应式系统仍可能产生身份差异。

isReactive、isReadonly、isProxy用于检查视图特性，不能代替业务数据schema或生命周期判断。

<a id="k04"></a>

### 4. 工程选择与常见排错路径

排查不更新时依次看：值是否进入支持的响应式容器、读写是否经过同一代理/ref、是否直接解构成快照、是否用了shallow/markRaw、观察者是否已停止、DOM是否尚未flush。

大型只读数据可采用浅层容器与替换，减少深层追踪成本；频繁编辑的局部表单可用reactive对象。不要仅因数据大就盲目markRaw，也不要让所有外部类实例进入深代理。

后端返回数据还需运行时验证；Proxy拦截页面访问不是安全隔离，用户仍能直接请求接口。

<a id="summary"></a>

## 三、知识小结

reactive的关键是稳定代理与访问路径，浅层/只读/raw各有明确语义。对象身份和解构快照问题，往往比“Vue没监听到”更接近真实原因。

参考：[Vue Reactivity Fundamentals](https://vuejs.org/guide/essentials/reactivity-fundamentals.html)；[Vue Advanced Reactivity](https://vuejs.org/api/reactivity-advanced.html)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="vue04-01"></a>

### VUE04-01 [P0·原理] reactive是否把原对象原地变成代理？

**回答：** 它返回代理对象，身份通常与raw不同，操作代理可转发到目标。直接读写raw可能绕过通知，因此应统一使用响应式路径。

对应讲解：[代理身份与访问路径](#k01)。

<a id="vue04-02"></a>

### VUE04-02 [P0·原理] 解构reactive后为什么有时不更新？

**回答：** 原始值属性解构得到当时的值，后续不再通过代理属性读取。可用toRef/toRefs或getter保留关系；对象引用的具体行为又需分别分析，不能说所有解构都绝对失效。

对应讲解：[深层、浅层与 ref 解包](#k02)。

<a id="vue04-03"></a>

### VUE04-03 [P1·基础] readonly是否意味着底层对象永远不变？

**回答：** 不是，它限制通过该视图的写入，不阻止其他可写引用更新原对象，也不等于冻结或授权。只读API仍需明确数据所有者。

对应讲解：[readonly、toRaw、markRaw 与检测](#k03)。

<a id="vue04-04"></a>

### VUE04-04 [P1·工程取舍] 什么时候用markRaw或shallowReactive？

**回答：** 当第三方实例不适合代理、或大块数据采用明确的浅层更新策略时可考虑。代价是内部变化不会自动按深响应式通知，且可能产生身份差异，要由使用方维护契约。

对应讲解：[工程选择与常见排错路径](#k04)。
