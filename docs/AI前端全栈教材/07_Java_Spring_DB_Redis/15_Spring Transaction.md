# 15 Spring 事务、传播与回滚

事务把明确的一组数据库操作作为提交单元。先贯穿Controller→代理Service→Mapper→数据库，再理解注解配置、异常与跨资源边界。

## 一、本章目录

- [完整业务流程](#k01)
- [回滚与异常](#k02)
- [传播、隔离与资源](#k03)
- [代理、自调用与异步验证](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 完整业务流程

```text
Controller接收并验证请求
  → 调用容器提供的Service代理
  → 开启或加入事务
  → Mapper写主表
  → Mapper写同一事务资源内的日志/关联表
  → 正常返回提交；符合回滚规则的失败则回滚
  → 映射响应
```

日志若写同一数据库并使用同一事务资源，可与主表原子提交。外部HTTP、文件、邮件或另一数据库不自动加入普通本地事务，不是所有“写日志”都能一起回滚。

<a id="k02"></a>

### 2. 回滚与异常

常见默认对RuntimeException和Error回滚，checked异常未必默认回滚；可用rollbackFor指定，Spring版本和全局配置也可能改变默认策略。

```java
// spring-context：示意Service方法，需Spring管理并经代理调用。
@Transactional(rollbackFor = Exception.class)
public void createDepartment(DepartmentCreateRequest request) {
    Department entity = toEntity(request);
    departmentMapper.insert(entity);
    departmentLogMapper.insert(entity.getId(), "CREATE");
}

```

示例省略项目类型和Mapper实现，重点是边界。catch后吞异常可能让拦截器看到正常返回，内部失败又可能已标记rollback-only，最终出现UnexpectedRollbackException；必须结合实际事务状态验证。

<a id="k03"></a>

### 3. 传播、隔离与资源

REQUIRED通常加入已有事务或新建，REQUIRES_NEW挂起外层并建立独立事务，NESTED在支持条件下使用保存点。独立事务可能额外占连接，外层连接仍被持有，连接池不足可导致等待。

隔离级别由数据库和管理配置共同决定，readOnly通常是优化/提示契约，不普遍等于安全禁止写入。长事务持有锁和连接，避免包住用户等待、慢远程调用或无界处理。

缓存和消息一致性需outbox、事件或补偿等明确方案，不会因为数据库事务注解自动解决。

<a id="k04"></a>

### 4. 代理、自调用与异步验证

this自调用可能绕过内层注解，手动new也没有容器代理。private/final及可见性是否可增强按代理方式和版本判断。

普通事务上下文常与线程关联，异步线程不自动继承数据库资源。外层已有事务时，自调用内层SQL仍可能使用它，但内层自己的传播规则未执行。

验证要让第二次写失败再查主表是否回滚，覆盖checked异常、异常捕获、自调用、并发和传播。仅成功提交不能证明回滚正确。

<a id="summary"></a>

## 三、知识小结

代理建立事务边界，异常规则决定回滚，传播处理已有上下文，跨线程/资源不自动扩大原子性。测试必须包含中途失败。

参考：[Spring Transaction Annotations](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="java15-01"></a>

### JAVA15-01 [P0·原理] 主表成功、日志失败何时能一起回滚？

**回答：** 两次写在同一有效事务资源内，调用经代理且失败符合回滚规则。若日志是外部HTTP或独立事务，不能自动保证原子回滚。

对应讲解：[完整业务流程](#k01)。

<a id="java15-02"></a>

### JAVA15-02 [P0·基础] 默认所有异常都回滚吗？

**回答：** 常见默认是RuntimeException和Error，checked异常需相应配置；版本与全局策略可能调整，应明确项目实际规则。

对应讲解：[回滚与异常](#k02)。

<a id="java15-03"></a>

### JAVA15-03 [P1·原理] REQUIRES_NEW有什么代价？

**回答：** 独立事务需要额外资源，外层资源可能仍被挂起持有，连接池不足会等待。独立提交也改变一致性边界，不只是多加一个注解参数。

对应讲解：[传播、隔离与资源](#k03)。

<a id="java15-04"></a>

### JAVA15-04 [P1·工程取舍] 怎样验证事务有效？

**回答：** 制造中途失败并查数据，覆盖异常类型、捕获方式、代理路径、传播和并发。注解存在和正常返回都不足以证明失败行为。

对应讲解：[代理、自调用与异步验证](#k04)。
