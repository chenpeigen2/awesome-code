# Music Player Electron 启动脚本

# 获取脚本所在目录
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $ScriptDir

Write-Host "======================================" -ForegroundColor Green
Write-Host "    Music Player - Electron App" -ForegroundColor Green
Write-Host "======================================" -ForegroundColor Green
Write-Host ""

# 检查 node_modules 是否存在
if (-not (Test-Path "node_modules")) {
    Write-Host "正在安装依赖..." -ForegroundColor Yellow
    npm install
    if ($LASTEXITCODE -ne 0) {
        Write-Host "依赖安装失败！" -ForegroundColor Red
        Read-Host "按 Enter 键退出"
        exit 1
    }
}

# 编译 Electron 主进程
Write-Host "编译 Electron 主进程..." -ForegroundColor Yellow
npm run build:electron
if ($LASTEXITCODE -ne 0) {
    Write-Host "Electron 主进程编译失败！" -ForegroundColor Red
    Read-Host "按 Enter 键退出"
    exit 1
}

# 检查端口 5173 是否被占用
$portInUse = Get-NetTCPConnection -LocalPort 5173 -ErrorAction SilentlyContinue
$viteProcess = $null

if (-not $portInUse) {
    # 启动 Vite 开发服务器
    Write-Host "启动 Vite 开发服务器..." -ForegroundColor Yellow
    $viteProcess = Start-Process -FilePath "npm" -ArgumentList "run", "dev" -PassThru -WindowStyle Normal
    
    # 使用 wait-on 等待 Vite 服务器就绪
    Write-Host "等待 Vite 服务器启动..." -ForegroundColor Yellow
    npx wait-on http://localhost:5173 -t 30000
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Vite 服务器启动超时！" -ForegroundColor Red
        if ($viteProcess) {
            Stop-Process -Id $viteProcess.Id -Force -ErrorAction SilentlyContinue
        }
        Read-Host "按 Enter 键退出"
        exit 1
    }
} else {
    Write-Host "端口 5173 已被占用，尝试使用现有服务..." -ForegroundColor Yellow
}

# 启动 Electron
Write-Host "启动 Electron 应用..." -ForegroundColor Green
npx electron .

# 清理：关闭 Vite 服务器
if ($viteProcess) {
    Write-Host "关闭 Vite 服务器..." -ForegroundColor Yellow
    Stop-Process -Id $viteProcess.Id -Force -ErrorAction SilentlyContinue
}

Write-Host ""
Write-Host "应用已关闭" -ForegroundColor Green
Read-Host "按 Enter 键退出"
