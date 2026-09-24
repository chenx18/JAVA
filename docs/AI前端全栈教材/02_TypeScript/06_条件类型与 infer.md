# 06 条件类型与 infer

条件类型按类型关系选择结果，infer在匹配结构中命名待提取的类型。它们应服务明确的API关系，而不是为了面试写难以维护的类型谜题。

## 一、本章目录

- [条件类型的基本判断](#k01)
- [分布式条件与抑制分布](#k02)
- [infer 提取结构](#k03)
- [递归类型、重载与性能边界](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 条件类型的基本判断

```ts
type Label<T> = T extends string ? 'text' : 'other';
const a: Label<string> = 'text';
const b: Label<number> = 'other';

type WithId<T> = T extends { id: string } ? T['id'] : never;
const id: WithId<{ id: string; name: string }> = 'u1';
```

extends在这里检查可赋值关系，不执行运行时instanceof，也不根据某个实际变量值分支。条件成立的分支可利用已知约束，失败分支可以返回never等类型。约束泛型入参与条件类型选择结果，是相关但不同的用途。

<a id="k02"></a>

### 2. 分布式条件与抑制分布

```ts
type Distribute<T> = T extends unknown ? T[] : never;
type Together<T> = [T] extends [unknown] ? T[] : never;
const a: Distribute<string | number> = ['x'];
const b: Together<string | number> = ['x', 1];
// @ts-expect-error 分布后是string[] | number[]，不是混合元素数组。
const c: Distribute<string | number> = ['x', 1];
```

左侧为裸类型参数T时，传入联合会逐成员计算再合并：`F<A|B>`得到`F<A>`|`F<B>`。用[T]包装后是在整体上判断，通常可抑制这种分布。

never作为空联合参与分布时，结果常仍是never；[T] extends [never]才可进行整体never检测。any会让条件运算出现特殊宽化或分支组合，应限制它进入复杂类型运算。

<a id="k03"></a>

### 3. infer 提取结构

```ts
type Return<F> = F extends (...args: any[]) => infer R ? R : never;
type Item<T> = T extends readonly (infer U)[] ? U : never;
type Inside<T> = T extends Promise<infer U> ? U : T;

const returned: Return<() => number> = 1;
const item: Item<readonly string[]> = 'x';
const inside: Inside<Promise<string>> = 'ok';
```

infer声明只能用在相应条件匹配位置，名字在成立分支可用；它不是调用函数获得结果。提取异步结果时通常优先标准Awaited，它处理递归Promise-like规则，简单一层`Promise<infer U>`示例不等于完整Awaited。

<a id="k04"></a>

### 4. 递归类型、重载与性能边界

递归条件类型可逐层处理Promise、元组或路径，但需要终止分支、支持范围和复杂度预算。过宽联合与模板字符串组合会造成类型爆炸，编译器可能报告实例化过深。

对重载函数用ReturnType或infer提取返回类型时，通常基于最后的签名，而不是为每个可能调用实际执行一次重载解析。类型运算不是完整定理证明，保持公共类型易读、必要时拆中间别名更有维护价值。

优先使用标准工具类型；只有业务关系确实无法直接表达时，再编写局部、可测试的高级类型。

<a id="summary"></a>

## 三、知识小结

条件类型问可赋值关系，分布看左侧是否为裸类型参数，infer从匹配结构中取类型。遇到never/any、重载和递归时，必须按规则而非直觉推导。

参考：[TypeScript 类型操作](https://www.typescriptlang.org/docs/handbook/2/types-from-types.html)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="ts06-01"></a>

### TS06-01 [P1·原理] infer是什么，和泛型参数有何不同？

**回答：** 泛型参数通常由调用或类型应用提供，infer在条件类型匹配结构时引入待推导类型变量，例如函数返回值或数组元素。它只在对应成立分支使用，不进行运行时操作。

对应讲解：[infer 提取结构](#k03)。

<a id="ts06-02"></a>

### TS06-02 [P1·原理] 为什么T extends unknown会把联合拆开？

**回答：** 条件左侧是裸类型参数，满足分布式条件规则，编译器逐个联合成员计算并合并。用[T]等包装改为整体判断可抑制分布，结果形状可能不同。

对应讲解：[分布式条件与抑制分布](#k02)。

<a id="ts06-03"></a>

### TS06-03 [P1·编码] 怎样判断一个类型整体是不是never？

**回答：** 可用[T] extends [never] ? true : false，包装避免T=never作为空联合被分布后直接得到never。直接T extends never容易得到与预期布尔结果不同的类型。

对应讲解：[分布式条件与抑制分布](#k02)。

<a id="ts06-04"></a>

### TS06-04 [P1·工程取舍] 高级类型越复杂越好吗？

**回答：** 不是。递归、分布和模板联合可能拖慢编译并产生难懂错误。先使用清晰结构和标准工具，复杂转换需限定输入、终止条件及类型测试，不能让公共API变成谜题。

对应讲解：[递归类型、重载与性能边界](#k04)。

<a id="ts06-05"></a>

### TS06-05 [P1·基础] 条件类型里的extends与运行时instanceof一样吗？

**回答：** 不一样，它在类型空间检查可赋值关系并选择分支，不创建对象或执行运行时分支。泛型约束限制可传入类型，条件类型则可按关系产生不同结果，二者用途不同。

对应讲解：[条件类型的基本判断](#k01)。
