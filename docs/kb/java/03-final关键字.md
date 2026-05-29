# 03-final关键字

`final` 表示“最终的、不能再改变的”。

它可以修饰变量、方法和类，不同位置含义不同。

---

## final 修饰变量

变量被 `final` 修饰后，只能赋值一次。

```java
final int id = 1;
```

之后不能再写：

```java
id = 2;
```

---

## final 修饰成员变量

后端项目里常见：

```java
private final DepartmentService service;

public DepartmentController(DepartmentService service) {
  this.service = service;
}
```

意思是：

```text
service 这个引用初始化后，不能再指向别的对象。
```

这不代表 service 对象内部不能变化，而是这个变量不能重新赋值。

---

## final 修饰方法

方法被 `final` 修饰后，子类不能重写它。

```java
public final void run() {
}
```

---

## final 修饰类

类被 `final` 修饰后，不能被继承。

```java
public final class String {
}
```

Java 里的 `String` 就是 final 类。

---

## 当前阶段最常见用法

你现在最常见的是：

```java
private final XxxService service;
private final XxxMapper mapper;
```

配合构造器注入：

```java
public XxxController(XxxService service) {
  this.service = service;
}
```

这样可以表达：

```text
这个依赖创建后不会被换掉。
```

---

## 一句话总结

```text
final 修饰变量表示只能赋值一次，常用于固定依赖引用。
```
