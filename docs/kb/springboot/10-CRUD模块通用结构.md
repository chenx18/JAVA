# 10-CRUD模块通用结构

CRUD 模块是后端项目最常见的业务单元。

User、Role、Department 都可以按同一套结构练习。

---

## 常见类结构

一个完整模块通常包含：

```text
Model / Entity
Repository / Mapper
Service
Controller
NotFoundException
```

例如 Department 模块：

```text
Department.java
DepartmentMapper.java
DepartmentService.java
DepartmentController.java
DepartmentNotFoundException.java
```

---

## Controller 方法

常见接口：

```text
GET    /departments/list       查询全部
GET    /departments/{id}       查询单个
POST   /departments/add        新增
PUT    /departments/update/{id} 修改
DELETE /departments/delete/{id} 删除
```

Controller 负责：

```text
接请求
取参数
调用 Service
返回 ApiResponse
```

---

## Service 方法

常见方法：

```java
getAllDepartments()
getDepartmentById(int id)
createDepartment(Department d)
updateDepartment(int id, Department d)
deleteDepartment(int id)
```

Service 负责：

```text
参数校验
业务规则
调用 Mapper / Repository
决定抛什么异常
```

---

## Repository / Mapper 方法

内存版常见 Repository：

```java
findAll()
findById(int id)
save(Department d)
updateById(Department d)
deleteById(int id)
existsById(int id)
```

数据库版常见 Mapper：

```java
findAll()
findById(int id)
insert(Department d)
updateById(Department d)
deleteById(int id)
```

---

## 返回值推荐

学习阶段推荐：

```text
查询：返回对象或集合
新增：返回新增对象
修改：返回修改后的对象
删除：Service 返回 void，Controller 返回 ApiResponse<Void>
```

Mapper 中：

```text
insert / update / delete 通常返回 int，表示影响行数
```

---

## User 和 Role 模块的意义

`User` 模块用于第一次串通完整结构。

`Role` 模块用于验证是否能脱离模板再写一遍。

真正目标不是背 User / Role，而是掌握 CRUD 模块通用结构。

---

## 一句话总结

```text
CRUD 模块练的是 Controller、Service、数据层、异常、统一返回之间的配合。
```
