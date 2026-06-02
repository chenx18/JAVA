# Spring Boot 专题索引

Spring Boot 专题用于沉淀从入门到项目开发的后端主线能力。

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
学习过程描述 -> 改成教程说明
临时过程口吻 -> 改成实际开发口吻
```

---

## 前置知识与后续路径

进入 Spring Boot 前，建议先具备：

```text
java/01-类对象与构造函数.md
java/04-访问修饰符与封装.md
java/05-异常基础.md
```

Spring Boot 内部依赖关系：

```text
01-02：知道项目怎么启动
  ↓
03-04：知道代码应该放在哪一层
  ↓
05-06：知道接口怎么接收请求
  ↓
07-08：知道成功和异常怎么统一返回
  ↓
09：知道 Service、Mapper 为什么能被自动传入
  ↓
10-15：知道 Entity、DTO、VO、Request、Response 怎么拆
  ↓
16：知道多个数据库操作为什么要事务
  ↓
17-18：知道状态字段和字典怎么给前端展示
```

学完基础接口后进入：

```text
mysql/01-SQL基础与MySQL终端.md
mybatis/01-MyBatis是什么.md
validation/01-Validation是什么.md
swagger/01-Swagger是什么.md
```

---

## 阅读顺序

1. `01-SpringBoot是什么.md`
2. `02-项目结构与启动流程.md`
3. `03-项目目录结构与包分层.md`
4. `04-分层架构.md`
5. `05-Controller与路由.md`
6. `06-请求参数接收.md`
7. `07-统一返回ApiResponse.md`
8. `08-全局异常处理.md`
9. `09-依赖注入与组件扫描.md`
10. `10-JavaBean与请求响应对象.md`
11. `11-CRUD模块通用结构.md`
12. `12-DTO请求对象与响应对象.md`
13. `13-列表查询DTO与分页参数.md`
14. `14-Lombok减少样板代码.md`
15. `15-DTO-VO-Entity区别.md`
16. `16-事务Transactional.md`
17. `17-简单字典接口与状态翻译.md`
18. `18-数据库版字典管理.md`

---

## 学习主线

Spring Boot 项目开发可以按这条线理解：

```text
启动项目
  ↓
理解项目目录结构
  ↓
理解 Controller / Service / Mapper 分层
  ↓
定义 Controller 接口
  ↓
接收 Request DTO
  ↓
@Valid 校验请求字段
  ↓
调用 Service 处理业务
  ↓
Request 转 Entity / Model
  ↓
调用 Repository / Mapper 操作数据
  ↓
Entity / Model 转 Response DTO / VO
  ↓
返回统一 ApiResponse
  ↓
异常交给 GlobalExceptionHandler
```

---

## 工程工具

- `Lombok`：减少 getter、setter、构造函数等样板代码
- `项目目录结构`：让 common、framework、project、domain、mapper、service 等职责清晰

---

## 与其他专题的关系

- `java/`：语言基础
- `mysql/`：数据库和 SQL
- `mybatis/`：Java 调用数据库
- `swagger/`：接口文档
- `validation/`：参数校验
- `docker/`：环境与容器
- `project-practice/`：项目实战篇