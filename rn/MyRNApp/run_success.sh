#!/bin/bash

# React Native MyRNApp 成功运行脚本
# 支持 Linux 环境，使用 Java 17

set -e

# 使用 Java 17
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# 配置 Android SDK
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH

# 创建 local.properties
echo "sdk.dir=$ANDROID_HOME" > android/local.properties

echo "=========================================="
echo "  React Native MyRNApp 启动脚本"
echo "=========================================="
echo ""
echo "📋 使用 Java 版本:"
java -version 2>&1 | head -1
echo ""

# 清理端口
if lsof -ti:8081 > /dev/null 2>&1; then
    echo "🔧 清理端口 8081..."
    lsof -ti:8081 | xargs kill -9
    sleep 2
fi

# 启动 Metro
echo "🚀 启动 Metro bundler..."
npm start &
METRO_PID=$!
echo "   Metro PID: $METRO_PID"
echo ""

echo "⏳ 等待 Metro 启动..."
sleep 10

# 检查 Metro 状态
if curl -s http://localhost:8081/status | grep -q "packager-status:running"; then
    echo "✅ Metro bundler 运行正常"
else
    echo "❌ Metro 启动失败"
    kill $METRO_PID 2>/dev/null || true
    exit 1
fi

echo ""
echo "📱 构建 Android 应用..."
cd android

# 清理并构建
echo "🧹 清理并构建..."
./gradlew clean
./gradlew assembleDebug --no-daemon

BUILD_STATUS=$?

cd ..

# 清理函数
cleanup() {
    echo ""
    echo "🛑 停止 Metro bundler (PID: $METRO_PID)..."
    kill $METRO_PID 2>/dev/null || true
    echo "✅ 已停止"
}

trap cleanup EXIT

if [ $BUILD_STATUS -eq 0 ]; then
    echo ""
    echo "=========================================="
    echo "  ✅ 构建成功!"
    echo "=========================================="
    echo ""
    echo "APK 文件位置: android/app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "📝 后续步骤:"
    echo "   1. 连接 Android 设备或启动模拟器"
    echo "   2. 运行: ./gradlew installDebug"
    echo "   3. 或使用: npm run android"
else
    echo ""
    echo "=========================================="
    echo "  ❌ 构建失败"
    echo "=========================================="
    echo ""
    echo "请检查:"
    echo "   - Android SDK 是否正确安装"
    echo "   - 设备是否已连接 (adb devices)"
    echo "   - 运行: npx react-native doctor"
    exit 1
fi