# Validation 专题索引

Validation 专题用于沉淀请求参数校验、字段合法性和校验异常处理相关知识。

本文档按“可对外发布的教程”组织，不记录个人学习过程、临时排查过程或日期进度。

---

## 专题写作结构

每篇文档尽量采用以下结构：

```text
1. 本节目录 / 学习目标
2. 完整示例
3. 注解与流程拆解
4. 核心知识沉淀
5. 实际开发注意点
6. 常见误区
7. 面试小题
8. 小结
```

---

## 前置知识与后续路径

进入 Validation 前，建议先具备：

```text
springboot/05-Controller与路由.md
springboot/06-请求参数接收.md
springboot/08-全局异常处理.md
springboot/12-DTO请求对象与响应对象.md
```

Validation 内部依赖关系：

```text
01：知道 Validation 解决什么问题
  ↓
02：知道常用字段校验注解
  ↓
03：知道 Controller 中如何触发 @Valid
  ↓
04：知道校验失败后如何统一返回
  ↓
05：区分字段校验和业务校验的边界
  ↓
06：最后整理面试表达
```

学完后进入：

```text
springboot/11-CRUD模块通用结构.md
project-practice/01-后台管理模块开发实战.md
```

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
- 校验规则写在请求对象字段上
- 常用注解来自 `jakarta.validation.constraints`
- 校验失败通常由 `MethodArgumentNotValidException` 表示
- 全局异常处理负责把校验错误变成统一返回格式
- Validation 处理“字段格式是否合法”，Service 处理“业务是否允许”