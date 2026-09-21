# 第七章 Tool Calling / Function Calling

## 一、本章具体知识点

- tool schema
- tool discovery
- tool invocation
- arguments
- validation
- tool execution
- tool result
- authorization
- retry
- idempotency

## 二、各知识点详细解释

Tool Calling 的核心：

```text
User
→ LLM
→ Tool Call
→ Backend validates
→ Tool execution
→ Tool Result
→ LLM
→ Final Answer
```

重点：

```text
LLM decides which tool
≠
LLM automatically has permission to run it
```

Tool 参数必须经过 runtime schema validation，并且后端再次做权限校验。

## 三、本章面试题与答案

### 题：为什么不能直接执行模型生成的工具参数？

**答案：**

模型输出属于不可信输入。必须先做 schema validation，再根据当前用户权限、租户、业务策略判断是否允许调用，必要时还应经过人工确认。最终执行层不能把模型文本当成可信代码。

### 题：Tool Calling 和普通 API 调用有什么区别？

**答案：**

普通 API 调用通常由程序逻辑确定调用哪个接口；Tool Calling 把“选择工具和参数”部分交给模型，但真正执行仍应由受控的应用程序完成。它增加了模型决策和安全验证这一层。

---
