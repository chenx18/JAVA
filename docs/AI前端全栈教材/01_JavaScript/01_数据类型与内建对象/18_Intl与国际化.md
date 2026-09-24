# 18 Intl与国际化

国际化不仅是翻译文案，还包括数字、日期、排序、复数和字符边界。Intl把这些规则交给明确的locale与选项，而不是拼接几个符号。

## 一、本章目录

- [语言、地区与选项](#k01)
- [数字与日期](#k02)
- [排序与字素](#k03)
- [复数、相对时间、列表与名称](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 语言、地区与选项

locale标签如zh-CN、en-US、tr-TR描述语言/地区约定；时区是独立配置，例如Asia/Shanghai，不应仅由语言猜时区。Intl.getCanonicalLocales标准化标签，Intl.Locale对象可读取或构造语言、地区、脚本、日历等偏好，maximize/minimize处理可能的子标签扩展。

各格式器常有supportedLocalesOf检查请求locale支持，resolvedOptions查看最终采用的配置。Intl.supportedValuesOf(key)可在支持的环境查询日历、货币、时区等类别的可用值，但不是任意语言标签列表。即使API存在，环境的ICU数据、支持语言和选项也可能不同。

<a id="k02"></a>

### 2. 数字与日期

Intl.NumberFormat(locales?,options?)支持普通数值、currency、percent、unit等样式，format返回显示文本，formatToParts返回结构化片段；支持时formatRange/formatRangeToParts处理范围。应明确currency、最小/最大小数位及舍入需求。

Intl.DateTimeFormat支持dateStyle/timeStyle或具体字段，以及timeZone；它格式化时间点，不会为日期运算自动补好业务规则。

```js
const money = new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' });
console.log(money.format(12.5)); // $12.50
const date = new Intl.DateTimeFormat('en-GB', {
  timeZone: 'UTC', year: 'numeric', month: '2-digit', day: '2-digit'
});
console.log(date.format(new Date('2026-01-02T00:00:00Z'))); // 02/01/2026
```

展示文本可能含不可见分隔或不换行空格，不要用简单去逗号再Number作为通用反向解析。高频渲染可复用格式器，但需在locale/时区配置变化时更新。

<a id="k03"></a>

### 3. 排序与字素

Intl.Collator(locales?,options?)提供compare，可配置usage、sensitivity、numeric等；比较结果只保证负/零/正，不固定为-1/1。数字样式文件名可用numeric:true自然排序。

Intl.Segmenter(locale,{granularity})按grapheme、word或sentence分段，segment返回可迭代结果，能处理多个码点组成的字素，但结果仍依语言规则与Unicode版本。

```js
const compare = new Intl.Collator('en', { numeric: true }).compare;
console.log(['file10', 'file2'].sort(compare)); // ['file2','file10']
const segmenter = new Intl.Segmenter('en', { granularity: 'grapheme' });
console.log([...segmenter.segment('e\u0301')].length); // 1
```

String.length为码元数，字符串展开为码点序列，Segmenter可取得字素边界。三者各有用途，不能把可见字符统计替换掉网络字节长度检查。

<a id="k04"></a>

### 4. 复数、相对时间、列表与名称

| API | 主要用途 |
| --- | --- |
| Intl.PluralRules | select/selectRange按语言和数量得到one/few/other等类别，再选择文案 |
| Intl.RelativeTimeFormat | format(value,unit)，如“昨天”“2天后”；numeric选项影响表达 |
| Intl.ListFormat | format/formatToParts，把列表按语言连接 |
| Intl.DisplayNames | of(code)取得语言、地区、货币等代码的本地化名称 |
| Intl.Locale | 表达语言地区及扩展偏好 |
| Intl.DurationFormat | 较新持续时间格式化；需确认支持与字段契约 |

PluralRules不自动翻译整句，RelativeTimeFormat也不自动计算两个时间点差几天。业务要先给出正确的数值和单位，再选择格式。翻译、数据计算和展示各司其职。

<a id="summary"></a>

## 三、知识小结

Intl记住“给出locale和明确选项，复用格式器，按结构使用结果”。显示文本不等于机器协议，语言不等于时区，格式化不等于业务计算。

参考：[MDN Intl](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Intl)。较新 API 按目标运行时核对支持，示例各自独立。

<a id="interview"></a>

## 四、面试题与答案

<a id="d18-01"></a>

### D18-01 [P1·工程取舍] 为什么不手动拼货币符号和日期？

**回答：** 不同地区的分组、小数、符号位置、日历和文字顺序不同。Intl在明确locale和选项下表达这些规则，手工拼接容易漏边界；金额计算精度和时间点选择仍需业务自己保证。

对应讲解：[数字与日期](#k02)。

<a id="d18-02"></a>

### D18-02 [P1·原理] locale能决定用户时区吗？

**回答：** 不能。语言/地区偏好与当前或业务时区是独立维度。同样说中文的用户可能处于不同地区，格式化时间应明确timeZone或按产品设定，而不是仅凭locale推断。

对应讲解：[语言、地区与选项](#k01)。

<a id="d18-03"></a>

### D18-03 [P1·原理] 怎样准确统计用户看到的字符数量？

**回答：** 先明确需要字素而非码元或码点，再用支持的Intl.Segmenter grapheme粒度分段。组合重音和emoji序列会让length、展开长度与字素数不同，环境Unicode数据也可能影响边界。

对应讲解：[排序与字素](#k03)。

<a id="d18-04"></a>

### D18-04 [P1·基础] PluralRules会自动返回完整翻译吗？

**回答：** 不会，它返回语言复数类别，应用仍要根据类别选翻译模板。相对时间等API也需要应用提供正确差值与单位，不能替代业务计算和文案管理。

对应讲解：[复数、相对时间、列表与名称](#k04)。
