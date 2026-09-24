# 09 Map

Map 用键的身份表达映射，适合动态字典、对象键和需要明确插入顺序的数据。它与普通对象的差别不只在能不能遍历。

## 一、本章目录

- [键、身份与顺序](#k01)
- [创建、增删查与遍历 API](#k02)
- [Map 与 Object 的选择和转换](#k03)
- [缓存中的生命周期](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 键、身份与顺序

Map允许任意值作键，按SameValueZero比较：NaN与NaN视为相同，正负零相同，对象按身份而不是内容。键按首次插入顺序迭代；覆盖已有键的值不改变位置，删除后重新插入才移到末尾。

```js
const a = {}, b = {};
const map = new Map([[a, 1], [b, 2], [NaN, 3]]);
console.log(map.size, map.get(a), map.get({})); // 3 1 undefined
map.set(NaN, 4);
console.log(map.get(NaN)); // 4
```

map[key]=value写的是Map对象上的普通属性，并没有新增Map条目。必须使用set/get等集合API。Map也不是WeakMap，它对键和值维持强引用。

<a id="k02"></a>

### 2. 创建、增删查与遍历 API

| API | 返回及语义 |
| --- | --- |
| new Map(iterable?) | 由可迭代键值对创建；重复键以后值覆盖 |
| size | 条目数，只读访问器；不是方法 |
| set(key,value) | 返回当前Map，可链式调用 |
| get(key) | 值或undefined |
| has(key) | 是否存在键；区分不存在与值为undefined |
| delete(key) | 是否成功移除条目 |
| clear() | 清空，返回undefined |
| keys()/values()/entries() | 按插入顺序的迭代器 |
| [Symbol.iterator]() | 默认等同entries迭代 |
| forEach(fn,thisArg?) | 回调(value,key,map)，返回undefined |
| Map.groupBy(items,fn) | 按回调返回键分组为Map，保留对象键身份 |

```js
const map = new Map([['x', undefined]]);
console.log(map.get('x'), map.get('y')); // undefined undefined
console.log(map.has('x'), map.has('y')); // true false
console.log([...map.keys()]); // ['x']
```

getOrInsert(key,defaultValue)、getOrInsertComputed(key,callback)是较新API：存在则返回已有值，不存在才插入默认值或计算结果。需检测目标支持；兼容实现使用has而不是get(...)??，因为已存undefined/null也可算存在。计算回调抛错或修改Map时还需明确行为，不用简单缓存包装假装完整原生实现。

<a id="k03"></a>

### 3. Map 与 Object 的选择和转换

普通对象适合结构稳定、字段名明确的业务记录；Map适合动态条目、任意键和需要size等集合语义的场景。对象的整数样式键可能改变常见枚举顺序，Map的键则按集合顺序组织。

```js
const map = new Map([['name', 'Alice'], ['age', 20]]);
const record = Object.fromEntries(map);
console.log(record.name); // Alice
console.log(new Map(Object.entries(record)).get('age')); // 20
```

转换成Object会把非Symbol键变成字符串，原本不同的对象键可能冲突。JSON.stringify(new Map(...))默认通常得到{}，需要显式转条目数组或领域记录；复杂键的往返协议不能靠直接JSON序列化自动保留。

<a id="k04"></a>

### 4. 缓存中的生命周期

Map可用于缓存，但必须决定容量、有效期、失效与用户隔离。全局Map一直保存大对象时，对象不会因业务页面消失就自动释放。

进行中请求去重可缓存Promise；失败后是否删除、完成后是否转结果缓存、多个订阅者如何取消，都是独立策略。按同key读取不会自动刷新LRU位置，需要显式delete后set。缓存键要包含影响结果的参数和授权上下文，避免跨用户串数据。

<a id="summary"></a>

## 三、知识小结

Map记住“任意键、身份比较、插入顺序、显式集合API”。查值不等于查存在，集合也不自动获得JSON协议或缓存淘汰能力。

参考：[MDN Map](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Map)。较新 API 按目标运行时核对支持，示例各自独立。

<a id="interview"></a>

## 四、面试题与答案

<a id="d09-01"></a>

### D09-01 [P0·基础] get返回undefined为什么还需要has？

**回答：** 键可能确实存在但保存undefined。get无法区分这与缺失键，has检查成员关系，适合缓存默认值和存在性判断。

对应讲解：[创建、增删查与遍历 API](#k02)。

<a id="d09-02"></a>

### D09-02 [P1·原理] Map键是对象时怎么比较？

**回答：** 比较对象身份，不比较字段内容。map.set({},1)后用另一个{}读取拿不到值；NaN等原始键按SameValueZero处理。

对应讲解：[键、身份与顺序](#k01)。

<a id="d09-03"></a>

### D09-03 [P1·基础] 覆盖Map条目会移动顺序吗？

**回答：** set已有键只更新值，不改变原插入位置。删除再set则作为新插入移到末尾，LRU常利用这一行为，但要自己维护容量和访问刷新。

对应讲解：[键、身份与顺序](#k01)。

<a id="d09-04"></a>

### D09-04 [P1·工程取舍] Map是否比Object更好？

**回答：** 取决于数据契约。固定业务字段适合Object，动态字典、对象键、成员计数和明确集合操作适合Map。序列化、内存保留和性能还需结合实际数据，不按类型名断言谁永远更快。

对应讲解：[Map 与 Object 的选择和转换](#k03)。
