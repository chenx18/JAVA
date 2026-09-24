# 07 Scheduler与nextTick

调度器组织的是带身份和顺序的任务，nextTick等待相应flush Promise。把代码中的队列、标志与Promise关联起来，才知道等待保证到哪一步。

源码基线：**Vue v3.5.43**。区分公开行为、这个版本的内部实现与本文教学模型；代码块各自独立，使用 Vue 包的例子在安装相应版本的 Node ESM 或前端工程中运行。

## 一、本章目录

- [queueJob 与任务身份](#k01)
- [flush 与 nextTick 的连接](#k02)
- [pre、post、sync 的范围](#k03)
- [递归与误用排查](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. queueJob 与任务身份

[queueJob](https://github.com/vuejs/core/blob/v3.5.43/packages/runtime-core/src/scheduler.ts)检查QUEUED标志去重，通过job.id及PRE标志安排插入位置；常见组件任务id来自instance.uid，使父创建在前的更新有序。

pre watcher可使用所属组件uid和PRE标志，在相应组件更新前执行。父更新若卸载子组件，可以跳过子已失效任务。队列不是将函数源码相同就认为同一个任务，而依赖稳定job引用及标记。

最小Set队列可以展示去重，但缺少父子顺序、post回调、递归限制与失效处理；第15章会明确这个差距。

<a id="k02"></a>

### 2. flush 与 nextTick 的连接

queueFlush在没有currentFlushPromise时设为resolvedPromise.then(flushJobs)。nextTick选取currentFlushPromise或一个已兑现Promise，再按需then回调。源码关键在[nextTick/queueFlush/flushJobs](https://github.com/vuejs/core/blob/v3.5.43/packages/runtime-core/src/scheduler.ts)。

```js
import { ref, watch, nextTick } from 'vue';
const n = ref(0), seen = [];
const stop = watch(n, v => seen.push(v));
n.value = 1; n.value = 2;
console.log(n.value, seen.length); // 2 0
await nextTick();
console.log(seen); // [2]
stop();
```

先修改状态再等待当前更新；没有排队更新时nextTick不会等一个未来的网络请求。flush期间新增任务需继续处理，异常路径也需复位队列标志，否则后续更新可能永远被误判为已入队。

<a id="k03"></a>

### 3. pre、post、sync 的范围

pre默认watcher在所属组件DOM更新前的相应位置执行，post走后渲染回调路径，sync同步触发并可能承担高频和重入成本。[scheduler/augmentJob](https://github.com/vuejs/core/blob/v3.5.43/packages/runtime-core/src/apiWatch.ts)给watch job补齐这些行为。

“post先于updated还是某回调先”要按具体父子关系和入队位置分析，不能写一张对所有嵌套情况绝对成立的扁平顺序表。异步组件和Suspense也会影响后渲染效果队列。

内存中的state已改、组件render完成、DOM已patch、浏览器已paint是不同阶段。

<a id="k04"></a>

### 4. 递归与误用排查

Vue开发调度器有递归更新检查，但它不是业务循环的正确终止条件。updated中无条件改同一状态、watch相互写回可能产生循环，应修正数据来源而不是增加nextTick。

nextTick不减少重计算，也不保证浏览器绘制。需要帧时机用合适浏览器API并考虑后台暂停；需要等图片、动画、网络则使用各自完成信号。

排查读旧DOM先验证响应式依赖、目标是否存在与ref生命周期，再检查flush；乱加timer只会让错误暂时难复现。

<a id="summary"></a>

## 三、知识小结

queueJob用稳定身份与顺序组织工作，queueFlush安排Promise，nextTick连接flush完成点。公开保证是Vue更新时机，不是所有异步与浏览器绘制。

<a id="interview"></a>

## 四、面试题与答案

<a id="vsr07-01"></a>

### VSR07-01 [P0·原理] nextTick与setTimeout(0)为何不同？

**回答：** nextTick关联当前Vue flush Promise，timer只是宿主任务调度。二者碰巧常在更新后执行不等于保证相同，网络和绘制也不由nextTick包办。

对应讲解：[flush 与 nextTick 的连接](#k02)。

<a id="vsr07-02"></a>

### VSR07-02 [P1·源码] 为何调度器关心父子组件id顺序？

**回答：** 父先创建的uid通常较小，排序支持父先更新，父卸载子后可跳过无效子任务。pre标志还让所属watcher位于组件更新前。

对应讲解：[queueJob 与任务身份](#k01)。

<a id="vsr07-03"></a>

### VSR07-03 [P0·原理] sync watcher是不是更准确？

**回答：** 只是更早同步执行，缺少同样批处理优势，高频写入或互相触发成本更高。按副作用需要选时机，不以越早越好评价。

对应讲解：[pre、post、sync 的范围](#k03)。

<a id="vsr07-04"></a>

### VSR07-04 [P0·原理] nextTick之后是否一定已经看见屏幕新像素？

**回答：** 不保证，它等待Vue更新流程相关完成点，浏览器绘制、图片和动画有独立时机。DOM可读取与已经paint要分开。

对应讲解：[递归与误用排查](#k04)。
