# Validation 专题索引

Validation 用于校验请求参数，避免非法数据进入业务逻辑。

---

## 阅读顺序

1. `01-Validation是什么.md`
2. `02-常用校验注解.md`
3. `03-Controller中使用Valid.md`
4. `04-全局处理校验异常.md`
5. `05-Validation与Service校验边界.md`
6. `06-Validation面试知识点.md`

---

## 学习重点

- `@Valid` 用来触发对象字段校验
- 校验规则写在实体类字段上
- 常用注解来自 `jakarta.validation.constraints`
- 校验失败通常由 `MethodArgumentNotValidException` 表示
- 全局异常处理负责把校验错误变成统一返回格式
- Validation 处理“字段是否合法”，Service 处理“业务是否允许”
