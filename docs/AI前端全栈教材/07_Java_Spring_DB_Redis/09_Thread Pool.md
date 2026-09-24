# 09 线程池、Future 与异步预算

线程池把执行资源、排队和拒绝集中管理。生产设计要回答任务最多占多少线程、队列能积压多少、超时取消和关闭如何发生。

## 一、本章目录

- [ThreadPoolExecutor 的接收过程](#k01)
- [明确有界资源的示例](#k02)
- [拒绝、结果与取消](#k03)
- [CompletableFuture、隔离与虚拟线程](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. ThreadPoolExecutor 的接收过程

典型过程是核心线程不足时创建；核心达到后尝试入队；队列满且未达最大线程数时增线程；仍无法接收则执行拒绝策略。corePoolSize、maximumPoolSize、keepAliveTime、队列、ThreadFactory和RejectedExecutionHandler共同决定行为。

无界队列会让最大线程数在许多情况下难以发挥扩容限制作用，并可能导致内存和等待积压。Executors便捷工厂不是一律禁止，但使用前需理解其具体线程和队列配置。

<a id="k02"></a>

### 2. 明确有界资源的示例

```java
import java.util.concurrent.*;
public class Example {
    public static void main(String[] args) throws Exception {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
            2, 4, 30, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(16),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
        );
        try {
            Future<Integer> result = pool.submit(() -> 21 * 2);
            System.out.println(result.get(1, TimeUnit.SECONDS)); // 42
        } finally {
            pool.shutdown();
            if (!pool.awaitTermination(2, TimeUnit.SECONDS)) pool.shutdownNow();
        }
    }
}
```

示例只展示参数与关闭，线程数应根据CPU/阻塞比例、下游连接数和延迟目标测量。不能对所有服务套“CPU核数+1”或“2N”公式，尤其数据库连接与远端限流可能更早成为瓶颈。

<a id="k03"></a>

### 3. 拒绝、结果与取消

AbortPolicy明确拒绝并抛异常，CallerRunsPolicy可把压力转回提交方但会改变其延迟/线程语义，Discard类策略可能静默丢任务，重要业务不应不加分析采用。

execute与submit的异常可观察路径不同，submit的失败通过Future结果传递，若从不get或观察可能漏报。get超时只停止调用者等待，不自动证明任务已取消；cancel(true)仍依赖中断协作。

任务应带身份、截止时间、合理重试和幂等语义，过期排队任务不应拿到线程后继续做已无价值操作。

<a id="k04"></a>

### 4. CompletableFuture、隔离与虚拟线程

thenApply变换值，thenCompose展开下一异步结果，thenCombine组合独立结果；Async变体可能使用默认或指定executor。阻塞工作不要无意识压进公共池，与其他业务争资源。

线程上下文和事务不会因为CompletableFuture链存在就自动正确传播，必须显式设计。不同下游可用独立资源预算防止一个慢服务拖垮全部任务。

Java21+虚拟线程降低大量阻塞任务的线程承载成本，但不消除数据库连接、内存、CPU和限流约束；本系列Java17基础示例不直接依赖它。

<a id="summary"></a>

## 三、知识小结

线程池管并发与排队，Future管结果等待，取消是协作，虚拟线程也有外部资源上限。参数、拒绝、监控和关闭一起构成完整策略。

参考：[Java 17 API](https://docs.oracle.com/en/java/javase/17/docs/api/)；[Java Learning](https://dev.java/learn/)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="java09-01"></a>

### JAVA09-01 [P0·原理] 线程池为何不是简单省去new Thread？

**回答：** 它还控制并发、排队、拒绝和资源生命周期，避免无限线程或任务积压。复用只是其中一部分，生产还需延迟和下游预算。

对应讲解：[ThreadPoolExecutor 的接收过程](#k01)。

<a id="java09-02"></a>

### JAVA09-02 [P1·原理] 无界队列为什么可能让maximumPoolSize不起作用？

**回答：** 核心线程满后任务通常先入队，队列一直可接收就不触发队列满后的扩线程路径。结果可能是长等待和内存积压，而非按最大线程数扩容。

对应讲解：[ThreadPoolExecutor 的接收过程](#k01)。

<a id="java09-03"></a>

### JAVA09-03 [P1·工程取舍] Future.get超时后应做什么？

**回答：** 按契约决定取消、查询状态或返回任务ID，不能认为任务已自动停止。取消需任务响应，中途副作用还要幂等/补偿，异常和资源也要被观察清理。

对应讲解：[拒绝、结果与取消](#k03)。

<a id="java09-04"></a>

### JAVA09-04 [P1·工程取舍] 虚拟线程是否意味着可以无限并发请求数据库？

**回答：** 不能，数据库连接和容量仍有限，虚拟线程不解决CPU、内存和下游压力。需要独立并发/速率预算，并按Java版本验证。

对应讲解：[CompletableFuture、隔离与虚拟线程](#k04)。

<a id="java09-05"></a>

### JAVA09-05 [P1·工程取舍] 线程数为什么不能只按CPU核数套公式？

**回答：** 任务阻塞比例、数据库连接、远端限流、内存和延迟目标都限制并发。核心数只是输入之一，需要有界队列与监控，在真实负载下调优。

对应讲解：[明确有界资源的示例](#k02)。
