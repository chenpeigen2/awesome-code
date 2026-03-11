# Dagger2 Demo 设计文档

## 项目概述

创建一个渐进式学习 Dagger2 依赖注入的 Android Demo，通过单 Activity + 多 Fragment 的方式，由浅入深展示 Dagger2 的核心概念。

## 用户需求

- **学习目的**: 从零开始学习 Dagger2 基础
- **学习方式**: 单一模块 + 分步示例，由浅入深
- **学习内容**: 基础注入、作用域、限定符、子组件、Android 集成
- **UI 形式**: 传统 XML View

## 设计方案

### 方案选择: 分页面递进式

一个 Activity + 多个 Fragment，每个 Fragment 展示一个概念。

## 项目结构

```
dagger-demo/
├── src/main/java/com/peter/dagger/demo/
│   ├── MainActivity.kt           # Fragment 容器 + 底部导航
│   ├── DemoApplication.kt        # Application，初始化 AppComponent
│   │
│   ├── ui/fragment/              # 5 个学习页面
│   │   ├── BasicFragment.kt      # 基础注入演示
│   │   ├── ScopeFragment.kt      # 作用域演示
│   │   ├── QualifierFragment.kt  # 限定符演示
│   │   ├── SubcomponentFragment.kt # 子组件演示
│   │   └── AndroidFragment.kt    # Android 集成演示
│   │
│   ├── di/                       # Dagger 组件和模块
│   │   ├── AppComponent.kt       # 应用级组件
│   │   ├── AppModule.kt          # 应用级模块
│   │   ├── BasicModule.kt        # 基础注入模块
│   │   ├── ScopeModule.kt        # 作用域模块
│   │   ├── QualifierModule.kt    # 限定符模块
│   │   └── ActivityScope.kt      # 自定义作用域注解
│   │
│   └── model/                    # 示例用的模型类
│       ├── Coffee.kt             # CoffeeShop 示例
│       ├── Heater.kt
│       ├── Pump.kt
│       └── Repository.kt
│
└── src/main/res/
    ├── layout/
    │   ├── activity_main.xml     # Fragment 容器 + BottomNavigationView
    │   └── fragment_demo.xml     # 通用 Fragment 布局
    └── menu/
        └── bottom_nav.xml        # 底部导航菜单
```

## 学习路径与内容

### Step 1: 基础注入 (BasicFragment)

**概念:** @Inject, @Module, @Component, @Provides, @Binds

**示例:** CoffeeShop 咖啡机

```kotlin
// 1. @Inject 构造器注入
class ElectricHeater @Inject constructor() : Heater

// 2. @Module 提供依赖
@Module
object CoffeeModule {
    @Provides fun providePump(heater: Heater): Pump = Thermosiphon(heater)
}

// 3. @Component 连接
@Component(modules = [CoffeeModule::class])
interface CoffeeComponent {
    fun inject(fragment: BasicFragment)
}
```

**UI 展示:** 点击按钮 → 显示注入的 CoffeeMaker 对象信息

### Step 2: 作用域 (ScopeFragment)

**概念:** @Singleton, 自定义 @Scope

**示例:** 对比三种作用域的生命周期

| 服务 | 作用域 | 生命周期 |
|------|--------|----------|
| DatabaseService | @Singleton | 应用级单例 |
| UserService | @ActivityScope | Activity 内单例 |
| RequestService | 无作用域 | 每次新建 |

**UI 展示:** 显示各服务的实例 ID，验证单例/非单例

### Step 3: 限定符 (QualifierFragment)

**概念:** @Qualifier, @Named

**示例:** 同一接口多个实现

```kotlin
// 自定义限定符
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocalDataSource

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RemoteDataSource

@Module
object RepositoryModule {
    @Provides @LocalDataSource
    fun provideLocalRepo(): DataSource = LocalDataSource()

    @Provides @RemoteDataSource
    fun provideRemoteRepo(): DataSource = RemoteDataSource()
}
```

**UI 展示:** 显示本地数据源 vs 远程数据源的注入结果

### Step 4: 子组件 (SubcomponentFragment)

**概念:** @Subcomponent, 组件继承

**示例:** 登录流程的子组件

```
AppComponent (应用级)
    └── LoginComponent (登录流程专用)
            └── 包含 UserRepository, AuthService
```

**UI 展示:** 显示子组件如何访问父组件的依赖

### Step 5: Android 集成 (AndroidFragment)

**概念:** Activity/Fragment 注入, ViewModel 手动注入

**示例:**
- 在 Activity 中手动调用 `component.inject(this)`
- 使用 Dagger 创建 ViewModel Factory

**UI 展示:** 显示在 Fragment 中注入的依赖

## Gradle 配置

### build.gradle.kts (dagger-demo)

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")  // 使用 ksp 处理注解
}

dependencies {
    // Dagger2
    implementation("com.google.dagger:dagger:2.52")
    ksp("com.google.dagger:dagger-compiler:2.52")

    // AndroidX
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.fragment:fragment-ktx:1.8.5")
    implementation("com.google.android.material:material:1.12.0")

    // ViewModel (手动注入，不用 Hilt)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
}
```

**说明:**
- 使用 **ksp** 而非 kapt（编译更快）
- 不使用 Hilt，纯 Dagger2 手动配置
- 版本与项目其他模块保持一致

## 技术规格

| 项目 | 内容 |
|------|------|
| **项目名称** | dagger-demo |
| **包名** | com.peter.dagger.demo |
| **UI 方式** | 单 Activity + 5 个 Fragment + BottomNavigationView |
| **学习路径** | 基础注入 → 作用域 → 限定符 → 子组件 → Android 集成 |
| **示例风格** | CoffeeShop + 实用场景 |
| **依赖处理** | ksp + Dagger 2.52 |
| **minSdk** | 33 |
| **targetSdk** | 36 |

## 实现阶段

| Phase | 内容 | 产出 |
|-------|------|------|
| Phase 1 | 项目基础搭建 | 目录结构、Gradle 配置、Application、MainActivity |
| Phase 2 | 基础注入 | BasicFragment + CoffeeModule + CoffeeComponent |
| Phase 3 | 作用域管理 | ScopeFragment + 自定义 @ActivityScope + ScopeModule |
| Phase 4 | 限定符 | QualifierFragment + @LocalDataSource/@RemoteDataSource |
| Phase 5 | 子组件 | SubcomponentFragment + LoginComponent |
| Phase 6 | Android 集成 | AndroidFragment + ViewModel 手动注入 |

## 设计日期

2026-03-11
