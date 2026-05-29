# 12-Lombok减少样板代码

Lombok 是 Java 项目中常用的编译期工具，用来减少重复的样板代码。

---

## 一句话理解

```text
Lombok = 用注解帮 Java 自动生成 getter、setter、构造函数、日志对象等代码。
```

它不会改变业务逻辑，只是减少手写代码。

---

## 为什么需要 Lombok

传统 Java 类经常要写很多重复代码：

```text
getter
setter
toString
equals
hashCode
无参构造函数
全参构造函数
final 字段构造函数
```

字段一多，类会变得很长。

Lombok 可以让代码更干净。

---

## 添加依赖

在 `pom.xml` 中添加：

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

Spring Boot 父工程通常会管理 Lombok 版本，所以学习阶段可以不写版本号。

添加依赖后建议重新编译：

```bash
mvn -DskipTests clean compile
```

如果编译通过，说明 Lombok 生效。

---

## @Data

`@Data` 常用于 DTO、VO、Entity 等数据类。

它会生成：

```text
getter
setter
toString
equals
hashCode
```

原始写法：

```java
public class DepartmentResponse {
  private int id;
  private String name;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
}
```

使用 Lombok：

```java
import lombok.Data;

@Data
public class DepartmentResponse {
  private int id;
  private String name;
}
```

虽然源码里看不到 `getId()`、`setId()`，但编译时 Lombok 会生成它们。

---

## @NoArgsConstructor

生成无参构造函数。

```java
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Department {
  private int id;
  private String name;
}
```

等价于：

```java
public Department() {
}
```

很多框架需要无参构造函数创建对象，例如 JSON 反序列化、MyBatis 映射等。

---

## @AllArgsConstructor

生成全参构造函数。

```java
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Department {
  private int id;
  private String name;
}
```

等价于：

```java
public Department(int id, String name) {
  this.id = id;
  this.name = name;
}
```

---

## @RequiredArgsConstructor

给 `final` 字段生成构造函数。

常用于 Controller / Service 的构造器注入。

原始写法：

```java
@RestController
public class DepartmentController {

  private final DepartmentService service;

  public DepartmentController(DepartmentService service) {
    this.service = service;
  }
}
```

使用 Lombok：

```java
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class DepartmentController {
  private final DepartmentService service;
}
```

Lombok 会生成：

```java
public DepartmentController(DepartmentService service) {
  this.service = service;
}
```

---

## @Slf4j

`@Slf4j` 用于生成日志对象。

```java
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DepartmentService {

  public void test() {
    log.info("test log");
  }
}
```

它会自动生成类似：

```java
private static final Logger log = LoggerFactory.getLogger(DepartmentService.class);
```

学习阶段可以先认识，后面学日志时再深入。

---

## 当前阶段推荐用法

```text
DTO / Request / Response：@Data
Entity / Model：@Data + @NoArgsConstructor + @AllArgsConstructor
Controller：@RequiredArgsConstructor
Service：@RequiredArgsConstructor
Mapper：通常不用 Lombok
```

例如：

```java
@Data
public class DepartmentCreateRequest {
  private int id;
  private String name;
}
```

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {
  private int id;
  private String name;
}
```

```java
@RequiredArgsConstructor
@RestController
public class DepartmentController {
  private final DepartmentService service;
}
```

```java
@RequiredArgsConstructor
@Service
public class DepartmentService {
  private final DepartmentMapper mapper;
}
```

---

## 不要乱用 @Data

`@Data` 适合数据类，不适合所有类。

不要给这些类随便加 `@Data`：

```text
Controller
Service
Mapper
配置类中的复杂业务组件
```

原因是它会生成 getter、setter、toString、equals、hashCode，这些对业务类通常没有意义。

一句话：

```text
数据类用 @Data，依赖注入类用 @RequiredArgsConstructor。
```

---

## 常见问题

### 1. 代码用了 @Data，但编译报 lombok 不存在

说明 `pom.xml` 没有加 Lombok 依赖，或 Maven 没刷新。

处理：

```text
检查 pom.xml
Maven Reload Project
mvn clean compile
```

### 2. VS Code 里仍然红线

可能是 Java Language Server 没刷新。

处理：

```text
Java: Clean Java Language Server Workspace
Restart and delete
```

### 3. 源码里没有 getter/setter，为什么还能调用

因为 Lombok 在编译期生成了这些方法。

---

## 和真实项目的关系

真实 Spring Boot 项目中 Lombok 很常见。

常见组合：

```java
@Data
```

```java
@RequiredArgsConstructor
```

```java
@Slf4j
```

它的主要价值是让类更短，把注意力留给业务逻辑。

---

## 一句话总结

```text
Lombok 不是新业务能力，而是减少 Java 重复代码的工程工具。
```
