# Task Plan: Hilt 依赖注入学习 Demo

## Goal
创建一个渐进式学习 Hilt 依赖注入的 Android Demo 项目，通过 5 个独立模块全面展示 Hilt 的核心功能和高级特性。

## Current Phase
Phase 0: 准备阶段

## Phases

### Phase 1: 项目基础设施搭建
- [ ] 创建 hilt-demo 项目目录结构
- [ ] 配置根项目 build.gradle.kts
- [ ] 配置 settings.gradle.kts 多模块
- [ ] 创建 gradle/libs.versions.toml 版本目录
- [ ] 配置 Hilt 插件和 KSP
- [ ] 创建 DemoApplication (@HiltAndroidApp)
- [ ] 创建 MainActivity (模块选择入口)
- **Status:** pending

### Phase 2: 基础注入模块 (feature-basic)
- [ ] 创建 feature-basic 模块
- [ ] 实现 DatabaseService (@Inject 构造器注入)
- [ ] 实现 NetworkService (@Inject 构造器注入)
- [ ] 实现 AnalyticsService (接口注入)
- [ ] 实现 BasicModule (@Module @Provides)
- [ ] 实现 BasicActivity (展示注入结果)
- [ ] 添加单例验证功能
- **Status:** pending

### Phase 3: ViewModel 注入模块 (feature-viewmodel)
- [ ] 创建 feature-viewmodel 模块
- [ ] 实现 UserRepository 接口
- [ ] 实现 UserRepositoryImpl
- [ ] 实现 UserModule (@Binds)
- [ ] 实现 UserViewModel (@HiltViewModel)
- [ ] 实现 ViewModelActivity
- [ ] 集成 SavedStateHandle
- **Status:** pending

### Phase 4: 作用域管理模块 (feature-scope)
- [ ] 创建 feature-scope 模块
- [ ] 实现 SingletonService (@Singleton)
- [ ] 实现 ActivityScopedService (@ActivityScoped)
- [ ] 实现 ViewModelScopedService (@ViewModelScoped)
- [ ] 实现 ScopeModule
- [ ] 实现 ScopeActivity (作用域对比演示)
- [ ] 添加作用域可视化表格
- **Status:** pending

### Phase 5: 高级特性模块 (feature-advanced)
- [ ] 创建 feature-advanced 模块
- [ ] 实现 @EntryPoint 示例
- [ ] 实现自定义 @Qualifier 限定符
- [ ] 实现 @Named 命名注入
- [ ] 实现 AdvancedModule
- [ ] 实现 AdvancedActivity
- [ ] 添加同一接口多实现演示
- **Status:** pending

### Phase 6: 测试支持模块 (feature-test)
- [ ] 配置 Hilt 测试依赖
- [ ] 创建 HiltTestRunner
- [ ] 实现 TestModule (替换生产模块)
- [ ] 实现 ViewModelTest 单元测试
- [ ] 实现 @UninstallModules 示例
- [ ] 实现 Fake/Mock 依赖替换
- **Status:** pending

## Key Questions
1. 如何验证 Hilt 版本与 Kotlin 版本兼容？→ 查看 Hilt 官方发布说明
2. Compose 中如何使用 HiltViewModel？→ 使用 hiltViewModel() 函数
3. 如何在非 Hilt 类中获取依赖？→ 使用 @EntryPoint
4. 测试时如何替换生产依赖？→ @UninstallModules + @TestInstallIn

## Decisions Made
| Decision | Rationale |
|----------|-----------|
| 渐进式学习 Demo | 5 个独立模块，每个专注一组概念 |
| Kotlin 2.0.21 | 与项目其他模块保持一致 |
| Hilt 2.52 | 最新稳定版，支持 KSP |
| Compose UI | 现代 Android UI 工具包 |
| minSdk 33 | 与项目其他模块保持一致 |

## Errors Encountered
| Error | Attempt | Resolution |
|-------|---------|------------|
| (暂无) | - | - |

## Notes
- 设计文档: docs/plans/2026-03-11-hilt-demo-design.md
- 项目位置: androidx/hilt-demo
- 技术栈: Kotlin, Hilt, ViewModel, Compose
