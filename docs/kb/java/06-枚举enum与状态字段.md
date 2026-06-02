# 枚举 enum 与状态字段

## 1. 先看一个完整可用示例

这一篇先不从概念开始，而是先看实际项目里可以直接用的写法。

示例项目中，`Department.status` 用数字表示状态：

```text
0 = Disabled
1 = Enabled
```

可以用一个枚举集中管理：

```java
package com.example.week4.common.enums;

public enum DepartmentStatusEnum {

  DISABLED(0, "Disabled"),
  ENABLED(1, "Enabled");

  private final int code;
  private final String label;

  DepartmentStatusEnum(int code, String label) {
    this.code = code;
    this.label = label;
  }

  public int getCode() {
    return code;
  }

  public String getLabel() {
    return label;
  }

  public static boolean containsCode(Integer code) {
    if (code == null) {
      return true;
    }

    for (DepartmentStatusEnum status : DepartmentStatusEnum.values()) {
      if (status.getCode() == code) {
        return true;
      }
    }

    return false;
  }
}
```

这段代码解决三个问题：

```text
1. 0 / 1 不再是看不懂的魔法数字
2. 后端知道 0 / 1 分别代表什么
3. Service 可以用 containsCode 校验前端传来的 status
```

---

## 2. 示例项目怎么使用这个 enum

`DepartmentService` 中原来可能这样判断：

```java
private void validateStatus(Integer status) {
  if (status == null) {
    return;
  }
  if (status != 0 && status != 1) {
    throw new IllegalArgumentException("status must be 0 or 1");
  }
}
```

这个写法能跑，但 `0` 和 `1` 的含义散落在 Service 中。

改成 enum 后：

```java
private void validateStatus(Integer status) {
  if (!DepartmentStatusEnum.containsCode(status)) {
    throw new IllegalArgumentException("status must be 0 or 1");
  }
}
```

这样 Service 不再关心有哪些状态值。

Service 只问一件事：

```text
这个 status 是否合法？
```

状态规则由 `DepartmentStatusEnum` 统一维护。

---

## 3. 这段 enum 代码逐段解释

### 3.1 enum 不是 class

枚举要写：

```java
public enum DepartmentStatusEnum {
```

不能写成：

```java
public class DepartmentStatusEnum {
```

因为下面这种写法：

```java
DISABLED(0, "Disabled"),
ENABLED(1, "Enabled");
```

是枚举常量写法，只能放在 `enum` 里。

---

### 3.2 DISABLED / ENABLED 是枚举项

```java
DISABLED(0, "Disabled"),
ENABLED(1, "Enabled");
```

表示这个枚举只有两个固定选项：

```text
DISABLED
ENABLED
```

括号里的：

```text
0, "Disabled"
1, "Enabled"
```

会传给下面的构造函数。

---

### 3.3 code 是数据库保存的值

```java
private final int code;
```

当前数据库里 `department.status` 存的是数字：

```text
0
1
```

所以 enum 里用 `code` 保存数据库值。

对应关系：

```text
DISABLED -> 0
ENABLED  -> 1
```

---

### 3.4 label 是给人看的含义

```java
private final String label;
```

`label` 表示这个状态的文字含义。

例如：

```text
0 -> Disabled
1 -> Enabled
```

即使接口暂时只返回数字，`label` 仍然有价值。

它可以用于：

```text
日志
调试
接口文档
字典接口
前端下拉框
```

---

### 3.5 构造函数给每个枚举项赋值

```java
DepartmentStatusEnum(int code, String label) {
  this.code = code;
  this.label = label;
}
```

当写：

```java
DISABLED(0, "Disabled")
```

就相当于把：

```text
code = 0
label = "Disabled"
```

保存到 `DISABLED` 这个枚举项里。

---

### 3.6 getter 用来读取 code 和 label

```java
public int getCode() {
  return code;
}

public String getLabel() {
  return label;
}
```

外部可以这样用：

```java
DepartmentStatusEnum.ENABLED.getCode();
DepartmentStatusEnum.ENABLED.getLabel();
```

结果：

```text
1
Enabled
```

---

### 3.7 containsCode 用来校验状态是否合法

```java
public static boolean containsCode(Integer code) {
  if (code == null) {
    return true;
  }

  for (DepartmentStatusEnum status : DepartmentStatusEnum.values()) {
    if (status.getCode() == code) {
      return true;
    }
  }

  return false;
}
```

它的含义：

```text
如果 code 是 null，允许通过
如果 code 是 0，允许通过
如果 code 是 1，允许通过
其他值不允许
```

为什么 `null` 返回 `true`？

因为在修改接口里，`status = null` 通常表示：

```text
本次请求不修改 status
```

所以不需要抛出异常。

---

## 4. 什么是魔法数字

魔法数字就是代码里直接出现，但含义不清楚的数字。

例如：

```java
if (status == 1) {
  ...
}
```

问题是别人不知道：

```text
1 是启用？
1 是正常？
1 是成功？
1 是管理员？
```

所以不推荐在业务代码里到处写：

```java
0
1
2
3
```

更推荐用 enum 或常量集中管理。

---

## 5. enum 常见写法分类

枚举不一定每次都要写很长。

它是按需要逐步增加能力的。

### 5.1 最简单枚举

```java
public enum Gender {
  MALE,
  FEMALE,
  UNKNOWN
}
```

适合：

```text
只在 Java 内部判断
不需要和数据库数字对应
不需要给前端展示 label
```

---

### 5.2 带 code 的枚举

```java
public enum DepartmentStatusEnum {

  DISABLED(0),
  ENABLED(1);

  private final int code;

  DepartmentStatusEnum(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
```

适合：

```text
数据库 status 存 0 / 1
接口也只返回 0 / 1
后端代码不想裸写魔法数字
```

---

### 5.3 带 code + label 的枚举

```java
public enum DepartmentStatusEnum {

  DISABLED(0, "Disabled"),
  ENABLED(1, "Enabled");

  private final int code;
  private final String label;

  DepartmentStatusEnum(int code, String label) {
    this.code = code;
    this.label = label;
  }

  public int getCode() {
    return code;
  }

  public String getLabel() {
    return label;
  }
}
```

适合：

```text
数据库存 code
接口可以只返回 code
后端内部希望知道 code 的文字含义
未来可能做字典接口或日志展示
```

---

### 5.4 带 fromCode 查询方法的枚举

```java
public static DepartmentStatusEnum fromCode(Integer code) {
  if (code == null) {
    return null;
  }

  for (DepartmentStatusEnum status : DepartmentStatusEnum.values()) {
    if (status.getCode() == code) {
      return status;
    }
  }

  return null;
}
```

使用：

```java
DepartmentStatusEnum status = DepartmentStatusEnum.fromCode(1);
```

适合：

```text
需要根据数据库 code 找枚举对象
需要拿 label
需要做状态转换
```

---

### 5.5 带 containsCode 校验方法的枚举

```java
public static boolean containsCode(Integer code) {
  if (code == null) {
    return true;
  }

  for (DepartmentStatusEnum status : DepartmentStatusEnum.values()) {
    if (status.getCode() == code) {
      return true;
    }
  }

  return false;
}
```

适合：

```text
新增或修改时需要校验 status 是否合法
不希望 Service 里直接写 status != 0 && status != 1
```

---

## 6. 常量类和 enum 怎么选

简单常量类也能解决魔法数字问题：

```java
public class DepartmentStatus {

  public static final int DISABLED = 0;
  public static final int ENABLED = 1;

  private DepartmentStatus() {
  }
}
```

使用：

```java
DepartmentStatus.ENABLED
```

但如果状态需要：

```text
code
label
fromCode
containsCode
```

就更适合用 `enum`。

简单判断：

```text
只是几个固定数字：常量类可以
状态有 code、label、转换、校验：enum 更好
```

---

## 7. 实际后台通常怎么返回状态

很多实际后台接口只返回数字：

```json
{
  "id": 1,
  "name": "研发部",
  "status": 1
}
```

前端自己根据字典或配置展示：

```text
0 -> 禁用
1 -> 启用
```

也可能由后端提供字典接口：

```http
GET /dict/department-status
```

返回：

```json
[
  { "label": "禁用", "value": 0 },
  { "label": "启用", "value": 1 }
]
```

所以常见模式是：

```text
数据库存数字
后端接口返回数字
后端代码用 enum 管理数字含义
前端通过字典把数字翻译成文字
```

重点是：

> enum 不一定是为了直接把 label 返回给前端，更重要的是让后端代码不要裸写 0/1。

---

## 8. enum、字典、数据库字段的关系

三者关系可以这样理解：

```text
数据库字段：保存 code，比如 status = 1
Java enum：管理 code 的含义，比如 ENABLED(1, "Enabled")
数据字典接口：把 code/label 提供给前端展示
```

也就是：

```text
department.status
  ↓
DepartmentStatusEnum
  ↓
/dict/department-status
  ↓
前端表格显示 启用/禁用
```

下一篇建议阅读：

```text
../springboot/17-数据字典与状态翻译.md
```

因为 enum 解决的是“后端代码如何管理状态值”；数据字典解决的是“前端页面如何展示这些状态值”。

---

## 9. 枚举类应该放哪里

如果枚举只属于某个业务模块，可以放：

```text
project/system/domain/enums/DepartmentStatusEnum.java
```

如果多个模块都要用，或者希望统一管理，可以放：

```text
common/enums/DepartmentStatusEnum.java
```

示例项目放在：

```text
common/enums/DepartmentStatusEnum.java
```

是可以的。

---

## 10. 面试怎么说

如果面试问：

> 项目里的状态字段怎么设计？

可以这样回答：

```text
数据库里一般会存状态 code，比如 0 表示禁用，1 表示启用。
但代码里不建议到处直接写 0 和 1，因为这是魔法数字，可读性差。
可以用常量类或者 enum 统一管理状态值。
如果状态只有简单值，可以用常量；如果状态需要 code、label、校验、转换，更推荐使用 enum。
Service 层校验状态时，不直接判断 status != 0 && status != 1，而是调用枚举的 containsCode 方法，让状态规则集中维护。
接口可以仍然只返回 status 数字，前端通过字典把数字翻译成文字。
```

---

## 11. 本节小结

现在重点掌握：

```text
1. enum 是固定选项集合，不是普通 class
2. 数据库存数字，Java enum 管理数字含义
3. code 对应数据库值
4. label 是给人看的含义
5. containsCode 用来校验前端传来的状态是否合法
6. 后台接口通常仍然只返回 status 数字
7. 前端展示文字通常靠字典
```

后面再深入：

```text
枚举和 JSON 序列化
枚举和 MyBatis TypeHandler
数据字典表
状态机
```
