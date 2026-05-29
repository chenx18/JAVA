# Java 数据类型分类

## 📊 数据类型总览

```
Java 数据类型
├── 基本数据类型（8种）
│   ├── 数值类型
│   │   ├── 整数类型：byte, short, int, long
│   │   └── 浮点类型：float, double
│   ├── 字符类型：char
│   └── 布尔类型：boolean
│
└── 引用数据类型
    ├── 类：String, Scanner, 自定义类...
    ├── 接口
    └── 数组
```

---

## 1️⃣ 基本数据类型（8种）

### 1.1 整数类型（4种）

| 类型 | 大小 | 范围 | 默认值 | 示例 |
|-----|------|------|--------|------|
| `byte` | 1字节 | -128 ~ 127 | 0 | `byte age = 18;` |
| `short` | 2字节 | -32768 ~ 32767 | 0 | `short year = 2024;` |
| `int` | 4字节 | -2,147,483,648 ~ 2,147,483,647 | 0 | `int count = 1000;` ⭐ |
| `long` | 8字节 | 超大整数 | 0L | `long population = 1400000000L;` |

**⚠️ 注意：**
- `long` 类型赋值时后面要加 `L` 或 `l`
- `int` 是最常用的整数类型

**示例代码：**
```java
byte b = 10;
short s = 1000;
int i = 100000;
long l = 10000000000L;  // 必须加 L
```

---

### 1.2 浮点类型（2种）

| 类型 | 大小 | 精度 | 默认值 | 示例 |
|-----|------|------|--------|------|
| `float` | 4字节 | 单精度，7位有效数字 | 0.0F | `float pi = 3.14F;` |
| `double` | 8字节 | 双精度，15位有效数字 | 0.0 | `double pi = 3.1415926;` ⭐ |

**⚠️ 注意：**
- `float` 类型赋值时后面要加 `F` 或 `f`
- `double` 是最常用的浮点类型

**示例代码：**
```java
float price = 19.9F;    // 必须加 F
double height = 1.75;   // 不需要加后缀
double pi = 3.1415926535;
```

---

### 1.3 字符类型（1种）

| 类型 | 大小 | 说明 | 默认值 | 示例 |
|-----|------|------|--------|------|
| `char` | 2字节 | 单个字符 | `\u0000` | `char grade = 'A';` |

**⚠️ 注意：**
- 只能放**单个字符**
- 使用**单引号** `''`
- 可以存储中文、英文、数字、符号

**示例代码：**
```java
char ch1 = 'A';      // 英文字符
char ch2 = '中';     // 中文字符
char ch3 = '9';      // 数字字符
char ch4 = '$';      // 特殊符号
```

---

### 1.4 布尔类型（1种）

| 类型 | 大小 | 说明 | 默认值 | 示例 |
|-----|------|------|--------|------|
| `boolean` | 1位 | 真/假 | `false` | `boolean isPass = true;` |

**示例代码：**
```java
boolean isTrue = true;
boolean isFalse = false;
boolean isOpen = true;
boolean isValid = false;
```

---

## 2️⃣ 引用数据类型

### 2.1 类（Class）

**示例：**
```java
String name = "张三";           // 字符串类
String message = "Hello Java";  // 字符串类
Integer num = 100;              // 包装类
```

### 2.2 接口（Interface）

**示例：**
```java
List<String> list = new ArrayList<>();
Map<String, Integer> map = new HashMap<>();
```

### 2.3 数组（Array）

**示例：**
```java
int[] numbers = {1, 2, 3, 4, 5};              // 整数数组
String[] names = {"A", "B", "C"};             // 字符串数组
double[] scores = {90.5, 85.0, 95.5};         // 小数数组
char[] letters = {'A', 'B', 'C'};             // 字符数组
```

---

## 3️⃣ 基本类型 vs 引用类型

| 特性 | 基本类型 | 引用类型 |
|-----|---------|---------|
| **存储内容** | 实际值 | 地址（指向对象） |
| **存储位置** | 栈内存 | 栈（引用）+ 堆（对象） |
| **默认值** | `0`, `false`, `\u0000` | `null` |
| **比较方式** | `==` 比较值 | `==` 比较地址 |
| **大小** | 固定大小 | 不固定 |

**示例对比：**
```java
// 基本类型
int a = 10;
int b = 10;
System.out.println(a == b);  // true（比较值）

// 引用类型
String s1 = new String("hello");
String s2 = new String("hello");
System.out.println(s1 == s2);  // false（比较地址）
System.out.println(s1.equals(s2));  // true（比较内容）
```

---

## 4️⃣ 数据类型转换

### 4.1 自动类型转换（隐式）

**规则：** 小类型 → 大类型（自动提升）

```
byte → short → int → long → float → double
     char → int → long → float → double
```

**示例：**
```java
int i = 100;
long l = i;        // 自动转换：int → long
double d = l;      // 自动转换：long → double
```

### 4.2 强制类型转换（显式）

**规则：** 大类型 → 小类型（可能丢失精度）

**语法：** `(目标类型)值`

**示例：**
```java
double d = 3.14;
int i = (int) d;   // 强制转换：3.14 → 3（丢失小数部分）

int i = 100;
byte b = (byte) i; // 强制转换：int → byte
```

---

## 5️⃣ 常用数据类型选择建议

### ⭐ 日常开发最常用（4种）

```java
int age = 19;                  // 整数：计数、索引
double height = 1.75;          // 小数：价格、比例
boolean isPass = true;         // 布尔：判断条件
String name = "张三";          // 字符串：文本内容
```

### 🔸 特殊场景使用

```java
byte b = 10;                   // 节省内存时用（文件操作、网络传输）
short s = 100;                 // 特殊场景（很少用）
long l = 10000000000L;         // 超大数字（时间戳、大数计算）
float f = 3.14F;               // 需要精确内存控制（很少用）
char ch = 'A';                 // 单个字符（验证码、等级标记）
```

---

## 6️⃣ 常见错误与注意事项

### ❌ 错误示例

```java
// 1. long 类型忘记加 L
long l = 10000000000;  // ❌ 编译错误（超出 int 范围）
long l = 10000000000L; // ✅ 正确

// 2. float 类型忘记加 F
float f = 3.14;  // ❌ 编译错误（默认是 double）
float f = 3.14F; // ✅ 正确

// 3. 字符串用单引号
String s = 'hello';  // ❌ 编译错误
String s = "hello";  // ✅ 正确

// 4. 字符用双引号
char ch = "A";  // ❌ 编译错误
char ch = 'A';  // ✅ 正确

// 5. 高精度赋值给低精度（不强制转换）
double d = 3.14;
int i = d;        // ❌ 编译错误
int i = (int) d;  // ✅ 正确
```

---

## 7️⃣ 快速记忆口诀

> **四型两浮一字符一布尔**
>
> - 四整：`byte`, `short`, `int`, `long`
> - 两浮：`float`, `double`
> - 一字符：`char`
> - 一布尔：`boolean`

---

## 📝 练习题

1. 定义一个 `long` 类型变量存储手机号
2. 定义一个 `float` 类型变量存储商品价格
3. 定义一个 `char` 类型变量存储等级（A/B/C）
4. 定义一个 `boolean` 类型变量判断是否及格
5. 定义一个 `int` 数组存储 5 个学生的成绩
6. 定义一个 `String` 数组存储 3 个学生的名字

---

**文档创建时间：** 2026-01-27
**适用版本：** Java 8+
