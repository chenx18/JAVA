# Docker 专题索引

Docker 专题用于沉淀本地开发环境、容器基础和后端项目部署相关知识。

本文档按“可对外发布的教程”组织，不记录个人学习过程、临时排查过程或日期进度。

---

## 专题写作结构

每篇文档尽量采用以下结构：

```text
1. 本节目录 / 学习目标
2. 完整示例
3. 命令与配置说明
4. 核心知识沉淀
5. 实际开发注意点
6. 常见误区
7. 面试小题
8. 小结
```

---

## 前置知识与后续路径

Docker 可以独立学习。对于 Java 后端入门，优先掌握“用 Docker 启动依赖服务”，再考虑“把 Java 应用也打进 Docker”。

Docker 内部依赖关系：

```text
01：知道 Docker 是什么、解决什么环境问题
  ↓
02：理解镜像和容器
  ↓
03：理解端口映射和数据卷
  ↓
04：理解 docker-compose.yml
  ↓
05：用 Docker 启动 MySQL
  ↓
06：掌握常用排查命令
  ↓
07：理解 Dockerfile 如何构建应用镜像
  ↓
08：最后整理面试表达
```

与项目主线的关系：

```text
docker/05-Docker运行MySQL实战.md
  ↓
mysql/01-SQL基础与MySQL终端.md
  ↓
mybatis/01-MyBatis是什么.md
```

---

## 阅读顺序

1. `01-Docker是什么与使用场景.md`
2. `02-镜像与容器.md`
3. `03-端口映射与数据卷.md`
4. `04-docker-compose基础.md`
5. `05-Docker运行MySQL实战.md`
6. `06-Docker常用命令.md`
7. `07-Dockerfile入门.md`
8. `08-Docker面试知识点.md`

---

## 最小掌握标准

后端开发入门阶段至少要掌握：

```text
Docker 是什么
镜像和容器的区别
如何用 docker compose 启动 MySQL
如何查看容器状态和日志
如何理解 3307:3306 端口映射
如何理解 volume 数据卷
Dockerfile 和 docker-compose 的区别
```

---

## 常见使用场景

```text
本地启动 MySQL
本地启动 Redis
本地启动 Nginx
统一团队开发环境
部署 Spring Boot 应用
配合 CI/CD 自动构建和发布
```