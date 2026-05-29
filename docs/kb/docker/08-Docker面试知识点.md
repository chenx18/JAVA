# 08-Docker面试知识点

Docker 在 Java 后端面试中通常属于工程化基础能力。

初级阶段不要求深入 Kubernetes 或镜像优化，但要能讲清楚基础概念和实际使用场景。

---

## 1. Docker 是什么

推荐回答：

```text
Docker 是一种容器化技术，用来把应用或中间件和运行环境隔离起来。
在本地开发中，常用 Docker 启动 MySQL、Redis 等依赖服务，避免在本机手动安装多个环境。
```

关键词：

```text
容器化
环境隔离
统一运行环境
快速启动依赖服务
```

---

## 2. 镜像和容器的区别

推荐回答：

```text
镜像是静态模板，类似安装包。
容器是镜像运行起来后的实例。
比如 mysql:8.4 是镜像，用它启动出来的 MySQL 服务就是容器。
```

一句话：

```text
镜像是模板，容器是运行实例。
```

---

## 3. Docker 和虚拟机的区别

Docker：

```text
容器级隔离
共享宿主机操作系统内核
启动快
资源占用相对小
适合应用和服务部署
```

虚拟机：

```text
硬件级虚拟化
每个虚拟机有完整操作系统
启动较慢
资源占用较大
隔离更重
```

简短回答：

```text
Docker 比虚拟机更轻量，启动更快，因为容器共享宿主机内核，而虚拟机通常需要完整操作系统。
```

---

## 4. docker-compose 是干什么的

推荐回答：

```text
docker-compose 用来编排多个容器服务。
比如一个项目需要 MySQL、Redis、后端服务，可以写在 docker-compose.yml 中，然后通过 docker compose up -d 一键启动。
```

关键词：

```text
多容器编排
一键启动
本地开发环境
依赖服务管理
```

---

## 5. Dockerfile 是什么

推荐回答：

```text
Dockerfile 是构建 Docker 镜像的说明文件。
比如 Spring Boot 项目可以先打成 jar，再通过 Dockerfile 放进 Java 运行环境镜像中，启动时执行 java -jar。
```

一句话：

```text
Dockerfile 管镜像怎么构建。
```

---

## 6. Dockerfile 和 docker-compose 的区别

推荐回答：

```text
Dockerfile 用来构建镜像，docker-compose.yml 用来启动和编排容器。
一个是构建说明书，一个是运行编排文件。
```

---

## 7. 项目里 Docker 通常怎么用

本地开发：

```text
Java 项目在本机运行
MySQL / Redis / MQ 在 Docker 中运行
```

部署环境：

```text
Java 项目也可能被打包成 Docker 镜像运行
数据库可能是 Docker 容器，也可能是独立数据库或云数据库
```

---

## 8. 端口映射怎么理解

例如：

```yaml
ports:
  - "3307:3306"
```

表示：

```text
本机 3307 端口映射到容器内部 3306 端口
```

Spring Boot 连接：

```properties
jdbc:mysql://localhost:3307/week4_db
```

---

## 9. 数据卷 volume 是什么

推荐回答：

```text
volume 用来持久化容器数据。
比如 MySQL 的数据目录可以挂到 Docker 数据卷中，这样容器删除后，数据不一定丢失。
```

关键词：

```text
数据持久化
容器删除不等于数据删除
数据库常用
```

---

## 10. 常用 Docker 命令

```bash
docker ps
docker ps -a
docker images
docker logs 容器名
docker exec -it 容器名 bash
docker compose up -d
docker compose down
```

---

## 11. 初级面试最低掌握

至少能回答：

```text
Docker 是什么
镜像和容器区别
Docker 和虚拟机区别
docker-compose 作用
Dockerfile 作用
端口映射是什么
数据卷是什么
项目中如何用 Docker 启动 MySQL / Redis
```

---

## 12. 面试回答总模板

```text
我在本地开发中主要用 Docker 管理项目依赖环境，比如 MySQL、Redis。
通过 docker-compose.yml 可以定义服务镜像、端口映射、环境变量和数据卷，
然后用 docker compose up -d 一键启动。
Java 项目本机运行时，可以通过 localhost 映射端口连接 Docker 中的 MySQL。
部署阶段，Spring Boot 项目也可以通过 Dockerfile 打包成镜像运行。
```
