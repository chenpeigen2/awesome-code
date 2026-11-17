# explore/javaParser

[JavaParser](https://javaparser.org/) 把 Java 源码解析成带类型信息的 AST，
可以查询、修改、再生成源码。和 Spoon 定位相似，但更轻量（无 classpath 要求）。

## 跑起来

```bash
./gradlew :explore:javaParser:demo    # 解析内置源码，打印类/方法/字段结构
./gradlew :explore:javaParser:test    # 8 个测试
```

## 这个模块以前的问题

`HelloWorld.java` 里硬编码了一个绝对路径：

```java
new JavaParser().parse(Paths.get(
        "/home/chenpeigen/demo_java/src/main/java/lee/pkg20220606/MyCalendarThree.java"));
```

换台机器（比如这台 Mac）直接 `NoSuchFileException`，demo 从未跑通过。
更隐蔽的是它用 `result.getResult().ifPresent(YamlPrinter::print)`：
**解析失败时什么都不发生**，看起来像「正常运行但没输出」。

现在 demo 用内联字符串，测试用 classpath 资源，都不依赖磁盘布局。

## 两个关键设计

**失败必须抛异常。** `ParseResult` 是「结果 + 问题列表」的复合体，
`ifPresent(...)` 会把解析失败变成静默无操作。`SourceAnalyzer.unwrap` 统一转成
`IllegalArgumentException`，消息里带全部语法问题**和源码片段**：

```
failed to parse inline source: (line 1,col 29) Parse error. Found "}"...
--- source ---
public class Broken { void x( }
```

只给行列号时得回头翻原文件才知道在说什么，带上片段就能直接定位。

**从 classpath 读，不从文件路径读。** `parseResource("sample/Calculator.java")`
让测试资源跟着 class 文件走，模块搬到哪都能跑。

## 文件

| 文件 | 作用 |
|---|---|
| `SourceAnalyzer` | `parse` / `parseResource` / 方法签名 / 返回类型 / 字段类型 / 注解统计 |
| `ParserDemo` | 解析内联源码并打印结构 |
| `SourceAnalyzerTest` | 8 个测试 |
| `src/test/resources/sample/Calculator.java` | classpath 资源示例 |

## 测试

断言的都是「从源码里读出了什么」：类名、方法签名 `add(int, int)`、
返回类型、字段类型 `List<String>`、注解数量，以及**解析失败时抛异常**。
最后一条是回归测试，防止有人把 `unwrap` 改回 `ifPresent`。
