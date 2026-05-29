# 07-Dockerfile入门

`Dockerfile` 用来描述如何把一个应用打包成 Docker 镜像。

一句话：

```text
Dockerfile 是应用镜像的构建说明书。
```

---

## 1. Dockerfile 和 docker-compose 的区别

`Dockerfile`：

```text
描述如何构建一个镜像
```

`docker-compose.yml`：

```text
描述如何启动一个或多个容器服务
```

区别：

```text
Dockerfile 管“怎么打包”
docker-compose.yml 管“怎么运行”
```

---

## 2. Java 项目什么时候需要 Dockerfile

本地开发阶段：

```text
Spring Boot 通常直接在 IDE 或命令行运行
MySQL / Redis 用 Docker 启动
```

部署阶段：

```text
Spring Boot 可以打包成 jar
再通过 Dockerfile 构建成镜像
然后在服务器或容器平台运行
```

---

## 3. Spring Boot Dockerfile 示例

假设已经打包出：

```text
target/app.jar
```

Dockerfile 可以写：

```dockerfile
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 4. 每一行含义

```dockerfile
FROM eclipse-temurin:17-jre
```

使用 Java 17 运行环境作为基础镜像。

```dockerfile
WORKDIR /app
```

设置容器内工作目录为 `/app`。

```dockerfile
COPY target/*.jar app.jar
```

把本机打包好的 jar 复制进容器，并命名为 `app.jar`。

```dockerfile
EXPOSE 8080
```

声明容器应用使用 8080 端口。

```dockerfile
ENTRYPOINT ["java", "-jar", "app.jar"]
```

容器启动时执行 `java -jar app.jar`。

---

## 5. 构建镜像

```bash
docker build -t springboot-demo:1.0 .
```

含义：

```text
根据当前目录 Dockerfile 构建镜像
镜像名是 springboot-demo
版本标签是 1.0
```

---

## 6. 运行镜像

```bash
docker run -d -p 8080:8080 --name springboot-demo springboot-demo:1.0
```

含义：

```text
后台运行容器
本机 8080 映射到容器 8080
容器名为 springboot-demo
使用 springboot-demo:1.0 镜像
```

---

## 7. 当前阶段需要掌握到什么程度

入门阶段不需要深入镜像优化。

先掌握：

```text
Dockerfile 用来打包应用镜像
docker-compose 用来启动服务组合
Spring Boot 部署时可以通过 Dockerfile 打包成镜像
```

---

## 8. 一句话总结

```text
Dockerfile 负责把应用做成镜像，docker-compose 负责把镜像和依赖服务跑起来。
```
