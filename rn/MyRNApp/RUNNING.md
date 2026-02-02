# MyRNApp 运行说明

这是一个基于 React Native 的移动应用程序项目。

## 环境要求

在运行此项目之前，请确保您的开发环境满足以下要求：

### 基础环境
- Node.js (版本 >= 16)
- npm 或 yarn
- Java Development Kit (JDK)

### Android 开发环境（必须）
- Android Studio
- Android SDK
- ANDROID_HOME 环境变量设置

## 环境检查

您可以使用以下命令检查环境配置：
```bash
npx react-native doctor
```

根据检查结果，您可能需要修复以下问题：
- 安装 Android Studio
- 下载并配置 Android SDK
- 设置 ANDROID_HOME 环境变量

## 运行项目

### 方法一：使用 npm

#### 1. 启动 Metro 服务器
```bash
# 在项目根目录下运行
npm start
# 或者指定端口（如果默认端口被占用）
npm start -- --port=8082
```

#### 2. 运行 Android 应用
```bash
npm run android
```

### 方法二：使用 React Native CLI

#### 1. 启动 Metro 服务器
```bash
npx react-native start
```

#### 2. 运行 Android 应用
```bash
npx react-native run-android
```

### 方法三：使用 yarn（如果已安装）
```bash
# 启动 Metro 服务器
yarn start

# 运行 Android 应用
yarn android
```

## 常见问题及解决方案

### 端口占用问题
如果遇到 `EADDRINUSE: address already in use :::8081` 错误：
```bash
# 使用不同端口启动
npm start -- --port=8082
```

### Android 构建失败
如果遇到构建失败，请确保：
1. 已正确安装 Android Studio
2. 已下载合适的 Android SDK 版本
3. 已正确设置 ANDROID_HOME 环境变量

### 环境变量设置
在 ~/.bashrc 或 ~/.zshrc 中添加：
```bash
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/tools/bin
export PATH=$PATH:$ANDROID_HOME/platform-tools
```

然后重新加载配置：
```bash
source ~/.bashrc  # 或 source ~/.zshrc
```

## 项目结构
```
MyRNApp/
├── android/              # Android 原生代码
├── ios/                  # iOS 原生代码
├── App.tsx               # 主应用组件
├── app.json              # 应用配置
├── index.js              # 应用入口点
├── package.json          # 项目依赖和脚本
├── metro.config.js       # Metro 打包配置
└── tsconfig.json         # TypeScript 配置
```

## 开发模式

### 实时重载
- Android: 在模拟器中按 R 键两次或从开发菜单中选择 "Reload"
- iOS: 在模拟器中按 Cmd+R

### 开发者菜单
- Android: 按 Ctrl+M (Windows/Linux) 或 Cmd+M (macOS)
- iOS: 按 Cmd+D (模拟器中)

## 调试

### 启用远程调试
在开发者菜单中选择 "Debug" 或 "Debug Remote JS"，这将在浏览器中打开调试界面。

### 日志输出
```bash
# 查看 Android 设备日志
adb logcat *:S ReactNative:V ReactNativeJS:V
```

## 测试

运行单元测试：
```bash
npm test
# 或
npm run test
```

## 代码质量

- 运行 ESLint 检查：
```bash
npm run lint
```

## 故障排除

如果遇到问题，请尝试以下步骤：

1. 清理缓存：
```bash
npx react-native start --reset-cache
```

2. 清理所有缓存和重新安装依赖：
```bash
rm -rf node_modules
npm install
npx react-native start --reset-cache
```

3. 检查设备连接：
```bash
adb devices
```

4. 确保模拟器或物理设备已正确连接并启用 USB 调试。