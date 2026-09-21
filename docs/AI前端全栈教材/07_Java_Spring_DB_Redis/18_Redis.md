# 第十八章 Redis

## 一、本章具体知识点

- String
- Hash
- List
- Set
- ZSet
- TTL
- cache
- distributed lock
- rate limit
- counter
- pub/sub
- stream

## 二、各知识点详细解释

Redis 是内存数据存储系统，常用于缓存、计数、集合运算、限流、分布式协作等。

### Cache Aside

常见策略：

```text
Read
→ Cache hit → return
→ miss → DB → set cache

Write
→ update DB
→ invalidate cache
```

更新顺序和一致性必须结合业务分析。

## 三、本章面试题与答案

### 题：为什么缓存通常先更新数据库再删除缓存？

**答案：**

一种常见 Cache Aside 写策略是先完成数据库写入，再删除旧缓存，避免把缓存更新成可能尚未最终成功的状态。对于高并发还需要考虑并发读写、双写问题、延迟双删、消息队列或版本号等方案，不能把某一种顺序说成绝对正确。

### 题：Redis 分布式锁需要注意什么？

**答案：**

至少要有唯一 token、过期时间和释放时校验 token，避免误删别人的锁；还要考虑业务执行时间超过 lease、网络分区和锁服务故障。严格的分布式一致性场景不能只依赖一个简单 SET NX EX 就结束。
