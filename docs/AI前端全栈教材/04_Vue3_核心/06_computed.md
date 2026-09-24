# 06 computed 与派生状态

computed表达可以从已有响应式状态计算出的结果。缓存来自依赖关系，而不是永久保存一次返回值；合理使用它可以避免重复状态和同步负担。

## 一、本章目录

- [依赖、缓存与读取](#k01)
- [computed、方法与watch的分工](#k02)
- [可写 computed 与双向转换](#k03)
- [结果稳定性与测试](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 依赖、缓存与读取

```ts
import { ref, computed } from 'vue';
const price = ref(10), count = ref(2);
let runs = 0;
const total = computed(() => { runs++; return price.value * count.value; });
console.log(total.value, total.value, runs); // 20 20 1
count.value = 3;
console.log(total.value, runs); // 30 2
```

getter执行时建立依赖，未发生相关变化时可复用结果；依赖变化使结果需要重新确认，通常在读取时求值。内部脏标记、版本与稳定性优化会随Vue版本变化，先掌握可观察契约。

Date.now、普通外部变量等不自动成为响应式依赖。computed(()=>Date.now())不会按时钟自动刷新；需要定时更新响应式源，并管理timer生命周期。

<a id="k02"></a>

### 2. computed、方法与watch的分工

计算展示金额、过滤结果等纯派生状态适合computed；每次动作都要执行的逻辑用函数；请求、日志、订阅等副作用用watch/生命周期等管理。

若一个值总能由其他状态推出，通常不必再用watch同步维护一份可写副本，否则容易产生顺序依赖和漏更新。computed getter应避免改源状态或发请求，async getter返回的是Promise而非自动被Vue等待的业务值。

缓存也有成本，大数组过滤可能仍很昂贵；需控制依赖粒度、数据规模和调用位置，而不是任何慢逻辑都包computed。

<a id="k03"></a>

### 3. 可写 computed 与双向转换

```ts
import { ref, computed } from 'vue';
const first = ref('Alice'), last = ref('Chen');
const full = computed({
  get: () => first.value + ' ' + last.value,
  set: (value: string) => {
    const [head = '', ...tail] = value.trim().split(/\s+/);
    first.value = head;
    last.value = tail.join(' ');
  }
});
full.value = 'Bob Li';
console.log(first.value, last.value); // Bob Li
```

setter将写入意图转换到真正源状态，不能简单full.value=value造成递归。示例只演示转换，真实姓名不应假定全球都能按空格拆分；业务规则应独立设计。

表单临时编辑、提交前验证与服务端权威状态也不应被一个双向computed隐藏全部生命周期。

<a id="k04"></a>

### 4. 结果稳定性与测试

computed每次返回新对象可能改变引用身份，即使内容相同也会影响依赖它的更新和props稳定性。较新Vue有计算结果稳定性优化，但不会自动深比较所有对象。

适合时保留稳定结果，避免在getter中引入随机数、时间或隐式副作用。测试应验证重复读取的计算次数、依赖变化、无关状态变化、可写映射和边界输入；不要仅断言页面最后显示了一个值。

<a id="summary"></a>

## 三、知识小结

computed管理派生结果与依赖缓存，方法执行动作，watch管理副作用。先减少重复状态，再考虑缓存与结果身份，不把computed当异步任务调度器。

参考：[Vue Computed](https://vuejs.org/guide/essentials/computed.html)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="vue06-01"></a>

### VUE06-01 [P0·原理] computed为什么能缓存？

**回答：** 运行getter时建立响应式依赖，相关依赖未变时可复用结果，变化后再按读取和内部失效机制重新求值。不是任何外部变量变化都能被它知道。

对应讲解：[依赖、缓存与读取](#k01)。

<a id="vue06-02"></a>

### VUE06-02 [P0·基础] computed、methods和watch怎么选？

**回答：** 纯派生数据用computed，每次调用动作可用方法，网络和订阅等副作用用watch或生命周期。别用watch维护本可直接推导的重复状态，也别在getter里做请求。

对应讲解：[computed、方法与watch的分工](#k02)。

<a id="vue06-03"></a>

### VUE06-03 [P1·原理] computed(()=>Date.now())会自动更新时间吗？

**回答：** 不会，Date.now本身不是响应式依赖。需要以timer更新响应式源并清理timer，或按业务事件计算；缓存不会自动知道物理时间流逝。

对应讲解：[依赖、缓存与读取](#k01)。

<a id="vue06-04"></a>

### VUE06-04 [P1·工程取舍] 返回新对象的computed有什么代价？

**回答：** 引用每次变化可能增加下游更新，框架不会无条件深比较全部内容。应看结果稳定性、依赖与实际性能，必要时优化数据结构而不是盲目增加缓存。

对应讲解：[结果稳定性与测试](#k04)。

<a id="vue06-05"></a>

### VUE06-05 [P0·原理] 可写computed的setter应该修改什么？

**回答：** 修改真正的源状态，把写入意图转换成领域数据，而不是再次给自身赋值造成递归。转换规则、校验与临时表单状态需明确，不能让双向映射隐藏全部业务流程。

对应讲解：[可写 computed 与双向转换](#k03)。
