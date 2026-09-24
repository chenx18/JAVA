# 05 BigInt

BigInt 为超过 Number 安全范围的整数提供精确整数运算。它不是任意精度小数，也不会自动解决接口解析和序列化问题。

## 一、本章目录

- [创建与准确性](#k01)
- [运算与混合类型](#k02)
- [API、位宽与字符串](#k03)
- [JSON 与业务协议](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 创建与准确性

使用整数后缀 n，或从整数文本构造。输入若先变成不准确的 Number，BigInt 只会精确保存那个已经错误的整数。

```js
const id = BigInt('9007199254740993');
console.log(id + 1n); // 9007199254740994n
console.log(BigInt(9007199254740993)); // 9007199254740992n
```

BigInt('0xff') 可接受相应整数文本；BigInt(1.5) 抛 RangeError，不会替你取整。BigInt 不能 new，Object(1n) 是包装对象。字符串ID若只用于标识、拼接和比较相等，保留字符串往往比引入大整数运算更简单。

<a id="k02"></a>

### 2. 运算与混合类型

BigInt 支持 +、-、*、/、%、**、比较及多数位运算。除法向零截断，不产生小数；指数需是非负 BigInt；除以0n会抛错。

```js
console.log(7n / 3n, -7n / 3n); // 2n -2n
console.log(1n == 1, 1n === 1); // true false
console.log(2n > 1, 2n < 3); // true true
```

Number 与 BigInt 通常不能混合算术，需要显式选择统一类型；部分比较允许混合，一元 + 和无符号右移 >>> 不支持 BigInt。Math 方法通常要求 Number，也不能直接喂 BigInt。

当 + 一侧为字符串时可以发生字符串拼接，不可把“BigInt 一切混合操作都报错”作为规则。转换回 Number 可能丢精度，先核对安全范围。

<a id="k03"></a>

### 3. API、位宽与字符串

| API | 返回 | 语义 |
| --- | --- | --- |
| BigInt(value) | BigInt | 接受可转换的整数值/文本；非法输入抛错 |
| BigInt.asIntN(bits,value) | BigInt | 按 bits 位有符号二进制截断解释 |
| BigInt.asUintN(bits,value) | BigInt | 按 bits 位无符号截断解释 |
| value.toString(radix=10) | 字符串 | radix 为 2—36 |
| value.toLocaleString(locales?,options?) | 字符串 | 本地化展示，不能当稳定机器协议 |
| value.valueOf() | BigInt | 包装对象解包 |

```js
console.log(BigInt.asIntN(8, 255n)); // -1n
console.log(BigInt.asUintN(8, -1n)); // 255n
console.log((255n).toString(16)); // ff
```

asIntN/asUintN 用于有限位宽协议，不是“校验是否在范围内”：超界会截断，想拒绝超界输入应先主动判断上下界。

<a id="k04"></a>

### 4. JSON 与业务协议

JSON 标准没有 BigInt 字面量。默认 JSON.stringify 遇到 BigInt 会抛 TypeError，项目应约定字符串字段或带类型的编码。

```js
const data = { id: 9007199254740993n };
const json = JSON.stringify(data, (_key, value) =>
  typeof value === 'bigint' ? value.toString() : value
);
console.log(json); // {"id":"9007199254740993"}
console.log(BigInt(JSON.parse(json).id)); // 9007199254740993n
```

不能把所有“只含数字的字符串”在 reviver 中都变成 BigInt，手机号、邮编、带前导零的业务编码可能必须保持字符串。字段还要限制长度，以免超大输入带来不必要的计算/内存成本。

<a id="summary"></a>

## 三、知识小结

BigInt 记住四点：精确整数、算术类型要统一、有限位宽会截断、JSON 需要协议。最重要的起点是避免数据在转 BigInt 前已被 Number 舍入。

参考：[MDN BigInt](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/BigInt)。较新 API 按目标运行时核对支持，示例各自独立。

<a id="interview"></a>

## 四、面试题与答案

<a id="d05-01"></a>

### D05-01 [P0·基础] BigInt 比 Number 精确，是否可以全部替换？

**回答：** 不适合。BigInt 只表示整数，没有小数和 Number 的所有API行为；混合算术、Math 和 JSON 都有边界。精确大整数运算适合 BigInt，普通实数和业务标识则分别按需求选 Number、十进制方案或字符串。

对应讲解：[创建与准确性](#k01)。

<a id="d05-02"></a>

### D05-02 [P1·原理] BigInt(超大数字字面量) 为什么仍然不准？

**回答：** 字面量若先按 Number 求值，就可能已经舍入。BigInt 构造只读取这个结果，不能恢复原始十进制文本。使用 n 字面量或从原始字符串构造。

对应讲解：[创建与准确性](#k01)。

<a id="d05-03"></a>

### D05-03 [P1·基础] -7n/3n 的结果是什么？与 Number 有什么区别？

**回答：** 结果为-2n，BigInt 除法向零截断，余数和整数运算按BigInt规则处理；Number除法可以产生近似小数。不能把它误记成向负无穷取整。

对应讲解：[运算与混合类型](#k02)。

<a id="d05-04"></a>

### D05-04 [P1·工程取舍] 大整数往返接口怎样保证一致？

**回答：** 在协议中明确哪些字段为大整数、用什么编码，并在解析前避免变成不安全 Number。常见方案是后端输出十进制字符串，前端按字段需要转 BigInt；序列化时再还原字符串。不要对任意数字字符串全局猜类型。

对应讲解：[JSON 与业务协议](#k04)。
