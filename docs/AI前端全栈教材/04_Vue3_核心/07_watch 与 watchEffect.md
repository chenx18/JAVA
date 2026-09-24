# 07 watch、watchEffect 与异步清理

watcher用于状态变化后的副作用。设计时需要同时决定监听什么、何时执行，以及前一次副作用何时失效或清理。

## 一、本章目录

- [显式 source 与自动依赖](#k01)
- [immediate、deep、once 与 flush](#k02)
- [取消旧副作用与版本归属](#k03)
- [停止、作用域与调试](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 显式 source 与自动依赖

watch接收ref、getter、响应式对象或来源数组，明确观察输入并提供newValue/oldValue；默认通常惰性执行。watchEffect立即执行并自动收集同步期间实际读取的响应式依赖。

```ts
import { ref, watch, nextTick } from 'vue';
const count = ref(0);
const changes: Array<[number, number]> = [];
const stop = watch(count, (next, previous) => changes.push([next, previous]));
count.value = 1;
count.value = 2;
await nextTick();
console.log(changes); // [[2,0]]
stop();
```

watch(state.count,...)若传的是普通数字就不是稳定的响应式来源，应传()=>state.count。watchEffect异步回调只自动追踪第一次await之前的同步读取；await后新读字段不能想当然地加入原依赖。

<a id="k02"></a>

### 2. immediate、deep、once 与 flush

immediate使初次也执行回调，once用于一次变化后停止的场景；具体首次行为需按选项组合理解。deep会遍历更深结构，Vue3.5还支持数字深度；大对象深监听可能昂贵。

监听reactive对象本身通常带深层观察语义；getter返回对象时默认更关注返回引用变化，需要deep才覆盖更多嵌套。深修改时newValue与oldValue可能是同一对象引用，不提供自动历史快照。

flush默认pre通常在组件自身DOM更新前执行，post适合读更新后的DOM，sync同步执行且不享受同样批处理，需谨慎避免大量同步工作或重入。

<a id="k03"></a>

### 3. 取消旧副作用与版本归属

```ts
import { ref, watch } from 'vue';
const query = ref('');
const result = ref<unknown>(null);
const stop = watch(query, async (value, _old, onCleanup) => {
  const controller = new AbortController();
  let active = true;
  onCleanup(() => { active = false; controller.abort(); });
  try {
    const response = await fetch('/api/search?q=' + encodeURIComponent(value), {
      signal: controller.signal
    });
    if (!response.ok) throw new Error('HTTP ' + response.status);
    const data: unknown = await response.json();
    if (active) result.value = data;
  } catch (error) {
    if (active && !controller.signal.aborted) console.error(error);
  }
});
// 示例由组件/作用域管理stop，服务端仍需处理查询权限。
```

清理在下一次失效或停止时运行，abort尽量停资源，active/版本阻止已完成或不响应取消的旧结果提交。应在启动异步等待前登记清理，避免失效已发生后才登记。

Vue3.5的onWatcherCleanup要求在watcher同步执行阶段调用，不能放在await之后；回调参数onCleanup有绑定差异，但也应尽早注册以保证时序。

<a id="k04"></a>

### 4. 停止、作用域与调试

watch/watchEffect返回可停止句柄，较新版本还可pause/resume；同步在组件setup创建的watcher通常随组件停止，异步后来创建的watcher未必自动绑定，需要自己持有stop。

onTrack/onTrigger等开发调试钩子帮助确认依赖与触发来源，不应当成生产业务事件接口。长期watcher应避免捕获不必要大对象，并在组件复用、KeepAlive激活状态和请求切换时定义是否继续执行。

测试要覆盖合并更新、深引用相同、清理先后、旧请求迟到、停止后不再提交，而不只测试一次字段修改。

<a id="summary"></a>

## 三、知识小结

watch明确来源，watchEffect自动收集同步读；deep控制遍历，flush控制时机，cleanup/stop控制生命周期。网络取消和状态归属检查相互补充。

参考：[Vue Watchers](https://vuejs.org/guide/essentials/watchers.html)；[Vue Reactivity Core](https://vuejs.org/api/reactivity-core.html)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="vue07-01"></a>

### VUE07-01 [P0·原理] watch与watchEffect的区别是什么？

**回答：** watch显式给来源，可观察新旧值并控制初次执行；watchEffect立即执行并追踪同步实际读取。异步await后的读取不会自动延长同一次依赖收集，选择取决于是否需要明确触发契约。

对应讲解：[显式 source 与自动依赖](#k01)。

<a id="vue07-02"></a>

### VUE07-02 [P1·原理] deep watch里新旧值为什么可能相同？

**回答：** 嵌套修改没有创建新的根对象，回调拿到的可能是同一引用。deep负责发现嵌套变化，不自动保存历史快照；需要差异比较时另行记录合适数据。

对应讲解：[immediate、deep、once 与 flush](#k02)。

<a id="vue07-03"></a>

### VUE07-03 [P0·工程取舍] 为什么取消fetch后还要active或版本号？

**回答：** 旧任务可能已完成网络、仍在解析或不支持取消。清理标记让提交时确认仍属于当前watcher运行，避免旧结果污染新状态；abort主要减少无用工作。

对应讲解：[取消旧副作用与版本归属](#k03)。

<a id="vue07-04"></a>

### VUE07-04 [P1·原理] post与nextTick是否表示浏览器已经绘制？

**回答：** 主要表示相应Vue更新flush和DOM时机，不保证像素已经绘制到屏幕。需要视觉帧配合时可考虑rAF，但也要限定浏览器调度与页面可见性。

对应讲解：[immediate、deep、once 与 flush](#k02)。

<a id="vue07-05"></a>

### VUE07-05 [P1·工程取舍] watcher一定随组件自动停止吗？

**回答：** 同步在setup相关作用域创建的通常会绑定生命周期；异步回调中后来创建的可能不绑定。应明确所有者并保存stop，额外资源也由清理逻辑释放。

对应讲解：[停止、作用域与调试](#k04)。
