# 08 Array

Array 是带有索引和 length 特殊行为的对象。掌握数组需要同时理解方法、空位、元素引用和迭代时机。本章将 API 按任务分组，逐项说明返回值及是否修改原数组。

## 一、本章目录

- [创建、length 与稀疏数组](#k01)
- [增删、截取、拼接与查找](#k02)
- [遍历、映射、筛选与归并](#k03)
- [排序、反转与复制修改](#k04)
- [分组、去重与数据结构选择](#k05)
- [数组里的异步、复制与实战边界](#k06)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 创建、length 与稀疏数组

| API | 返回/语义 |
| --- | --- |
| [] | 数组字面量，适合明确元素 |
| new Array(length) | 单一合法数字参数表示长度，产生空位 |
| Array.of(...items) | 把所有参数作为元素，Array.of(3) 是 [3] |
| Array.from(source,mapFn?,thisArg?) | 从 iterable 或类数组构造数组，可同步映射 |
| Array.fromAsync(source,mapFn?,thisArg?) | 返回 Promise，按异步迭代/类数组规则等待；需目标支持 |
| Array.isArray(value) | 判断数组身份 |

```js
const sparse = new Array(2);
console.log(sparse.length, 0 in sparse); // 2 false
console.log([undefined].length, 0 in [undefined]); // 1 true
console.log(Array.from(sparse)); // [undefined,undefined]
```

空位表示该索引属性不存在，不等于该属性存在且值为undefined。缩短length会删除越界元素；增大length只增加空位。delete arr[i] 不会把后续元素前移，也不减少length；按位置删除并压紧通常用splice。

Array.fromAsync通常逐项等待，并不等同于把所有任务并发启动的Promise.all；输入若是已启动Promise，它们的启动时机不会被改变。

<a id="k02"></a>

### 2. 增删、截取、拼接与查找

| 方法 | 返回值 | 是否修改原数组/边界 |
| --- | --- | --- |
| push(...items)、unshift(...items) | 新 length | 是；尾/头添加 |
| pop()、shift() | 被移除值或 undefined | 是；尾/头删除 |
| splice(start,deleteCount?,...items) | 删除元素数组 | 是；支持负start；省略删除数与显式undefined不同 |
| toSpliced(start,deleteCount?,...items) | 新数组 | 否；返回编辑后整体，不是删除部分 |
| slice(start=0,end=length) | 新数组 | 否；浅拷贝、左闭右开、支持负位置 |
| concat(...values) | 新数组 | 否；通常展开一层数组，可受isConcatSpreadable影响 |
| at(index) | 元素或 undefined | 否；支持负索引 |
| includes(value,fromIndex=0) | Boolean | SameValueZero，能找NaN，空位视作undefined |
| indexOf/lastIndexOf(value,fromIndex?) | 索引或 -1 | 查相等值，跳过空位，找不到NaN |
| find/findLast(predicate,thisArg?) | 首/尾符合的元素或 undefined | 回调会访问空位，把它表现为undefined |
| findIndex/findLastIndex(predicate,thisArg?) | 索引或 -1 | 便于区分找到undefined和没找到 |

```js
const values = [1, 2, 3];
console.log(values.splice(1, 1, 9)); // [2]
console.log(values); // [1,9,3]
console.log([NaN].includes(NaN), [NaN].indexOf(NaN)); // true -1
```

splice(1) 删除尾部，而 splice(1,undefined) 把删除数转成0。读取返回undefined还不能判定元素是否存在，要结合索引或自有属性判断。

<a id="k03"></a>

### 3. 遍历、映射、筛选与归并

这些回调方法通常传入(value,index,array)，并可接受thisArg；箭头的词法this不受thisArg改变。reduce/reduceRight回调还先接收accumulator，没有thisArg参数。

| 方法 | 返回 | 适用任务 |
| --- | --- | --- |
| forEach(fn,thisArg?) | undefined | 同步副作用；不能用break，return只退出当前回调 |
| map(fn,thisArg?) | 新数组 | 一项映射一项；长度通常保持，空位保留 |
| filter(fn,thisArg?) | 新数组 | 选择符合条件元素，结果紧凑 |
| some/every(fn,thisArg?) | Boolean | 找到决定结果的项即短路；空数组分别false/true |
| reduce/reduceRight(fn,initial?) | 任意累积结果 | 从左/右归并；无初值且无可用元素会抛错 |
| flat(depth=1) | 新数组 | 按深度展开数组，处理展开层的空位 |
| flatMap(fn,thisArg?) | 新数组 | 先映射再展平一层；不是任意深度 |
| keys()/values()/entries() | 迭代器 | 索引、值、键值对；默认迭代是values |
| join(separator=',') | 字符串 | null/undefined/空位通常贡献空字段 |
| toString()/toLocaleString(...) | 字符串 | 展示；不作为无歧义序列化协议 |

map/forEach/filter/some/every/reduce通常跳过不存在的索引。空位规则并不统一，因此不能背“所有数组方法都会跳过空位”。for...of/values/find/includes和新复制方法的处理需要分别看。

```js
console.log([, 2].map(x => x * 2)); // [空位,4]
console.log([, 2].filter(() => true)); // [2]
console.log([].every(() => false)); // true
console.log([1, 2, 3].reduce((sum, n) => sum + n, 0)); // 6
```

边遍历边增删数组容易混淆：多数回调方法开始时确定遍历长度，但访问每个索引时读取当时的值；新增、删除和修改的效果不同。业务代码优先分离变换与原数组修改。

<a id="k04"></a>

### 4. 排序、反转与复制修改

| 方法 | 返回 | 修改原数组 |
| --- | --- | --- |
| sort(compareFn?) | 原数组 | 是 |
| toSorted(compareFn?) | 新数组 | 否 |
| reverse() | 原数组 | 是 |
| toReversed() | 新数组 | 否 |
| fill(value,start=0,end=length) | 原数组 | 是；所有填充位置使用同一value |
| copyWithin(target,start=0,end=length) | 原数组 | 是；复制覆盖，不改变length |
| with(index,value) | 新数组 | 否；支持负索引，越界抛RangeError |

默认sort按字符串形式比较，不是数值大小。数值常用(a,b)=>a-b，比较器应稳定表达一致的次序，不能返回随机结果。现代标准要求稳定排序：比较结果为0时保留原相对次序；仍应区分不同运行时的兼容范围。

```js
console.log([2, 10, 1].sort()); // [1,10,2]
console.log([2, 10, 1].sort((a, b) => a - b)); // [1,2,10]
const rows = new Array(2).fill({ count: 0 });
rows[0].count++;
console.log(rows[1].count); // 1
```

fill填入对象时共享引用；需要独立对象用Array.from({length:n},()=>({...}))。toSorted/toReversed/toSpliced/with的“不修改原数组”仍是浅层的，元素对象不会被自动深拷贝；它们把读取到的空位表现为undefined，和某些旧方法保留空位的行为不同。

<a id="k05"></a>

### 5. 分组、去重与数据结构选择

Object.groupBy(iterable,fn)返回按属性键分组的无原型对象；Map.groupBy(iterable,fn)保留任意分组键身份。这两个是Object/Map的静态方法，不是Array实例方法。旧环境可用Map或reduce实现。

```js
const users = [{ id: 1 }, { id: 2 }, { id: 1 }];
const latest = [...new Map(users.map(user => [user.id, user])).values()];
console.log(latest.map(user => user.id)); // [1,2]
```

这里按ID保留最后一次的值，但键的迭代位置保持第一次插入的位置。若要保留第一项、按最新出现顺序排列或按深层内容比较，算法都不同；先定义去重契约。Set去重对象按身份，不会自动按ID判断。

<a id="k06"></a>

### 6. 数组里的异步、复制与实战边界

forEach忽略回调返回值，所以forEach(async...)不会产生一个等待全部完成的Promise。map(async...)得到Promise数组，需用all/allSettled接管；大量任务还要限制启动并发。

```js
const tasks = [1, 2, 3].map(async n => n * 2);
console.log(await Promise.all(tasks)); // [2,4,6]
```

本例含顶层await，在.mjs、浏览器module或async包装中运行。顺序依赖用for...of配await；提前终止要区分停止遍历和取消已启动任务。数组拷贝也要分清外层与嵌套引用，尤其状态管理中“新数组”不等于“所有元素都新建”。

大型数组不能假定Math.max(...array)永远可用，展开实参数量受运行时限制；可用循环累积。算法选型还要考虑插入/查找方式和总数据规模，而不只比较代码短长。

<a id="summary"></a>

## 三、知识小结

按“创建→查找→变换→修改→排序→归并→迭代”查API。每次使用再检查四件事：返回什么、是否改原数组、如何处理空位、回调是否真的被等待。理解这四项能解释绝大多数数组面试变形题。

参考：[MDN Array](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array)。较新 API 按目标运行时核对支持，示例各自独立。

<a id="interview"></a>

## 四、面试题与答案

<a id="d08-01"></a>

### D08-01 [P0·基础] slice 和 splice 的参数与返回有何不同？

**回答：** slice按左闭右开区间返回浅复制，不改原数组；splice从start开始删除指定数量并插入元素，修改原数组，返回被删除项。toSpliced是不改原数组的编辑方法，返回编辑后的整个新数组，不是删除项。

对应讲解：[增删、截取、拼接与查找](#k02)。

<a id="d08-02"></a>

### D08-02 [P0·原理] map、forEach、filter、reduce 怎么选？

**回答：** map表达逐项转换，filter表达筛选，reduce表达累积归并，forEach用于同步副作用。它们返回值不同，不能因为都能循环就随便互换。reduce最好明确初值；forEach不会收集或等待async回调。

对应讲解：[遍历、映射、筛选与归并](#k03)。

<a id="d08-03"></a>

### D08-03 [P1·原理] 空位与 undefined 有什么区别？

**回答：** 空位是索引属性不存在，undefined可以是已存在索引的值。length可能相同，但in和自有检查不同；map等会跳过空位，而values/find等可将它读作undefined，所以不同方法输出不同。

对应讲解：[创建、length 与稀疏数组](#k01)。

<a id="d08-04"></a>

### D08-04 [P0·编码] 数组数值排序为什么要比较器？

**回答：** 默认sort按字符串比较，10会排在2之前。有限数值可用(a,b)=>a-b，比较器的负/零/正定义先后而非要求只返回-1/0/1。sort修改原数组；要保留原数组可用toSorted或先浅复制。

对应讲解：[排序、反转与复制修改](#k04)。

<a id="d08-05"></a>

### D08-05 [P0·原理] fill({}) 为何导致每行一起变化？

**回答：** fill把同一个对象引用放入每个位置，没有为各位置构造对象。修改其中一项属性时其他位置访问的仍是同一对象。用工厂回调逐项创建，例如Array.from({length:n},()=>({}))。

对应讲解：[排序、反转与复制修改](#k04)。

<a id="d08-06"></a>

### D08-06 [P0·编码] 怎么按用户ID去重？Set(users)行吗？

**回答：** Set按对象身份比较，两个不同对象即使id相同也不会合并。用Map以id为键，并先定义保留第一条还是最后一条，以及结果顺序。Map覆盖值不自动把已有键移到末尾。

对应讲解：[分组、去重与数据结构选择](#k05)。

<a id="d08-07"></a>

### D08-07 [P0·原理] await array.forEach(async ...) 为什么没等完？

**回答：** forEach立即返回undefined，且忽略各回调返回的Promise。外层await等的是undefined，不是任务集合。并发处理用Promise.all(array.map(...))并处理错误，大量任务用并发池，顺序处理用for...of。

对应讲解：[数组里的异步、复制与实战边界](#k06)。

<a id="d08-08"></a>

### D08-08 [P1·基础] find返回undefined，如何判断是不是没找到？

**回答：** 匹配到的元素本身也可能是undefined。需要索引时用findIndex/findLastIndex判断是否为-1；查索引属性存在时可用自有检查。不能只依赖返回值真假。

对应讲解：[增删、截取、拼接与查找](#k02)。

<a id="d08-09"></a>

### D08-09 [P1·原理] 数组新复制方法是否实现深拷贝？

**回答：** 没有。新外层数组与旧数组不同，但元素中的对象引用仍可共享；空位处理也可能和旧的slice/map不同。不可变状态更新仍应针对需要改变的对象层创建新结构。

对应讲解：[排序、反转与复制修改](#k04)。
