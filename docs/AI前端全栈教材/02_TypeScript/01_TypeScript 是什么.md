# 01 TypeScript 的定位、推断与检查边界

TypeScript在JavaScript值和执行规则之上增加静态类型分析。学会它首先要分清：编译器知道什么、运行时真实发生什么、外部数据怎样建立可信边界。本系列基础例子以strict模式为前提。

## 一、本章目录

- [类型检查、转换与运行时](#k01)
- [类型注解、推断与字面量拓宽](#k02)
- [结构类型与额外属性检查](#k03)
- [编译选项与外部边界](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 类型检查、转换与运行时

普通类型注解、interface和类型别名在输出JavaScript时会被擦除，不会自动验证接口数据或改变JS的闭包、this和事件循环。构建器能转译TS，不代表执行了类型检查；Vite等项目通常还需要单独运行tsc/vue-tsc。

```ts
function greet(name: string): string {
  return name.toUpperCase();
}
console.log(greet('Alice')); // ALICE
// @ts-expect-error 此行用来验证编译器能发现错误，不运行。
if (false) greet(123);
```

class是JavaScript运行时能力，普通enum和部分namespace写法也可能生成运行时代码，因此“TS一切都只是擦除”同样不准确。选定编译工具和目标环境后，确认采用的语法能否由工具链处理。

<a id="k02"></a>

### 2. 类型注解、推断与字面量拓宽

局部变量优先利用推断，公共函数参数/边界用明确契约。const基础字面量常推断为字面量类型，let可重新赋值，常拓宽为string/number等；const对象的可写属性仍可能拓宽。

```ts
const exact = 1;
let flexible = 1;
const record = { status: 'idle' };
const fixed = { status: 'idle' } as const;

const a: 1 = exact;
const b: number = flexible;
const c: string = record.status;
const d: 'idle' = fixed.status;
console.log(a, b, c, d); // 1 1 idle idle
```

as const抑制字面量拓宽，并给字面量对象/元组添加只读类型；不等于运行时Object.freeze，也不保证所有已引用对象深度不可变。类型断言as T只是告诉检查器采用某类型视角，没有执行转换。

<a id="k03"></a>

### 3. 结构类型与额外属性检查

类型兼容主要依据结构：目标需要哪些字段/调用签名，来源是否满足。它不同于Java通常依赖显式类/interface声明的名义关系，但TS中的private/protected、unique symbol品牌等也会影响兼容性。

```ts
interface User { name: string }
const source = { name: 'Alice', age: 20 };
const user: User = source;

// @ts-expect-error 新鲜对象字面量触发额外属性检查。
const direct: User = { name: 'Alice', age: 20 };
console.log(user.name);
```

额外属性检查不是“对象类型必须字段数完全相同”，它对新鲜字面量有更严格规则，帮助发现拼写问题。对象经变量传递可能不再触发同样检查，仍不代表多余字段被运行时删除。

<a id="k04"></a>

### 4. 编译选项与外部边界

strict开启一组严格检查，具体包括strictNullChecks、noImplicitAny等；noUncheckedIndexedAccess和exactOptionalPropertyTypes是值得单独考虑的选项，不等于strict自动包含的所有可能检查。tsc --noEmit只检查而不输出文件。

从网络取得数据时，response.json返回值的类型定义可能过于宽松；应把信任边界视为unknown，经过结构校验后使用。给fetch加泛型包装、用as User，或写`Promise<User>`返回类型，都不能证明服务器真返回User。

noEmitOnError控制存在诊断时的输出策略，开发服务器热更新也可能绕过独立类型检查。CI必须明确安装、类型检查、测试与构建各自是否执行。

<a id="summary"></a>

## 三、知识小结

复述路径：TS检查开发期关系，JS负责运行；推断保留或拓宽字面量；结构兼容不等于字段完全相同；类型擦除决定外部数据仍需运行时校验。

参考：[TypeScript Handbook](https://www.typescriptlang.org/docs/handbook/intro.html)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="ts01-01"></a>

### TS01-01 [P0·基础] TypeScript解决什么问题，不能解决什么？

**回答：** 它通过静态分析把参数、返回值和数据结构契约显式化，辅助编辑、重构和发现错误。普通类型会擦除，因此不能自动校验网络数据、保证权限或改变JS运行机制；构建转译与类型检查也要分开。

对应讲解：[类型检查、转换与运行时](#k01)。

<a id="ts01-02"></a>

### TS01-02 [P0·原理] const count=1推断为number还是1？

**回答：** 基础const字面量通常保留字面量类型1，let可重新赋值通常拓宽为number；对象可写属性即使对象绑定是const，也可能拓宽。as const影响字面量类型与只读视图，不是运行时冻结。

对应讲解：[类型注解、推断与字面量拓宽](#k02)。

<a id="ts01-03"></a>

### TS01-03 [P0·原理] 为什么多一个字段的变量能赋给interface，直接字面量却报错？

**回答：** 结构兼容允许来源提供额外字段，但新鲜对象字面量有额外属性检查以发现拼写错误。两种场景规则不同，既不能推断interface是精确对象，也不能认为运行时自动删除多余字段。

对应讲解：[结构类型与额外属性检查](#k03)。

<a id="ts01-04"></a>

### TS01-04 [P1·工程取舍] 项目能构建，为什么类型仍然错？

**回答：** 某些构建器只剥离类型并转换语法，没有运行完整类型检查。应在开发和CI中明确执行tsc/vue-tsc，使用一致的tsconfig；编译通过也不等于接口数据经过运行时验证。

对应讲解：[编译选项与外部边界](#k04)。
