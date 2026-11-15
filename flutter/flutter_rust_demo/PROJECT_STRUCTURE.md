# 项目结构

> 详细架构说明、FRB 特性清单、构建注意事项见 [README.md](./README.md)。

```
flutter_rust_demo/
├── lib/
│   ├── main.dart                 # 入口：RustLib.init() + MaterialApp
│   └── src/
│       ├── pages/                # 5 个功能页面（每页对应一个 Rust 能力）
│       │   ├── home_shell.dart   # 导航外壳（窄屏 NavigationBar / 宽屏 NavigationRail）
│       │   ├── mandelbrot_page.dart   # 分形渲染（像素跨 FFI + 缩放交互）
│       │   ├── primes_page.dart       # 素数实验室（并发任务 + BigInt）
│       │   ├── matrix_page.dart       # 矩阵计算器（结构化错误模式匹配）
│       │   ├── text_page.dart         # 文本分析（实时 + Unicode）
│       │   └── stream_page.dart       # 流式任务（StreamSink 事件流）
│       └── rust/                 # FRB 生成代码（勿手改）
│           ├── api/              # 与 rust/src/api/ 一一对应的 Dart 绑定
│           └── frb_generated*.dart
├── rust/                         # Rust 核心（cargo test = 41 个单元测试）
│   ├── src/api/*.rs              # mandelbrot / primes / matrix / text / streaming / simple
│   └── .cargo/config.toml        # Android 交叉编译目标配置
├── rust_builder/                 # cargokit（FRB 自带，负责把 Rust 编进各平台）
├── test/home_shell_test.dart     # Widget 测试（无需 FFI）
├── integration_test/             # 集成测试（真机验证 FFI 全链路）
├── android/ ios/ macos/          # 平台目录（macos 为本次新增，需 Xcode）
└── flutter_rust_bridge.yaml      # codegen 配置（rust_input: crate::api）
```

## 数据流

```
UI 事件 (Flutter)
   ↓ 生成代码 (frb_generated)
Rust api 函数 (async → Rust 线程池 / sync → 直接调用)
   ↓ 纯函数核心（可 #[cfg(test)] 单测）
结果（结构体 / 枚举 / Vec<u8> / Stream 事件）
   ↓
setState 刷新 (Flutter)
```

## 命令速查

```bash
cd rust && cargo test                 # Rust 单元测试
flutter test                          # Flutter Widget 测试
flutter_rust_bridge_codegen generate  # Rust API 变动后重新生成桥接
GRADLE_USER_HOME=~/.gradle-home-apfs flutter build apk --debug   # Android 构建（见 README 注意事项）
```
