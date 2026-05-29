# 01-Validation是什么

Validation 是参数校验机制。

它用于检查前端传过来的数据是否符合后端要求。

---

## 一句话理解

```text
Validation = 请求进入业务逻辑前的字段检查。
```

比如新增部门时，前端传：

```json
{
  "id": 0,
  "name": ""
}
```

这类数据明显不合理：

```text
id 不应该小于 1
name 不应该为空
```

如果没有 Validation，就要在 Service 里手写很多判断。

---

## Validation 解决什么问题

它解决的是请求参数的基础合法性问题，例如：

```text
不能为空
不能是空字符串
数字不能小于某个值
字符串长度不能超过限制
邮箱格式必须正确
```

这些规则比较通用，适合交给 Validation 处理。

---

## Validation 的执行位置

典型流程：

```text
前端发送 JSON
        ↓
@RequestBody 把 JSON 转成 Java 对象
        ↓
@Valid 触发字段校验
        ↓
校验通过：进入 Service
校验失败：抛出异常
        ↓
GlobalExceptionHandler 统一返回错误信息
```

---

## Validation 不负责什么

Validation 不负责复杂业务规则。

例如：

```text
部门名称是否重复
用户是否有权限删除
删除部门前是否还有用户属于这个部门
订单状态是否允许修改
```

这些应该放在 Service 中处理。

---

## 与前端表单校验的关系

前端可以做校验，但后端仍然必须校验。

因为请求不一定来自正常页面，也可能来自：

```text
Apifox
Postman
脚本
恶意请求
其他系统调用
```

所以后端不能完全相信前端传来的数据。
