# 08 Scheduler、批处理与 nextTick

响应式值的变化和DOM更新是两个时间点。调度器把相关工作去重、排序和批量处理，nextTick帮助等待当前Vue更新流程，而不是一个通用延迟工具。

## 一、本章目录

- [同步状态与异步更新](#k01)
- [队列、去重与顺序](#k02)
- [nextTick 的使用与边界](#k03)
- [实际调试与性能判断](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 同步状态与异步更新

设置ref.value或代理属性时，数据本身通常立即改变；组件DOM更新可进入调度队列，多个同步改动被合并，以减少重复渲染。并不是先保留三份旧值再逐个全部展示。

```ts
import { ref, watch, nextTick } from 'vue';
const count = ref(0);
const seen: number[] = [];
const stop = watch(count, value => seen.push(value));
count.value++;
count.value++;
console.log(count.value, seen.length); // 2 0
await nextTick();
console.log(seen); // [2]
stop();
```

是否批处理还取决于任务类别和flush选项，sync watcher可同步执行。不能把所有响应式effect都说成相同的一条延迟队列。

<a id="k02"></a>

### 2. 队列、去重与顺序

教学模型可分为加入任务、去重、安排flush、按规则执行、处理后置回调。父子组件及watcher顺序影响可观察DOM，实际排序和内部数据结构依版本实现，不应死背某个私有函数名就认为理解了调度。

同一轮任务执行中可能产生新任务，调度器需避免重复、处理嵌套并防止无限递归更新。用户代码仍不应在更新钩子里无条件再次写同一个状态形成循环。

浏览器微任务机制提供调度基础，但Vue的nextTick是等待Vue当前flush关联的完成点，不是替代所有宿主任务规则。

<a id="k03"></a>

### 3. nextTick 的使用与边界

```vue
<script setup lang="ts">
import { ref, nextTick } from 'vue'
const visible = ref(false)
const input = ref<HTMLInputElement | null>(null)
async function open() {
  visible.value = true
  await nextTick()
  input.value?.focus()
}
</script>
<template>
  <button type="button" @click="open">编辑</button>
  <input v-if="visible" ref="input" aria-label="名称">
</template>
```

先改变需要触发渲染的状态，再await nextTick，再读新DOM；如果并没有排队的更新，nextTick不会凭空等待未来任意请求或动画完成。它不保证图片解码、CSS动画、字体加载和浏览器下一帧绘制全部结束。

setTimeout(0)是另一个宿主任务调度入口，可能碰巧在DOM之后，但契约不同，不是nextTick的等价替代。

<a id="k04"></a>

### 4. 实际调试与性能判断

读到旧DOM时，先检查状态是否真的被追踪、条件是否渲染了目标、引用是否已挂载，再看flush顺序。不要到处加nextTick掩盖数据流错误。

多个nextTick也不会把CPU计算挪到线程；频繁await已兑现微任务仍可能延后绘制。重计算应减少工作量、分片或用Worker，视觉更新可按帧协调。

测试应分别观察同步状态、watcher回调、DOM提交和实际浏览器帧，避免把某次时间上的巧合写成固定保证。

<a id="summary"></a>

## 三、知识小结

状态立即可变，DOM通常批量更新；nextTick等Vue flush，不等所有异步或实际绘制。先修正依赖与数据流，再选择准确的等待点。

参考：[Vue nextTick](https://vuejs.org/api/general.html#nexttick)；[Vue Watcher Timing](https://vuejs.org/guide/essentials/watchers.html#callback-flush-timing)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="vue08-01"></a>

### VUE08-01 [P0·原理] 连续修改三次状态为什么通常只更新一次组件？

**回答：** 相关组件更新任务可在同一轮队列中去重合并，最终渲染读取最新状态，减少中间重复工作。状态值自身已同步改变，sync watcher等场景也有不同规则。

对应讲解：[同步状态与异步更新](#k01)。

<a id="vue08-02"></a>

### VUE08-02 [P0·基础] nextTick应该何时调用？

**回答：** 需要观察由某次状态变更产生的新DOM时，先改状态，再await nextTick。它等待相关Vue更新点，不会替你等待未来网络请求或修复未追踪的数据。

对应讲解：[nextTick 的使用与边界](#k03)。

<a id="vue08-03"></a>

### VUE08-03 [P1·原理] nextTick等于setTimeout(0)吗？

**回答：** 不等于。前者关联Vue当前更新flush，后者是宿主timer任务，时机与保证不同。不能因为常见例子输出相近就当语义一致。

对应讲解：[nextTick 的使用与边界](#k03)。

<a id="vue08-04"></a>

### VUE08-04 [P1·工程取舍] 多加nextTick能解决页面卡顿吗？

**回答：** 不能自动减少计算或让计算并行，微任务连续执行还可能延后绘制。先定位长任务和更新范围，再减少工作、分片或选择Worker。

对应讲解：[实际调试与性能判断](#k04)。

<a id="vue08-05"></a>

### VUE08-05 [P1·原理] 更新队列去重是否意味着所有副作用只执行一次？

**回答：** 不是，任务类别、flush策略、嵌套更新和依赖变化会影响执行。去重针对特定队列任务，sync watcher和重新产生的工作不能用同一概括；业务仍要避免无条件自触发循环。

对应讲解：[队列、去重与顺序](#k02)。
