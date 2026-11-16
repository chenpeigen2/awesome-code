# compile — 编译期技术

| 模块 | Gradle 路径 | 内容 |
|------|-------------|------|
| [`apt`](apt) | `:compile:apt` | 注解处理器的**使用方**：演示如何用生成出来的 Builder |
| [`apt-processor`](apt-processor) | `:compile:apt-processor` | 注解 `@Builder` + 处理器 `BuilderProcessor` 本体 |
| [`inmemory`](inmemory) | `:compile:inmemory` | 内存编译：用 `javax.tools.JavaCompiler` 把源码编译成 `byte[]` |

## apt / apt-processor

### 为什么拆成两个模块

注解处理器**不能自己处理自己**。如果 `@Builder`、`BuilderProcessor` 和用它生成 Builder
的 `Person` 在同一个模块里，`PersonBuilder` 在编译 `Person` 时还不存在，
于是 `AptDemo` 里那行 `new PersonBuilder()` 一定编译失败——处理器生成的代码
和引用它的代码不能在同一次 javac 调用里互相依赖（除非靠多轮编译，很脆）。

拆开之后就干净了：

```
:compile:apt-processor   ← @Builder + BuilderProcessor（纯 JDK，无第三方依赖）
        ↑ annotationProcessor
:compile:apt             ← Person(@Builder) + AptDemo(new PersonBuilder())
```

`:compile:apt` 里这样接：

```kotlin
dependencies {
    implementation(project(":compile:apt-processor"))
    annotationProcessor(project(":compile:apt-processor"))
}
```

### 处理器如何被 javac 发现

靠 SPI 文件，不是靠注解：

```
compile/apt-processor/src/main/resources/META-INF/services/javax.annotation.processing.Processor
```

内容一行：`org.peter.apt.BuilderProcessor`

（生产项目通常用 `com.google.auto.service:auto-service` 自动生成这个文件，
这里手写是为了让机制显式可见。）

### 生成了什么

`Person` 上有三个 `@Builder` setter，编译后多出一个 `PersonBuilder`：

```java
public class PersonBuilder {
    private final Person object = new Person();

    public Person build() { return object; }

    public PersonBuilder setAge(int value) { object.setAge(value); return this; }
    public PersonBuilder setEmail(java.lang.String value) { ... }
    public PersonBuilder setName(java.lang.String value) { ... }
}
```

注意参数类型是 `java.lang.String` 而不是 `String`：这是 `TypeMirror.toString()`
的规范行为（它给全限定名）。要拿简单名得走 `Elements.getTypeElement(...).getSimpleName()`，
但那样会丢掉泛型和数组信息，所以生成器通常保留全限定名。

生成的文件在 `build/generated/sources/annotationProcessor/java/main/`。

### 处理器的几条规矩

- **用 `Messager` 报错，不要抛异常**。处理器里抛异常会让 javac 打出一堆无意义堆栈。
- **校验元素类型**。`@Target(METHOD)` 只挡住「写在字段上」；static setter、
  多参数方法都得处理器自己判。
- **输出顺序要稳定**。用 `TreeMap` 排序，否则每次编译生成的文件顺序都变，
  diff 噪音大，测试也没法稳定断言。
- **`getSupportedSourceVersion()` 返回 `latestSupported()`**，避免在 JDK 25 上
  刷 "source version 8 is obsolete" 警告。

### 运行

```bash
./gradlew :compile:apt:test              # 验证 Gradle 配置接对了
./gradlew :compile:apt-processor:test    # 验证处理器逻辑（用 javax.tools 手动跑）
./gradlew :compile:apt:demo              # 跑一遍生成的 Builder
```

两个测试的分工：

| 测试 | 验证什么 |
|------|----------|
| `BuilderProcessorTest` | 处理器逻辑本身。用 `javax.tools` 手动喂源码、收集生成的源码、断言内容与报错 |
| `AptDemoTest` | **Gradle 配置**接对了。`PersonBuilder` 在源码里不存在却能编译通过，就证明 `annotationProcessor` 真的生效 |

## inmemory

`InMemoryCompiler.compile(binaryName, source)` 返回 `类名 → 字节码` 的映射，
内部类也会一起返回（`demo.Outer$Inner`）。

```java
Map<String, byte[]> compiled = InMemoryCompiler.compile("demo.Greeting", """
        package demo;
        public class Greeting {
            public String hello() { return "hi"; }
        }
        """);

Object instance = InMemoryCompiler.newInstance(compiled, "demo.Greeting");
```

三个协作角色：

- `StringSource` —— 把源码字符串伪装成一个 `.java` 文件（`SimpleJavaFileObject`）
- `ByteArrayOutput` —— 把 javac 写出的 `.class` 收进内存
- `InMemoryFileManager` —— 继承 `ForwardingJavaFileManager`，把上面两者接进 javac

### 与 ASM 的关系

两条路线都能得到一个可加载的 `byte[]`：

| | ASM | 内存编译 |
|---|---|---|
| 输入 | 直接写指令 | 写 Java 源码 |
| 谁做类型检查 | 没有，ASM 不管 | javac 全做 |
| 生成的东西可读吗 | 不可读 | 可读、可 diff |
| 能改已有类吗 | 能 | 不能（只能新建） |
| 典型场景 | 运行期增强、AOP、Mock | 注解处理器、动态脚本 |

所以 `asm/poet`（生成源码）和这里是同一条路线的两个端点，
而 `asm/basics`（直接写字节码）是另一条。

### 运行

```bash
./gradlew :compile:inmemory:test
./gradlew :compile:inmemory:demo
```
