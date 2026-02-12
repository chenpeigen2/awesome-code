@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

REM Music Player Electron 启动脚本

REM 获取脚本所在目录
cd /d "%~dp0"

echo ======================================
echo     Music Player - Electron App
echo ======================================
echo.

REM 检查 node_modules 是否存在
if not exist "node_modules" (
    echo 正在安装依赖...
    call npm install
    if errorlevel 1 (
        echo 依赖安装失败！
        pause
        exit /b 1
    )
)

REM 编译 Electron 主进程
echo 编译 Electron 主进程...
call npm run build:electron
if errorlevel 1 (
    echo Electron 主进程编译失败！
    pause
    exit /b 1
)

REM 检查端口 5173 是否被占用
netstat -ano | findstr ":5173" >nul
if errorlevel 1 (
    REM 启动 Vite 开发服务器
    echo 启动 Vite 开发服务器...
    start "Vite Dev Server" cmd /c "npm run dev"
    
    REM 使用 wait-on 等待 Vite 服务器就绪
    echo 等待 Vite 服务器启动...
    call npx wait-on http://localhost:5173 -t 30000
    if errorlevel 1 (
        echo Vite 服务器启动超时！
        pause
        exit /b 1
    )
) else (
    echo 端口 5173 已被占用，尝试使用现有服务...
)

REM 启动 Electron
echo 启动 Electron 应用...
call npx electron .

echo.
echo 应用已关闭

REM 提示用户关闭 Vite 服务器
echo 请手动关闭 Vite Dev Server 窗口（如果已打开）
pause
