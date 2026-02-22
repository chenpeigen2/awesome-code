#!/bin/bash
set -e

echo "=== Flutter Rust Bridge Android APK Build ==="

# 设置环境变量
export ANDROID_HOME=/opt/android-sdk
export ANDROID_SDK_ROOT=/opt/android-sdk
export NDK_HOME=${ANDROID_HOME}/ndk/27.0.12077973
source $HOME/.cargo/env

echo "=== 环境信息 ==="
echo "Flutter version: $(flutter --version | head -1)"
echo "Rust version: $(rustc --version)"
echo "Cargo version: $(cargo --version)"

echo "=== 清理之前的构建 ==="
flutter clean

echo "=== 获取依赖 ==="
flutter pub get

echo "=== 生成 Rust 绑定代码 ==="
flutter_rust_bridge_codegen generate

echo "=== 构建 Android APK (release) ==="
flutter build apk --release

echo "=== 构建完成 ==="
ls -la build/app/outputs/flutter-apk/

echo "=== APK 文件信息 ==="
APK_FILE=$(find build/app/outputs/flutter-apk -name "*.apk" | head -1)
if [ -n "$APK_FILE" ]; then
    echo "APK 文件: $APK_FILE"
    echo "文件大小: $(du -h "$APK_FILE" | cut -f1)"
fi

echo "=== Done ==="
