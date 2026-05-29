# 07-动态SQL之where和set

动态 SQL 是 MyBatis XML 的重要能力。

它解决的问题是：

```text
SQL 的条件或更新字段不是固定的，而是根据前端传参动态变化。
```

后台管理系统里最常见的两个场景：

```text
列表查询：传了条件就按条件查，没传条件就查全部
部分修改：传了哪个字段就改哪个字段，没传的字段不动
```

对应 MyBatis XML：

```text
查询：<where> + <if>
修改：<set> + <if>
```

---

## 1. 为什么需要动态 SQL

假设部门列表支持按名称搜索：

```text
GET /departments/list?pageNum=1&pageSize=10&name=研发
```

如果传了 `name`，SQL 应该是：

```sql
select id, name from department
where name like '%研发%'
order by id
limit 0, 10
```

如果没传 `name`，SQL 应该是：

```sql
select id, name from department
order by id
limit 0, 10
```

也就是说：

```text
where name like ... 这一段不是固定存在的
```

这就是动态 SQL。

---

## 2. `<if>` 是什么

`<if>` 用来判断条件是否成立，成立才拼接里面的 SQL。

```xml
<if test="name != null and name != ''">
  name like concat('%', #{name}, '%')
</if>
```

意思是：

```text
如果 name 不是 null，也不是空字符串，就拼接 name like ...
```

如果 `name` 没传，这段 SQL 不会出现。

---

## 3. `<where>` 解决什么问题

普通 SQL 写法：

```sql
select * from department where name like '%研发%'
```

但如果条件是动态的，就可能遇到问题。

错误示例：

```xml
select id, name from department
where
<if test="name != null and name != ''">
  name like concat('%', #{name}, '%')
</if>
```

如果 `name` 没传，最终 SQL 可能变成：

```sql
select id, name from department where
```

这是错误 SQL。

所以使用 `<where>`：

```xml
<select id="findPage" resultMap="DepartmentResultMap">
  select id, name, description, status from department
  <where>
    <if test="name != null and name != ''">
      name like concat('%', #{name}, '%')
    </if>
  </where>
  order by id
  limit #{offset}, #{pageSize}
</select>
```

`<where>` 会自动处理：

```text
里面有条件：自动加 where
里面没条件：不加 where
开头多余的 and / or：自动处理
```

---

## 4. 分页查询要注意 count 和 list 条件一致

分页接口通常需要两条 SQL：

```text
countQuery：查符合条件的总数
findPage：查当前页数据
```

如果 `findPage` 带了 `name` 条件，`countQuery` 也必须带同样条件。

```xml
<select id="countQuery" resultType="long">
  select count(*) from department
  <where>
    <if test="name != null and name != ''">
      name like concat('%', #{name}, '%')
    </if>
  </where>
</select>

<select id="findPage" resultMap="DepartmentResultMap">
  select id, name, description, status from department
  <where>
    <if test="name != null and name != ''">
      name like concat('%', #{name}, '%')
    </if>
  </where>
  order by id
  limit #{offset}, #{pageSize}
</select>
```

否则会出现：

```text
列表只查出研发部门
total 却是全部部门数量
```

---

## 5. `<set>` 解决什么问题

修改接口有两种方式：

```text
全量修改：前端传完整对象，后端全部覆盖
部分修改：前端只传要修改的字段，没传的不动
```

真实项目里经常需要部分修改。

例如只修改状态：

```json
{
  "status": 0
}
```

这时 SQL 应该是：

```sql
update department set status = 0 where id = 1
```

而不是把 `name`、`description` 都改成 `null`。

---

## 6. 动态更新示例

```xml
<update id="updateById" parameterType="com.example.week4.Department">
  update department
  <set>
    <if test="name != null and name != ''">
      name = #{name},
    </if>
    <if test="description != null">
      description = #{description},
    </if>
    <if test="status != null">
      status = #{status},
    </if>
  </set>
  where id = #{id}
</update>
```

`<set>` 会自动处理：

```text
自动加 set
自动去掉最后多余的逗号
```

所以每个字段后面可以写逗号：

```xml
name = #{name},
```

如果最后一个字段也带逗号，`<set>` 会帮你清掉。

---

## 7. Integer 和 int 的区别

动态更新时，状态字段推荐写：

```java
private Integer status;
```

不要写：

```java
private int status;
```

原因：

```text
Integer 可以是 null，表示前端没传
int 默认是 0，无法区分没传还是传了 0
```

对于部分更新，必须区分：

```text
没传 status：不修改 status
传了 status: 0：修改为 0
```

所以动态更新的可选数字字段，通常使用包装类型：

```text
Integer
Long
Boolean
```

而不是：

```text
int
long
boolean
```

---

## 8. 部分更新为什么不要随便加 @NotBlank

如果 UpdateRequest 是部分更新：

```java
public class DepartmentUpdateRequest {
  private String name;
  private String description;
  private Integer status;
}
```

就不要给 `name` 加：

```java
@NotBlank
```

因为前端可能只想改状态：

```json
{
  "status": 0
}
```

如果 `name` 有 `@NotBlank`，没传 `name` 就会校验失败。

所以：

```text
全量修改：可以要求字段必填
部分修改：字段通常允许为空，由 Service 判断至少传一个字段
```

---

## 9. 空更新要在 Service 拦截

如果前端传：

```json
{}
```

动态 SQL 没有任何字段可更新。

Service 应该提前拦截：

```java
if (
  department.getName() == null &&
  department.getDescription() == null &&
  department.getStatus() == null
) {
  throw new IllegalArgumentException("no field to update");
}
```

原因：

```text
这是业务规则，不是数据库规则
Service 层负责判断请求是否有业务意义
```

---

## 10. 字段变更后哪些地方要同步

如果表新增字段：

```sql
alter table department add column description varchar(255);
alter table department add column status int default 1;
```

通常要同步这些地方：

```text
Entity：Department
CreateRequest：新增请求
UpdateRequest：修改请求
Response：返回对象
Service：对象转换和赋值
Mapper XML：resultMap / select / insert / update
数据库表结构：真实字段
```

漏任何一处，都可能出现问题。

常见例子：

```text
Java 有字段，但 select 没查出来：返回 null
Request 有字段，但 Service 没 set：新增或修改不生效
resultMap 没配：字段映射不到对象
insert 没补字段：新增时存不进去
update 没补字段：修改时不生效
```

---

## 11. 常见错误

### 1. `<set>` 里面不写逗号

不推荐：

```xml
<if test="name != null">name = #{name}</if>
<if test="status != null">status = #{status}</if>
```

如果两个条件都成立，可能拼成：

```sql
set name = ? status = ?
```

正确：

```xml
<if test="name != null">name = #{name},</if>
<if test="status != null">status = #{status},</if>
```

---

### 2. 忘记查询新字段

错误：

```sql
select id, name from department
```

如果前端需要 `description/status`，应该：

```sql
select id, name, description, status from department
```

---

### 3. count 和 list 查询条件不一致

错误：

```text
findPage 按 name 查
countQuery 查全部
```

正确：

```text
findPage 和 countQuery 使用同样的 where 条件
```

---

### 4. 可选数字字段使用 int

不推荐：

```java
private int status;
```

推荐：

```java
private Integer status;
```

---

## 12. 一句话总结

```text
MyBatis 动态 SQL 的核心：查询条件不固定用 <where> + <if>，更新字段不固定用 <set> + <if>。
```

