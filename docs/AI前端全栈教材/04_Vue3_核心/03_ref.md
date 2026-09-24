# 03 ref、shallowRef 与引用工具

ref提供一个可追踪的.value入口，使原始值、可替换对象和组合式函数返回值都能进入响应式系统。重点是读写入口、解包场景和浅层边界。

## 一、本章目录

- [ref 的值容器](#k01)
- [shallowRef 与显式触发](#k02)
- [isRef、unref、toRef、toRefs 与 toValue](#k03)
- [customRef 与副作用所有权](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. ref 的值容器

```ts
import { ref, computed } from 'vue';
const count = ref(0);
const doubled = computed(() => count.value * 2);
count.value++;
console.log(count.value, doubled.value); // 1 2
```

脚本中通过.value读写，读取可被当前响应式effect追踪，变化触发相关更新。ref既能保存原始值也能保存对象，默认对象值会经深层响应式转换；替换.value也能保持同一个ref容器对外的引用。

模板顶层ref通常自动解包，但嵌套访问、数组和集合有具体规则，不能推导为“任何地方都不用.value”。类型上ref(null)还需包含未来合法对象与空状态。

<a id="k02"></a>

### 2. shallowRef 与显式触发

```ts
import { shallowRef, triggerRef, watchEffect } from 'vue';
const data = shallowRef({ count: 0 });
const values: number[] = [];
const stop = watchEffect(() => values.push(data.value.count), { flush: 'sync' });
data.value.count = 1;
console.log(values); // [0]
triggerRef(data);
console.log(values); // [0,1]
stop();
```

shallowRef只追踪.value访问，不把内部对象深度转换。替换.value通常可触发，内部修改需要自己设计不可变更新或在适当场景triggerRef。上例用sync便于展示，不代表业务watcher应都同步执行。

外部状态库、复杂第三方实例或大块不可变数据常适合浅层容器，但少追踪也意味着不能期望内部任意修改自动通知。

<a id="k03"></a>

### 3. isRef、unref、toRef、toRefs 与 toValue

| API | 作用 |
| --- | --- |
| isRef(value) | 判断是否ref |
| unref(value) | 是ref则取.value，否则返回原值 |
| toValue(value/ref/getter) | 归一化值，也会调用getter；较新Vue能力 |
| toRef(object,key) | 建立某属性的ref入口 |
| toRef(value/ref/getter) | 归一化为ref，具体重载和只读性按输入 |
| toRefs(object) | 为当前可枚举属性建立一组ref，方便保留关联的解构 |

```ts
import { reactive, toRefs, toRef } from 'vue';
const state = reactive({ count: 0 });
const { count } = toRefs(state);
count.value++;
console.log(state.count); // 1
const linked = toRef(state, 'count');
state.count++;
console.log(linked.value); // 2
```

toRefs只处理调用时可枚举属性，未来才新增的可选属性可单独toRef。unref不会把普通函数当getter调用，toValue可以；组合式函数接收哪些形式应在契约中声明。

<a id="k04"></a>

### 4. customRef 与副作用所有权

customRef让开发者提供get/set并决定何时track/trigger，可实现输入防抖等行为。它不是取消副作用责任：定时器、订阅和请求仍要随作用域停止。

对外返回ref时要明确它可写还是只读、替换是否允许、延迟更新何时生效。每次getter返回新对象可能破坏身份稳定，使父子更新出现意外行为；定制响应式入口必须配边界测试。

ref与reactive不是竞争关系，选择取决于值类型、是否需要整体替换和对外传递方式。

<a id="summary"></a>

## 三、知识小结

ref追踪.value，shallowRef只追踪外壳，工具API帮助保留属性关联或归一化输入。解包取决于场景，浅层数据与customRef都需要明确更新和清理策略。

参考：[Vue Reactivity Core](https://vuejs.org/api/reactivity-core.html)；[Vue Reactivity Utilities](https://vuejs.org/api/reactivity-utilities.html)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="vue03-01"></a>

### VUE03-01 [P0·原理] ref为什么能让原始值响应式？

**回答：** 它把值放到有可追踪读写入口的容器里，访问.value时建立依赖，设置时触发，而不是直接监听某个普通局部变量的赋值。

对应讲解：[ref 的值容器](#k01)。

<a id="vue03-02"></a>

### VUE03-02 [P0·原理] shallowRef内部属性变化为什么没更新？

**回答：** 它只追踪.value层，不深代理内部对象。需要替换.value、明确不可变更新或使用triggerRef等策略，选择浅层就是主动控制追踪范围。

对应讲解：[shallowRef 与显式触发](#k02)。

<a id="vue03-03"></a>

### VUE03-03 [P1·基础] toRefs与直接解构reactive有什么区别？

**回答：** 直接解构原始值属性通常得到当时的值，失去通过原代理读取的关联；toRefs返回属性ref，后续.value仍读写原对象属性。它仅为当时可枚举属性建立入口。

对应讲解：[isRef、unref、toRef、toRefs 与 toValue](#k03)。

<a id="vue03-04"></a>

### VUE03-04 [P1·原理] unref和toValue一样吗？

**回答：** 不一样，unref只在ref时解包，普通函数仍作为值返回；toValue还能调用getter。组合式函数要声明接受值、ref还是getter，并在响应式读取上下文中正确使用。

对应讲解：[isRef、unref、toRef、toRefs 与 toValue](#k03)。

<a id="vue03-05"></a>

### VUE03-05 [P1·工程取舍] customRef为何还需要管理生命周期？

**回答：** 它只是让开发者控制get/set里的追踪和触发，不自动关闭其中的timer、订阅或请求。延迟更新和返回对象身份也影响下游，应绑定作用域清理并测试停止与重复使用。

对应讲解：[customRef 与副作用所有权](#k04)。
