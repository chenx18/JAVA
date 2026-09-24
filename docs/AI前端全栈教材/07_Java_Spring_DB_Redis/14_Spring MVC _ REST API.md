# 14 Spring MVC、REST API 与边界对象

可交付接口需要接收、校验、授权、业务处理、持久化与响应映射。Controller不是所有逻辑的容器，DTO的价值在于明确不同外部契约。

## 一、本章目录

- [从 Filter 到响应](#k01)
- [路径、查询、请求体与验证](#k02)
- [REST、状态码与响应结构](#k03)
- [身份、约束兜底与接口测试](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 从 Filter 到响应

请求经Servlet Filter、安全链、DispatcherServlet、HandlerMapping/Adapter、Controller、Service和数据访问层，再由消息转换器序列化响应；拦截器在MVC处理链上参与特定阶段。

Filter在Servlet层，HandlerInterceptor围绕MVC处理器，ControllerAdvice统一处理适合的异常。登录安全通常采用成熟安全框架过滤链，不把教学拦截器当完整认证系统。

Controller管协议边界，Service管业务与事务，Mapper/Repository管数据访问，错误与请求上下文应贯穿链路。

<a id="k02"></a>

### 2. 路径、查询、请求体与验证

@PathVariable取路径变量，@RequestParam取查询/表单参数，@RequestBody经转换器读请求体，@ModelAttribute可绑定查询对象。GET也能验证，@Valid并非仅用于POST。

```java
// spring-context: UserCreateRequest.java，需要Jakarta Validation依赖。
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record UserCreateRequest(
    @NotBlank @Size(max = 50) String userName
) {}

```

@Valid用于相应对象校验和级联，方法参数约束与异常处理方式需看Spring版本/配置。缺参、转换失败、JSON错误、Bean Validation失败不是同一错误路径。

新增、部分更新、查询和响应可按契约拆对象，避免允许客户端写内部字段；名字叫DTO还是Request是约定，职责清楚更重要。

<a id="k03"></a>

### 3. REST、状态码与响应结构

资源路径使用稳定名词，GET读取、POST创建、PUT/PATCH更新、DELETE删除，明确幂等和部分更新规则。201、204、400、401、403、404、409等各有语义。

统一响应结构仍应保留HTTP语义和稳定错误码，不把所有失败变200。响应对象控制敏感字段与序列化，数据库实体关系不应无条件公开。

分页限制pageSize、排序白名单并约定total与records；Long大ID给浏览器时注意字符串编码，时间与金额同样需协议。

<a id="k04"></a>

### 4. 身份、约束兜底与接口测试

密码使用合适散列如BCrypt，token按契约验证签名、时效、受众/发行者等，后端检查角色与资源归属。前端隐藏按钮或传role不是授权。

业务预检查改善错误信息，数据库唯一/非空等约束保护并发事实，异常转换做最后兜底。日志与用户文案分离并带requestId。

测试正常CRUD、缺参、空值、重复、不存在、非法状态、未登录/越权、分页上限和并发冲突。Swagger/OpenAPI辅助文档和交互测试，注解不能证明逻辑正确。

<a id="summary"></a>

## 三、知识小结

MVC完成HTTP到业务的边界转换，DTO表达场景，Validation与数据库约束分层配合，授权和异常处理构成完整接口行为。

参考：[Spring Framework Reference](https://docs.spring.io/spring-framework/reference/)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="java14-01"></a>

### JAVA14-01 [P0·基础] PathVariable、RequestParam、RequestBody怎么选？

**回答：** 分别对应路径、查询/表单参数和经转换器解析的请求体。复杂查询可绑定对象再校验，按协议位置选择而非字段名字。

对应讲解：[路径、查询、请求体与验证](#k02)。

<a id="java14-02"></a>

### JAVA14-02 [P0·原理] @Valid只能用于POST吗？

**回答：** 不是，验证作用于相应绑定对象，GET查询对象也可校验。方法参数约束的开启与异常需结合版本和配置。

对应讲解：[路径、查询、请求体与验证](#k02)。

<a id="java14-03"></a>

### JAVA14-03 [P0·工程取舍] 为什么拆请求和响应对象？

**回答：** 读写场景的必填、可修改和敏感字段不同，拆分明确外部契约并降低持久化结构耦合。可复用稳定片段，不宜无条件用Entity接收和返回全部字段。

对应讲解：[REST、状态码与响应结构](#k03)。

<a id="java14-04"></a>

### JAVA14-04 [P1·原理] Service已查重为何还要唯一约束？

**回答：** 并发请求可能同时通过预检查，数据库约束才在写入处保护唯一事实。预查负责友好提示，约束与异常转换负责最终一致行为。

对应讲解：[身份、约束兜底与接口测试](#k04)。

<a id="java14-05"></a>

### JAVA14-05 [P0·基础] Filter和HandlerInterceptor处于什么位置？

**回答：** Filter在Servlet请求链层，HandlerInterceptor围绕Spring MVC处理器，DispatcherServlet组织匹配与调用。认证安全链和统一异常处理有各自边界，不能因为都能拦请求就任意替代。

对应讲解：[从 Filter 到响应](#k01)。
