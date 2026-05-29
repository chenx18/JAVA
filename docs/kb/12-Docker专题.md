# 12-Docker专题

Docker 是后端开发中常用的容器化工具，常用于统一开发环境、启动项目依赖服务，以及部署应用。

这个专题围绕后端开发中最常见的 Docker 使用方式展开，重点讲清楚：

```text
Docker 是什么
镜像和容器是什么
端口映射和数据卷是什么
docker-compose 如何启动依赖环境
Spring Boot 项目如何连接 Docker 中的 MySQL
Dockerfile 在部署阶段的作用
Docker 面试常见问题
```

---

## 阅读顺序

1. `docker/01-Docker是什么与使用场景.md`
2. `docker/02-镜像与容器.md`
3. `docker/03-端口映射与数据卷.md`
4. `docker/04-docker-compose基础.md`
5. `docker/05-Docker运行MySQL实战.md`
6. `docker/06-Docker常用命令.md`
7. `docker/07-Dockerfile入门.md`
8. `docker/08-Docker面试知识点.md`

---

## 学习目标

学完本专题后，应能：

```text
解释 Docker 的作用
区分镜像和容器
看懂基础 docker-compose.yml
用 Docker 启动 MySQL
理解端口映射和数据卷
掌握常用 Docker 命令
知道 Dockerfile 和 docker-compose 的区别
回答基础 Docker 面试题
```

---

## 后端开发中的典型用法

本地开发中常见结构：

```text
Spring Boot 在本机运行
MySQL / Redis / MQ 在 Docker 中运行
```

部署阶段常见结构：

```text
Spring Boot 也打包成 Docker 镜像运行
MySQL / Redis 可能在 Docker 中，也可能是独立服务或云服务
```

一句话：

```text
Docker 用来统一和隔离运行环境。
```
