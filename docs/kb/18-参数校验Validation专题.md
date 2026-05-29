# 18-参数校验Validation专题

参数校验用于检查前端传来的请求数据是否合法。

一句话：

```text
Validation 负责在业务执行前，先判断请求字段是否符合规则。
```

---

## 阅读顺序

1. `validation/01-Validation是什么.md`
2. `validation/02-常用校验注解.md`
3. `validation/03-Controller中使用Valid.md`
4. `validation/04-全局处理校验异常.md`
5. `validation/05-Validation与Service校验边界.md`
6. `validation/06-Validation面试知识点.md`

---

## 当前阶段掌握到什么程度

现在不需要背完整套 Bean Validation 规范。

先掌握这些就够：

```text
知道 @Valid 用来触发校验
会在实体字段上写 @NotBlank / @NotNull / @Min / @Size
知道校验失败会抛 MethodArgumentNotValidException
会在 GlobalExceptionHandler 中返回统一错误格式
能区分参数格式校验和业务规则校验
```

---

## Validation 在项目里的位置

Validation 通常发生在 Controller 接收参数之后，Service 业务执行之前。

```text
请求 JSON
   ↓
@RequestBody 转成 Java 对象
   ↓
@Valid 触发字段校验
   ↓
校验通过：进入 Service
校验失败：进入 GlobalExceptionHandler
```

所以它更像是：

```text
请求参数入口检查
```
