# 12 Spring IoC、DI 与 Bean 生命周期

Spring容器管理对象创建、依赖组装和生命周期，业务通过依赖契约协作。理解容器边界后，才能解释注入、作用域、代理和测试为什么有效。

## 一、本章目录

- [控制反转与依赖注入](#k01)
- [Bean 定义与注入选择](#k02)
- [作用域、生命周期与代理](#k03)
- [循环依赖、配置与测试](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 控制反转与依赖注入

IoC把对象创建与组装的控制交给容器，DI是通过构造器等方式提供依赖的实现思路。业务类依赖接口或明确职责，不在内部随意new完整下游图。

```java
public class Example {
    interface UserRepository { String findName(int id); }
    static class UserService {
        private final UserRepository repository;
        UserService(UserRepository repository) { this.repository = repository; }
        String name(int id) { return repository.findName(id); }
    }
    public static void main(String[] args) {
        UserService service = new UserService(id -> "Alice");
        System.out.println(service.name(1)); // Alice
    }
}
```

这是不依赖Spring的构造器注入示例，容器把组装步骤自动化，但设计原则本身并不属于某一个框架专有能力。

<a id="k02"></a>

### 2. Bean 定义与注入选择

@Component/@Service/@Repository等可参与扫描，@Configuration中的@Bean显式声明对象，ApplicationContext根据定义创建并组装。扫描范围、profile、条件配置和依赖版本影响某Bean是否存在。

同类型多个候选时用明确@Qualifier或@Primary等规则，不靠字段名巧合维持复杂系统。单构造器的组件通常无需额外@Autowired标注；构造器参数表达必需依赖，final字段更容易保证完整初始化和测试。

手动new出来的普通对象不自动获得容器注入、代理和生命周期管理，不能认为类上有注解就在哪儿创建都生效。

<a id="k03"></a>

### 3. 作用域、生命周期与代理

默认singleton通常指一个容器内的单实例，不等于全JVM所有容器只有一份，也不保证线程安全。prototype、request等作用域有不同生命周期，Web作用域还依赖相应环境。

Bean创建与依赖注入后会经历初始化和后处理等阶段，容器可能返回代理对象；销毁回调管理资源，但prototype等并非都由容器完整负责后续销毁。不要在单例Service字段存当前请求用户或临时表单。

@PostConstruct/@PreDestroy等注解包名与Spring Boot版本关联，Boot3使用Jakarta生态相关约定。

<a id="k04"></a>

### 4. 循环依赖、配置与测试

构造器循环依赖常在设计上说明职责相互缠绕；不要首先靠允许循环引用或延迟注入掩盖所有问题。可抽取协作服务、事件或更清晰边界。

@Configuration代理语义与@Bean方法调用有关，proxyBeanMethods设置会影响方法间调用是否走容器语义；明确通过参数注入依赖通常更直接。

单元测试可直接构造业务类并替换依赖，集成测试再验证容器、配置、代理和真实存储。启动成功不能证明所有业务线程安全或授权正确。

<a id="summary"></a>

## 三、知识小结

DI把业务与组装分开，Bean定义决定容器管理范围，作用域决定共享，代理和生命周期影响实际行为。构造器注入让依赖显式，测试分业务逻辑与容器集成。

参考：[Spring IoC Container](https://docs.spring.io/spring-framework/reference/core/beans.html)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="java12-01"></a>

### JAVA12-01 [P0·基础] IoC和DI是什么关系？

**回答：** IoC是控制转移的思想，DI通过向对象提供依赖实现这种组装方式。Spring管理Bean创建和生命周期，业务声明需要什么，而不是内部控制全部依赖实例。

对应讲解：[控制反转与依赖注入](#k01)。

<a id="java12-02"></a>

### JAVA12-02 [P0·工程取舍] 为什么推荐构造器注入？

**回答：** 它明确必需依赖，便于final字段和直接单元测试，减少对象半初始化状态。不是说所有其他注入都非法，而是默认更易验证契约。

对应讲解：[Bean 定义与注入选择](#k02)。

<a id="java12-03"></a>

### JAVA12-03 [P0·原理] Spring单例Bean自动线程安全吗？

**回答：** 不会，singleton主要定义容器内共享实例范围。可变字段仍会被并发请求访问，应保持无状态或采用正确同步，不把请求态放单例成员。

对应讲解：[作用域、生命周期与代理](#k03)。

<a id="java12-04"></a>

### JAVA12-04 [P1·原理] 类上有@Service，手动new也能获得AOP吗？

**回答：** 通常不能，容器负责注入和创建代理，手动new只是普通对象。调用是否经过容器提供的代理决定相关增强是否触发。

对应讲解：[Bean 定义与注入选择](#k02)。

<a id="java12-05"></a>

### JAVA12-05 [P1·工程取舍] 构造器循环依赖应怎样处理？

**回答：** 先检查职责是否互相缠绕，考虑抽取协作服务或重新划分边界。允许循环、延迟注入或自注入可能只掩盖设计，且生命周期与代理仍需集成验证。

对应讲解：[循环依赖、配置与测试](#k04)。
