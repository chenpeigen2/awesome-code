# json/gson

Gson 用法示例：POJO 映射、`InstanceCreator`、严格模式。

## 跑起来

```bash
./gradlew :json:gson:test    # 9 个测试
```

## 这个模块以前的问题

**5 个类在默认包**（`GsonTest`、`Person`、`StrictTest`、`InstanceCreatorExample`、
`package-info`）。默认包类的后果：

- 其它包无法 `import`，只能靠反射或复制粘贴；
- JDK 9+ 模块系统**禁止**从具名模块引用默认包；
- IDE 的「移动类」重构会直接拒绝。

**`InstanceCreatorExample` 没演示 InstanceCreator。** 关键那行被注释掉了：

```java
Gson gson = new GsonBuilder()
//      .registerTypeAdapter(User.class, new UserInstanceCreator())   ← 注释掉
        .create();
```

对象照样能解析出来（Gson 默认走 Unsafe 分配，不用构造器），所以**光看输出根本发现不了**。
它还用了 `void main()`（非标准签名，只有 JEP 445 预览版才认）。

**`StrictTest` 是 catch 一切然后打印「可能成功也可能失败」**，测不出任何结论。

## 现在的结构

所有类迁到 `org.peter.gson`，`InstanceCreatorExample` 重写：

- `registerTypeAdapter` 真的注册了；
- `creatorInvocations()` 暴露调用次数，测试可以断言「确实走了 InstanceCreator」；
- 配一个对照测试 `withoutInstanceCreatorNoArgConstructorIsStillNotUsed` 证明
  「能解析出来」不足以证明注册生效 —— 必须数调用次数。

## 一个容易踩的坑：`Strictness.STRICT` 不管未知字段

我一开始写测试假设 `setStrictness(STRICT)` 会让「JSON 里多出目标类没有的字段」报错。
跑出来没抛异常 —— 查了 `Strictness` 枚举才确认：**它只管 JSON 语法严格性**
（比如未加引号的字符串 `{name:A}`），未知字段是 `Excluder` / `@Expose` 策略管的，
默认静默忽略。

这是**测试写错了，不是实现错了**，所以改测试：拆成两个
`unknownFieldsAreSilentlyIgnoredByDefault`（断言拼错字段名只会得到 `null`，
不会报错）和 `strictnessRejectsMalformedJsonSyntax`（断言语法错误确实抛
`JsonSyntaxException`）。

## 文件

| 文件 | 作用 |
|---|---|
| `org/peter/gson/Person.java` | 普通 POJO |
| `org/peter/gson/InstanceCreatorExample.java` | `InstanceCreator` 真正生效的示例 |
| `org/peter/gson/GsonTest.java` | 原示例（已迁包） |
| `org/peter/gson/StrictTest.java` | 原示例（已迁包） |
| `org/peter/gson/package-info.java` | 包说明 + 为什么不能用默认包 |
| `adapter/` `bp/` `entity/` `json/` `rawcollections/` `stream/` `stu/` | 原有子包示例 |
| `GsonBehaviorTest` | 9 个测试 |
