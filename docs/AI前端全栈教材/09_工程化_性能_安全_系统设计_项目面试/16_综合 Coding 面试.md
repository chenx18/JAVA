# 16 综合 Coding：契约、实现与边界

Coding面试先明确输入、支持范围和结果，再实现与验证。本章提供新的树转换和限定JSON比较练习，并串联JavaScript专题中已有的异步、缓存和parser实现。

## 一、本章目录

- [实现前的检查路线](#k01)
- [异步与资源题](#k02)
- [树、拷贝与相等](#k03)
- [协议与框架最小模型](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 实现前的检查路线

先问输入是否可能为空、重复、乱序、循环或非法类型；输出是否保序、是否修改原数据、错误如何表示；再选择Map/Set/队列与复杂度。

写出一个最小正常例子后，主动增加能区分错误实现的边界。讲复杂度要区分预期查找成本、递归栈、输出空间和排序，不只说“用了Map就是O(1)”。

<a id="k02"></a>

### 2. 异步与资源题

并发池控制任务函数启动，Promise.all汇总结果不自动限制并发，retry需错误类别、预算和取消，防抖节流需this/参数/尾部行为。实现中同步throw和异步reject都要结算，避免一个失败让调度永久卡住。

可查阅[并发池与重试完整示例](<../01_JavaScript/02_核心知识/12_异步控制与任务调度.md>)、[防抖节流](<../01_JavaScript/02_核心知识/05_闭包与函数应用.md>)，面试回答仍需先复述契约，不能只说“看链接”。

<a id="k03"></a>

### 3. 树、拷贝与相等

列表转树需要处理乱序、重复ID、孤儿和环；先建索引，再验证关系，再连接节点。深拷贝还要保存源到副本映射，保留循环与共享引用，支持范围应明确。

深相等不是万能一句递归。Date、Map、Set、Symbol、描述符、原型和循环图都有不同语义；文末示例只接受无环、稠密、有限JSON数据树，并设置递归深度保护，不作为任意对象比较库。

<a id="k04"></a>

### 4. 协议与框架最小模型

SSE需要增量解码与残片缓冲，响应式模型需要依赖清理，EventEmitter需要once重入策略，LRU需容量和访问顺序。每个模型都应说明与完整标准或框架差异。

[流式parser与最新请求控制](<../01_JavaScript/02_核心知识/19_请求竞态与流式解析.md>)、[响应式示例](<../01_JavaScript/02_核心知识/08_Proxy与Reflect.md>)已有对应代码。验证关注空输入、异常、取消、重入和资源释放，不只最后一行输出。

<a id="summary"></a>

## 三、知识小结

编码按契约→结构→实现→边界→复杂度解释。能说清不支持什么，并用有效反例检验，胜过把简化代码称作完整原生实现。

参考：[OpenTelemetry Concepts](https://opentelemetry.io/docs/concepts/)；[MCP Specification](https://modelcontextprotocol.io/specification/2026-07-28)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="eng16-01"></a>

### ENG16-01 [P0·编码] 怎样写一个不会因拒绝卡住的并发池？

**回答：** 输入未启动任务函数，按上限领取索引，每个任务用try/catch/finally结算并继续补位，结果按输入位置存。同步throw和reject都要处理，取消区分排队与运行；不能把已启动Promise再放池里限制过去的启动。

对应讲解：[异步与资源题](#k02)。

<a id="eng16-02"></a>

### ENG16-02 [P1·编码] 把乱序父子列表转成树，并拒绝重复、孤儿和环。

**回答：** 下例先创建独立节点索引，验证父节点存在，再沿父链检测环，最后连接。完成集合避免重复遍历已验证链，期望时间与空间均为O(n)，不修改输入行。

```ts
type Row = { id: string; parentId: string | null; name: string };
type TreeNode = Row & { children: TreeNode[] };
function buildTree(rows: readonly Row[]): TreeNode[] {
  const nodes = new Map<string, TreeNode>();
  for (const row of rows) {
    if (!row.id || nodes.has(row.id)) throw new Error('duplicate or empty id');
    nodes.set(row.id, { ...row, children: [] });
  }
  for (const node of nodes.values()) {
    if (node.parentId !== null && !nodes.has(node.parentId)) throw new Error('orphan');
  }
  const complete = new Set<string>();
  for (const node of nodes.values()) {
    const chain = new Set<string>();
    let current: string | null = node.id;
    while (current !== null && !complete.has(current)) {
      if (chain.has(current)) throw new Error('cycle');
      chain.add(current);
      current = nodes.get(current)!.parentId;
    }
    for (const id of chain) complete.add(id);
  }
  const roots: TreeNode[] = [];
  for (const node of nodes.values()) {
    if (node.parentId === null) roots.push(node);
    else nodes.get(node.parentId)!.children.push(node);
  }
  return roots;
}
const result = buildTree([
  { id: 'child', parentId: 'root', name: 'Child' },
  { id: 'root', parentId: null, name: 'Root' }
]);
console.log(result[0]?.children[0]?.name); // Child
```

**追问与回答：** 根以parentId=null表示；重复ID、空ID、孤儿和环抛错。输入字段类型应先在外部边界验证。

对应讲解：[树、拷贝与相等](#k03)。

<a id="eng16-03"></a>

### ENG16-03 [P1·编码] 实现限定JSON数据树的深相等。

**回答：** 下例比较值、数组顺序和对象键集合，键顺序不影响对象相等。只约定无环稠密JSON数据、有限数值和普通字段，不支持任意类/Map/Set/访问器图；外部数据需先验证。

```ts
type JsonValue = null | boolean | number | string | JsonValue[] |
  { [key: string]: JsonValue };
function equalJson(a: JsonValue, b: JsonValue, depth = 0): boolean {
  if (depth > 100) throw new RangeError('too deep');
  if (a === b) return true;
  if (a === null || b === null || typeof a !== 'object' || typeof b !== 'object') return false;
  if (Array.isArray(a) || Array.isArray(b)) {
    if (!Array.isArray(a) || !Array.isArray(b) || a.length !== b.length) return false;
    return a.every((value, index) => {
      const other = b[index];
      return other !== undefined && equalJson(value, other, depth + 1);
    });
  }
  const keys = Object.keys(a);
  if (keys.length !== Object.keys(b).length) return false;
  return keys.every(key => {
    const left = a[key], right = b[key];
    return Object.hasOwn(b, key) && left !== undefined && right !== undefined &&
      equalJson(left, right, depth + 1);
  });
}
console.log(equalJson({ x: [1, 2] }, { x: [1, 2] })); // true
console.log(equalJson({ x: 1 }, { x: 2 })); // false
```

**追问与回答：** 深度保护避免无界递归，但不是完整循环图算法。-0与0按本例严格等值视为相同，特殊非JSON数值不在契约内。

对应讲解：[树、拷贝与相等](#k03)。

<a id="eng16-04"></a>

### ENG16-04 [P0·原理] 为什么SSE parser和深拷贝都不能只测正常输入？

**回答：** parser会遇到半字符、跨事件块、超长和中断，拷贝会遇到循环、共享引用与特殊对象。正常例子不能区分许多错误实现，应围绕边界和支持契约设计反例。

对应讲解：[协议与框架最小模型](#k04)。

<a id="eng16-05"></a>

### ENG16-05 [P0·编码] Coding题开始写之前应先确认哪些契约？

**回答：** 确认空值、重复、乱序、循环和非法类型，输出顺序、原数据是否可改、错误策略与复杂度目标。先给小例子再补反例，避免写完才发现支持范围与题目不同。

对应讲解：[实现前的检查路线](#k01)。
