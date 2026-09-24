# 03 String

String 表示不可变的 UTF-16 字符串。查 API 时要同时看索引单位、范围是否包含终点、返回值和正则参数。本章覆盖现代常用 String API，并把历史兼容方法单独说明。

## 一、本章目录

- [字符串值、索引与不可变性](#k01)
- [字符读取与构造](#k02)
- [搜索、前后缀与位置参数](#k03)
- [截取、拆分和连接](#k04)
- [replace、replaceAll 与替换函数](#k05)
- [search、match 与 matchAll](#k06)
- [清理、补齐、重复与大小写](#k07)
- [规范化、比较与完整 Unicode](#k08)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 字符串值、索引与不可变性

字符串可以用单引号、双引号、模板字面量构造。模板字面量支持插值和换行；标签模板是一次函数调用，处理转义、插值和安全性取决于标签实现，不会自动防 XSS。

字符串方法不会原地改写字符串。length、方括号索引与大多数位置参数按 UTF-16 码元计算；一个码点可能占两个码元，多个码点还可能组成一个用户看到的字素。

```js
const text = 'A😀B';
console.log(text.length); // 4
console.log([...text].length); // 3
const trimmed = ' hi '.trim();
console.log(trimmed); // hi
```

字符串迭代器按码点推进，所以 [...text] 能合并代理对，但家庭 emoji、组合重音等仍可能是多个码点。视觉字符统计用 Intl.Segmenter 的 grapheme 粒度；字节数用 TextEncoder 等按指定编码计算。三种长度不可混用。

<a id="k02"></a>

### 2. 字符读取与构造

| API | 返回值 | 重要边界 |
| --- | --- | --- |
| str[index] | 单个码元的字符串或 undefined | 不支持负索引含义 |
| at(index) | 单个码元的字符串或 undefined | 负数从尾部数；仍不是完整码点读取 |
| charAt(index=0) | 字符串 | 越界为空串 |
| charCodeAt(index=0) | 码元值 | 越界 NaN |
| codePointAt(index=0) | 码点值或 undefined | 参数仍是码元位置；落在低代理位置只读该位置 |
| String.fromCharCode(...units) | 字符串 | 参数按 16 位码元处理 |
| String.fromCodePoint(...points) | 字符串 | 接收有效码点数值；越界抛 RangeError |
| String.raw(template,...values) | 原始模板片段拼接结果 | 保留模板片段反斜杠，不替用户输入做安全转义 |

```js
console.log('abc'.at(-1), 'abc'.charAt(-1)); // c ''
console.log('😀'.codePointAt(0).toString(16)); // 1f600
console.log(String.fromCodePoint(0x1f600)); // 😀
```

模板中的插值仍会被转换成字符串。String.raw 用于保留模板字面量的 raw 片段，不是“把任意字符串变成带转义的安全文本”。

<a id="k03"></a>

### 3. 搜索、前后缀与位置参数

| API | 形式和返回值 | 位置含义 |
| --- | --- | --- |
| includes(search, position=0) | Boolean | 从 position 向后找 |
| indexOf(search, position=0) | 首个匹配位置，未找到 -1 | 返回原字符串坐标 |
| lastIndexOf(search, position) | 最后一个符合条件的位置，未找到 -1 | 向前找；默认从尾部附近开始 |
| startsWith(search, position=0) | Boolean | 把 position 当候选开头 |
| endsWith(search, endPosition=str.length) | Boolean | 把 endPosition 当候选结束位置，不是开始位置 |

includes/startsWith/endsWith 按字符串搜索，不接受通常的正则对象作为搜索模式；需要正则用 search/test 等。匹配区分大小写，位置和返回坐标按码元计算。

```js
const text = 'abcabc';
console.log(text.includes('ab', 1)); // true
console.log(text.indexOf('ab', 1)); // 3
console.log(text.startsWith('ab', 3)); // true
console.log(text.endsWith('ab', 5)); // true
```

不要写 if (str.indexOf(term))：开头的匹配位置 0 是假值，未找到的 -1 却是真值。判断存在用 includes 或显式比较 !== -1。

<a id="k04"></a>

### 4. 截取、拆分和连接

| API | 返回值 | 行为 |
| --- | --- | --- |
| slice(start=0,end=length) | 子串 | 左闭右开；负位置从尾部算；start≥end 返回空串 |
| substring(start=0,end=length) | 子串 | 负值/NaN 按 0；较大起点与终点会交换 |
| split(separator,limit) | 字符串数组 | limit 是最多返回项数；省略 separator 通常返回整串一项 |
| concat(...values) | 新字符串 | 依次转成字符串拼接 |
| 数组.join(separator=',') | 字符串 | 属于 Array，用于把片段连接起来 |

```js
console.log('abcdef'.slice(4, 1)); // ''
console.log('abcdef'.substring(4, 1)); // bcd
console.log('a,b,c'.split(',', 2)); // ['a','b']
console.log('a1b2'.split(/(\d)/)); // ['a','1','b','2','']
```

split 的正则捕获组会进入结果；split('') 按码元切开，可能拆断 emoji。substr(start,length) 是历史方法，第二个参数是长度，不是终点；新代码优先用 slice。

<a id="k05"></a>

### 5. replace、replaceAll 与替换函数

replace(pattern,replacement) 返回替换后的新字符串：字符串模式通常只替换第一处；正则模式是否全局替换由 g 控制。replaceAll 匹配全部字符串出现位置；传正则时要求 g，否则抛 TypeError。

replacement 可以是字符串或函数。替换字符串中的 $$ 表示 $，$& 表示整个匹配，$1 等表示捕获组，$<name> 表示命名捕获组；还支持匹配前后片段的替换模式。因此“用户输入原样替换”应使用返回文本的函数。

```js
console.log('a-a'.replace('a', 'x')); // x-a
console.log('a-a'.replaceAll('a', 'x')); // x-x
const userText = '$&';
console.log('cat'.replace('cat', () => userText)); // $&
console.log('price=12'.replace(/\d+/, value => String(Number(value) * 2))); // price=24
```

函数参数包含匹配、各捕获组、偏移、原字符串，命名捕获时还会有 groups；参数位置受捕获组数量影响。动态构建正则还需正则转义，不能把替换文本转义与模式转义当成同一件事。

<a id="k06"></a>

### 6. search、match 与 matchAll

| API | 返回内容 | 适用场景 |
| --- | --- | --- |
| search(regexp) | 首个匹配索引，未找到 -1 | 只关心位置 |
| match(regexp)，无 g | 匹配数组含捕获组及 index/input/groups，或 null | 一次完整匹配信息 |
| match(regexp)，有 g | 各次完整匹配组成的数组，或 null | 只收集完整匹配文本 |
| matchAll(regexp) | 可迭代的各次匹配结果 | 收集每次捕获组和位置；正则需 g |

```js
const text = 'a=1 b=2';
console.log(text.match(/([a-z])=(\d)/g)); // ['a=1','b=2']
console.log([...text.matchAll(/([a-z])=(\d)/g)].map(m => [m[1], m[2]]));
// [['a','1'],['b','2']]
```

matchAll 返回迭代器，不是数组；可以 for...of 消费或展开收集。需要分步控制 lastIndex 时使用 RegExp.exec，但要处理空匹配与状态；详见 RegExp 章节。

<a id="k07"></a>

### 7. 清理、补齐、重复与大小写

| 方法 | 参数/返回 | 边界 |
| --- | --- | --- |
| trim() | 新字符串 | 删除两端语言定义的空白/换行，不删除中间空白 |
| trimStart()/trimEnd() | 新字符串 | 只处理一侧；trimLeft/trimRight 为别名 |
| padStart(targetLength,padString=' ')/padEnd(...) | 新字符串 | 目标按码元长度；原串更长时不截断；填充可被截取 |
| repeat(count) | 新字符串 | 重复次数取整数语义；负数、Infinity 或超长结果可抛错 |
| toLowerCase()/toUpperCase() | 新字符串 | Unicode 大小写转换，结果长度可能改变 |
| toLocaleLowerCase(locales?)/toLocaleUpperCase(locales?) | 新字符串 | 使用语言相关大小写规则 |

```js
console.log('7'.padStart(3, '0')); // 007
console.log('abcdef'.padEnd(3, '.')); // abcdef
console.log('ß'.toUpperCase()); // SS
console.log('x'.repeat(3)); // xxx
```

trim 不是清洗 HTML，大小写转换也不是任何语言下都正确的排序或等价规则。用户可见名称的比较需考虑语言环境。

<a id="k08"></a>

### 8. 规范化、比较与完整 Unicode

normalize(form='NFC') 支持 NFC/NFD/NFKC/NFKD。它处理 Unicode 规范/兼容等价，不处理所有视觉相似字符，也不是安全用户名策略的全部。

localeCompare(other,locales?,options?) 返回负数、0 或正数，不能假定非零只会是 -1/1。大量排序可复用 Intl.Collator。

isWellFormed() 判断是否包含孤立代理码元；toWellFormed() 将它们替换成 U+FFFD，返回新串。它们不判断语言文法或业务文本是否合法，也不负责字节解码。

```js
const a = 'é';
const b = 'e\u0301';
console.log(a === b); // false
console.log(a.normalize() === b.normalize()); // true
console.log('a'.localeCompare('b') < 0); // true
```

String 的 valueOf()/toString() 可从包装对象取得字符串；[Symbol.iterator]() 提供按码点迭代。anchor/big/blink/bold/fixed/fontcolor/fontsize/italics/link/small/strike/sub/sup 属于历史 HTML 包装方法，不用于现代 DOM 渲染。外部字符串应按输出上下文进行转义或清洗，textContent 适合纯文本。

<a id="summary"></a>

## 三、知识小结

String API 按“读字符、找位置、截片段、拆连接、做替换、处理正则、规范文本”记。所有操作都围绕不可变原值，但返回类型不同。输出题先写清码元/码点/字素，再检查位置参数是否含终点、是否支持负数及正则 g。

参考：[MDN String](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String)。较新 API 按目标运行时核对支持，示例各自独立。

<a id="interview"></a>

## 四、面试题与答案

<a id="d03-01"></a>

### D03-01 [P0·原理] 字符串不可变，为什么 name += 'x' 可以执行？

**回答：** += 计算拼接结果，再给变量重新赋值；没有修改原字符串本身。若绑定是 const 则重新赋值失败。String 方法也返回结果而不改原值，应根据返回类型使用它。

对应讲解：[字符串值、索引与不可变性](#k01)。

<a id="d03-02"></a>

### D03-02 [P1·原理] length、展开数组与用户看到的字符数为何不同？

**回答：** length 按 UTF-16 码元；字符串迭代器按码点，所以展开能合并代理对；一个字素仍可由多个码点组成，视觉计数需要 Intl.Segmenter。UTF-8 字节数又是编码层的数量，不能用任一字符计数代替。

对应讲解：[字符串值、索引与不可变性](#k01)。

<a id="d03-03"></a>

### D03-03 [P0·基础] slice、substring、substr 怎样区分？

**回答：** slice 和 substring 都按左闭右开区间截取；slice 支持负索引且不交换反向起止，substring 把负数按0处理并交换反向边界。substr 第二参数是长度，属于历史兼容方法，新代码优先 slice。

对应讲解：[截取、拆分和连接](#k04)。

<a id="d03-04"></a>

### D03-04 [P0·基础] includes 与 indexOf 怎么选，endsWith 第二参数是什么？

**回答：** 只判断存在用 includes；需要位置用 indexOf 并判断是否为 -1。startsWith 的第二参数是候选起点，endsWith 的第二参数是候选结束位置，且不包含该位置的字符。

对应讲解：[搜索、前后缀与位置参数](#k03)。

<a id="d03-05"></a>

### D03-05 [P0·原理] replaceAll 就是 replace 加一个 g 吗？

**回答：** 它解决全量替换，但字符串模式与正则模式的传参规则仍不同。replace 的字符串模式只替换第一处，replaceAll 的字符串模式匹配全部；replaceAll 收到正则必须有 g。替换字符串还会解释 $&/$1 等模式，原样插入用户文本适合使用替换函数。

对应讲解：[replace、replaceAll 与替换函数](#k05)。

<a id="d03-06"></a>

### D03-06 [P1·原理] match 有 g 为什么丢了捕获组？

**回答：** 全局 match 的结果是各次完整匹配文本，不保留每次捕获组结构。无 g 的 match 取得一次匹配及其捕获；需要全量匹配并保留捕获和位置，用 matchAll 的迭代器或谨慎使用 exec 循环。

对应讲解：[search、match 与 matchAll](#k06)。

<a id="d03-07"></a>

### D03-07 [P1·原理] split 的 limit 是分割次数吗？

**回答：** 不是，是最多返回的数组项数，达到上限后的余下文本不自动合并为最后一项。正则捕获组还可能作为独立项进入结果；split('') 按码元切分，可能拆断代理对。

对应讲解：[截取、拆分和连接](#k04)。

<a id="d03-08"></a>

### D03-08 [P1·工程取舍] 统一转小写就能实现国际化搜索和排序吗？

**回答：** 不能。语言相关大小写、重音与规范等价是不同问题。可以按需求使用 normalize、locale 相关转换和 Intl.Collator；还要确定是否忽略大小写和重音。视觉相似和安全标识规则又需额外设计。

对应讲解：[规范化、比较与完整 Unicode](#k08)。

<a id="d03-09"></a>

### D03-09 [P1·基础] padStart 会截断长字符串吗？String.raw 会清洗 HTML 吗？

**回答：** padStart 在原串已达目标长度时保持原内容，只为短串补齐；String.raw 保留模板原始片段中的反斜杠，不负责 HTML 清洗或用户数据安全。它们的名字不能替代对具体契约的理解。

对应讲解：[清理、补齐、重复与大小写](#k07)。
