# 第五章 this、call、apply、bind 与 new

## 一、本章具体知识点

- this 是什么
- 默认绑定
- 隐式绑定
- 显式绑定
- new 绑定
- 箭头函数 this
- call
- apply
- bind
- constructor
- new 的执行过程
- 手写 call/apply/bind/new

## 二、各知识点详细解释

### 1. this 不是作用域的一部分

普通函数的 this 主要由调用方式决定，而不是由函数定义位置决定：

```js
const obj = {
  name: 'Tom',
  say() {
    console.log(this.name)
  }
}
```

`obj.say()` 与把函数单独取出来调用，this 可能不同。

### 2. 默认绑定

严格模式下普通函数独立调用的 this 为 undefined；非严格模式有历史行为。

### 3. 隐式绑定

```js
obj.fn()
```

调用表达式中，obj 成为 this 的来源。

### 4. 显式绑定

```js
fn.call(obj)
fn.apply(obj, args)
fn.bind(obj)
```

### 5. 箭头函数

箭头函数没有自己的 this，它使用定义时外层作用域中的 this。它也没有自己的 arguments、prototype、不能作为普通构造器使用。

### 6. new

可以抽象成：

```text
创建新对象
→ 建立原型关系
→ 以新对象为 this 调用构造函数
→ 如果构造函数返回对象则按规则返回该对象，否则返回新对象
```

## 三、本章面试题与答案

### 题 1：this 到底由什么决定？

**答案：**

普通函数的 this 主要由调用方式决定：独立调用、作为对象方法调用、通过 call/apply/bind、通过 new 调用都会产生不同绑定规则。箭头函数没有自己的 this，而是捕获定义位置外层的 this。

### 题 2：箭头函数为什么没有自己的 this？

**答案：**

箭头函数设计上不建立自己的 this binding，它直接使用外层词法环境中的 this。因此特别适合需要保留外层 this 的回调场景。

### 题 3：new 做了什么？

**答案：**

概念上先创建新对象，再让这个对象的原型与构造函数 prototype 建立关系，然后以新对象作为 this 执行构造函数；如果构造函数显式返回对象，则按照 new 的返回规则使用该对象，否则返回新建实例。

---
