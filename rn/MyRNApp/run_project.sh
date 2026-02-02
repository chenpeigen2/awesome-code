#!/bin/bash

set -e

echo "=========================================="
echo "  React Native MyRNApp 启动脚本"
echo "=========================================="
echo ""

# 函数：检查命令是否存在
check_command() {
    if ! command -v $1 &> /dev/null; then
        echo "❌ 未找到命令: $1"
        return 1
    fi
    echo "✅ 找到命令: $1"
    return 0
}

# 检查必要命令
echo "🔍 检查环境..."
check_command node || exit 1
check_command npm || exit 1
check_command java || exit 1
check_command adb || echo "⚠️  adb 未找到，Android 调试可能受限"
echo ""

# 检查端口占用
if lsof -ti:8081 > /dev/null 2>&1; then
    echo "⚠️  端口 8081 被占用，正在清理..."
    lsof -ti:8081 | xargs kill -9
    echo "✅ 端口已清理"
    echo ""
fi

# 检查 node_modules
if [ ! -d "node_modules" ]; then
    echo "⚠️  node_modules 不存在，正在安装依赖..."
    npm install
    echo ""
fi

# 启动 Metro bundler
echo "📦 启动 Metro bundler..."
echo "选项:"
echo "  1) 正常启动 (默认)"
echo "  2) 重置缓存启动"
echo "  3) 仅启动 Metro，不运行应用"
read -p "请选择 [1-3]: " -n 1 choice
echo ""
echo ""

case $choice in
    2)
        echo "🔄 重置缓存并启动 Metro..."
        npx react-native start --reset-cache &
        ;;
    3)
        echo "🚀 仅启动 Metro..."
        npx react-native start &
        echo "Metro 已启动，请在新终端中运行: npm run android"
        exit 0
        ;;
    *)
        echo "🚀 正常启动 Metro..."
        npm start &
        ;;
esac

METRO_PID=$!
echo "Metro bundler 进程 ID: $METRO_PID"
echo ""

echo "⏳ 等待 Metro bundler 启动..."
sleep 10
echo ""

# 检查 Metro 是否运行
if ! curl -s http://localhost:8081/status > /dev/null; then
    echo "❌ Metro bundler 启动失败"
    kill $METRO_PID 2>/dev/null || true
    exit 1
fi
echo "✅ Metro bundler 运行正常"
echo ""

# 运行 Android 应用
echo "📱 运行 Android 应用..."
echo "选项:"
echo "  1) 正常构建 (默认)"
echo "  2) 清理后构建"
echo "  3) 仅构建 APK"
read -p "请选择 [1-3]: " -n 1 android_choice
echo ""
echo ""

case $android_choice in
    2)
        echo "🧹 清理并构建..."
        cd android && ./gradlew clean && cd ..
        npm run android
        ;;
    3)
        echo "📦 仅构建 APK..."
        cd android && ./gradlew assembleDebug && cd ..
        echo "✅ APK 构建完成，位置: android/app/build/outputs/apk/debug/"
        ;;
    *)
        echo "🚀 正常构建..."
        npm run android
        ;;
esac

# 清理函数
cleanup() {
    echo ""
    echo "🛑 停止 Metro bundler (PID: $METRO_PID)..."
    kill $METRO_PID 2>/dev/null || true
    echo "✅ 已清理"
}

trap cleanup EXIT

echo ""
echo "=========================================="
echo "  脚本执行完成"
echo "=========================================="