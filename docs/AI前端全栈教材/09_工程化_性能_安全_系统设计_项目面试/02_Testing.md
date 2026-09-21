# 第二章 Testing

## 一、本章具体知识点

- Unit Test
- Integration Test
- E2E
- Mock
- Stub
- Fixture
- Vitest
- Playwright
- Vue Test Utils
- Contract Test
- AI test

## 二、各知识点详细解释

Unit Test 测最小逻辑单元；Integration Test 测多个模块协作；E2E 测真实用户流程。

不要把所有问题都用 E2E 测，因为 E2E 通常更慢、更脆弱。

Vue 项目可以覆盖：

```text
composable
component
store
API integration
user flow
```

AI 项目新增：

```text
stream parser
message reducer
tool arguments
agent workflow
retry behavior
prompt regression
```

## 三、本章面试题与答案

### 题：单元测试和 E2E 怎么分工？

**答案：**

单元测试验证局部逻辑，速度快、失败定位清晰；集成测试验证模块协作；E2E 验证核心用户路径。生产项目通常按测试金字塔合理分配，而不是所有逻辑都通过浏览器测试。

### 题：AI 输出不稳定怎么测试？

**答案：**

不能把生成文本的每个字符都作为固定断言。可以把 Tool 调用、结构化输出、状态机转换、权限校验等确定性部分做精确测试；模型层通过固定数据、mock provider、schema 验证和回归样本集合控制变动。

---
