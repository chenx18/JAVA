# Promise / async await

> 专题：JavaScript 核心  
> 编号：J02

### 一句话答案

Promise 是异步状态的抽象，`async/await` 是 Promise 的语法糖。

### 核心原理

Promise 状态只能从 `pending` 变成 `fulfilled` 或 `rejected`，且不可逆。`await` 后面的代码会在 Promise 状态确定后继续执行。

### 项目里怎么用

```text
接口请求封装
并行请求 Promise.all
接口错误统一处理
登录后再拉用户信息
```

### 常见坑

- 可并行请求写成串行。
- `Promise.all` 任意一个失败导致整体失败。
- `async` 函数忘记 try/catch。

### 面试表达

我会根据请求是否有依赖决定串行还是并行。有依赖用 await 顺序执行，没有依赖用 Promise.all 提升速度，并在请求层统一处理错误。
