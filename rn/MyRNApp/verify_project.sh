#!/bin/bash

set -e

echo "=========================================="
echo "  React Native 项目验证脚本"
echo "=========================================="
echo ""

echo "🔍 检查项目结构..."
if [ -f "package.json" ]; then
    echo "✅ 找到 package.json"
    PROJECT_NAME=$(grep '"name"' package.json | head -1 | awk -F: '{print $2}' | sed 's/[",]//g' | tr -d '[:space:]')
    echo "  项目名称: $PROJECT_NAME"
else
    echo "❌ 未找到 package.json"
    exit 1
fi

echo ""
echo "📦 检查依赖..."
if [ -d "node_modules" ]; then
    echo "✅ node_modules 存在"
else
    echo "⚠️  node_modules 不存在，正在安装..."
    npm install
fi

echo ""
echo "🚀 启动 Metro bundler (测试模式)..."
echo "按 Ctrl+C 停止测试"
echo ""

# 启动 Metro 并检查是否成功
timeout 30s npx react-native start &
METRO_PID=$!

sleep 8

if curl -s http://localhost:8081/status > /dev/null; then
    echo "✅ Metro bundler 启动成功"
    echo "  访问: http://localhost:8081"
    
    # 检查 Metro 状态
    echo ""
    echo "📊 Metro 状态:"
    curl -s http://localhost:8081/status | head -20
    
    kill $METRO_PID 2>/dev/null || true
    echo ""
    echo "✅ 项目验证通过"
    echo ""
    echo "要运行完整应用，请使用以下命令:"
    echo "  1. 启动 Metro: npm start"
    echo "  2. 在另一个终端运行: npm run android"
    echo ""
    echo "或使用脚本: ./run_project.sh"
else
    echo "❌ Metro bundler 启动失败"
    kill $METRO_PID 2>/dev/null || true
    exit 1
fi

echo "=========================================="
echo "  验证完成"
echo "=========================================="