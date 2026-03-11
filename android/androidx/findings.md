# Findings & Decisions - Dagger2 Demo

## Requirements

### 项目目标
创建一个详细的 Dagger2 依赖注入学习 Demo，通过渐进式学习方式展示 Dagger2 的核心功能，帮助理解 Hilt 的底层原理。

### 模块划分
1. **feature-basic** - 基础注入 (@Inject, @Module, @Component)
2. **feature-scope** - 作用域管理 (@Singleton, 自定义 Scope)
3. **feature-advanced** - 高级特性 (@Qualifier, @Named, Subcomponent)
4. **feature-android** - Android 集成 (@BindsInstance, AndroidInjector)
5. **feature-test** - 测试支持

---

## Dagger2 核心概念

### 核心注解
| 注解 | 用途 |
|------|------|
| `@Inject` | 标记需要注入的构造器/字段/方法 |
| `@Module` | 定义提供依赖的类 |
| `@Provides` | 在 Module 中提供实例的方法 |
| `@Component` | 连接 @Inject 和 @Module 的桥梁 |
| `@Binds` | 绑定接口到实现 (比 @Provides 更高效) |

### 作用域注解
| 注解 | 用途 |
|------|------|
| `@Singleton` | 单例 (需要配合 Component 作用域) |
| `@Scope` | 自定义作用域的元注解 |
| `@Reusable` | 可重用作用域 (弱引用缓存) |

### 高级注解
| 注解 | 用途 |
|------|------|
| `@Qualifier` | 自定义限定符的元注解 |
| `@Named` | 字符串限定符 |
| `@Subcomponent` | 子组件，继承父组件的依赖 |
| `@BindsInstance` | 绑定运行时实例到 Component |

---

## Dagger2 vs Hilt 对比

| 特性 | Dagger2 | Hilt |
|------|---------|------|
| Application 组件 | 手动创建 | 自动生成 (@HiltAndroidApp) |
| Activity 注入 | 手动调用 inject() | 自动注入 (@AndroidEntryPoint) |
| 生命周期组件 | 需要手动管理 | 自动管理 (@ActivityScoped 等) |
| ViewModel 注入 | 手动 Factory | 自动 (@HiltViewModel) |
| 学习曲线 | 陡峭 | 平缓 |
| 灵活性 | 高 | 中等 |
| 样板代码 | 多 | 少 |

---

## Gradle 配置要点

### 项目级 build.gradle.kts
```kotlin
plugins {
    id("com.google.dagger.hilt.android") version "2.52" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
}
```

### 模块级 build.gradle.kts
```kotlin
plugins {
    id("com.google.devtools.ksp") // 或 kapt
}

dependencies {
    implementation("com.google.dagger:dagger:2.52")
    ksp("com.google.dagger:dagger-compiler:2.52")
    // 或 kapt("com.google.dagger:dagger-compiler:2.52")
}
```

---

## 项目结构设计

```
dagger-demo/
├── app/                        # 主应用入口
│   └── src/main/java/com/peter/dagger/demo/
│       ├── MainActivity.kt     # 模块选择列表
│       ├── DemoApplication.kt  # Application 初始化 Dagger
│       └── AppComponent.kt     # 应用级组件
│
├── feature-basic/             # 基础注入
│   └── src/main/java/com/peter/dagger/demo/basic/
│       ├── BasicActivity.kt
│       ├── CoffeeModule.kt
│       ├── CoffeeComponent.kt
│       ├── Coffee.kt
│       ├── Heater.kt
│       └── Pump.kt
│
├── feature-scope/             # 作用域管理
│   └── src/main/java/com/peter/dagger/demo/scope/
│       ├── ScopeActivity.kt
│       ├── ActivityScope.kt    # 自定义作用域
│       ├── ScopeComponent.kt
│       └── ScopeModule.kt
│
├── feature-advanced/          # 高级特性
│   └── src/main/java/com/peter/dagger/demo/advanced/
│       ├── AdvancedActivity.kt
│       ├── QualifierModule.kt
│       ├── NamedModule.kt
│       └── SubcomponentDemo.kt
│
└── feature-android/           # Android 集成
    └── src/main/java/com/peter/dagger/demo/android/
        ├── AndroidActivity.kt
        ├── AndroidComponent.kt
        └── AndroidModule.kt
```

---

## 技术版本

| 依赖 | 版本 |
|------|------|
| Kotlin | 2.0.21 |
| Dagger | 2.52 |
| KSP | 2.0.21-1.0.27 |
| Compose BOM | 2024.10.01 |
| minSdk | 33 |
| targetSdk | 36 |

---

## 经典示例: CoffeeShop

Dagger 官方教程的经典示例，适合演示基础概念:

```kotlin
// 咖啡机需要加热器和泵
class CoffeeMaker @Inject constructor(
    private val heater: Heater,
    private val pump: Pump
) {
    fun brew() { ... }
}

// 电加热器
class ElectricHeater @Inject constructor() : Heater { ... }

// 泵需要加热器
class Thermosiphon @Inject constructor(
    private val heater: Heater
) : Pump { ... }
```

---

## Resources

### 官方文档
- [Dagger 官方指南](https://dagger.dev/)
- [Dagger2 GitHub](https://github.com/google/dagger)
- [Android 依赖注入指南](https://developer.android.com/training/dependency-injection)

### 教程
- [Dagger2 官方教程](https://dagger.dev/tutorial/)
- [Dagger2 入门指南](https://dagger.dev/dev-guide/)

---

*Update after every 2 view/browser/search operations*
