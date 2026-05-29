# 05-Swagger面试知识点

Swagger 在面试中通常不会问得特别深，但会考察你是否理解接口文档、前后端联调和 Spring Boot 自动配置。

---

## Swagger 是什么

可以回答：

```text
Swagger 是一个接口文档工具，可以根据 Controller 自动生成接口文档，并提供在线测试页面。
```

在 Spring Boot 3 项目中，常用 `springdoc-openapi` 集成 Swagger UI。

---

## Swagger 有什么作用

常见作用：

```text
自动生成接口文档
展示接口路径、请求方式、参数和返回值
方便后端自测接口
方便前端和测试查看接口说明
减少口头沟通成本
```

---

## Spring Boot 为什么加依赖就能用 Swagger

可以回答：

```text
因为 springdoc-openapi 提供了 Spring Boot 自动配置。项目启动时会自动扫描 Controller 和 Mapping 注解，生成 OpenAPI 文档，并暴露 Swagger UI 页面。
```

关键词：

```text
自动配置
Controller 扫描
OpenAPI 文档
Swagger UI
```

---

## Swagger 会扫描哪些内容

主要扫描：

```text
@RestController
@RequestMapping
@GetMapping
@PostMapping
@PutMapping
@DeleteMapping
@PathVariable
@RequestParam
@RequestBody
返回值类型
```

如果使用 Swagger 注解，还会读取：

```text
@Tag
@Operation
@Parameter
@Schema
```

---

## Swagger 和 Apifox 有什么区别

可以回答：

```text
Swagger 更偏后端项目内的自动接口文档和在线测试页面；Apifox 更偏团队接口管理、环境管理、测试用例和协作。
```

两者不是冲突关系，可以同时使用。

---

## 项目中是否必须使用 Swagger

不是必须。

但是在前后端分离项目里，接口文档非常重要。

可以使用：

```text
Swagger
Apifox
Postman
YApi
内部平台
```

重点不是工具名字，而是接口说明要清楚、稳定、可维护。
