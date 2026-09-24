# 10 Set

Set 表达成员唯一性。使用前要先确定“相同”指值、对象身份，还是业务字段；只有前两者是原生集合直接理解的规则。

## 一、本章目录

- [唯一性与成员身份](#k01)
- [完整常用 API](#k02)
- [集合运算及兼容实现](#k03)
- [业务去重与事件幂等](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 唯一性与成员身份

Set使用SameValueZero：NaN可去重，正负零视为相同，两个不同对象即使字段相同仍是两个成员。迭代按插入顺序，不会自动数值排序。

```js
console.log([...new Set([1, 1, NaN, NaN, -0, 0])]); // [1,NaN,0]
console.log(new Set([{ id: 1 }, { id: 1 }]).size); // 2
```

数组的稀疏空位经迭代读作undefined，new Set(new Array(3))只有一个undefined成员。成员为对象时，后来修改其字段不会改变其身份，也不会自动与其他对象合并。

<a id="k02"></a>

### 2. 完整常用 API

| API | 结果 |
| --- | --- |
| new Set(iterable?) | 从迭代值创建集合 |
| size | 成员数，只读访问器 |
| add(value) | 返回当前Set；重复成员不再次插入 |
| has(value) | Boolean |
| delete(value) | 是否移除成员 |
| clear() | 清空，返回undefined |
| values()/keys() | 值迭代器；两者对Set含义相同 |
| entries() | [value,value]迭代器 |
| forEach(fn,thisArg?) | 回调(value,value,set)，返回undefined |
| [Symbol.iterator]() | 默认值迭代器 |

forEach把值传两遍是接口形状兼容，并不代表Set内部有独立键值。遍历期间增删会影响过程，稳妥的通知或算法通常先确定是否要快照。

<a id="k03"></a>

### 3. 集合运算及兼容实现

现代Set提供union(other)、intersection(other)、difference(other)、symmetricDifference(other)，返回新的Set；isSubsetOf、isSupersetOf、isDisjointFrom返回Boolean。other通常需要set-like接口：size、has、keys，而不只是任意可迭代对象；数组不能无条件直接当other。

旧环境可用下列显式算法，参数约定为Set：

```js
const a = new Set([1, 2]), b = new Set([2, 3]);
const union = new Set([...a, ...b]);
const intersection = new Set([...a].filter(x => b.has(x)));
const difference = new Set([...a].filter(x => !b.has(x)));
console.log([...union], [...intersection], [...difference]); // [1,2,3] [2] [1]
```

这些结果按本实现的迭代顺序产生；原生交集等方法可能依据集合大小选择遍历方向，不能把成员相同误当成所有实现的迭代顺序都相同。新API发布与目标浏览器支持是两个问题。

<a id="k04"></a>

### 4. 业务去重与事件幂等

按id去重可维护seenIds，并决定保留第一个还是最后一个记录。对于流式事件去重，还要有稳定事件ID、保留上限和作用域；不能用文本相同来判定两条事件相同。

前端Set只能阻止某个运行实例中的重复处理，刷新后会丢失，也无法替代后端事务和幂等键。长会话中的去重集合同样需要淘汰策略，避免为了去重造成无限内存增长。

<a id="summary"></a>

## 三、知识小结

Set负责成员关系和集合运算，不理解深层内容相等、不自动排序、不提供持久幂等。实际题目先定义比较规则，再选Set或Map。

参考：[MDN Set](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Set)。较新 API 按目标运行时核对支持，示例各自独立。

<a id="interview"></a>

## 四、面试题与答案

<a id="d10-01"></a>

### D10-01 [P0·原理] Set为什么不能按对象内容去重？

**回答：** 原生比较对象身份，两个对象实例即使属性相同也不同。按业务ID去重需提取键并维护Set/Map；深层相等还要另定义比较与成本。

对应讲解：[唯一性与成员身份](#k01)。

<a id="d10-02"></a>

### D10-02 [P1·基础] Set的keys、values、entries有何不同？

**回答：** keys和values都迭代成员值，entries产生[value,value]，默认迭代也是值。forEach回调前两个参数也都是该值，不能把Set理解成有另一份独立键的Map。

对应讲解：[完整常用 API](#k02)。

<a id="d10-03"></a>

### D10-03 [P1·编码] 并集、交集、差集如何实现？

**回答：** 并集加入两边全部成员；交集遍历一边并用另一边has过滤；差集保留不在另一边的成员。选择遍历方向时同时考虑大小和输出顺序，原生新方法也需要运行时支持。

对应讲解：[集合运算及兼容实现](#k03)。

<a id="d10-04"></a>

### D10-04 [P1·工程取舍] 用Set记录事件ID就实现了幂等吗？

**回答：** 只实现当前作用域内、记录仍存在期间的去重，不保证服务器副作用只执行一次。还需稳定ID、会话隔离、容量策略，跨刷新或跨端可靠性由持久化与后端幂等协议保证。

对应讲解：[业务去重与事件幂等](#k04)。
