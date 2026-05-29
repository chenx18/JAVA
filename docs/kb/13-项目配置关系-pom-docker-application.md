# 13-项目配置关系：pom.xml、docker-compose.yml、application.properties

学习 Spring Boot 连接数据库时，最容易混乱的不是代码，而是配置文件。

本篇用来讲清楚三个常见配置文件分别负责什么：

```text
pom.xml
application.properties
docker-compose.yml
```

一句话先记住：

```text
pom.xml 管 Java 项目需要什么能力。
application.properties 管 Spring Boot 怎么连接外部环境。
docker-compose.yml 管外部环境怎么启动。
```

---

## 1. 为什么后端项目有这么多配置

一个后端项目不是只有 Java 代码。

它通常还依赖：

```text
数据库 MySQL
缓存 Redis
消息队列 RabbitMQ
文件服务 MinIO
搜索服务 Elasticsearch
```

所以项目里会出现不同职责的配置文件。

如果不分清职责，就会搞混：

```text
MySQL 驱动写在哪里？
数据库密码写在哪里？
Docker 端口写在哪里？
Spring Boot 为什么知道连哪个数据库？
```

答案分别在不同文件里。

---

## 2. 三个配置文件的关系

以第四周项目为例：

```text
Spring Boot 项目
  需要 MyBatis 和 MySQL 驱动 -> pom.xml
  需要知道数据库地址和密码 -> application.properties
  需要启动一个 MySQL 服务 -> docker-compose.yml
```

关系图：

```text
pom.xml
  -> 告诉 Java 项目：我要用 MyBatis、MySQL 驱动

application.properties
  -> 告诉 Spring Boot：数据库地址、账号、密码是什么

docker-compose.yml
  -> 告诉 Docker：启动一个 MySQL 容器
```

三者配合后，链路才完整：

```text
Spring Boot 代码
  -> 通过 pom.xml 拥有数据库驱动能力
  -> 读取 application.properties 的数据库连接信息
  -> 连接 docker-compose.yml 启动的 MySQL
```

---

## 3. pom.xml 是什么

`pom.xml` 是 Maven 项目的核心配置文件。

它主要负责：

```text
项目基本信息
Java 版本
Spring Boot 版本
项目依赖
构建插件
```

最常见的是管理依赖。

比如你要写 Web 接口，需要：

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

你要用 MyBatis，需要：

```xml
<dependency>
  <groupId>org.mybatis.spring.boot</groupId>
  <artifactId>mybatis-spring-boot-starter</artifactId>
  <version>3.0.5</version>
</dependency>
```

你要连接 MySQL，需要：

```xml
<dependency>
  <groupId>com.mysql</groupId>
  <artifactId>mysql-connector-j</artifactId>
  <scope>runtime</scope>
</dependency>
```

理解方式：

```text
pom.xml 不是写业务配置的地方。
pom.xml 是告诉 Maven：这个 Java 项目需要下载哪些 jar 包。
```

---

## 4. pom.xml 中 dependency 怎么理解

一个依赖通常长这样：

```xml
<dependency>
  <groupId>com.mysql</groupId>
  <artifactId>mysql-connector-j</artifactId>
  <scope>runtime</scope>
</dependency>
```

各部分含义：

```text
groupId：组织名 / 包归属
artifactId：具体依赖名
version：版本号
scope：依赖作用范围
```

为什么有的依赖没有 version？

因为 Spring Boot 的 parent 已经帮你管理了一批常用依赖版本。

例如：

```xml
<parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-parent</artifactId>
  <version>3.5.14</version>
</parent>
```

这表示：

```text
当前项目继承 Spring Boot 的依赖版本管理。
很多常见依赖不需要你手写 version。
```

---

## 5. application.properties 是什么

`application.properties` 是 Spring Boot 的应用配置文件。

它主要负责：

```text
应用名称
端口号
数据库连接信息
MyBatis 配置
日志配置
环境变量配置
```

例如连接 MySQL：

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/week4_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

理解方式：

```text
pom.xml 让项目具备连接 MySQL 的能力。
application.properties 告诉项目具体连接哪一个 MySQL。
```

也就是说：

```text
pom.xml = 有能力
application.properties = 怎么用这个能力
```

---

## 6. docker-compose.yml 是什么

`docker-compose.yml` 是 Docker Compose 的配置文件。

它负责描述：

```text
这个项目需要哪些外部服务
这些服务用什么镜像
端口怎么映射
密码和数据库名是什么
数据保存在哪里
```

例如启动 MySQL：

```yaml
services:
  mysql:
    image: mysql:8.4
    container_name: week4-mysql
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: 123456
      MYSQL_DATABASE: week4_db
      TZ: Asia/Shanghai
    ports:
      - "3307:3306"
    volumes:
      - week4-mysql-data:/var/lib/mysql

volumes:
  week4-mysql-data:
```

理解方式：

```text
docker-compose.yml 不是 Java 配置。
它是项目依赖环境的启动说明书。
```

---

## 7. docker-compose.yml 常见字段

### services

表示有哪些服务。

```yaml
services:
  mysql:
```

意思是：

```text
我要启动一个叫 mysql 的服务。
```

---

### image

表示使用哪个镜像。

```yaml
image: mysql:8.4
```

意思是：

```text
用 MySQL 8.4 这个镜像启动容器。
```

---

### container_name

表示容器名称。

```yaml
container_name: week4-mysql
```

以后查看日志时可以用：

```bash
docker logs week4-mysql
```

---

### environment

表示容器启动时的环境变量。

```yaml
environment:
  MYSQL_ROOT_PASSWORD: 123456
  MYSQL_DATABASE: week4_db
```

对于 MySQL 来说，它们表示：

```text
root 密码是 123456
启动时创建 week4_db 数据库
```

---

### ports

表示端口映射。

```yaml
ports:
  - "3307:3306"
```

意思是：

```text
本机 3307 -> 容器内部 3306
```

为什么不是 `3306:3306`？

因为本机 `3306` 可能已经被别的 MySQL 占用了。

这时可以改成：

```text
3307:3306
```

Spring Boot 连接时就要写：

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/week4_db
```

---

### volumes

表示数据卷。

```yaml
volumes:
  - week4-mysql-data:/var/lib/mysql
```

意思是：

```text
把 MySQL 数据保存到 Docker 数据卷中。
容器删了，数据不一定丢。
```

---

## 8. 三个配置如何配合工作

假设你访问接口：

```text
GET /departments/list
```

完整链路大概是：

```text
Controller 接到请求
  -> Service 调用业务逻辑
    -> Mapper / Repository 查询数据库
      -> MySQL Driver 根据 application.properties 连接数据库
        -> Docker 中运行的 MySQL 返回数据
```

其中：

```text
MySQL Driver 来自 pom.xml
数据库地址来自 application.properties
MySQL 服务来自 docker-compose.yml
```

---

## 9. 如果缺配置会发生什么

### 缺 MySQL Driver

如果 `pom.xml` 没有 MySQL 驱动，可能会报：

```text
Cannot load driver class: com.mysql.cj.jdbc.Driver
```

意思是：

```text
项目没有连接 MySQL 的驱动 jar 包。
```

---

### 数据库地址写错

如果 `application.properties` 地址或端口写错，可能会报：

```text
Communications link failure
Connection refused
```

意思是：

```text
Spring Boot 连不上数据库。
```

---

### Docker 没启动

如果 Docker MySQL 没启动，Spring Boot 也会连接失败。

检查：

```bash
docker ps
```

如果看不到 MySQL 容器，就启动：

```bash
docker compose up -d
```

---

### 端口冲突

如果启动 Docker MySQL 时报：

```text
ports are not available
bind: Only one usage of each socket address is normally permitted
```

意思是：

```text
本机端口已经被占用了。
```

解决：

```yaml
ports:
  - "3307:3306"
```

然后 Spring Boot 也改成：

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/week4_db
```

---

## 10. 实际开发中要掌握到什么程度

### pom.xml

重要程度：高。

必须会：

```text
看懂依赖
添加依赖
知道 Spring Boot parent 管版本
知道 Maven 会下载 jar 包
知道 scope 大概是什么意思
```

不要求死背所有依赖坐标，但要知道去哪里找、怎么加。

---

### application.properties

重要程度：高。

必须会：

```text
配置端口
配置数据库连接
配置账号密码
配置 MyBatis 基础项
区分不同环境配置
```

---

### docker-compose.yml

重要程度：中等偏高。

后端开发至少要会：

```text
看懂服务配置
改端口
改密码
改数据库名
启动和停止服务
查看容器日志
```

不要求一开始会写复杂部署，但要能维护本地开发环境。

---

## 11. 没有 AI 时是不是要自己手写

是的，但实际开发中很少完全从 0 手写。

常见做法是：

```text
复制公司项目模板
复制旧项目配置
查看官方文档
用 Spring Initializr 生成
用 AI 辅助生成
```

但你必须能看懂和修改。

因为真正开发时经常遇到：

```text
加一个新依赖
改数据库连接
端口冲突
MySQL 密码不对
Docker 容器启动失败
依赖版本冲突
```

这些问题不能只靠复制，要能判断是哪一层配置出问题。

---

## 12. 一句话总结

```text
pom.xml：项目需要什么能力。
application.properties：项目怎么连接外部环境。
docker-compose.yml：外部环境怎么启动。
```

再短一点：

```text
pom 管依赖。
properties 管连接。
compose 管环境。
```