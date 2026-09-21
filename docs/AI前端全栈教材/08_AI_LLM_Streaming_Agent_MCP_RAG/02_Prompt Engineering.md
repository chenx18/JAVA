# 第二章 Prompt Engineering

## 一、本章具体知识点

- system instruction
- developer instruction
- user input
- few-shot
- constraints
- output schema
- prompt injection
- context construction

## 二、各知识点详细解释

Prompt 的本质是构造模型输入上下文，而不是一句神秘的“魔法指令”。高质量应用应把固定规则、用户输入、检索内容、工具结果分层组织，并尽量把可验证约束放到 schema 与程序侧。

不要把外部网页、用户文本、Tool 返回内容和系统指令放在同一个“完全可信”的层级里。

## 三、本章面试题与答案

### 题：为什么 Prompt Injection 难以完全靠 Prompt 解决？

**答案：**

模型看到的文本本质上仍是上下文中的数据和指令，外部内容可能通过自然语言诱导模型改变行为。因此真正的安全边界不能只依赖提示词，应通过权限、工具白名单、schema 校验、输出过滤和人工确认等程序性控制建立。

---
