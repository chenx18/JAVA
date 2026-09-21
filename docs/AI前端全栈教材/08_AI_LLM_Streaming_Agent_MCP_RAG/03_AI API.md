# 第三章 AI API

## 一、本章具体知识点

- request
- response
- model selection
- token usage
- timeout
- retry
- cancellation
- rate limit
- error handling
- gateway

## 二、各知识点详细解释

生产应用中不要让前端直接暴露 provider secret：

```text
Browser
→ Your Backend
→ AI Gateway
→ Model Provider
```

Backend 可以统一处理：

- API key
- authorization
- model routing
- rate limit
- budget
- retry
- logging
- fallback

## 三、本章面试题与答案

### 题：为什么 API Key 不能直接写在 Vue 前端？

**答案：**

浏览器代码、网络请求和构建产物都属于用户可见环境，放在前端就无法真正保密。应该通过后端代理或 AI Gateway 持有密钥，同时后端根据用户身份和额度执行鉴权与限流。

---
