# MySQL 与 MyBatis 接入

## 本节目录与路线

1. 用 Compose 启动独立的 MySQL 开发环境。
2. 保存建表 SQL，确认用户表的约束。
3. 引入 MyBatis 和 MySQL 驱动，配置数据源。
4. 通过一条真实 SQL 验证完整调用链。

前置：[02-创建项目与首次启动](02-创建项目与首次启动.md) 已通过验收。本节所有命令在新项目根目录执行。Redis 在登录态阶段接入。

## 一、先看完成后的效果

```text
GET /dev/db-check
  ↓
DbProbeController
  ↓
DbProbeMapper → select 1
  ↓
Docker MySQL
  ↓
{"code":200,"message":"操作成功","data":1}
```

这个临时开发探针直接调用 Mapper，用来验证连接，不承载业务规则。真正的用户接口仍然经过 Service。探针只在 `dev` profile 注册，不作为线上健康接口。

## 二、准备配置文件

### 1. 本地变量

文件：`.env.example`

```dotenv
MYSQL_ROOT_PASSWORD=replace-with-local-root-password
MYSQL_DATABASE=pulse_admin
MYSQL_USER=pulse_app
MYSQL_PASSWORD=replace-with-local-app-password
```

执行：

```powershell
Copy-Item -LiteralPath '.env.example' -Destination '.env'
```

第一次创建后，编辑 `.env`，将两处密码占位值改为不同的本地密码。变量值先使用英文字母、数字和短横线，不加引号；后面的简易 PowerShell 加载方式只支持这种简单 `KEY=value` 格式，不是完整 dotenv 解析器。

在生成器已有 `.gitignore` 后追加：

```gitignore
.env
.env.*
!.env.example
target/
*.log
```

`.env.example` 是配置说明，可提交；`.env` 是运行值，不提交。Spring Boot **不会自动读取 Compose 的 `.env`**，稍后会显式加载到启动终端。

### 2. Compose

文件：`docker-compose.yml`

```yaml
name: pulse-admin-dev
services:
  mysql:
    image: mysql:8.4
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD:?set MYSQL_ROOT_PASSWORD in .env}
      MYSQL_DATABASE: ${MYSQL_DATABASE:?set MYSQL_DATABASE in .env}
      MYSQL_USER: ${MYSQL_USER:?set MYSQL_USER in .env}
      MYSQL_PASSWORD: ${MYSQL_PASSWORD:?set MYSQL_PASSWORD in .env}
    ports:
      - "127.0.0.1:3307:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./sql/001-init.sql:/docker-entrypoint-initdb.d/001-init.sql:ro
    healthcheck:
      test: ["CMD-SHELL", "mysqladmin ping -h 127.0.0.1 -uroot -p\"$${MYSQL_ROOT_PASSWORD}\" --silent"]
      interval: 5s
      timeout: 3s
      retries: 30
      start_period: 30s
volumes:
  mysql_data:
```

这里宿主机用 `3307`，容器内部仍是 `3306`。Java 在 Windows 上运行时连接 `localhost:3307`；Java 将来在同一 Compose 网络中运行时连接 `mysql:3306`。

命名卷 `mysql_data` 保存数据库数据。Docker Desktop 使用 D 盘数据盘时，这个卷也保存在该虚拟磁盘中，不是项目目录里的一个普通文件夹。

### 3. 初始化 SQL

文件：`sql/001-init.sql`。先创建这个文件，再执行 Compose，避免缺失的挂载路径被当成目录。

```sql
use pulse_admin;

create table if not exists sys_user (
  id bigint primary key auto_increment,
  user_name varchar(50) not null,
  password varchar(100) not null,
  nickname varchar(50) not null,
  email varchar(100),
  status tinyint not null default 1,
  create_time datetime not null default current_timestamp,
  update_time datetime not null default current_timestamp on update current_timestamp,
  constraint uk_sys_user_user_name unique (user_name),
  constraint ck_sys_user_status check (status in (0, 1))
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;
```

这里 `user_name` 的唯一判断不区分大小写，因此 `Alice` 和 `alice` 算同一个用户名。若业务希望区分大小写，需要同时调整排序规则和接口约定。

### 4. 启动数据库

```powershell
docker compose config --quiet
docker compose up -d mysql
docker compose ps
docker compose logs --tail 50 mysql
```

等待状态变成 `healthy`。`config --quiet` 验证配置，不把完整环境变量值打印出来。首次启动可能需要下载镜像并初始化数据库。

### 5. 确认建表成功

使用数据库 UI（DBeaver、DataGrip 等）创建 MySQL 连接：

| 项目 | 值 |
| --- | --- |
| Host | `127.0.0.1` |
| Port | `3307` |
| Database | `pulse_admin` |
| User | `.env` 中的 `MYSQL_USER` |
| Password | `.env` 中的 `MYSQL_PASSWORD` |

打开 SQL 编辑器执行：

```sql
show tables;
show create table sys_user;
select count(*) from sys_user;
```

也可以进入容器内 MySQL 客户端，出现密码提示后输入应用账号密码：

```powershell
docker compose exec mysql mysql -u pulse_app -p pulse_admin
```

**初始化 SQL 只在空数据卷的首次初始化时执行。** 修改 `001-init.sql` 后重启容器，不会自动修改已有表；修改 `.env` 也不会自动修改库内已有账号密码。已有数据库应执行明确的 `ALTER TABLE` 或迁移脚本。不要为了重新执行初始化而删除已有数据卷。

## 三、Spring Boot 接入

### 1. 增加依赖

在 `pom.xml` 的 `<dependencies>` 内追加：

```xml
<dependency>
  <groupId>org.mybatis.spring.boot</groupId>
  <artifactId>mybatis-spring-boot-starter</artifactId>
  <version>3.0.5</version>
</dependency>
<dependency>
  <groupId>com.mysql</groupId>
  <artifactId>mysql-connector-j</artifactId>
  <scope>runtime</scope>
</dependency>
```

### 2. 数据源和 XML 配置

将 `src/main/resources/application-dev.yml` 替换为：

```yaml
server:
  address: 127.0.0.1
spring:
  datasource:
    url: jdbc:mysql://localhost:3307/pulse_admin?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&sslMode=DISABLED
    username: ${MYSQL_USER}
    password: ${MYSQL_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
```

这是只连本机的开发配置；生产连接使用服务器配置的 TLS，不照搬 `sslMode=DISABLED` 和 `allowPublicKeyRetrieval=true`。

在公共配置 `src/main/resources/application.yml` 最外层追加：

```yaml
mybatis:
  mapper-locations: classpath:mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true
```

YAML 同一级的 `spring`、`server`、`mybatis` 各保留一个，不能重复声明同名根节点。

### 3. 开发探针

文件：`src/main/java/com/example/admin/project/system/mapper/DbProbeMapper.java`

```java
package com.example.admin.project.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DbProbeMapper {
    @Select("select 1")
    int ping();
}
```

文件：`src/main/java/com/example/admin/framework/web/DbProbeController.java`

```java
package com.example.admin.framework.web;

import com.example.admin.common.response.ApiResponse;
import com.example.admin.project.system.mapper.DbProbeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("dev")
@RestController
@RequiredArgsConstructor
public class DbProbeController {
    private final DbProbeMapper mapper;

    @GetMapping("/dev/db-check")
    public ApiResponse<Integer> check() {
        return ApiResponse.success(mapper.ping());
    }
}
```

每个 Mapper 标注 `@Mapper`，不需要同时在启动类添加广泛的 `@MapperScan`。`@RequiredArgsConstructor` 为 `final` 字段生成构造函数，由 Spring 注入实例。

### 4. 加载变量后启动

先停止前一篇启动的 Java 进程。在启动应用的 PowerShell 终端执行：

```powershell
Get-Content -LiteralPath '.env' | ForEach-Object {
    if ($_ -match '^([A-Z_][A-Z0-9_]*)=(.*)$') {
        [Environment]::SetEnvironmentVariable($Matches[1], $Matches[2], 'Process')
    }
}
.\mvnw.cmd spring-boot:run
```

新开的终端不会继承另一个终端的 Process 变量，需要重新加载。VS Code 的运行按钮也不一定继承这个终端变量；先统一用终端启动，避免同时运行两个 Java 进程。

在 Apifox 调用：

```http
GET http://localhost:8081/dev/db-check
```

预期 HTTP 200，`data` 为 1。只有 Java 编译通过，还不能证明数据库连接或 SQL 能正常执行。

## 四、验收与排查

- [ ] MySQL 容器状态是 `healthy`。
- [ ] UI 客户端或容器客户端可以连接应用账号。
- [ ] `sys_user` 存在，唯一索引和状态约束存在。
- [ ] `/health` 和 `/dev/db-check` 都正常。
- [ ] `.env` 不进入 Git，`.env.example` 中没有实际密码。

| 错误 | 优先检查 |
| --- | --- |
| Connection refused | 容器是否运行、宿主机端口是否为 3307 |
| Access denied | `.env` 与已有数据卷内的账号密码是否一致 |
| Unknown database/table | 初始化是否执行、连接的是否为 `pulse_admin` |
| 无法解析 MYSQL_PASSWORD | 是否在同一个终端加载变量后启动 |
| XML 解析失败 | XML 根节点、DTD、namespace；看异常最底层的 Caused by |

暂时停止项目依赖可用 `docker compose stop`，下次 `docker compose start`。`docker compose down` 会删除容器和网络但默认保留命名卷；加 `-v` 才会删除卷，本教程日常操作不使用它。

## 五、知识沉淀与面试小题

1. **容器重建后数据还在吗？** 数据在命名卷里，只要卷保留并重新挂载，数据就还在。
2. **为什么 Spring Boot 不能用 .env 就自动启动？** Compose 的变量替换和 Java 进程环境是两套机制，需要显式传递。
3. **能否用 init SQL 管理长期数据库升级？** 初始化脚本不会重跑；真实发布应保存增量迁移，后续可以使用 Flyway/Liquibase。

复习：[Compose](../kb/docker/04-docker-compose基础.md)、[约束](../kb/mysql/07-数据库约束与数据兜底.md)、[参数绑定](../kb/mybatis/03-参数绑定.md)。

下一篇：[04-用户模块完整开发](04-用户模块完整开发.md)。
