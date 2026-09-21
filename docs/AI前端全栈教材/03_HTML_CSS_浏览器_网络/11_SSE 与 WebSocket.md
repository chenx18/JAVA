# 第十一章 SSE 与 WebSocket

## 一、本章具体知识点

- Polling
- Long Polling
- SSE
- EventSource
- text/event-stream
- WebSocket handshake
- full duplex
- reconnect
- heartbeat

## 二、各知识点详细解释

SSE 基于 HTTP，服务器向客户端持续发送事件流，天然适合服务端单向推送；AI 文本生成就是典型场景。

WebSocket 建立后提供双向通信，适合实时协作、双向消息和游戏等场景。

AI 前端常见链路：

```text
Vue
→ HTTP request
→ SSE / ReadableStream
→ parse chunks
→ reactive state
→ incremental render
```

## 三、本章面试题与答案

### 题：SSE 和 WebSocket 怎么选？

**答案：**

如果主要是服务器持续向浏览器推送事件，例如 AI 流式回答、日志流，可以优先考虑 SSE；如果客户端和服务端都需要高频双向实时通信，例如实时协作，则 WebSocket 更合适。还要考虑代理、基础设施和断线重连需求。

### 题：AI Streaming 为什么常用 SSE？

**答案：**

很多 AI 对话场景主要是客户端发送一次请求，服务器持续向客户端推送文本和事件，通信模式与 SSE 很契合；同时它建立在 HTTP 生态上，服务端和浏览器处理比较直接。

---
