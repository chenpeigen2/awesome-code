# Kotlin 协程学习 Demo

本项目是一个完整的 Kotlin 协程学习示例，涵盖了协程的基础概念、Flow、Channel、异常处理、Android 集成以及进阶原理等内容。

## 项目简介

本 Demo 通过可运行的代码示例，帮助开发者深入理解 Kotlin 协程的核心概念和实际应用。每个模块都包含详细的注释和日志输出，便于学习调试。

## 模块内容概览

### 一、协程基础 (01-Basics)

| 示例 | 说明 | 对应 Activity |
|------|------|---------------|
| suspend 函数 | 挂起函数原理与使用 | `SuspendFunctionActivity` |
| launch vs async | 协程启动方式对比 | `LaunchAsyncActivity` |
| Dispatchers 调度器 | 四种调度器的使用场景 | `DispatchersActivity` |
| Job 与协程控制 | Job 状态、取消与等待 | `JobActivity` |
| CoroutineScope 作用域 | 结构化并发与作用域管理 | `CoroutineScopeActivity` |

### 二、Kotlin Flow (02-Flow)

| 示例 | 说明 | 对应 Activity |
|------|------|---------------|
| Flow 基础 | Flow 构建器与 collect | `FlowBasicsActivity` |
| Flow 操作符 | map/filter/transform/combine | `FlowOperatorsActivity` |
| StateFlow | 状态流与 UI 状态管理 | `StateFlowActivity` |
| 冷流与热流 | Flow、SharedFlow、StateFlow 对比 | `ColdHotFlowActivity` |

### 三、Channel 通道 (03-Channel)

| 示例 | 说明 | 对应 Activity |
|------|------|---------------|
| Channel 基础 | Channel 的创建与使用 | `ChannelBasicsActivity` |
| 生产者消费者模式 | produce 与 consume | `ProduceConsumeActivity` |
| select 表达式 | 多路复用与选择 | `SelectExpressionActivity` |

### 四、异常处理 (04-ErrorHandling)

| 示例 | 说明 | 对应 Activity |
|------|------|---------------|
| try/catch | 协程中的异常捕获 | `TryCatchActivity` |
| CoroutineExceptionHandler | 全局异常处理器 | `ExceptionHandlerActivity` |
| SupervisorJob | 子协程独立失败 | `SupervisorJobActivity` |

### 五、Android 集成 (05-Android)

| 示例 | 说明 | 对应 Activity |
|------|------|---------------|
| lifecycleScope | 生命周期感知的协程 | `LifecycleScopeActivity` |
| viewModelScope | ViewModel 协程作用域 | `ViewModelScopeActivity` |
| Flow 收集最佳实践 | repeatOnLifecycle 与 flowWithLifecycle | `CollectFlowActivity` |

### 六、进阶原理 (06-Advanced)

| 示例 | 说明 | 对应 Activity |
|------|------|---------------|
| Continuation 原理 | 挂起与恢复机制 | `ContinuationActivity` |
| 状态机原理 | 协程的编译器转换 | `StateMachineActivity` |
| 自定义 CoroutineScope | 创建自定义作用域 | `CustomScopeActivity` |
| Room + 协程 | 数据库协程操作 | `RoomExampleActivity` |

### 七、协程测试 (07-Testing)

> 注：测试模块代码示例已在文档中提供，暂无对应 Activity。

| 示例 | 说明 |
|------|------|
| TestDispatcher | 测试调度器 |
| 时间控制 | 虚拟时间与 delay 控制 |
| Flow 测试 | 测试 Flow 发射与收集 |

## 如何运行

### 环境要求

- Android Studio Arctic Fox 或更高版本
- JDK 11+
- Android SDK 24+

### 运行步骤

1. 克隆项目并打开 Android Studio
2. 等待 Gradle 同步完成
3. 连接 Android 设备或启动模拟器
4. 运行 `coroutine-demo` 模块

```bash
# 命令行运行
./gradlew :coroutine-demo:installDebug
```

### 项目结构

```
coroutine-demo/
├── src/main/java/com/peter/coroutine/demo/
│   ├── MainActivity.kt           # 主入口
│   ├── basics/                   # 协程基础
│   ├── flow/                     # Flow 相关
│   ├── channel/                  # Channel 相关
│   ├── errorhandling/            # 异常处理
│   ├── android/                  # Android 集成
│   ├── advanced/                 # 进阶原理
│   └── data/                     # 数据层 (Room)
└── docs/                         # 教程文档
    ├── 01-basics.md
    ├── 02-flow.md
    ├── 03-channel.md
    ├── 04-error-handling.md
    ├── 05-android-integration.md
    ├── 06-advanced.md
    └── 07-testing.md
```

## 学习路径建议

### 初学者路径

1. **协程基础** - 从 `SuspendFunctionActivity` 开始，理解挂起函数的概念
2. **launch vs async** - 学习两种协程启动方式的区别
3. **Dispatchers** - 了解调度器和线程切换
4. **Job** - 掌握协程的控制和取消

### 进阶路径

1. **Flow 基础** - 学习 Flow 的基本用法
2. **Flow 操作符** - 掌握常用的 Flow 操作符
3. **StateFlow/SharedFlow** - 理解热流的使用场景
4. **Channel** - 学习协程间的通信

### Android 开发者路径

1. **lifecycleScope** - 理解生命周期感知
2. **viewModelScope** - ViewModel 中的协程使用
3. **Flow 收集最佳实践** - 学习 repeatOnLifecycle
4. **Room + 协程** - 数据库协程操作

### 原理深入路径

1. **Continuation 原理** - 理解挂起和恢复
2. **状态机原理** - 了解编译器如何转换协程
3. **自定义 Scope** - 创建和管理自定义作用域

## 相关文档

- [协程基础教程](docs/01-basics.md)
- [Flow 教程](docs/02-flow.md)
- [Channel 教程](docs/03-channel.md)
- [异常处理教程](docs/04-error-handling.md)
- [Android 集成教程](docs/05-android-integration.md)
- [进阶原理教程](docs/06-advanced.md)
- [测试教程](docs/07-testing.md)

## 依赖版本

- Kotlin: 1.9.x
- Coroutines: 1.7.x
- Lifecycle: 2.7.x
- Room: 2.6.x

## 参考资料

- [Kotlin 官方协程文档](https://kotlinlang.org/docs/coroutines-guide.html)
- [Android 协程最佳实践](https://developer.android.com/kotlin/coroutines)
- [Kotlin Flow 官方文档](https://kotlinlang.org/docs/flow.html)
