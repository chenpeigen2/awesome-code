# Capacitor Hello World Android 项目

## 项目说明

这是一个使用 Capacitor 框架创建的 Hello World Android 应用。

## 项目结构

```
hello-capacitor-app/
├── www/                    # Web 资源目录
│   └── index.html         # 主页面
├── android/               # Android 原生项目
├── capacitor.config.json  # Capacitor 配置文件
└── package.json          # Node.js 包配置
```

## 功能特性

1. **响应式设计** - 使用现代 CSS 和渐变背景
2. **平台检测** - 显示当前运行平台（Web/Android）
3. **设备信息** - 显示设备型号、系统版本等信息
4. **交互功能**：
   - 显示弹窗
   - 获取电池信息
   - 设备震动反馈

## 运行项目

### 开发模式（浏览器）
```bash
cd hello-capacitor-app
npm start
```

### Android 构建
```bash
# 同步项目
npx cap sync

# 构建 Android 项目（需要 Android Studio）
npx cap build android
```

### 在 Android Studio 中打开
```bash
npx cap open android
```

## 依赖说明

- `@capacitor/core`: Capacitor 核心库
- `@capacitor/android`: Android 平台支持
- `@capacitor/cli`: Capacitor 命令行工具

## 注意事项

1. 需要 Node.js >= 16.0.0
2. Android 开发需要 Android Studio 和 Android SDK
3. 构建签名需要配置 keystore 文件