# 事务 Transactional

## 1. 事务的完整开发流程

事务不是为了写一个单独的测试接口，而是服务于真实业务流程。

以“新增部门，同时写一条部门操作日志”为例：

```text
Controller 接请求
  ↓
Service 开事务 @Transactional
  ↓
Mapper 写 department
  ↓
Mapper 写 department_log
  ↓
如果中间异常，全部回滚
```

这句话要先记住：

> 事务用来保证一个业务方法里的多次数据库操作，要么全部成功，要么全部失败。

---

## 2. 为什么事务通常写在 Service 层

项目里常见分层：

```text
Controller：接收请求，返回响应
Service：组织业务流程，做校验，决定是否需要事务
Mapper：执行 SQL，操作数据库
```

事务一般写在 Service 层，因为 Service 才知道一个完整业务动作包含哪些步骤。

例如新增部门：

```text
1. 校验部门名称不能为空
2. 校验部门名称不能重复
3. 新增 department
4. 写 department_log
5. 返回新增结果
```

这些步骤组合在一起才是“新增部门”这个业务。

Mapper 只知道一条 SQL：

```text
insert department
insert department_log
```

它不知道这两条 SQL 是否必须一起成功，所以事务不应该主要写在 Mapper 层。

---

## 3. 什么时候需要事务

只查询数据，一般不需要事务：

```java
public DepartmentResponse getDepartmentById(int id) {
  ...
}
```

只做单表单次写入，有时也可以不写事务，但实际开发中为了统一和扩展，新增、修改、删除通常会考虑事务。

只要一个业务方法里出现多次数据库写操作，就应该优先考虑事务：

```text
新增部门 + 写日志
修改部门 + 写日志
删除部门 + 写日志
创建订单 + 扣库存 + 写支付记录
注册用户 + 初始化角色 + 写欢迎消息
```

---

## 4. 项目中的真实写法

假设有两个表：

```text
department
department_log
```

新增部门时，不单独写一个测试方法，而是让原来的业务方法本身具备事务能力。

```java
@Transactional
public DepartmentResponse createDepartment(DepartmentCreateRequest request) {
  if (request == null) {
    throw new IllegalArgumentException("department request cannot be null");
  }

  if (mapper.existsByName(request.getName())) {
    throw new IllegalArgumentException("department name already exists");
  }

  validateStatus(request.getStatus());

  Department department = new Department();
  department.setName(request.getName());
  department.setDescription(request.getDescription());
  department.setStatus(request.getStatus());

  mapper.insert(department);

  saveDepartmentLog(
      department.getId(),
      "CREATE_DEPARTMENT",
      "Create department: " + department.getName());

  return toResponse(department);
}
```

这里有两次数据库写操作：

```text
mapper.insert(department)
logMapper.insert(log)
```

只要后面写日志失败，前面新增的部门也会回滚。

---

## 5. 抽取日志方法

如果新增、修改、删除都要写日志，不要每个方法里重复 new 日志对象。

可以抽一个私有方法：

```java
private void saveDepartmentLog(Integer departmentId, String operation, String content) {
  DepartmentLog log = new DepartmentLog();
  log.setDepartmentId(departmentId);
  log.setOperation(operation);
  log.setContent(content);
  log.setCreateTime(LocalDateTime.now());

  logMapper.insert(log);
}
```

这样业务方法只关心：

```java
saveDepartmentLog(id, "UPDATE_DEPARTMENT", "Update department: " + name);
```

而不用每次重复创建日志对象。

---

## 6. 修改和删除也可以加事务

修改部门：

```java
@Transactional
public DepartmentResponse updateDepartment(int id, DepartmentUpdateRequest request) {
  // 1. 参数校验
  // 2. 判断数据是否存在
  // 3. 执行 update
  // 4. 查询更新后的数据
  // 5. 写修改日志
  // 6. 返回结果
}
```

删除部门：

```java
@Transactional
public void deleteDepartment(int id) {
  // 1. 参数校验
  // 2. 删除前先查询部门，因为删完后就查不到名称了
  // 3. 执行 delete
  // 4. 写删除日志
}
```

删除时有一个细节：

```java
Department department = mapper.findById(id);
```

要放在删除之前。

因为删除之后，数据库里已经没有这条部门记录了，如果日志里还想记录部门名称，就必须提前查出来。

---

## 7. 默认哪些异常会回滚

Spring 的 `@Transactional` 默认遇到运行时异常会回滚：

```text
RuntimeException
IllegalArgumentException
NullPointerException
自定义 RuntimeException 子类
```

例如：

```java
throw new RuntimeException("save log failed");
```

会触发回滚。

但普通受检异常默认不一定回滚：

```text
Exception
IOException
SQLException
```

如果希望所有异常都回滚，可以写：

```java
@Transactional(rollbackFor = Exception.class)
```

优先记住默认规则：

> RuntimeException 会回滚。

---

## 8. try catch 为什么可能导致不回滚

不推荐写法：

```java
@Transactional
public void createSomething() {
  try {
    mapper.insertA();
    mapper.insertB();
  } catch (Exception e) {
    e.printStackTrace();
  }
}
```

问题是：

```text
异常被 catch 吃掉了
Spring 感知不到异常
事务认为方法正常结束
所以不会回滚
```

如果确实要 catch，通常要继续抛出异常：

```java
@Transactional
public void createSomething() {
  try {
    mapper.insertA();
    mapper.insertB();
  } catch (Exception e) {
    throw new RuntimeException("create failed", e);
  }
}
```

---

## 9. 常见误区

### 9.1 事务不是写在 Controller

Controller 只负责接请求，不负责组织数据库一致性。

不推荐：

```java
@Transactional
@PostMapping("/add")
public ApiResponse<?> add(...) {
  ...
}
```

推荐：

```text
Controller 调 Service
Service 方法上加 @Transactional
```

### 9.2 事务不是写在 Mapper

Mapper 是 SQL 执行者，不是业务流程组织者。

一个业务可能调用多个 Mapper：

```text
DepartmentMapper
DepartmentLogMapper
UserMapper
RoleMapper
```

所以事务应该放在 Service 统一控制。

### 9.3 不要为了测试事务污染真实接口

学习时可以写测试方法模拟失败。

但真实业务代码里不应该出现：

```java
boolean fail
```

真实项目应该让业务方法本身完整：

```text
createDepartment = 新增部门 + 写新增日志
updateDepartment = 修改部门 + 写修改日志
deleteDepartment = 删除部门 + 写删除日志
```

---

## 10. 面试小题

如果面试问：

> 项目中如何使用事务？

可以这样回答：

```text
通常将事务放在 Service 层。
因为 Service 代表一个完整业务流程，里面可能会调用多个 Mapper。
比如新增部门时，不只是插入 department 表，还要插入 department_log 操作日志。
这两个操作必须一起成功，如果写日志失败，部门新增也应该回滚。
所以可以在 createDepartment 方法上加 @Transactional。
Spring 默认对 RuntimeException 回滚，如果 catch 了异常，要注意重新抛出，否则事务可能不会回滚。
```

---

## 11. 本节小结

现在不需要死背事务传播级别、隔离级别。

需要掌握：

```text
1. @Transactional 通常写在 Service 层
2. 多个数据库写操作要考虑事务
3. RuntimeException 默认回滚
4. catch 异常后不重新抛出，可能不会回滚
5. 新增/修改/删除 + 写日志，是很典型的事务场景
```

后面再深入：

```text
事务传播行为
事务隔离级别
脏读、不可重复读、幻读
事务失效场景
```

