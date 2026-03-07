# Kotlin 协程 Android Demo 设计文档

## 概述

创建一个从基础到高级的 Kotlin 协程 Android demo，采用代码实验室形式，每个示例都有详细注释和对应的 README 文档。

## 目标

- 覆盖协程从入门到原理的完整知识体系
- 提供可运行的代码示例和详细注释
- 适合阅读学习的教程格式

## 模块结构

**模块名**: `coroutine-demo`

### 包结构

```
com.peter.coroutine.demo/
├── MainActivity.kt              # 主入口，列出所有示例
├── basics/                      # 01-基础
│   ├── SuspendFunctionActivity.kt
│   ├── LaunchAsyncActivity.kt
│   ├── DispatchersActivity.kt
│   ├── JobActivity.kt
│   └── CoroutineScopeActivity.kt
├── flow/                        # 02-Flow
│   ├── FlowBasicsActivity.kt
│   ├── FlowOperatorsActivity.kt
│   ├── StateFlowActivity.kt
│   └── ColdHotFlowActivity.kt
├── channel/                     # 03-Channel
│   ├── ChannelBasicsActivity.kt
│   ├── ProduceConsumeActivity.kt
│   └── SelectExpressionActivity.kt
├── errorhandling/               # 04-异常处理
│   ├── TryCatchActivity.kt
│   ├── ExceptionHandlerActivity.kt
│   └── SupervisorJobActivity.kt
├── android/                     # 05-Android 集成
│   ├── LifecycleScopeActivity.kt
│   ├── ViewModelScopeActivity.kt
│   └── CollectFlowActivity.kt
├── advanced/                    # 06-进阶原理
│   ├── ContinuationActivity.kt
│   ├── StateMachineActivity.kt
│   ├── CustomScopeActivity.kt
│   └── RoomExampleActivity.kt   # Room 示例
├── testing/                     # 07-测试
│   └── (测试代码在 test 目录)
└── data/                        # 共用数据
    ├── local/
    │   └── UserDatabase.kt      # Room 数据库
    └── model/
        └── User.kt
```

### 文档结构

```
coroutine-demo/
├── README.md                    # 总览
└── docs/
    ├── 01-basics.md
    ├── 02-flow.md
    ├── 03-channel.md
    ├── 04-error-handling.md
    ├── 05-android-integration.md
    ├── 06-advanced.md
    └── 07-testing.md
```

## 模块内容详解

### 01-Basics - 协程基础

| Activity | 内容 | UI 形式 |
|----------|------|---------|
| SuspendFunctionActivity | suspend 函数、调用原理 | 日志输出 |
| LaunchAsyncActivity | launch vs async、返回值 | 日志输出 |
| DispatchersActivity | Default/IO/Main/Unconfined | 交互按钮 |
| JobActivity | Job 状态、取消、join | 交互按钮 |
| CoroutineScopeActivity | 作用域、结构化并发 | 日志输出 |

### 02-Flow - Flow 流

| Activity | 内容 | UI 形式 |
|----------|------|---------|
| FlowBasicsActivity | flow builder、collect、冷流 | 日志输出 |
| FlowOperatorsActivity | map/filter/transform/combine/zip | 日志输出 |
| StateFlowActivity | StateFlow vs LiveData | 交互 UI |
| ColdHotFlowActivity | 冷流热流区别、SharedFlow | 日志输出 |

### 03-Channel - 通道

| Activity | 内容 | UI 形式 |
|----------|------|---------|
| ChannelBasicsActivity | Channel 基础、send/receive | 日志输出 |
| ProduceConsumeActivity | produce、consumeEach | 日志输出 |
| SelectExpressionActivity | select 表达式、多路复用 | 日志输出 |

### 04-Error Handling - 异常处理

| Activity | 内容 | UI 形式 |
|----------|------|---------|
| TryCatchActivity | 协程中的 try/catch | 交互按钮 |
| ExceptionHandlerActivity | CoroutineExceptionHandler | 交互按钮 |
| SupervisorJobActivity | SupervisorJob vs Job | 日志输出 |

### 05-Android Integration - Android 集成

| Activity | 内容 | UI 形式 |
|----------|------|---------|
| LifecycleScopeActivity | lifecycleScope、生命周期感知 | 交互 UI |
| ViewModelScopeActivity | viewModelScope 使用 | 交互 UI |
| CollectFlowActivity | repeatOnLifecycle、flowWithLifecycle | 交互 UI |

### 06-Advanced - 进阶原理

| Activity | 内容 | UI 形式 |
|----------|------|---------|
| ContinuationActivity | Continuation 接口、挂起点 | 代码 + 日志 |
| StateMachineActivity | 状态机原理、标签 | 代码 + 日志 |
| CustomScopeActivity | 自定义 CoroutineScope | 日志输出 |
| RoomExampleActivity | Room + 协程使用 | 交互 UI |

### 07-Testing - 测试

| 测试类 | 内容 |
|--------|------|
| BasicsTest | 协程基础测试 |
| FlowTest | Flow 测试 |
| TestDispatcherTest | TestDispatcher 使用 |
| TimeControlTest | 虚拟时间控制 |

## 技术选型

- **UI**: View + ViewBinding（与现有 demo 一致）
- **架构**: 简单 MVVM（Android 集成部分）
- **数据库**: Room（进阶部分示例）
- **依赖注入**: 无（保持简单）
- **最低 SDK**: 24

## 依赖

```kotlin
// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// Lifecycle
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

// Room
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// Testing
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
testImplementation("androidx.arch.core:core-testing:2.2.0")
```

## 代码风格

每个 Activity 遵循以下格式：

```kotlin
/**
 * ## 标题
 *
 * ### 概念说明
 * [详细解释这个概念...]
 *
 * ### 关键点
 * - 要点 1
 * - 要点 2
 *
 * ### 运行结果
 * [预期输出说明]
 */
class XxxActivity : AppCompatActivity() {
    // 详细注释的代码...
}
```

## 文件清单

### Kotlin 文件 (28 个)
- MainActivity.kt
- basics/ (5 个)
- flow/ (4 个)
- channel/ (3 个)
- errorhandling/ (3 个)
- android/ (3 个)
- advanced/ (4 个)
- data/ (2 个)
- testing/ (3 个在 test 目录)

### 布局文件 (23 个)
- activity_main.xml
- 各 Activity 对应布局

### 文档文件 (8 个)
- README.md
- docs/ (7 个 markdown)

### 配置文件 (2 个)
- build.gradle.kts
- AndroidManifest.xml

## 验收标准

1. 所有示例可编译运行
2. 每个示例有详细的代码注释
3. 每个模块有对应的 README 文档
4. Room 示例功能正常
5. 测试代码可运行通过
