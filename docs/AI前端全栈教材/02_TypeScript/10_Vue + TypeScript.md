# 第十章 Vue + TypeScript

## 一、本章具体知识点

- defineProps
- defineEmits
- defineModel
- defineExpose
- ref 类型推断
- reactive 类型推断
- computed 类型推断
- template 类型检查
- vue-tsc
- generic components

## 二、各知识点详细解释

### defineProps

```ts
const props = defineProps<{
  userId: string
  disabled?: boolean
}>()
```

### defineEmits

```ts
const emit = defineEmits<{
  save: [id: string]
}>()
```

### defineModel

```ts
const model = defineModel<string>()
```

核心思想：把组件契约直接表达成类型。

Vue 官方文档也强调，Composition API 与 TypeScript 结合时可以依赖类型推断来获得模板和组合式 API 的检查能力。([vuejs.org](https://vuejs.org/guide/typescript/composition-api))

## 三、本章面试题与答案

### 题：为什么 Vue 项目里要用 TypeScript，而不仅仅是 JavaScript？

**答案：**

因为大型 Vue 项目中的 props、emits、API 返回值、Store 和组件间契约会快速变复杂。TypeScript 能把这些关系显式化，并在 refactoring、编辑器提示和编译期发现错误。Vue 还可以通过 `vue-tsc` 对 SFC 做模板相关的类型检查。

---
