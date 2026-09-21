# 第三章 CI/CD 与 Docker

## 一、本章具体知识点

- CI
- CD
- GitHub Actions
- Docker image
- Dockerfile
- multi-stage build
- environment
- secrets
- health check
- rollback
- migration

## 二、各知识点详细解释

典型流程：

```text
Git push
→ lint
→ typecheck
→ test
→ build
→ image
→ security scan
→ deploy
→ health check
→ rollback if needed
```

Docker 多阶段构建可以让编译环境与运行环境分离，减少最终镜像内容。

数据库 migration 必须与应用版本兼容，避免新旧实例在滚动发布期间互相不兼容。

## 三、本章面试题与答案

### 题：为什么生产发布要做 health check？

**答案：**

容器能启动不代表应用可用。health check 可以确认关键依赖和进程状态，部署系统据此决定实例是否进入流量池或是否需要回滚。

---
