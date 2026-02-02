#!/bin/bash

# React Native MyRNApp 运行脚本
# 使用方法: ./run_final.sh [选项]
# 选项:
#   --metro-only   仅启动 Metro bundler
#   --build-only  仅构建 Android
#   --clean       清理后运行

set -e

METRO_PID=""
CLEAN_MODE=false
METRO_ONLY=false
BUILD_ONLY=false

# 解析参数
for arg in "$@"; do
    case $arg in
        --metro-only)
            METRO_ONLY=true
            ;;
        --build-only)
            BUILD_ONLY=true
            ;;
        --clean)
            CLEAN_MODE=true
            ;;
        --help)
            echo "React Native MyRNApp 运行脚本"
            echo "用法: $0 [选项]"
            echo "选项:"
            echo "  --metro-only   仅启动 Metro bundler"
            echo "  --build-only   仅构建 Android 应用"
            echo "  --clean        清理后运行"
            echo "  --help         显示帮助"
            exit 0
            ;;
    esac
done

cleanup() {
    if [ -n "$METRO_PID" ]; then
        echo "🛑 停止 Metro bundler (PID: $METRO_PID)..."
        kill $METRO_PID 2>/dev/null || true
    fi
}

trap cleanup EXIT

echo "=========================================="
echo "  React Native MyRNApp"
echo "=========================================="
echo ""

# 清理端口
if lsof -ti:8081 > /dev/null 2>&1; then
    echo "🔧 清理端口 8081..."
    lsof -ti:8081 | xargs kill -9
fi

if [ "$BUILD_ONLY" = false ]; then
    # 启动 Metro
    echo "🚀 启动 Metro bundler..."
    if [ "$CLEAN_MODE" = true ]; then
        npx react-native start --reset-cache &
    else
        npm start &
    fi
    
    METRO_PID=$!
    echo "Metro PID: $METRO_PID"
    
    echo "⏳ 等待 Metro 启动..."
    sleep 10
    
    if curl -s http://localhost:8081/status > /dev/null; then
        echo "✅ Metro 运行正常"
    else
        echo "❌ Metro 启动失败"
        exit 1
    fi
fi

if [ "$METRO_ONLY" = true ]; then
    echo ""
    echo "✅ Metro 已启动"
    echo "   访问: http://localhost:8081"
    echo "   按 Ctrl+C 停止"
    wait $METRO_PID
    exit 0
fi

# 构建 Android
echo ""
echo "📱 构建 Android 应用..."
if [ "$CLEAN_MODE" = true ]; then
    echo "🧹 清理构建..."
    cd android && ./gradlew clean && cd ..
fi

npm run android

echo ""
echo "=========================================="
echo "  完成!"
echo "=========================================="
echo ""
echo "如果构建失败，请检查:"
echo "  1. Android SDK 是否安装"
echo "  2. 模拟器或设备是否连接"
echo "  3. 运行: npx react-native doctor"
echo ""
echo "常用命令:"
echo "  npm start             启动 Metro"
echo "  npm run android       运行 Android 应用"
echo "  npx react-native doctor 检查环境"