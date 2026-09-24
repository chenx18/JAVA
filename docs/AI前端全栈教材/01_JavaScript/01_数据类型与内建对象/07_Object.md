# 07 Object

Object 的核心不是一张方法名表，而是属性由什么组成、属性在哪一层、某次操作会读取或改变什么。理解这三点，枚举、合并、冻结与原型问题才有统一依据。

## 一、本章目录

- [对象创建、属性键与访问](#k01)
- [数据属性与访问器属性](#k02)
- [自有、继承、可枚举与 Symbol](#k03)
- [合并、转换与分组 API](#k04)
- [属性定义、原型与对象完整性 API](#k05)
- [实例方法、原型污染与安全边界](#k06)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 对象创建、属性键与访问

对象字面量适合普通记录；Object.create(proto,descriptors?) 显式选择原型及描述符；Object(value) 对非空原始值可产生包装对象，对已有对象通常返回自身。new Object() 一般没有字面量更清楚。

属性键为字符串或 Symbol。obj[expr] 先计算表达式，再转换为属性键；数字键会变成字符串。Map 则可直接以任意值作键。

```js
const obj = { 1: 'one', ['user' + 'Name']: 'Alice' };
console.log(obj[1] === obj['1']); // true
console.log(obj.userName); // Alice
const dict = Object.create(null);
dict.key = 'value';
console.log(Object.getPrototypeOf(dict)); // null
```

无原型对象适合字典，但没有继承的 toString/hasOwnProperty 等方法。?. 只在接收者为 null/undefined 时短路，不验证属性类型，也不会使未声明变量变得安全。

<a id="k02"></a>

### 2. 数据属性与访问器属性

数据描述符含 value、writable、enumerable、configurable；访问器描述符含 get、set、enumerable、configurable。同一描述符不能同时指定 value/writable 与 get/set。

| 标志 | 含义 | 不代表什么 |
| --- | --- | --- |
| writable | 数据属性能否被普通赋值改变 | 不限制嵌套对象内容 |
| enumerable | 是否进入某些枚举/复制操作 | 不等于不能直接读 |
| configurable | 能否删除及进行多数重新定义 | false 不是所有变化都禁止，具体规则需满足 |

```js
const user = {};
Object.defineProperty(user, 'id', { value: 1 });
console.log(Object.keys(user)); // []
console.log(Object.getOwnPropertyDescriptor(user, 'id'));
// {value:1,writable:false,enumerable:false,configurable:false}
```

defineProperty 新建属性时，省略的布尔标志默认 false，区别于普通对象字面量属性默认可写、可枚举、可配置。对已有属性重新定义时，未提供的字段通常保留旧值。读取 getter 会执行函数，枚举并取值、复制、序列化可能因此有副作用。

<a id="k03"></a>

### 3. 自有、继承、可枚举与 Symbol

| 操作 | 自有字符串 | 自有 Symbol | 继承属性 |
| --- | --- | --- | --- |
| Object.keys/values/entries | 仅可枚举 | 不含 | 不含 |
| Object.getOwnPropertyNames | 全部 | 不含 | 不含 |
| Object.getOwnPropertySymbols | 不含 | 全部 | 不含 |
| Reflect.ownKeys | 全部 | 全部 | 不含 |
| for...in | 可枚举 | 不含 | 包含可枚举字符串属性 |
| Object.hasOwn(obj,key) | 判断自有存在 | 支持 | 不检查 |
| key in obj | 判断存在 | 支持 | 检查整条原型链 |

存在与值为 undefined 不同；原型上可枚举属性也不是对象自己的数据。普通对象常见键顺序为数组索引字符串先按数值升序，再其他字符串按插入顺序，最后 Symbol 按插入顺序；不是“对象键永远无序”，也不能无视特殊对象和 Proxy。

<a id="k04"></a>

### 4. 合并、转换与分组 API

| API | 返回/是否修改目标 | 行为 |
| --- | --- | --- |
| Object.assign(target,...sources) | target；修改 target | 复制来源自有可枚举字符串/Symbol值，读取 getter，可能调用目标 setter |
| {...source} | 新普通对象 | 浅复制自有可枚举属性值，不保留原描述符和原型 |
| Object.entries/values/keys(obj) | 新数组 | 只取自有可枚举字符串键 |
| Object.fromEntries(iterable) | 新普通对象 | 键值对转对象，重复键以后者覆盖；支持 Symbol 键 |
| Object.groupBy(items,fn) | 分组对象 | 分组键转属性键，结果为无原型对象；按运行时支持 |
| Object.is(a,b) | Boolean | SameValue 比较，NaN 相等、正负零不同 |

```js
const source = { nested: { n: 1 } };
const copy = { ...source };
copy.nested.n++;
console.log(source.nested.n); // 2
console.log(Object.fromEntries([['x', 1], ['x', 2]])); // {x:2}
```

浅复制只分离外层对象，嵌套引用仍共享。assign 与 spread 在 setter、错误发生后的部分写入等行为上不同，不是所有场景都可机械互换。复制描述符用 getOwnPropertyDescriptors 配合 defineProperties，但这也不是深复制。

<a id="k05"></a>

### 5. 属性定义、原型与对象完整性 API

| API | 返回/用途 |
| --- | --- |
| defineProperty(obj,key,descriptor)、defineProperties(obj,descriptors) | 定义属性，返回 obj |
| getOwnPropertyDescriptor(obj,key)、getOwnPropertyDescriptors(obj) | 读取一个/全部自有描述符 |
| getPrototypeOf(obj)、setPrototypeOf(obj,proto) | 读/设原型；设置返回 obj |
| preventExtensions(obj)、isExtensible(obj) | 禁止新增自有属性/检查可扩展 |
| seal(obj)、isSealed(obj) | 禁止扩展且自有属性不可配置/检查 |
| freeze(obj)、isFrozen(obj) | 在 seal 基础上让自有数据属性不可写/检查 |

三种限制都不自动递归冻结。freeze 也不会把 getter 返回值变成不可变数据；访问器仍有自身语义。严格模式下某些非法赋值抛错，非严格代码中可能静默失败。动态 setPrototypeOf 会使对象模型与优化更复杂，应优先在创建时确定结构。

<a id="k06"></a>

### 6. 实例方法、原型污染与安全边界

Object.prototype 常见方法：hasOwnProperty(key) 判断自有属性，propertyIsEnumerable(key) 判断自有可枚举属性，isPrototypeOf(obj) 判断原型链关系，toString/toLocaleString/valueOf 参与展示或转换。对象可能遮蔽这些方法，无原型对象也不继承它们，因此自有判断优先 Object.hasOwn。

把外部对象或可控路径递归合并进配置，可能经 __proto__、constructor.prototype 等路径影响不该修改的原型。策略应优先限制允许的字段与层级、使用可信合并实现、对字典选 Map 或无原型对象，并测试嵌套危险路径；只过滤最外层 __proto__ 不充分。

JSON.parse 本身不等于执行脚本，但解析结果再进入赋值、合并、DOM 或动态执行时可能产生风险。schema 校验和后端授权不能由 Object.freeze 或 Proxy 代替。

<a id="summary"></a>

## 三、知识小结

理解对象操作，依次问：属性是自有还是继承？字符串键还是 Symbol？是否可枚举？读取是否触发 getter？写入是否改目标？复制到的是值还是描述符？这比把所有 Object 方法当成相同的“遍历工具”更容易记。

参考：[MDN Object](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object)。较新 API 按目标运行时核对支持，示例各自独立。

<a id="interview"></a>

## 四、面试题与答案

<a id="d07-01"></a>

### D07-01 [P0·原理] 属性值是 undefined，代表属性不存在吗？

**回答：** 不代表。读取缺失属性和读取值为undefined的属性都可能得到undefined。Object.hasOwn检查自有存在，in检查自身加原型链；先确定你关心的是哪种存在关系。

对应讲解：[自有、继承、可枚举与 Symbol](#k03)。

<a id="d07-02"></a>

### D07-02 [P0·基础] defineProperty 默认标志为什么容易踩坑？

**回答：** 新建属性时writable/enumerable/configurable默认false，和对象字面量普通属性不同。结果可能能直接读取却不出现在Object.keys里，也不能直接修改或删除。定义已有属性时，省略字段的行为又要按原描述符处理。

对应讲解：[数据属性与访问器属性](#k02)。

<a id="d07-03"></a>

### D07-03 [P0·原理] assign 与展开有什么共同点和区别？

**回答：** 两者常用于浅复制自有可枚举属性，嵌套对象共享，getter会被读取。assign写入指定目标且可能触发其setter；展开在新对象上创建属性，不保留来源原型和描述符。错误时assign还可能留下已写入部分。

对应讲解：[合并、转换与分组 API](#k04)。

<a id="d07-04"></a>

### D07-04 [P1·基础] keys、for...in、Reflect.ownKeys 的范围如何区别？

**回答：** keys仅自有可枚举字符串键，for...in还可能包含原型链上的可枚举字符串键，Reflect.ownKeys包括全部自有字符串和Symbol键。选择前应明确继承、枚举性和键种类三维条件。

对应讲解：[自有、继承、可枚举与 Symbol](#k03)。

<a id="d07-05"></a>

### D07-05 [P1·原理] freeze 和 const 能保证对象深度不可变吗？

**回答：** 不能。const限制绑定，freeze限制对象自身的属性定义和写入，两者都不递归冻结嵌套对象。访问器、共享引用和特殊容器还有额外语义，深不可变需要明确支持范围。

对应讲解：[属性定义、原型与对象完整性 API](#k05)。

<a id="d07-06"></a>

### D07-06 [P1·工程取舍] 原型污染如何进入业务？

**回答：** 常见入口是把用户可控路径或深层对象无约束合并到共享配置，危险键路径可能修改原型。应校验允许字段、限制深度、选择安全的数据结构和合并实现，并测试嵌套constructor/prototype等路径。

对应讲解：[实例方法、原型污染与安全边界](#k06)。
