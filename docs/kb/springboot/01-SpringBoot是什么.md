# 01-SpringBoot是什么

Spring Boot 是 Java 后端开发中常用的项目框架。

它的目标是让开发者更快创建、启动和维护 Spring 项目。

---

## 一句话理解

```text
Spring Boot = 帮你快速搭好 Java 后端项目骨架的框架。
```

它帮你处理很多基础设施问题，例如：

```text
启动 Web 服务
加载配置文件
管理对象
扫描组件
整合第三方依赖
提供默认配置
```

---

## 为什么不用纯 Java 写接口

如果只用纯 Java 写 Web 接口，需要自己处理很多底层细节：

```text
监听端口
解析 HTTP 请求
路由分发
JSON 转换
对象创建和管理
异常统一处理
```

Spring Boot 把这些常见能力封装好了。

开发者更多关注：

```text
接口怎么设计
业务怎么处理
数据怎么存取
错误怎么返回
```

---

## 当前阶段最重要的能力

初学 Spring Boot，先掌握：

```text
项目如何启动
Controller 如何写接口
Service 如何写业务
Mapper / Repository 如何访问数据
如何统一返回
如何统一处理异常
```

不要一开始追求“精通所有 Spring 原理”。

---

## Spring Boot 和 Java 的关系

Java 是语言，Spring Boot 是框架。

```text
Java：提供类、对象、方法、异常等语言能力
Spring Boot：基于 Java 帮你组织后端项目
```

学 Spring Boot 时，遇到不懂的语法，可以回查 `java/` 专题。
