# Task Plan: Dagger2 依赖注入学习 Demo

## Goal
创建一个渐进式学习 Dagger2 依赖注入的 Android Demo，通过单 Activity + 多 Fragment 方式展示 Dagger2 核心概念。

## Current Phase
Phase 2: 基础注入 (BasicFragment)

## Phases

### Phase 1: 项目基础搭建 ✅
- [x] 创建 dagger-demo 模块目录结构
- [x] 配置 build.gradle.kts (手动 DI)
- [x] 配置 settings.gradle.kts 添加模块
- [x] 创建 DemoApplication (初始化 AppContainer)
- [x] 创建 MainActivity (Fragment 容器 + BottomNav)
- [x] 创建所有布局文件
- [x] 创建 CoffeeShop 模型类
- [x] 创建作用域示例类
- **Status:** complete

### Phase 2: 基础注入 (BasicFragment)
- [x] 创建 BasicFragment
- [x] 创建 CoffeeShop 模型 (CoffeeMaker, Heater, Pump)
- [x] 实现 AppContainer 提供依赖
- [x] UI 展示注入结果
- **Status:** complete

### Phase 3: 作用域管理 (ScopeFragment)
- [x] 创建 ScopeFragment
- [x] 创建 DatabaseService (@Singleton)
- [x] 创建 UserService (@ActivityScoped)
- [x] 创建 RequestService (无作用域)
- [x] UI 展示实例 ID 对比
- **Status:** complete

### Phase 4: 限定符 (QualifierFragment)
- [ ] 创建 QualifierFragment
- [ ] 创建 @LocalDataSource 限定符
- [ ] 创建 @RemoteDataSource 限定符
- [ ] 创建 DataSource 接口和两个实现
- [ ] UI 展示多实现注入
- **Status:** pending

### Phase 5: 子组件 (SubcomponentFragment)
- [ ] 创建 SubcomponentFragment
- [ ] 创建 LoginComponent (@Subcomponent)
- [ ] 在 AppComponent 中声明子组件工厂
- [ ] UI 展示子组件访问父组件依赖
- **Status:** pending

### Phase 6: Android 集成 (AndroidFragment)
- [ ] 创建 AndroidFragment
- [ ] 实现 ViewModel 手动注入
- [ ] 实现 Fragment 注入演示
- [ ] UI 展示 ViewModel 中的依赖
- **Status:** pending

## Key Questions
1. ~~ Dagger 注解处理器不支持 Kotlin 2.2.x → 改用手动 DI
2. 如何验证作用域生效？→ 对比实例 hashCode
3. Subcomponent 和 Component dependencies 区别？→ Subcomponent 继承父组件作用域

## Decisions Made
| Decision | Rationale |
|----------|-----------|
| 单模块 + 多 Fragment | 学习路径清晰，切换方便 |
| 手动 DI | Dagger 注解处理器不支持 Kotlin 2.2.x |
| XML View | 用户选择传统 UI 方式 |
| CoffeeShop 示例 | Dagger 官方经典教程 |

## Errors Encountered
| Error | Attempt | Resolution |
|-------|---------|------------|
| Dagger 不支持 Kotlin 2.2.x | 1 | 改用手动 DI，避免注解处理器 |
| 编译错误 | 2 | 修复语法错误和 |

## Notes
- 设计文档: docs/plans/2026-03-11-dagger-demo-design.md
- 项目位置: androidx/dagger-demo
- 包名: com.peter.dagger.demo
- 官方 Demo: 使用手动 DI 屼式演示 Dagger2 概念
