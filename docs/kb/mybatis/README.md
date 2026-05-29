# MyBatis 专题索引

MyBatis 用来把 Java 方法和 SQL 绑定起来。

---

## 阅读顺序

1. `01-MyBatis是什么.md`
2. `02-Mapper接口与SQL注解.md`
3. `03-参数绑定.md`
4. `04-增删改查返回值.md`
5. `05-MyBatis XML写法.md`
6. `06-MyBatis XML核心知识点.md`
7. `07-动态SQL之where和set.md`
8. `08-自增主键与useGeneratedKeys.md`
9. `100-MyBatis面试知识点.md`

---

## 学习重点

- Mapper 是数据库版 Repository
- Java 方法通过 MyBatis 执行 SQL
- `#{}` 用于参数绑定
- 查询返回对象或集合
- 新增、修改、删除通常返回影响行数 `int`
- 注解 SQL 适合简单场景，XML SQL 适合复杂真实项目
- XML 中 `namespace` 对 Mapper 接口，`id` 对 Mapper 方法名
- `resultType` 直接指定返回类型，`resultMap` 手动配置字段映射
- `resultMap` 里 `<id>` 表示对象标识字段，`<result>` 表示普通字段
- 动态查询常用 `<where> + <if>`
- 动态更新常用 `<set> + <if>`
- 新增不传 `id` 时，用 `useGeneratedKeys` 回填数据库自增主键
