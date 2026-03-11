# Task Plan: Dagger2 依赖注入学习 Demo

## Goal
创建一个渐进式学习 Dagger2 依赖注入的 Android Demo 项目，通过多个功能模块全面展示 Dagger2 的核心概念和高级特性。

## Current Phase
Phase 1: 项目基础设施搭建

## Phases

### Phase 1: 项目基础设施搭建
- [ ] 创建 dagger-demo 项目目录结构
- [ ] 配置 build.gradle.kts (kapt/ksp)
- [ ] 配置 settings.gradle.kts
- [ ] 创建 gradle/libs.versions.toml 版本目录
- [ ] 创建 DemoApplication (手动初始化 Dagger)
- [ ] 创建 MainActivity (模块选择入口)
- [ ] 创建 AppComponent (应用级组件)
- **Status:** pending

### Phase 2: 基础注入模块 (@Inject, @Module, @Component)
- [ ] 创建 feature-basic 模块
- [ ] 实现 CoffeeShop 经典示例
- [ ] 演示 @Inject 构造器注入
- [ ] 演示 @Module + @Provides
- [ ] 演示 @Component 接口
- [ ] 实现 BasicActivity (展示注入过程)
- **Status:** pending

### Phase 3: 作用域管理 (@Singleton, @Scope)
- [ ] 创建 feature-scope 模块
- [ ] 实现自定义 @ActivityScope
- [ ] 演示 @Singleton 单例
- [ ] 演示作用域绑定
- [ ] 实现作用域对比演示
- **Status:** pending

### Phase 4: 高级特性 (@Qualifier, @Named, Subcomponent)
- [ ] 创建 feature-advanced 模块
- [ ] 演示 @Qualifier 自定义限定符
- [ ] 演示 @Named 命名注入
- [ ] 演示 @Subcomponent 子组件
- [ ] 演示依赖组件 (Component dependencies)
- [ ] 实现同一接口多实现演示
- **Status:** pending

### Phase 5: Android 集成 (@BindsInstance, AndroidInjector)
- [ ] 创建 feature-android 模块
- [ ] 演示 @BindsInstance 运行时绑定
- [ ] 演示 AndroidInjector 模式
- [ ] 演示 Activity/Fragment 注入
- [ ] 演示 ViewModel 手动注入
- **Status:** pending

### Phase 6: 测试支持
- [ ] 配置测试依赖
- [ ] 实现测试组件替换
- [ ] 实现 Mock/Fake 依赖注入
- [ ] 单元测试示例
- **Status:** pending

## Key Questions
1. Dagger2 与 Hilt 的区别是什么？→ Dagger2 更底层，需要手动配置组件
2. kapt vs ksp 选择哪个？→ ksp 更快，但 Dagger2 对 kapt 支持更成熟
3. 如何处理循环依赖？→ 使用 Provider<T> 延迟获取
4. Subcomponent 和 Component dependencies 区别？→ Subcomponent 继承父组件作用域

## Decisions Made
| Decision | Rationale |
|----------|-----------|
| 渐进式学习 Demo | 每个模块专注一组概念 |
| Kotlin 2.0.21 | 与项目其他模块保持一致 |
| Dagger 2.52 | 与 Hilt 版本一致，方便对比 |
| kapt | Dagger2 官方支持更成熟 |
| minSdk 33 | 与项目其他模块保持一致 |

## Errors Encountered
| Error | Attempt | Resolution |
|-------|---------|------------|
| (暂无) | - | - |

## Notes
- 项目位置: androidx/dagger-demo
- 技术栈: Kotlin, Dagger2, ViewModel, Compose
- 对比学习: 可与 hilt-demo 对比理解 Hilt 封装
