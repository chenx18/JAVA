# 01-Swagger是什么

Swagger 是一个接口文档工具。

它可以根据后端代码自动生成接口文档，并提供一个网页页面让你直接测试接口。

---

## 一句话理解

```text
Swagger = 项目自带的接口说明书 + 在线接口测试页面。
```

比如你写了一个 Controller：

```java
@GetMapping("/departments/{id}")
public ApiResponse<Department> getDepartmentById(@PathVariable int id) {
  return ApiResponse.success(service.getDepartmentById(id));
}
```

Swagger 可以识别出：

```text
请求方式：GET
接口地址：/departments/{id}
参数：id
返回值：ApiResponse<Department>
```

然后把这些内容显示到网页上。

---

## Swagger 解决什么问题

没有 Swagger 时，别人要知道接口怎么调用，通常需要问后端：

```text
这个接口地址是什么？
用 GET 还是 POST？
参数放路径里、Params 里，还是 Body 里？
返回数据是什么格式？
```

有 Swagger 后，这些信息可以直接在页面上看到。

---

## Swagger 不负责什么

Swagger 不负责业务逻辑。

它不做：

```text
数据校验
数据库操作
权限判断
异常处理
业务计算
```

这些仍然是 Controller、Service、Mapper、ExceptionHandler 各自负责。

Swagger 只是帮你把接口展示出来。

---

## 在分层架构中的位置

可以这样理解：

```text
Controller：接收请求，定义接口
Service：处理业务逻辑
Mapper：操作数据库
Swagger：读取接口信息，生成接口文档
```

Swagger 更像是开发辅助工具，不是业务分层的一部分。
