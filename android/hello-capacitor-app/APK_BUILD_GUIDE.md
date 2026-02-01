# Android APK 构建指南

## 📱 项目状态

✅ **已完成**：
- Capacitor 项目结构创建
- Android 平台集成
- 调试签名密钥配置
- HTML/JavaScript 应用开发

⚠️ **构建问题**：
当前系统使用 Java 25，但 Gradle 构建工具不支持此版本

## 🔧 解决方案

### 方案一：降级 Java 版本（推荐）

```bash
# 安装 Java 17 (LTS)
sudo apt update
sudo apt install openjdk-17-jdk

# 设置 JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# 验证版本
java -version  # 应显示 17.x.x
```

然后重新构建：
```bash
cd hello-capacitor-app
npx cap build android --keystorepath debug.keystore --keystorepass android --keystorealias androiddebugkey --keystorealiaspass android
```

### 方案二：使用 Android Studio

1. 打开项目：
```bash
cd hello-capacitor-app
npx cap open android
```

2. 在 Android Studio 中：
   - 选择 "Build" → "Build Bundle(s) / APK(s)" → "Build APK(s)"
   - Android Studio 会自动处理 Java 版本兼容性

### 方案三：使用 Docker 构建环境

创建 Dockerfile 进行构建（适用于 CI/CD）：

```dockerfile
FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y nodejs npm
# 复制项目文件并构建
```

## 📁 项目文件结构

```
hello-capacitor-app/
├── www/                    # Web 应用源码
│   └── index.html         # 主页面
├── android/               # Android 原生项目
├── debug.keystore         # 调试签名密钥
├── capacitor.config.json  # Capacitor 配置
└── package.json          # Node.js 依赖
```

## 🎯 构建成功后的 APK 位置

构建成功后，APK 文件将位于：
```
hello-capacitor-app/android/app/build/outputs/apk/debug/app-debug.apk
```

## 📱 安装测试

将 APK 文件传输到 Android 设备后，启用"未知来源"安装：
```bash
adb install app-debug.apk
```

## 🔍 故障排除

常见问题：
1. **Java 版本不兼容** - 使用 Java 11-17
2. **Android SDK 缺失** - 确保安装了 build-tools 和 platform
3. **签名问题** - 检查 keystore 文件和密码

## 🚀 下一步

构建成功后，你可以：
1. 在真实设备上测试应用
2. 添加更多 Capacitor 插件功能
3. 配置正式签名用于发布
4. 优化应用性能和用户体验