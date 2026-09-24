# 13 Date

Date 保存时间点，读取和展示时才解释成本地或UTC日历字段。把时间点、时区、自然日期和持续时长分开，是避免跨时区错误的前提。

## 一、本章目录

- [时间戳、构造与解析](#k01)
- [读取与修改字段](#k02)
- [展示、序列化与无效日期](#k03)
- [自然日、时长与业务协议](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 时间戳、构造与解析

Date内部保存自Unix纪元起的毫秒数，或无效时间值。它不保存“这个对象属于上海时区”这样的固定时区属性。

| 调用 | 返回与解释 |
| --- | --- |
| new Date() | 当前时间点的Date对象 |
| new Date(milliseconds) | 根据毫秒时间戳构造 |
| new Date(dateString) | 解析文本；优先明确ISO和时区 |
| new Date(year,monthIndex,day=1,...) | 按本地日历构造，月份0起；0—99年份有历史映射 |
| Date.now() | 当前毫秒Number |
| Date.parse(text) | 毫秒Number或NaN |
| Date.UTC(year,monthIndex,...) | 按UTC字段得到毫秒Number，非Date对象 |
| Date()，不带new | 当前时间的字符串，不按构造器参数生成对象 |

标准日期形式'2026-01-01'通常按UTC日期解释，'2026-01-01T00:00:00'无偏移的日期时间通常按本地时间解释；带Z或明确偏移的字符串能表达确定时间点。非标准日期字符串不要依赖不同浏览器碰巧一致的解析。

<a id="k02"></a>

### 2. 读取与修改字段

本地读取族：getFullYear、getMonth、getDate、getDay、getHours、getMinutes、getSeconds、getMilliseconds。对应UTC族在get后加UTC，例如getUTCFullYear、getUTCDate。getTime/valueOf返回时间戳；getTimezoneOffset返回该时间点本地时区相对UTC的分钟差（UTC减本地），可能受夏令时影响。

getDate是月内日期，getDay是星期且周日为0，getMonth从0到11。getYear是历史接口，不适合现代完整年份处理。

setter族包括setFullYear、setMonth、setDate、setHours、setMinutes、setSeconds、setMilliseconds及UTC对应方法，另有setTime。**它们修改原Date，通常返回新时间戳Number**；setYear为历史方法。

```js
const date = new Date('2026-01-31T00:00:00Z');
date.setUTCMonth(1);
console.log(date.toISOString()); // 2026-03-03T00:00:00.000Z
```

月份改为二月后仍尝试保留31日，超出的天数会向后溢出。因此“下个月同日”“月末”“增加30天”必须定义不同算法。

<a id="k03"></a>

### 3. 展示、序列化与无效日期

| 方法 | 返回/用途 |
| --- | --- |
| toISOString() | UTC ISO文本；Invalid Date抛RangeError |
| toUTCString() | UTC展示文本 |
| toString()/toDateString()/toTimeString() | 本地展示，不作跨平台稳定机器协议 |
| toLocaleString/DateString/TimeString(locales?,options?) | 本地化，可指定语言/时区选项 |
| toJSON() | 通常调用ISO表示；无效日期返回null |

```js
const invalid = new Date('not a date');
console.log(Number.isNaN(invalid.getTime())); // true
console.log(JSON.stringify({ date: invalid })); // {"date":null}
console.log(new Date('2026-01-01T08:00:00+08:00').toISOString());
// 2026-01-01T00:00:00.000Z
```

typeof invalid仍是object，instanceof Date也可能为true，所以“是Date对象”不代表包含有效时间。JSON将Date转文本后，再parse不会自动还原Date。

<a id="k04"></a>

### 4. 自然日、时长与业务协议

两个Date相减得到毫秒差，可表达时间点间隔；增加86400000毫秒表达固定24小时，不必然等于某个时区的“明天同一钟点”，因为可能跨夏令时切换。

生日、账期等纯日期应明确以日期字段或日期字符串存储，不随用户时区意外前后移一天。日志/事件通常使用UTC时间点，展示再选用户时区。格式化不会改变原时间点。

前端校验日期不能只看new Date是否产生对象；某些越界字段会规范化溢出。严格日期输入应验证字段范围或构造后回读比对。更复杂的日历运算使用合适的日期库或经兼容性确认的Temporal，不能把它当所有环境已经内建。

<a id="summary"></a>

## 三、知识小结

Date先确认“输入单位和时区”，再区分“时间点、日历字段、展示文本”。getDate/getDay/getMonth、可变setter、自然日与固定24小时是常见面试和业务边界。

参考：[MDN Date](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Date)。较新 API 按目标运行时核对支持，示例各自独立。

<a id="interview"></a>

## 四、面试题与答案

<a id="d13-01"></a>

### D13-01 [P0·基础] getDate、getDay、getMonth分别是什么？

**回答：** 分别是月内日期、星期（周日0）和月份索引（0—11）。读取本地或UTC字段也要选对应方法；getYear不是完整年份API，应使用getFullYear或UTC版本。

对应讲解：[读取与修改字段](#k02)。

<a id="d13-02"></a>

### D13-02 [P1·原理] Date保存时区吗？

**回答：** Date保存时间戳，不保存一个固定业务时区。相同时间点可以按不同地区格式化，local getter根据宿主时区解释，UTC getter按UTC解释。时区属于解释与业务协议，不是随意给字符串加后缀。

对应讲解：[时间戳、构造与解析](#k01)。

<a id="d13-03"></a>

### D13-03 [P1·原理] 一月31日加一个月为何变成三月？

**回答：** setMonth保留其余日历字段，二月不存在31日时按溢出规则规范化，可能进入三月。需要月末或限到目标月最后一天时应明确该规则并实现，不能等同于直接setMonth。

对应讲解：[读取与修改字段](#k02)。

<a id="d13-04"></a>

### D13-04 [P1·基础] Invalid Date怎样判断，JSON会怎样？

**回答：** 对可信Date实例检查getTime是否NaN。它仍是对象；toISOString会抛错，而toJSON对无效值返回null，JSON序列化因此可能得到null。接口应主动处理无效日期而不是等它静默变空。

对应讲解：[展示、序列化与无效日期](#k03)。

<a id="d13-05"></a>

### D13-05 [P1·工程取舍] 明天同一时间等于当前时间加24小时吗？

**回答：** 未必，目标时区若跨夏令时变化，自然日长度可能不是24小时。需要先明确是持续时长还是日历日期，再选择时区明确的运算和验证。

对应讲解：[自然日、时长与业务协议](#k04)。
