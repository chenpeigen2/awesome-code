# ASM 字节码操作

四个模块，按「从字节码到源码」的顺序排列：

| 模块 | Gradle 路径 | 内容 |
|------|-------------|------|
| [`basics`](basics) | `:asm:basics` | Core API：生成类、读类结构、反汇编 |
| [`tree`](tree) | `:asm:tree` | Tree API：把类读成对象树，改字段/方法后再写回 |
| [`transform`](transform) | `:asm:transform` | asm-commons：方法计时、删方法、类改名 |
| [`poet`](poet) | `:asm:poet` | JavaPoet：生成 Java 源码（对照 ASM 的字节码生成） |

另外 [`../compile/inmemory`](../compile/inmemory) 是相邻技术：让 javac 在内存里编译，
同样产出一个 `byte[]`，可以对照理解「生成字节码」和「生成源码再编译」两条路线。

## 运行

```bash
./gradlew :asm:basics:test      # 全部模块的测试都是这样跑
./gradlew :asm:tree:test
./gradlew :asm:transform:test
./gradlew :asm:poet:test
./gradlew :compile:inmemory:test
```

每个模块都有一个 `demo` 任务，跑通「生成/增强字节码 → 加载 → 真实调用」：

```bash
./gradlew :asm:basics:demo      # 从零生成一个类，加载并调用
./gradlew :asm:tree:demo        # 给 Bean 补 getter/setter，再调用它们
./gradlew :asm:transform:demo   # 计时 + 删方法 + 类改名
./gradlew :asm:poet:demo        # 生成源码 → javac 编译 → 加载 → 调用
```

演示程序本身也被测试覆盖（`TreeDemoTest` / `TransformDemoTest` / `PoetDemoTest`），
所以 `demo` 不会在无人察觉的情况下失效。

## 核心概念

**类文件是一串指令，不是一个对象。** ASM 的 Core API 用「访问者模式」把它流式读出来：
`ClassReader` 逐条推送事件给 `ClassVisitor`，`ClassVisitor` 再推给 `MethodVisitor`。
不修改就只需要一个 `ClassWriter` 直通；要修改就在中间插一层。

**为什么有 Tree API。** 流式 API 只能「顺着读一遍」，改不了「先看后决定」的逻辑
（比如「只有方法数超过 10 才加字段」）。Tree API 把整个类读成 `ClassNode`，
可以任意查看、增删，最后再写回。

**帧（stack map frame）是最容易踩的坑。** 插入跳转后，原来的栈映射帧就失效了。
`ClassWriter.COMPUTE_FRAMES` 让 ASM 重算，代价是它需要能加载相关的类
（所以 `ClassWriter(reader, ...)` 传原始 reader 能省掉一部分解析）。

**构造器要小心。** `AdviceAdapter` 知道 `super()` 调用在哪，能避免把代码插到
`super()` 之前（那是非法的）。计时这类逻辑还要跳过 `<init>` / `<clinit>`。

## 各模块要点

### basics

- `ClassFile`：只读模型，`describe()` 输出 javap 风格的类结构
- `Disassembler`：`TraceClassVisitor` + `Textifier`，输出可读指令
- `GeneratedHelloWorld`：从零生成一个类，再用自定义 `ClassLoader` 加载并调用

关键点：生成的类**必须能被真实的 JVM 加载并执行**。测试不是比对字节数组，
而是 `defineClass` 之后反射调用、断言真实返回值，并跑一遍
`CheckClassAdapter.verify`。

### tree

- `ClassTree`：`read` / `write` / `writeComputingFrames`
- `GetterSetterGenerator`：给所有非静态字段补 getter/setter，幂等（重复调用不重复加）

关键点：`Type.getOpcode(ILOAD)` 会自动选对 `ILOAD`/`LLOAD`/`FLOAD`/`DLOAD`/`ALOAD`，
不用手写 switch。

### transform

- `TimingTransformer`：用 `AdviceAdapter` 在每个方法前后插入计时
- `MethodRemover`：按名字或「名字 + 描述符」删方法（后者才能保住重载）
- `ClassRenamer`：`ClassRemapper` 把类内所有自引用一起改名

关键点：`onMethodExit` 里栈顶是返回值，插入计时代码前必须先把它存进临时局部变量；
`ATHROW` 时栈上是 `Throwable` 而不是方法声明的返回类型，要分开处理。
计时结果通过 `TimingRuntime.record(String, long)` 这个静态入口送出——
被增强的类因此不需要知道任何回调对象。

### poet

- `BeanGenerator`：用 JavaPoet 生成不可变 POJO 的完整源码

关键点：JavaPoet 的验收标准不是「字符串好看」，而是**生成的源码能被 javac 编译、
编译出的类行为正确**。测试真的调用 `ToolProvider.getSystemJavaCompiler()` 编译并反射验证
`equals`/`hashCode`/`toString`。

## 参考

- [ASM 官方文档](https://asm.ow2.io/asm4-guide.pdf)
- [JavaPoet](https://github.com/square/javapoet)
- JVMS §4.7.4：StackMapTable 属性

## 一个必踩的坑：类加载器是「父优先」的

所有演示都用自定义 `ClassLoader` 加载改过的字节码。默认的
`ClassLoader.loadClass()` 是**父优先**：它先在父加载器里找同名类，找到就直接返回。
而父加载器里正好有原始版本（因为我们就是从那里读的字节码），
于是**改过的字节码永远不会被定义**，反射调用新方法时抛 `NoSuchMethodException`。

正确做法是让目标类「子优先」加载：

```java
@Override
protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    if (name.equals(TARGET)) {
        Class<?> loaded = findLoadedClass(name);
        return loaded != null ? loaded : findClass(name);
    }
    return super.loadClass(name, resolve);
}
```

这个问题在 `TreeDemo` 里真实发生过（测试直接挂了），修好后才绿。
任何「运行期替换类」的 agent 都会遇到同一个坑。
