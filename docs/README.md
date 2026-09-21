# docs 总入口

这个目录用于沉淀长期可复用的学习和项目知识。

当前分成三条主线：

```text
kb/        Java 后端学习与项目开发知识库
frontend/  2026 AI 前端工程师面试知识库与传统前端基础
real-development/  按真实项目顺序执行的后台开发流程
```

---

## 阅读入口

- [Java 后端知识库](kb/README.md)
- [前端专题](frontend/README.md)
- [真实项目手把手开发](real-development/README.md)：先看开发路线，再从新项目启动跟写到用户模块。

---

## 目录定位

### kb

`kb/` 只放 Java 后端相关内容：

```text
Java 基础
Spring Boot
MySQL
MyBatis
Docker
Swagger
Validation
项目实战
```

### frontend

`frontend/` 放前端相关内容。当前采用“专题目录 + 单题文件”结构：

```text
javascript/    Jxx  JavaScript 核心
browser/       Bxx  浏览器、网络、安全
html-css/      Cxx  HTML 与 CSS
vue/           Vxx  Vue 专题
react/         Rxx  React 专题
engineering/   Exx  工程化与性能优化
ai-frontend/   Axx  AI 应用前端专题
project/       Pxx  项目表达与综合实战
```

---

### real-development

围绕 `pulse-admin-server` 按项目交付顺序组织教程，包含需求与接口设计、新建项目、MySQL/MyBatis、用户 CRUD、登录权限、前后端联调及部署。每阶段都有执行步骤与验收标准，知识点解释链接到 `kb/`。

---

## 使用原则

- Java 后端主线继续放到 `kb/`
- 真实后台项目的执行流程放到 `real-development/`
- 前端面试恢复和项目表达放到 `frontend/`
- 不按“第几天、第几周”维护长期文档
- 前端单题按专题目录维护，不再堆在 `frontend/` 根目录
