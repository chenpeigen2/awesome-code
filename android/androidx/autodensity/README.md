# AutoDensity - Android 屏幕密度适配库

一个轻量级、易用的 Android 屏幕密度适配库，让你告别多屏幕适配烦恼。

## 核心原理

### 什么是密度适配？

Android 使用 `dp`（density-independent pixels）作为布局单位：
```
px = dp × density
```

不同手机的 `density` 不同，导致同样的 `dp` 值显示的像素数不同：
- 手机 A：density=2.75，360dp = 990px
- 手机 B：density=3.0，360dp = 1080px

**密度适配**就是统一不同设备的 `density`，让界面显示一致。

### 如何实现？

修改 `DisplayMetrics` 中的三个关键值：

| 属性 | 说明 | 影响 |
|------|------|------|
| `density` | 密度值 | dp 转换 |
| `scaledDensity` | 缩放密度 | sp 转换（文字） |
| `densityDpi` | DPI | 资源选择 |

**计算公式**：
```
targetDensity = screenWidth / designWidthDp
```

例如：屏幕宽 1080px，设计稿 360dp
```
targetDensity = 1080 / 360 = 3.0
```

这样，`360dp` 的宽度刚好填满屏幕。

## 快速开始

### 1. 添加依赖

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.peter.autodensity:autodensity:1.0.0")
}
```

### 2. 初始化

```kotlin
class MyApp : Application(), DensityAware {

    override fun onCreate() {
        super.onCreate()
        // 初始化
        AutoDensity.init(this)
    }

    // 启用密度适配
    override fun shouldAdaptDensity() = true
}
```

### 3. 完成！

就这么简单，你的应用已经完成密度适配。

## 高级用法

### 指定设计稿宽度

```kotlin
AutoDensity.init(this)
AutoDensity.setDesignWidth(375)  // iOS 设计稿通常是 375dp
```

### 控制字体大小

```kotlin
AutoDensity.setFontScale(1.15f)  // 字体放大 15%
AutoDensity.setFontScale(0.85f)  // 字体缩小 15%
```

### 单个 Activity 禁用适配

```kotlin
class SpecialActivity : AppCompatActivity(), ActivityDensityAware {

    // 这个 Activity 不进行密度适配
    override fun shouldAdaptDensity() = false
}
```

### 小屏等比缩放

当屏幕宽度小于设计稿时，自动等比缩小：

```kotlin
class MyActivity : AppCompatActivity(), ActivityDensityAware {

    // 当屏幕宽度 < 360dp 时，等比缩放
    override fun getBaseWidthDp() = 360
}
```

## 架构设计

```
com.peter.autodensity/
├── api/                          # 公开 API
│   ├── AutoDensity.kt            # 主入口
│   ├── DensityConfig.kt          # 配置 & 接口
│
├── core/                         # 核心实现
│   ├── DisplayMetricsInfo.kt     # 数据模型
│   ├── DensityCalculator.kt      # 密度计算
│   ├── DensityApplier.kt         # 应用密度
│   └── DensityManager.kt         # 管理器
│
└── util/                         # 工具类
    └── Logger.kt                 # 日志
```

### 核心类说明

| 类 | 职责 |
|---|------|
| `AutoDensity` | 对外入口，提供 init/setDesignWidth/setFontScale 等方法 |
| `DensityConfig` | 配置类，包含设计稿宽度、字体缩放等参数 |
| `DensityCalculator` | 计算目标 density、scaledDensity、densityDpi |
| `DensityApplier` | 将计算结果应用到 Resources |
| `DensityManager` | 管理配置、缓存、生命周期 |

### 调用流程

```
Application.onCreate()
    ↓
AutoDensity.init()
    ↓
DensityManager.initialize()
    ↓
Activity.onCreate()
    ↓
ActivityCallbackHandler.onActivityCreated()
    ↓
DensityCalculator.calculate()
    ↓
DensityApplier.apply()
    ↓
resources.displayMetrics 更新完成
```

## 常见问题

### Q: 为什么设置 PPI 不能让字体变大？

A: PPI 影响的是整体 density，不只是字体。要单独控制字体大小，请使用 `setFontScale()`：

```kotlin
// ❌ 错误方式
AutoDensity.setForcePPI(8888)  // 整个 UI 都会变得很大

// ✅ 正确方式
AutoDensity.setFontScale(1.5f)  // 只放大字体 50%
```

### Q: 如何处理横竖屏切换？

A: 库会自动处理。`Activity` 的 `onConfigurationChanged` 会触发重新计算。

### Q: 对性能有影响吗？

A: 几乎没有。密度计算只在 Activity 创建时执行一次，之后直接使用缓存。

### Q: 支持 AndroidX 吗？

A: 是的，完全兼容 AndroidX。

## 注意事项

1. **初始化时机**：必须在 `Application.onCreate()` 中初始化
2. **接口实现**：Application 或 Activity 需要实现 `DensityAware` 接口
3. **调用顺序**：`setDesignWidth()` / `setFontScale()` 要在 `init()` 之前调用

## License

MIT License
