# 12 TypeScript 核心记忆与工程配置

本章把前面知识接到真实工程：检查配置、模块边界、函数契约、声明文件和运行时验证。小结用于回忆关系，不能替代逐章例子与题目。

## 一、本章目录

- [tsconfig 与检查流水线](#k01)
- [函数类型、重载与替代关系](#k02)
- [模块、类型导入与声明文件](#k03)
- [项目迁移、验证与复述路线](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. tsconfig 与检查流水线

```json
{
  "compilerOptions": {
    "target": "ES2022",
    "module": "ESNext",
    "moduleResolution": "Bundler",
    "strict": true,
    "noEmit": true,
    "noUncheckedIndexedAccess": true,
    "exactOptionalPropertyTypes": true,
    "isolatedModules": true
  },
  "include": ["src"]
}
```

这是面向现代前端构建器的配置示例，不直接套给所有Node应用。target控制语法输出目标，lib描述可用API的类型视图，module/moduleResolution需匹配运行和打包机制；加DOM lib不会给Node真正安装浏览器DOM。

strict、无隐式any、空值检查、索引读取和可选属性各自解决不同问题。skipLibCheck跳过声明文件检查不等于项目类型全对；CI应在一致工具链下执行安装、typecheck、test、build。

<a id="k02"></a>

### 2. 函数类型、重载与替代关系

```ts
function normalize(value: string): string;
function normalize(value: number): number;
function normalize(value: string | number): string | number {
  return typeof value === 'string' ? value.trim() : value;
}
const text: string = normalize(' A ');
const number: number = normalize(1);
console.log(text, number);
```

重载签名表达不同调用契约，实现签名要兼容各重载，但调用方不能直接使用未公开的实现签名形状。能用一个联合或泛型清楚表达时，不必滥用重载。

回调参数可替换性要看实现是否接受调用方可能传入的全部值；strictFunctionTypes下普通函数类型检查更严格，方法签名存在兼容例外。返回类型可更具体，也不代表参数可随意更窄。

<a id="k03"></a>

### 3. 模块、类型导入与声明文件

import type/export type表达只在类型侧使用的关系，帮助工具正确消除导入并降低运行时循环依赖。verbatimModuleSyntax等配置会使导入书写与输出关系更明确；按工具链版本采用。

.d.ts声明运行时模块的类型形状，本身不提供实现。declare function或declare module不能让不存在的包和函数自动可用；发布库要保证JS导出、类型导出、包exports和运行环境一致。

class同时存在实例类型与运行时构造器，interface/type主要在类型空间。普通enum可产生运行时代码，const enum涉及内联和编译工具兼容；字符串字面量联合配as const对象常是更直接的前端状态表达。不能仅为“像Java”就全面使用enum。

<a id="k04"></a>

### 4. 项目迁移、验证与复述路线

迁移现有JS项目可先明确边界与严格程度，逐步类型化接口、核心数据和公共组件，再处理长尾；不要一次给所有未知数据加any后宣布完成。临时断言应有明确原因、位置与消除计划。

类型测试验证“哪些写法应该通过/报错”，运行测试验证真实数据与副作用，组件/E2E测试验证交互。@ts-expect-error适合证明预期诊断，错误消失时会提醒；不能把它当无注释的长期屏蔽器。

复述路线：类型分类→推断/拓宽→收窄→泛型关系→keyof/索引访问→条件/映射→状态建模→运行时边界→工程检查。重点是每一步解决什么问题，而不是背出工具类型列表。

<a id="summary"></a>

## 三、知识小结

工程中的TypeScript要与真实运行、构建器、模块和接口协议一致。能写复杂类型不等于系统安全；类型检查、运行测试、数据校验与授权分别提供证据。

参考：[TypeScript Handbook](https://www.typescriptlang.org/docs/handbook/intro.html)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="ts12-01"></a>

### TS12-01 [P0·工程取舍] target、lib、moduleResolution分别负责什么？

**回答：** target影响输出语法目标，lib提供API类型视图，moduleResolution决定编译器如何解析导入并应匹配运行/构建方式。类型里有某API不代表运行时已有polyfill或宿主能力。

对应讲解：[tsconfig 与检查流水线](#k01)。

<a id="ts12-02"></a>

### TS12-02 [P1·原理] 重载实现签名为什么不能随便调用？

**回答：** 对外公开的是重载列表，实现签名用于容纳并实现这些情况，不自动增加调用契约。若想支持一个联合参数调用，应明确增加对应重载或采用更合适的泛型/联合签名。

对应讲解：[函数类型、重载与替代关系](#k02)。

<a id="ts12-03"></a>

### TS12-03 [P1·原理] 声明文件与import type会产生运行时代码吗？

**回答：** 声明文件描述形状而不实现功能，type-only导入通常被消除。必须保证真实JS模块与声明对应，不能靠declare让不存在的能力运行。enum/class等有运行时成分又需分别理解。

对应讲解：[模块、类型导入与声明文件](#k03)。

<a id="ts12-04"></a>

### TS12-04 [P0·工程取舍] TypeScript项目应怎样验收？

**回答：** 检查关键边界和公共契约是否准确，strict检查是否执行，预期错误是否被测试，未知数据是否经过运行校验，再验证组件和业务行为。不能用any覆盖率或能构建作为全部证据。

对应讲解：[项目迁移、验证与复述路线](#k04)。
