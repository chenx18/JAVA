# 02-SpringBoot接入Swagger

Spring Boot 接入 Swagger，通常只需要先加一个依赖。

当前项目是 Spring Boot 3.x，推荐使用 `springdoc-openapi`。

---

## 添加依赖

在 `pom.xml` 的 `<dependencies>` 中加入：

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.9</version>
</dependency>
```

这段配置的意思是：

```text
告诉 Maven：我要在项目里使用 springdoc-openapi 这个 Swagger 工具包。
```

---

## 为什么加依赖就能用

不是 XML 本身有魔法，而是这个依赖包内部已经写好了 Spring Boot 自动配置。

流程可以理解为：

```text
pom.xml 加 dependency
        ↓
Maven 下载 jar 包
        ↓
Spring Boot 启动时扫描依赖
        ↓
发现 springdoc 的自动配置
        ↓
自动扫描 Controller
        ↓
生成 OpenAPI 接口文档
        ↓
提供 Swagger UI 页面
```

---

## 常用访问地址

项目启动后，访问：

```text
http://localhost:8080/swagger-ui/index.html
```

如果端口改成了 `8081`，就是：

```text
http://localhost:8081/swagger-ui/index.html
```

还有一个原始 JSON 文档地址：

```text
http://localhost:8080/v3/api-docs
```

这个地址返回的是机器可读的 OpenAPI JSON，不是给人看的页面。

---

## Swagger 会扫描什么

Swagger 主要扫描这些 Spring MVC 注解：

```java
@RestController
@RequestMapping
@GetMapping
@PostMapping
@PutMapping
@DeleteMapping
@PathVariable
@RequestParam
@RequestBody
```

例如：

```java
@RestController
@RequestMapping("/departments")
public class DepartmentController {

  @GetMapping("/{id}")
  public ApiResponse<Department> getDepartmentById(@PathVariable int id) {
    return ApiResponse.success(service.getDepartmentById(id));
  }
}
```

Swagger 会生成：

```text
GET /departments/{id}
参数：id
返回：ApiResponse<Department>
```

---

## 接入后是否需要写配置类

学习阶段通常不需要。

只要加依赖、重启项目、访问 Swagger 页面即可。

后面如果需要分组、标题、接口描述、安全认证等，再单独加配置。
