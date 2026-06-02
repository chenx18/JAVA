# foreach 批量操作

## 1. 本节解决什么问题

后台管理系统里经常有这种操作：

```text
勾选多条数据
  ↓
点击批量删除
  ↓
后端一次性删除多条记录
```

例如批量删除部门：

```http
DELETE /departments/batch
```

请求体：

```json
[1, 2, 3]
```

对应 SQL：

```sql
delete from department where id in (1, 2, 3);
```

MyBatis XML 中用：

```xml
<foreach>
```

来把 Java 的 `List<Integer>` 拼成 SQL 的 `in (...)`。

---

## 2. 完整开发流程

批量删除部门的流程：

```text
Controller 接收 ids 数组
  ↓
Service 校验 ids 不能为空
  ↓
Service 开事务 @Transactional
  ↓
Mapper 查询这些 id 是否存在
  ↓
Mapper 执行批量删除
  ↓
可选：写批量删除日志
  ↓
返回成功
```

可以先做基础版：

```text
校验 ids
  ↓
delete from department where id in (...)
```

后面再优化：

```text
删除前查询数据
写操作日志
校验删除数量
```

---

## 3. Controller 怎么接收数组

前端传 JSON 数组：

```json
[1, 2, 3]
```

Controller 用：

```java
@DeleteMapping("/batch")
public ApiResponse<Void> batchDeleteDepartments(@RequestBody List<Integer> ids) {
  service.batchDeleteDepartments(ids);
  return ApiResponse.success(null);
}
```

重点：

```java
@RequestBody List<Integer> ids
```

表示从请求体里读取数组。

如果前端传的是：

```json
{
  "ids": [1, 2, 3]
}
```

那就不是 `List<Integer>`，而应该新建请求 DTO：

```java
public class BatchDeleteRequest {
  private List<Integer> ids;
}
```

入门项目先用最简单的 JSON 数组。

---

## 4. Service 怎么校验

Service 不能直接相信前端传来的 ids。

基础校验：

```java
@Transactional
public void batchDeleteDepartments(List<Integer> ids) {
  if (ids == null || ids.isEmpty()) {
    throw new IllegalArgumentException("ids cannot be empty");
  }

  for (Integer id : ids) {
    if (id == null || id <= 0) {
      throw new IllegalArgumentException("id must be > 0");
    }
  }

  int rows = mapper.deleteByIds(ids);

  if (rows == 0) {
    throw new IllegalArgumentException("no department deleted");
  }
}
```

这里建议加：

```java
@Transactional
```

原因：

```text
批量删除属于写操作
以后可能还要加日志、删关联数据、更新统计
```

---

## 5. Mapper 接口写法

```java
int deleteByIds(@Param("ids") List<Integer> ids);
```

重点是：

```java
@Param("ids")
```

因为 XML 里要使用：

```xml
collection="ids"
```

二者要一致。

---

## 6. XML foreach 写法

```xml
<delete id="deleteByIds">
  delete from department
  where id in
  <foreach collection="ids"
           item="id"
           open="("
           separator=","
           close=")">
    #{id}
  </foreach>
</delete>
```

最终拼出来的 SQL 类似：

```sql
delete from department where id in (?, ?, ?)
```

参数分别是：

```text
1
2
3
```

---

## 7. foreach 每个属性什么意思

```xml
<foreach collection="ids"
         item="id"
         open="("
         separator=","
         close=")">
  #{id}
</foreach>
```

逐个理解：

```text
collection="ids"
```

表示遍历哪个集合。这里对应 Mapper 里的：

```java
@Param("ids")
```

```text
item="id"
```

表示每次循环时，当前元素临时叫 `id`。

```text
open="("
```

循环开始前拼一个左括号。

```text
separator=","
```

每个元素之间用逗号隔开。

```text
close=")"
```

循环结束后拼一个右括号。

所以：

```json
[1, 2, 3]
```

会变成：

```sql
(1, 2, 3)
```

---

## 8. 为什么不能直接字符串拼接

不要这样写：

```java
String sql = "delete from department where id in (" + ids + ")";
```

原因：

```text
容易 SQL 注入
参数类型不好处理
空数组容易拼错
代码难维护
```

MyBatis 的：

```xml
#{id}
```

会使用预编译参数，安全性更好。

---

## 9. 是否需要校验删除数量

基础版：

```java
int rows = mapper.deleteByIds(ids);

if (rows == 0) {
  throw new IllegalArgumentException("no department deleted");
}
```

更严格版：

```java
if (rows != ids.size()) {
  throw new IllegalArgumentException("some departments do not exist");
}
```

但这个严格版有个细节：

```text
如果 ids 里有重复 ID，ids.size() 会大于实际删除行数
```

例如：

```json
[1, 1, 2]
```

只会删除 2 条，但 `ids.size()` 是 3。

所以真实开发中通常会先去重：

```java
List<Integer> uniqueIds = ids.stream().distinct().toList();
```

先掌握基础版即可。

---

## 10. 批量删除和日志

如果业务要求写日志，有两种方式。

### 10.1 写一条总日志

```text
DELETE_DEPARTMENTS
Batch delete department ids: [1, 2, 3]
```

优点：

```text
简单
日志少
```

缺点：

```text
看不到每个部门名称
```

### 10.2 每个部门写一条日志

删除前先查出部门列表：

```java
List<Department> departments = mapper.findByIds(ids);
```

然后循环写日志：

```java
for (Department department : departments) {
  saveDepartmentLog(
      department.getId(),
      "DELETE_DEPARTMENT",
      "Delete department: " + department.getName());
}
```

优点：

```text
日志详细
```

缺点：

```text
代码多
写入次数多
```

可以先不加批量日志，先把 foreach 跑通。

---

## 11. 常见误区

### 11.1 collection 名字和 @Param 不一致

Mapper：

```java
int deleteByIds(@Param("ids") List<Integer> ids);
```

XML：

```xml
<foreach collection="list">
```

这样不符合预期。

正确：

```xml
<foreach collection="ids">
```

### 11.2 忘记 open 和 close

不推荐写法：

```xml
where id in
<foreach collection="ids" item="id" separator=",">
  #{id}
</foreach>
```

会拼成：

```sql
where id in 1,2,3
```

正确需要括号：

```sql
where id in (1,2,3)
```

所以要写：

```xml
open="("
close=")"
```

### 11.3 空数组导致 SQL 错误

如果 ids 是空数组：

```json
[]
```

可能拼成：

```sql
delete from department where id in
```

所以 Service 必须先校验：

```java
if (ids == null || ids.isEmpty()) {
  throw new IllegalArgumentException("ids cannot be empty");
}
```

### 11.4 item 名字和 #{id} 不一致

不推荐写法：

```xml
<foreach collection="ids" item="item">
  #{id}
</foreach>
```

正确：

```xml
<foreach collection="ids" item="id">
  #{id}
</foreach>
```

或者：

```xml
<foreach collection="ids" item="item">
  #{item}
</foreach>
```

---

## 12. 面试怎么说

如果面试问：

> MyBatis 怎么实现批量删除？

可以这样回答：

```text
Controller 可以用 @RequestBody List<Integer> 接收前端传来的 id 数组。
Service 层先校验 ids 不能为空、id 必须大于 0，然后调用 Mapper。
Mapper 方法一般写成 deleteByIds，并用 @Param("ids") 标记集合参数。
XML 里用 foreach 遍历 ids，拼接 SQL 的 in (...)。
比如 delete from department where id in (...)。
同时批量删除属于写操作，如果还涉及日志或关联数据，Service 方法上会加 @Transactional。
```

---

## 13. 本节小结

本节重点：

```text
1. 前端传 JSON 数组
2. Controller 用 @RequestBody List<Integer> 接
3. Service 校验 ids
4. Mapper 用 @Param("ids")
5. XML 用 foreach 拼 in (...)
6. collection 名字要和 @Param 一致
7. 批量写操作通常放在事务里
```

先把批量删除跑通，再考虑批量新增、批量修改。

