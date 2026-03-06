# AutoDensity

一个轻量级的 Android 屏幕密度适配库，支持自动计算最佳 DPI，让你的应用在不同屏幕尺寸的设备上保持一致的视觉效果。

## 功能特性

- ✅ 自动计算最佳屏幕密度（DPI）
- ✅ 支持手机、平板、折叠屏等不同设备类型
- ✅ 支持基于宽度的等比缩放
- ✅ 支持无障碍显示大小设置
- ✅ 支持配置变更时自动更新
- ✅ 支持多窗口/分屏模式
- ✅ 零小米依赖，可独立使用

## 快速开始

### 1. 添加依赖

```groovy
dependencies {
    implementation 'com.peter.autodensity:autodensity:1.0.0'
}
```

### 2. 初始化

在 `Application.onCreate()` 中初始化：

```kotlin
class MyApplication : Application(), IDensity {

    override fun onCreate() {
        super.onCreate()
        // 初始化自动密度适配
        AutoDensity.init(this)
    }

    override fun shouldAdaptAutoDensity(): Boolean = true
}
```

### 3. Activity 配置（可选）

如果需要单独控制某个 Activity 是否启用适配：

```kotlin
class MainActivity : AppCompatActivity(), IDensity {

    override fun shouldAdaptAutoDensity(): Boolean = true

    // 可选：设置基准宽度，用于等比缩放
    // 例如：以 360dp 为基准进行缩放
    override fun getRatioUiBaseWidthDp(): Int = 360
}
```

## 高级用法

### 自定义密度配置

```kotlin
// 在 init 之前设置
AutoDensity.setForceDeviceScale(1.1f)  // 强制设备缩放值
AutoDensity.setForcePPI(400)            // 强制PPI

// 初始化
AutoDensity.init(this)
```

### 启用调试日志

```kotlin
AutoDensityConfig.debugEnabled = true
```

### 自定义标准 DPI

```kotlin
AutoDensityConfig.setStandardDpi(420f)
AutoDensityConfig.setStandardPpi(380f)
```

### 手动更新密度

```kotlin
// 在任意位置更新密度
AutoDensity.updateDensity(context)

// 强制更新
AutoDensity.forceUpdateDensity(context)
```

### 创建适配的 Context

```kotlin
// 创建自动适配的 Context
val wrappedContext = AutoDensity.createAutoDensityContextWrapper(context)

// 指定基准宽度
val wrappedContext = AutoDensity.createAutoDensityContextWrapperWithBaseDp(context, 360)
```

## 工作原理

1. **PPI 计算**：根据设备物理尺寸和分辨率计算真实 PPI
2. **缩放计算**：根据设备类型（手机/平板）计算最佳缩放值
3. **DPI 调整**：动态修改 `Resources.DisplayMetrics` 的 density 相关属性
4. **配置监听**：监听 `Configuration` 变化，自动更新密度

## 注意事项

1. **初始化时机**：必须在 `Application.onCreate()` 中调用 `AutoDensity.init()`
2. **配置变更**：Activity 需要正确处理 `onConfigurationChanged`，库会自动监听
3. **多进程**：暂不支持多进程环境
4. **系统资源**：默认会更新系统资源密度，可通过 `AutoDensity.init(this, false)` 禁用

## 常见问题

### Q: 为什么我的布局在某些设备上显示不正常？

A: 检查是否使用了固定像素值（px），建议使用 dp/sp 作为单位。

### Q: 如何禁用某个 Activity 的密度适配？

A: 让 Activity 实现 `IDensity` 接口，返回 `shouldAdaptAutoDensity() = false`。

### Q: 支持哪些 Android 版本？

A: 支持 Android 7.0 (API 24) 及以上版本。

## 迁移指南（从小米 miuix.autodensity）

如果你之前使用小米的 `miuix.autodensity`，可以通过以下步骤迁移：

1. **包名替换**：
   - `miuix.autodensity.AutoDensityConfig` → `com.peter.autodensity.AutoDensity`
   - `miuix.autodensity.IDensity` → `com.peter.autodensity.IDensity`

2. **移除小米特有功能**：
   - 移除 `MiuixApplication` 的使用
   - 移除对 `miuix.os.Build.IS_FOLDABLE` 等的依赖
   - 移除 `SkuScale` 的使用

3. **初始化方式变化**：
   ```kotlin
   // 旧方式
   AutoDensityConfig.init(this)

   // 新方式
   AutoDensity.init(this)
   ```

## License

MIT License
