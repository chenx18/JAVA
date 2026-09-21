# 第六章 AI Chat 状态机

## 一、本章具体知识点

- idle
- submitting
- streaming
- tool-calling
- done
- error
- cancelled
- retrying
- message parts

## 二、各知识点详细解释

AI Chat 不应只有一个 `loading: boolean`。

更准确：

```text
IDLE
↓
SUBMITTING
↓
STREAMING
↓
TOOL_CALLING
↓
STREAMING
↓
DONE
```

异常状态：

```text
ERROR
CANCELLED
TIMEOUT
```

Message 也不一定只有 role/content，可以包含：

```text
text
reasoning
tool-call
tool-result
source
file
error
artifact
```

## 三、本章面试题与答案

### 题：为什么 AI Chat 不适合只用 loading boolean？

**答案：**

AI 请求至少存在提交、流式输出、工具执行、完成、错误、取消、重试等不同阶段，它们的 UI 和允许操作并不相同。用显式状态机或 discriminated union 可以防止状态组合混乱，例如 done 时不能继续显示“停止生成”。

---
