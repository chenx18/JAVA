# 04 this、call、apply、bind与new

this题的稳定方法是先识别函数种类，再查看本次调用表达式。函数放在哪个对象里、变量起什么名字，都不能单独决定this。

## 一、本章目录

- [普通函数的调用接收者](#k01)
- [call、apply、bind](#k02)
- [箭头函数与对象字面量](#k03)
- [new 与构造返回值](#k04)
- [混合调用的判断顺序](#k05)
- [手写题的支持范围与验证](#k06)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 普通函数的调用接收者

```js
'use strict';
function read() { return this?.name; }
const a = { name: 'A', read };
const b = { name: 'B', read: a.read };
const detached = a.read;
console.log(a.read(), b.read(), detached()); // A B undefined
```

a和b保存同一函数，却因调用时的接收者不同而得到不同结果。detached只保存函数值，没有把“来自a”这个属性引用一起保存。

严格普通函数独立调用this为undefined。非严格函数收到undefined/null时通常以自身realm的全局对象作为this，原始值接收者可能被装箱。函数自身是否严格很重要，不能只看调用代码所在模块。

宿主回调由宿主决定调用方式，例如DOM普通监听器的this通常为currentTarget；timer的宿主行为不能不加说明地直接套成所有环境都是window。

<a id="k02"></a>

### 2. call、apply、bind

| 方法 | 执行时机 | 实参 | 返回 |
| --- | --- | --- | --- |
| call(thisArg,...args) | 立即 | 逐项 | 目标调用结果 |
| apply(thisArg,argsArray?) | 立即 | 数组或类数组 | 目标调用结果 |
| bind(thisArg,...prefix) | 不立即 | 预置参数，之后继续追加 | 新绑定函数 |

```js
function sum(a, b) { return this.base + a + b; }
const ctx = { base: 10 };
console.log(sum.call(ctx, 1, 2), sum.apply(ctx, [1, 2])); // 13 13
const bound = sum.bind(ctx, 1);
console.log(bound(2), bound.call({ base: 100 }, 2)); // 13 13
```

对已绑定函数普通调用，再call或再次bind不能改掉第一次固定的this，但能继续传递/预置参数。每次bind产生新函数，解绑事件监听时必须保留同一函数引用。

apply的列表是类数组参数语义，不等于任意iterable；调用spread则读取迭代协议。两者在巨大参数列表上都要考虑运行时限制。

<a id="k03"></a>

### 3. 箭头函数与对象字面量

箭头不建立自身this，它沿外层环境取值。对象字面量不是this作用域，所以属性里的箭头不会自动指向这个对象。

```js
function make() {
  return {
    value: 'object',
    arrow: () => this.value,
    method() { return this.value; }
  };
}
const obj = make.call({ value: 'outer' });
console.log(obj.arrow(), obj.method()); // outer object
console.log(obj.arrow.call({ value: 'other' })); // outer
```

判断arrow时看它在make这次调用的环境；判断method时看obj.method()。若把method也取出，它的调用接收者又会改变。类实例上的箭头字段常能固定实例this，但每个实例要创建自己的函数，与共享原型方法有成本差异。

<a id="k04"></a>

### 4. new 与构造返回值

一般function构造器可用四步模型理解：创建对象→连接构造器prototype→以新对象为this执行初始化→依据显式返回值决定最终结果。

```js
function A() { this.x = 1; return 2; }
function B() { this.x = 1; return { x: 9 }; }
console.log(new A().x, new B().x); // 1 9
```

返回原始值通常被忽略，返回非null对象或函数可替代新实例。Ctor.prototype不是对象时，一般默认原型规则会参与；new.target让函数知道本次构造目标。

箭头、async、生成器和方法简写不具有一般构造能力。class不能通过普通apply模拟构造，派生类还需要super/私有初始化等专门语义。可构造的原生bound函数在new时忽略预置this但保留预置参数，不能用“bind永远最高优先级”概括。

<a id="k05"></a>

### 5. 混合调用的判断顺序

依次排查：箭头走词法this；可构造的new走构造路径；普通调用原生bound使用已绑定this；其余普通函数再看call/apply、方法调用、独立调用及严格规则。

```js
'use strict';
const obj = {
  n: 1,
  method() { return this.n; }
};
const bound = obj.method.bind({ n: 2 });
console.log(bound.call({ n: 3 })); // 2
console.log(obj.method.call({ n: 3 })); // 3
```

这不是一条对任意对象属性都有效的神秘优先级链；先识别目标的内部行为才有意义。({method:arrow}).method()仍不会为箭头创建新的this。

<a id="k06"></a>

### 6. 手写题的支持范围与验证

普通new教学可以演示对象创建与返回值，但若直接Ctor.apply就不能完整支持class、私有字段和new.target。bind若只用箭头返回包装，则无法表达构造调用。题目应先说明是机制演示还是要求兼容原生。

实现call/apply时常见“给接收者临时挂Symbol属性再调用”的办法，会遇到null、原始值、冻结对象、Proxy、严格this和清理问题；不能只通过一个普通对象示例就说实现完成。已有Reflect.apply可表达标准调用，但借用它并不是从零实现引擎内部调用。

验证至少包括：普通调用、方法脱离、绑定后call、预置参数、new绑定函数、构造返回对象/原始值、不可构造目标和异常路径。

<a id="summary"></a>

## 三、知识小结

this复述顺序：函数种类→调用形态→严格规则→构造边界。call/apply立即调用，bind建立新函数；new与箭头各有独立语义。用明确接收者和返回值验证推断。

参考：[MDN this](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/this)；[MDN bind](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Function/bind)。较新 API 按目标运行时核对支持，示例各自独立。

<a id="interview"></a>

## 四、面试题与答案

<a id="c04-01"></a>

### C04-01 [P0·原理] this由定义位置还是调用位置决定？

**回答：** 一般普通函数主要由调用形态决定，箭头沿定义位置的外层this环境读取，原生bound普通调用使用既有绑定。先分函数种类，再讨论obj.fn、独立调用、call/apply或new，不能只答“谁调用指向谁”。

对应讲解：[混合调用的判断顺序](#k05)。

<a id="c04-02"></a>

### C04-02 [P0·基础] call、apply、bind怎么比较？

**回答：** call/apply立即执行，区别在逐项参数或类数组列表；bind返回预置this和部分参数的新函数。箭头this不受其改变，new可构造绑定函数又会忽略预置this。

对应讲解：[call、apply、bind](#k02)。

<a id="c04-03"></a>

### C04-03 [P0·原理] 对象方法传给回调后为什么this变了？

**回答：** 传递的是函数值，不是原来的属性引用；接收方如何调用决定普通函数接收者。可用bind保留接收者或箭头包装显式调用，但事件解绑要保存同一个函数引用。

对应讲解：[普通函数的调用接收者](#k01)。

<a id="c04-04"></a>

### C04-04 [P1·编码] 怎样演示普通构造函数的new流程？

**回答：** 在明确仅支持一般function构造器的前提下，创建指定原型对象，以它为this调用，按返回类型决定使用返回对象还是实例。下面不模拟class、派生类、私有字段、new.target和完整可构造性检查，不能作为原生替代。

```js
function createInstance(Ctor, ...args) {
  const prototype = Ctor.prototype;
  const validPrototype = prototype !== null &&
    (typeof prototype === 'object' || typeof prototype === 'function');
  const instance = Object.create(validPrototype ? prototype : Object.prototype);
  const returned = Reflect.apply(Ctor, instance, args);
  return returned !== null &&
    (typeof returned === 'object' || typeof returned === 'function')
    ? returned : instance;
}
function User(name) { this.name = name; }
const user = createInstance(User, 'Alice');
console.log(user.name, user instanceof User); // Alice true
```

**追问与回答：** 若要求生产级构造语义应使用new或Reflect.construct，并写明兼容边界。

对应讲解：[手写题的支持范围与验证](#k06)。

<a id="c04-05"></a>

### C04-05 [P1·编码] bind教学实现如何区分普通调用和new？

**回答：** 包装函数检查new.target。普通调用用固定receiver，构造调用使用Reflect.construct并保留参数。本例借用原生Reflect表达机制，不完整模拟原生bound的name/length/prototype/instanceof等细节。

```js
function bindDemo(target, receiver, ...prefix) {
  if (typeof target !== 'function') throw new TypeError('function required');
  function bound(...rest) {
    const args = [...prefix, ...rest];
    if (new.target) return Reflect.construct(target, args, new.target === bound ? target : new.target);
    return Reflect.apply(target, receiver, args);
  }
  return bound;
}
function User(name) { this.name = name; }
const Bound = bindDemo(User, { ignored: true }, 'A');
console.log(new Bound().name); // A
```

**追问与回答：** 手写时最重要的是先约定支持范围，再测构造和普通调用，而不是只给目标临时挂属性。

对应讲解：[手写题的支持范围与验证](#k06)。

<a id="c04-06"></a>

### C04-06 [P1·编码] 不借 call/apply，怎样演示“指定对象调用”？

**回答：** 可暂时把函数作为对象属性，再用对象方法形式调用，最后删除临时属性。下例仅接受非空可扩展普通对象和数组实参，用于解释调用接收者；不实现原生 call/apply 对 null、原始值、类数组、严格 this 或代理的全部语义。

```js
function applyOnObject(fn, receiver, args = []) {
  if (typeof fn !== 'function' || receiver === null ||
      typeof receiver !== 'object' || !Array.isArray(args)) {
    throw new TypeError('object receiver and array arguments required');
  }
  const key = Symbol('temporary-call');
  Object.defineProperty(receiver, key, { value: fn, configurable: true });
  try { return receiver[key](...args); }
  finally { delete receiver[key]; }
}
function callOnObject(fn, receiver, ...args) {
  return applyOnObject(fn, receiver, args);
}
function read(suffix) { return this.name + suffix; }
const owner = { name: 'A' };
console.log(callOnObject(read, owner, '!')); // A!
console.log(Reflect.ownKeys(owner)); // ['name']
```

**追问与回答：** 冻结或不可扩展对象不允许增加临时属性，Proxy 还可能改变定义、调用或删除行为；原生调用并不要求这样修改接收者。生产使用原生方法或 Reflect.apply，此模型不能作为完整 polyfill。

对应讲解：[手写题的支持范围与验证](#k06)。
