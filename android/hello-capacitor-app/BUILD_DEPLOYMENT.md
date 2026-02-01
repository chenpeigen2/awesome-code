# Android Capacitor 项目构建和部署完整指南

## 🏗️ 项目构建流程

### 1. 开发环境准备

#### 系统要求
```
操作系统: Ubuntu 20.04+ / macOS 10.15+ / Windows 10+
JDK 版本: OpenJDK 11-17 (推荐 17)
Node.js: 16.0+ (推荐 18.x LTS)
Android SDK: API 21+ (推荐 30+)
```

#### 环境变量配置
```bash
# JDK 配置
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Android SDK 配置
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools:$PATH
```

### 2. 项目初始化流程

#### 创建项目步骤
```bash
# 1. 创建基础项目结构
mkdir hello-capacitor-app
cd hello-capacitor-app

# 2. 初始化 Node.js 项目
npm init -y

# 3. 安装 Capacitor 依赖
npm install @capacitor/core @capacitor/android
npm install -D @capacitor/cli

# 4. 初始化 Capacitor 配置
npx cap init
```

#### 目录结构说明
```
project-root/
├── www/                    # Web 资源目录
├── android/                # Android 原生项目
├── node_modules/           # Node.js 依赖
├── capacitor.config.json   # Capacitor 配置文件
├── package.json            # Node.js 包配置
└── README.md              # 项目说明
```

### 3. Web 资源开发

#### HTML 页面开发
```html
<!-- www/index.html -->
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Hello Capacitor</title>
    <script src="@capacitor/core"></script>
</head>
<body>
    <!-- 应用内容 -->
    <script>
        // 应用逻辑
        document.addEventListener('deviceready', function() {
            console.log('应用准备就绪');
        });
    </script>
</body>
</html>
```

#### 开发模式测试
```bash
# 启动本地开发服务器
npm start
# 或
npx serve www
```

### 4. Android 平台集成

#### 添加 Android 平台
```bash
# 添加 Android 支持
npx cap add android

# 验证平台添加
npx cap ls
```

#### Capacitor 配置文件
```json
{
  "appId": "com.example.hello",
  "appName": "HelloCapacitor",
  "webDir": "www",
  "bundledWebRuntime": false,
  "android": {
    "path": "android"
  }
}
```

### 5. 资源同步流程

#### 手动同步
```bash
# 同步 Web 资源到原生项目
npx cap sync

# 仅同步 Android 平台
npx cap sync android
```

#### 自动同步机制
```
Web 资源变更
    ↓
执行 npx cap sync
    ↓
复制 www/ → android/app/src/main/assets/public/
    ↓
更新配置文件
    ↓
同步插件依赖
    ↓
准备构建
```

## 📦 APK 构建详细流程

### 1. 构建前准备

#### 清理环境
```bash
# 清理 Gradle 缓存
cd android
./gradlew clean

# 清理构建输出
rm -rf app/build/
```

#### 检查环境配置
```bash
# 验证 Java 版本
java -version

# 验证 Gradle 版本
./gradlew --version

# 检查 Android SDK
echo $ANDROID_HOME
```

### 2. Debug 版本构建

#### 基本构建命令
```bash
# 进入 Android 目录
cd android

# 构建 Debug APK
./gradlew assembleDebug

# 构建并安装到连接设备
./gradlew installDebug
```

#### 构建过程详解
```
1. 项目配置加载
2. 依赖解析和下载
3. 源代码编译
4. 资源处理和打包
5. APK 签名（调试密钥）
6. 输出 APK 文件
```

#### 构建输出位置
```
android/app/build/outputs/apk/debug/app-debug.apk
```

### 3. Release 版本构建

#### 签名密钥配置
```bash
# 生成发布密钥
keytool -genkey -v -keystore release.keystore \
  -alias release-key \
  -keyalg RSA -keysize 2048 -validity 10000

# 配置签名信息
```

#### 构建配置文件
```json
{
  "android": {
    "buildOptions": {
      "keystorePath": "release.keystore",
      "keystorePassword": "your-password",
      "keystoreAlias": "release-key",
      "keystoreAliasPassword": "alias-password"
    }
  }
}
```

#### Release 构建命令
```bash
# 构建 Release APK
npx cap build android

# 或使用 Gradle
cd android
./gradlew assembleRelease
```

### 4. 构建优化选项

#### 性能优化配置
```gradle
// android/app/build.gradle
android {
    buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            shrinkResources true
        }
    }
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
}
```

#### 代码分割配置
```javascript
// webpack.config.js 或类似配置
module.exports = {
    optimization: {
        splitChunks: {
            chunks: 'all',
            cacheGroups: {
                vendor: {
                    test: /[\\/]node_modules[\\/]/,
                    name: 'vendors',
                    chunks: 'all',
                }
            }
        }
    }
};
```

## 🚀 部署和发布流程

### 1. 本地测试部署

#### ADB 安装测试
```bash
# 连接设备检查
adb devices

# 安装 APK
adb install app-debug.apk

# 卸载应用
adb uninstall com.example.hello
```

#### 设备调试
```bash
# 启用调试模式
adb shell am start -D com.example.hello/.MainActivity

# 查看日志
adb logcat | grep capacitor

# Chrome 远程调试
chrome://inspect/#devices
```

### 2. 应用商店发布

#### Google Play 发布准备
```bash
# 生成发布版本
npx cap build android --release

# 检查 APK 信息
aapt dump badging app-release.apk

# 签名验证
jarsigner -verify -verbose -certs app-release.apk
```

#### 发布清单要求
```
应用图标: 512x512 PNG
截图: 手机和平板截图
应用描述: 详细功能说明
隐私政策: 必需链接
```

### 3. 持续集成配置

#### GitHub Actions 示例
```yaml
name: Build Android APK
on: [push]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - name: Setup Node.js
      uses: actions/setup-node@v3
      with:
        node-version: '18'
    - name: Setup Java
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    - name: Install dependencies
      run: npm install
    - name: Build APK
      run: |
        npx cap sync
        cd android
        ./gradlew assembleRelease
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-release
        path: android/app/build/outputs/apk/release/app-release.apk
```

## 🔧 故障排除和优化

### 1. 常见构建问题

#### Gradle 构建失败
```bash
# 清理 Gradle 缓存
rm -rf ~/.gradle/caches/

# 重新下载依赖
./gradlew --refresh-dependencies

# 增加内存限制
export GRADLE_OPTS="-Xmx4096m -XX:MaxPermSize=512m"
```

#### 签名问题解决
```bash
# 检查密钥库
keytool -list -keystore debug.keystore

# 重新生成调试密钥
rm debug.keystore
keytool -genkey -v -keystore debug.keystore -storepass android -alias androiddebugkey -keypass android -keyalg RSA -keysize 2048 -validity 10000
```

### 2. 性能优化建议

#### 构建性能优化
```
启用 Gradle Daemon: 提升构建速度
并行构建: ./gradlew assembleDebug --parallel
增量构建: 利用 Gradle 增量编译
构建缓存: 启用构建缓存机制
```

#### APK 大小优化
```
资源压缩: 启用资源压缩
代码混淆: ProGuard 优化
无用代码移除: R8 优化
图片优化: WebP 格式转换
```

### 3. 调试和监控

#### 构建日志分析
```bash
# 详细构建日志
./gradlew assembleDebug --info

# 堆栈跟踪
./gradlew assembleDebug --stacktrace

# 调试模式
./gradlew assembleDebug --debug
```

#### 性能监控
```bash
# 构建时间分析
./gradlew assembleDebug --profile

# 依赖分析
./gradlew app:dependencies

# 方法数统计
./gradlew app:countReleaseMethods
```

## 📊 版本管理和发布策略

### 1. 版本控制流程

#### 语义化版本管理
```
主版本号.次版本号.修订号
1.0.0 → 1.0.1 (bug 修复)
1.0.1 → 1.1.0 (功能新增)
1.1.0 → 2.0.0 (重大更新)
```

#### 版本配置
```json
// package.json
{
  "version": "1.0.0"
}

// android/app/build.gradle
android {
    defaultConfig {
        versionCode 1
        versionName "1.0.0"
    }
}
```

### 2. 发布分支策略

#### Git 分支模型
```
main/master     → 生产环境版本
develop         → 开发版本
feature/*       → 功能开发分支
release/*       → 发布准备分支
hotfix/*        → 紧急修复分支
```

#### 发布流程
```bash
# 创建发布分支
git checkout -b release/1.0.0 develop

# 版本号更新
# 构建测试
# 合并到主分支
git checkout main
git merge release/1.0.0
git tag -a v1.0.0 -m "Release version 1.0.0"

# 合并回 develop
git checkout develop
git merge release/1.0.0
```

这个完整的构建和部署指南涵盖了从项目创建到应用发布的全过程，帮助开发者建立标准化的开发和发布流程。