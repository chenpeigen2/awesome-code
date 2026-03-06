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

        // 创建配置
        val config = DensityConfig(
            designWidthDp = 360,       // 设计稿宽度
            fontScale = null,          // null = 跟随系统
            debug = true,              // 开启调试日志
            updateSystemResources = true,
            forceDesignWidth = false
        )

        // 初始化
        AutoDensity.init(this, config)
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
// 方式一：在配置中指定
val config = DensityConfig(designWidthDp = 375)  // iOS 设计稿通常是 375dp
AutoDensity.init(this, config)

// 方式二：动态修改
AutoDensity.setDesignWidth(375)
```

### 控制字体大小

```kotlin
// 方式一：在配置中指定
val config = DensityConfig(
    designWidthDp = 360,
    fontScale = 1.15f  // 字体放大 15%
)

// 方式二：动态修改
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

### 限制界面最大宽度（baseWidthDp）

当屏幕宽度大于设计稿时，防止界面过度放大：

```kotlin
class MyActivity : AppCompatActivity(), ActivityDensityAware {

    // 当屏幕宽度 > 400dp 时，限制在 400dp
    // -1 = 使用屏幕实际宽度（默认）
    // 0 = 不限制
    override fun getBaseWidthDp() = 400
}
```

### 强制使用设计稿宽度（forceDesignWidth）

忽略 baseWidthDp 限制，强制使用 designWidthDp：

```kotlin
class MyActivity : AppCompatActivity(), ActivityDensityAware {

    // 强制使用 designWidthDp，不做任何限制
    override fun forceDesignWidth() = true
}
```

或者在全局配置中设置：

```kotlin
val config = DensityConfig(
    designWidthDp = 360,
    forceDesignWidth = true  // 全局强制使用 designWidthDp
)
```

### Service 密度适配

Service 中的 View（如悬浮窗、通知）也需要密度适配时：

```kotlin
class DemoService : Service(), ServiceDensityAware {

    override fun onCreate() {
        super.onCreate()

        // 应用密度适配到 Service
        AutoDensity.applyToContext(this)

        // 创建悬浮窗...
    }

    // 启用密度适配
    override fun shouldAdaptDensity() = true

    // 是否强制使用 designWidthDp
    override fun forceDesignWidth() = false
}
```

**注意**：Service 的 `baseWidthDp` 使用屏幕实际宽度，不支持自定义。

### 获取适配后的 DisplayMetrics

用于手动计算 dp/px 转换：

```kotlin
val metrics = AutoDensity.getAdaptedMetrics(context)
val px = 100 * metrics.density  // 100dp 转换为 px
```

## 配置变更处理

库会自动处理以下配置变更场景：

| 场景 | 处理方式 |
|------|----------|
| 屏幕旋转 | 自动重新计算密度 |
| 分辨率切换 | 自动检测并更新 |
| 分屏模式 | 自动适配分屏后的尺寸 |
| 系统字体缩放 | 自动应用新的 fontScale |

## 调试日志

开启 `debug = true` 后，会在 Logcat 中输出详细日志：

```
D/DensityDebugger: ╔══════════════════════════════════════════════════════════╗
D/DensityDebugger: ║               AutoDensity 初始化配置                      ║
D/DensityDebugger: ╠══════════════════════════════════════════════════════════╣
D/DensityDebugger: ║  config.designWidthDp = 360 dp
D/DensityDebugger: ║  → 实际使用的 designWidth = 360 dp
D/DensityDebugger: ╚══════════════════════════════════════════════════════════╝

D/DensityDebugger: ╔══════════════════════════════════════════════════════════╗
D/DensityDebugger: ║  对比 (原始 → 目标)                                       ║
D/DensityDebugger: ╠══════════════════════════════════════════════════════════╣
D/DensityDebugger: ║  density:       2.75 → 3.0
D/DensityDebugger: ║  densityDpi:    440 → 480
D/DensityDebugger: ║  scaledDensity: 2.75 → 3.0
D/DensityDebugger: ╚══════════════════════════════════════════════════════════╝
```

## 架构设计

```
com.peter.autodensity/
├── api/                          # 公开 API
│   ├── AutoDensity.kt            # 主入口
│   └── DensityConfig.kt          # 配置 & 接口
│
└── core/                         # 核心实现
    ├── DisplayInfo.kt            # 屏幕信息数据类
    ├── DensityCalculator.kt      # 密度计算
    ├── DensityApplier.kt         # 应用密度
    ├── DensityManager.kt         # 管理器
    └── DensityDebugger.kt        # 调试日志
```

### 核心类说明

| 类 | 职责 |
|---|------|
| `AutoDensity` | 对外入口，提供 init/setDesignWidth/setFontScale 等方法 |
| `DensityConfig` | 配置类，包含设计稿宽度、字体缩放等参数 |
| `DensityCalculator` | 计算目标 density、scaledDensity、densityDpi |
| `DensityApplier` | 将计算结果应用到 Resources |
| `DensityManager` | 管理配置、缓存、生命周期 |
| `DensityDebugger` | 统一的调试日志输出 |

### 接口说明

| 接口 | 适用场景 | 方法 |
|------|----------|------|
| `DensityAware` | Application/Activity/Service | `shouldAdaptDensity()` |
| `ActivityDensityAware` | Activity | `getBaseWidthDp()`, `forceDesignWidth()` |
| `ServiceDensityAware` | Service | `forceDesignWidth()` |

### 调用流程

```
Application.onCreate()
    ↓
AutoDensity.init(config)
    ↓
DensityManager.initialize()
    ↓
Activity.onCreate()
    ↓
ActivityLifecycleCallbacks.onActivityCreated()
    ↓
DensityCalculator.calculate()
    ↓
DensityApplier.apply()
    ↓
resources.displayMetrics 更新完成
```

## 常见问题

### Q: 为什么设置很小的 designWidthDp 没有效果？

A: 当 `designWidthDp < baseWidthDp` 时，会限制在 `baseWidthDp`，防止界面过大。如果要强制使用 designWidthDp，设置 `forceDesignWidth = true`：

```kotlin
// 方式一：Activity 级别
class MyActivity : AppCompatActivity(), ActivityDensityAware {
    override fun forceDesignWidth() = true
}

// 方式二：全局配置
val config = DensityConfig(designWidthDp = 80, forceDesignWidth = true)
```

### Q: 如何单独控制字体大小？

A: 使用 `fontScale` 参数，它只影响 `scaledDensity`（文字大小），不影响整体布局：

```kotlin
AutoDensity.setFontScale(1.5f)  // 只放大字体 50%
```

### Q: 如何处理横竖屏切换？

A: 库会自动处理。当 `Configuration` 改变时会触发重新计算并应用到所有活跃的 Activity。

### Q: Service 中的 View 如何适配？

A: 在 Service 的 `onCreate()` 中调用 `AutoDensity.applyToContext(this)`，并实现 `ServiceDensityAware` 接口。

### Q: 对性能有影响吗？

A: 几乎没有。密度计算只在 Activity 创建或配置变更时执行，之后使用缓存。

### Q: 支持 AndroidX 吗？

A: 是的，完全兼容 AndroidX。

## 注意事项

1. **初始化时机**：必须在 `Application.onCreate()` 中初始化
2. **接口实现**：Application 或 Activity 需要实现 `DensityAware` 接口
3. **Service 适配**：需要手动调用 `AutoDensity.applyToContext(this)`

## License

MIT License
