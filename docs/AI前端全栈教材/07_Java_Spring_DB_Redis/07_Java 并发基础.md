# 07 Java 并发、可见性与同步

线程安全要同时考虑原子性、可见性和有序性。Java服务常同时处理多个请求，单例Service中的可变字段并不会因为被Spring管理就自动安全。

## 一、本章目录

- [线程、任务与共享状态](#k01)
- [原子性、可见性与 happens-before](#k02)
- [synchronized 与 Lock](#k03)
- [中断、取消与请求生命周期](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 线程、任务与共享状态

Runnable表达无直接返回值任务，Callable可返回值并抛异常，Future表示等待结果。Thread负责执行线程，Executor体系把任务提交与线程管理分离。

共享可变数据需要明确同步；局部变量引用的对象若被共享，也不能只因变量是局部就认为安全。不可变对象和避免共享通常比到处加锁更易推理。

ThreadLocal是线程关联的数据槽，不是通用缓存，也不自动跨线程传递。线程池会复用线程，请求结束应在finally中remove，避免上下文泄漏到后续任务。

<a id="k02"></a>

### 2. 原子性、可见性与 happens-before

原子性关注操作是否不可分割，可见性关注写入是否能被其他线程看到，有序性关注语言允许的执行/观察顺序。happens-before规定特定操作之间的可见与排序保证，不等同于墙钟时间上谁先运行。

锁释放与后续获得同一锁、volatile写与相应后续读、线程start/join等建立规定关系。安全发布对象时，还需避免构造未完成就把this暴露出去。

volatile保证特定可见和排序语义，但i++包含读、计算、写，仍可能丢更新。

<a id="k03"></a>

### 3. synchronized 与 Lock

```java
public class Example {
    static class Counter {
        private int value;
        synchronized void increment() { value++; }
        synchronized int value() { return value; }
    }
    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();
        Runnable task = () -> { for (int i = 0; i < 1000; i++) counter.increment(); };
        Thread a = new Thread(task), b = new Thread(task);
        a.start(); b.start(); a.join(); b.join();
        System.out.println(counter.value()); // 2000
    }
}
```

synchronized提供互斥及相应可见性，异常离开时自动释放。Lock API可提供tryLock、可中断获取与多个Condition等能力，手工lock后应try/finally unlock。

选择看需要的控制语义，不简单断言某种锁永远更快。多锁按一致顺序获取，减少临界区和不必要外部IO。

<a id="k04"></a>

### 4. 中断、取消与请求生命周期

interrupt是协作信号，不是强制安全终止线程。阻塞方法可能抛InterruptedException，处理时决定传播、结束或恢复中断标记，不应无理由吞掉继续执行。

Future.cancel(true)尝试中断，任务仍须响应。异步任务读取请求用户、事务或日志上下文时，需明确传播与清理；不要假设ThreadLocal自动跟随CompletableFuture到新线程。

测试用明确同步工具和超时验证结果，不靠sleep“应该差不多执行完”作为并发正确性证明。

<a id="summary"></a>

## 三、知识小结

并发先避免不必要共享，再为复合状态建立同步与可见性。volatile不是万能原子操作，中断不是强制杀线程，ThreadLocal必须随复用线程正确清理。

参考：[Java 17 API](https://docs.oracle.com/en/java/javase/17/docs/api/)；[Java Learning](https://dev.java/learn/)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="java07-01"></a>

### JAVA07-01 [P0·原理] volatile为什么不能保证i++安全？

**回答：** i++是读取、计算和写回的复合过程，多个线程可基于同一旧值写回，volatile的可见性和排序不让整体原子化。需要锁或适合的原子操作。

对应讲解：[原子性、可见性与 happens-before](#k02)。

<a id="java07-02"></a>

### JAVA07-02 [P0·基础] synchronized与Lock如何选择？

**回答：** 前者语言级互斥和自动释放简单明确，后者可提供超时、可中断和条件队列等更丰富控制。根据语义与测量选择，Lock使用需finally释放。

对应讲解：[synchronized 与 Lock](#k03)。

<a id="java07-03"></a>

### JAVA07-03 [P1·原理] ThreadLocal能直接当用户缓存吗？

**回答：** 它按线程存关联值，线程池复用会延长生命周期，未remove可能串上下文；异步切线程又不会自动传递。请求上下文需明确设置、传播和清理，缓存则按业务键与有效期管理。

对应讲解：[线程、任务与共享状态](#k01)。

<a id="java07-04"></a>

### JAVA07-04 [P1·工程取舍] 取消Future为什么任务还在执行？

**回答：** 取消或中断是协作请求，代码可能忽略中断、正在不可中断工作或已经提交副作用。任务必须按协议检查并清理，不能假设cancel撤销所有工作。

对应讲解：[中断、取消与请求生命周期](#k04)。
