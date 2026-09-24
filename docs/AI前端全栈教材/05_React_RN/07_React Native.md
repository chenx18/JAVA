# 07 React Native 与原生边界

React Native复用React组件和Hooks思想，但宿主是原生平台，布局、资源、权限和性能路径与浏览器不同。具备Web经验后，需要重点补齐这些边界。

## 一、本章目录

- [宿主组件与布局](#k01)
- [JSI、Fabric、TurboModules 与 Codegen](#k02)
- [线程、动画与性能](#k03)
- [平台能力、生命周期与发布](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 宿主组件与布局

View、Text、Pressable、TextInput、Image等映射到原生宿主能力，不是DOM标签。没有浏览器document/querySelector，也不能直接套全部CSS；StyleSheet及样式对象使用RN支持的属性和单位规则，Flex默认方向等与Web习惯有差异。

```tsx
// react-native-only
import { FlatList, Text } from 'react-native';
type Item = { id: string; name: string };
export function UserList({ items }: { items: Item[] }) {
  return <FlatList
    data={items}
    keyExtractor={item => item.id}
    renderItem={({ item }) => <Text>{item.name}</Text>}
  />;
}
```

ScrollView通常一次渲染其子内容，FlatList/SectionList提供虚拟化能力；长列表仍需合理窗口、稳定key、行渲染成本和可变尺寸策略。虚拟化不是让无穷数据与图片无代价。

<a id="k02"></a>

### 2. JSI、Fabric、TurboModules 与 Codegen

旧桥接架构常通过异步序列化消息连接JS和Native，高频跨边界交互可能有成本。JSI提供JS与原生交互基础，Fabric负责新渲染器相关能力，TurboModules改善原生模块体系，Codegen根据声明生成部分接线。

这些是不同层，不应把所有改进都称为“JSI取消了全部线程切换”。同步原生调用也可能阻塞，数据复制、线程调度和原生工作仍有成本。具体架构默认状态和兼容性随RN版本变化，老项目迁移需验证依赖模块支持。

<a id="k03"></a>

### 3. 线程、动画与性能

JS繁忙可能拖慢事件和状态处理，原生/UI侧工作也可能造成掉帧。动画库的UI线程或worklet方案能减少某些跨边界压力，但仍受原生布局、图片解码与内存约束。

排查使用目标设备和release构建，区分JS执行、原生UI、网络、图片和列表问题。稳定renderItem/props、合适虚拟化、图片尺寸与缓存、避免频繁跨边界大数据传输都有不同适用条件。

开发模式的日志与调试工具可能明显影响性能，不应直接据此评价生产交互。

<a id="k04"></a>

### 4. 平台能力、生命周期与发布

移动应用需要处理前后台AppState、权限拒绝、网络切换、深链接、键盘、安全区域和系统版本。Web的页面卸载思维不能直接代替原生应用生命周期。

敏感凭据按平台能力使用合适安全存储，不把普通持久化KV库当加密保险箱。原生模块可能需要Android/iOS构建和签名配置；OTA更新也受平台政策、原生接口兼容与回滚约束。

测试包含真机、低端设备、离线、权限拒绝、前后台切换和升级，不仅是浏览器预览或模拟器一次点击。

<a id="summary"></a>

## 三、知识小结

React模型可复用，宿主能力不能照搬。RN学习按组件与布局→JS/Native架构→线程性能→平台生命周期与发布展开，所有优化在目标设备验证。

参考：[React Native Architecture](https://reactnative.dev/architecture/overview)；[React Native FlatList](https://reactnative.dev/docs/flatlist)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="react07-01"></a>

### REACT07-01 [P0·基础] React Native与Web React最大的差别是什么？

**回答：** 组件和Hooks思想相似，但最终宿主是原生组件而非DOM，布局、权限、资源和线程边界不同。浏览器API和完整CSS不能直接照搬。

对应讲解：[宿主组件与布局](#k01)。

<a id="react07-02"></a>

### REACT07-02 [P1·原理] JSI、Fabric、TurboModules分别负责什么？

**回答：** JSI是JS与原生交互基础，Fabric涉及渲染器，TurboModules涉及原生模块体系，Codegen辅助类型声明到原生接线。它们不是同一个开关，也不消除所有成本。

对应讲解：[JSI、Fabric、TurboModules 与 Codegen](#k02)。

<a id="react07-03"></a>

### REACT07-03 [P1·工程取舍] RN列表卡顿如何定位？

**回答：** 在目标设备release环境区分JS、UI线程、图片和列表节点成本，再优化虚拟化、行渲染和跨边界数据。仅加memo或只看开发模拟器帧率不充分。

对应讲解：[线程、动画与性能](#k03)。

<a id="react07-04"></a>

### REACT07-04 [P1·工程取舍] Web经验迁移到RN还需要补什么？

**回答：** 补原生生命周期、权限、深链接、安全存储、构建签名、依赖兼容和真机验证。业务逻辑可复用，但平台边界与发布不是浏览器部署的简单翻版。

对应讲解：[平台能力、生命周期与发布](#k04)。
