# 06-统一返回ApiResponse

统一返回是后端接口开发中的常见做法。

它让成功和失败都保持相似的响应结构。

---

## 常见返回结构

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

字段含义：

```text
code：业务状态码
message：提示信息
data：真实返回数据
```

---

## 为什么要统一返回

统一返回的好处：

```text
前端好处理
接口风格一致
错误和成功格式统一
方便后续扩展
```

---

## ApiResponse 泛型类

```java
public class ApiResponse<T> {
  private int code;
  private String message;
  private T data;
}
```

`T` 表示 data 的类型暂时不写死。

例如：

```java
ApiResponse<String>
ApiResponse<User>
ApiResponse<List<Department>>
ApiResponse<Void>
```

---

## success 和 fail

```java
public static <T> ApiResponse<T> success(T data) {
  return new ApiResponse<>(200, "success", data);
}

public static <T> ApiResponse<T> fail(int code, String message) {
  return new ApiResponse<>(code, message, null);
}
```

理解：

```text
public：外部可以调用
static：不用 new ApiResponse 就能调用
<T>：声明这个方法支持泛型
ApiResponse<T>：返回统一响应对象
```

---

## 为什么对象会变 JSON

不是 `new ApiResponse(...)` 决定 JSON。

真正决定的是：

```text
@RestController
Spring Boot 的 JSON 序列化机制
```

Controller 返回对象后，Spring Boot 会把对象转成 JSON 响应。

---

## 为什么需要 getter

Spring Boot 序列化 JSON 时通常需要读取字段值。

getter 可以让框架读取：

```text
getCode()
getMessage()
getData()
```

所以返回对象通常要写 getter。
