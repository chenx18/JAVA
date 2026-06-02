# Swagger 专题索引

Swagger 专题用于沉淀接口文档、接口调试和前后端联调相关知识。

本文档按“可对外发布的教程”组织，不记录个人学习过程、临时排查过程或日期进度。

---

## 专题写作结构

每篇文档尽量采用以下结构：

```text
1. 本节目录 / 学习目标
2. 完整示例
3. 注解与配置说明
4. 核心知识沉淀
5. 实际开发注意点
6. 常见误区
7. 面试小题
8. 小结
```

---

## 前置知识与后续路径

进入 Swagger 前，建议先具备：

```text
springboot/05-Controller与路由.md
springboot/06-请求参数接收.md
springboot/07-统一返回ApiResponse.md
```

Swagger 内部依赖关系：

```text
01：知道 Swagger 是接口文档工具
  ↓
02：知道 Spring Boot 如何接入 Swagger
  ↓
03：掌握常用接口说明注解
  ↓
04：理解 Swagger 和 Apifox 的区别
  ↓
05：最后整理面试表达
```

学完后进入：

```text
project-practice/01-后台管理模块开发实战.md
```

---

## 阅读顺序

1. `01-Swagger是什么.md`
2. `02-SpringBoot接入Swagger.md`
3. `03-Swagger常用注解.md`
4. `04-Swagger与Apifox区别.md`
5. `05-Swagger面试知识点.md`

---

## 学习重点

- Swagger 不是业务代码，而是接口文档工具
- Spring Boot 通过依赖和自动配置接入 Swagger
- Swagger 主要扫描 `@RestController`、`@GetMapping`、`@PostMapping` 等接口注解
- 常用注解先掌握 `@Tag`、`@Operation`、`@Parameter`、`@Schema`
- Swagger 适合跟项目一起启动，Apifox 适合团队接口管理