# Flutter + Rust 计算器 Demo 设计文档

## 概述

使用 Flutter + Rust 构建一个科学计算器 Android 应用，通过 `flutter_rust_bridge` 实现跨语言调用，使用 Docker 进行构建。

## 目标

- 验证 Flutter + Rust 技术栈在 Android 上的可行性
- 学习 flutter_rust_bridge 的集成方式
- 实现一个可用的科学计算器

## 项目结构

```
flutter-rust-demo/
├── rust/                          # Rust 计算引擎
│   ├── Cargo.toml
│   ├── src/
│   │   └── lib.rs                 # 计算逻辑 + flutter_rust_bridge 导出
│   └── build.rs                   # Android NDK 交叉编译配置
│
├── lib/                           # Flutter 应用
│   ├── main.dart                  # 入口
│   └── bridge_generated.dart      # 自动生成的绑定代码
│
├── android/                       # Android 平台配置
├── linux/                         # Linux 平台配置（可选，用于本地测试）
│
├── docker/
│   └── Dockerfile                 # 构建环境
├── Makefile                       # 构建命令封装
└── pubspec.yaml                   # Flutter 依赖
```

## Rust 计算引擎

### API 设计

```rust
// rust/src/lib.rs

/// 计算器操作类型
pub enum BinaryOp {
    Add,
    Subtract,
    Multiply,
    Divide,
}

/// 科学函数类型
pub enum ScientificOp {
    Sin,      // 正弦
    Cos,      // 余弦
    Tan,      // 正切
    Log,      // 自然对数
    Log10,    // 常用对数
    Sqrt,     // 平方根
    Power,    // 幂运算 x^y
}

/// 执行基础二元运算
pub fn calculate_binary(left: f64, op: BinaryOp, right: f64) -> Result<f64, String>

/// 执行科学函数运算
pub fn calculate_scientific(op: ScientificOp, value: f64) -> Result<f64, String>

/// 执行带幂的科学运算 (x^y)
pub fn calculate_power(base: f64, exponent: f64) -> Result<f64, String>
```

### 设计要点

- 所有函数返回 `Result<f64, String>`，错误信息传给 Flutter 显示
- 使用 `flutter_rust_bridge` 的属性宏自动生成 Dart 绑定
- 不引入复杂依赖，只用 Rust 标准库的数学函数

## Flutter UI

### 界面布局

仿 iOS 计算器风格：

```
┌─────────────────────────┐
│                     123 │  ← 显示区（当前值/结果）
├─────────────────────────┤
│  sin  │  cos  │  tan  │ √│  ← 科学函数行
├─────────────────────────┤
│   C   │   ±   │   %   │ ÷ │  ← 清除/符号/百分比/除
├─────────────────────────┤
│   7   │   8   │   9   │ × │
│   4   │   5   │   6   │ - │
│   1   │   2   │   3   │ + │
│       0     │   .   │ = │
└─────────────────────────┘
```

### 状态管理

- 使用 `setState`（简单场景，无需额外状态管理库）
- 状态：`displayValue`、`pendingOp`、`pendingValue`

### 依赖

```yaml
dependencies:
  flutter_rust_bridge: ^2.0.0
  ffi: ^2.0.0
```

**不引入**：第三方状态管理、路由库（保持最小化）

## Docker 构建环境

### Dockerfile

```dockerfile
FROM ghcr.io/cunarist/rust-flutter-docker:latest

WORKDIR /app

# 构建流程:
# 1. flutter_rust_bridge_codegen generate
# 2. cargo ndk -t arm64-v8a build --release
# 3. flutter build apk
```

### Makefile

```makefile
build-docker:
	docker run --rm -v $(PWD):/app -w /app flutter-rust-builder make build

build:
	flutter_rust_bridge_codegen generate
	cd rust && cargo ndk -t arm64-v8a build --release
	flutter build apk

run:
	flutter run
```

## 构建流程

1. `make build-docker` → 在 Docker 中完成所有编译
2. 输出 APK 到 `build/app/outputs/`

## 风险与缓解

| 风险 | 缓解措施 |
|------|----------|
| flutter_rust_bridge 版本兼容性 | 使用稳定版本，参考官方示例 |
| Docker 镜像过大 | 使用社区维护的精简镜像 |
| Android NDK 版本问题 | 固定 NDK 版本 |