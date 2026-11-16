# explore/spoon

[Spoon](https://spoon.gforge.inria.fr/) 是 Java 源码的**结构化解析器**：它把 `.java` 文件读成一个
可查询、可修改的对象模型（`CtClass` / `CtMethod` / `CtField` …），然后还能把模型重新生成回源码。

## 和反射、ASM 的区别

| 手段 | 输入 | 能看到什么 | 能改什么 |
|---|---|---|---|
| 反射 | 已加载的 `Class` | 运行期结构 | 改不了 |
| ASM | `.class` 字节码 | 指令级细节，看不到源码结构 | 能改字节码，但极难写 |
| **Spoon** | **`.java` 源码** | **完整 AST + 类型 + 注解** | **改模型再生成源码** |

Spoon 的典型用途：自定义代码规范检查、批量源码迁移（比如 JDK 升级时改 API 调用）、
从源码里抽接口文档。

## 跑起来

```bash
./gradlew :explore:spoon:demo    # 解析内置源码，打印类/字段/方法
./gradlew :explore:spoon:test    # 7 个测试
```

## 关键点

**`setNoClasspath(true)`** —— 解析时不给完整 classpath，Spoon 就不会因为「找不到 `List` 的实现」
而报错，而是把无法解析的类型留成 `CtTypeReference` 的模糊形态。做源码分析工具时这个开关几乎是必须的，
否则被分析项目的依赖必须全都凑齐才能开始。

**`VirtualFile`** —— 可以直接解析内存里的字符串，不必先写磁盘。demo 因此自包含，
不依赖仓库里的任何路径（这正是本仓库以前 `explore/javaParser` 犯的错：硬编码
`/home/chenpeigen/demo_java/...`，换台机器就跑不了）。

**查询靠 `TypeFilter`** —— `model.getElements(new TypeFilter<>(CtMethod.class))` 拿到所有方法，
再自己过滤。比反射的「只能从某个类往下找」灵活得多：这里是全模型范围。

## 测试

`SpoonModelTest` 解析 `src/test/resources/sample/` 下的两个类，断言：

- 类名集合是 `[Order, OrderService]`
- `Order` 的字段类型是 `{String, List, long}`
- `total()` 返回 `long`
- `legacyTotal` 带 `@Deprecated`
- `SpoonDemo().run()` 输出里含 `classes: [Order, OrderService]`

都是**观测行为**的断言（模型里到底读到了什么），不是「Spoon 内部怎么实现」。
