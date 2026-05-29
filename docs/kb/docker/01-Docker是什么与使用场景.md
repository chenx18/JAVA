# 01-Docker是什么与使用场景

Docker 是一种容器化技术，用来统一和隔离软件运行环境。

一句话：

```text
Docker 可以把软件和它需要的运行环境放进一个容器里运行。
```

---

## 1. Docker 解决什么问题

开发中经常遇到：

```text
我电脑能跑，你电脑跑不了
测试环境能跑，生产环境跑不了
MySQL 版本不一致
Redis 没装
环境卸载不干净
项目依赖服务太多
```

Docker 解决的核心问题：

```text
统一运行环境
隔离本机环境
快速启动依赖服务
方便删除和重建环境
```

---

## 2. Docker 和普通安装的区别

直接在 Windows 安装 MySQL：

```text
MySQL 装进本机系统
卸载、重装、换版本比较麻烦
容易遇到环境变量、端口、服务问题
```

用 Docker 运行 MySQL：

```text
MySQL 运行在容器里
本机只需要连接容器暴露的端口
删除和重建更方便
```

---

## 3. 后端开发为什么常用 Docker

后端项目经常依赖：

```text
MySQL
Redis
Nginx
RabbitMQ
MinIO
Elasticsearch
Nacos
```

如果全部手动安装，本机环境会很乱。

Docker 可以让项目用配置文件描述依赖环境：

```text
这个项目需要 MySQL 8.4
这个项目需要 Redis 7
这个项目需要 Nginx
```

然后一条命令启动。

---

## 4. 本地开发中的 Docker

常见结构：

```text
Spring Boot 在本机运行
MySQL / Redis 在 Docker 中运行
```

好处：

```text
Java 代码调试方便
依赖服务不用手动安装
环境可以快速重建
```

---

## 5. 部署阶段中的 Docker

部署时，Spring Boot 项目也可以打包成 Docker 镜像运行。

结构可能是：

```text
Spring Boot 容器
MySQL 容器
Redis 容器
Nginx 容器
```

也可能是：

```text
Spring Boot 容器
MySQL 云数据库
Redis 云服务
```

生产环境不一定把数据库也放进 Docker，很多公司会使用独立数据库或云数据库。

---

## 6. 一句话总结

```text
Docker 是用来统一、隔离和快速启动运行环境的工具。
```
