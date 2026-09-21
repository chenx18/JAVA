# 第十六章 MySQL：SQL 与 Index

## 一、本章具体知识点

- SELECT
- JOIN
- GROUP BY
- HAVING
- subquery
- index
- B+Tree
- composite index
- covering index
- EXPLAIN
- slow query

## 二、各知识点详细解释

B+Tree 索引通过有序结构减少大量无效扫描。复合索引通常遵守最左前缀原则的查询可用性规律，但实际是否使用还要看优化器统计信息、查询条件和数据分布。

覆盖索引意味着查询所需列可以直接从索引结构获得，减少回表。

`EXPLAIN` 用来分析执行计划，面试时要真正会看 type、possible_keys、key、rows、Extra 等信息。

## 三、本章面试题与答案

### 题：为什么索引不是越多越好？

**答案：**

索引会占用磁盘和内存，也会增加 INSERT/UPDATE/DELETE 的维护成本；优化器还要选择执行计划。索引应该围绕实际查询模式设计，而不是所有列都加索引。

### 题：什么是最左前缀？

**答案：**

对复合索引 `(a,b,c)`，查询能否有效利用索引通常要求从最左列开始形成可用前缀，例如 `a`、`a,b`、`a,b,c`。具体是否能继续利用到后续列，还取决于范围条件、排序、优化器等实际情况。

---
