# SystemInfo Demo 设计文档

## 概述

创建一个系统信息展示 Demo，用于获取和展示 Android 设备的屏幕显示、电池状态、系统信息、字体配置等信息。作为生产代码参考，提供可复用的工具类。

## 目标

- 提供可复用的 `SystemInfoHelper` 工具类
- 展示如何获取 Android 系统各项信息
- 代码结构清晰，便于复制到其他项目使用

## 模块结构

```
systeminfo-demo/
├── src/main/java/com/peter/systeminfo/demo/
│   ├── MainActivity.kt              # 主 Activity + ViewPager2
│   ├── ViewPagerAdapter.kt          # Tab 适配器
│   ├── helper/
│   │   └── SystemInfoHelper.kt      # 核心工具类（可复用）
│   └── fragments/
│       ├── DisplayFragment.kt       # 屏幕显示信息
│       ├── BatteryFragment.kt       # 电池状态
│       ├── SystemFragment.kt        # 系统信息
│       └── FontFragment.kt          # 字体配置
└── src/main/res/
    ├── layout/
    │   ├── activity_main.xml
    │   └── fragment_info_list.xml   # 通用列表布局
    └── values/
        └── strings.xml
```

## SystemInfoHelper 工具类

```kotlin
object SystemInfoHelper {

    // ===== 屏幕显示 =====
    data class DisplayInfo(
        val resolution: String,        // "1080 x 2400"
        val density: Float,            // 2.75
        val densityDpi: Int,           // 440
        val scaledDensity: Float,      // 字体缩放
        val widthPixels: Int,          // 1080
        val heightPixels: Int,         // 2400
        val refreshRate: Float,        // 120Hz
        val screenWidthDp: Float,      // 360dp
        val screenHeightDp: Float      // 800dp
    )
    fun getDisplayInfo(activity: Activity): DisplayInfo

    // ===== 电池状态 =====
    data class BatteryInfo(
        val level: Int,                // 85%
        val status: String,            // 充电中/放电中
        val health: String,            // 良好/过热等
        val temperature: Float,        // 25.5°C
        val voltage: Int,              // 4200mV
        val technology: String         // Li-ion
    )
    fun getBatteryInfo(context: Context): BatteryInfo

    // ===== 系统信息 =====
    data class SystemInfo(
        val androidVersion: String,    // "Android 14"
        val apiLevel: Int,             // 34
        val device: String,            // 设备代号
        val model: String,             // Pixel 7
        val brand: String,             // Google
        val manufacturer: String,
        val buildId: String
    )
    fun getSystemInfo(): SystemInfo

    // ===== 字体配置 =====
    data class FontInfo(
        val fontScale: Float,          // 1.0 (系统字体缩放)
        val systemFontScale: Float,    // 系统设置中的字体大小
        val defaultFontSize: Float     // 默认字体大小 sp
    )
    fun getFontInfo(context: Context): FontInfo
}
```

## Fragment 与 UI 设计

每个 Fragment 使用统一的列表布局展示信息项：

```
┌─────────────────────────────────┐
│  📱 屏幕显示                      │
├─────────────────────────────────┤
│  分辨率          1080 × 2400     │
│  密度             2.75           │
│  DPI              440            │
│  刷新率           120 Hz         │
│  宽度             360 dp         │
│  高度             800 dp         │
│  ...                             │
└─────────────────────────────────┘
```

### UI 组件

- 使用 `RecyclerView` + `MaterialCardView` 展示信息列表
- 每个 item 包含：`label` (属性名) + `value` (属性值)
- 支持复制属性值到剪贴板（长按）

### 4 个 Tab

| Tab | 名称 | 内容 |
|-----|------|------|
| 1 | 显示 | 分辨率、密度、DPI、刷新率、dp 尺寸 |
| 2 | 电池 | 电量、状态、温度、电压、技术 |
| 3 | 系统 | Android 版本、API、型号、品牌 |
| 4 | 字体 | 字体缩放、默认字体大小 |

## 技术要点

### 屏幕信息获取

```kotlin
// 使用 WindowMetrics API (Android 11+)
val windowMetrics = activity.windowManager.currentWindowMetrics
val bounds = windowMetrics.bounds

// 密度信息
val density = context.resources.displayMetrics.density
val densityDpi = context.resources.displayMetrics.densityDpi

// 刷新率
val refreshRate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
    activity.display?.refreshRate ?: 60f
} else {
    // fallback
}
```

### 电池信息获取

```kotlin
val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
val batteryStatus = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
```

### 系统信息获取

```kotlin
Build.VERSION.SDK_INT
Build.VERSION.RELEASE
Build.MODEL
Build.BRAND
Build.MANUFACTURER
Build.DEVICE
Build.ID
```

### 字体信息获取

```kotlin
val configuration = context.resources.configuration
val fontScale = configuration.fontScale

// 默认字体大小
val defaultFontSize = context.resources.getDimension(android.R.dimen.app_icon_size)
```
