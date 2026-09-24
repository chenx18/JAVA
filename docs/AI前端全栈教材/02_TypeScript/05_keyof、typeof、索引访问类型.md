# 05 keyof、typeof 与索引访问类型

这组类型操作让契约从已有结构中推导，减少多份手工声明漂移。关键是区分值空间和类型空间，并理解取键不代表运行时枚举。

## 一、本章目录

- [keyof 取得键的类型集合](#k01)
- [类型位置的 typeof 与值](#k02)
- [T[K]、数组元素与键约束](#k03)
- [as const 与 satisfies](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. keyof 取得键的类型集合

```ts
interface User { id: string; age: number }
type UserKey = keyof User;
const key: UserKey = 'age';
// @ts-expect-error 不在User的键类型中。
const wrong: UserKey = 'email';
```

keyof处理类型，不返回运行时数组。字符串索引签名的keyof可能包含string|number，因为JS对象数字键可转成字符串；number索引、Symbol键和具体字面量结构又有各自结果。

联合类型A|B的keyof关注可以安全用于该联合的键，通常是共同键，不是把每个成员所有键直接拼起来。需要逐成员取键时可使用分布式条件类型，必须说明目的。

<a id="k02"></a>

### 2. 类型位置的 typeof 与值

```ts
const config = {
  mode: 'production',
  retry: 3
} as const;
type Config = typeof config;
type Mode = Config['mode'];
const selected: Mode = 'production';
```

运行时typeof产生'number'等字符串；类型位置typeof引用已知值的静态类型。它不会发请求、运行任意表达式再推断业务数据。使用typeof现有配置可避免复制声明，但也可能把as const的过窄字面量契约传播到不希望固定的地方。

typeof SomeClass通常指类的静态构造器侧；SomeClass作为类型名通常指实例侧。它们不是同一结构，泛型工厂和依赖注入中要分清。

<a id="k03"></a>

### 3. T[K]、数组元素与键约束

```ts
interface User { id: string; age: number }
type Age = User['age'];
type Values = User[keyof User];
type Element = readonly User[] extends readonly (infer T)[] ? T : never;

function get<T, K extends keyof T>(object: T, key: K): T[K] {
  return object[key];
}
const age = get({ id: '1', age: 20 }, 'age');
console.log(age);
```

也可直接用User[] [number]的类型索引形式取得元素类型，常写type Item = (typeof items)[number]。索引访问只提取已声明结构的值类型，不执行属性读取。

Object.keys返回运行时字符串数组，不能普遍断言为(keyof T)[]：来源对象可能有静态类型未列出的额外字段，Symbol键又不会出现在Object.keys中。需要安全遍历时按业务验证或缩小到受控对象，不把断言包装成普遍真理。

<a id="k04"></a>

### 4. as const 与 satisfies

```ts
type Route = { path: string; requiresAuth: boolean };
const routes = {
  home: { path: '/', requiresAuth: false },
  account: { path: '/account', requiresAuth: true }
} satisfies Record<string, Route>;
type RouteName = keyof typeof routes;
const name: RouteName = 'account';
console.log(routes[name].path);
```

satisfies检查表达式是否满足目标类型，同时避免简单类型注解把整个结果都压成目标的宽泛视图；它仍可能参与上下文类型推断，不能说“完全不影响推断”。as断言改变检查器视角，不检查实际值；satisfies进行兼容检查，但同样不是运行时校验。

as const适合固定字面配置，satisfies适合验证配置契约，二者可组合；先明确需保留哪些字面量、哪些字段应可变。

<a id="summary"></a>

## 三、知识小结

keyof取键类型，typeof把值的静态类型带入类型空间，T[K]取得属性值类型。satisfies验证静态契约，as const控制字面量与只读推断，运行时仍需真实数据验证。

参考：[TypeScript 类型操作](https://www.typescriptlang.org/docs/handbook/2/types-from-types.html)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="ts05-01"></a>

### TS05-01 [P0·基础] keyof T和Object.keys(object)是一回事吗？

**回答：** 不是，前者是类型运算，后者运行时枚举自有可枚举字符串键。静态类型可能没列出所有实际字段，也可能包含Symbol，因此不能普遍把Object.keys断言为完整精确的keyof数组。

对应讲解：[keyof 取得键的类型集合](#k01)。

<a id="ts05-02"></a>

### TS05-02 [P0·原理] 类型位置typeof与运行时typeof有何不同？

**回答：** 运行时typeof给字符串分类，类型位置typeof提取某个值已知的静态类型。它不会验证网络返回，也不会把运行时任意表达式变成可信类型。

对应讲解：[类型位置的 typeof 与值](#k02)。

<a id="ts05-03"></a>

### TS05-03 [P1·原理] keyof(A|B)为何通常不是所有键的并集？

**回答：** 联合值可能属于任一成员，键必须能在未收窄时安全使用，因此通常取共同可用键。要逐成员取得全部键可设计分布式条件类型，但结果不能直接用于任一联合值而不收窄。

对应讲解：[keyof 取得键的类型集合](#k01)。

<a id="ts05-04"></a>

### TS05-04 [P1·工程取舍] satisfies比as好在哪里？

**回答：** satisfies会检查兼容关系，并保留表达式较具体的类型信息；as主要改变检查器视角，可能掩盖错误。二者都不进行运行时数据校验，是否采用取决于静态配置还是外部输入边界。

对应讲解：[as const 与 satisfies](#k04)。

<a id="ts05-05"></a>

### TS05-05 [P0·原理] T[K]怎样把属性名与返回类型关联起来？

**回答：** K约束为keyof T后，T[K]表示对应属性值类型，传不同合法键可得到不同结果类型。它是静态提取，不执行读取，也不能证明外部对象运行时符合T。

对应讲解：[T[K]、数组元素与键约束](#k03)。
