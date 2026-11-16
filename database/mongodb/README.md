# database/mongodb

MongoDB Java 驱动（同步 API）的用法示例。分成**离线可测**和**需要真实实例**两部分。

## 跑起来

```bash
./gradlew :database:mongodb:demo    # 完全离线，不需要 mongod
./gradlew :database:mongodb:test    # 10 个测试（4 个集成测试默认跳过）
```

## 为什么拆成两部分

`MongoQuery` 和 `Book` 不碰网络，只构造 `Bson` 对象和做 POJO 编解码，所以能离线测。
`MongoIntegrationTest` 需要真实数据库，用环境变量开关：

```bash
MONGO_URI="mongodb://localhost:27017" ./gradlew :database:mongodb:test
```

没设这个变量时，`@EnabledIfEnvironmentVariable` 让 4 个集成测试自动跳过——
这样 CI 上没有 mongod 也不会红。

## 这个模块以前的问题

原来的 `MongoUtils.java` 硬编码了真实服务器的 IP 和账号密码：

```java
private final static String IP = "101.201.81.221";
String uri = "mongodb://admin:123456@" + IP + ":27017/?...";
```

两个后果：一是凭据进了 git 历史；二是**模块本身没法运行**——那个 IP 早就不通了，
`main()` 一跑就超时。已删除。

## 踩过的坑

**`Bson` 是标记接口，没有 `.and()`**。要组合条件必须用 `Filters.and(...)`：

```java
Filters.and(Filters.gte("priceCents", min), Filters.lte("priceCents", max));  // 对
// Filters.gte(...).and(...)                                                  // 编译不过
```

**`Filters.and` 生成的是 `{$and:[...]}`，不是单字段子文档**。想让两个范围条件落在同一个
字段上、走同一个索引，得手写：

```java
new Document("priceCents", new Document("$gte", min).append("$lte", max));
```

前者语义正确但索引利用差，后者才是常见写法。`MongoQuery.priceBetween` 用后者。

**`BsonDocument.equals` 对实现类和数值宽度敏感**。`BsonDocumentWrapper` 和 `BsonDocument`
即使 JSON 一样也不相等，`int32` 和 `int64` 也不相等。测试里统一比 `.toJson()` 字符串。

## 文件

| 文件 | 作用 |
|---|---|
| `MongoQuery` | 静态方法构造常用 `Bson`：等值、范围、`$set`、排序、投影、索引键 |
| `Book` | 普通 POJO，无注解，配合 `PojoCodecProvider.automatic(true)` |
| `MongoDemo` | 离线 demo：打印 BSON JSON + POJO 编解码往返 |
| `MongoQueryTest` | 6 个用例，断言构造出的 BSON |
| `PojoCodecTest` | 4 个用例，断言编解码往返 + demo 输出 |
| `MongoIntegrationTest` | 4 个用例，需 `MONGO_URI` |
