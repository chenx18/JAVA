# 第六章 原型、原型链与 class

## 一、本章具体知识点

- prototype
- __proto__
- constructor
- prototype chain
- Object.create
- 属性查找
- 方法共享
- 原型继承
- class
- extends
- super
- static

## 二、各知识点详细解释

### 1. prototype

函数对象可以拥有 prototype 属性，用于构造实例的原型对象。

### 2. __proto__

`__proto__` 是历史形成的访问器，用来访问对象的 [[Prototype]]。实际开发优先使用 `Object.getPrototypeOf` / `Object.setPrototypeOf`。

### 3. 属性查找

读取 `obj.x` 时，如果 own property 没有 x，会继续沿着 prototype 链向上查找。

```text
obj
↓
prototype
↓
Object.prototype
↓
null
```

### 4. class

class 是更现代的类语法，但其对象继承底层仍建立在 prototype 机制上。`extends` 建立原型继承与静态继承关系。

## 三、本章面试题与答案

### 题 1：prototype 和 __proto__ 的区别？

**答案：**

函数对象通常有 `prototype` 属性，它是构造实例时使用的原型对象；普通对象通过其内部 [[Prototype]] 与原型对象关联，`__proto__` 是历史兼容访问器。两者不是同一个概念。

### 题 2：class 是新的继承机制吗？

**答案：**

不是完全独立的新对象继承模型。class 提供更清晰的语法，但实例方法仍然通过 prototype 工作，extends 仍然建立原型链和静态继承关系。

---
