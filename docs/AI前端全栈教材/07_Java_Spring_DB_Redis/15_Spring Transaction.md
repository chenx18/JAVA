# 第十五章 Spring Transaction

## 一、本章具体知识点

- transaction
- ACID
- isolation
- propagation
- rollback
- @Transactional
- proxy
- self-invocation
- optimistic/pessimistic locking

## 二、各知识点详细解释

事务提供一组操作的原子提交或回滚语义，但具体隔离级别、数据库引擎和 Spring propagation 会影响行为。

Spring 的 `@Transactional` 通常通过 AOP proxy 实现，因此同类内部自调用同样可能导致事务代理不生效。

## 三、本章面试题与答案

### 题：@Transactional 为什么有时失效？

**答案：**

常见原因之一是调用没有经过 Spring proxy，例如同一个类内部通过 `this.method()` 自调用；另外还有异常类型、事务管理器、方法可见性、数据库引擎和代理配置等因素，需要结合实际配置排查。

---
