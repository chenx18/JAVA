# 第二章 React Hooks

## 一、本章具体知识点

- useState
- useReducer
- useEffect
- useLayoutEffect
- useRef
- useContext
- useMemo
- useCallback
- useId
- useTransition
- useDeferredValue
- 自定义 Hook

## 二、各知识点详细解释

### useState

state 是组件需要“记住”的信息。更新 state 会安排一次重新渲染。React 官方把 useState 和 useReducer 归类为 State Hooks。([react.dev](https://react.dev/reference/react/hooks))

### useRef

ref 保存不会因为更新而触发 render 的信息，常见用途是 DOM node、timer id、第三方实例。React 官方明确指出更新 ref 不会重新渲染组件。([react.dev](https://react.dev/reference/react/hooks))

### useEffect

Effect 用于与外部系统同步，例如网络、DOM、动画或非 React 库；官方文档特别强调，不要把 Effect 当成普通数据流编排工具。([react.dev](https://react.dev/reference/react/hooks))

### useMemo/useCallback

用于在特定情况下缓存计算结果或函数引用。它们不是默认必需品，先通过性能分析确认瓶颈。

## 三、本章面试题与答案

### 题：useEffect 是干什么的？

**答案：**

Effect 用于让组件与外部系统同步，例如建立订阅、调用浏览器 API、连接第三方库或执行网络副作用。它不是“组件生命周期的万能替代”，如果只是根据 props/state 计算另一个值，通常应该直接计算或使用 memo，而不是额外创建 Effect。

### 题：useRef 为什么不会触发重新渲染？

**答案：**

ref 保存的是跨 render 持久化的数据容器，修改 `.current` 不会安排 React render，因此适合保存不参与 UI 输出的数据。

---
