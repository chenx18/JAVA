# 06-Docker常用命令

Docker 不需要一开始学很深，但常用命令必须熟悉。

---

## 1. 查看 Docker 版本

```bash
docker --version
```

---

## 2. 查看正在运行的容器

```bash
docker ps
```

只显示正在运行的容器。

---

## 3. 查看所有容器

```bash
docker ps -a
```

包括已经停止的容器。

---

## 4. 查看镜像

```bash
docker images
```

显示本机已有镜像。

---

## 5. 启动 compose 服务

```bash
docker compose up -d
```

含义：

```text
根据 docker-compose.yml 启动服务
-d 表示后台运行
```

---

## 6. 停止 compose 服务

```bash
docker compose down
```

会停止并删除 compose 创建的容器和网络。

注意：如果使用了命名数据卷，数据卷通常不会被删除。

---

## 7. 查看容器日志

```bash
docker logs week4-mysql
```

持续查看日志：

```bash
docker logs -f week4-mysql
```

---

## 8. 进入容器执行命令

```bash
docker exec -it week4-mysql bash
```

进入 MySQL 客户端：

```bash
docker exec -it week4-mysql mysql -uroot -p
```

---

## 9. 停止容器

```bash
docker stop week4-mysql
```

---

## 10. 启动已存在容器

```bash
docker start week4-mysql
```

---

## 11. 删除容器

```bash
docker rm week4-mysql
```

删除前通常需要先停止：

```bash
docker stop week4-mysql
```

---

## 12. 命令速查

```bash
docker --version
docker ps
docker ps -a
docker images
docker compose up -d
docker compose down
docker logs week4-mysql
docker exec -it week4-mysql mysql -uroot -p
```
