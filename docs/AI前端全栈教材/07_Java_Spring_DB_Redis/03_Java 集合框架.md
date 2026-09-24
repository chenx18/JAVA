# 03 Java 集合框架与选型

集合选择首先看访问模式、顺序、唯一性和并发要求。接口描述行为，具体实现决定复杂度与成本，不应只背数组和链表的抽象优缺点。

## 一、本章目录

- [Collection、List、Set、Map 与队列](#k01)
- [ArrayList、LinkedList 与遍历](#k02)
- [顺序、排序与不可变集合](#k03)
- [常用操作与并发边界](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. Collection、List、Set、Map 与队列

List保留序列和重复元素，Set表达成员唯一性，Map表达键值关系且不继承Collection，Queue/Deque提供队列和双端操作。泛型声明元素契约，集合仍需考虑null、可变性和线程安全。

```java
import java.util.*;
public class Example {
    public static void main(String[] args) {
        List<String> names = new ArrayList<>(List.of("A", "B"));
        names.add("A");
        Set<String> unique = new LinkedHashSet<>(names);
        Deque<String> queue = new ArrayDeque<>(unique);
        System.out.println(queue.removeFirst()); // A
        System.out.println(queue.removeFirst()); // B
    }
}
```

ArrayDeque常适合栈/队列，不允许null；并发或阻塞队列另选对应容器，不能因接口都是Queue就认为语义相同。

<a id="k02"></a>

### 2. ArrayList、LinkedList 与遍历

ArrayList动态数组擅长随机访问和紧凑存储，尾部追加摊还成本较好，中间插入删除涉及移动。LinkedList按索引定位仍需遍历，节点分配和缓存局部性也有代价，只有已在合适迭代位置等场景才可能受益。

Iterator/ListIterator定义遍历与适当修改方式，增强for中直接修改结构可能触发ConcurrentModificationException；这是尽力而为的错误发现机制，不是线程安全保证。

subList常是原列表视图而非独立副本，父子结构修改相互影响并有约束。需要独立结果时显式复制。

<a id="k03"></a>

### 3. 顺序、排序与不可变集合

HashSet/HashMap不保证业务需要的迭代顺序，LinkedHashSet/LinkedHashMap保留相应顺序，TreeSet/TreeMap按比较器组织。比较器与equals不一致时，去重和排序可能与直觉不同。

List.of/Set.of/Map.of建立不可修改集合并有null/重复元素等限制；Collections.unmodifiableList是包装视图，底层若经其他引用变化，视图仍可能观察变化。Java16+ Stream.toList返回不可修改列表，而Collectors.toList不应被当作具体实现与可变性的永久保证。

Comparator.comparing、thenComparing和显式null顺序可表达业务排序，不能用相减处理所有整数比较以免溢出。

<a id="k04"></a>

### 4. 常用操作与并发边界

List常用get/set/add/remove/contains/size，Map常用get/put/containsKey/getOrDefault/computeIfAbsent/merge，Set常用add/remove/contains及集合运算。注意`List<Integer>`.remove(1)可能按索引删除，而remove(Integer.valueOf(1))按对象值删除。

普通集合不是自动线程安全，Collections.synchronizedX包装仍需注意复合操作与迭代同步，ConcurrentHashMap等提供不同并发契约。选择应同时考虑操作原子性、遍历一致性和null规则。

<a id="summary"></a>

## 三、知识小结

按序列、唯一、映射、队列选接口，再按顺序、访问、可变性与并发选实现。视图不等于副本，单个方法安全不等于复合流程原子。

参考：[Java Learning](https://dev.java/learn/)；[Java 17 API](https://docs.oracle.com/en/java/javase/17/docs/api/)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="java03-01"></a>

### JAVA03-01 [P0·工程取舍] ArrayList和LinkedList怎么选？

**回答：** 多数普通列表可先考虑ArrayList的随机访问和局部性；LinkedList按索引仍需遍历，节点开销大，不能只凭中间插入理论复杂度说更快。按真实操作模式与基准判断。

对应讲解：[ArrayList、LinkedList 与遍历](#k02)。

<a id="java03-02"></a>

### JAVA03-02 [P0·原理] unmodifiableList和独立不可变副本一样吗？

**回答：** 不一样，它通常是包装视图，底层通过其他引用修改仍可被观察。需要独立快照要复制，并明确元素本身是否也可变。

对应讲解：[顺序、排序与不可变集合](#k03)。

<a id="java03-03"></a>

### JAVA03-03 [P1·基础] `List<Integer>`.remove为什么容易误删？

**回答：** 存在按索引和按对象值的重载，整数实参可能选择索引版本。按值删除要明确包装值或使用合适谓词，理解重载选择比猜运行结果可靠。

对应讲解：[常用操作与并发边界](#k04)。

<a id="java03-04"></a>

### JAVA03-04 [P1·原理] ConcurrentModificationException能证明线程安全问题被防住吗？

**回答：** 不能，fail-fast主要用于尽早发现某些结构修改，且不是绝对保证。线程安全需要合适容器、同步与复合操作设计。

对应讲解：[ArrayList、LinkedList 与遍历](#k02)。

<a id="java03-05"></a>

### JAVA03-05 [P0·基础] List、Set、Map和Deque分别表达什么？

**回答：** List表达有序可重复序列，Set表达唯一成员，Map表达键值且不继承Collection，Deque表达双端队列。再按顺序、null、可变性和并发选择具体实现。

对应讲解：[Collection、List、Set、Map 与队列](#k01)。
