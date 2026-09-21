# 第十二章 字符串、数组、对象与常用内建 API

## 一、本章具体知识点

- String API
- Array 高阶函数
- reduce
- map/filter/find
- some/every
- sort
- slice/splice
- Object.keys/values/entries
- destructuring
- spread/rest
- Map/Set
- JSON
- structuredClone

## 二、各知识点详细解释

### 1. map / filter / reduce

map 是一一映射；filter 保留符合条件的元素；reduce 把一系列值聚合成一个结果。

### 2. sort

`sort` 会原地修改数组，比较器返回负数/0/正数表达相对顺序。数字排序要自己传比较函数：

```js
[10, 2, 3].sort((a, b) => a - b)
```

### 3. slice vs splice

`slice` 返回新数组，不修改原数组；`splice` 会修改原数组。

### 4. JSON

JSON 是数据交换格式，不等于 JavaScript 对象。JSON 无法表达 JS 全部值，例如 function、Symbol、undefined 等。

### 5. structuredClone

用于结构化克隆支持范围内的深复制，比 JSON 序列化更适合处理很多内建数据结构，但仍不是“任何 JS 值都能复制”。

## 三、本章面试题与答案

### 题 1：map、forEach、reduce 有什么区别？

**答案：**

map 用于将每个元素映射成新值并返回新数组；forEach 主要用于遍历并执行副作用，不产生映射结果；reduce 用一个累积器把多个元素聚合成单个结果，适合求和、分组、构建索引等。

### 题 2：slice 和 splice 的区别？

**答案：**

slice 不修改原数组，返回一个新数组；splice 会修改原数组，可以删除、插入和替换元素。

---
