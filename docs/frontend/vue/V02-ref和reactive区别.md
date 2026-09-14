# ref 和 reactive 区别

> 专题：Vue 专题  
> 编号：V02

### 一句话答案

`ref` 适合包装基本类型或单个值，`reactive` 适合包装对象；模板里 ref 自动解包，脚本里要通过 `.value` 访问。

### 核心原理

```text
ref：通过 .value 持有值
reactive：返回 Proxy 代理对象
```

### 项目里怎么用

- 基本类型：`const count = ref(0)`
- 表单对象：`const form = reactive({ name: '' })`
- 组合式函数返回多个状态时常用 ref。

### 常见坑

- 脚本里忘记 `.value`。
- reactive 解构后响应性丢失。
- 用 reactive 包基本类型。

### 面试表达

我一般基本类型用 ref，对象状态用 reactive。但如果需要从组合式函数返回多个字段，我更倾向用 ref 或 toRefs，避免解构时丢响应式。
