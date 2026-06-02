# MyBatis 专题索引

MyBatis 用来把 Java 方法和 SQL 绑定起来，是 Spring Boot 项目中常见的数据访问框架。

本文档按“可对外发布的教程”组织，不记录个人学习过程、临时排查过程或日期进度。

---

## 专题写作结构

每篇文档尽量采用以下结构：

```text
1. 本节目录 / 学习目标
2. 完整示例
3. 实现步骤
4. 核心知识沉淀
5. 实际开发注意点
6. 常见误区
7. 面试小题
8. 小结
```

写作原则：

```text
个人过程记录 -> 改成通用常见误区
XML 异常排查 -> 改成常见误区与排查方式
学习过程描述 -> 改成实际开发说明
```

---

## 前置知识与后续路径

进入 MyBatis 前，建议先具备：

```text
java/01-类对象与构造函数.md
springboot/04-分层架构.md
springboot/11-CRUD模块通用结构.md
mysql/01-SQL基础与MySQL终端.md
mysql/03-CRUD增删改查.md
mysql/05-主键约束索引基础.md
```

MyBatis 内部依赖关系：

```text
01-04：知道 Mapper 是什么、参数怎么传、返回值怎么看
  ↓
05-06：掌握 XML、namespace、id、resultType、resultMap
  ↓
07：掌握动态查询和动态更新
  ↓
08：掌握新增时数据库自增 id 回填
  ↓
09-10：掌握关联查询和 JOIN
  ↓
11：掌握 foreach 批量操作
  ↓
100：最后整理面试表达
```

学完后进入：

```text
springboot/16-事务Transactional.md
project-practice/01-后台管理模块开发实战.md
```

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
9. `09-关联查询与一对多关系.md`
10. `10-JOIN查询.md`
11. `11-foreach批量操作.md`
12. `100-MyBatis面试知识点.md`

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
- 一对多查询常用 `resultMap + collection`
- 批量操作常用 `<foreach>` 拼接 `in (...)`