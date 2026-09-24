# 09 判别联合与类型安全状态机

状态建模要让合法数据组合容易表达，让矛盾组合难以出现。判别联合把状态名和该状态所需数据绑定起来，状态机再约束如何从一个状态转到另一个。

## 一、本章目录

- [从多个Boolean到合法状态](#k01)
- [判别字段与穷尽读取](#k02)
- [状态转移与请求竞态](#k03)
- [接口结果与UI状态分离](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 从多个Boolean到合法状态

```ts
type RequestState<T> =
  | { kind: 'idle' }
  | { kind: 'loading'; requestId: number }
  | { kind: 'success'; requestId: number; data: T }
  | { kind: 'error'; requestId: number; message: string };

const state: RequestState<string[]> = { kind: 'success', requestId: 1, data: [] };
```

如果分开保存loading、error和可选data，就容易出现“还在加载却已成功”等难解释组合。联合为各状态规定必需字段：success必须有data，error必须有message，idle不携带伪造成功数据。

它提高静态表达能力，但不能阻止外部JSON或any构造非法值，也不自动约束事件发生顺序。

<a id="k02"></a>

### 2. 判别字段与穷尽读取

```ts
type State =
  | { kind: 'loading' }
  | { kind: 'success'; data: string[] }
  | { kind: 'error'; message: string };
function render(state: State): string {
  switch (state.kind) {
    case 'loading': return '加载中';
    case 'success': return state.data.join(',');
    case 'error': return state.message;
    default: {
      const impossible: never = state;
      throw new Error(String(impossible));
    }
  }
}
console.log(render({ kind: 'success', data: ['A'] }));
```

kind必须是可区分的字面量，编译器才好把某分支的数据收窄。不要在收窄前把判别字段与数据拆成不再关联的独立变量，或者用as强行读取当前状态没有的字段。

新增成员后，never检查帮助发现遗漏；UI要决定新增状态怎样显示，而不只是让类型检查通过。

<a id="k03"></a>

### 3. 状态转移与请求竞态

```ts
type State = { kind: 'idle' } | { kind: 'loading'; id: number } |
  { kind: 'success'; id: number; data: string };
type Action = { type: 'start'; id: number } |
  { type: 'resolve'; id: number; data: string };

function reduce(state: State, action: Action): State {
  if (action.type === 'start') return { kind: 'loading', id: action.id };
  if (state.kind !== 'loading' || state.id !== action.id) return state;
  return { kind: 'success', id: action.id, data: action.data };
}
let state: State = { kind: 'idle' };
state = reduce(state, { type: 'start', id: 2 });
state = reduce(state, { type: 'resolve', id: 1, data: 'old' });
console.log(state.kind); // loading
```

联合类型保证返回的状态形状合法，id比较保证迟到结果不能提交；两者处理不同问题。还需设计失败、取消、重试和部分数据状态，不能认为加了kind就已经解决竞态。

更严格的状态机可将允许事件和转换表关联，复杂异步业务还要持久化版本、幂等和服务端状态。

<a id="k04"></a>

### 4. 接口结果与UI状态分离

接口`Result<T>`可表达成功数据或业务错误；UI状态还可能有idle、loading、stale、cancelled等。不要把网络响应原封不动当全部界面状态，也不要让每个组件再自由增加互相冲突的Boolean。

流式场景还需要区分开始、接收增量、完成、停止和异常中断，并绑定messageId/requestId。类型建模应贴合协议，而不是用一个大对象让所有属性可选。

测试包括每条合法转移、非法转移保持或拒绝、过时事件、取消后完成和新增联合成员的穷尽检查。

<a id="summary"></a>

## 三、知识小结

判别联合约束状态形状，状态机约束转换，版本ID约束事件归属。三者一起帮助构建可靠UI，但外部数据验证和服务端权威状态仍不可省略。

参考：[TypeScript Handbook](https://www.typescriptlang.org/docs/handbook/intro.html)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="ts09-01"></a>

### TS09-01 [P0·原理] 判别联合为什么比全部可选字段更好？

**回答：** 它把状态和必需数据绑定，success分支才能访问data，error分支必须有message，减少静态可表达的矛盾组合。全可选对象则让调用方处处猜字段是否有效。

对应讲解：[从多个Boolean到合法状态](#k01)。

<a id="ts09-02"></a>

### TS09-02 [P0·编码] 如何检查所有状态都被处理？

**回答：** 根据字面量判别字段switch，在default把剩余值赋给never或传给assertNever。新增状态未处理时编译器会提示，但运行时输入仍需要校验。

对应讲解：[判别字段与穷尽读取](#k02)。

<a id="ts09-03"></a>

### TS09-03 [P1·原理] 判别联合能自动防止旧请求覆盖吗？

**回答：** 不能，它保证形状而不保证时序。要在提交前核对requestId或版本，并定义取消、失败和重试转移。合法success对象也可能属于旧请求。

对应讲解：[状态转移与请求竞态](#k03)。

<a id="ts09-04"></a>

### TS09-04 [P1·工程取舍] 为什么接口Result和UI状态不能总共用？

**回答：** 接口表达一次调用结局，UI还涉及加载前、部分结果、取消和陈旧数据等生命周期。可以建立映射，但不应把所有层的责任混成一个属性全可选的大对象。

对应讲解：[接口结果与UI状态分离](#k04)。
