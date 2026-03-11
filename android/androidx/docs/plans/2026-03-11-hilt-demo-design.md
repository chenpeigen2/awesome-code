# Hilt Demo 设计文档

## 概述

创建一个渐进式学习 Hilt 依赖注入的 Android Demo 项目，通过 5 个独立模块全面展示 Hilt 的核心功能和高级特性。

## 项目信息

| 项目 | 值 |
|------|-----|
| 名称 | hilt-demo |
| 类型 | Android 学习演示 |
| 位置 | androidx/hilt-demo |
| 技术栈 | Kotlin, Hilt, ViewModel, Compose |

## 项目结构

```
hilt-demo/
├── app/                        # 主应用入口
│   ├── build.gradle.kts
│   └── src/main/java/com/peter/hilt/demo/
│       ├── MainActivity.kt     # 主页面（模块选择列表）
│       └── DemoApplication.kt  # Application 类 (@HiltAndroidApp)
│
├── feature-basic/             # 模块1: 基础注入
│   ├── build.gradle.kts
│   └── src/main/java/com/peter/hilt/demo/basic/
│       ├── BasicActivity.kt        # 入口页面
│       ├── DatabaseService.kt      # @Inject 构造器注入示例
│       ├── NetworkService.kt       # @Inject 构造器注入示例
│       ├── AnalyticsService.kt     # 接口注入示例
│       └── BasicModule.kt          # @Module @Provides 示例
│
├── feature-viewmodel/          # 模块2: ViewModel 注入
│   ├── build.gradle.kts
│   └── src/main/java/com/peter/hilt/demo/viewmodel/
│       ├── ViewModelActivity.kt    # 入口页面
│       ├── UserViewModel.kt        # @HiltViewModel 示例
│       ├── UserRepository.kt       # Repository 接口
│       ├── UserRepositoryImpl.kt   # Repository 实现
│       └── UserModule.kt           # @Binds 绑定示例
│
├── feature-scope/              # 模块3: 作用域管理
│   ├── build.gradle.kts
│   └── src/main/java/com/peter/hilt/demo/scope/
│       ├── ScopeActivity.kt        # 入口页面
│       ├── SingletonService.kt     # @Singleton 示例
│       ├── ActivityScopedService.kt # @ActivityScoped 示例
│       ├── ViewModelScopedService.kt # @ViewModelScoped 示例
│       └── ScopeModule.kt          # 作用域模块
│
├── feature-advanced/           # 模块4: 高级特性
│   ├── build.gradle.kts
│   └── src/main/java/com/peter/hilt/demo/advanced/
│       ├── AdvancedActivity.kt     # 入口页面
│       ├── EntryPointDemo.kt       # @EntryPoint 示例
│       ├── QualifierDemo.kt        # @Qualifier 限定符示例
│       ├── NamedDemo.kt            # @Named 命名示例
│       └── AdvancedModule.kt       # 高级模块配置
│
├── feature-test/               # 模块5: 测试支持
│   ├── build.gradle.kts
│   └── src/test/java/com/peter/hilt/demo/test/
│       ├── HiltTestRunner.kt       # 自定义测试 Runner
│       ├── ViewModelTest.kt        # ViewModel 单元测试
│       └── TestModule.kt           # 测试用替换模块
│
├── build.gradle.kts            # 根项目配置
├── settings.gradle.kts         # 项目设置
└── gradle/
    └── libs.versions.toml       # 版本目录
```

## 模块详细设计

### 模块1: 基础注入 (feature-basic)

**学习目标**: 掌握 Hilt 最基本的注入方式

**核心概念**:
- `@HiltAndroidApp` - 应用入口
- `@AndroidEntryPoint` - Activity/Fragment 入口
- `@Inject` - 构造器/字段/方法注入
- `@Module` - 模块定义
- `@Provides` - 提供实例
- `@Singleton` - 单例作用域

**UI 设计**:
```
┌─────────────────────────────────────┐
│         基础注入演示                  │
├─────────────────────────────────────┤
│                                     │
│  构造器注入示例:                      │
│  DatabaseService: 已注入 ✓          │
│  NetworkService: 已注入 ✓           │
│                                     │
│  字段注入示例:                        │
│  AnalyticsService: 已注入 ✓         │
│                                     │
│  单例验证:                           │
│  [获取实例] [显示 Hash]              │
│                                     │
└─────────────────────────────────────┘
```

### 模块2: ViewModel 注入 (feature-viewmodel)

**学习目标**: 掌握 ViewModel 与 Hilt 的集成

**核心概念**:
- `@HiltViewModel` - ViewModel 注入
- `SavedStateHandle` - 状态保存
- Repository 模式注入
- `@Binds` - 接口绑定

**UI 设计**:
```
┌─────────────────────────────────────┐
│         ViewModel 注入演示            │
├─────────────────────────────────────┤
│                                     │
│  用户信息:                           │
│  ┌─────────────────────────────┐   │
│  │ 用户名: [从 ViewModel 加载]   │   │
│  │ 邮箱: [从 ViewModel 加载]     │   │
│  └─────────────────────────────┘   │
│                                     │
│  操作:                               │
│  [刷新数据] [清除缓存]               │
│                                     │
│  SavedStateHandle:                  │
│  当前参数: [显示传入参数]            │
│                                     │
└─────────────────────────────────────┘
```

### 模块3: 作用域管理 (feature-scope)

**学习目标**: 理解不同作用域的生命周期

**核心概念**:
- `@Singleton` - 应用级单例
- `@ActivityScoped` - Activity 级别
- `@ViewModelScoped` - ViewModel 级别
- `@FragmentScoped` - Fragment 级别
- 作用域与实例复用

**UI 设计**:
```
┌─────────────────────────────────────┐
│         作用域管理演示                │
├─────────────────────────────────────┤
│                                     │
│  Singleton 服务:                     │
│  实例 Hash: [xxxxx]                  │
│  创建次数: 1                         │
│                                     │
│  ActivityScoped 服务:                │
│  实例 Hash: [xxxxx]                  │
│  [重新创建] (旋转屏幕测试)            │
│                                     │
│  ViewModelScoped 服务:               │
│  实例 Hash: [xxxxx]                  │
│                                     │
│  作用域对比表:                        │
│  ┌──────────┬────────┬────────┐    │
│  │ 作用域    │ 生命周期 │ 实例数 │    │
│  ├──────────┼────────┼────────┤    │
│  │ Singleton │ 应用   │ 1     │    │
│  │ Activity  │ Activity│ N     │    │
│  │ ViewModel │ VM     │ N     │    │
│  └──────────┴────────┴────────┘    │
└─────────────────────────────────────┘
```

### 模块4: 高级特性 (feature-advanced)

**学习目标**: 掌握 Hilt 高级用法

**核心概念**:
- `@EntryPoint` - 入口点
- `@Component` - 组件依赖
- `@Qualifier` - 自定义限定符
- `@Named` - 命名注入
- `@BindsInstance` - 绑定实例

**UI 设计**:
```
┌─────────────────────────────────────┐
│         高级特性演示                  │
├─────────────────────────────────────┤
│                                     │
│  📌 EntryPoint 示例:                 │
│  [从非 Hilt 类获取依赖]              │
│                                     │
│  🏷 Qualifier 示例:                  │
│  [同一个接口多个实现]                 │
│  ┌─────────────────────────────┐   │
│  │ DebugService: [Debug 实现]   │   │
│  │ ReleaseService: [Release 实现]│   │
│  └─────────────────────────────┘   │
│                                     │
│  📛 Named 示例:                      │
│  [通过名称区分实现]                   │
│                                     │
└─────────────────────────────────────┘
```

### 模块5: 测试支持 (feature-test)

**学习目标**: 掌握 Hilt 测试方法

**核心概念**:
- `@HiltAndroidTest` - 测试入口
- `UninstallModules` - 卸载模块
- `TestInstallIn` - 测试安装
- `Fake` / `Mock` - 替换依赖

**测试示例**:
```kotlin
@HiltAndroidTest
class ViewModelTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var userRepository: UserRepository

    @Test
    fun testUserRepository() {
        // 测试注入的 Repository
        assertNotNull(userRepository)
    }
}
```

## 技术栈版本

| 依赖 | 版本 |
|------|------|
| Kotlin | 2.0.21 |
| Hilt | 2.52 |
| Compose | 1.7.0 |
| Compose Material 3 | 1.3.0 |
| minSdk | 33 |
| targetSdk | 36 |

## Gradle 配置

### 根项目 build.gradle.kts
```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}
```

### 模块 build.gradle.kts 示例
```kotlin
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)

    // 测试
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)
}
```

## 实现优先级

1. **P0 - 基础设施**
   - 创建项目结构
   - 配置 Gradle 和 Hilt 插件
   - 创建 DemoApplication

2. **P1 - 基础模块**
   - 实现 basic 模块
   - 展示最基础的注入

3. **P2 - ViewModel 模块**
   - 实现 viewmodel 模块
   - 展示 MVVM 架构

4. **P3 - 作用域模块**
   - 实现 scope 模块
   - 展示生命周期管理

5. **P4 - 高级模块**
   - 实现 advanced 模块
   - 展示高级特性

6. **P5 - 测试模块**
   - 实现测试支持
   - 展示测试方法

## 验收标准

- [ ] 项目可以成功编译
- [ ] 5 个模块可以独立运行
- [ ] 每个模块有清晰的代码注释
- [ ] 包含 README 说明每个模块的学习要点
- [ ] 测试可以通过
