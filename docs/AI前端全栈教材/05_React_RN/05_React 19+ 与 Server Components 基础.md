# 05 React 19 系列、Actions 与 Server Components

新API应放在稳定的组件模型中理解。这里以React19系列及已核对的19.3发布资料说明能力，区分React公共API、框架支持和底层打包器接口。

## 一、本章目录

- [Actions、pending 与乐观更新](#k01)
- [Server Components 与 SSR](#k02)
- [use client、use server 与授权](#k03)
- [19.3 新能力与兼容性](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. Actions、pending 与乐观更新

Actions将某些异步提交与React状态管理结合，useActionState返回当前结果、动作入口及pending，useOptimistic表达等待结果期间的乐观状态，useFormStatus观察所属表单状态。

```tsx
import { useActionState } from 'react';
export function NameForm() {
  const [state, submit, pending] = useActionState(
    async (_previous: { message: string }, form: FormData) => {
      const name = String(form.get('name') ?? '').trim();
      return { message: name ? '收到：' + name : '请输入名称' };
    },
    { message: '' }
  );
  return <form action={submit}>
    <input name="name" aria-label="名称" />
    <button disabled={pending}>提交</button>
    <p>{state.message}</p>
  </form>;
}
```

示例只展示客户端Action契约，不是完整保存接口。乐观UI需要失败回滚、重复提交和版本归属策略；pending只描述相关操作阶段，不代表服务器权限、事务或幂等自动处理。

<a id="k02"></a>

### 2. Server Components 与 SSR

Server Component可在服务器环境执行，访问服务端资源并产生RSC协议结果；组件本身的客户端代码不必随交互包发送。SSR则是在服务端生成HTML的渲染策略，两者可以配合但不是同义词。

Client Component承担交互与客户端Hooks，但也可能参与初始服务端HTML生成，所以“Client”不等于只在浏览器才做任何工作。服务端模块不能因此随意把秘密放进传给客户端的props或输出中。

RSC跨边界的数据遵守React/框架支持的序列化协议，不要假定任意对象和函数都可传，也不简单等于只支持普通JSON。

<a id="k03"></a>

### 3. use client、use server 与授权

'use client'建立客户端模块边界及其依赖关系；'use server'用于Server Functions相关声明，不是标记任意Server Component的必需指令。边界影响代码打包与可调用能力。

Server Function可被客户端触发时仍是服务端入口，必须验证身份、参数、资源权限、幂等和速率，不因编译器生成调用协议就天然可信。输入校验与错误反馈应按业务契约设计。

React19的RSC公共模型与框架/打包器使用的底层接口稳定性不能混为一谈；官方仍建议相关集成锁定兼容版本。应用优先使用支持该模型的框架，不直接拼装内部协议。

<a id="k04"></a>

### 4. 19.3 新能力与兼容性

已核对的React19.3发布说明将ViewTransition和Fragment Refs列为稳定能力。ViewTransition与浏览器视图过渡API协作，仍需目标浏览器、交互和减少动画偏好支持；不能认为升级依赖就自动让所有页面有正确过渡。

Fragment Ref允许对一组子节点进行相应焦点、事件或测量操作而不强加额外包装DOM；它不是一个真实HTMLElement，API和约束应按文档使用，短Fragment语法也不能直接附任意属性。

新版本功能与React Compiler、框架路由、SSR支持各有接入条件。学习先理解解决的问题，再检查项目版本，不把每个新名字都列成所有岗位必考。

<a id="summary"></a>

## 三、知识小结

Actions组织提交体验，RSC划分服务端组件职责，SSR提供HTML，Server Functions仍需授权。新版本能力按公共API、框架集成和浏览器支持分别确认。

参考：[React 19.3 Release](https://react.dev/blog/2026/09/09/react-19-3)；[React Server Components](https://react.dev/reference/rsc/server-components)；[React useActionState](https://react.dev/reference/react/useActionState)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="react05-01"></a>

### REACT05-01 [P0·原理] Server Components与SSR一样吗？

**回答：** 不一样，RSC是组件执行和传输模型，SSR是服务器生成HTML的策略。二者可组合；Client Component也可能参与初始SSR，因此不能按名字简单划成只服务端或只浏览器。

对应讲解：[Server Components 与 SSR](#k02)。

<a id="react05-02"></a>

### REACT05-02 [P1·基础] use client与use server分别表达什么？

**回答：** 前者声明客户端模块边界，后者涉及Server Functions；Server Component不需要靠use server来标记。边界影响打包和调用协议，不代替身份与资源授权。

对应讲解：[use client、use server 与授权](#k03)。

<a id="react05-03"></a>

### REACT05-03 [P1·工程取舍] 乐观更新只要先改UI就够了吗？

**回答：** 还要定义失败回滚、并发提交、请求版本和服务器返回值如何对账。useOptimistic等帮助表达体验，但不能解决后端事务、幂等和权限。

对应讲解：[Actions、pending 与乐观更新](#k01)。

<a id="react05-04"></a>

### REACT05-04 [P1·工程取舍] React19.3新特性为何仍需环境检查？

**回答：** React包支持只是其中一层，ViewTransition等还涉及浏览器与框架协作，RSC底层集成也有版本兼容要求。应锁定工具链和回退行为，不以版本号替代真实验证。

对应讲解：[19.3 新能力与兼容性](#k04)。
