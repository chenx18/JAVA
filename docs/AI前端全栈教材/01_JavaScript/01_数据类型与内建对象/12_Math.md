# 12 Math

Math 是提供数值工具的静态对象，不是构造器。它处理Number数值；随机、取整和浮点辅助各有边界，不能靠简写替代输入契约。

## 一、本章目录

- [取整、符号与极值](#k01)
- [幂根、指数、对数与精度辅助](#k02)
- [常量、三角与双曲函数](#k03)
- [random 与区间随机数](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 取整、符号与极值

| 方法 | 返回语义 |
| --- | --- |
| floor(x) / ceil(x) | 向负无穷/正无穷取整 |
| trunc(x) | 删除小数部分，趋向0 |
| round(x) | 最近整数，中点偏正无穷；可能产生-0 |
| abs(x) / sign(x) | 绝对值/符号；sign保留零的符号并对NaN返回NaN |
| min(...values) / max(...values) | 极值；空参数分别Infinity/-Infinity |

```js
console.log(Math.floor(-1.5), Math.ceil(-1.5), Math.trunc(-1.5), Math.round(-1.5));
// -2 -1 -1 -1
console.log(Math.max(), Math.min()); // -Infinity Infinity
```

用|0或~~代替取整会把Number压成32位有符号整数，大整数可能溢出，不是Math.trunc的通用替代。巨大数组不要无条件展开成函数实参，可循环比较并定义NaN处理策略。

<a id="k02"></a>

### 2. 幂根、指数、对数与精度辅助

| 方法族 | 作用与参数 |
| --- | --- |
| pow(x,y)、sqrt(x)、cbrt(x) | 幂、非负平方根、立方根；负数sqrt为NaN |
| hypot(...values) | 平方和的平方根，处理多维距离 |
| exp(x)、expm1(x) | e^x 与 e^x−1；后者适合接近0的差值 |
| log(x)、log10(x)、log2(x)、log1p(x) | 自然/十进/二进对数与log(1+x) |
| imul(a,b) | 32位整数乘法结果 |
| clz32(x) | 转32位无符号表示后的前导零数量 |
| fround(x)、f16round(x) | 舍入至单精度/半精度表示；后者需支持检查 |
| sumPrecise(iterable) | 较新的更精确数值求和，减少中间累积舍入；需支持检查 |

```js
console.log(Math.cbrt(-8), Math.hypot(3, 4)); // -2 5
console.log(Math.imul(0xffffffff, 5)); // -5
console.log(Math.clz32(1)); // 31
```

这些方法不是大整数或精确十进制库。sumPrecise也不能恢复输入在进入算法前已经丢失的信息，或表示任意实数。

<a id="k03"></a>

### 3. 常量、三角与双曲函数

常量包括E、PI、LN2、LN10、LOG2E、LOG10E、SQRT1_2、SQRT2。sin/cos/tan接收弧度，asin/acos/atan返回角度的弧度值；atan2(y,x)根据坐标象限计算角度，参数顺序先y后x。

sinh/cosh/tanh以及asinh/acosh/atanh为双曲及反双曲函数。注意各自定义域，非法数值通常得到NaN或无穷，而不是业务友好的报错。角度转弧度用degrees*Math.PI/180。

```js
console.log(Math.abs(Math.sin(Math.PI / 2) - 1) < 1e-12); // true
console.log(Math.atan2(1, 0) === Math.PI / 2); // true
```

<a id="k04"></a>

### 4. random 与区间随机数

Math.random()产生[0,1)内的伪随机数；不是密码学安全随机源。整数min到max都包含时，常见表达式是floor(random()*(max-min+1))+min，前提是边界为合理安全整数，范围与浮点精度可接受。

```js
function randomInteger(min, max, random = Math.random) {
  if (!Number.isSafeInteger(min) || !Number.isSafeInteger(max) || min > max ||
      !Number.isSafeInteger(max - min + 1)) throw new RangeError('invalid range');
  return Math.floor(random() * (max - min + 1)) + min;
}
console.log(randomInteger(1, 6, () => 0)); // 1
console.log(randomInteger(1, 6, () => 0.5)); // 4
```

严谨均匀取安全整数还需处理可表示范围与偏差；密码、token、验证码使用Web Crypto或服务端安全随机。数组洗牌应用Fisher–Yates等明确算法，sort(()=>Math.random()-0.5)比较器不一致且会有偏差。

<a id="summary"></a>

## 三、知识小结

取整先看方向，位操作先看32位，三角先看弧度，随机先看安全目标。Math提供数值操作，不替代精度、输入范围与安全协议设计。

参考：[MDN Math](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Math)。较新 API 按目标运行时核对支持，示例各自独立。

<a id="interview"></a>

## 四、面试题与答案

<a id="d12-01"></a>

### D12-01 [P0·基础] floor、ceil、trunc、round对负数如何不同？

**回答：** floor向负无穷，ceil向正无穷，trunc趋向0，round取最近整数且中点偏正无穷。以-1.5为例分别为-2、-1、-1、-1，所以不能把所有取整都称作去掉小数。

对应讲解：[取整、符号与极值](#k01)。

<a id="d12-02"></a>

### D12-02 [P1·原理] 为什么|0不能普遍代替Math.trunc？

**回答：** 位运算会按32位整数规则转换，超出范围的值会截断或改变符号，NaN等也有特定转换；Math.trunc只是按Number语义去小数。短写法改变了支持范围。

对应讲解：[取整、符号与极值](#k01)。

<a id="d12-03"></a>

### D12-03 [P1·工程取舍] Math.random能生成登录token吗？

**回答：** 不应使用。它不是密码学安全随机，缺少这种安全保证。应使用Web Crypto或服务端可靠随机源，并按认证协议处理长度、编码与存储；随机展示和安全凭证是不同目标。

对应讲解：[random 与区间随机数](#k04)。

<a id="d12-04"></a>

### D12-04 [P1·基础] 为什么Math.max(...大数组)可能报错？

**回答：** 展开会把每个元素变成实参，运行时对参数数量和资源有限制。用循环或分块归并求极值更可控，还要明确空输入和NaN的返回策略。

对应讲解：[取整、符号与极值](#k01)。
