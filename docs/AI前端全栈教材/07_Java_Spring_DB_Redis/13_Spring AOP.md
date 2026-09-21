# 第十三章 Spring AOP

## 一、本章具体知识点

- proxy
- advice
- pointcut
- join point
- aspect
- JDK proxy
- CGLIB-style subclass proxy
- transaction

## 二、各知识点详细解释

AOP 把横切关注点从业务主逻辑中抽离，例如日志、事务、权限、监控。

抽象结构：

```text
Client
→ Proxy
→ Advice
→ Target
```

Spring AOP 主要基于代理，因此某些情况下“同类内部自调用”不会经过代理，从而导致切面不生效。

## 三、本章面试题与答案

### 题：Spring AOP 为什么同类方法自调用可能失效？

**答案：**

因为 Spring AOP 常通过代理对象织入切面。外部调用走的是 proxy → target，而 target 内部通过 `this.xxx()` 调用自己时没有经过 proxy，因此相关 advice 没有被触发。

---
