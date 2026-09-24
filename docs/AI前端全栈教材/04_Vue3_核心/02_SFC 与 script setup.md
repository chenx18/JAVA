# 02 SFC、script setup 与编译宏

单文件组件把模板、脚本与样式放在同一语义单元。script setup是编译语法，理解编译期与实例运行期边界，才能正确使用宏、props和样式隔离。

## 一、本章目录

- [SFC 如何进入运行时](#k01)
- [props、emits、model 与 expose 宏](#k02)
- [版本差异与异步 setup](#k03)
- [scoped、深度选择器与 CSS Modules](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. SFC 如何进入运行时

构建工具通过Vue插件解析.vue文件，转换script、template和style。模板通常编译为render函数，样式按scoped/CSS Modules规则处理；浏览器不会原生认识.vue文件。

script setup顶层声明可供模板使用，主体逻辑为组件实例setup的一部分，通常每个实例执行；普通script模块顶层则在模块加载实例内执行，适合明确共享配置但不适合无意共享用户状态。

```vue
<script setup lang="ts">
import { ref } from 'vue'
const open = ref(false)
</script>
<template>
  <button type="button" @click="open = !open">{{ open ? '关闭' : '打开' }}</button>
</template>
<style scoped>
button { border-radius: .5rem; }
</style>
```

<a id="k02"></a>

### 2. props、emits、model 与 expose 宏

defineProps声明输入，defineEmits声明输出事件，defineModel表达v-model契约，defineExpose明确父级通过模板引用可调用的接口。它们由编译器处理，不是需要从vue导入的普通函数。

```vue
<script setup lang="ts">
const props = withDefaults(defineProps<{ title: string; disabled?: boolean }>(), {
  disabled: false
})
const emit = defineEmits<{ confirm: [] }>()
function confirm() { if (!props.disabled) emit('confirm') }
defineExpose({ confirm })
</script>
<template>
  <button type="button" :disabled="props.disabled" @click="confirm">{{ props.title }}</button>
</template>
```
宏参数的提升和静态分析带来限制，不能任意依赖稍后才有的setup局部变量。类型式声明与运行时声明按支持方式选择，类型契约也不提供外部JSON验证。

<a id="k03"></a>

### 3. 版本差异与异步 setup

Vue3.5在同一script setup内对defineProps解构读取做响应式编译转换；旧版直接解构可能丢失后续跟踪。把值传给外部函数时仍应按getter/ref接口保留读取关系，不要把当前值当完整响应式来源。

顶层await会形成async setup，组件挂起与父级异步协调需要相应机制；Suspense在当前Vue3文档中仍标为实验性能力，不能忽略其版本和错误处理边界。

defineOptions、defineSlots和泛型组件等扩展帮助表达组件契约，但需确认Vue编译器与类型工具支持。普通setup函数仍可使用，不要求所有组件都改成同一写法。

<a id="k04"></a>

### 4. scoped、深度选择器与 CSS Modules

scoped通常通过编译添加作用域标记约束选择器，不是Shadow DOM隔离。子组件根节点、插槽内容及深度选择都有相应规则；:deep()用于穿透到子树内部，:slotted()处理插槽选择，:global()显式全局。

CSS Modules通过类名映射降低命名冲突，可与组件逻辑配合。深度选择器使用过多会耦合子组件内部结构，升级容易破坏；公共样式应优先通过组件属性、CSS变量或约定接口暴露。

SFC能编译不代表模板类型和运行交互都正确，应配合vue-tsc、组件测试和实际页面验证。

<a id="summary"></a>

## 三、知识小结

SFC是构建输入，script setup是编译语法，宏表达组件契约，scoped是选择器转换。实例状态、模块共享、响应式解构和异步setup需明确边界。

参考：[Vue script setup](https://vuejs.org/api/sfc-script-setup.html)；[Vue SFC CSS](https://vuejs.org/api/sfc-css-features.html)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="vue02-01"></a>

### VUE02-01 [P0·原理] script setup与普通setup有什么区别？

**回答：** script setup是编译器提供的简洁SFC语法，顶层绑定直接用于模板并支持宏；普通setup是组件的运行时选项函数。二者都参与组件实例初始化，不应把script setup当只执行一次的模块脚本。

对应讲解：[SFC 如何进入运行时](#k01)。

<a id="vue02-02"></a>

### VUE02-02 [P0·基础] defineExpose为什么有用？

**回答：** 它明确父级通过组件引用能访问的命令式接口，避免把整个内部状态都变成公共依赖。优先props/events表达数据关系，确需focus等命令时再暴露小接口。

对应讲解：[props、emits、model 与 expose 宏](#k02)。

<a id="vue02-03"></a>

### VUE02-03 [P1·原理] scoped是不是完全隔离CSS？

**回答：** 不是Shadow DOM，它通常通过作用域标记改写选择器，子根、插槽和deep/global都有规则。过度穿透会依赖子组件内部结构，应优先稳定样式接口。

对应讲解：[scoped、深度选择器与 CSS Modules](#k04)。

<a id="vue02-04"></a>

### VUE02-04 [P1·工程取舍] 顶层await与props解构有什么版本注意？

**回答：** 顶层await形成异步setup并需要相应异步协调；3.5的响应式props解构是编译转换，旧版和传值到外部函数场景不能简单套用。编译器、运行时和类型工具应配套。

对应讲解：[版本差异与异步 setup](#k03)。
