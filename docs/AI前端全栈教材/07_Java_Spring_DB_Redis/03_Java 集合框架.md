# 第三章 Java 集合框架

## 一、本章具体知识点

- Collection
- List
- Set
- Map
- ArrayList
- LinkedList
- HashSet
- TreeSet
- HashMap
- TreeMap
- Queue
- Deque

## 二、各知识点详细解释

集合框架解决的是高频的数据组织需求。选择集合要根据访问模式：

```text
需要按索引快速读取 → ArrayList
需要唯一元素 → Set
键值映射 → Map
需要队列/双端队列 → Queue / Deque
排序树结构 → TreeMap / TreeSet
```

LinkedList 在理论上的中间插入删除并不意味着工程上总是比 ArrayList 快，因为真实成本还包含节点分配、CPU cache locality 等因素。

## 三、本章面试题与答案

### 题：ArrayList 和 LinkedList 怎么选？

**答案：**

ArrayList 基于动态数组，随机访问快、内存局部性好，是多数普通列表场景默认选择。LinkedList 基于链表，只有在已经持有节点位置且频繁在中间修改时才有潜在优势，但实际项目需要结合访问模式和基准测试决定。

---
