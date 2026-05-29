# Spring Boot 专题索引

Spring Boot 专题用于沉淀从入门到项目开发的后端主线能力。

这里不记录“第几天学了什么”，而是按真实项目能力组织知识。

---

## 阅读顺序

1. `01-SpringBoot是什么.md`
2. `02-项目结构与启动流程.md`
3. `03-分层架构.md`
4. `04-Controller与路由.md`
5. `05-请求参数接收.md`
6. `06-统一返回ApiResponse.md`
7. `07-全局异常处理.md`
8. `08-依赖注入与组件扫描.md`
9. `09-JavaBean与请求响应对象.md`
10. `10-CRUD模块通用结构.md`
11. `11-DTO请求对象与响应对象.md`
12. `12-列表查询DTO与分页参数.md`
13. `13-Lombok减少样板代码.md`
14. `14-DTO-VO-Entity区别.md`
15. `15-项目目录结构与包分层.md`

---

## 学习主线

Spring Boot 项目开发可以按这条线理解：

```text
启动项目
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
