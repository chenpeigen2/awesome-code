# AGENTS.md

本文件为 AI 代理提供项目的上下文指南，帮助理解和操作此代码库。

## 项目概述

**flutter_rust_demo** 是一个基于 **Flutter + Rust Bridge** 的跨平台计算器应用示例项目，展示 Flutter 与 Rust 的高效 FFI 集成。

### 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Flutter | 3.10+ | 跨平台 UI 框架 |
| Dart | 3.10+ | Flutter 开发语言 |
| Rust | stable | 高性能后端计算 |
| flutter_rust_bridge | 2.11.1 | Dart ↔ Rust FFI 桥接 |
| Tokio | 1.x | Rust 异步运行时 |

### 支持平台

- ✅ Android (API 34+)
- ✅ iOS (Xcode 15+)

---

## 项目结构

```
flutter_rust_demo/
├── lib/                        # Flutter/Dart 源代码
│   ├── main.dart               # 应用入口和所有 UI 页面
│   └── src/
│       ├── rust/               # 自动生成的 Rust 绑定代码
│       │   ├── api/            # API 绑定
│       │   └── frb_generated.dart
│       └── utils/
│           └── unit_converter.dart  # 单位换算逻辑
│
├── rust/                       # Rust 源代码
│   ├── src/
│   │   ├── lib.rs              # Rust 库入口
│   │   ├── frb_generated.rs    # 自动生成的桥接代码
│   │   └── api/
│   │       ├── mod.rs          # API 模块入口
│   │       └── simple.rs       # 计算器核心逻辑
│   └── Cargo.toml
│
├── rust_builder/               # Rust 构建工具包 (cargokit)
│   ├── android/                # Android Rust 构建配置
│   ├── ios/                    # iOS Rust 构建配置
│   └── cargokit/               # Cargo 构建工具链
│
├── android/                    # Android 平台原生代码
├── ios/                        # iOS 平台原生代码
│
├── test/                       # 单元测试
│   ├── calculator_test.dart    # Rust FFI 测试
│   └── unit_converter_test.dart
│
├── integration_test/           # 集成测试
├── test_driver/                # 测试驱动
│
├── pubspec.yaml                # Flutter 项目配置
├── flutter_rust_bridge.yaml    # Rust Bridge 代码生成配置
├── analysis_options.yaml       # Dart 静态分析配置
│
└── Docker 构建配置
    ├── Dockerfile              # Docker 镜像定义
    ├── docker_build.sh         # Docker 内部构建脚本
    └── build_apk_docker.sh     # Docker APK 构建入口
```

---

## 构建命令

### 环境准备

```bash
# 安装 Flutter 依赖
flutter pub get

# 安装 Rust Android 编译目标
rustup target add aarch64-linux-android
rustup target add armv7-linux-androideabi
rustup target add x86_64-linux-android
rustup target add i686-linux-android
```

### Docker 构建（推荐）

```bash
# 构建 Debug APK
./build_apk_docker.sh debug

# 构建 Release APK
./build_apk_docker.sh release
```

输出位置：
- Debug: `build/app/outputs/flutter-apk/app-debug.apk`
- Release: `build/app/outputs/flutter-apk/app-release.apk`

### 本地构建

```bash
# 生成 Rust 绑定代码（修改 Rust 代码后需要执行）
flutter_rust_bridge_codegen generate

# 运行应用
flutter run -d <device_id>

# 构建 Android APK
flutter build apk --debug
flutter build apk --release

# 构建 iOS（仅 macOS）
flutter build ios --debug
flutter build ios --release
```

### 测试

```bash
# 运行单元测试
flutter test

# 运行特定测试文件
flutter test test/calculator_test.dart

# 运行集成测试
flutter test integration_test/
```

### 代码分析

```bash
# Dart 静态分析
flutter analyze
```

### 清理

```bash
# 清理 Flutter 构建
flutter clean

# 清理 Rust 构建
cd rust && cargo clean
```

---

## 开发指南

### 修改 Rust 代码后

1. 修改 `rust/src/api/simple.rs`
2. 重新生成绑定代码：`flutter_rust_bridge_codegen generate`
3. 重新构建：`flutter run`

### 修改 Dart/UI 代码后

- 热重载（在 `flutter run` 状态下按 `r`）
- 热重启（按 `R`）

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

## 核心文件说明

### `lib/main.dart`

包含所有 UI 代码，包括：
- `CalculatorApp` - 应用根组件
- `HomePage` - 主页面（导航管理）
- `CalculatorPage` - 计算器页面
- `UnitConverterPage` - 单位换算页面
- `HistoryPage` - 历史记录页面
- `StatsPage` - 统计页面
- `PerformancePage` - 性能测试页面

### `rust/src/api/simple.rs`

Rust 核心计算逻辑，提供：

**数据类型：**
- `Operation` - 运算类型枚举 (Add, Subtract, Multiply, Divide)
- `CalcResult` - 计算结果 (Success/Error)
- `CalculationRecord` - 计算记录
- `CalculatorStats` - 统计信息

**核心函数：**
- `calculate_async(a, b, operation)` - 异步计算
- `calculate_sync(a, b, operation)` - 同步计算
- `create_record(a, b, operation)` - 创建计算记录
- `compute_stats(records)` - 计算统计数据
- `heavy_computation(iterations)` - 模拟耗时计算
- `batch_calculate(requests)` - 批量计算

### `flutter_rust_bridge.yaml`

代码生成配置：

```yaml
rust_input: crate::api      # Rust API 入口模块
rust_root: rust/            # Rust 项目根目录
dart_output: lib/src/rust   # 生成的 Dart 代码输出目录
```

---

## 代码风格

### Dart

- 使用 `flutter_lints` 推荐的 lint 规则
- 类名：PascalCase（如 `CalculatorPage`）
- 变量/方法：camelCase（如 `_calculate`）
- 私有成员以下划线开头（如 `_result`）
- 使用 `const` 构造函数优化性能

### Rust

- 遵循标准 Rust 格式化（`cargo fmt`）
- 公开函数/结构体添加文档注释
- 枚举变体：PascalCase
- 函数：snake_case

---

## 测试策略

项目采用分层测试：

1. **单元测试** (`test/`)
   - Rust FFI 接口测试
   - 边界情况测试（除零、负数、小数）
   - 统计功能测试

2. **集成测试** (`integration_test/`)
   - 端到端 UI 测试

---

## 常见问题

### Rust 代码修改后没有生效？

需要重新生成绑定代码：
```bash
flutter_rust_bridge_codegen generate
```

### 构建时报链接错误？

确保安装了所有 Android 目标：
```bash
rustup target add aarch64-linux-android armv7-linux-androideabi x86_64-linux-android i686-linux-android
```

### Docker 构建网络超时？

Dockerfile 已配置国内镜像源，如果仍然超时，检查网络连接。

### 测试失败提示 RustLib 未初始化？

测试文件中需要先调用：
```dart
setUpAll(() async {
  await RustLib.init();
});
```

---

## 相关文档

- `PROJECT_STRUCTURE.md` - 详细项目文档（中文）
- `README.md` - Flutter 默认说明
