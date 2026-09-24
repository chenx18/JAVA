# 04 HashMap 的哈希、冲突与扩容

HashMap先根据hash缩小候选范围，再用equals判断逻辑键。理解契约与访问过程，比孤立背树化阈值更能指导实际集合使用。

## 一、本章目录

- [hashCode 与 equals](#k01)
- [桶、碰撞与树化](#k02)
- [负载因子与扩容](#k03)
- [API、null 与并发](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. hashCode 与 equals

相等对象必须有相同hashCode，不同对象可以hash冲突。HashMap利用hash定位桶，在候选中再比较键；只重写equals不维护hashCode会破坏查找契约。

```java
import java.util.*;
public class Example {
    record UserKey(String tenant, String id) {}
    public static void main(String[] args) {
        Map<UserKey, String> names = new HashMap<>();
        names.put(new UserKey("t1", "u1"), "Alice");
        System.out.println(names.get(new UserKey("t1", "u1"))); // Alice
    }
}
```

record的组件等价和hash适合此类不可变键示例，但组件若引用可变对象，仍需考虑稳定性。键进入Map后修改参与equals/hashCode的字段，可能让它再也从原查找路径被找到。

<a id="k02"></a>

### 2. 桶、碰撞与树化

常见JDK8+实现使用数组桶与链/树结构，hash扰动和容量相关索引帮助分散键。碰撞多时链式查找成本上升，满足容量与节点条件时可树化改善最坏路径。

常见实现中的树化相关阈值8和最小容量64是实现细节，较小表可能先扩容而非立即树化。红黑树并不让所有操作无条件严格O(log n)，hash、比较和对象分布仍影响实际路径。

教学解释应先给“同桶多个候选”的访问过程，再讲为什么极端冲突值得切换结构。

<a id="k03"></a>

### 3. 负载因子与扩容

size与capacity不同，负载因子决定何时触发扩容阈值，常见默认0.75是时间空间折中而非普遍最佳常数。扩容会重新分配桶位置，有CPU与内存成本。

容量通常按2的幂组织，扩容时某条目可根据新增的高位决定保留旧索引或移到旧索引加旧容量。提前估算条目数有助于减少扩容，但过度预分配也浪费内存。

HashMap不保证业务顺序，扩容或版本变化更不能作为可依赖排序机制。

<a id="k04"></a>

### 4. API、null 与并发

HashMap允许一个null键及多个null值，get返回null既可能表示缺失也可能是真null值，需containsKey区分。putIfAbsent、computeIfAbsent、merge等有各自null语义和回调约束。

HashMap不是并发Map，多个线程无同步读写可产生错误；不能把单线程测试通过当线程安全证据。ConcurrentHashMap也不允许null键值，复合业务流程仍需正确原子操作或锁。

面试排错优先看键是否可变、equals/hashCode是否一致、是否错误依赖顺序、是否存在并发和缓存无限增长。

<a id="summary"></a>

## 三、知识小结

hash负责定位，equals确认身份，负载与扩容控制分布，树化处理部分极端冲突。稳定键与正确并发契约比记住几个阈值更重要。

参考：[Java Learning](https://dev.java/learn/)；[Java 17 API](https://docs.oracle.com/en/java/javase/17/docs/api/)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="java04-01"></a>

### JAVA04-01 [P0·原理] 为什么equals相等必须hashCode相同？

**回答：** HashMap先按hash找候选桶，如果逻辑相等对象落到不一致路径，就可能无法找到已有条目。相同hash不要求equals相等，因此碰撞后仍需比较。

对应讲解：[hashCode 与 equals](#k01)。

<a id="java04-02"></a>

### JAVA04-02 [P0·原理] 可变对象作为key有什么风险？

**回答：** 参与hashCode/equals的字段改变后，存储时桶位置与新查找值不一致，可能查不到或产生逻辑重复。优先用稳定不可变键或避免修改这些字段。

对应讲解：[hashCode 与 equals](#k01)。

<a id="java04-03"></a>

### JAVA04-03 [P1·基础] 为什么冲突多时会树化？

**回答：** 链式候选过多会退化，合适条件下树结构改善极端查找路径。阈值还与容量和JDK实现相关，不是任何桶到8个就无条件变树。

对应讲解：[桶、碰撞与树化](#k02)。

<a id="java04-04"></a>

### JAVA04-04 [P1·工程取舍] HashMap如何减少扩容成本，是否越大越好？

**回答：** 根据预期条目和负载合理初始化容量可减少重新分配，但过大浪费内存和遍历成本。结合数据规模和生命周期选择，不依赖默认或极大容量解决所有问题。

对应讲解：[负载因子与扩容](#k03)。

<a id="java04-05"></a>

### JAVA04-05 [P0·原理] HashMap.get返回null为什么不能直接断定没有键？

**回答：** HashMap允许null值，缺失和真实null都可能得到null，需containsKey区分。ConcurrentHashMap的null契约不同，不能把一种容器规则机械套给另一种。

对应讲解：[API、null 与并发](#k04)。
