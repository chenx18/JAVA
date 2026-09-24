# 01 Vue 2 响应式原理与 Vue 3 对比

Vue 2 原理仍然值得掌握，但目标是理解维护和对比，不是背完整源码。高级 Vue 面试通常希望你能从一次数据修改讲到依赖收集、更新调度和限制，再说明 Vue 3 为什么改用 Proxy。

源码基线：**Vue 2.7.16 与 Vue 3.5.43**。Vue 2 已结束常规维护，本章用于老项目维护和迁移理解。Vue 2.7 虽回移了部分 Composition API，底层仍沿用 getter/setter 体系，不能按 API 外观判定用了 Proxy。

## 一、本章目录

- [1. Vue 2响应式的整体流程](#k01)
- [2. Observer、Dep 与 Watcher](#k02)
- [3. Vue 2的新增属性与删除限制](#k03)
- [4. Vue 2的数组监听限制](#k04)
- [5. $set 与 $delete 的作用](#k05)
- [6. nextTick 与批量更新](#k06)
- [7. Vue 2与Vue 3响应式对比](#k07)
- [8. Proxy为什么能解决部分限制](#k08)
- [9. Vue 2源码阅读路径](#k09)
- [10. 高级面试的掌握边界](#k10)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. Vue 2响应式的整体流程

Vue 2 的核心响应式流程可以先压缩成：

```text
data 初始化
  ↓
Observer 递归遍历对象
  ↓
Object.defineProperty 为已有属性定义 getter/setter
  ↓
组件 render 读取属性
  ↓
Dep 收集当前 Watcher
  ↓
属性被修改
  ↓
Dep 通知 Watcher
  ↓
Watcher 进入调度队列
  ↓
批量执行更新并重新渲染
```

Vue 2 不是在变量本身上安装监听器，而是在对象属性上定义 getter/setter。只有经过这些 getter 读取和 setter 写入，Vue 才能知道依赖关系和变化。

```js
const state = { count: 0 }
let currentWatcher = null
let count = state.count

Object.defineProperty(state, 'count', {
  get() {
    if (currentWatcher) currentWatcher.dependencies.add('count')
    return count
  },
  set(value) {
    if (Object.is(count, value)) return
    count = value
    console.log('notify', value)
  }
})

currentWatcher = { dependencies: new Set() }
state.count
currentWatcher = null
state.count = 1
console.log(state.count) // 1；教学例只演示读写入口，不执行完整订阅更新。
```

上面只是帮助理解的极简模型，真实 Vue 2 还要处理对象递归、嵌套依赖、组件渲染、计算属性、用户 watcher、队列去重和异常边界。

<a id="k02"></a>

### 2. Observer、Dep 与 Watcher

可以用三个角色理解 Vue 2：

| 角色 | 责任 |
| --- | --- |
| Observer | 遍历对象，为已有属性安装响应式 getter/setter |
| Dep | 表示某个属性或依赖集合，保存并通知订阅者 |
| Watcher | 表示组件渲染、computed 或用户 watch 等需要重新运行的订阅者 |

组件首次渲染时，render 读取 `state.user.name`：

1. Watcher 执行 getter/render 前，先通过 `pushTarget` 等设置活动订阅者。
2. render 读取 `state` 相关属性，触发属性 getter。
3. 属性对应的 Dep 把该 Watcher 加入订阅集合。
4. 读取嵌套对象时，嵌套对象自己的 Observer/Dep 也参与依赖关系。
5. `name` 的 setter 触发后，Dep 通知相关 Watcher。
6. 普通渲染 Watcher 通常进入 scheduler 队列；lazy Watcher 先标记 dirty，sync Watcher 可同步运行，不能说所有订阅都走相同时机。

执行后还要恢复外层活动目标，并清理本轮不再读取的旧依赖。这样嵌套计算不会串订阅者，分支切换也不会持续订阅已不用的字段。

依赖收集不是“页面所有数据变了就全部刷新”。理论上组件只会对它渲染过程中读取的响应式属性建立关系；实际更新还会经过组件树、VNode 和调度器。

<a id="k03"></a>

### 3. Vue 2的新增属性与删除限制

Vue 2 在初始化时遍历已经存在的对象属性。如果之后直接添加属性，Vue 2 没有机会为这个新属性安装 getter/setter：

```js
this.user.age = 18
```

这行可能确实把 `age` 写进对象，但 Vue 2 的响应式系统不会因此自动知道需要通知哪些 Watcher。删除属性也有相同问题：直接 `delete` 不会通过 Vue 2 设计好的通知入口完成依赖更新。

这不是JavaScript对象不能新增属性，而是Vue 2在初始化时采用了“逐属性定义访问器”的监听策略。

<a id="k04"></a>

### 4. Vue 2的数组监听限制

Vue 2 的数组实现没有逐个拦截索引，而是观察数组元素并包装变异方法。`Object.defineProperty` 本身能为可配置的索引定义访问器，但无法借此自动覆盖未来所有索引；数组 `length` 也不能被随意重定义为普通访问器。因此在 Vue 2 中下面这些操作存在检测限制：

```js
this.items[0] = nextItem
this.items.length = 0
```

Vue 2 会改写一组能改变数组结构的变异方法，例如 `push`、`pop`、`shift`、`unshift`、`splice`、`sort` 和 `reverse`，在调用原生方法后通知相关依赖。

`push/unshift/splice` 插入的新对象还会被观察。已有元素对象的已观察字段仍可响应，例如 `items[0].name = 'B'` 与 `items[0] = nextItem` 不是同一种修改。`filter/map` 返回新数组，赋回已响应式的数组属性也能触发，不属于改写的七个方法。

这并不意味着所有数组方法都触发更新，也不意味着直接修改数组成员对象的任意深层内容都可以脱离响应式路径。工程代码应使用Vue支持的变更入口或替换数组引用，并在实际版本中验证边界。

<a id="k05"></a>

### 5. $set 与 $delete 的作用

`$set`/`Vue.set`的目的，是通过Vue 2知道的方式添加属性或修改数组索引，并触发必要的通知：

```js
this.$set(this.user, 'age', 18)
this.$set(this.items, 0, nextItem)
this.$delete(this.user, 'age')
```

数组场景可以使用 `splice`，对象场景可以在初始化时把预计字段声明完整。这样做通常比到处动态补属性更容易维护。

源码中数组索引的 `set/del` 分支会借 `splice`；已存在普通属性走赋值；新的已观察嵌套属性通过 `defineReactive` 建立访问器，再通知对象级 `ob.dep`。**已经直接添加的非响应式属性，再调用 `$set` 同名键不会自动修复访问器**，因为它会命中已有键分支。应从正确入口新增，或替换父级响应式属性指向的新对象。

`Vue.set` 不能用于任意给 Vue 实例或根 `$data` 动态补响应式字段；根字段应提前声明。未观察对象也不会仅因一次 set 自动变成完整响应式对象。

Vue 3 改用Proxy后，新增属性、删除属性和数组索引等操作可以被代理拦截，因此Vue 3不再提供同样用途的 `$set`/`$delete` API。迁移时不能只做文本替换，还要检查是否依赖旧版数组/对象监听限制、插件和生命周期行为。

<a id="k06"></a>

### 6. nextTick 与批量更新

Vue 2 的数据修改与DOM更新不是简单的一行对一行同步关系。多个同步修改通常会把Watcher更新放入队列，下一次合适的异步时机批量刷新，减少重复渲染。

```js
this.count++
console.log(this.$el.textContent) // 这里可能仍是旧DOM

this.$nextTick(() => {
  console.log(this.$el.textContent) // 在本轮Vue更新后读取
})
```

`nextTick`适合等待Vue自己的更新队列完成到可观察DOM更新的时机。它不是等待网络请求完成、图片加载完成或浏览器一定已经绘制下一帧，也不是修复错误数据流的万能延迟。

Vue 3仍有批量更新和`nextTick`概念，但实现细节、调度队列和组合式API使用方式有所变化。面试时先讲“状态修改→队列→批量flush→DOM”，再限定版本，不必死背某个Promise/MutationObserver降级实现。

<a id="k07"></a>

### 7. Vue 2与Vue 3响应式对比

| 对比点 | Vue 2 | Vue 3 |
| --- | --- | --- |
| 主要机制 | `Object.defineProperty` | `Proxy`与Reflect等对象操作 |
| 新增对象属性 | 直接新增可能不被检测 | 可拦截新增操作 |
| 删除对象属性 | 直接delete可能不通知 | 可拦截删除操作 |
| 数组索引/length | 存在检测限制，需set/splice等 | 代理可处理更完整的数组操作 |
| 依赖订阅 | Dep/Watcher等模型 | effect/track/trigger等模型，具体源码有优化 |
| 组织方式 | Options API为主 | Composition API与Options API均可用 |
| 兼容策略 | 旧插件与旧生命周期语义 | 部分API移除/变化，迁移需查版本 |

Vue 3的改进不是“Proxy把所有问题自动解决”：

- 复杂对象和第三方实例仍需考虑代理身份与内部槽。
- `toRaw`、`markRaw`、`shallowReactive`等能力说明响应式边界仍然重要。
- 解构、异步副作用、缓存、组件更新和资源清理仍需要正确设计。
- Proxy不是权限系统，不能防止用户绕过前端调用后端。

<a id="k08"></a>

### 8. Proxy为什么能解决部分限制

Proxy代理对象操作，因此可以观察属性读取、设置、删除、`in`、枚举等更广泛的操作：

```js
const raw = { count: 0 }
const state = new Proxy(raw, {
  get(target, key, receiver) {
    console.log('track', String(key))
    return Reflect.get(target, key, receiver)
  },
  set(target, key, value, receiver) {
    const ok = Reflect.set(target, key, value, receiver)
    if (ok) console.log('trigger', String(key), value)
    return ok
  },
  deleteProperty(target, key) {
    const existed = Object.hasOwn(target, key)
    const ok = Reflect.deleteProperty(target, key)
    if (ok && existed) console.log('delete', String(key))
    return ok
  }
})

state.newKey = 1
delete state.newKey
```

Vue 3在更完整的响应式实现中还要处理依赖集合、嵌套代理、数组和集合、调度、只读与浅层代理等问题。Proxy减少了Vue 2新增/删除/数组索引方面的限制，但不等于一段get/set代码就是Vue 3源码。

<a id="k09"></a>

### 9. Vue 2源码阅读路径

不用从整个仓库随机阅读，可以按一次更新的主线看：

```text
入口初始化
→ Observer / defineReactive
→ Dep
→ Watcher
→ 组件 render 与更新
→ scheduler / queueWatcher
→ nextTick
→ patch / DOM更新
```

阅读每个函数时问四个问题：

1. 当前对象或状态是什么？
2. 谁在读取或修改它？
3. 依赖在哪个集合中登记？
4. 更新是立即执行还是进入队列？

高级面试重点是能画出流程、解释限制和指出版本差异。没必要背每个工具函数的源码，也不能把网上某个Vue 2简化实现当成官方完整实现。

<a id="k10"></a>

### 10. 高级面试的掌握边界

至少能回答：

- Vue 2为什么要Observer、Dep和Watcher？
- 新增属性和数组下标为什么有问题？
- `$set`为什么能解决？
- nextTick等待的到底是什么？
- Vue 3为什么换Proxy？
- Vue 2和Vue 3的限制分别是什么？

如果目标岗位是维护Vue 2老项目，再补组件更新、computed/watch、虚拟DOM和旧插件兼容；如果目标主要是Vue 3，掌握对比与迁移边界即可，不需要把Vue 2全部源码作为主线。

<a id="summary"></a>

## 三、知识小结

Vue 2的记忆主线是：

```text
Observer遍历
→ defineReactive使用defineProperty
→ Watcher执行前设置活动目标
→ getter通过Dep收集Watcher
→ setter通知Watcher
→ scheduler批量更新
→ nextTick后观察DOM
```

它的高频限制是新增属性、删除属性和数组索引/length，因此需要`$set`、`$delete`或数组变异方法。Vue 3使用Proxy后减少这些限制，但仍然需要理解代理身份、依赖清理、调度和副作用生命周期。

Vue 2需要“会解释、会对比、会读主线”，不需要全文背源码；只有目标岗位明确维护Vue 2大型项目时，才继续深入旧组件更新和插件兼容。

参考：[Vue 2 Reactivity](https://v2.vuejs.org/v2/guide/reactivity.html)、[Vue 3 Reactivity in Depth](https://vuejs.org/guide/extras/reactivity-in-depth.html)。

固定版本源码：[Observer/defineReactive/set/del](https://github.com/vuejs/vue/blob/v2.7.16/src/core/observer/index.ts)、[Watcher.get/update/cleanupDeps](https://github.com/vuejs/vue/blob/v2.7.16/src/core/observer/watcher.ts)、[数组方法包装](https://github.com/vuejs/vue/blob/v2.7.16/src/core/observer/array.ts)、[scheduler](https://github.com/vuejs/vue/blob/v2.7.16/src/core/observer/scheduler.ts)、[nextTick](https://github.com/vuejs/vue/blob/v2.7.16/src/core/util/next-tick.ts)。按函数定位，不将主分支的行号当作永久入口。

<a id="interview"></a>

## 四、面试题与答案

<a id="vsr01-01"></a>

### VSR01-01 [P0·原理] Vue 2响应式系统的基本流程是什么？

**回答：** Vue 2在初始化时递归观察对象，为已有属性定义getter/setter。组件渲染读取属性时，当前Watcher通过Dep建立依赖；属性setter执行时，Dep通知相关Watcher；Watcher进入调度队列，Vue批量执行更新并刷新DOM。数组和新增属性还有额外限制，需要专门的变更入口。

对应讲解：[Vue 2响应式的整体流程](#k01)。

<a id="vsr01-02"></a>

### VSR01-02 [P0·原理] Dep和Watcher分别负责什么？

**回答：** Dep可以理解为某个响应式属性的依赖集合，负责收集和通知订阅者；Watcher代表需要重新运行的订阅者，例如组件渲染、computed或用户watch。读取建立Dep到Watcher的关系，修改从Dep通知Watcher。具体源码会有嵌套依赖和队列处理，不能把两者简化成两个普通数组。

对应讲解：[Observer、Dep与Watcher](#k02)。

<a id="vsr01-03"></a>

### VSR01-03 [P0·原理] Vue 2为什么不能直接检测新增属性？

**回答：** Vue 2主要在初始化时为已有属性定义getter/setter。后来直接添加的新属性没有安装响应式访问器，所以虽然JavaScript对象确实增加了字段，Vue 2没有对应依赖通知入口。初始化时声明字段或用Vue.set等受支持入口可以解决。

对应讲解：[Vue 2的新增属性与删除限制](#k03)。

<a id="vsr01-04"></a>

### VSR01-04 [P0·原理] Vue 2为什么需要$set和$delete？

**回答：** 它们用Vue 2知道的方式添加/删除属性或更新数组索引，并触发必要的依赖通知。普通delete或对象新增绕过了初始化时定义的访问器；Vue 3的Proxy能拦截更广对象操作，因此不再需要同样用途的API。

对应讲解：[$set与$delete的作用](#k05)。

<a id="vsr01-05"></a>

### VSR01-05 [P1·原理] Vue 2为什么需要改写数组方法？

**回答：** Vue 2不能为任意未来数组索引完整安装访问器，直接写items[0]或修改length可能不触发通知。因此它改写push、splice、sort等变异方法，在原操作后通知依赖；工程上使用splice、set或替换引用，避免直接下标写入的检测限制。

对应讲解：[Vue 2的数组监听限制](#k04)。

<a id="vsr01-06"></a>

### VSR01-06 [P0·原理] Vue 2的nextTick解决什么问题？

**回答：** Vue会把Watcher更新放入队列并批量执行，状态修改后DOM可能还没更新。nextTick用于等待本轮Vue更新到可观察DOM的时机，再执行读取或后续操作。它不等待网络、图片或保证下一帧绘制，也不是修复错误数据流的延迟工具。

对应讲解：[nextTick与批量更新](#k06)。

<a id="vsr01-07"></a>

### VSR01-07 [P0·对比] Vue 2和Vue 3响应式机制有什么区别？

**回答：** Vue 2主要通过Object.defineProperty逐属性定义getter/setter，存在新增属性、删除属性和数组索引/length的检测限制；Vue 3以Proxy代理对象操作，能观察更广的新增、删除和数组操作，并以effect/track/trigger等模型组织依赖。Vue 3仍有代理身份、浅层、集合和副作用边界，不能只说Proxy解决一切。

对应讲解：[Vue 2与Vue 3响应式对比](#k07)。

<a id="vsr01-08"></a>

### VSR01-08 [P1·原理] Proxy为什么能解决Vue 2的一部分限制？

**回答：** Proxy代理对象级操作，除了读取和写入，还能拦截新增、删除、in和枚举等操作，因此不需要在初始化时为每个未来属性逐个定义访问器。它仍需要完整依赖图、嵌套代理、集合和调度实现，不能用极简get/set模型冒充Vue源码。

对应讲解：[Proxy为什么能解决部分限制](#k08)。

<a id="vsr01-09"></a>

### VSR01-09 [P1·源码] Vue 2源码应该重点看哪些路径？

**回答：** 先沿一次更新主线阅读：Observer/defineReactive安装访问器，Dep管理依赖，Watcher表示订阅，组件render触发读取，setter通知后进入scheduler/queueWatcher，nextTick执行队列，最后patch更新DOM。每一步都问读写对象、依赖集合和执行时机，不必随机背整个仓库。

对应讲解：[Vue 2源码阅读路径](#k09)。

<a id="vsr01-10"></a>

### VSR01-10 [P1·场景] Vue 2项目维护面试要准备什么？

**回答：** 先准备响应式限制和$set/$delete、数组变更、nextTick，再补computed/watch、组件更新、旧插件和生命周期兼容。能给出真实排查过程，例如某个新增字段不更新、旧请求覆盖状态或组件卸载未清理，并说明如何复现和验证，比全文背源码更有价值。

对应讲解：[高级面试的掌握边界](#k10)。
