# 17-Swagger专题

Swagger 是后端项目常用的接口文档与接口测试工具。

一句话：

```text
Swagger 会扫描 Controller，把接口自动整理成网页文档，并提供在线测试入口。
```

---

## 阅读顺序

1. `swagger/01-Swagger是什么.md`
2. `swagger/02-SpringBoot接入Swagger.md`
3. `swagger/03-Swagger常用注解.md`
4. `swagger/04-Swagger与Apifox区别.md`
5. `swagger/05-Swagger面试知识点.md`

---

## 当前阶段掌握到什么程度

现在不需要背完 Swagger 的所有配置和注解。

先掌握这些就够：

```text
会接入依赖
知道访问地址
知道它扫描 Controller
会用 @Tag 和 @Operation 优化接口说明
知道 Swagger 和 Apifox 的区别
```

---

## Swagger 在项目里的位置

Swagger 不属于业务层，也不负责处理数据。

它主要服务于接口说明：

```text
Controller 写接口
Swagger 扫描接口
前端/测试/后端通过 Swagger 页面查看和测试接口
```

所以它更像是：

```text
接口文档层 / 开发辅助工具
```
