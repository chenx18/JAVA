# 第十二章 Spring IOC / DI

## 一、本章具体知识点

- Bean
- ApplicationContext
- Dependency Injection
- constructor injection
- BeanDefinition
- component scanning
- configuration
- lifecycle
- scope

## 二、各知识点详细解释

Spring IOC 的核心是：对象创建与依赖管理交给容器，而业务代码声明自己依赖什么。

推荐构造器注入：

```java
@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }
}
```

容器启动时解析 BeanDefinition，创建实例、注入依赖并执行生命周期回调。

## 三、本章面试题与答案

### 题：IOC 是什么？

**答案：**

IOC 是控制反转，把对象创建、组装和生命周期管理的控制权交给容器。DI 是 IOC 的一种实现方式，业务对象通过构造器等方式声明依赖，由 Spring 容器负责提供。

### 题：为什么推荐构造器注入？

**答案：**

构造器注入能表达对象创建所必须的依赖，使依赖不可遗漏；字段也可以声明为 final，方便测试和不可变设计，同时避免对象处于“半初始化”状态。

---
