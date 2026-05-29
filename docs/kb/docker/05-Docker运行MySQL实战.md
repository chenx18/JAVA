# 05-Docker运行MySQL实战

MySQL 是后端开发最常见的数据库之一。

使用 Docker 运行 MySQL，可以避免在本机直接安装数据库服务。

---

## 1. docker-compose.yml 示例

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

---

## 2. 配置解释

```text
image：使用 MySQL 8.4 镜像
container_name：容器名
MYSQL_ROOT_PASSWORD：root 用户密码
MYSQL_DATABASE：自动创建的数据库
TZ：时区
ports：端口映射
volumes：数据卷
```

---

## 3. 启动 MySQL

在 `docker-compose.yml` 所在目录执行：

```bash
docker compose up -d
```

查看容器：

```bash
docker ps
```

如果看到 `week4-mysql` 且状态为 `Up`，说明启动成功。

---

## 4. 查看日志

```bash
docker logs week4-mysql
```

如果看到类似：

```text
ready for connections
```

说明 MySQL 已经可以连接。

---

## 5. 进入 MySQL 终端

```bash
docker exec -it week4-mysql mysql -uroot -p
```

输入密码：

```text
123456
```

进入后看到：

```text
mysql>
```

---

## 6. 查看数据库

```sql
show databases;
```

切换数据库：

```sql
use week4_db;
```

---

## 7. Spring Boot 连接配置

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/week4_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

重点：

```text
因为端口映射是 3307:3306，所以 Spring Boot 连接 localhost:3307。
```

---

## 8. 常见问题：端口冲突

如果启动时报：

```text
ports are not available
```

说明本机端口被占用。

解决：

```yaml
ports:
  - "3307:3306"
```

然后 Spring Boot 也连接 3307。

---

## 9. 一句话总结

```text
Docker MySQL 运行在容器里，Spring Boot 通过端口映射连接它。
```
