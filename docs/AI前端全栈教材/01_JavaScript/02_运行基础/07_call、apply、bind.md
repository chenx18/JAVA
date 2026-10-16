# call、apply、bind

<!-- core-mindmap:start -->
[![call、apply、bind 知识导图](examples/c07-mindmap.png)](examples/c07-mindmap.svg)

<details>
<summary>PlantUML 源码</summary>

```plantuml
@startmindmap
skinparam defaultFontName Microsoft YaHei
skinparam defaultFontSize 14
skinparam dpi 100
skinparam backgroundColor white
skinparam shadowing false

* call、apply、bind
**[#EDE7F6] 三个方法
*** call：立即调用，逐项传参
*** apply：立即调用，读取参数列表
*** bind：返回新函数，以后调用
**[#E3F2FD] 参数与返回
*** 第一个参数提供this
*** apply支持数组和类数组
*** bind预先参数在前，后续参数在后
*** 执行时返回目标结果并传播错误
**[#E0F2F1] 实际应用
*** 方法借用与现有对象初始化
*** 固定回调this并保存函数身份
*** new绑定函数保留参数、忽略指定this
**[#FFF3E0] 注意
*** 箭头this不能被替换
*** bind不复制原函数全部属性
*** Father.call不自动建立继承
*** 手写模型不等于完整原生实现

@endmindmap
```

</details>

<!-- core-mindmap:end -->

**口述总览：** call 和 apply 都会立即调用函数，并为这次调用提供 this，区别主要在参数怎么传：call 逐项传参，apply 从数组或类数组对象中读取参数。bind 不立即执行，而是返回一个记住 this 和部分参数的新函数，供以后调用。它们不会永久修改原函数；箭头函数和 new 调用绑定函数还有各自的限制。

本篇回答“怎样指定 this、怎样传参、什么时候执行”。默认指向和箭头外层 this 的判断，先看[this 指向](06_this指向.md)。

除特别说明外，各代码块在现代 Node.js ES 模块中独立运行。先读常用 API 与例子，再看使用边界和文末实现。

## 为什么需要主动指定 this

把对象的方法交给其他代码后，对方可能把它单独调用，方法不会自动记住原来的对象。例如事件回调、定时任务或方法借用中，希望明确让函数使用某个对象，就可以考虑这些方法。

选择时先问两件事：现在是否需要执行，以及参数是逐项提供还是已经放在列表里。立即执行用 call 或 apply；需要生成一个以后执行的函数，则使用 bind。

<a id="k01"></a>

## call、apply、bind：立即调用与返回新函数

**口述：** call 和 apply 立即调用目标函数，为这次调用提供 this 和参数。bind 暂不执行，返回一个新函数，记住目标函数、指定的 this 和预先提供的参数。它们不会永久修改原函数。

这三个方法通常从 Function.prototype 继承。对于普通、未绑定的函数，call/apply 提供的 this 按[上一篇的严格或非严格规则](06_this指向.md#k01)处理；箭头和已绑定函数有自己的限制。

### 逐项传参并调用：call(thisArg, ...args)

第一个参数是本次希望提供的 this，后面的参数逐项传给函数。返回目标函数的返回值，目标抛错则继续向外抛出。

### 从参数列表取值并调用：apply(thisArg, argsArray)

第二个参数可以是数组，也可以是有 length 和数字索引的类数组对象；其中每一项分别成为一个实参。传 null 或 undefined，表示不提供实参。

### 固定 this 和部分参数：bind(thisArg, ...prefix)

返回一个新函数，之后普通调用时使用指定的 this。先使用 bind 时提供的参数，再追加后续调用的参数；返回值仍由目标函数决定。

<!-- example: c04-call-apply-bind -->
```js
function sum(a, b) { return this.base + a + b; }
const owner = { base: 10 };

console.log(sum.call(owner, 1, 2)); // 13
console.log(sum.apply(owner, [1, 2])); // 13
console.log(sum.apply(owner, { 0: 1, 1: 2, length: 2 })); // 13

const addOne = sum.bind(owner, 1);
console.log(addOne(2)); // 13
console.log(addOne.call({ base: 100 }, 2)); // 13
console.log(sum.call({ base: 100 }, 1, 2)); // 103
```

addOne 保存了 owner 和参数 1，所以再用 call 也不能换掉它普通调用时的 this；原来的 sum 没有被改写，仍可通过 call 使用其他对象。

再次 bind 也不能替换第一次已经固定的普通调用 this，但可以继续提供参数。箭头的 this 不能被这三个方法替换，但它们仍然可以传递参数。

### apply 是展开参数列表，不是把整个数组传进去

<!-- example: c04-apply-arguments -->
```js
function inspect(first, second) {
  console.log(JSON.stringify(first), second);
}
inspect.apply(null, ['pink', 'blue']); // "pink" blue
inspect.call(null, ['pink', 'blue']); // ["pink","blue"] undefined
```

第一种调用产生两个实参，第二种调用只产生一个数组实参。apply 不会把列表本身自动作为一个参数交给目标。

普通 Set 能被遍历，但没有供 apply 读取的 length，不能直接当作它的参数列表：

<!-- example: c04-apply-iterable -->
```js
function count(...args) { return args.length; }
const values = new Set([1, 2]);
console.log(count.apply(null, values)); // 0
console.log(count(...values)); // 2
console.log(count.apply(null, Array.from(values))); // 2
```

Math.max.apply(null, valuesArray) 可以逐项传入数组中的数值；Math.max 的计算不依赖 this，改传 Math 不会因此改变最大值。超大数组展开成实参可能超过引擎限制，应改用循环等方式。

### bind 返回的是新函数，不是完整复制原函数

每次调用 bind 都会创建不同的函数。它记住要执行的目标，并不会把目标的源代码和全部自有属性复制一遍。

<!-- example: c04-bind-identity -->
```js
function handle() { return this.name; }
handle.label = 'original';
const owner = { name: 'Alice' };
const first = handle.bind(owner);
const second = handle.bind(owner);

console.log(first === second); // false
console.log(first()); // Alice
console.log(first.label); // undefined
```

事件注册与移除要保存同一个绑定函数，不能在移除时重新 bind。bind 只固定 this 使用哪个值，不冻结对象的属性。

### Father.call(this) 只借用初始化，不自动建立继承

<!-- example: c04-borrow-initialization -->
```js
function Father(name) { this.name = name; }
Father.prototype.say = function () { return this.name; };

function Son(name) {
  Father.call(this, name);
}
const son = new Son('Alice');
console.log(son.name); // Alice
console.log(typeof son.say); // undefined
console.log(son instanceof Father); // false
```

Father 在 Son 正在初始化的对象上写入了 name，所以属性存在。但 Son 的原型链没有因此连接到 Father.prototype，原型方法不会自动继承。

真正的原型关系要另外建立，见[原型与继承](../03_对象机制/03_原型与原型链.md)。如果 Father 是 class，则不能通过 Father.call(this) 普通调用，应按 class 的继承和 super 规则处理。

<a id="k02"></a>

## new 调用绑定函数：忽略指定的 this，保留参数

<!-- example: c04-bound-construction -->
```js
function User(name) { this.name = name; }
const owner = { name: 'unchanged' };
const BoundUser = User.bind(owner, 'Alice');
const user = new BoundUser();

console.log(user.name, owner.name); // Alice unchanged
console.log(user instanceof User, user instanceof BoundUser); // true true
console.log(Object.hasOwn(BoundUser, 'prototype')); // false
```

目标函数可以被 new 调用时，它经原生 bind 得到的函数也能构造。这里 Alice 仍作为参数使用，但初始化的是新对象，不是 owner。

因此不能把“bind 的 this 永远不变”推广到构造调用，也不能只凭函数有没有自己的 prototype 属性判断能否 new。

箭头函数绑定后仍不能 new。class 必须按构造方式调用，继承类还有 super 等规则，构造的基础过程和返回值规则见[this 指向篇](06_this指向.md#k04)。

<a id="k03"></a>

## 与普通调用放在一起，怎样判断优先使用哪个 this

**口述：** 判断 this 时，先区分普通函数、箭头函数和绑定后的函数，再看它是作为对象方法调用、单独调用，还是用于创建实例。如果函数是交给别人执行的回调，还要看对方怎样调用它。

可以按下面的顺序分析：

- 箭头：使用定义位置外层的 this，不能通过调用方式替换，也不能 new。
- new：先确认目标能构造；原生绑定函数的指定 this 在此时被忽略。
- bind 返回的函数被普通调用：继续使用已经指定的 this，仍按目标函数的严格性处理。
- 其他普通函数：call/apply 提供 this；obj.fn() 使用 obj；独立调用再区分严格模式。
- 定时器、事件等：明确浏览器或 Node.js，以及 API 怎样调用回调，不仅凭“回调”这个名称判断。

<!-- example: c04-mixed-calls -->
```js
const item = { n: 1, read() { return this.n; } };
const bound = item.read.bind({ n: 2 });

console.log(bound.call({ n: 3 })); // 2
console.log(item.read.call({ n: 3 })); // 3
try { new item.read(); }
catch (error) { console.log(error.name); } // TypeError
```

read 是对象方法简写，能正常调用，但不支持 new。分析完 this 后，还要看函数具体读取什么属性、返回什么值或在哪里抛错。

<a id="k04"></a>

### 手写实现：说明哪些情况支持，哪些没有模拟

手写 call、bind 或 new 可以帮助解释规则，但不能只通过几个对象例子，就说与原生完全等价。

- 临时把函数放到对象属性上再调用，会修改对象，遇到冻结对象或代理可能失败；原生 call/apply 不要求这样修改对象。
- 普通包装函数可能改变 name、length、prototype 和 instanceof 的表现。
- 使用 new.target 可以区分普通调用和构造调用；真正的构造步骤可交给 Reflect.construct，但这也不自动补齐原生 bind 的全部行为。

文末实现分别注明支持范围。实际业务通常直接使用原生方法。

<a id="summary"></a>

复述时按“是否立即调用 → 参数怎样提供 → 返回什么 → 哪些情况不能替换 this”展开。绑定用于保存调用安排，不等于复制函数、冻结对象或自动建立继承。

<a id="interview"></a>

## 试题与答案

<a id="c07-01"></a>

### C07-01 [P0·基础] call、apply、bind怎么比较？

**回答：** call、apply 立即调用目标，前者逐项传参，后者读取数组或类数组参数列表，返回目标结果。bind 返回记住 this 和部分参数的新函数，供以后调用，不是完整函数拷贝。箭头的 this 不能被替换，new 调用可构造的绑定函数时则忽略预先指定的 this。

<a id="c07-02"></a>

### C07-02 [P1·编码] bind教学实现如何区分普通调用和new？

**回答：** new.target 可以区分普通调用和构造调用。普通调用把指定的 this 和参数交给目标，构造调用借助 Reflect.construct 执行。本例没有完整模拟原生 bind 的函数名称、参数数量、prototype 与 instanceof 等行为。

<!-- example: c04-bind-demo -->
```js
function bindDemo(target, receiver, ...prefix) {
  if (typeof target !== 'function') throw new TypeError('function required');
  function bound(...rest) {
    const args = [...prefix, ...rest];
    if (new.target) {
      return Reflect.construct(target, args, new.target === bound ? target : new.target);
    }
    return Reflect.apply(target, receiver, args);
  }
  return bound;
}
function User(name) { this.name = name; }
const receiver = { base: 10 };
const add = bindDemo(function(a, b) { return this.base + a + b; }, receiver, 1);
console.log(add.call({ base: 100 }, 2)); // 13
const Bound = bindDemo(User, receiver, 'A');
console.log(new Bound().name, Object.hasOwn(receiver, 'name')); // A false
```

<a id="c07-03"></a>

### C07-03 [P1·编码] 不借 call/apply，怎样演示“指定对象调用”？

**回答：** 临时添加函数属性，通过属性形式调用，并在 finally 删除。本例仅支持非空可扩展普通对象和数组实参；不模拟 null、原始值、严格 this 或 Proxy 的全部行为。

<!-- example: c04-object-call-demo -->
```js
function applyOnObject(fn, receiver, args = []) {
  if (typeof fn !== 'function' || receiver === null ||
      typeof receiver !== 'object' || !Array.isArray(args)) {
    throw new TypeError('object receiver and array arguments required');
  }
  const key = Symbol('temporary');
  Object.defineProperty(receiver, key, { value: fn, configurable: true });
  try { return receiver[key](...args); }
  finally { delete receiver[key]; }
}
const receiver = { name: 'A' };
console.log(applyOnObject(function(suffix) { return this.name + suffix; }, receiver, ['!'])); // A!
try { applyOnObject(() => { throw new Error('failed'); }, receiver); }
catch (error) { console.log(error.message); } // failed
console.log(Reflect.ownKeys(receiver).join(',')); // name
try { applyOnObject(() => 1, Object.freeze({})); }
catch (error) { console.log(error.name); } // TypeError
```

参考：[MDN：call](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Function/call)、[MDN：apply](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Function/apply)、[MDN：bind](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Function/bind)。
