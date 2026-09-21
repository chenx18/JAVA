# 第四章 Streaming

## 一、本章具体知识点

- full response
- streaming response
- chunk
- stream reader
- TextDecoder
- buffering
- parser
- backpressure
- abort
- retry
- reconnect

## 二、各知识点详细解释

普通请求：

```text
Request
→ Wait
→ Full Response
→ Render
```

Streaming：

```text
Request
→ chunk 1
→ chunk 2
→ chunk 3
→ ...
→ done
```

AI UI 通常需要把事件流解析成内部事件模型，再更新 Vue state：

```text
Network Stream
→ Parser
→ AI Event
→ State Machine
→ Vue State
→ UI
```

不要每一个网络 chunk 都无脑触发昂贵的全树更新，可以通过 buffer/batch 控制更新频率。

## 三、本章面试题与答案

### 题：Streaming 和普通 HTTP Response 有什么区别？

**答案：**

普通响应通常在完整 body 准备后交给应用；Streaming 允许应用边接收边处理，因此用户可以更早看到结果。AI 场景中可以显著改善首 token 到完整回答之间的感知体验，但也增加了解析、取消、断线和状态管理复杂度。

---
