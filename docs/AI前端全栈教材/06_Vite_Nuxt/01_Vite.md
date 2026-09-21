# 第一章 Vite

## 一、本章具体知识点

- 为什么需要构建工具
- ESM
- Dev Server
- HMR
- Dependency Pre-Bundling
- module graph
- transform
- Plugin
- Build
- Rollup / Rolldown 生态

## 二、各知识点详细解释

### 1. 为什么前端需要构建工具

浏览器可以执行 JavaScript，但真实项目还需要 TypeScript、Vue SFC、CSS 预处理、模块图管理、代码分割、压缩和资源处理。构建工具负责把开发源代码转成适合浏览器/部署环境的产物。

### 2. Vite 开发模式

现代 Vite 开发服务器大量利用浏览器原生 ESM。开发时并不需要先把整个项目打包完再启动页面，而是按模块请求和转换，并维护 module graph。

### 3. HMR

文件改变：

```text
File Change
→ Module Graph
→ Invalidate Module
→ HMR Update
→ Browser Apply
```

真正的 HMR 不是简单“刷新浏览器”，而是尽可能只替换受影响的模块。

### 4. Dependency Pre-Bundling

第三方依赖可能包含大量 CommonJS/多文件模块。Vite 在开发阶段会预构建依赖，以减少浏览器需要处理的模块数量并统一依赖格式。

### 5. Plugin

Vite 插件系统允许介入 resolve/load/transform/build 等阶段。

## 三、本章面试题与答案

### 题：Vite 为什么开发启动通常很快？

**答案：**

核心是开发阶段大量利用原生 ESM，避免传统工具每次启动都先把整个应用完整打包，同时对依赖做预构建并维护模块图。最终速度还取决于依赖规模和项目插件。

### 题：HMR 是怎么工作的？

**答案：**

开发服务器维护 module graph。当文件变化时定位受影响模块，使其失效并生成 HMR update，通过 WebSocket 等机制通知浏览器，浏览器只更新相关模块而不是完整刷新整个页面。

---
