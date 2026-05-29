# 09-JavaBean与请求响应对象

JavaBean 是 Spring Boot 项目中常见的数据对象写法。

它常用于请求体、响应体和数据库映射对象。

---

## JavaBean 的基本特征

当前阶段可以先这样理解：

```text
私有字段
无参构造函数
getter / setter
```

示例：

```java
public class Department {
  private int id;
  private String name;

  public Department() {
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
}
```

---

## 为什么字段用 private

字段用 `private` 是封装思想。

它避免外部代码随意修改对象内部状态。

外部通过 getter / setter 访问字段。

---

## 为什么需要 getter

接口返回对象时，Spring Boot 需要读取对象字段并转成 JSON。

getter 可以让框架读取字段值。

---

## 为什么需要 setter

使用 `@RequestBody` 接收 JSON 时，框架通常需要：

```text
先创建对象
再把 JSON 字段填到对象里
```

setter 可以让框架设置字段值。

---

## 为什么需要无参构造函数

很多框架创建对象时，会先调用无参构造函数。

常见场景：

```text
JSON 转 Java 对象
数据库结果映射成 Java 对象
配置绑定成 Java 对象
```

所以请求体对象和响应对象建议保留无参构造函数。

---

## 字段多怎么办

传统 Java 会写很多 getter / setter。

真实项目中常用 Lombok 减少样板代码，例如：

```java
@Data
public class Department {
  private int id;
  private String name;
}
```

但学习阶段先手写更容易理解框架为什么需要这些方法。
