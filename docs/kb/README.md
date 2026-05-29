# 主线知识库索引

这套知识库不是记录“第几天学了什么”，而是沉淀以后会反复查的主线知识。

---

## 长期专题结构

```text
java/        Java 语言基础
springboot/  Spring Boot 入门到项目开发主线
docker/      环境、容器、部署基础
mysql/       数据库与 SQL
mybatis/     Java 操作数据库的持久层框架
swagger/     接口文档与接口测试页面
validation/  参数校验体系
99-学习目录.md  全周期学习路线和阶段地图
```

---

## 推荐阅读顺序

### 1. Java 基础

1. `java/README.md`
2. `java/01-类对象与构造函数.md`
3. `java/02-方法与构造函数重载.md`
4. `java/03-final关键字.md`
5. `java/04-访问修饰符与封装.md`
6. `java/05-异常基础.md`

### 2. Spring Boot 主线

1. `springboot/README.md`
2. `springboot/01-SpringBoot是什么.md`
3. `springboot/02-项目结构与启动流程.md`
4. `springboot/03-分层架构.md`
5. `springboot/04-Controller与路由.md`
6. `springboot/05-请求参数接收.md`
7. `springboot/06-统一返回ApiResponse.md`
8. `springboot/07-全局异常处理.md`
9. `springboot/08-依赖注入与组件扫描.md`
10. `springboot/09-JavaBean与请求响应对象.md`
11. `springboot/10-CRUD模块通用结构.md`
12. `springboot/11-DTO请求对象与响应对象.md`
13. `springboot/12-列表查询DTO与分页参数.md`
14. `springboot/13-Lombok减少样板代码.md`
15. `springboot/14-DTO-VO-Entity区别.md`
16. `springboot/15-项目目录结构与包分层.md`

### 3. 数据库与项目依赖专题

1. `docker/README.md`
2. `mysql/README.md`
3. `mybatis/README.md`
4. `swagger/README.md`
5. `validation/README.md`

---

## 当前阶段优先阅读

如果你正在进行数据库版 CRUD，并补接口文档、参数校验、DTO、Lombok 和 MyBatis XML，建议按这个顺序看：

1. `springboot/10-CRUD模块通用结构.md`
2. `springboot/11-DTO请求对象与响应对象.md`
3. `springboot/12-列表查询DTO与分页参数.md`
4. `springboot/14-DTO-VO-Entity区别.md`
5. `springboot/13-Lombok减少样板代码.md`
6. `12-Docker专题.md`
7. `13-项目配置关系-pom-docker-application.md`
8. `14-MySQL专题.md`
9. `mysql/03-CRUD增删改查.md`
10. `mysql/07-数据库约束与数据兜底.md`
11. `16-MyBatis专题.md`
12. `mybatis/01-MyBatis是什么.md`
13. `mybatis/02-Mapper接口与SQL注解.md`
14. `mybatis/03-参数绑定.md`
15. `mybatis/04-增删改查返回值.md`
16. `mybatis/05-MyBatis XML写法.md`
17. `mybatis/06-MyBatis XML核心知识点.md`
18. `mybatis/07-动态SQL之where和set.md`
19. `mybatis/08-自增主键与useGeneratedKeys.md`
20. `17-Swagger专题.md`
21. `swagger/02-SpringBoot接入Swagger.md`
22. `swagger/03-Swagger常用注解.md`
23. `18-参数校验Validation专题.md`
24. `validation/02-常用校验注解.md`
25. `validation/04-全局处理校验异常.md`

---

## 根目录保留文档

根目录只保留跨专题入口和总览：

- `README.md`
- `99-学习目录.md`
- `12-Docker专题.md`
- `13-项目配置关系-pom-docker-application.md`
- `14-MySQL专题.md`
- `16-MyBatis专题.md`
- `17-Swagger专题.md`
- `18-参数校验Validation专题.md`

具体知识点优先进入各专题目录。

---

## 后面继续补什么

后面建议继续补：

- `springboot/15-SpringBoot连接数据库.md`
- `springboot/16-数据库版CRUD模块.md`
- `springboot/17-JWT与登录鉴权.md`
- `springboot/18-RBAC权限模型.md`

---

## 使用规则

- Java 语言知识放到 `java/`
- Spring Boot 项目主线放到 `springboot/`
- Docker、MySQL、MyBatis、Swagger、Validation 各自独立专题
- 根目录只放总览和跨专题文档
- 不再按“第几周、第几天”维护长期主线笔记
- 以后优先查专题目录
