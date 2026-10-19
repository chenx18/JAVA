# this 指向

<!-- core-mindmap:start -->
[![this 指向 知识导图](examples/c06-mindmap.png)](examples/c06-mindmap.svg)

<details>
<summary>PlantUML 源码</summary>

```plantuml
@startmindmap
skinparam defaultFontName Microsoft YaHei
skinparam defaultFontSize 14
skinparam dpi 100
skinparam backgroundColor white
skinparam shadowing false

* this 指向
**[#EDE7F6] this 基础
*** this 是什么
**** 本次执行使用的特殊值\n不固定指向函数自身，也不一定是对象
**[#E3F2FD] 普通调用
*** 方法调用与独立调用
**** 普通未绑定方法看本次通过谁调用\n取出独立调用，不会自动记住原对象
*** 严格与非严格模式
**** 严格保留值，独立调用为 undefined
**** 非严格会替换 null、undefined 或装箱\n严格性看函数自身
*** 立即执行函数
**** 立即执行只说明调用时机\nthis 仍按函数类型和调用方式判断
**[#E0F2F1] 箭头与绑定
*** 箭头函数
**** 沿用定义位置外层 this\n对象字面量不另设 this
**** 不能替换 this，不等于对象内容不变
*** call、apply、bind
**** 为普通调用提供 this\n目标仍按自身严格性处理传入值
**** 箭头不被替换\n绑定函数用于构造时忽略绑定 this
left side
**[#FFF3E0] 回调与运行场景
*** 普通回调
**** this 看接收方怎样调用\n独立调用与对象属性调用不同
*** 递归
**** 每次都是新调用\n不会自动继承上次 this
*** 定时器
**** 页面普通回调：window\nNode.js 普通回调：通常为 Timeout
**** 箭头和绑定继续遵守自身规则
*** DOM 事件
**** 普通未绑定监听器执行期间\nthis 等于 currentTarget
**** currentTarget 看当前监听对象\ntarget 看事件目标
**[#FCE4EC] 类与构造
*** class 中的 this
**** 普通方法看调用\n实例箭头字段保留实例 this
**** 静态普通方法看通过哪个类调用
*** new 与构造返回值
**** 普通构造函数：创建对象、关联原型\n以新对象为 this 执行，再处理返回值
**** 明确返回非 null 对象或函数\n可替代新实例成为最终结果
**** 注意：教学模型不等于完整模拟 new
@endmindmap
```

</details>

<!-- core-mindmap:end -->

**口述总览：** this 的指向主要看函数类型和调用方式。普通函数作为对象的方法调用时，this 通常指向这个对象；单独调用时，严格模式下是 undefined，非严格模式下是全局对象。使用 new 调用普通构造函数时，this 指向新创建的实例。箭头函数没有自己的 this，会沿用定义位置外层的 this。函数交给定时器或事件监听执行时，还要看运行环境和具体调用规则。

**记忆线：** 先看函数类型 → 再看调用方式 → 检查严格模式与运行环境 → 遇到 new 再检查返回值。

本篇归纳为 **12 个知识点**，与导图的知识点分支一一对应。理解正文后，用[自查问题](#self-check)检查，再对照[试题与答案](#interview)练习口述。

本篇先回答“this 是谁，为什么是它”。需要主动指定 this 或比较三个调用方法时，接着读[call、apply、bind](07_call、apply、bind.md)。

除标注浏览器或 Node.js 专用的示例外，代码块分别在现代 Node.js ES 模块中运行。模块中的普通函数默认处于严格模式。

## this 是什么：函数执行时使用的一个值

**口述：** this 是函数执行时可以使用的一个特殊值，常用来访问当前这次调用关联的对象。它不固定指向函数自身，也不只由函数写在哪里决定；同一个普通函数，换一种调用方式，this 就可能不同。

例如执行 user.say() 时，say 中的 this 通常是 user。技术资料有时称 user 为这次调用的“接收者”，理解为“这次函数用哪个对象作为 this”即可。

this 也不保证一定是对象：严格模式下，它可以是 undefined、null 或数字等原始值。判断时先看函数种类和调用写法，再确定具体结果。

<a id="k01"></a>

## 普通调用与方法调用：看本次调用的写法

**口述：** 普通函数作为对象的方法调用时，this 通常指向这个对象。如果把方法取出来，再作为普通函数单独调用，就不会自动记住原来的对象，需要按独立调用的规则判断。

<!-- example: c04-receiver -->
```js
function read() { return this?.name; }
const first = { name: 'A', read };
const second = { name: 'B', read: first.read };
const detached = first.read;

console.log(first.read(), second.read(), detached()); // A B undefined
console.log(first.read === second.read); // true
```

两个对象保存的是同一个函数，但 first.read() 与 second.read() 分别通过不同对象调用，所以结果不同。detached() 是独立调用，在本例的严格模式下 this 为 undefined，代码中的可选链让读取结果也是 undefined。

window.fn() 也是通过对象属性调用函数。因此不能用这个例子证明“所有普通函数的 this 都是 window”。

### 严格模式：独立调用与显式传值怎样处理

- 严格普通函数独立调用时，this 为 undefined；通过 call 等方式提供的 null、数字等值也原样保留。
- 非严格普通函数收到 null 或 undefined 作为 this 时，会使用其所属环境的全局对象；其他原始值会转换为对应的包装对象。
- 浏览器页面的全局对象通常通过 window 访问；不能把所有 JavaScript 环境都称为 window。

<!-- example: c04-strict-receiver -->
```js
function strictRead() { return this; }
const legacyRead = new Function('return this');

console.log(strictRead()); // undefined
console.log(strictRead.call(null)); // null
console.log(strictRead.call(3)); // 3
console.log(legacyRead() === globalThis); // true
console.log(legacyRead.call(null) === globalThis); // true

const wrapped = legacyRead.call(3);
console.log(typeof wrapped, wrapped.valueOf()); // object 3
```

strictRead 定义在 ES 模块中，所以是严格函数。Function 创建的字符串函数没有声明严格模式，用于演示非严格行为；示例中的 call 用于提供这次调用的 this，完整用法见[call、apply、bind 篇](07_call、apply、bind.md#k01)。

**注意：** 严格性看函数自身。严格代码调用一个已经存在的非严格函数，不会让那个函数自动变成严格函数。

### 立即执行函数：仍然按调用方式判断

<!-- example: c04-iife -->
```js
(function () {
  'use strict';
  console.log(this); // undefined
})();
```

这只是创建函数后立即独立调用，所以按严格的独立调用处理。非严格普通函数会使用全局对象；立即执行的是箭头函数时，则使用外层 this。不能只凭“立即执行”就判断是 window。

<a id="k03"></a>

## 箭头函数：沿用定义位置外层的 this

**口述：** 箭头函数不建立自己的 this，而是使用定义它的外层 this。对象字面量的花括号不会额外提供 this，call、apply、bind 也不能替换箭头使用的外层 this。

<!-- example: c04-arrow-environment -->
```js
function make() {
  return {
    value: 'object',
    arrow: () => this.value,
    method() { return this.value; }
  };
}
const outer = { value: 'outer' };
const item = make.call(outer);

console.log(item.arrow(), item.method()); // outer object
console.log(item.arrow.call({ value: 'other' })); // outer
outer.value = 'changed';
console.log(item.arrow()); // changed
```

按顺序看：

1. make.call(outer) 让这次 make 调用的 this 是 outer。
2. 箭头创建在 make 内部，因此使用这次调用的外层 this。
3. item.method() 是普通方法调用，this 使用 item。
4. 对箭头使用 call，不能把 outer 换成另一个对象。
5. outer 的属性仍然能修改，所以箭头后来读取到 changed。

“箭头 this 不变”不代表对象内容不变。不同外层调用创建的箭头，也可以使用不同的外层 this。箭头的参数和构造限制见[函数篇](03_函数.md#k04)。

## 回调、递归和浏览器场景：看谁怎样执行函数

**口述：** 回调只是把函数交给其他代码使用；this 的结果取决于对方怎样调用它。递归也没有额外的 this 规则。浏览器和 Node.js 提供的现成接口（通常称为 API）可能采用不同的调用方式，因此示例必须注明环境。

### 普通回调与 `arguments[0]()` 的区别

<!-- example: c04-callback-forms -->
```js
function readThis() { return this; }
function invoke(callback) {
  console.log(callback() === undefined); // true
  console.log(arguments[0]() === arguments); // true

  const holder = { callback };
  console.log(holder.callback() === holder); // true
}
invoke(readThis);
```

callback() 是独立调用；`arguments[0]()` 是通过 arguments 对象的第 0 个属性调用；holder.callback() 是通过 holder 调用。因此同一个普通函数会得到不同的 this。

如果传入的是箭头函数或 bind 返回的函数，则继续按它们自己的规则判断，不会因为放进 arguments 就改用这个对象。

### 递归：再次调用也要看调用写法

<!-- example: c04-recursion -->
```js
function repeat(count) {
  console.log(this?.name ?? '没有对象');
  if (count > 0) repeat(count - 1);
}
const owner = { name: 'A', repeat };
owner.repeat(1);
// A
// 没有对象
```

第一次通过 owner.repeat(1) 调用，this 是 owner；内部写 repeat(0)，已经变成独立调用。当前是严格模式，所以第二次的 this 为 undefined。

若业务希望继续通过同一个对象调用，可以使用 this.repeat(...) 等明确写法。是否递归、递归几次，都不能代替对调用方式的判断。

### 定时器：浏览器与 Node.js 要分别说明

下面在浏览器页面运行：

<!-- example: c04-browser-timer -->
```js
// browser-only
window.setTimeout(function () {
  'use strict';
  console.log(this === window); // true
}, 0);
```

浏览器页面的 setTimeout、setInterval 执行普通、未绑定的函数回调时，会提供 window 作为 this。因此即使回调自身是严格函数，this 也不会自动变成 undefined。

下面仅在 Node.js 运行：

<!-- example: c04-node-timer -->
```js
// node-only
const timer = setTimeout(function () {
  'use strict';
  console.log(this === timer); // true
}, 0);
```

Node.js 普通定时器回调通常使用返回的 Timeout 对象作为 this。箭头回调仍然沿用外层 this，bind 后的回调则在普通调用时使用绑定值。

**注意：** setTimeout(fn, 0) 是把函数交给定时器稍后执行，不等于现在直接写 fn()。业务需要某个对象时，可以用箭头调用该对象的方法，或保存一次 bind 的结果。

### 页面元素事件（DOM 事件）：this 与 currentTarget、target

下面在浏览器页面运行：

<!-- example: c04-dom-listener -->
```js
// browser-only
const button = document.createElement('button');
const icon = document.createElement('span');
button.append(icon);
document.body.append(button);

button.addEventListener('click', function (event) {
  console.log(this === button); // true
  console.log(this === event.currentTarget); // true
  console.log(event.target === icon); // true
});
icon.click();
button.remove();
```

监听注册在 button，实际点击目标是内部 icon：

- currentTarget 表示当前正在执行监听的对象，这里是 button。
- target 表示事件目标，这里是 icon。
- 普通、未绑定的函数监听器中，this 与执行期间的 currentTarget 一致。

button.onclick = function (...) {} 的普通函数写法也按相应事件调用规则处理。箭头监听器使用外层 this，bind 后的函数使用指定的 this，不能再直接等同于 currentTarget。

### class：普通方法随调用变化，箭头字段保留实例 this

<!-- example: c04-class-methods -->
```js
class User {
  constructor(name) { this.name = name; }
  read() { return this?.name; }
  arrow = () => this.name;
  static who() { return this; }
}
class Admin extends User {}

const user = new User('Alice');
const read = user.read;
const arrow = user.arrow;
console.log(user.read(), read(), arrow()); // Alice undefined Alice
console.log(User.who() === User, Admin.who() === Admin); // true true

const who = User.who;
console.log(who()); // undefined
```

类方法默认严格。普通实例方法取出后独立调用，不会永久记住 user；实例箭头字段使用初始化时的实例 this，所以取出后仍读取 Alice。

静态方法也看本次通过谁调用：User.who() 使用 User，继承后通过 Admin.who() 调用则使用 Admin。普通静态方法也可以用 call 指定其他对象，涉及私有字段时还有额外限制，见[class 篇](../03_对象机制/05_class与私有成员.md)。

原型上的普通方法同样按调用方式判断。方法不是因为“写在原型上”，就固定使用某个实例作为 this。

<a id="k04"></a>

## new 调用：this 使用新创建的实例

**口述：** 使用 new 调用普通构造函数时，会创建一个新实例，构造函数中的 this 就指向这个新实例。调用通常返回该实例；如果构造函数明确返回另一个对象，则采用那个对象。

这里关注构造调用中的 this；完整创建过程与实现边界见[构造函数与 new](../03_对象机制/02_构造函数与new.md)。

<!-- example: c04-constructor-return -->
```js
function User(name) { this.name = name; return 7; }
function Replacement() { this.value = 1; return { value: 9 }; }

const user = new User('Alice');
console.log(user.name); // Alice
console.log(Object.getPrototypeOf(user) === User.prototype); // true
console.log(new Replacement().value); // 9
```

new User 的数字返回值被忽略。new Replacement 则返回 value 为 9 的另一个对象，原先 this 对象上的 value = 1 不是最终结果。

<a id="k05"></a>

## 综合判断：先确定函数类型，再看本次调用

**口述：** 先区分普通函数和箭头函数，再看它是作为对象方法调用、单独调用，还是用于创建实例。如果函数交给其他代码执行，还要确认对方怎样调用它。使用 bind 固定过 this 的函数，需要继续应用绑定函数的规则。

- 普通方法调用：看本次通过哪个对象调用。
- 独立调用：看函数自身是否严格。
- 箭头函数：到定义位置的外层找 this。
- new 调用普通构造函数：先使用新实例，再看构造函数是否明确返回其他对象。
- 定时器、事件和其他回调：确认运行环境与调用形式。
- call、apply、bind：主动提供 this，详细区别和特殊情况见[call、apply、bind 篇](07_call、apply、bind.md)。

<a id="k06"></a>

构造过程与受限手写实现已独立放在[构造函数与 new](../03_对象机制/02_构造函数与new.md#c10-01)。

<a id="summary"></a>

本篇的重点是根据调用方式解释结果。能说明方法取出后为什么变化、箭头为什么沿用外层 this，以及构造调用如何初始化实例，就能把常见场景串起来。

<a id="k02"></a>

继续阅读：[call、apply、bind：指定 this、传参和调用时机](07_call、apply、bind.md#k01)。其中的[绑定函数与 new](07_call、apply、bind.md#k02)说明构造调用的特殊情况。

<a id="self-check"></a>

## 自查问题

先遮住答案，沿记忆线逐题口述；点问题可以跳到对应回答。

1. [this 是什么？它一定指向函数自身或某个对象吗？](#recall-01)
2. [普通方法调用与独立调用有什么区别？同一函数放进不同对象，或取出后作为回调，this 会怎样变化？](#recall-02)
3. [严格与非严格函数怎样处理独立调用、null、undefined 和原始值？严格调用者会让被调用函数变严格吗？](#recall-03)
4. [立即执行函数的 this 一定是 window 吗？普通严格函数、非严格函数和箭头怎样判断？](#recall-04)
5. [箭头 this 从哪里来？对象字面量是否提供 this？不能替换 this 是否表示对象属性不能改变？](#recall-05)
6. [call、apply、bind 怎样影响 this？绑定目标非严格或被 new 调用时，还有哪些规则？](#recall-06)
7. [同一个回调独立调用、通过对象属性调用、通过 `arguments[0]()` 调用，this 有什么不同？](#recall-07)
8. [第一次通过对象调用，内部再通过函数名独立递归，this 会自动沿用原对象吗？](#recall-08)
9. [浏览器与 Node.js 的定时器回调 this 有什么不同？浏览器严格回调为什么仍可能是 window？](#recall-09)
10. [在父元素监听、点击子元素时，this、currentTarget、target 分别是什么？箭头和绑定监听器呢？](#recall-10)
11. [普通实例方法、实例箭头字段、静态方法怎样确定 this？取出方法和通过子类调用会怎样？](#recall-11)
12. [new 调用普通构造函数时做什么？返回数字、null、对象或函数时，最终结果怎样确定？](#recall-12)

<a id="interview"></a>

## 试题与答案

以下按 12 个知识点逐项回答，最后保留综合判断题及已拆分篇目的入口。

<a id="recall-01"></a>

### this 的含义：本次执行使用的特殊值

**自查：** this 是什么？它一定指向函数自身或某个对象吗？

**回答：** this 是函数执行时可以使用的特殊值，常用于访问本次调用关联的对象。同一个普通函数换一种调用方式，this 就可能不同。它不固定指向函数自身，也不保证一定是对象；严格模式下可以是 undefined、null 或数字等原始值。判断时先看函数类型和调用方式。

**记忆：** this 是本次执行使用的值，先看函数类型和调用方式。

<a id="recall-02"></a>

<a id="c06-03"></a>

### C06-03 [P0·原理] 对象方法传给回调后为什么this变了？

**自查：** 普通方法调用与独立调用有什么区别？同一函数放进不同对象，或取出后作为回调，this 会怎样变化？

**回答：** 普通、未绑定函数通过对象方法调用时，this 通常是本次调用所通过的对象；同一函数分别通过不同对象调用，可以使用不同的 this。

传出去的是函数本身，不会自动记住原来的对象。对方单独调用、作为另一个对象的方法调用，或交给浏览器执行，都可能得到不同的 this。可以保存一次 bind 结果，或用箭头明确调用原对象的方法；事件注册和移除要复用同一个函数。

**记忆：** 通过哪个对象调用，就看哪个对象；取出独立调用，要重新判断。

<a id="recall-03"></a>

### 严格与非严格模式：this 值怎样处理

**自查：** 严格与非严格函数怎样处理独立调用、null、undefined 和原始值？严格调用者会让被调用函数变严格吗？

**回答：** 严格普通函数独立调用时 this 为 undefined；显式提供 null 或原始值时，原样保留。非严格普通函数收到 null 或 undefined 时，会使用其所属环境的全局对象；其他原始值会转换成包装对象。严格性由函数自身决定，严格代码调用非严格函数，不会自动改变被调用函数的严格性。

**记忆：** 严格保留传入值，非严格会替换或装箱；严格性看函数自身。

<a id="recall-04"></a>

### 立即执行函数：立即调用不增加 this 规则

**自查：** 立即执行函数的 this 一定是 window 吗？普通严格函数、非严格函数和箭头怎样判断？

**回答：** 立即执行只说明创建函数后马上调用。常见的普通函数立即执行写法属于独立调用，this 仍根据函数自身是否严格，分别得到 undefined 或所属环境的全局对象。立即执行的是箭头函数时，则沿用外层 this；不能凭调用时机判断为 window。

**记忆：** 立即执行看时机，this 仍看函数和调用方式。

<a id="recall-05"></a>

### 箭头函数：沿用定义位置外层的 this

**自查：** 箭头 this 从哪里来？对象字面量是否提供 this？不能替换 this 是否表示对象属性不能改变？

**回答：** 箭头不建立自己的 this，而是沿用定义位置外层的 this。对象字面量不额外提供 this，箭头写在对象里面也不代表 this 是该对象。call、apply、bind 不能替换它使用的外层 this，但外层对象的属性仍可以变化；不同外层调用创建的箭头，也可以使用不同的 this。

**记忆：** 箭头找外层，对象花括号不提供 this；对象内容仍能变。

<a id="recall-06"></a>

### 显式提供 this：普通绑定调用与构造调用要区分

**自查：** call、apply、bind 怎样影响 this？绑定目标非严格或被 new 调用时，还有哪些规则？

**回答：** call、apply 为本次调用提供 this；bind 返回绑定函数，后续普通调用时向目标提供绑定值。目标若是非严格普通函数，仍会把 null、undefined 替换成全局对象，或把其他原始值装箱；这些方法也不能替换箭头沿用的外层 this。可构造目标的绑定函数通过 new 调用时，忽略原来绑定的 this，采用构造调用规则。用法与边界继续看 [call、apply、bind](07_call、apply、bind.md#k01)。

**记忆：** 绑定给目标提供值，目标仍按自身规则处理；new 绑定函数忽略绑定 this。

<a id="recall-07"></a>

### 普通回调：接收方怎样调用决定结果

**自查：** 同一个回调独立调用、通过对象属性调用、通过 `arguments[0]()` 调用，this 有什么不同？

**回答：** 回调只是交给其他代码调用的函数，不自带额外的 this 规则。对普通、未绑定函数，独立调用按严格性判断，通过对象属性调用则使用本次接收对象；通过 arguments 的第零个属性调用时，this 是 arguments 对象。宿主 API 还可能按自己的规则提供值。若传入箭头或绑定函数，仍要遵循它们自己的规则。

**记忆：** 回调只说明交给别人，this 要看别人怎样调用。

<a id="recall-08"></a>

### 递归调用：每次都检查调用方式

**自查：** 第一次通过对象调用，内部再通过函数名独立递归，this 会自动沿用原对象吗？

**回答：** 递归没有额外的 this 规则。第一次通过对象调用，内部再通过函数名独立调用时，后一次需要按独立调用处理，严格模式下是 undefined。需要保持对象时，可以通过明确的对象方法调用或显式传递来表达，不能认为递归会自动继承上次调用的 this。

**记忆：** 递归也是新调用，每次都看调用写法。

<a id="recall-09"></a>

### 定时器回调：区分浏览器页面与 Node.js

**自查：** 浏览器与 Node.js 的定时器回调 this 有什么不同？浏览器严格回调为什么仍可能是 window？

**回答：** 浏览器页面的定时器调用普通、未绑定回调时，会提供 window 作为 this，因此即使回调严格，this 仍可为 window。Node.js 普通定时器回调通常使用对应 Timeout 对象作为 this。箭头沿用外层 this；绑定函数的普通调用向目标提供绑定值，再由目标按自身规则处理。

**记忆：** 页面普通回调通常是 window，Node.js 通常是 Timeout；箭头和绑定另看自身规则。

<a id="recall-10"></a>

### DOM 事件：区分 this、currentTarget 与 target

**自查：** 在父元素监听、点击子元素时，this、currentTarget、target 分别是什么？箭头和绑定监听器呢？

**回答：** DOM 普通、未绑定函数监听器执行期间，this 等于 event.currentTarget，表示当前正在执行监听的对象；event.target 表示事件目标。父元素监听、点击子元素时，两者可以分别是父元素和子元素。箭头使用外层 this，绑定函数按绑定规则调用，不能直接把它们的 this 等同于 currentTarget。

**记忆：** currentTarget 看当前监听对象，target 看事件目标。

<a id="recall-11"></a>

### class 方法：普通方法看调用，实例箭头字段保留实例

**自查：** 普通实例方法、实例箭头字段、静态方法怎样确定 this？取出方法和通过子类调用会怎样？

**回答：** 类方法默认严格。普通实例方法的 this 仍由调用方式决定，取出独立调用时为 undefined；实例箭头字段使用初始化时的实例 this，取出后仍能访问原实例。普通静态方法通过父类调用时使用父类，通过继承它的子类调用时使用子类。原型普通方法也不会永久绑定某个实例。

**记忆：** 普通方法看调用，实例箭头字段保留实例；静态方法看通过哪个类调用。

<a id="recall-12"></a>

### new 调用：构造时的 this 不一定是最终返回对象

**自查：** new 调用普通构造函数时做什么？返回数字、null、对象或函数时，最终结果怎样确定？

**回答：** 对于一般 function 构造函数，可以按创建对象、关联原型、以新对象为 this 执行构造函数、处理返回值四步理解。明确返回非 null 的对象或函数时，采用该返回值；否则返回新创建的实例。因此构造时使用的 this 对象与最终结果不一定相同。这是普通构造函数的教学模型，不能覆盖所有原生构造语义；完整规则和实现边界见 [构造函数与 new](../03_对象机制/02_构造函数与new.md)。

**记忆：** 创建对象 → 关联原型 → 执行构造 → 检查返回值。

## 综合追问与后续衔接

<a id="c06-01"></a>

### C06-01 [P0·原理] this由定义位置还是调用位置决定？

**回答：** 先区分函数类型。普通函数主要看调用方式：作为对象方法调用时通常指向该对象，单独调用时再区分严格模式。箭头函数沿用定义位置外层的 this。bind 后的函数普通调用时向目标提供绑定值，目标仍按自身规则处理；new 调用普通构造函数则使用新创建的实例。所以不能只用定义位置或“谁调用就是谁”概括全部情况。

**记忆：** 普通函数看调用，箭头找外层；绑定、构造与宿主场景分别判断。

### 原合篇题目的新位置

<a id="c06-02"></a>

- [call、apply、bind 怎么比较？](07_call、apply、bind.md#c07-01)

<a id="c06-05"></a>

- [bind 教学实现如何区分普通调用和 new？](07_call、apply、bind.md#c07-02)

<a id="c06-06"></a>

- [不借 call/apply，怎样演示“指定对象调用”？](07_call、apply、bind.md#c07-03)

参考：[MDN：this](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Operators/this)、[MDN：new](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Operators/new)。
