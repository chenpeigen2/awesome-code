# awesome-code 项目综述

## 项目概述

**awesome-code** 是一个综合性多语言技术学习与实践代码仓库，涵盖了主流编程语言和框架的示例项目、算法练习、框架探索以及完整的桌面/移动应用程序。该仓库旨在作为技术学习、框架探索和最佳实践的参考资源。

---

## 技术栈概览

| 技术领域 | 主要技术 |
|---------|---------|
| **JVM 生态** | Java 25, Kotlin 2.3.0, Gradle 9.3.1 |
| **前端/桌面** | Electron, React, TypeScript, JavaFX |
| **移动端** | Android, Flutter, React Native, Capacitor |
| **Go** | Fyne GUI, 算法实现 |
| **Python** | PyTorch, PyQt6, pandas, matplotlib |
| **其他语言** | Rust, C++, Lua, Zig, Erlang, Groovy, Shell |

---

## 项目结构

```
awesome-code/
├── kotlin/                  # Kotlin 基础与协程示例
├── python/                  # Python 学习 (PyTorch, pandas, matplotlib)
├── go/                      # Go 应用 (Fyne GUI)
├── rust/                    # Rust 实验
├── zig/                     # Zig 示例
├── lua/                     # Lua 脚本
├── erl/                     # Erlang 实验
├── c++/                     # C++ 参考
├── flutter/                 # Flutter 移动应用
├── rn/                      # React Native 应用
├── js/                      # JavaScript 实验
├── shell/                   # Shell 脚本
│
├── music-player/            # Electron + React 音乐播放器
├── javafx-music-player/     # JavaFX 音乐播放器
├── py-imageviewer/          # PyQt6 图像查看器
├── electron-music-player/   # Electron 音乐播放器
│
├── android/                 # Android 原生项目
│   ├── androidx/            # AndroidX 组件示例
│   ├── CrossProcessRenderDemo/  # 跨进程渲染
│   ├── RemoteViewsDemo/     # RemoteViews 示例
│   └── hello-capacitor-app/ # Capacitor 混合应用
│
├── vertx/                   # Vert.x 生态
│   ├── vertx-grpc/          # gRPC 服务
│   ├── vertx-web/           # Web 应用
│   ├── vertx-config/        # 配置管理
│   └── service-discovery/   # 服务发现
│
├── grpc/                    # gRPC 示例
├── dagger/                  # Dagger 依赖注入
├── database/                # 数据库
│   ├── mongodb/
│   ├── postgres/
│   └── couchdb/
│
├── explore/                 # 框架探索
│   ├── ai/                  # AI SDK
│   ├── bun/                 # Bun 运行时
│   ├── disruptor/           # LMAX Disruptor
│   ├── Mutiny/              # 响应式编程
│   ├── calcite-tutorial/    # Apache Calcite
│   ├── jsprit/              # 车辆路径规划
│   ├── Kryo/                # 序列化
│   ├── snappy/              # 压缩
│   ├── javaParser/          # Java 解析器
│   ├── javassist/           # 字节码操作
│   ├── graphQL/             # GraphQL
│   └── ...                  # 更多框架
│
├── asm/                     # ASM 字节码操作
├── auto/                    # AutoValue
├── spi/                     # SPI 服务提供者
├── compile/                 # 编译时注解处理
├── rxjava3/                 # RxJava3 响应式
├── json/                    # JSON 处理 (Gson)
├── http/                    # HTTP 客户端 (OkHttp)
├── jwt/                     # JWT 认证
├── io/                      # IO 操作 (Okio)
├── nacos/                   # Nacos 服务发现
├── web/                     # WebFlux
├── patterns/                # 设计模式
└── claude/                  # Claude API 示例
```

---

## 核心项目详解

### 1. 桌面应用

#### music-player (Electron + React)
跨平台桌面音乐播放器，功能包括：
- 本地音乐播放 (mp3, flac, wav, aac, ogg, ape)
- 在线音乐搜索与下载
- 歌词窗口 (始终置顶)
- Zustand 状态管理 + 持久化

#### py-imageviewer (PyQt6)
功能丰富的图像查看器：
- 本地与在线图片浏览
- 异步支持 (aiohttp)
- PyQt6 GUI

#### javafx-music-player
JavaFX 技术栈的音乐播放器

### 2. Android 项目

#### androidx/ (30+ 子项目)
AndroidX 组件示例集合：
- **lifecycle-demo**: 生命周期管理
- **recyclerview-demo**: 列表视图
- **constraintlayout-demo**: 约束布局
- **workmanager-demo**: 后台任务
- **datastore-demo**: 数据存储
- **window-demo**: 多窗口支持
- **autodensity-demo**: 自适应密度
- **apt-annotation/compiler**: 注解处理器
- **aidl_server/client**: AIDL 跨进程通信

#### CrossProcessRenderDemo
跨进程渲染演示

#### RemoteViewsDemo
RemoteViews 远程视图示例

#### hello-capacitor-app
Capacitor 混合应用示例

### 3. Vert.x 生态

- **vertx-grpc**: gRPC 服务端/客户端
- **vertx-web**: Web 应用开发
- **vertx-config**: 配置管理
- **service-discovery**: 服务发现模式

### 4. 框架探索 (explore/)

| 项目 | 描述 |
|------|------|
| disruptor | LMAX Disruptor 高性能队列 |
| Mutiny | SmallRye 响应式编程 |
| calcite-tutorial | Apache Calcite SQL 框架 |
| jsprit | 车辆路径规划 (VRP) |
| Kryo | 高性能序列化 |
| snappy | 快速压缩 |
| javaParser | Java 源码解析 |
| javassist | 字节码操作 |
| graphQL | GraphQL 查询语言 |
| velocity | 模板引擎 |
| jsqlparser | SQL 解析器 |
| reactivestreams | 响应式流 |
| ai | AI SDK 实验 |

### 5. 移动端开发

#### Flutter
- **flutter_rust_demo**: Flutter + Rust 混合开发
- **flutter_widgets_gallery**: Flutter 组件展示

#### React Native
- **MyRNApp**: React Native 示例应用

### 6. Go 项目

- **Fyne GUI 应用**: 跨平台桌面应用框架
- **算法实现**: LeetCode 解决方案

---

## 技术特性

### 构建系统
- **Gradle**: Java/Kotlin 项目统一构建
- **Version Catalog**: 集中式依赖版本管理
- **镜像配置**: Aliyun/Tencent 镜像加速

### 代码规范
- **Go**: go fmt, PascalCase/camelCase 命名
- **Java**: PascalCase 类名, camelCase 方法名
- **Kotlin**: 表达式体, val 优先, 协程
- **Python**: snake_case 函数, Ruff linting
- **TypeScript**: 严格模式, const 优先

### 测试框架
- **Java/Kotlin**: JUnit 5 + Mockito
- **Go**: testify + 表驱动测试
- **Python**: pytest + pytest-asyncio
- **TypeScript**: Vitest + Testing Library

---

## 依赖版本

| 依赖 | 版本 |
|------|------|
| Java | 25 |
| Kotlin | 2.3.0 |
| Gradle | 9.3.1 |
| JUnit | 5.14.2 |
| Guava | 33.5.0-jre |
| OkHttp | 5.3.0 |
| Vert.x | 4.5.24 |
| gRPC | 1.78.0 |
| Dagger | 2.59 |

---

## 开发指南

### 快速开始

```bash
# Java/Kotlin 项目
./gradlew build

# Go 项目
go run ./path/to/main.go

# Python 项目
pip install -e .

# TypeScript/Bun
bun install && bun run index.ts

# Electron 应用
npm run electron:dev
```

### 常用命令

```bash
# 运行特定测试
./gradlew test --tests ClassName.methodName

# 构建特定子项目
./gradlew :grpc:build

# Go 测试
go test ./...

# Python 测试
pytest -v
```

---

## 项目特色

1. **多语言覆盖**: 涵盖 10+ 编程语言，适合对比学习
2. **完整应用**: 包含可运行的桌面/移动端完整应用
3. **框架深度**: 深入探索主流框架的核心特性
4. **最佳实践**: 遵循各语言的代码规范和设计模式
5. **学习导向**: 注释详尽，适合技术学习参考

---

## 许可证

本仓库包含多个项目，各项目可能有不同的许可证。请查看各项目目录了解具体许可信息。

---

*文档生成日期: 2026-03-07*
