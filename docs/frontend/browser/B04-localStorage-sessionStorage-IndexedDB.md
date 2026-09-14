# localStorage / sessionStorage / IndexedDB

> 专题：浏览器、网络、安全  
> 编号：B04

### 一句话答案

localStorage 是长期小容量同步存储，sessionStorage 是会话级存储，IndexedDB 是浏览器端大容量异步数据库。

### 核心对比

```text
localStorage：持久保存，同源共享，同步 API
sessionStorage：标签页会话级，关闭标签页清除
IndexedDB：异步、大容量、结构化数据
```

### 项目里怎么用

- localStorage 存主题、非敏感配置。
- sessionStorage 存临时页面状态。
- IndexedDB 存离线数据、缓存文档、较大结构化数据。

### 常见坑

- localStorage 是同步 API，大量读写会阻塞主线程。
- 不要存敏感 token 或隐私数据。
- 不同标签页 sessionStorage 不共享。

### 面试表达

我会根据数据大小、生命周期和安全性选择存储。小配置可用 localStorage，临时状态用 sessionStorage，大数据或离线场景用 IndexedDB。敏感信息尽量不要放前端存储。
