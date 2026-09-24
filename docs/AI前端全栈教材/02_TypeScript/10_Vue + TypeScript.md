# 10 Vue 与 TypeScript 的组件契约

Vue中的类型重点是组件、状态、组合式函数和接口之间的契约。示例以Vue 3.5系列为参考，SFC宏必须放在对应编译环境中，不能直接当普通Node函数调用。

## 一、本章目录

- [props、emits、model 与 expose](#k01)
- [ref、reactive 与 computed 类型](#k02)
- [DOM 引用、事件与泛型组合](#k03)
- [SFC 检查与实际边界](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. props、emits、model 与 expose

```vue
<script setup lang="ts">
const props = withDefaults(defineProps<{
  userId: string
  disabled?: boolean
}>(), { disabled: false })

const emit = defineEmits<{ save: [id: string] }>()
const name = defineModel<string>({ required: true })
function submit() { emit('save', props.userId) }
defineExpose({ submit })
</script>

<template>
  <input v-model="name" aria-label="名称">
  <button type="button" :disabled="props.disabled" @click="submit">保存</button>
</template>
```

defineProps/defineEmits/defineModel/defineExpose是编译宏，不必从vue导入。类型定义约束父子调用和编辑器提示，不能替代网络数据校验。defineModel默认值若与父侧初始undefined不一致，可能产生父子状态不同步，应明确required或父侧初始化。

Vue 3.5中同一script setup内的props解构有编译转换支持；传入外部函数时仍需传getter等保留响应式读取，而不是把当前值当永久订阅。旧版本解构规则需区分。

<a id="k02"></a>

### 2. ref、reactive 与 computed 类型

```ts
import { ref, reactive, computed } from 'vue';
type User = { id: string; name: string };
const selected = ref<User | null>(null);
const form = reactive({ name: '', age: 0 });
const valid = computed(() => form.name.trim().length > 0);
selected.value = { id: '1', name: 'A' };
console.log(valid.value, selected.value.name);
```

ref推断当前值但需要包含未来合法状态；初始化null时显式写User|null比as User伪造占位更准确。reactive返回深层解包后的代理类型，不宜随意把复杂泛型返回强制写成原始T，尤其涉及嵌套ref和集合。

computed通常从getter推断结果，写入需要可写computed的setter契约；getter应专注派生值，不负责网络请求或修改源状态。

<a id="k03"></a>

### 3. DOM 引用、事件与泛型组合

```ts
import { ref } from 'vue';
const input = ref<HTMLInputElement | null>(null);
function focusInput() { input.value?.focus(); }
function onInput(event: Event) {
  const target = event.target;
  if (target instanceof HTMLInputElement) console.log(target.value);
}
```

模板引用在挂载前、卸载后或条件移除时可能为空，不能只用!隐藏生命周期。事件target也不保证是某种具体元素，应做运行时检查或在受控位置表达约束。

组合式函数应保留输入输出关系，接受值/ref/getter时明确读取和监听规则。泛型SFC可用于列表和选择器，但类型参数应真实连接items、选中值和事件，不能只为形式增加复杂签名。

<a id="k04"></a>

### 4. SFC 检查与实际边界

tsc检查普通TS项目，vue-tsc还能检查Vue SFC的脚本与模板类型。Vite成功转译不能代替vue-tsc --noEmit；编辑器Vue扩展、Vue编译器和类型检查工具的版本也要配套。

组件测试应覆盖props默认值、事件载荷、v-model同步、异步数据、条件渲染后的DOM引用和卸载清理。类型检查能发现部分契约错误，但不能证明交互体验或接口鉴权正确。

将API DTO映射到组件需要的展示结构，可减少后端字段变更直接传遍组件。类型契约与真实转换应一起维护。

<a id="summary"></a>

## 三、知识小结

Vue+TS围绕props/emits/model、响应式状态、DOM生命周期和组合式函数建立契约。宏依赖SFC编译，类型依赖正确推断，实际交互与接口仍需运行验证。

参考：[Vue TypeScript Composition API](https://vuejs.org/guide/typescript/composition-api.html)；[Vue script setup](https://vuejs.org/api/sfc-script-setup.html)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="ts10-01"></a>

### TS10-01 [P0·基础] defineProps等宏为什么不能直接在普通TS文件调用？

**回答：** 它们由Vue SFC编译器识别并转换，不是普通运行时函数。需要在script setup等支持位置使用，并用配套工具检查；类型注解也不提供接口运行时验证。

对应讲解：[props、emits、model 与 expose](#k01)。

<a id="ts10-02"></a>

### TS10-02 [P0·原理] ref(null)后想保存User应怎样写？

**回答：** 按真实生命周期声明`ref<User|null>`(null)，后续读取先处理空状态。不要用as User把尚不存在的对象伪装成有效数据，模板引用也同样要处理挂载和卸载。

对应讲解：[ref、reactive 与 computed 类型](#k02)。

<a id="ts10-03"></a>

### TS10-03 [P1·原理] Vue 3.5的props解构是否永远保持响应式？

**回答：** 同一script setup中的编译器会转换相关读取，但把解构出的当前值传给外部函数不等于传递响应式来源。需要getter/ref等接口，并区分旧版与新版编译行为。

对应讲解：[props、emits、model 与 expose](#k01)。

<a id="ts10-04"></a>

### TS10-04 [P0·工程取舍] Vite构建成功为何还要vue-tsc？

**回答：** Vite主要转译和打包，完整SFC类型检查是另一项工作。vue-tsc能覆盖脚本和模板契约，应纳入CI；仍需交互测试验证运行行为。

对应讲解：[SFC 检查与实际边界](#k04)。

<a id="ts10-05"></a>

### TS10-05 [P0·原理] DOM模板引用为什么应包含null？

**回答：** 挂载前、卸载后或v-if移除时元素可能不存在。类型反映生命周期，使用时检查或在合适阶段访问；非空断言不会在运行时等待元素出现。

对应讲解：[DOM 引用、事件与泛型组合](#k03)。
