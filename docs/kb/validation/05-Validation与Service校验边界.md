# 05-Validation与Service校验边界

Validation 和 Service 都可以做校验，但负责的事情不同。

---

## Validation 负责什么

Validation 负责基础参数合法性。

适合处理：

```text
字段不能为空
字符串不能全是空格
数字不能小于 1
长度不能超过限制
邮箱格式是否正确
```

例如：

```java
@NotBlank(message = "部门名称不能为空")
private String name;
```

这是字段规则，不需要进入业务逻辑。

---

## Service 负责什么

Service 负责业务规则。

适合处理：

```text
ID 对应的数据是否存在
名称是否重复
当前用户是否有权限操作
当前状态是否允许修改
删除前是否有关联数据
```

例如：

```java
if (mapper.findById(id) == null) {
  throw new DepartmentNotFoundException(id);
}
```

这是业务规则，应该放在 Service。

---

## 常见分工

```text
@NotBlank 校验 name 不能为空：Validation
判断 name 是否已存在：Service

@Min 校验 id 必须大于 0：Validation
判断 id 对应记录是否存在：Service

@Email 校验邮箱格式：Validation
判断邮箱是否已注册：Service
```

---

## 为什么不要全写在 Service

如果所有校验都写在 Service，代码会越来越乱：

```java
if (name == null || name.trim().isEmpty()) {}
if (name.length() > 20) {}
if (email == null || !email.contains("@")) {}
if (age < 0) {}
```

这些基础字段规则可以交给 Validation。

Service 保持专注：

```text
处理业务逻辑
调用 Mapper
判断业务异常
返回业务结果
```

---

## 为什么不能只靠 Validation

Validation 不知道数据库里的状态。

它只能判断当前传入对象本身是否合法。

比如：

```text
name 是否为空：Validation 能判断
name 是否已存在：Validation 通常不能直接判断
```

所以实际项目中通常两者都要用。

---

## 最佳理解

```text
Validation：防止脏参数进入业务层
Service：保证业务规则正确
```
