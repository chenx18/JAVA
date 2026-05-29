# 04-docker-compose基础

`docker-compose.yml` 用来描述一个项目需要启动哪些容器服务。

一句话：

```text
docker-compose.yml 是项目依赖环境的启动说明书。
```

---

## 1. docker-compose 解决什么问题

一个项目可能需要多个服务：

```text
MySQL
Redis
Nginx
后端服务
前端服务
```

如果每个都手动执行 `docker run`，命令会很长，也不方便维护。

Docker Compose 可以把这些服务写进一个文件，然后一键启动：

```bash
docker compose up -d
```

---

## 2. 基础结构

```yaml
services:
  mysql:
    image: mysql:8.4
    container_name: week4-mysql
    ports:
      - "3307:3306"

volumes:
  week4-mysql-data:
```

---

## 3. services

```yaml
services:
  mysql:
```

表示定义一个名为 `mysql` 的服务。

一个 compose 文件可以有多个服务：

```yaml
services:
  mysql:
  redis:
  nginx:
```

---

## 4. image

```yaml
image: mysql:8.4
```

表示使用 `mysql:8.4` 这个镜像启动容器。

---

## 5. container_name

```yaml
container_name: week4-mysql
```

表示容器名字。

查看日志时可以写：

```bash
docker logs week4-mysql
```

---

## 6. restart

```yaml
restart: unless-stopped
```

含义：

```text
除非手动停止，否则 Docker 会尝试自动重启容器。
```

开发环境常用。

---

## 7. environment

```yaml
environment:
  MYSQL_ROOT_PASSWORD: 123456
  MYSQL_DATABASE: week4_db
```

表示传给容器的环境变量。

对 MySQL 来说：

```text
MYSQL_ROOT_PASSWORD：root 密码
MYSQL_DATABASE：初始化时自动创建的数据库
```

---

## 8. ports

```yaml
ports:
  - "3307:3306"
```

含义：

```text
本机 3307 -> 容器 3306
```

---

## 9. volumes

```yaml
volumes:
  - week4-mysql-data:/var/lib/mysql
```

含义：

```text
把 MySQL 数据目录保存到 Docker 数据卷
```

文件底部还要声明：

```yaml
volumes:
  week4-mysql-data:
```

---

## 10. 常用命令

启动：

```bash
docker compose up -d
```

停止并删除容器网络：

```bash
docker compose down
```

查看运行状态：

```bash
docker ps
```

---

## 11. 一句话总结

```text
docker-compose.yml 用来定义并一键启动项目依赖的多个容器服务。
```
