# 15 JSON与运行时校验

JSON 是交换文本格式，不是JavaScript对象本身。序列化、语法解析、数据结构校验和业务校验是四个不同步骤。

## 一、本章目录

- [JSON 语法及支持范围](#k01)
- [stringify：特殊值与自定义序列化](#k02)
- [parse、reviver 与原始文本](#k03)
- [rawJSON 与新接口边界](#k04)
- [运行时校验与业务边界](#k05)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. JSON 语法及支持范围

JSON允许对象、数组、字符串、数字、true、false、null，键和字符串用双引号；不支持注释、尾逗号、undefined、函数、Symbol、BigInt字面量或NaN/Infinity。JavaScript对象字面量合法，不意味着其文本是合法JSON。

JSON.parse只按JSON语法生成值，不会执行其中的函数或脚本。解析出的对象继续进入合并、HTML渲染或命令拼接时，仍需相应安全处理。

<a id="k02"></a>

### 2. stringify：特殊值与自定义序列化

JSON.stringify(value,replacer?,space?)返回字符串，有些顶层值返回undefined，也可能抛错。只处理自有可枚举字符串属性；Symbol键忽略。

| 值 | 对象属性 | 数组元素/顶层的特殊行为 |
| --- | --- | --- |
| undefined、函数、Symbol | 通常省略 | 数组中为null；顶层通常undefined |
| NaN、Infinity、-Infinity | null | null |
| Date | 通过toJSON变ISO文本 | 无效Date通常null |
| Map/Set | 默认通常{} | 集合条目不是普通可枚举字段 |
| BigInt | 默认抛TypeError | 需要明确编码策略 |
| 循环引用 | 抛TypeError | 不是图结构序列化 |

replacer可以是允许的键列表或转换函数；space可以是缩进空格数/字符串，有长度上限。toJSON在对应序列化过程中先定制值，replacer再参与转换；读取getter也可能有副作用。

```js
console.log(JSON.stringify({ a: undefined, b: NaN })); // {"b":null}
console.log(JSON.stringify([undefined, NaN])); // [null,null]
console.log(JSON.stringify({ id: 1n }, (_k, v) => typeof v === 'bigint' ? String(v) : v));
// {"id":"1"}
```

<a id="k03"></a>

### 3. parse、reviver 与原始文本

JSON.parse(text,reviver?)先解析，再按从内向外的遍历调用reviver。返回undefined会删除对应属性；根键通常是空串，根也可被替换。它不是一个自动可信的类型恢复系统。

```js
const data = JSON.parse('{"createdAt":"2026-01-01T00:00:00Z","debug":1}', (key, value) => {
  if (key === 'debug') return undefined;
  if (key === 'createdAt') return new Date(value);
  return value;
});
console.log(data.createdAt instanceof Date, 'debug' in data); // true false
```

按字段恢复Date/BigInt优于猜“所有像日期或数字的字符串”。现代运行时对原始值的reviver可能提供第三参数context.source，用于读取原文本，从而在支持时重新处理超大整数；经典的只读value回调拿到的Number可能已失精度。协议直接给字符串更具跨环境可移植性。

<a id="k04"></a>

### 4. rawJSON 与新接口边界

支持时，JSON.rawJSON(text)建立原始JSON片段包装，JSON.isRawJSON(value)检测这类包装；stringify会将它当作已经表示好的JSON原始值文本插入。输入受API约束，不能当任意字符串拼接入口；rawJSON当前只接受符合其规则的原始值文本，不把对象/数组文本当通用拼接节点。

它可帮助输出不经Number舍入的整数文本，却不能保证接收方使用普通JSON.parse后仍精确。生产仍需共同协议、输入校验和运行时支持，不用新API掩盖两端解析差异。

<a id="k05"></a>

### 5. 运行时校验与业务边界

校验层次应明确：

1. 网络/HTTP层：是否取得可接受响应。
2. JSON语法层：是否能parse。
3. 结构层：是对象还是数组，字段类型、必选项、长度等是否符合schema。
4. 业务层：权限、状态、范围、关联关系是否允许操作。

```js
function isUser(value) {
  return value !== null && typeof value === 'object' &&
    !Array.isArray(value) &&
    typeof value.id === 'string' &&
    typeof value.name === 'string' && value.name.length > 0;
}
console.log(isUser(JSON.parse('{"id":"1","name":"A"}'))); // true
console.log(isUser(JSON.parse('{"id":1,"name":null}'))); // false
```

此示例只验证两个字段，不代表完整用户schema。TypeScript类型在运行时通常被擦除，不能替代对接口、存储、模型输出的校验。前端验证改善交互，后端仍要执行权威验证与授权。

<a id="summary"></a>

## 三、知识小结

先问“合法JSON吗”，再问“结构正确吗”，最后问“业务允许吗”。序列化会丢失或改变某些值，所以JSON往返不能当作任意对象的保真复制。

参考：[MDN JSON](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/JSON)。较新 API 按目标运行时核对支持，示例各自独立。

<a id="interview"></a>

## 四、面试题与答案

<a id="d15-01"></a>

### D15-01 [P0·基础] JSON与JS对象的区别是什么？

**回答：** JSON是有明确语法和类型范围的文本格式，JS对象是运行时值。对象能包含函数、Symbol键、原型与循环，JSON不能直接表达全部这些语义；合法对象字面量文本也不一定是合法JSON。

对应讲解：[JSON 语法及支持范围](#k01)。

<a id="d15-02"></a>

### D15-02 [P0·原理] stringify为什么会丢字段或变成null？

**回答：** 序列化有规定的值处理规则：对象中的undefined/函数/Symbol值通常被省略，数组中对应位置通常变null，NaN和无穷也变null。BigInt和循环还可能抛错，因此需要明确协议而非默认保真。

对应讲解：[stringify：特殊值与自定义序列化](#k02)。

<a id="d15-03"></a>

### D15-03 [P1·原理] reviver什么时候能恢复超大整数？

**回答：** 传统只读取已解析value时，Number可能已舍入，无法恢复。支持context.source的运行时可按特定字段读取原始数值文本重新解析；更通用的协议是在发送端将大整数编码为字符串。

对应讲解：[parse、reviver 与原始文本](#k03)。

<a id="d15-04"></a>

### D15-04 [P0·工程取舍] JSON.parse成功就能信任数据吗？

**回答：** 只能说明语法可解析，结果甚至可能只是null或数字。还要校验schema、长度范围和业务权限，并根据后续输出上下文处理安全问题。静态类型声明不会自动验证网络数据。

对应讲解：[运行时校验与业务边界](#k05)。

<a id="d15-05"></a>

### D15-05 [P1·原理] JSON.rawJSON是否解决了大整数的所有问题？

**回答：** 没有，它帮助控制输出JSON文本，但接收方若仍按不安全Number解析就会失精度。还需接口契约、兼容性和字段级处理，并遵守rawJSON对输入片段的约束。

对应讲解：[rawJSON 与新接口边界](#k04)。
