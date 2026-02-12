#!/bin/bash

# Music Player Electron 启动脚本 (Linux/macOS)

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}======================================${NC}"
echo -e "${GREEN}    Music Player - Electron App${NC}"
echo -e "${GREEN}======================================${NC}"

# 检查 node_modules 是否存在
if [ ! -d "node_modules" ]; then
    echo -e "${YELLOW}正在安装依赖...${NC}"
    npm install
    if [ $? -ne 0 ]; then
        echo -e "${RED}依赖安装失败！${NC}"
        exit 1
    fi
fi

# 编译 Electron 主进程
echo -e "${YELLOW}编译 Electron 主进程...${NC}"
npm run build:electron
if [ $? -ne 0 ]; then
    echo -e "${RED}Electron 主进程编译失败！${NC}"
    exit 1
fi

# 检查是否已有 Vite 服务运行
if lsof -i :5173 > /dev/null 2>&1; then
    echo -e "${YELLOW}端口 5173 已被占用，尝试使用现有服务...${NC}"
else
    # 启动 Vite 开发服务器（后台运行）
    echo -e "${YELLOW}启动 Vite 开发服务器...${NC}"
    npm run dev &
    VITE_PID=$!
    
    # 等待 Vite 服务器启动
    echo -e "${YELLOW}等待 Vite 服务器启动...${NC}"
    sleep 3
    
    # 检查 Vite 是否启动成功
    if ! kill -0 $VITE_PID 2>/dev/null; then
        echo -e "${RED}Vite 服务器启动失败！${NC}"
        exit 1
    fi
fi

# 启动 Electron
echo -e "${GREEN}启动 Electron 应用...${NC}"
npx electron .

# 清理：当 Electron 关闭时，也关闭 Vite 服务器
if [ ! -z "$VITE_PID" ]; then
    echo -e "${YELLOW}关闭 Vite 服务器...${NC}"
    kill $VITE_PID 2>/dev/null
fi

echo -e "${GREEN}应用已关闭${NC}"
