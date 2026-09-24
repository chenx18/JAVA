# 07 映射类型、模板字面量与工具类型

映射类型遍历键并变换属性，模板字面量类型组合字符串约束，工具类型将常用变换封装成可复用表达式。要明确它们只改变静态视图，不自动处理运行时对象。

## 一、本章目录

- [映射属性与修饰符](#k01)
- [键重映射与模板字面量](#k02)
- [对象与集合工具类型](#k03)
- [函数、构造器与推断工具](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 映射属性与修饰符

```ts
type Optional<T> = { [K in keyof T]?: T[K] };
type Mutable<T> = { -readonly [K in keyof T]: T[K] };
type Present<T> = { [K in keyof T]-?: T[K] };

interface User { readonly id: string; name?: string }
const patch: Optional<User> = { name: 'Alice' };
const mutable: Mutable<User> = { id: '1' };
mutable.id = '2';
```

[K in keyof T]逐个生成属性。?、readonly增加修饰，-?、-readonly去掉修饰。变化通常是浅层的，`Partial<对象>`不会递归让每个嵌套属性可选。

Required去除可选标记，不等于对数据做必填校验；Readonly也不调用Object.freeze。需要运行时变换必须写相应逻辑。

<a id="k02"></a>

### 2. 键重映射与模板字面量

```ts
type Getters<T> = {
  [K in keyof T as K extends string ? `get${Capitalize<K>}` : never]: () => T[K]
};
type Events = `${'user' | 'order'}:${'created' | 'deleted'}`;
const event: Events = 'user:created';
```

as子句可重命名键，映射为never可过滤键。模板类型把联合成员组合成字符串集合，适合受控事件名或配置键；如果输入联合很大，组合数量可能迅速增长。

Capitalize、Uncapitalize、Uppercase、Lowercase是内建字符串类型变换；它们不是按用户locale进行业务文本国际化，也不会把运行时字符串改写。

<a id="k03"></a>

### 3. 对象与集合工具类型

| 工具 | 作用 | 常见边界 |
| --- | --- | --- |
| `Partial<T>` / `Required<T>` | 属性变可选/去可选 | 浅层类型变化 |
| `Readonly<T>` | 只读视图 | 不冻结运行时对象 |
| `Pick<T,K>` / `Omit<T,K>` | 选键/排除键 | 运行时字段不会自动删掉 |
| `Record<K,V>` | 键集合到值类型的映射 | string键不保证实际所有键存在 |
| `Exclude<T,U>` / `Extract<T,U>` | 按可赋值关系排除/保留联合成员 | 不是任意对象属性的删除 |
| `NonNullable<T>` | 去null/undefined | 不校验实际输入 |

```ts
interface User { id: string; name: string; password: string }
type PublicUser = Omit<User, 'password'>;
function publicUser(user: User): PublicUser {
  const { password: _ignored, ...safe } = user;
  return safe;
}
console.log(publicUser({ id: '1', name: 'A', password: 'hidden' }));
```

仅把user断言为PublicUser不会删除password，序列化时仍可能泄露。类型级字段隐藏必须与真实数据转换一致。

<a id="k04"></a>

### 4. 函数、构造器与推断工具

| 工具 | 用途 |
| --- | --- |
| `Parameters<F>` / `ReturnType<F>` | 参数元组/返回类型 |
| `ConstructorParameters<C>` / `InstanceType<C>` | 构造参数/实例类型 |
| `Awaited<T>` | 按等待语义递归拆解结果类型 |
| `ThisParameterType<F>` / `OmitThisParameter<F>` | 提取/移除显式this参数类型 |
| `ThisType<T>` | 对象字面量中提供上下文this标记，不创建运行时值 |
| `NoInfer<T>` | 阻止某位置作为特定推断来源，较新TS能力 |

```ts
async function load(id: string) { return { id, name: 'A' }; }
type Args = Parameters<typeof load>;
type Loaded = Awaited<ReturnType<typeof load>>;
const args: Args = ['u1'];
const loaded: Loaded = { id: 'u1', name: 'A' };
```

重载签名的提取结果需注意最后签名规则。泛型类型推断工具仍建立在已有声明上；如果声明是any或与实现不符，工具类型不会替你恢复可信信息。

<a id="summary"></a>

## 三、知识小结

映射类型变键与属性，条件工具筛联合，函数工具提取签名，Awaited描述等待结果。记住每一种变换的输入和输出，再核对它是否需要对应运行时逻辑。

参考：[TypeScript 类型操作](https://www.typescriptlang.org/docs/handbook/2/types-from-types.html)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="ts07-01"></a>

### TS07-01 [P0·原理] Partial如何实现，是否递归？

**回答：** 通过[K in keyof T]?:T[K]遍历自有声明键并添加可选修饰，通常只改变当前一层。嵌套对象仍遵守原字段要求；深可选需要另定义对数组、函数等的支持规则。

对应讲解：[映射属性与修饰符](#k01)。

<a id="ts07-02"></a>

### TS07-02 [P0·原理] `Omit<User,'password'>`会防止JSON泄露密码吗？

**回答：** 不会，它只改变静态视图。实际对象仍可能有password，JSON序列化照样能输出。必须显式构造响应对象或移除字段，并在服务端做权威数据输出控制。

对应讲解：[对象与集合工具类型](#k03)。

<a id="ts07-03"></a>

### TS07-03 [P1·基础] Exclude和Omit的区别是什么？

**回答：** Exclude按可赋值关系过滤联合成员，Omit按键排除对象属性。两者处理的维度不同，不能用排除某个对象形状替代删除其一个字段。

对应讲解：[对象与集合工具类型](#k03)。

<a id="ts07-04"></a>

### TS07-04 [P1·工程取舍] 模板字面量类型适合哪些场景？

**回答：** 适合受控事件名、配置键或有限路径组合；需控制联合规模，避免类型爆炸。它只约束静态字符串集合，不能自动验证外部任意字符串或执行国际化转换。

对应讲解：[键重映射与模板字面量](#k02)。

<a id="ts07-05"></a>

### TS07-05 [P1·基础] `Awaited<ReturnType<F>>`解决什么？

**回答：** 先提取函数返回类型，再按等待语义得到异步结果类型，减少重复声明。它依赖F签名准确，不会验证真正的HTTP数据，也不能把any变成安全契约。

对应讲解：[函数、构造器与推断工具](#k04)。
