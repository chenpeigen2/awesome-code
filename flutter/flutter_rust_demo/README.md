# Rust Lab · Flutter + flutter_rust_bridge

一个真正展示 Rust 能力的 Flutter 桌面/移动应用：5 个功能页面全部由 Rust 承担计算核心，
Flutter 只负责 UI。演示 flutter_rust_bridge (FRB) 的完整特性面。

## 架构

```
rust/src/api/            Rust 侧（核心逻辑，全部有 #[cfg(test)] 单元测试）
├── mandelbrot.rs        分形渲染：RGBA 像素缓冲跨 FFI、重计算异步化
├── primes.rs            素数筛/因式分解/第 n 个素数
├── matrix.rs            矩阵乘法/行列式：结构化错误枚举（Dart 侧 sealed class 模式匹配）
├── text.rs              文本统计：Unicode 感知、词频 Top-K、回文
├── streaming.rs         StreamSink 进度流：蒙特卡洛估算 π，Rust→Dart 多事件推送
└── simple.rs            引擎信息（#[frb(sync)] 同步调用）+ 最小 async 示例

lib/src/pages/           Flutter 侧（每页对应一个 Rust 能力）
├── mandelbrot_page.dart  像素渲染 RawImage + 单击放大/双击缩小 + 经典取景预设
├── primes_page.dart      三任务并发 (Future.wait)，BigInt 跨界
├── matrix_page.dart      可调维度矩阵编辑器 + 错误类别模式匹配展示
├── text_page.dart        实时分析（输入即分析）+ 频次条形图
└── stream_page.dart      进度条实时刷新（订阅 Rust 事件流）

lib/src/rust/            FRB 自动生成的桥接代码（勿手改）
```

## 演示的 FRB 特性

| 特性 | 位置 |
|------|------|
| `async fn` → Dart Future（Rust 线程池执行，不阻塞 UI） | mandelbrot / primes |
| 大块二进制 `Vec<u8>`（RGBA 像素）跨桥 | mandelbrot |
| 结构体 / 枚举 / Option 自动转换 | 全部 |
| `#[frb(sync)]` 同步调用（零开销） | simple::engine_info |
| `StreamSink<T>` 事件流（独立线程推送） | streaming |
| 结构化错误枚举 → Dart sealed class + switch 模式匹配 | matrix |
| `#[cfg(test)]` 纯函数核心 + 薄胶水层（可脱离 FFI 单测） | 全部 |

## 测试

```bash
# Rust：41 个单元测试（预期值来自 OEIS 数列、数学性质、手工计算）
cd rust && cargo test

# Flutter：Widget 测试（不需要 FFI）
flutter test

# 集成测试（真机/模拟器，验证 FFI 全链路）
flutter test integration_test
```

## 构建注意事项（本机踩过的坑）

1. **Android 构建用 JDK 21**（Gradle 8.14 与 JDK 25 不兼容；已验证 AGP 9 + Gradle 9.x 路线
   在 Flutter 3.47.5 下无法编译 flutter_tools 的 gradle 脚本，故保持 AGP 8.11.1 组合）：
   `flutter config --jdk-dir=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home`，
   构建完成后可还原（`flutter config --jdk-dir=`）。
2. **Gradle 缓存不能放 T7 移动硬盘（exFAT）**：macOS 写扩展属性会生成 AppleDouble 文件
   （`._*`），Gradle 读取时直接崩溃（kotlin-dsl 脚本编译失败）。已将
   `~/.gradle/caches` 指向 APFS 本地盘 `~/.gradle-home-apfs/caches`；T7 上原缓存保留未动，
   可用 `dot_clean -m` 清理后再考虑迁回。
3. **国内网络**：`download.flutter.io` 证书被墙导致 `androidx.test:runner:1.2+` 动态版本
   解析失败。已在 `android/build.gradle.kts` 用 resolutionStrategy 固定为具体版本
   （runner/rules 1.6.x、espresso 3.6.1）；`plugins.gradle.org` 被墙，pluginManagement
   已加 aliyun/tencent 镜像。
4. **macOS 桌面**：需安装 Xcode 后 `flutter run -d macos`（项目已含 macos/ 目录）。
5. **FRB codegen**：`flutter_rust_bridge_codegen generate`（二进制位于 `~/.local/bin`，
   或 `cargo install flutter_rust_bridge_codegen@2.11.1`）。

## Docker 构建 APK

仓库内有现成脚本（不依赖本机 JDK/网络配置）：

```bash
./docker_build.sh
```
