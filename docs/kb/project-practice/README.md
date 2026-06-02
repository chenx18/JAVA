# 项目实战篇索引

项目实战篇用于把 Java、Spring Boot、MyBatis、MySQL 等知识组合成真实后台模块开发能力。

本文档按“可对外发布的实战教程”组织，不记录个人学习过程、临时排查过程或日期进度。

---

## 专题写作结构

每篇实战文档尽量采用以下结构：

```text
1. 实战目标
2. 完整模块结构
3. 接口清单
4. 建表 SQL
5. 分层实现步骤
6. 核心知识沉淀
7. 常见误区
8. 面试小题
9. 小结
```

---

## 前置知识与后续路径

进入项目实战前，最低前置知识是：

```text
java/01-类对象与构造函数.md
java/04-访问修饰符与封装.md
java/05-异常基础.md
springboot/03-项目目录结构与包分层.md
springboot/04-分层架构.md
springboot/05-Controller与路由.md
springboot/07-统一返回ApiResponse.md
springboot/08-全局异常处理.md
mysql/03-CRUD增删改查.md
mybatis/05-MyBatis XML写法.md
validation/03-Controller中使用Valid.md
swagger/02-SpringBoot接入Swagger.md
```

实战篇的目标不是再学单个知识点，而是把前面的知识串成完整模块：

```text
建表 SQL
  ↓
Entity / Request / Response
  ↓
Mapper 接口
  ↓
Mapper XML
  ↓
Service 校验和事务
  ↓
Controller 接口
  ↓
Validation 校验
  ↓
Swagger 文档
  ↓
接口测试
  ↓
面试表达
```

---

## 阅读顺序

1. `01-后台管理模块开发实战.md`

---

## 实战主线

```text
建表
  ↓
Entity / Request / Response
  ↓
Mapper / XML
  ↓
Service 业务校验
  ↓
Controller 接口
  ↓
统一返回与异常处理
  ↓
接口测试
```

---

## 后续实战建议

```text
02-User模块CRUD练习实战.md
03-模块开发检查清单.md
04-后台模块面试表达模板.md
```