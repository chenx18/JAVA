# 06 Java IO、NIO 与 Stream

IO中的流负责输入输出，Stream API负责序列计算，CompletableFuture负责异步结果组合。名字相近但职责不同，不应混成同一种“流”。

## 一、本章目录

- [字节、字符与编码](#k01)
- [NIO 的 Buffer、Channel 与 Selector](#k02)
- [Stream API 的惰性流水线](#k03)
- [异步结果与背压区别](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 字节、字符与编码

InputStream/OutputStream处理字节，Reader/Writer处理字符，InputStreamReader/OutputStreamWriter按charset连接二者。文本读写应显式编码，避免依赖机器默认值；缓冲可减少小块系统调用。

Files提供readString/writeString、newBufferedReader、copy/move、walk等便利操作，仍要处理大小、路径、编码和资源关闭。readAllBytes/readString一次加载大文件可能耗尽内存，应按规模选择流式处理。

文件路径不是用户字符串随意拼接即可安全访问，需限定根目录、规范化并检查授权；上传下载还要约束大小与类型。

<a id="k02"></a>

### 2. NIO 的 Buffer、Channel 与 Selector

ByteBuffer通过position、limit、capacity管理读写状态，flip准备从刚写入数据中读取，clear准备重新写入但不等于安全擦除底层字节。Channel表达通道，部分通道支持非阻塞模式与Selector多路选择。

NIO不等于所有API自动非阻塞，也不等于一定比传统缓冲IO更快。直接缓冲和映射文件有各自内存与生命周期成本，选择取决于数据规模和访问模式。

<a id="k03"></a>

### 3. Stream API 的惰性流水线

```java
import java.util.*;
public class Example {
    public static void main(String[] args) {
        List<String> names = List.of(" Alice ", "", "Bob");
        List<String> result = names.stream()
            .map(String::trim)
            .filter(value -> !value.isEmpty())
            .sorted()
            .toList();
        System.out.println(result); // [Alice, Bob]
    }
}
```

map/filter等中间操作通常惰性，终止操作触发消费；Stream通常只消费一次。reduce、collect、groupingBy适合不同聚合，副作用应谨慎，peek不是可靠替代业务写入流程。

parallelStream使用并行机制但不保证更快，阻塞IO、共享可变状态和公共线程池竞争可能更糟。先保持计算纯、数据可分割，再测收益。

<a id="k04"></a>

### 4. 异步结果与背压区别

CompletableFuture提供thenApply/thenCompose/thenCombine/exceptionally等组合；返回Future不等于线程自动可无限创建，也不等于一个可持续消费并自动背压的流协议。

IO读取、Stream计算、异步结果和网络SSE/Reactive Streams是不同层。大数据处理需控制内存、线程池和消费者速度；不要先readAllBytes再stream()就声称实现了流式低内存。

<a id="summary"></a>

## 三、知识小结

字节/字符IO看编码与资源，NIO看缓冲状态和通道模型，Stream看惰性计算，Future看异步结局。按实际瓶颈选择，不靠相同名称类比。

参考：[Java Learning](https://dev.java/learn/)；[Java 17 API](https://docs.oracle.com/en/java/javase/17/docs/api/)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="java06-01"></a>

### JAVA06-01 [P0·基础] Java IO Stream和集合Stream一样吗？

**回答：** 不是，前者传递字节或字符，后者表达序列上的计算流水线。CompletableFuture又是异步结果机制，三者可组合但职责不同。

对应讲解：[Stream API 的惰性流水线](#k03)。

<a id="java06-02"></a>

### JAVA06-02 [P1·原理] ByteBuffer.flip和clear分别做什么？

**回答：** flip调整读写位置使刚写的数据可被读取，clear为再次写入重置状态，但不保证清除底层内容。理解position/limit比记方法名更重要。

对应讲解：[NIO 的 Buffer、Channel 与 Selector](#k02)。

<a id="java06-03"></a>

### JAVA06-03 [P1·工程取舍] parallelStream一定更快吗？

**回答：** 不一定，任务粒度、拆分成本、共享状态、阻塞IO和公共线程池竞争都会影响。先用适合并行的纯计算，再在真实数据和资源条件下测试。

对应讲解：[Stream API 的惰性流水线](#k03)。

<a id="java06-04"></a>

### JAVA06-04 [P0·工程取舍] 读大文件为何不直接readAllBytes？

**回答：** 它会把全部内容放进内存，可能造成峰值和GC压力。应按块处理并显式编码与关闭资源，后续计算也要保持有界，而非仅把已加载数组包装成Stream。

对应讲解：[字节、字符与编码](#k01)。

<a id="java06-05"></a>

### JAVA06-05 [P1·原理] CompletableFuture能直接解决持续流的背压吗？

**回答：** 不能，它主要组合异步结局，不自动管理不断产生数据时的消费速率。IO、Stream计算、异步结果和Reactive Streams等是不同层，仍需线程、缓冲和生产消费协议。

对应讲解：[异步结果与背压区别](#k04)。
