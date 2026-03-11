# Task Plan: 跨平台音乐播放器 (Harmony Player)

## Goal
使用 Compose Multiplatform 构建一个功能完善的跨平台桌面音乐播放器，支持 Linux、macOS、Windows 三大平台，具备本地音乐管理、在线音乐、歌词显示、均衡器等核心功能。

## Current Phase
Phase 4

## Phases

### Phase 1: 需求分析与技术调研
- [x] 确认核心功能需求
- [x] 确认 UI 风格 (Material 3)
- [x] 确认音频后端 (mpv)
- [x] 调研 Compose Multiplatform 桌面开发
- [x] 调研 mpv 与 Kotlin/JVM 集成方案
- [x] 确定项目目录结构
- **Status:** complete

### Phase 2: 项目架构设计
- [x] 设计模块划分 (core, feature, platform)
- [x] 确定数据层架构 (Repository, DataSource)
- [x] 设计 UI 组件层级
- [x] 确定依赖注入方案
- [x] 设计跨平台抽象层 (expect/actual)
- [x] 设计数据库 Schema (SQLDelight)
- [x] 设计核心接口 (Player, Repository)
- **Status:** complete

### Phase 3: 基础设施搭建
- [x] 创建 Kotlin Multiplatform 项目
- [x] 配置 Gradle 多平台构建
- [x] 集成 Compose Desktop
- [x] 配置 SQLDelight 数据库
- [x] 集成 Koin 依赖注入
- [x] 配置 mpv 音频后端
- **Status:** complete

### Phase 4: 核心播放功能
- [x] 实现音频播放引擎 (mpv 封装)
- [x] 实现播放状态管理 (PlayState)
- [x] 实现播放队列管理
- [x] 实现基础播放控件 UI
- [x] 实现进度条与时间显示
- [x] 实现音量控制
- **Status:** complete

### Phase 5: 音乐库管理
- [x] 实现本地文件扫描
- [x] 实现音乐元数据解析 (ID3v2, Vorbis)
- [ ] 实现播放列表 CRUD
- [x] 实现封面图片提取与缓存
- [ ] 实现标签编辑功能
- [x] 实现搜索与过滤
- **Status:** in_progress

### Phase 6: 高级功能
- [x] 实现歌词解析与同步显示 (LRC)
- [x] 实现音频均衡器 (10段)
- [x] 实现预设音效
- [ ] 实现在线音乐 API 对接
- [ ] 实现桌面系统集成 (系统托盘、媒体键)
- **Status:** in_progress

### Phase 7: UI 精细化
- [ ] 实现 Material 3 主题系统
- [ ] 实现深色/浅色模式
- [ ] 实现响应式布局
- [ ] 实现动画效果
- [ ] 实现窗口状态持久化
- **Status:** pending

### Phase 8: 打包与分发
- [ ] 配置 macOS .dmg 打包
- [ ] 配置 Windows .msi 打包
- [ ] 配置 Linux .deb/.rpm 打包
- [ ] 测试跨平台兼容性
- [ ] 编写用户文档
- **Status:** pending

## Key Questions
1. mpv 库如何通过 JNI 与 Kotlin/JVM 集成？→ 使用 JNA 或自定义 JNI 绑定
2. 如何处理各平台的音频输出差异？→ mpv 统一处理，使用 libmpv
3. 在线音乐使用哪个 API？→ 待定，可考虑网易云音乐 API (非官方)
4. 如何处理大音乐库的性能问题？→ 分页加载、异步扫描、索引优化
5. 歌词如何同步？→ LRC 时间戳解析 + 播放进度监听

## Decisions Made
| Decision | Rationale |
|----------|-----------|
| Compose Multiplatform | 声明式 UI，跨平台代码共享，与 Kotlin 生态无缝集成 |
| mpv 音频后端 | 高性能、格式支持广泛、跨平台一致性好 |
| SQLDelight | 类型安全的 SQL，跨平台支持，编译时验证 |
| Koin DI | 轻量级、Kotlin 友好、运行时无代码生成 |
| Material 3 | 现代化设计、动态配色、用户熟悉 |
| MVVM + Clean Architecture | 关注点分离、可测试性、可维护性 |
| 项目位置: androidx/harmony-player | 作为 AndroidX 仓库的新模块 |
| 网易云音乐 API | 曲库丰富，社区有成熟的非官方 API 封装 |

## Errors Encountered
| Error | Attempt | Resolution |
|-------|---------|------------|
| (暂无) | - | - |

## Notes
- 项目名称暂定: Harmony Player
- 目标平台: Linux (x64), macOS (x64/ARM64), Windows (x64)
- 最低系统版本: macOS 11+, Windows 10+, Ubuntu 20.04+
- 使用 Coroutines + Flow 处理异步操作
