# 16 MySQL 查询、MyBatis 映射与索引

SQL先表达正确关系，再用执行计划验证性能。Mapper连接参数与Java结果，数据库约束和索引仍按真实业务及访问模式设计。

## 一、本章目录

- [JOIN、过滤与聚合](#k01)
- [参数绑定、动态 SQL 与映射](#k02)
- [B+Tree、联合与覆盖索引](#k03)
- [执行计划与深分页](#k04)
- [知识小结](#summary)
- [面试题与答案](#interview)

## 二、知识讲解

<a id="k01"></a>

### 1. JOIN、过滤与聚合

WHERE过滤行，GROUP BY组织分组，HAVING过滤分组结果，ORDER BY排序，LIMIT限制结果数量。逻辑阶段不等于优化器物理执行顺序。

LEFT JOIN保留左表未匹配行，但右表条件放WHERE可能排除NULL补行；放ON限制匹配，放WHERE限制结果，需要根据目标选择。一对多连接还会产生重复主表信息，聚合计数不能直接忽略。

```sql
SELECT d.id AS department_id, d.name AS department_name,
       l.id AS log_id, l.operation
FROM department d
LEFT JOIN department_log l ON l.department_id = d.id
WHERE d.id = 1
ORDER BY l.id DESC;
```

别名区分重复列名。resultMap的column对应结果集列标签，property对应Java属性。对一对多JOIN行直接LIMIT，与先分页主表再关联不是同一结果。

<a id="k02"></a>

### 2. 参数绑定、动态 SQL 与映射

MyBatis #{value}通常生成预编译参数绑定，${value}做文本替换，不能直接接不可信输入。表名、列名、排序方向不能简单靠值占位符实现，应服务端白名单映射。

resultType按规则映射对象，resultMap显式描述属性、标识、association/collection等；`<id>`是映射识别对象的标志，不会替数据库创建主键。

where/set/if/foreach帮助拼装条件，需防空集合、空更新和过宽SQL。主键回填、update/delete影响行数受语句与驱动行为影响，要按契约验证，不把任何0都等同不存在。

<a id="k03"></a>

### 3. B+Tree、联合与覆盖索引

InnoDB聚簇主键组织数据，二级索引通常保存定位主键的值。覆盖索引能直接提供所需列，减少回表；索引占空间并增加写维护成本。

联合索引(a,b,c)按列顺序组织，最左前缀解释高效定位基础，但范围、排序、覆盖、索引条件下推和优化器策略影响实际利用，不能机械说缺a就绝对不读该索引。

函数、隐式转换、前导通配等可能降低合适索引使用，具体看表达式索引、排序规则和版本。按查询组合设计，而非所有列加索引。

<a id="k04"></a>

### 4. 执行计划与深分页

EXPLAIN看访问方式、key、估计rows和Extra，EXPLAIN ANALYZE会实际执行并提供运行信息，应选择合适环境。统计与实际分布不同会影响估计，Using index也不等于绝对最快。

```sql
SELECT id, user_name
FROM app_user
WHERE status = 1 AND id > 100
ORDER BY id
LIMIT 20;
```

这是游标分页示意，依赖稳定排序键；offset深分页可能扫描并跳过大量数据。复合排序需处理并列键，总数与记录查询条件应一致，并发变化下的语义也要约定。

慢查询先拿SQL、参数形态、索引、数据量和计划再优化，不凭单次小数据耗时下结论。

<a id="summary"></a>

## 三、知识小结

先保证关系与分页正确，再明确映射和参数边界，最后用索引及执行计划优化。正确结果和真实数据规模比口诀重要。

参考：[MySQL 8.4 Reference](https://dev.mysql.com/doc/refman/8.4/en/)；[MyBatis XML](https://mybatis.org/mybatis-3/sqlmap-xml.html)。示例按标注环境运行，版本相关能力以目标版本为准。

<a id="interview"></a>

## 四、面试题与答案

<a id="java16-01"></a>

### JAVA16-01 [P0·原理] LEFT JOIN右表条件写WHERE可能有什么变化？

**回答：** 未匹配补出的NULL行可能被过滤，结果不再保留全部左行。ON控制匹配条件，WHERE控制最终结果，需要按业务目的选择。

对应讲解：[JOIN、过滤与聚合](#k01)。

<a id="java16-02"></a>

### JAVA16-02 [P0·基础] MyBatis两种参数写法有什么区别？

**回答：** 井号占位通常绑定参数值，美元占位直接拼文本。动态标识符用白名单，不能直接拼用户输入；绑定值也不代替业务授权。

对应讲解：[参数绑定、动态 SQL 与映射](#k02)。

<a id="java16-03"></a>

### JAVA16-03 [P1·原理] 最左前缀能否机械判断是否使用索引？

**回答：** 不能，它解释列顺序和高效定位基础，但覆盖、排序、范围和优化器策略也影响计划。应看实际执行证据，而不是一条口诀判断所有查询。

对应讲解：[B+Tree、联合与覆盖索引](#k03)。

<a id="java16-04"></a>

### JAVA16-04 [P1·工程取舍] 一对多JOIN直接LIMIT为什么可能分页错？

**回答：** 限制的是连接结果行，可能截断某个实体的明细，且主实体数量不符合目标。应先分页主表再关联或采用明确聚合方案。

对应讲解：[JOIN、过滤与聚合](#k01)。

<a id="java16-05"></a>

### JAVA16-05 [P1·工程取舍] 为什么深分页可能需要游标方案？

**回答：** 大offset可能扫描并跳过很多行，游标按稳定排序键继续查询可降低这类成本。但需处理并列键、筛选、并发变化和无法任意跳页等取舍，执行计划仍要验证。

对应讲解：[执行计划与深分页](#k04)。
