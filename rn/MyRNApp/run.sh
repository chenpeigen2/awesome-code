#!/bin/bash

set -e

echo "=========================================="
echo "  React Native MyRNApp 启动脚本"
echo "=========================================="
echo ""

# 检查端口占用
if lsof -ti:8081 > /dev/null 2>&1; then
    echo "⚠️  端口 8081 被占用，正在清理..."
    lsof -ti:8081 | xargs kill -9
    echo "✅ 端口已清理"
    echo ""
fi

if [ ! -d "node_modules" ]; then
    echo "⚠️  node_modules 不存在，正在安装依赖..."
    npm install
    echo ""
fi

echo "📦 启动 Metro bundler..."
npm start &
METRO_PID=$!
echo "Metro bundler 进程 ID: $METRO_PID"
echo ""

echo "⏳ 等待 Metro bundler 启动..."
sleep 8
echo ""

echo "📱 运行 Android 应用..."
echo "注意：确保已安装 Android SDK 和配置好模拟器/设备"
npm run android

trap "echo ''; echo '🛑 停止 Metro bundler (PID: $METRO_PID)...'; kill $METRO_PID 2>/dev/null || true; echo '✅ 已清理'" EXIT
