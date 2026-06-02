# 主线知识库索引

这套知识库用于沉淀 Java 后端学习和项目开发中会反复查阅的主线知识。

它不是个人学习日记，而是按专题组织的可发布教程库。

---

## 专题结构

```text
java/              Java 语言基础
springboot/        Spring Boot 入门到项目开发主线
mysql/             数据库与 SQL
mybatis/           Java 操作数据库的持久层框架
docker/            环境、容器、部署基础
swagger/           接口文档与接口测试页面
validation/        参数校验体系
project-practice/  项目实战篇
```

---

## 学习依赖总图

专题化以后，可以把知识库分成两种用法：

```text
按路线学习：从 Java 基础一路走到项目实战
按专题查询：开发时按问题进入对应专题
```

完整依赖关系如下：

```text
Java 基础
  ↓
Spring Boot 项目结构 / 分层 / Controller
  ↓
MySQL SQL / 建表 / CRUD
  ↓
MyBatis Mapper / XML / 动态 SQL
  ↓
Validation 参数校验 + Swagger 接口文档
  ↓
事务 / 日志 / JOIN / 批量操作 / 字典
  ↓
项目实战篇
```

最小起步路线：

```text
java/01-类对象与构造函数.md
java/04-访问修饰符与封装.md
java/05-异常基础.md
springboot/01-SpringBoot是什么.md
springboot/03-项目目录结构与包分层.md
springboot/04-分层架构.md
springboot/05-Controller与路由.md
springboot/07-统一返回ApiResponse.md
mysql/01-SQL基础与MySQL终端.md
mysql/03-CRUD增删改查.md
mybatis/01-MyBatis是什么.md
mybatis/05-MyBatis XML写法.md
project-practice/01-后台管理模块开发实战.md
```

不建议一开始就死磕全部专题。优先跑通“接口接请求 -> Service 处理 -> Mapper 查库 -> 返回 JSON”这条链路。

---

## 推荐阅读顺序

### 1. Java 基础

```text
java/README.md
```

先理解类、对象、构造函数、访问修饰符、异常、枚举等基础能力。

### 2. Spring Boot 主线

```text
springboot/README.md
```

按项目启动、目录结构、分层架构、Controller、统一返回、异常处理、DTO、事务、字典等顺序学习。

### 3. 数据库与持久层

```text
mysql/README.md
mybatis/README.md
```

先掌握 SQL 和表设计，再学习 MyBatis 的 Mapper、XML、动态 SQL、JOIN、foreach 等能力。

### 4. 工程依赖与工具

```text
docker/README.md
swagger/README.md
validation/README.md
```

用于补齐本地环境、接口文档、参数校验等项目开发基础设施。

### 5. 项目实战篇

```text
project-practice/README.md
```

用于把 Java、Spring Boot、MyBatis、MySQL 等知识组合成完整后台模块开发能力。

---

## 根目录文档定位

根目录只保留跨专题入口和总览：

```text
README.md
99-学习目录.md
12-Docker专题.md
13-项目配置关系-pom-docker-application.md
14-MySQL专题.md
16-MyBatis专题.md
17-Swagger专题.md
18-参数校验Validation专题.md
```

具体知识点优先进入各专题目录。

---

## 可发布教程写作结构

每篇教程尽量采用：

```text
1. 本节目录 / 学习目标
2. 完整示例
3. 实现步骤 / 语法拆解
4. 核心知识沉淀
5. 实际开发注意点
6. 常见误区
7. 面试小题
8. 小结
```

写作原则：

```text
个人学习痕迹 -> 删除
个人过程记录 -> 改成通用常见误区
临时过程口吻 -> 改成实际开发口吻
```

---

## 使用规则

- Java 语言知识放到 `java/`
- Spring Boot 项目主线放到 `springboot/`
- Docker、MySQL、MyBatis、Swagger、Validation 各自独立专题
- 项目开发模板和模块实战放到 `project-practice/`
- 根目录只放总览和跨专题文档
- 不按“第几天、第几周”维护长期主线文档