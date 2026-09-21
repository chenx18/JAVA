# 第九章 HTTP/1.1、HTTP/2、HTTP/3

## 一、本章具体知识点

- HTTP method
- status code
- header
- keep-alive
- chunked
- HTTP/2 streams
- multiplexing
- HPACK
- HTTP/3
- QUIC
- connection migration

## 二、各知识点详细解释

HTTP/1.1 以请求/响应和持久连接为基础，但同一连接上的请求处理存在队头阻塞等问题。

HTTP/2 使用二进制帧、stream、多路复用、HPACK 等机制，可以在一个连接上并行承载多个请求。

HTTP/3 建立在 QUIC 之上，QUIC 使用 UDP，并在传输层重新实现可靠性与拥塞控制，同时减少 TCP 层级的队头阻塞影响。

## 三、本章面试题与答案

### 题：HTTP/2 为什么比 HTTP/1.1 更适合现代 Web？

**答案：**

HTTP/2 通过二进制帧和多路复用，让多个逻辑流可以共享一个连接，减少传统 HTTP/1.1 下大量并行连接和请求排队的限制；同时 HPACK 减少 header 开销。但它仍可能受到底层 TCP 丢包影响。

### 题：HTTP/3 与 HTTP/2 最大的底层变化？

**答案：**

HTTP/2 基于 TCP，HTTP/3 基于 QUIC。QUIC 将可靠传输、多路复用、TLS 等能力组合在 UDP 之上，从而减少 TCP 级队头阻塞和连接建立成本方面的一些限制。

---
