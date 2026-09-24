# 11 WeakMap与WeakSet

弱集合让附加数据与对象生命周期关联。它解决的是不额外阻止回收的问题，不是按时淘汰缓存，也不提供可观测的GC进度。

## 一、本章目录

- [弱引用到底弱在哪里](#k01)
- [API 与不可枚举性](#k02)
- [对象元数据与缓存选型](#k03)
- [WeakRef、FinalizationRegistry 与资源清理](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 弱引用到底弱在哪里

WeakMap弱持有键；WeakSet弱持有成员。现代规范允许对象和非注册Symbol作为键/成员，Symbol.for产生的注册符号不能作为可回收的弱键；旧环境对Symbol支持可能不同。

```js
const metadata = new WeakMap();
let node = {};
metadata.set(node, { selected: true });
console.log(metadata.get(node).selected); // true
node = null;
// 此后不再持有该键；是否及何时回收不能同步验证。
```

WeakMap的值不是“任何时候都弱引用”。只要键仍由外部保持可达，对应值也可以保持可达；如果值另被其他结构引用，它不会因键被回收就被强制销毁。不要用等待几毫秒后某值消失来写GC必然性测试。

<a id="k02"></a>

### 2. API 与不可枚举性

| 容器 | API | 不具备 |
| --- | --- | --- |
| WeakMap | new WeakMap(iterable?)、set/get/has/delete | size、clear、keys/values/entries、forEach |
| WeakSet | new WeakSet(iterable?)、add/has/delete | size、clear、迭代API |

set/add返回当前集合，get返回值或undefined，has/delete返回Boolean。读取不存在键与值为undefined仍应通过has区分。

较新的WeakMap.getOrInsert及getOrInsertComputed按是否已有键返回或插入默认结果，需检查运行时支持；兼容写法用has/get/set。它们不改变弱引用语义。

不可枚举让程序不能依靠“现在剩下哪些键”直接观察GC的不确定时机。仅靠WeakMap无法实现可列出所有缓存项、统计全部条目或手动LRU淘汰。

<a id="k03"></a>

### 3. 对象元数据与缓存选型

WeakMap适合DOM节点、实例或编译节点的附加元数据：不想修改目标对象本身，也不想让辅助表单独延长目标寿命。前提是别处没有长期强引用这些对象。

Map+TTL按时间有效性缓存，LRU按访问次序淘汰，容量上限控制数量或内存预算；WeakMap按键的可达性关联生命周期。这些维度不同，不能因为担心泄漏就把所有Map替换为WeakMap。

例如用户ID为字符串、需要查看缓存大小并定期淘汰，普通Map更符合需求。对象身份键且只需伴随对象存在的元数据，WeakMap更合适。

<a id="k04"></a>

### 4. WeakRef、FinalizationRegistry 与资源清理

WeakRef(obj)提供不阻止目标回收的引用，deref()取得目标或undefined；能取到目标时仍要按程序规则使用，不能把它当跨阶段可靠存储。

FinalizationRegistry.register(target,heldValue,unregisterToken?)登记可能的终结清理，unregister(token)取消登记。回调什么时候执行、甚至进程结束前是否执行都不保证；heldValue等附加引用也要避免间接强持有target。

它们适合允许非确定性结果的优化辅助，不适合负责关闭网络连接、提交日志、释放业务锁或保存用户数据。可靠清理仍由dispose/finally/组件卸载等明确生命周期负责。

<a id="summary"></a>

## 三、知识小结

弱集合记忆为“附加表不单独留住对象”。能否回收看所有引用链，回收时机不由弱集合保证；需要容量、过期、遍历或可靠关闭时使用相应机制。

参考：[MDN WeakMap](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/WeakMap)；[MDN WeakSet](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/WeakSet)。较新 API 按目标运行时核对支持，示例各自独立。

<a id="interview"></a>

## 四、面试题与答案

<a id="d11-01"></a>

### D11-01 [P1·原理] WeakMap弱的是键还是值？

**回答：** 弱持有键，不因表中存在条目就单独阻止键回收。值的存活还取决于键和其他强引用，不能把它说成键和值都会在固定时间自动清除。

对应讲解：[弱引用到底弱在哪里](#k01)。

<a id="d11-02"></a>

### D11-02 [P1·原理] WeakMap为什么没有size和遍历？

**回答：** 键是否存活受不确定的GC影响；枚举或size会暴露这种变化，也难以提供普通Map一样稳定可推理的集合视图。WeakMap主要让已持有键的代码访问关联数据。

对应讲解：[API 与不可枚举性](#k02)。

<a id="d11-03"></a>

### D11-03 [P1·工程取舍] 何时选WeakMap，何时选Map加TTL？

**回答：** 对象元数据需要伴随键生命周期时选WeakMap；按字符串ID缓存、需要大小、遍历、过期或淘汰时选Map并明确策略。生命周期关联与业务时效不是同一目标。

对应讲解：[对象元数据与缓存选型](#k03)。

<a id="d11-04"></a>

### D11-04 [P2·工程取舍] 能否靠FinalizationRegistry关闭WebSocket？

**回答：** 不能承担这种可靠职责。GC和终结回调时机不确定，回调可能根本来不及执行。连接应由明确所有者在完成、失败、取消或卸载时关闭，弱引用机制最多作可容忍丢失的辅助。

对应讲解：[WeakRef、FinalizationRegistry 与资源清理](#k04)。
