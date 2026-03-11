# Findings & Decisions - Hilt Demo

## Requirements

### 项目目标
创建一个详细的 Hilt 依赖注入学习 Demo，通过渐进式学习方式展示 Hilt 的核心功能。

### 模块划分
1. **feature-basic** - 基础注入概念
2. **feature-viewmodel** - ViewModel 注入
3. **feature-scope** - 作用域管理
4. **feature-advanced** - 高级特性
5. **feature-test** - 测试支持

---

## Hilt 核心概念

### 入口点注解
| 注解 | 用途 |
|------|------|
| `@HiltAndroidApp` | Application 类入口 |
| `@AndroidEntryPoint` | Activity/Fragment/View 入口 |
| `@HiltViewModel` | ViewModel 入口 |

### 注入方式
| 注解 | 用途 |
|------|------|
| `@Inject` | 构造器/字段/方法注入 |
| `@Module` | 模块定义 |
| `@Provides` | 提供实例方法 |
| `@Binds` | 接口绑定 |

### 作用域
| 注解 | 生命周期 |
|------|----------|
| `@Singleton` | 应用级单例 |
| `@ActivityScoped` | Activity 级别 |
| `@ViewModelScoped` | ViewModel 级别 |
| `@FragmentScoped` | Fragment 级别 |
| `@ViewScoped` | View 级别 |

### 高级特性
| 注解 | 用途 |
|------|------|
| `@EntryPoint` | 非 Hilt 类获取依赖 |
| `@Qualifier` | 自定义限定符 |
| `@Named` | 命名注入 |
| `@BindsInstance` | 绑定运行时实例 |

---

## Gradle 配置要点

### 根项目 plugins
```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}
```

### 模块依赖
```kotlin
dependencies {
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // ViewModel 支持
    implementation(libs.androidx.hilt.navigation.compose)

    // 测试
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)
}
```

---

## 项目结构设计

```
hilt-demo/
├── app/                        # 主应用入口
│   └── src/main/java/com/peter/hilt/demo/
│       ├── MainActivity.kt     # 模块选择列表
│       └── DemoApplication.kt  # @HiltAndroidApp
│
├── feature-basic/             # 基础注入
│   └── src/main/java/com/peter/hilt/demo/basic/
│       ├── BasicActivity.kt
│       ├── DatabaseService.kt
│       ├── NetworkService.kt
│       ├── AnalyticsService.kt
│       └── BasicModule.kt
│
├── feature-viewmodel/         # ViewModel 注入
│   └── src/main/java/com/peter/hilt/demo/viewmodel/
│       ├── ViewModelActivity.kt
│       ├── UserViewModel.kt
│       ├── UserRepository.kt
│       ├── UserRepositoryImpl.kt
│       └── UserModule.kt
│
├── feature-scope/             # 作用域管理
│   └── src/main/java/com/peter/hilt/demo/scope/
│       ├── ScopeActivity.kt
│       ├── SingletonService.kt
│       ├── ActivityScopedService.kt
│       ├── ViewModelScopedService.kt
│       └── ScopeModule.kt
│
├── feature-advanced/          # 高级特性
│   └── src/main/java/com/peter/hilt/demo/advanced/
│       ├── AdvancedActivity.kt
│       ├── EntryPointDemo.kt
│       ├── QualifierDemo.kt
│       ├── NamedDemo.kt
│       └── AdvancedModule.kt
│
└── feature-test/              # 测试支持
    └── src/test/java/com/peter/hilt/demo/test/
        ├── HiltTestRunner.kt
        ├── ViewModelTest.kt
        └── TestModule.kt
```

---

## 技术版本

| 依赖 | 版本 |
|------|------|
| Kotlin | 2.0.21 |
| Hilt | 2.52 |
| Compose BOM | 2024.10.01 |
| KSP | 2.0.21-1.0.27 |
| minSdk | 33 |
| targetSdk | 36 |

---

## Resources

### 官方文档
- [Hilt 官方指南](https://dagger.dev/hilt/)
- [Android 依赖注入](https://developer.android.com/training/dependency-injection/hilt-android)
- [Hilt 与 Compose](https://developer.android.com/jetpack/compose/libraries#hilt)

---

*Update after every 2 view/browser/search operations*
