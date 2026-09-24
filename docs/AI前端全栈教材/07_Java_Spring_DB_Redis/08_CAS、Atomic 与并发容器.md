# 08 CAS、Atomic 与并发容器

原子类和并发容器提供特定操作的安全机制，但不会让任意多步业务自动成为事务。先理解操作边界，再分析竞争与一致性要求。

## 一、本章目录

- [CAS 与 ABA](#k01)
- [AtomicInteger、AtomicReference 与 LongAdder](#k02)
- [ConcurrentHashMap 与复合操作](#k03)
- [竞争成本与选型](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. CAS 与 ABA

compareAndSet(expected,next)仅在当前值符合预期时更新，失败可重读再尝试。它避免某些互斥锁路径，但竞争激烈时重试可能消耗CPU，算法是否lock-free也需要整体分析。

ABA指值A→B→A后只比较最终值无法知道中间变化；若业务依赖版本历史，可用AtomicStampedReference等携带版本标记。不是每个计数场景都需要解决ABA，也不是有版本号就解决所有资源生命周期问题。

<a id="k02"></a>

### 2. AtomicInteger、AtomicReference 与 LongAdder

```java
import java.util.concurrent.atomic.AtomicInteger;
public class Example {
    public static void main(String[] args) throws InterruptedException {
        AtomicInteger count = new AtomicInteger();
        Runnable task = () -> { for (int i = 0; i < 1000; i++) count.incrementAndGet(); };
        Thread a = new Thread(task), b = new Thread(task);
        a.start(); b.start(); a.join(); b.join();
        System.out.println(count.get()); // 2000
    }
}
```

getAndIncrement与incrementAndGet返回旧/新值不同，updateAndGet等更新函数可能在竞争重试中执行多次，应保持无副作用。AtomicReference可原子替换对象引用，不会自动保护对象内部后续可变字段。

LongAdder分散计数热点，适合高并发统计，但sum不是对所有并发更新的严格原子快照，不适合直接替代每次需要唯一递增值的AtomicLong。

<a id="k03"></a>

### 3. ConcurrentHashMap 与复合操作

ConcurrentHashMap支持并发访问和相应原子方法，不接受null键值以减少并发缺失判断歧义。get后if再put仍是多个操作，应按需求使用putIfAbsent、compute、merge等。

compute回调应短小，避免慢IO或递归修改导致阻塞/异常复杂化。遍历通常具有弱一致性，不是某一时刻完整冻结快照；size在并发下也不能当所有业务条件的精确事务判断。

线程安全容器内放可变User对象，并不自动保护User字段。跨多个key的一致性仍需更高层同步或数据库事务。

<a id="k04"></a>

### 4. 竞争成本与选型

低锁/无锁并不等于没有等待成本；CAS失败、自旋、缓存行争用和对象分配都需测量。伪共享是独立字段碰巧共享缓存行造成干扰，属于硬件/布局层优化，不应在无证据时先堆填充字段。

先确定需要精确序列、统计计数、单key原子更新还是多对象一致性，再选Atomic、Adder、ConcurrentMap或锁。可维护的正确方案优先，再用JMH等合理基准验证热点。

<a id="summary"></a>

## 三、知识小结

CAS只承诺一次条件更新，Atomic定义单值操作，Adder偏统计吞吐，并发Map提供集合级契约。复合业务、内部可变值与快照一致性仍需设计。

参考：[Java 17 API](https://docs.oracle.com/en/java/javase/17/docs/api/)；[Java Learning](https://dev.java/learn/)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="java08-01"></a>

### JAVA08-01 [P1·原理] CAS的主要限制是什么？

**回答：** 可能有ABA、竞争重试成本和多变量一致性问题。是否需要版本戳取决于业务语义；复杂跨对象状态常更适合锁或事务，而非不断叠CAS。

对应讲解：[CAS 与 ABA](#k01)。

<a id="java08-02"></a>

### JAVA08-02 [P1·原理] AtomicReference里的对象字段也是原子的吗？

**回答：** 不是，它保证引用相关操作的原子性，对象内部字段后续修改仍需不可变设计或同步。原子容器不自动递归赋予线程安全。

对应讲解：[AtomicInteger、AtomicReference 与 LongAdder](#k02)。

<a id="java08-03"></a>

### JAVA08-03 [P1·工程取舍] LongAdder可以生成严格递增ID吗？

**回答：** 不适合作为同一契约替代，sum不是并发更新的精确原子快照，它偏统计吞吐。需要每次原子取序列用适合机制，跨进程唯一性又是另一问题。

对应讲解：[AtomicInteger、AtomicReference 与 LongAdder](#k02)。

<a id="java08-04"></a>

### JAVA08-04 [P0·原理] ConcurrentHashMap的get-if-put安全吗？

**回答：** 每个方法并发安全不代表组合原子。应使用putIfAbsent/compute等匹配意图的操作，多key事务和value内部状态仍需另行同步。

对应讲解：[ConcurrentHashMap 与复合操作](#k03)。

<a id="java08-05"></a>

### JAVA08-05 [P1·工程取舍] 无锁算法是否一定比锁更快？

**回答：** 不一定，CAS重试、缓存行竞争和对象分配有成本，复杂状态也难维护。先选能表达原子边界的正确方案，再以适合负载基准比较，不能只凭lock-free名称判断。

对应讲解：[竞争成本与选型](#k04)。
