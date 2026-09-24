# 02 Java 类型、String、包装与精确数值

Java有原始类型与引用类型，自动装箱让二者使用更方便，也带来null与比较陷阱。字符串、金额和接口编码需要独立理解。

## 一、本章目录

- [八种原始类型与包装](#k01)
- [String 不可变、池与拼接](#k02)
- [常用 String API 与比较](#k03)
- [BigDecimal、金额与协议](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 八种原始类型与包装

byte/short/int/long是不同整数范围，float/double是二进制浮点，char是UTF-16码元，boolean表达真假；它们不能保存null。包装类Byte/Short/Integer/Long/Float/Double/Character/Boolean属于对象，可为空并参与泛型集合。

自动装箱/拆箱不是没有成本，拆箱null会抛NullPointerException。包装类可能缓存部分值，不能根据某次==结果判断数值等价；内容比较使用适当equals或数值比较。

Java字段可有默认值，局部变量通常必须在使用前明确初始化；不要把JS的undefined直接套进Java模型。

<a id="k02"></a>

### 2. String 不可变、池与拼接

```java
public class Example {
    public static void main(String[] args) {
        String a = "hello";
        String b = new String("hello");
        System.out.println(a == b); // false
        System.out.println(a.equals(b)); // true
        String emoji = "😀";
        System.out.println(emoji.length() + ":" + emoji.codePointCount(0, emoji.length())); // 2:1
    }
}
```

String创建后字符序列不可原地修改，替换/截取等产生结果。不可变便于共享、字符串池和hash缓存，也降低作为键时被修改的风险。字面量池、intern与new的身份规则不能替代equals。

大量循环拼接常用StringBuilder，StringBuffer提供同步方法但不代表所有复合业务自动线程安全。charAt按码元，codePointAt/codePointCount用于码点；用户可见字素又是更高层。

<a id="k03"></a>

### 3. 常用 String API 与比较

常用方法包括length、isEmpty/isBlank、charAt、substring、indexOf/contains/startsWith/endsWith、replace/replaceAll、split、strip/trim、toLowerCase/toUpperCase、join、format/formatted等，返回值与正则语义要区分。

substring结束位置不包含；replace通常做字面替换，replaceAll使用正则；split默认行为会处理正则并可能丢弃尾部空项，需要limit时明确指定。strip按Unicode空白规则处理，trim与其范围不同。

对象==比较引用身份，equals由类定义逻辑等价，Objects.equals可处理null。重写equals必须遵守对称、传递、一致性等契约并同步维护hashCode。

<a id="k04"></a>

### 4. BigDecimal、金额与协议

```java
import java.math.BigDecimal;
import java.math.RoundingMode;
public class Example {
    public static void main(String[] args) {
        BigDecimal a = new BigDecimal("0.1");
        BigDecimal b = new BigDecimal("0.2");
        System.out.println(a.add(b)); // 0.3
        System.out.println(new BigDecimal("1").divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP)); // 0.33
        System.out.println(new BigDecimal("1.0").equals(new BigDecimal("1.00"))); // false
        System.out.println(new BigDecimal("1.0").compareTo(new BigDecimal("1.00"))); // 0
    }
}
```

避免用new BigDecimal(0.1)把二进制近似值当精确十进制输入，使用字符串或适当valueOf。除法可能需要scale和RoundingMode，equals会考虑scale，compareTo表达数值比较。

数据库DECIMAL、Java BigDecimal和前端传输编码要一致。大整数ID传浏览器时注意Number安全范围，通常用字符串；时间也需约定时区和格式。

<a id="summary"></a>

## 三、知识小结

原始类型不能null，包装拆箱可能空指针；String不可变且身份与内容不同；金额用明确十进制和舍入协议，接口编码要考虑前端数值边界。

参考：[Java Learning](https://dev.java/learn/)；[Java 17 API](https://docs.oracle.com/en/java/javase/17/docs/api/)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="java02-01"></a>

### JAVA02-01 [P0·基础] Java的==和equals有什么区别？

**回答：** 原始类型==比较相应值，对象==比较引用身份；equals由类定义逻辑等价，默认Object实现仍偏身份。字符串内容用equals，可能为空时考虑Objects.equals。

对应讲解：[常用 String API 与比较](#k03)。

<a id="java02-02"></a>

### JAVA02-02 [P0·原理] Integer用==为何有时像正确比较？

**回答：** 装箱可能复用缓存对象，让部分值身份相同，但缓存范围和来源不能作为业务契约。应比较数值或equals，并注意拆箱null会抛错。

对应讲解：[八种原始类型与包装](#k01)。

<a id="java02-03"></a>

### JAVA02-03 [P0·原理] String不可变有什么价值？

**回答：** 创建后的字符序列不被原地改动，便于安全共享、池复用和hash缓存，作为键更稳定。重新给变量赋另一个字符串不是修改原String。

对应讲解：[String 不可变、池与拼接](#k02)。

<a id="java02-04"></a>

### JAVA02-04 [P1·工程取舍] BigDecimal是否不用考虑精度规则？

**回答：** 仍要明确输入来源、scale、舍入和比较语义，非终止小数除法可能需要显式策略，equals与compareTo也不同。精确类型不能替代完整财务协议。

对应讲解：[BigDecimal、金额与协议](#k04)。
