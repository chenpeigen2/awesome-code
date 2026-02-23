# Flutter Rust Demo 项目文档

基于 **Flutter + Rust Bridge** 的跨平台计算器应用示例项目，展示 Flutter 与 Rust 的高效集成。

---

## 目录

- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [核心文件详解](#核心文件详解)
- [Rust API 接口](#rust-api-接口)
- [快速开始](#快速开始)
- [构建指南](#构建指南)
- [开发指南](#开发指南)
- [架构设计](#架构设计)

---

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Flutter | 3.10+ | 跨平台 UI 框架 |
| Dart | 3.10+ | Flutter 开发语言 |
| Rust | stable | 高性能后端计算 |
| flutter_rust_bridge | 2.11.1 | Dart ↔ Rust FFI 桥接 |
| Tokio | 1.x | Rust 异步运行时 |

### 支持平台

| 平台 | 状态 |
|------|------|
| Android | ✅ 支持 |
| iOS | ✅ 支持 |

---

## 项目结构

```
flutter_rust_demo/
│
├── 📱 android/                     # Android 平台原生代码
│   ├── app/                        # Android 应用模块
│   │   ├── build.gradle.kts        # 应用级 Gradle 配置
│   │   └── src/main/               # Android 源码
│   ├── build.gradle.kts            # 项目级 Gradle 配置
│   ├── settings.gradle.kts         # Gradle 项目设置
│   └── gradle/wrapper/             # Gradle Wrapper
│
├── 🍎 ios/                         # iOS 平台原生代码
│   ├── Runner/                     # iOS 应用主模块
│   │   ├── AppDelegate.swift       # iOS 应用代理
│   │   └── Info.plist              # iOS 配置文件
│   ├── Runner.xcodeproj/           # Xcode 项目文件
│   └── Runner.xcworkspace/         # Xcode 工作空间
│
├── 🎯 lib/                         # Flutter/Dart 源代码
│   ├── main.dart                   # 应用入口（UI + 业务逻辑）
│   └── src/rust/                   # 自动生成的 Rust 绑定代码
│       ├── api/                    # API 绑定
│       └── frb_generated.dart      # FFI 生成代码
│
├── 🦀 rust/                        # Rust 源代码
│   ├── src/
│   │   ├── lib.rs                  # Rust 库入口
│   │   ├── frb_generated.rs        # 自动生成的桥接代码
│   │   └── api/                    # API 接口定义
│   │       ├── mod.rs              # API 模块入口
│   │       └── simple.rs           # 计算器核心逻辑
│   ├── Cargo.toml                  # Rust 依赖配置
│   ├── Cargo.lock                  # Rust 依赖锁定
│   ├── .cargo/config.toml          # Cargo 配置
│   └── target/                     # Rust 编译输出
│
├── 🔧 rust_builder/                # Rust 构建工具包
│   ├── android/                    # Android Rust 构建配置
│   │   ├── build.gradle            # Android 构建脚本
│   │   └── src/main/               # JNI 桥接代码
│   ├── ios/                        # iOS Rust 构建配置
│   │   └── rust_lib_flutter_rust_demo.podspec
│   ├── cargokit/                   # Cargo 构建工具链
│   │   ├── build_tool/             # 构建工具
│   │   ├── gradle/                 # Gradle 插件
│   │   └── cmake/                  # CMake 配置
│   ├── pubspec.yaml                # 包配置
│   └── README.md                   # 构建说明
│
├── 🧪 test/                        # 单元测试目录
├── 🔄 integration_test/            # 集成测试目录
├── 📋 test_driver/                 # 测试驱动
│
├── 🐳 Docker 构建配置
│   ├── Dockerfile                  # Docker 镜像定义
│   ├── docker_build.sh             # Docker 内部构建脚本
│   └── build_apk_docker.sh         # Docker APK 构建入口
│
├── ⚙️ 配置文件
│   ├── pubspec.yaml                # Flutter 项目配置
│   ├── flutter_rust_bridge.yaml    # Rust Bridge 代码生成配置
│   ├── analysis_options.yaml       # Dart 静态分析配置
│   └── .gitignore                  # Git 忽略规则
│
└── 📄 PROJECT_STRUCTURE.md         # 本文档
```

---

## 核心文件详解

### 1. pubspec.yaml - Flutter 项目配置

```yaml
name: flutter_rust_demo
version: 1.0.0+1

environment:
  sdk: ^3.10.8

dependencies:
  flutter:
    sdk: flutter
  cupertino_icons: ^1.0.8
  rust_lib_flutter_rust_demo:    # Rust 构建包
    path: rust_builder
  flutter_rust_bridge: 2.11.1    # FFI 桥接库
  freezed_annotation: ^3.1.0     # 代码生成注解

dev_dependencies:
  flutter_test:
    sdk: flutter
  flutter_lints: ^6.0.0
  integration_test:
    sdk: flutter
  freezed: ^3.2.5
  build_runner: ^2.11.1
```

### 2. flutter_rust_bridge.yaml - 代码生成配置

```yaml
rust_input: crate::api      # Rust API 入口模块
rust_root: rust/            # Rust 项目根目录
dart_output: lib/src/rust   # 生成的 Dart 代码输出目录
```

### 3. rust/Cargo.toml - Rust 依赖配置

```toml
[package]
name = "rust_lib_flutter_rust_demo"
version = "0.1.0"
edition = "2021"

[lib]
crate-type = ["cdylib", "staticlib"]  # 动态库 + 静态库

[dependencies]
flutter_rust_bridge = "=2.11.1"
tokio = { version = "1", features = ["rt", "time"] }
```

---

## Rust API 接口

### 数据类型

#### Operation - 运算类型枚举

```rust
pub enum Operation {
    Add,       // 加法
    Subtract,  // 减法
    Multiply,  // 乘法
    Divide,    // 除法
}
```

#### CalcResult - 计算结果

```rust
pub enum CalcResult {
    Success { value: f64 },      // 成功，返回结果值
    Error { message: String },   // 失败，返回错误信息
}
```

#### CalculationRecord - 计算记录

```rust
pub struct CalculationRecord {
    pub a: f64,                  // 操作数 A
    pub b: f64,                  // 操作数 B
    pub operation: Operation,    // 运算类型
    pub result: CalcResult,      // 计算结果
    pub timestamp: i64,          // 时间戳
}
```

#### CalculatorStats - 统计信息

```rust
pub struct CalculatorStats {
    pub total_calculations: i32,  // 总计算次数
    pub success_count: i32,       // 成功次数
    pub error_count: i32,         // 错误次数
    pub average_value: f64,       // 平均值
}
```

### 核心函数

| 函数 | 说明 | 异步 |
|------|------|------|
| `calculate_async(a, b, operation)` | 异步计算 | ✅ |
| `calculate_sync(a, b, operation)` | 同步计算 | ❌ |
| `create_record(a, b, operation)` | 创建计算记录 | ✅ |
| `compute_stats(records)` | 计算统计数据 | ❌ |
| `heavy_computation(iterations)` | 模拟耗时计算 | ✅ |
| `batch_calculate(requests)` | 批量计算 | ✅ |
| `format_result(...)` | 格式化结果字符串 | ❌ |
| `get_supported_operations()` | 获取支持的运算列表 | ❌ |

---

## 快速开始

### 环境要求

- **Flutter SDK**: 3.10 或更高版本
- **Rust**: stable 工具链
- **Android SDK**: API 34+（Android 构建）
- **Xcode**: 15+（iOS 构建，仅 macOS）

### 安装依赖

```bash
# 安装 Flutter 依赖
flutter pub get

# 安装 Rust 目标平台
rustup target add aarch64-linux-android
rustup target add armv7-linux-androideabi
rustup target add x86_64-linux-android
rustup target add i686-linux-android
```

### 运行应用

```bash
# 查看已连接设备
flutter devices

# 运行到指定设备
flutter run -d <device_id>

# 运行到 Android
flutter run -d android

# 运行到 iOS（仅 macOS）
flutter run -d ios
```

---

## 构建指南

### 使用 Docker 构建（推荐）

Docker 构建方式封装了所有构建环境，无需本地配置。

```bash
# 构建 Debug APK
./build_apk_docker.sh debug

# 构建 Release APK
./build_apk_docker.sh release
```

**输出位置：**
- Debug: `build/app/outputs/flutter-apk/app-debug.apk`
- Release: `build/app/outputs/flutter-apk/app-release.apk`

### 本地构建

```bash
# 生成 Rust 绑定代码
flutter_rust_bridge_codegen generate

# 构建 Android APK
flutter build apk --debug
flutter build apk --release

# 构建 iOS（仅 macOS）
flutter build ios --debug
flutter build ios --release
```

### 清理构建

```bash
# 清理 Flutter 构建
flutter clean

# 清理 Rust 构建
cd rust && cargo clean
```

---

## 开发指南

### 修改 Rust 代码后

```bash
# 1. 修改 rust/src/api/simple.rs

# 2. 重新生成绑定代码
flutter_rust_bridge_codegen generate

# 3. 重新构建
flutter run
```

### 修改 Dart/UI 代码后

```bash
# 热重载即可生效（在 flutter run 状态下按 r）
r

# 热重启（按 R）
R
```

### 添加新的 Rust API

1. 在 `rust/src/api/simple.rs` 中添加新函数：

```rust
pub fn my_new_function(input: String) -> String {
    format!("Hello, {}!", input)
}
```

2. 重新生成绑定代码：

```bash
flutter_rust_bridge_codegen generate
```

3. 在 Dart 中调用：

```dart
import 'package:flutter_rust_demo/src/rust/api/simple.dart';

final result = await myNewFunction(input: "World");
```

---

## 架构设计

### 整体架构

```
┌─────────────────────────────────────────────────────┐
│                  Flutter UI Layer                    │
│  ┌─────────────┬─────────────┬─────────────┐        │
│  │  Calculator │   History   │    Stats    │        │
│  │    Page     │    Page     │    Page     │        │
│  └──────┬──────┴──────┬──────┴──────┬──────┘        │
│         │             │             │               │
│         └─────────────┼─────────────┘               │
│                       ▼                             │
│  ┌─────────────────────────────────────────────┐    │
│  │         Dart Business Logic                 │    │
│  │  - State Management (setState)              │    │
│  │  - UI Event Handling                        │    │
│  │  - Data Transformation                      │    │
│  └────────────────────┬────────────────────────┘    │
└───────────────────────┼─────────────────────────────┘
                        │
                        ▼ FFI Bridge
┌─────────────────────────────────────────────────────┐
│           flutter_rust_bridge Layer                 │
│  ┌─────────────────────────────────────────────┐    │
│  │  - Type Conversion (Dart ↔ Rust)            │    │
│  │  - Async/Await Mapping                      │    │
│  │  - Memory Management                        │    │
│  └─────────────────────────────────────────────┘    │
└───────────────────────┼─────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────┐
│                  Rust Backend Layer                 │
│  ┌─────────────┬─────────────┬─────────────┐        │
│  │  calculate  │   compute   │    heavy    │        │
│  │   _async    │   _stats    │ computation │        │
│  └─────────────┴─────────────┴─────────────┘        │
│  ┌─────────────────────────────────────────────┐    │
│  │  - Core Algorithm                           │    │
│  │  - Async Processing (Tokio)                 │    │
│  │  - High Performance Computing               │    │
│  └─────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────┘
```

### 数据流

```
用户输入
    │
    ▼
┌─────────┐     ┌──────────────┐     ┌─────────────┐
│ Flutter │ ──▶ │ Dart API     │ ──▶ │ Rust FFI    │
│   UI    │     │ (generated)  │     │ (generated) │
└─────────┘     └──────────────┘     └─────────────┘
                                           │
                                           ▼
                                    ┌─────────────┐
                                    │ Rust Logic  │
                                    │ (api/simple)│
                                    └─────────────┘
                                           │
                                           ▼
┌─────────┐     ┌──────────────┐     ┌─────────────┐
│ Flutter │ ◀── │ Dart API     │ ◀── │ Rust FFI    │
│   UI    │     │ (generated)  │     │ (generated) │
└─────────┘     └──────────────┘     └─────────────┘
    │
    ▼
显示结果
```

### 为什么选择 Rust？

| 特性 | Dart | Rust |
|------|------|------|
| 性能 | 一般 | ⚡ 极高 |
| 内存安全 | GC 管理 | 编译时保证 |
| 并发 | Isolate | 原生异步 |
| 计算密集型 | 不适合 | ✅ 非常适合 |

---

## 常见问题

### Q: Rust 代码修改后没有生效？

A: 需要重新生成绑定代码：
```bash
flutter_rust_bridge_codegen generate
```

### Q: 构建时报链接错误？

A: 确保安装了所有 Android 目标：
```bash
rustup target add aarch64-linux-android armv7-linux-androideabi x86_64-linux-android i686-linux-android
```

### Q: Docker 构建网络超时？

A: Dockerfile 已配置国内镜像源，如果仍然超时，检查网络连接或使用 VPN。

---

## 许可证

MIT License