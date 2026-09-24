# 08 unknown、any、never 与类型收窄

面对不确定数据，类型系统需要描述未知、放弃检查和不可能状态。unknown、any、never用途不同，收窄则让代码通过实际条件逐步获得可用信息。

## 一、本章目录

- [any、unknown、never 与 void](#k01)
- [内建守卫与控制流分析](#k02)
- [自定义类型谓词与断言函数](#k03)
- [穷尽检查与断言滥用](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. any、unknown、never 与 void

| 类型 | 主要含义 | 使用方式 |
| --- | --- | --- |
| any | 放宽或跳过许多检查 | 可能传播不安全操作，应限制边界 |
| unknown | 当前未证明具体类型 | 使用前收窄或校验 |
| never | 不可能有正常值 | 穷尽检查、不可达分支、永不正常返回 |
| void | 返回值不被调用契约使用 | 不等于运行时强制返回undefined |

```ts
function print(value: unknown) {
  if (typeof value === 'string') console.log(value.toUpperCase());
}
const callback: () => void = () => 123;
callback(); // 类型契约不使用返回值，运行时函数仍可能返回123。
```

unknown不是“另一种随意操作的any”。never也不是null/undefined，后两者有实际运行时值。任何自称never返回的正常路径都应被检查。

<a id="k02"></a>

### 2. 内建守卫与控制流分析

typeof区分原始类别，instanceof观察实例关系，in判断属性存在，等值比较和判别字段也能帮助收窄。注意typeof null为object，真值检查可能把合法0或空串排除。

```ts
function length(value: string | string[] | null) {
  if (value === null) return 0;
  if (Array.isArray(value)) return value.length;
  return value.length;
}
console.log(length(null), length('ab')); // 0 2
```

收窄来自控制流和赋值信息，重新赋值可能改变后续已知类型。回调在未来执行时，外部变量可能变化，编译器不一定沿用此刻的收窄；必要时保存稳定局部值或重新验证。

<a id="k03"></a>

### 3. 自定义类型谓词与断言函数

```ts
type User = { id: string; name: string };
function isUser(value: unknown): value is User {
  return value !== null && typeof value === 'object' &&
    'id' in value && typeof value.id === 'string' &&
    'name' in value && typeof value.name === 'string';
}
function assertUser(value: unknown): asserts value is User {
  if (!isUser(value)) throw new TypeError('invalid user');
}
const incoming: unknown = { id: '1', name: 'A' };
assertUser(incoming);
console.log(incoming.name);
```

value is User把布尔判断和类型收窄关联，asserts在正常返回后建立断言。编译器不会完整证明谓词实现必然正确，写成return true也可能编译，因此它们是需要测试的信任边界。结构复杂时使用可信schema工具减少手工遗漏。

<a id="k04"></a>

### 4. 穷尽检查与断言滥用

```ts
type State = { kind: 'idle' } | { kind: 'success'; value: string };
function assertNever(value: never): never {
  throw new Error('unexpected state: ' + JSON.stringify(value));
}
function render(state: State): string {
  switch (state.kind) {
    case 'idle': return 'waiting';
    case 'success': return state.value;
    default: return assertNever(state);
  }
}
```

新增联合成员后，未处理的default会不再是never，从而提示遗漏。外部JSON仍可能违反类型，因此保留运行时抛错有价值。

as any、双重断言as unknown as T、非空断言!都可能跳过本应证明的事实。!不会自动检查null，as不会转换类型；能通过守卫、明确数据生命周期或修正接口契约解决时，不应靠断言压掉错误。

<a id="summary"></a>

## 三、知识小结

unknown要求证明，any放弃部分证明，never表达不可能，void表达不用结果。类型守卫必须与真实检查一致，穷尽检查要和运行时外部边界配合。

参考：[TypeScript 类型操作](https://www.typescriptlang.org/docs/handbook/2/types-from-types.html)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="ts08-01"></a>

### TS08-01 [P0·基础] unknown与any有什么本质差别？

**回答：** unknown保留未知这一事实，操作前必须收窄；any放宽很多检查并可能把不安全性传播到下游。处理网络、JSON和用户输入通常从unknown开始，再经过验证。

对应讲解：[any、unknown、never 与 void](#k01)。

<a id="ts08-02"></a>

### TS08-02 [P0·原理] never和void有什么区别？

**回答：** never表示不存在正常可用的值，适合永不返回或不可能分支；void通常表示调用方不使用返回值。一个赋给()=>void的回调运行时仍可能返回值，不能据此认为它必然返回undefined。

对应讲解：[any、unknown、never 与 void](#k01)。

<a id="ts08-03"></a>

### TS08-03 [P1·原理] 自定义value is User就一定验证正确吗？

**回答：** 不一定，谓词声明是对编译器的承诺，具体布尔逻辑可能遗漏字段或直接撒谎。需要测试合法、缺字段、错误类型和边界值，复杂数据用schema验证更可维护。

对应讲解：[自定义类型谓词与断言函数](#k03)。

<a id="ts08-04"></a>

### TS08-04 [P0·编码] 如何让新增联合状态触发遗漏检查？

**回答：** 通过判别字段switch穷尽成员，在default把剩余值传给接收never的assertNever。增加成员后未处理分支将报类型错误；运行时仍需保留异常与外部schema验证。

对应讲解：[穷尽检查与断言滥用](#k04)。

<a id="ts08-05"></a>

### TS08-05 [P0·原理] 真值判断收窄为什么可能误伤合法数据？

**回答：** if(value)会排除0、空串等假值，业务可能允许这些值。应按null/undefined、类型或判别字段表达真正条件，收窄成功不等于业务判断就正确。

对应讲解：[内建守卫与控制流分析](#k02)。
