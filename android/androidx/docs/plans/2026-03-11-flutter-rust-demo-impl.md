# Flutter + Rust Calculator Demo Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 构建一个 Flutter + Rust 科学计算器 Android 应用，验证 flutter_rust_bridge 集成方案。

**Architecture:** Rust 层提供计算引擎（四则运算 + 科学函数），通过 flutter_rust_bridge 自动生成绑定，Flutter 层实现 iOS 风格 UI。

**Tech Stack:** Flutter, Rust, flutter_rust_bridge, Docker, cargo-ndk, Android NDK

---

## Task 1: 创建 Flutter 项目结构

**Files:**
- Create: `flutter-rust-demo/` (项目根目录)

**Step 1: 创建 Flutter 项目**

```bash
cd /home/chenpeigen/code/awesome-code/android/androidx
flutter create --template=app --platforms=android,linux flutter-rust-demo
```

Expected: 创建 `flutter-rust-demo/` 目录，包含 Flutter 项目结构

**Step 2: 验证项目创建成功**

```bash
cd flutter-rust-demo && ls -la
```

Expected: 看到 `lib/`, `android/`, `linux/`, `pubspec.yaml` 等

**Step 3: Commit**

```bash
git add flutter-rust-demo/
git commit -m "feat(flutter-rust-demo): initialize Flutter project structure"
```

---

## Task 2: 配置 pubspec.yaml 依赖

**Files:**
- Modify: `flutter-rust-demo/pubspec.yaml`

**Step 1: 更新 pubspec.yaml**

```yaml
name: flutter_rust_demo
description: Flutter + Rust scientific calculator demo
publish_to: 'none'
version: 1.0.0+1

environment:
  sdk: '>=3.0.0 <4.0.0'

dependencies:
  flutter:
    sdk: flutter
  flutter_rust_bridge: ^2.4.0
  ffi: ^2.1.0
  freezed_annotation: ^2.4.0

dev_dependencies:
  flutter_test:
    sdk: flutter
  flutter_lints: ^3.0.0
  ffigen: ^13.0.0
  freezed: ^2.4.0
  build_runner: ^2.4.0
  integration_test:
    sdk: flutter

flutter:
  uses-material-design: true
```

**Step 2: 获取依赖**

```bash
cd flutter-rust-demo && flutter pub get
```

Expected: 依赖下载成功

**Step 3: Commit**

```bash
git add flutter-rust-demo/pubspec.yaml flutter-rust-demo/pubspec.lock
git commit -m "feat(flutter-rust-demo): add flutter_rust_bridge dependencies"
```

---

## Task 3: 创建 Rust 项目结构

**Files:**
- Create: `flutter-rust-demo/rust/Cargo.toml`
- Create: `flutter-rust-demo/rust/src/lib.rs`

**Step 1: 创建 Cargo.toml**

```toml
[package]
name = "calculator"
version = "0.1.0"
edition = "2021"

[lib]
crate-type = ["cdylib", "staticlib"]

[dependencies]
flutter_rust_bridge = "2.4.0"
```

**Step 2: 创建 src/lib.rs 基础结构**

```rust
pub mod api;

/// 计算器操作类型
pub enum BinaryOp {
    Add,
    Subtract,
    Multiply,
    Divide,
}

/// 科学函数类型
pub enum ScientificOp {
    Sin,
    Cos,
    Tan,
    Log,
    Log10,
    Sqrt,
    Power,
}

/// 执行基础二元运算
pub fn calculate_binary(left: f64, op: BinaryOp, right: f64) -> Result<f64, String> {
    match op {
        BinaryOp::Add => Ok(left + right),
        BinaryOp::Subtract => Ok(left - right),
        BinaryOp::Multiply => Ok(left * right),
        BinaryOp::Divide => {
            if right == 0.0 {
                Err("Division by zero".to_string())
            } else {
                Ok(left / right)
            }
        }
    }
}

/// 执行科学函数运算
pub fn calculate_scientific(op: ScientificOp, value: f64) -> Result<f64, String> {
    match op {
        ScientificOp::Sin => Ok(value.sin()),
        ScientificOp::Cos => Ok(value.cos()),
        ScientificOp::Tan => Ok(value.tan()),
        ScientificOp::Log => {
            if value <= 0.0 {
                Err("Log of non-positive number".to_string())
            } else {
                Ok(value.ln())
            }
        }
        ScientificOp::Log10 => {
            if value <= 0.0 {
                Err("Log10 of non-positive number".to_string())
            } else {
                Ok(value.log10())
            }
        }
        ScientificOp::Sqrt => {
            if value < 0.0 {
                Err("Square root of negative number".to_string())
            } else {
                Ok(value.sqrt())
            }
        }
        ScientificOp::Power => Ok(value), // Power 需要 base 和 exponent，这里返回原值
    }
}

/// 执行幂运算 (base^exponent)
pub fn calculate_power(base: f64, exponent: f64) -> Result<f64, String> {
    if base == 0.0 && exponent < 0.0 {
        Err("Zero cannot be raised to a negative power".to_string())
    } else {
        Ok(base.powf(exponent))
    }
}

// Flutter Rust Bridge 入口
#[flutter_rust_bridge::frb]
pub fn add(left: f64, right: f64) -> f64 {
    left + right
}

#[flutter_rust_bridge::frb]
pub fn subtract(left: f64, right: f64) -> f64 {
    left - right
}

#[flutter_rust_bridge::frb]
pub fn multiply(left: f64, right: f64) -> f64 {
    left * right
}

#[flutter_rust_bridge::frb]
pub fn divide(left: f64, right: f64) -> Result<f64, String> {
    if right == 0.0 {
        Err("Division by zero".to_string())
    } else {
        Ok(left / right)
    }
}

#[flutter_rust_bridge::frb]
pub fn sin(value: f64) -> f64 {
    value.sin()
}

#[flutter_rust_bridge::frb]
pub fn cos(value: f64) -> f64 {
    value.cos()
}

#[flutter_rust_bridge::frb]
pub fn tan(value: f64) -> f64 {
    value.tan()
}

#[flutter_rust_bridge::frb]
pub fn log(value: f64) -> Result<f64, String> {
    if value <= 0.0 {
        Err("Log of non-positive number".to_string())
    } else {
        Ok(value.ln())
    }
}

#[flutter_rust_bridge::frb]
pub fn log10(value: f64) -> Result<f64, String> {
    if value <= 0.0 {
        Err("Log10 of non-positive number".to_string())
    } else {
        Ok(value.log10())
    }
}

#[flutter_rust_bridge::frb]
pub fn sqrt(value: f64) -> Result<f64, String> {
    if value < 0.0 {
        Err("Square root of negative number".to_string())
    } else {
        Ok(value.sqrt())
    }
}

#[flutter_rust_bridge::frb]
pub fn power(base: f64, exponent: f64) -> Result<f64, String> {
    if base == 0.0 && exponent < 0.0 {
        Err("Zero cannot be raised to a negative power".to_string())
    } else {
        Ok(base.powf(exponent))
    }
}
```

**Step 3: Commit**

```bash
git add flutter-rust-demo/rust/
git commit -m "feat(flutter-rust-demo): add Rust calculator engine with FRB annotations"
```

---

## Task 4: 创建 Docker 构建环境

**Files:**
- Create: `flutter-rust-demo/docker/Dockerfile`
- Create: `flutter-rust-demo/Makefile`

**Step 1: 创建 Dockerfile**

```dockerfile
# 使用官方 Flutter 镜像作为基础
FROM cirrusci/flutter:stable

# 安装 Rust
RUN curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y
ENV PATH="/root/.cargo/bin:${PATH}"

# 安装 flutter_rust_bridge_codegen 和 cargo-ndk
RUN cargo install flutter_rust_bridge_codegen cargo-ndk

# 安装 Android NDK 所需的 Rust target
RUN rustup target add aarch64-linux-android armv7-linux-androideabi

# 安装 LLVM (ffigen 需要)
RUN apt-get update && apt-get install -y llvm clang libclang-dev

WORKDIR /app

# 默认命令
CMD ["bash"]
```

**Step 2: 创建 Makefile**

```makefile
.PHONY: build docker-build clean run

# Docker 镜像名称
IMAGE_NAME = flutter-rust-builder

# 构建 Docker 镜像
docker-image:
	docker build -t $(IMAGE_NAME) -f docker/Dockerfile docker/

# 在 Docker 中运行命令
docker-run:
	docker run --rm -it -v $(PWD):/app -w /app $(IMAGE_NAME) bash

# 完整构建流程（在 Docker 中执行）
docker-build: docker-image
	docker run --rm -v $(PWD):/app -w /app $(IMAGE_NAME) make build

# 生成绑定代码 + 编译 Rust + 构建 Flutter
build:
	flutter_rust_bridge_codegen generate
	cd rust && cargo ndk -t arm64-v8a -o ../android/app/src/main/jniLibs build --release
	flutter build apk

# 仅生成绑定代码
generate:
	flutter_rust_bridge_codegen generate

# 仅编译 Rust
build-rust:
	cd rust && cargo ndk -t arm64-v8a -o ../android/app/src/main/jniLibs build --release

# 运行应用（需要连接设备或启动模拟器）
run:
	flutter run

# 清理构建产物
clean:
	flutter clean
	cd rust && cargo clean
	rm -rf android/app/src/main/jniLibs
```

**Step 3: Commit**

```bash
git add flutter-rust-demo/docker/ flutter-rust-demo/Makefile
git commit -m "feat(flutter-rust-demo): add Docker build environment and Makefile"
```

---

## Task 5: 配置 Android NDK 支持

**Files:**
- Modify: `flutter-rust-demo/android/app/build.gradle`

**Step 1: 在 android/app/build.gradle 中添加 NDK 配置**

在 `android` 块内添加：

```gradle
android {
    // ... 现有配置 ...

    defaultConfig {
        // ... 现有配置 ...
        ndk {
            abiFilters 'arm64-v8a'
        }
    }
}
```

**Step 2: Commit**

```bash
git add flutter-rust-demo/android/app/build.gradle
git commit -m "feat(flutter-rust-demo): configure Android NDK for arm64-v8a"
```

---

## Task 6: 创建 Flutter 计算器 UI

**Files:**
- Create: `flutter-rust-demo/lib/main.dart`
- Create: `flutter-rust-demo/lib/calculator_button.dart`
- Create: `flutter-rust-demo/lib/calculator_screen.dart`

**Step 1: 创建 CalculatorButton 组件**

```dart
// lib/calculator_button.dart
import 'package:flutter/material.dart';

class CalculatorButton extends StatelessWidget {
  final String text;
  final VoidCallback onPressed;
  final Color? backgroundColor;
  final Color? textColor;
  final double? fontSize;

  const CalculatorButton({
    super.key,
    required this.text,
    required this.onPressed,
    this.backgroundColor,
    this.textColor,
    this.fontSize,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(4.0),
      child: ElevatedButton(
        style: ElevatedButton.styleFrom(
          backgroundColor: backgroundColor ?? const Color(0xFF333333),
          foregroundColor: textColor ?? Colors.white,
          shape: const CircleBorder(),
          padding: const EdgeInsets.all(20),
        ),
        onPressed: onPressed,
        child: Text(
          text,
          style: TextStyle(fontSize: fontSize ?? 28),
        ),
      ),
    );
  }
}
```

**Step 2: 创建 CalculatorScreen**

```dart
// lib/calculator_screen.dart
import 'package:flutter/material.dart';
import 'calculator_button.dart';

enum CalculatorMode {
  basic,
  scientific,
}

class CalculatorScreen extends StatefulWidget {
  const CalculatorScreen({super.key});

  @override
  State<CalculatorScreen> createState() => _CalculatorScreenState();
}

class _CalculatorScreenState extends State<CalculatorScreen> {
  String _display = '0';
  String _previousValue = '';
  String _operation = '';
  bool _waitingForOperand = false;
  CalculatorMode _mode = CalculatorMode.basic;

  void _inputDigit(String digit) {
    setState(() {
      if (_waitingForOperand) {
        _display = digit;
        _waitingForOperand = false;
      } else {
        _display = _display == '0' ? digit : _display + digit;
      }
    });
  }

  void _inputDecimal() {
    setState(() {
      if (_waitingForOperand) {
        _display = '0.';
        _waitingForOperand = false;
      } else if (!_display.contains('.')) {
        _display += '.';
      }
    });
  }

  void _clear() {
    setState(() {
      _display = '0';
      _previousValue = '';
      _operation = '';
      _waitingForOperand = false;
    });
  }

  void _toggleSign() {
    setState(() {
      if (_display != '0') {
        if (_display.startsWith('-')) {
          _display = _display.substring(1);
        } else {
          _display = '-$_display';
        }
      }
    });
  }

  void _percent() {
    setState(() {
      _display = (double.parse(_display) / 100).toString();
    });
  }

  void _performOperation(String op) {
    setState(() {
      _previousValue = _display;
      _operation = op;
      _waitingForOperand = true;
    });
  }

  void _calculateResult() {
    if (_operation.isEmpty || _previousValue.isEmpty) return;

    // TODO: 调用 Rust 计算函数
    final left = double.parse(_previousValue);
    final right = double.parse(_display);
    double result;

    switch (_operation) {
      case '+':
        result = left + right; // 临时使用 Dart 计算
        break;
      case '-':
        result = left - right;
        break;
      case '×':
        result = left * right;
        break;
      case '÷':
        result = left / right;
        break;
      default:
        return;
    }

    setState(() {
      _display = _formatResult(result);
      _operation = '';
      _previousValue = '';
      _waitingForOperand = true;
    });
  }

  void _performScientific(String func) {
    final value = double.parse(_display);
    double result;

    switch (func) {
      case 'sin':
        result = value.sin(); // 临时使用 Dart 计算
        break;
      case 'cos':
        result = value.cos();
        break;
      case 'tan':
        result = value.tan();
        break;
      case '√':
        result = value < 0 ? double.nan : sqrt(value);
        break;
      case 'ln':
        result = value <= 0 ? double.nan : log(value);
        break;
      case 'log':
        result = value <= 0 ? double.nan : log(value) / ln10;
        break;
      case 'x²':
        result = value * value;
        break;
      default:
        return;
    }

    setState(() {
      _display = _formatResult(result);
      _waitingForOperand = true;
    });
  }

  String _formatResult(double value) {
    if (value.isNaN) return 'Error';
    if (value.isInfinite) return 'Error';
    if (value == value.truncateToDouble()) {
      return value.toInt().toString();
    }
    return value.toStringAsFixed(8).replaceAll(RegExp(r'0+$'), '').replaceAll(RegExp(r'\.$'), '');
  }

  void _toggleMode() {
    setState(() {
      _mode = _mode == CalculatorMode.basic
          ? CalculatorMode.scientific
          : CalculatorMode.basic;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        backgroundColor: Colors.black,
        title: const Text('Calculator', style: TextStyle(color: Colors.white)),
        actions: [
          IconButton(
            icon: Icon(
              _mode == CalculatorMode.scientific
                  ? Icons.calculate
                  : Icons.functions,
              color: Colors.white,
            ),
            onPressed: _toggleMode,
          ),
        ],
      ),
      body: Column(
        children: [
          // 显示区
          Expanded(
            child: Container(
              padding: const EdgeInsets.all(20),
              alignment: Alignment.bottomRight,
              child: Text(
                _display,
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 60,
                  fontWeight: FontWeight.w300,
                ),
              ),
            ),
          ),

          // 科学函数行（仅在科学模式显示）
          if (_mode == CalculatorMode.scientific)
            _buildScientificRow(),

          // 基础按钮区域
          _buildBasicButtons(),
        ],
      ),
    );
  }

  Widget _buildScientificRow() {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          _buildSmallButton('sin'),
          _buildSmallButton('cos'),
          _buildSmallButton('tan'),
          _buildSmallButton('√'),
          _buildSmallButton('ln'),
          _buildSmallButton('log'),
          _buildSmallButton('x²'),
        ],
      ),
    );
  }

  Widget _buildSmallButton(String text) {
    return Padding(
      padding: const EdgeInsets.all(2.0),
      child: SizedBox(
        width: 48,
        height: 48,
        child: ElevatedButton(
          style: ElevatedButton.styleFrom(
            backgroundColor: const Color(0xFF333333),
            foregroundColor: Colors.white,
            padding: EdgeInsets.zero,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(24),
            ),
          ),
          onPressed: () => _performScientific(text),
          child: Text(text, style: const TextStyle(fontSize: 14)),
        ),
      ),
    );
  }

  Widget _buildBasicButtons() {
    return Column(
      children: [
        Row(
          children: [
            _buildButton('C', const Color(0xFFA5A5A5), Colors.black),
            _buildButton('±', const Color(0xFFA5A5A5), Colors.black),
            _buildButton('%', const Color(0xFFA5A5A5), Colors.black),
            _buildButton('÷', const Color(0xFFFF9F0A)),
          ],
        ),
        Row(
          children: [
            _buildButton('7'),
            _buildButton('8'),
            _buildButton('9'),
            _buildButton('×', const Color(0xFFFF9F0A)),
          ],
        ),
        Row(
          children: [
            _buildButton('4'),
            _buildButton('5'),
            _buildButton('6'),
            _buildButton('-', const Color(0xFFFF9F0A)),
          ],
        ),
        Row(
          children: [
            _buildButton('1'),
            _buildButton('2'),
            _buildButton('3'),
            _buildButton('+', const Color(0xFFFF9F0A)),
          ],
        ),
        Row(
          children: [
            _buildWideButton('0'),
            _buildButton('.'),
            _buildButton('=', const Color(0xFFFF9F0A)),
          ],
        ),
      ],
    );
  }

  Widget _buildButton(String text, [Color? bgColor, Color? textColor]) {
    return Expanded(
      child: CalculatorButton(
        text: text,
        backgroundColor: bgColor,
        textColor: textColor,
        onPressed: () {
          if (text == 'C') {
            _clear();
          } else if (text == '±') {
            _toggleSign();
          } else if (text == '%') {
            _percent();
          } else if (text == '.' || text == '0' || int.tryParse(text) != null) {
            if (text == '.') {
              _inputDecimal();
            } else {
              _inputDigit(text);
            }
          } else if (text == '=') {
            _calculateResult();
          } else {
            _performOperation(text);
          }
        },
      ),
    );
  }

  Widget _buildWideButton(String text) {
    return Expanded(
      flex: 2,
      child: Padding(
        padding: const EdgeInsets.all(4.0),
        child: ElevatedButton(
          style: ElevatedButton.styleFrom(
            backgroundColor: const Color(0xFF333333),
            foregroundColor: Colors.white,
            shape: const StadiumBorder(),
            padding: const EdgeInsets.symmetric(horizontal: 30, vertical: 20),
          ),
          onPressed: () => _inputDigit(text),
          child: Align(
            alignment: Alignment.centerLeft,
            child: Text(text, style: const TextStyle(fontSize: 28)),
          ),
        ),
      ),
    );
  }
}
```

**Step 3: 更新 main.dart**

```dart
// lib/main.dart
import 'package:flutter/material.dart';
import 'calculator_screen.dart';

void main() {
  runApp(const CalculatorApp());
}

class CalculatorApp extends StatelessWidget {
  const CalculatorApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Rust Calculator',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: Colors.orange,
          brightness: Brightness.dark,
        ),
        useMaterial3: true,
      ),
      home: const CalculatorScreen(),
    );
  }
}
```

**Step 4: Commit**

```bash
git add flutter-rust-demo/lib/
git commit -m "feat(flutter-rust-demo): add iOS-style calculator UI"
```

---

## Task 7: 集成 Rust 计算函数（占位）

**Files:**
- Create: `flutter-rust-demo/lib/bridge_generated.dart` (自动生成)
- Modify: `flutter-rust-demo/lib/calculator_screen.dart`

**Step 1: 生成绑定代码（在 Docker 中）**

```bash
cd flutter-rust-demo && make docker-build
```

Expected: 生成 `lib/bridge_generated.dart` 和编译 Rust 库

**Step 2: 在 calculator_screen.dart 中集成 Rust**

```dart
// 在文件顶部添加导入
import 'bridge_generated.dart';

// 创建 Rust API 实例
late final RustApi _rustApi;

@override
void initState() {
  super.initState();
  _rustApi = RustApi();
}

// 更新 _calculateResult 方法中的计算调用
void _calculateResult() {
  if (_operation.isEmpty || _previousValue.isEmpty) return;

  final left = double.parse(_previousValue);
  final right = double.parse(_display);

  _rustApi.add(left: left, right: right).then((result) {
    setState(() {
      _display = _formatResult(result);
      _operation = '';
      _previousValue = '';
      _waitingForOperand = true;
    });
  });
}
```

**Step 3: Commit**

```bash
git add flutter-rust-demo/lib/
git commit -m "feat(flutter-rust-demo): integrate Rust calculator functions"
```

---

## Task 8: 添加 .gitignore

**Files:**
- Create: `flutter-rust-demo/.gitignore`

**Step 1: 创建 .gitignore**

```gitignore
# Flutter
.dart_tool/
.flutter-plugins
.flutter-plugins-dependencies
build/
*.lock
!pubspec.lock

# Rust
rust/target/
**/*.so
**/*.a
**/*.dll

# Android
android/.gradle/
android/app/src/main/jniLibs/
*.apk
*.aab

# IDE
.idea/
.vscode/
*.iml

# OS
.DS_Store
Thumbs.db
```

**Step 2: Commit**

```bash
git add flutter-rust-demo/.gitignore
git commit -m "chore(flutter-rust-demo): add .gitignore for Flutter and Rust"
```

---

## Task 9: 构建并测试

**Step 1: 构建 Docker 镜像**

```bash
cd flutter-rust-demo && make docker-image
```

Expected: Docker 镜像构建成功

**Step 2: 完整构建**

```bash
make docker-build
```

Expected: 生成 APK 文件

**Step 3: 验证 APK**

```bash
ls -la build/app/outputs/flutter-apk/
```

Expected: 看到 `app-release.apk`

**Step 4: Commit 最终状态**

```bash
git add -A
git commit -m "feat(flutter-rust-demo): complete Flutter + Rust calculator demo"
```

---

## 任务总结

| Task | 描述 | 预估时间 |
|------|------|----------|
| 1 | 创建 Flutter 项目结构 | 5 min |
| 2 | 配置 pubspec.yaml 依赖 | 5 min |
| 3 | 创建 Rust 项目结构 | 10 min |
| 4 | 创建 Docker 构建环境 | 10 min |
| 5 | 配置 Android NDK 支持 | 5 min |
| 6 | 创建 Flutter 计算器 UI | 20 min |
| 7 | 集成 Rust 计算函数 | 15 min |
| 8 | 添加 .gitignore | 2 min |
| 9 | 构建并测试 | 15 min |

**总计**: 约 1.5 小时（不含 Docker 镜像下载时间）
