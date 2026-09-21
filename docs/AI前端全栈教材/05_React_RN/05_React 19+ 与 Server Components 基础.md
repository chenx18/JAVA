# 第五章 React 19+ 与 Server Components 基础

## 一、本章具体知识点

- Actions 基础
- transitions
- Suspense
- Server Components
- Server Functions
- useActionState
- useOptimistic
- ViewTransition

## 二、各知识点详细解释

React 19.3 已将 View Transitions 和 Fragment Refs 稳定化，并继续推进 Server Components 等能力。([react.dev](https://react.dev/blog/2026/09/09/react-19-3))

Server Components 的核心不是“SSR”。Server Component 可以在服务端环境读取服务端资源并生成 RSC payload；它不会把组件本身发送到浏览器，也不能使用交互型 client APIs，需要把交互部分组合成 Client Component。([react.dev](https://react.dev/reference/rsc/server-components))

## 三、本章面试题与答案

### 题：Server Component 和 SSR 一样吗？

**答案：**

不一样。SSR 是渲染策略，强调服务端生成 HTML；Server Components 是组件模型，允许部分组件只在服务器环境执行。RSC 输出可以参与 SSR，但两者不是同义词。

### 题：use client 是什么？

**答案：**

它声明一个客户端边界，使该模块及其依赖进入客户端组件体系。它不是“把某个函数标记成在浏览器运行”的简单开关，也不是 Server Components 的反义词；Server Component 本身不需要使用某个“use server”指令来声明。([react.dev](https://react.dev/reference/rsc/server-components))

---
