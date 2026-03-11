# Task Plan: Dagger2 依赖注入学习 Demo

## Goal
创建一个渐进式学习 Dagger2 依赖注入的 Android Demo，通过单 Activity + 多 Fragment 方式展示 Dagger2 核心概念。

## Current Phase
Phase 1: 项目基础搭建

## Phases

### Phase 1: 项目基础搭建
- [ ] 创建 dagger-demo 模块目录结构
- [ ] 配置 build.gradle.kts (ksp + dagger)
- [ ] 配置 settings.gradle.kts 添加模块
- [ ] 更新 gradle/libs.versions.toml 版本目录
- [ ] 创建 DemoApplication (初始化 AppComponent)
- [ ] 创建 MainActivity (Fragment 容器 + BottomNav)
- [ ] 创建 activity_main.xml 布局
- [ ] 创建 bottom_nav.xml 菜单
- [ ] 创建 AppComponent 和 AppModule
- [ ] 配置 AndroidManifest.xml
- **Status:** pending

### Phase 2: 基础注入 (BasicFragment)
- [ ] 创建 BasicFragment
- [ ] 创建 CoffeeShop 模型 (CoffeeMaker, Heater, Pump)
- [ ] 创建 CoffeeModule (@Module @Provides)
- [ ] 创建 CoffeeComponent (@Component)
- [ ] 实现字段注入演示
- [ ] UI 展示注入结果
- **Status:** pending

### Phase 3: 作用域管理 (ScopeFragment)
- [ ] 创建 ScopeFragment
- [ ] 创建 @ActivityScope 自定义注解
- [ ] 创建 DatabaseService (@Singleton)
- [ ] 创建 UserService (@ActivityScoped)
- [ ] 创建 RequestService (无作用域)
- [ ] 创建 ScopeModule 和 ScopeComponent
- [ ] UI 展示实例 ID 对比
- **Status:** pending

### Phase 4: 限定符 (QualifierFragment)
- [ ] 创建 QualifierFragment
- [ ] 创建 @LocalDataSource 限定符
- [ ] 创建 @RemoteDataSource 限定符
- [ ] 创建 DataSource 接口和两个实现
- [ ] 创建 QualifierModule
- [ ] UI 展示多实现注入
- **Status:** pending

### Phase 5: 子组件 (SubcomponentFragment)
- [ ] 创建 SubcomponentFragment
- [ ] 创建 LoginComponent (@Subcomponent)
- [ ] 创建 LoginModule
- [ ] 在 AppComponent 中声明子组件工厂
- [ ] UI 展示子组件访问父组件依赖
- **Status:** pending

### Phase 6: Android 集成 (AndroidFragment)
- [ ] 创建 AndroidFragment
- [ ] 实现 ViewModel 手动注入
- [ ] 创建 ViewModelFactory (Dagger 提供)
- [ ] 实现 Fragment 注入演示
- [ ] UI 展示 ViewModel 中的依赖
- **Status:** pending

## Key Questions
1. ksp vs kapt 选择哪个？→ ksp 编译更快，已选择 ksp
2. 如何验证作用域生效？→ 对比实例 hashCode
3. Subcomponent 和 Component dependencies 区别？→ Subcomponent 继承父组件作用域

## Decisions Made
| Decision | Rationale |
|----------|-----------|
| 单模块 + 多 Fragment | 学习路径清晰，切换方便 |
| ksp | 比 kapt 编译更快 |
| Dagger 2.52 | 与 Hilt 版本一致 |
| XML View | 用户选择传统 UI 方式 |
| CoffeeShop 示例 | Dagger 官方经典教程 |

## Errors Encountered
| Error | Attempt | Resolution |
|-------|---------|------------|
| (暂无) | - | - |

## Notes
- 设计文档: docs/plans/2026-03-11-dagger-demo-design.md
- 项目位置: androidx/dagger-demo
- 包名: com.peter.dagger.demo
