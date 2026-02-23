#!/bin/bash
set -e

# 用法: ./docker_build.sh [debug|release]
# 默认构建 debug APK

BUILD_TYPE="${1:-debug}"

if [[ "$BUILD_TYPE" != "debug" && "$BUILD_TYPE" != "release" ]]; then
    echo "错误: 构建类型必须是 'debug' 或 'release'"
    echo "用法: $0 [debug|release]"
    exit 1
fi

echo "=== Flutter Rust Bridge Android APK Build (${BUILD_TYPE}) ==="

# 设置环境变量
export ANDROID_HOME=/opt/android-sdk
export ANDROID_SDK_ROOT=/opt/android-sdk
export NDK_HOME=${ANDROID_HOME}/ndk/27.0.12077973
source $HOME/.cargo/env

# 配置 Flutter 中国镜像（解决 storage.googleapis.com 下载超时问题）
export PUB_HOSTED_URL=https://pub.flutter-io.cn
export FLUTTER_STORAGE_BASE_URL=https://storage.flutter-io.cn

# 配置 GitHub 代理加速
export GIT_PROXY_COMMAND="env -u http_proxy -u https_proxy"

echo "=== 环境信息 ==="
echo "Flutter version: $(flutter --version | head -1)"
echo "Rust version: $(rustc --version)"
echo "Cargo version: $(cargo --version)"

echo "=== 清理之前的构建 ==="
flutter clean
# 清理 Gradle 缓存，避免锁文件冲突
rm -rf android/.gradle
rm -rf build

echo "=== 获取依赖 ==="
# 尝试多次下载依赖
for i in {1..3}; do
    echo "尝试获取依赖 (第 $i 次)..."
    if flutter pub get; then
        echo "依赖获取成功!"
        break
    else
        echo "依赖获取失败，等待 10 秒后重试..."
        sleep 10
    fi
done

echo "=== 生成 Rust 绑定代码 ==="
flutter_rust_bridge_codegen generate

echo "=== 构建 Android APK (${BUILD_TYPE}) ==="
if [[ "$BUILD_TYPE" == "debug" ]]; then
    flutter build apk --debug
else
    flutter build apk --release
fi

echo "=== 构建完成 ==="
ls -la build/app/outputs/flutter-apk/

echo "=== APK 文件信息 ==="
if [[ "$BUILD_TYPE" == "debug" ]]; then
    APK_FILE="build/app/outputs/flutter-apk/app-debug.apk"
else
    APK_FILE="build/app/outputs/flutter-apk/app-release.apk"
fi

if [ -f "$APK_FILE" ]; then
    echo "APK 文件: $APK_FILE"
    echo "文件大小: $(du -h "$APK_FILE" | cut -f1)"
fi

echo "=== Done ==="
