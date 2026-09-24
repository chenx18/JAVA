# 03 interface 与 type

interface与type在许多对象契约中都能工作。选择依据是要表达什么关系、是否需要开放扩展，以及团队约定，而不是认为其中一个总能让代码更安全。

## 一、本章目录

- [对象契约与类型别名](#k01)
- [extends、交叉与冲突定位](#k02)
- [声明合并与模块增强](#k03)
- [函数契约与工程选择](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 对象契约与类型别名

```ts
interface User { id: string; name: string }
type UserAlias = { id: string; name: string };
type Status = 'idle' | 'loading';
type Pair = readonly [string, number];
type Result<T> = { ok: true; data: T } | { ok: false; error: string };
```

interface主要声明对象结构，也能描述可调用或可构造签名；type为任意类型表达式命名，可表达联合、交叉、元组、条件与映射类型。类型别名不是运行时变量，不会创建类实例或验证对象。

<a id="k02"></a>

### 2. extends、交叉与冲突定位

```ts
interface Identified { id: string }
interface User extends Identified { name: string }
type UserWithRole = User & { role: 'reader' | 'editor' };
const value: UserWithRole = { id: '1', name: 'A', role: 'reader' };
```

interface extends要求继承关系兼容，冲突往往在声明处报错；交叉类型保留同时满足的约束，可能在冲突属性处形成never并在使用时暴露。继承一个成员结构可静态确定的类型与“任意union都能extends”不同。

不要为了让后一个字段覆盖前一个就写A&B；需要类型级覆盖时可以先Omit旧键再组合，但仍要确认运行时合并实现与类型一致。

<a id="k03"></a>

### 3. 声明合并与模块增强

```ts
interface Settings { theme: string }
interface Settings { locale: string }
const settings: Settings = { theme: 'light', locale: 'zh-CN' };
```

同名interface在允许的声明空间可以合并，type别名不能靠重复声明合并。合并适合为开放库接口补充能力，也可能让项目全局类型被无意扩展。

模块增强要指向真正模块并遵守可增强的导出结构；只改类型不代表运行时自动添加字段/方法。增加声明时需要对应真实实现，避免“编辑器有提示，运行时是undefined”。

<a id="k04"></a>

### 4. 函数契约与工程选择

```ts
interface Formatter {
  (value: string): string;
}
type Loader<T> = (id: string) => Promise<T>;
const format: Formatter = value => value.trim();
console.log(format(' A ')); // A
```

对象公共接口希望可扩展时可用interface；联合状态、组合变换、条件与映射场景通常用type。复杂类型需考虑诊断可读性与编译性能，不用无意义多层别名制造“高级感”。

团队可以统一默认风格，但必须保留表达能力的例外。面试先解释共同点，再说合并和表达范围，而不是背一串绝对优劣。

<a id="summary"></a>

## 三、知识小结

共同点是描述静态契约；interface强调对象结构和开放合并，type命名更广的类型表达式。声明不创造运行时行为，冲突处理和诊断位置也影响选型。

参考：[TypeScript Handbook](https://www.typescriptlang.org/docs/handbook/intro.html)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="ts03-01"></a>

### TS03-01 [P0·基础] interface和type的主要区别是什么？

**回答：** 对象结构场景二者通常都可用；interface支持声明合并与extends，type能表达联合、元组、条件和映射等更广表达式。它们都主要服务静态检查，不会自动生成对象。

对应讲解：[对象契约与类型别名](#k01)。

<a id="ts03-02"></a>

### TS03-02 [P1·原理] extends和交叉遇到同名冲突时一样吗？

**回答：** 不完全一样。interface继承要求兼容，冲突常在声明处暴露；交叉要求同时满足，冲突属性可能变成never。交叉不是运行时覆盖，不能依靠顺序消除冲突。

对应讲解：[extends、交叉与冲突定位](#k02)。

<a id="ts03-03"></a>

### TS03-03 [P1·工程取舍] 模块增强只写声明就能增加方法吗？

**回答：** 不能。声明只是让检查器相信方法存在，运行时仍需真实实现。增强还要匹配正确模块和声明作用域，避免全局污染或与第三方版本升级冲突。

对应讲解：[声明合并与模块增强](#k03)。

<a id="ts03-04"></a>

### TS03-04 [P0·工程取舍] 项目是否必须统一只用interface？

**回答：** 可以制定默认约定，但不宜一刀切。公共可扩展对象契约适合interface，联合状态和类型运算需要type等表达。可读性、错误定位和真实契约一致性比名称偏好重要。

对应讲解：[函数契约与工程选择](#k04)。
