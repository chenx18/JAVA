# 第五章 SSE

## 一、本章具体知识点

- text/event-stream
- event
- data
- id
- retry
- EventSource
- fetch streaming
- reconnection
- server disconnect

## 二、各知识点详细解释

SSE 是基于 HTTP 的服务器到客户端事件流。典型数据：

```text
event: message
data: {"type":"text","delta":"hello"}

```

AI 前端也经常直接使用 fetch + ReadableStream，以便自定义 POST 请求、headers 和事件解析，而不局限于 EventSource。

前端流程：

```text
fetch
→ response.body
→ reader.read()
→ TextDecoder
→ SSE parser
→ domain event
```

## 三、本章面试题与答案

### 题：AI 项目为什么有时用 fetch streaming，而不是 EventSource？

**答案：**

EventSource 是标准 SSE 客户端 API，但主要面向 GET 和特定事件流模式。AI 对话通常需要 POST 请求、请求体、Authorization 和自定义协议，因此 fetch + ReadableStream 可以提供更灵活的控制。

---
