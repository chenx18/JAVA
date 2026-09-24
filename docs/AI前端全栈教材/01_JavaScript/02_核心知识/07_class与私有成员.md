# 07 class与私有成员

class在原型体系上提供更明确的构造、继承和私有成员规则。读类代码先确认成员存在哪里，再沿初始化顺序分析，不把所有成员都当成实例字段。

## 一、本章目录

- [实例、原型与静态成员](#k01)
- [字段初始化顺序](#k02)
- [super 与两条继承链](#k03)
- [私有成员与品牌校验](#k04)
- [继承、组合与实际选择](#k05)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. 实例、原型与静态成员

```js
class Counter {
  value = 0;
  increment() { return ++this.value; }
  reset = () => { this.value = 0; };
  static kind = 'counter';
}
const a = new Counter(), b = new Counter();
console.log(a.increment === b.increment, a.reset === b.reset); // true false
console.log(Counter.kind, a.kind); // counter undefined
```

value与reset在每个实例上创建，increment位于Counter.prototype共享，kind在Counter自身。箭头字段固定实例this但增加每实例函数；普通原型方法共享但被取出后需处理调用接收者。

class声明有TDZ，类体默认严格，必须按构造规则调用。原型上的方法一般不可枚举，这与手工直接给原型赋值创建的默认描述符也不同。

<a id="k02"></a>

### 2. 字段初始化顺序

```js
const events = [];
class Base {
  x = (events.push('base field'), 1);
  constructor() { events.push('base body'); }
}
class Child extends Base {
  y = (events.push('child field'), 2);
  constructor() { super(); events.push('child body'); }
}
new Child();
console.log(events); // ['base field','base body','child field','child body']
```

先基类字段，再基类构造体；super完成建立派生this后，初始化派生字段，再继续派生构造体。静态字段和static块在类定义求值时按声明顺序执行，不等到每次new。

若基类构造体调用会被子类覆写的方法，实际可能进入子方法，但子字段尚未初始化。避免让构造期间的虚调用依赖未准备好的派生状态。

<a id="k03"></a>

### 3. super 与两条继承链

extends一般建立Child→Base以及Child.prototype→Base.prototype两条关系。前者支持静态继承，后者支持实例方法继承。

```js
class Base { greet() { return this.name; } }
class Child extends Base {
  constructor() { super(); this.name = 'child'; }
  greet() { return super.greet() + '!'; }
}
console.log(new Child().greet()); // child!
console.log(Object.getPrototypeOf(Child) === Base); // true
```

super.method从方法所属位置的父级查找，但以当前this调用，不会创建一个父实例。派生构造器通常必须先super再用this；显式返回对象存在特殊路径。基类构造返回非null对象可替换实例，派生构造返回非undefined原始值会报错，不能直接套普通function的返回规则。

<a id="k04"></a>

### 4. 私有成员与品牌校验

#字段/方法/访问器属于语言级私有成员，不是名字恰好为'#x'的字符串属性，也不是Symbol键；反射枚举不能直接取得它们。

```js
class Secret {
  #value = 1;
  read() { return this.#value; }
}
const value = new Secret();
console.log(Reflect.ownKeys(value)); // []
try { Secret.prototype.read.call({}); }
catch (error) { console.log(error.name); } // TypeError
```

访问时检查接收者是否具有对应私有成员。Object.create(Secret.prototype)连接了原型，但未完成私有初始化；透明Proxy作为this也可能失败。静态私有成员同样要看接收者，继承得到的静态方法若用this访问父类静态私有字段可能不适用于子类。

下划线是约定，Symbol防冲突，闭包通过可见性封装局部状态，#成员由语法和品牌检查约束；这些不等价。

<a id="k05"></a>

### 5. 继承、组合与实际选择

继承适合稳定的is-a关系和明确可替换契约；组合把可变行为注入协作对象，常更容易单独测试。不要为了复用几个函数创建深继承树。

读class题时依次检查：成员位置、初始化时机、super查找起点、this接收者、私有品牌和返回值。要解释真实业务取舍，则补上共享内存、回调绑定和可替换性成本。

<a id="summary"></a>

## 三、知识小结

字段随实例、普通方法在原型、static随类；初始化按基类到派生类；super控制查找起点，this控制接收者；私有成员不是普通键。

参考：[MDN Classes](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Classes)。较新 API 按目标运行时核对支持，示例各自独立。

<a id="interview"></a>

## 四、面试题与答案

<a id="c07-01"></a>

### C07-01 [P0·基础] class字段、方法、static分别放在哪里？

**回答：** 实例字段及箭头字段属于每个实例，普通实例方法通常在prototype共享，static字段和方法属于类自身。位置决定共享范围和查找路径，不能仅凭写在类体里就认为都在实例上。

对应讲解：[实例、原型与静态成员](#k01)。

<a id="c07-02"></a>

### C07-02 [P0·原理] 派生类字段什么时候初始化？

**回答：** 基类字段和基类构造体先执行，super建立派生this后再初始化派生字段，然后执行super之后的派生构造代码。父构造期间调用被覆写的方法可能提前访问未初始化子字段。

对应讲解：[字段初始化顺序](#k02)。

<a id="c07-03"></a>

### C07-03 [P1·原理] super.method的this是父对象吗？

**回答：** 不是。super从相应父级找方法，调用接收者仍是当前this，所以可读子实例属性。静态方法与实例方法查找的是不同继承链。

对应讲解：[super 与两条继承链](#k03)。

<a id="c07-04"></a>

### C07-04 [P1·原理] 为什么有原型关系的对象也不能读#字段？

**回答：** 私有字段访问不仅看原型链，还检查接收者是否真正拥有该类初始化的私有成员。Object.create或Proxy可能有看似相同方法路径却不具备品牌，借用方法会抛错。

对应讲解：[私有成员与品牌校验](#k04)。
