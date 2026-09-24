# 04 Number

Number 同时表示整数和小数，但这不意味着任意整数、小数都能精确保存。本章将数值表示、校验、文本解析和展示格式分开，避免用格式化掩盖计算错误。

## 一、本章目录

- [二进制浮点与精度](#k01)
- [安全整数、NaN、Infinity 与正负零](#k02)
- [构造、解析与校验 API](#k03)
- [实例方法与展示](#k04)
- [误差比较与业务数值](#k05)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 二进制浮点与精度

Number 的语义基于 IEEE 754 双精度浮点表示，有限位数需要表示符号、指数和有效数字。0.1 这样的十进制有限小数在二进制中通常是无限循环，存储与运算会舍入。

```js
console.log(0.1 + 0.2); // 0.30000000000000004
console.log(0.1 + 0.2 === 0.3); // false
console.log((0.1 + 0.2).toFixed(2)); // '0.30'
```

最后一行只是把结果格式化成字符串，不会把底层 Number 改成精确十进制。金额可按最小货币单位使用安全范围内整数，或使用经过验证的十进制库；税率、汇率和分摊还需明确舍入规则，不能仅写 Math.round(x*100)/100 就宣称精确财务计算。

<a id="k02"></a>

### 2. 安全整数、NaN、Infinity 与正负零

| 值/属性 | 含义 | 注意 |
| --- | --- | --- |
| MAX_SAFE_INTEGER / MIN_SAFE_INTEGER | ±(2^53−1) | 该范围内整数可可靠精确区分 |
| MAX_VALUE | 最大有限正 Number | 不是最大安全整数 |
| MIN_VALUE | 最小正非零 Number | 不是最负值 |
| EPSILON | 1 附近相邻可表示数的间距 | 不是适用所有数量级的统一误差 |
| NaN | 数值计算中的无效结果 | 属于 number，且不与自身 === |
| POSITIVE_INFINITY / NEGATIVE_INFINITY | 正负无穷 | 仍属于 number |
| +0 / -0 | 有符号的零 | === 相等，Object.is 可区分 |

```js
console.log(2 ** 53 === 2 ** 53 + 1); // true
console.log(Number.isNaN(0 / 0)); // true
console.log(1 / 0, 1 / -0); // Infinity -Infinity
console.log(Object.is(0, -0)); // false
```

超出安全范围不意味着每个数都不精确；问题是不能保证相邻整数都可区分。把已经舍入的 Number 转 BigInt 或 String 无法恢复原始整数。接口 ID 通常应从传输层就使用字符串。

<a id="k03"></a>

### 3. 构造、解析与校验 API

| API | 返回/用途 | 关键区别 |
| --- | --- | --- |
| Number(value) | Number 原始值 | 空白字符串/null→0；Symbol 抛错；BigInt 可转但可能失精度 |
| new Number(value) | 包装对象 | 业务中通常不需要，对象条件判断为真 |
| Number.parseInt(text,radix) | 整数 Number 或 NaN | 与全局 parseInt 同类行为，前缀解析 |
| Number.parseFloat(text) | Number 或 NaN | 与全局 parseFloat 同类行为 |
| Number.isNaN(value) | Boolean | 只认可 Number NaN，不做隐式数值转换 |
| Number.isFinite(value) | Boolean | 值必须是有限 Number |
| Number.isInteger(value) | Boolean | 检查已表示出来的值是否为有限整数 |
| Number.isSafeInteger(value) | Boolean | 整数且处于安全范围 |

全局 isNaN/isFinite 会先转换，例如 isNaN('x') 为 true，Number.isNaN('x') 为 false。Number.isInteger 也不能修复精度问题：传入表达式在调用前就可能已经舍入。

```js
console.log(isNaN('x'), Number.isNaN('x')); // true false
console.log(isFinite('3'), Number.isFinite('3')); // true false
console.log(Number.isSafeInteger(2 ** 53)); // false
```

<a id="k04"></a>

### 4. 实例方法与展示

| 方法 | 返回值 | 主要参数与边界 |
| --- | --- | --- |
| toString(radix=10) | 字符串 | 进制 2—36；不会添加 0x 等前缀 |
| toFixed(digits=0) | 字符串 | 指定小数位；现代标准 digits 为 0—100；大数量级可能指数显示 |
| toExponential(fractionDigits?) | 字符串 | 指数表示，小数位参数可省略 |
| toPrecision(precision?) | 字符串 | 指定有效数字数；省略通常等同普通表示；范围 1—100 |
| toLocaleString(locales?,options?) | 本地化字符串 | 可能有分组符号和不换行空格，不用于可靠反向解析 |
| valueOf() | Number 原始值 | 常用于从包装对象取值 |

```js
console.log((255).toString(16)); // ff
console.log((12.345).toFixed(2)); // '12.35'
console.log((1234).toPrecision(3)); // '1.23e+3'
```

小数的真实存储值可能位于十进制直觉的另一侧，因此 toFixed 的舍入结果不能仅凭手写十进制“正好一半”判断。批量格式化货币可复用 Intl.NumberFormat。

<a id="k05"></a>

### 5. 误差比较与业务数值

对于计算得到的近似实数，可按业务设计绝对与相对容差。例如差值小于 max(absTolerance, relTolerance × max(|a|,|b|)) 时视为足够接近。容差由数据单位、数量级和误差预算决定，不能无条件套 Number.EPSILON。

```js
function nearlyEqual(a, b, absTolerance = 1e-12, relTolerance = 1e-12) {
  if (a === b) return true;
  if (!Number.isFinite(a) || !Number.isFinite(b)) return false;
  return Math.abs(a - b) <= Math.max(
    absTolerance, relTolerance * Math.max(Math.abs(a), Math.abs(b))
  );
}
console.log(nearlyEqual(0.1 + 0.2, 0.3)); // true
```

这用于声明允许近似的计算，不用于把两个不同订单 ID 或账户金额随意判成相等。日志还应保留原始输入及协议单位，便于区分解析错误与计算误差。

<a id="summary"></a>

## 三、知识小结

数值处理依次考虑：输入契约、是否有限、整数与安全范围、运算精度、最后展示。记忆时把“可表示的值”“可安全区分的整数”“格式化后的字符串”分开。

参考：[MDN Number](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Number)。较新 API 按目标运行时核对支持，示例各自独立。

<a id="interview"></a>

## 四、面试题与答案

<a id="d04-01"></a>

### D04-01 [P0·原理] 0.1+0.2 为什么不等于 0.3？

**回答：** 十进制小数转为有限位二进制浮点时可能舍入，运算后又按可表示值舍入，因此结果与字面量0.3的表示不一致。不是所有小数运算都错，而是有限表示不能覆盖所有实数。

**追问与回答：** 金额怎样处理？约定最小单位或十进制计算方案，并规定舍入与安全范围；toFixed 仅解决显示。

对应讲解：[二进制浮点与精度](#k01)。

<a id="d04-02"></a>

### D04-02 [P0·基础] MAX_VALUE 和 MAX_SAFE_INTEGER 为什么不同？

**回答：** MAX_VALUE 描述最大有限数量级，MAX_SAFE_INTEGER 描述整数精确区分的安全边界。很大的 Number 可以存在，但相邻整数可能被舍入成同一值，因此最大值范围不能保证ID精度。

对应讲解：[安全整数、NaN、Infinity 与正负零](#k02)。

<a id="d04-03"></a>

### D04-03 [P0·基础] 判断数字合法，typeof 足够吗？

**回答：** typeof NaN 和 Infinity 都为 number。通常先用 Number.isFinite，再根据业务检查整数、安全范围和上下界；若输入允许字符串，还应独立定义转换规则。

对应讲解：[构造、解析与校验 API](#k03)。

<a id="d04-04"></a>

### D04-04 [P1·原理] Number.EPSILON 能修复所有浮点比较吗？

**回答：** 它只是1附近的表示间距，数值数量级与业务精度变化后，同一绝对阈值可能太小或太大。应使用有业务依据的绝对/相对容差，或在要求精确时改用合适的数据表示。

对应讲解：[误差比较与业务数值](#k05)。

<a id="d04-05"></a>

### D04-05 [P0·基础] toFixed、toPrecision 和 Math.round 有何区别？

**回答：** toFixed 指定小数位，toPrecision 指定有效数字，二者返回字符串；Math.round 返回舍入后的数值，规则是最接近整数且中点偏正无穷。格式化函数不改变原 Number 的精度。

对应讲解：[实例方法与展示](#k04)。
