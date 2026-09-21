# 第十四章 Vue Router

## 一、本章具体知识点

- createRouter
- history
- route record
- nested routes
- params
- query
- meta
- lazy loading
- navigation guard
- scroll behavior

## 二、各知识点详细解释

Vue Router 把 URL 与组件树建立映射。

```text
URL
→ 匹配 Route Record
→ 产生 route
→ 渲染组件
```

权限路由常见模型：

```text
User
→ Role
→ Permission
→ Route Meta
→ Navigation Guard
```

但后端 API 必须再次授权，不能把前端路由守卫当成真正权限边界。

## 三、本章面试题与答案

### 题：为什么前端路由权限不等于真正权限？

**答案：**

前端代码运行在用户环境，用户可以直接调用 API 或修改前端逻辑，所以路由守卫只能控制 UI 可见性和导航体验。真正资源权限必须在服务端根据用户身份和权限校验。

---
