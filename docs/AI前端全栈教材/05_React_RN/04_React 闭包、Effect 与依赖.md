# 04 React 闭包、Effect 依赖与请求竞态

每次render提供一组新的变量绑定，回调关联创建时的环境。旧值问题应从快照、触发条件和副作用归属分析，而不是用空依赖数组强行停止检查。

## 一、本章目录

- [旧闭包与函数式更新](#k01)
- [依赖数组是同步契约](#k02)
- [异步请求的清理与提交](#k03)
- [StrictMode 与正确恢复](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 旧闭包与函数式更新

事件回调和Effect创建时会关联某次render的props/state。timer后来执行时仍可能读取那次count，不会自动转到新render的局部绑定。

函数式setCount(previous=>previous+1)适合表达基于最新队列状态的更新；它不能让回调中所有其他旧变量自动变新。需要读取最新非响应式值时可按场景用ref或受支持的Effect Event，但仍需明确哪些变化应重新同步。

所谓stale closure并不是JS闭包把所有对象复制了，而是旧函数继续访问旧环境或旧计算结果。

<a id="k02"></a>

### 2. 依赖数组是同步契约

Effect使用的响应式值应体现在依赖中，依赖比较常基于Object.is。[]表达没有依赖变化需要重新同步，不是“我不想让linter提醒”的开关。

如果每次render创建对象使Effect总重跑，先看能否把对象构造放进Effect、使用原始依赖或重新设计数据流，而不一定先用useMemo包所有东西。纯派生值可以直接计算，用户点击动作通常放事件处理器，不必借Effect绕一圈。

遵循exhaustive-deps等检查，确需例外时说明契约并测试，而不是删除警告行。

<a id="k03"></a>

### 3. 异步请求的清理与提交

```tsx
import { useEffect, useState } from 'react';
export function Search({ query }: { query: string }) {
  const [text, setText] = useState('');
  useEffect(() => {
    const controller = new AbortController();
    let active = true;
    async function load() {
      try {
        const response = await fetch('/api/search?q=' + encodeURIComponent(query), {
          signal: controller.signal
        });
        if (!response.ok) throw new Error('HTTP ' + response.status);
        const value = await response.text();
        if (active) setText(value);
      } catch (error) {
        if (active && !controller.signal.aborted) setText('加载失败');
      }
    }
    void load();
    return () => { active = false; controller.abort(); };
  }, [query]);
  return <p>{text}</p>;
}
```

cleanup在相关Effect重新同步前及卸载时运行；取消减少浪费，active保护迟到提交。真实结构化接口仍需schema和独立error/loading状态，这里只展示生命周期。

不要把Effect回调直接声明成async来返回Promise，Effect需要返回清理函数或undefined；在内部调用async函数即可。

<a id="k04"></a>

### 4. StrictMode 与正确恢复

开发StrictMode会额外检查render纯度和Effect建立/清理，具体范围依启用位置与版本。目的在于暴露缺少清理或不可重复的逻辑，不应仅用ref阻止第二次执行而保留第一次泄漏的连接。

可靠副作用要能建立、清理、再建立；服务器写操作的重复防护还需要幂等和正确触发位置。不要把购买、发送消息等用户动作无条件放挂载Effect导致意外重发。

测试改变query、快速切换、卸载、取消后返回与开发检查，验证最终提交属于当前运行。

<a id="summary"></a>

## 三、知识小结

闭包读创建时环境，依赖声明何时重同步，cleanup结束旧运行，版本/active保护提交。函数式更新解决前值递推，不能代替所有依赖与权限设计。

参考：[React Synchronizing with Effects](https://react.dev/learn/synchronizing-with-effects)；[React Removing Effect Dependencies](https://react.dev/learn/removing-effect-dependencies)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="react04-01"></a>

### REACT04-01 [P0·原理] React为什么出现stale closure？

**回答：** 旧回调关联旧render的变量环境，新render创建新绑定，不会改写旧闭包。应根据需要用正确依赖、函数式更新、受控ref或重设计数据流，不能一律加空依赖数组。

对应讲解：[旧闭包与函数式更新](#k01)。

<a id="react04-02"></a>

### REACT04-02 [P0·原理] Effect依赖为什么不能随意省略？

**回答：** 依赖描述哪些输入变化需要重新与外部系统同步，省略可能让订阅或请求持续使用旧值。先改善结构和依赖稳定性，不用关闭lint代替证明。

对应讲解：[依赖数组是同步契约](#k02)。

<a id="react04-03"></a>

### REACT04-03 [P0·工程取舍] cleanup中abort后为何仍检查active？

**回答：** 取消可能不被底层支持或发生在结果已到达之后。active/requestId在提交点阻止旧运行污染当前状态，abort主要减少无用工作。

对应讲解：[异步请求的清理与提交](#k03)。

<a id="react04-04"></a>

### REACT04-04 [P1·原理] StrictMode下Effect多执行一次应该怎样处理？

**回答：** 检查副作用能否正确建立、清理再建立，修复订阅和资源生命周期。不要仅用标志压住开发检查；写操作重复还要通过动作触发和服务端幂等解决。

对应讲解：[StrictMode 与正确恢复](#k04)。
