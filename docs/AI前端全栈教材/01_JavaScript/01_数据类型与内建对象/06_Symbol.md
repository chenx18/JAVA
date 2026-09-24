# 06 Symbol

Symbol 主要用于表达不与普通字符串键冲突的身份，以及参与语言内建协议。描述文字只是便于调试，不决定一个普通 Symbol 的身份。

## 一、本章目录

- [唯一值、描述与注册表](#k01)
- [符号键与枚举](#k02)
- [常用 Symbol API 与内建协议](#k03)
- [Symbol 与私有成员的区别](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 唯一值、描述与注册表

```js
const first = Symbol('id');
const second = Symbol('id');
console.log(first === second); // false
console.log(first.description); // id
console.log(Symbol.for('app.id') === Symbol.for('app.id')); // true
console.log(Symbol.keyFor(first)); // undefined
```

Symbol() 每次返回新值；Symbol.for(key) 按键使用符号注册表，Symbol.keyFor(symbol) 查询已注册符号的键。注册表可在同一 agent 的 realm 之间共享，不是跨进程或不同 Worker 自动同步的全网字典。模块要共享协议键时应统一导出或使用明确注册名。

Symbol 不是构造器，不能 new。String(symbol) 和 symbol.toString() 可显示描述，隐式拼接或数值转换通常不允许；不要把 Symbol 的文本表示当作唯一ID。

<a id="k02"></a>

### 2. 符号键与枚举

对象属性键分为字符串和 Symbol。Symbol 可以减少库与业务代码的键名冲突，但不会使属性不可访问。

```js
const token = Symbol('token');
const obj = { name: 'A', [token]: 1 };
console.log(Object.keys(obj)); // ['name']
console.log(Object.getOwnPropertySymbols(obj)[0] === token); // true
console.log(Reflect.ownKeys(obj).length); // 2
console.log({ ...obj }[token]); // 1
console.log(JSON.stringify(obj)); // {"name":"A"}
```

Object.keys、for...in 主要枚举字符串键；Object.getOwnPropertySymbols 返回自有符号键；Reflect.ownKeys 同时返回自有字符串/符号键。对象展开和 Object.assign 会复制自有可枚举 Symbol 属性。JSON.stringify 忽略符号键。

<a id="k03"></a>

### 3. 常用 Symbol API 与内建协议

| 成员 | 作用 | 典型触发 |
| --- | --- | --- |
| Symbol()/for()/keyFor() | 创建或共享符号及查注册键 | 身份和公共键 |
| description、toString()、valueOf() | 描述、文本展示、解包 | 调试或包装对象 |
| Symbol.iterator / asyncIterator | 提供迭代器 | for...of / for await |
| Symbol.toPrimitive | 自定义转原始值 | 运算与 String/Number 转换 |
| Symbol.toStringTag | 对象类型标签 | Object.prototype.toString |
| Symbol.hasInstance | 自定义实例判断 | instanceof |
| Symbol.match/matchAll/replace/search/split | 定制字符串模式协议 | 对应 String 方法 |
| Symbol.isConcatSpreadable | 是否展开合并 | Array.concat |
| Symbol.species | 某些派生操作选择构造器 | 部分容器派生对象 |
| Symbol.unscopables | 排除 with 名称暴露 | 历史兼容；不建议使用 with |
| Symbol.dispose / asyncDispose | 显式资源管理协议 | using/await using，按运行时支持 |

内建符号是协议入口，不需要把所有协议都手写一遍。理解“普通对象提供了约定方法，于是语言操作能调用它”即可把迭代、转换、正则等行为串起来。较新资源管理语法不能仅靠转译假定目标运行时具备资源API。

<a id="k04"></a>

### 4. Symbol 与私有成员的区别

Symbol 键可以被 Reflect.ownKeys 找到，引用泄露后也可直接读写。它适合避免无意冲突，不适合实现访问控制或秘密存储。

```js
const secret = Symbol('secret');
const obj = { [secret]: 'visible' };
const discovered = Object.getOwnPropertySymbols(obj)[0];
console.log(obj[discovered]); // visible
```

需要语言级私有成员时考虑 class 的 #字段；需要由闭包控制访问时使用局部状态。无论哪种方式，已经发送到浏览器的业务秘密都不能只靠前端语言封装获得服务器级保密。

<a id="summary"></a>

## 三、知识小结

Symbol 的三条主线是独立身份、对象属性键、内建协议。记忆时把“描述相同”“注册键相同”“符号值相同”分开，并牢记不冲突不等于私有。

参考：[MDN Symbol](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Symbol)。较新 API 按目标运行时核对支持，示例各自独立。

<a id="interview"></a>

## 四、面试题与答案

<a id="d06-01"></a>

### D06-01 [P0·基础] Symbol('x') 与 Symbol.for('x') 有何不同？

**回答：** 前者每次创建新身份，描述只是调试信息；后者按注册键复用符号。Symbol.keyFor 只能找已注册符号的键。不要把description或toString文本当作唯一身份。

对应讲解：[唯一值、描述与注册表](#k01)。

<a id="d06-02"></a>

### D06-02 [P1·原理] Symbol 属性能被复制、遍历或 JSON 序列化吗？

**回答：** 要看API。Object.keys与for...in不列出符号键；Object.getOwnPropertySymbols和Reflect.ownKeys能取得；展开与assign复制自有可枚举符号属性；JSON.stringify忽略符号键。

对应讲解：[符号键与枚举](#k02)。

<a id="d06-03"></a>

### D06-03 [P1·基础] 为什么 Symbol 不能代替私有字段？

**回答：** 符号键可以由反射API发现，得到符号后便能访问属性。Symbol主要防止意外键冲突，#私有字段还有词法访问和接收者校验，二者目的不同。

对应讲解：[Symbol 与私有成员的区别](#k04)。

<a id="d06-04"></a>

### D06-04 [P1·原理] Symbol.iterator 让对象获得了什么能力？

**回答：** 它是语言约定的入口；for...of等消费者调用该方法取得迭代器，再不断调用next读取结果。不是属性名中包含iterator就自动可迭代，必须提供符合协议的行为。

对应讲解：[常用 Symbol API 与内建协议](#k03)。
