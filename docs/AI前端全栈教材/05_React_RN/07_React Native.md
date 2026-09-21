# 第七章 React Native

## 一、本章具体知识点

- React Native rendering
- JS runtime
- Native runtime
- JSI
- Fabric
- TurboModules
- Codegen
- New Architecture
- Native Modules
- Native Components
- FlatList
- Metro

## 二、各知识点详细解释

React Native 的目标是在 JavaScript/TypeScript 层描述 UI 和逻辑，并让原生平台负责实际 UI 能力。现代 RN 架构的关键是减少传统 bridge 的序列化成本，转向 JSI、Fabric、TurboModules、Codegen 等机制。

FlatList 等虚拟化列表对移动端性能很重要，不能把大列表简单写成大量 View。

## 三、本章面试题与答案

### 题：React Native 和 Web React 最大的区别？

**答案：**

组件模型和 Hooks 思维高度相似，但宿主环境不同。Web React 最终更新 DOM，而 RN 更新原生 host components。RN 还需要处理 JS 与原生运行时边界、移动端线程模型、原生能力和包体。

### 题：RN 为什么以前有 Bridge，现在强调 JSI？

**答案：**

传统 Bridge 需要跨边界传递和序列化消息，高频交互时开销明显。JSI 提供更直接的 JavaScript 与原生能力交互基础，新架构再结合 Fabric、TurboModules、Codegen，减少部分旧桥接模型的成本。

---
