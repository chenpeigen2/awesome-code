# Jetpack Compose 学习 Demo 设计文档

## 概述

创建一个 `compose-demo` 模块，提供从入门到进阶的 Jetpack Compose 完整学习路径。采用分层递进式结构，共 5 个层级、20 个示例。

## 目标

- 系统学习 Compose 核心概念
- 从简单到复杂的学习曲线
- 每个示例可独立运行和调试
- 代码注释详细，便于自学

## 模块结构

```
compose-demo/
├── src/main/java/com/peter/compose/demo/
│   ├── MainActivity.kt              # 入口列表页
│   ├── MenuItem.kt                  # 列表数据类
│   ├── MainAdapter.kt               # RecyclerView 适配器
│   │
│   ├── level1/                      # Level 1 - 入门基础
│   │   ├── PreviewActivity.kt       # Compose 预览与 @Preview
│   │   ├── BasicComponentsActivity.kt   # Text, Button, Image, Icon
│   │   ├── LayoutComponentsActivity.kt  # Column, Row, Box, Card
│   │   └── ModifiersActivity.kt     # Modifier 基础用法
│   │
│   ├── level2/                      # Level 2 - 状态与交互
│   │   ├── StateBasicsActivity.kt   # mutableStateOf, remember
│   │   ├── StateHoistingActivity.kt # 状态提升模式
│   │   ├── SideEffectsActivity.kt   # LaunchedEffect, DisposableEffect
│   │   └── GesturesActivity.kt      # click, swipe, drag
│   │
│   ├── level3/                      # Level 3 - 架构与数据流
│   │   ├── ViewModelIntegrationActivity.kt  # ViewModel + Compose
│   │   ├── FlowLiveDataActivity.kt  # Flow/LiveData 收集
│   │   ├── MviPatternActivity.kt    # 单向数据流示例
│   │   └── DependencyInjectionActivity.kt   # Koin 集成
│   │
│   ├── level4/                      # Level 4 - 列表与动画
│   │   ├── LazyListsActivity.kt     # LazyColumn, LazyRow, LazyGrid
│   │   ├── AnimationsActivity.kt    # animate*AsState, AnimatedVisibility
│   │   ├── ContentTransformActivity.kt  # Crossfade, slide 动画
│   │   └── CanvasDrawingActivity.kt # Canvas 自定义绘制
│   │
│   └── level5/                      # Level 5 - 高级进阶
│       ├── CustomLayoutActivity.kt  # 自定义 Layout
│       ├── CompositionLocalActivity.kt  # CompositionLocalProvider
│       ├── PerformanceActivity.kt   # 重组优化, key, derivedStateOf
│       └── InteropActivity.kt       # AndroidView, View 互操作
│
├── src/main/res/
│   ├── layout/activity_main.xml
│   └── values/strings.xml
│
└── build.gradle.kts
```

## 技术栈

- **Kotlin**: 2.2.21
- **Compose BOM**: 2024.x.x（最新稳定版）
- **Material3**: 使用 Material3 组件
- **Activity Compose**: 1.9.x
- **Lifecycle ViewModel Compose**: 2.8.x

## 各层级详细内容

### Level 1 - 入门基础（4 个示例）

#### 1.1 PreviewActivity - Compose 预览
- @Preview 注解基础
- @Preview with name, showBackground
- @PreviewLightMode / @PreviewDarkMode
- Multipreview 注解

#### 1.2 BasicComponentsActivity - 基础组件
- Text: 文本样式、字体、行数限制
- Button: 按钮样式、启用/禁用
- Image: 图片加载、contentScale
- Icon: Material Icons 使用

#### 1.3 LayoutComponentsActivity - 布局组件
- Column: 垂直布局、对齐
- Row: 水平布局、权重
- Box: 叠加布局
- Card: 卡片容器

#### 1.4 ModifiersActivity - 修饰符基础
- padding, margin
- size, width, height
- background, border
- click, semantic

### Level 2 - 状态与交互（4 个示例）

#### 2.1 StateBasicsActivity - 状态基础
- mutableStateOf
- remember, rememberSaveable
- 状态读写的触发重组

#### 2.2 StateHoistingActivity - 状态提升
- 状态提升模式
- 无状态组件 vs 有状态组件
- 实践：封装可复用组件

#### 2.3 SideEffectsActivity - 副作用
- LaunchedEffect
- DisposableEffect
- SideEffect
- rememberCoroutineScope

#### 2.4 GesturesActivity - 手势处理
- Modifier.clickable
- Modifier.pointerInput
- detectTapGestures
- detectDragGestures

### Level 3 - 架构与数据流（4 个示例）

#### 3.1 ViewModelIntegrationActivity - ViewModel 集成
- viewModel() 函数
- ViewModel 状态持有
- 配置变更状态保留

#### 3.2 FlowLiveDataActivity - Flow/LiveData 集成
- collectAsState()
- collectAsStateWithLifecycle()
- observeAsState()

#### 3.3 MviPatternActivity - MVI 模式
- 单向数据流
- State + Action + Effect
- 实践：计数器 MVI 示例

#### 3.4 DependencyInjectionActivity - 依赖注入
- Koin 集成
- ViewModel 注入
- Repository 注入

### Level 4 - 列表与动画（4 个示例）

#### 4.1 LazyListsActivity - 懒加载列表
- LazyColumn / LazyRow
- LazyVerticalGrid
- items(), itemsIndexed()
- key, contentType

#### 4.2 AnimationsActivity - 动画基础
- animate*AsState
- AnimatedVisibility
- animateContentSize

#### 4.3 ContentTransformActivity - 内容转换
- Crossfade
- AnimatedContent
- slideIn / slideOut

#### 4.4 CanvasDrawingActivity - Canvas 绘制
- Canvas 组件
- drawRect, drawCircle, drawPath
- 绘制自定义图形

### Level 5 - 高级进阶（4 个示例）

#### 5.1 CustomLayoutActivity - 自定义布局
- Layout 可组合函数
- MeasurePolicy
- 实践：瀑布流布局

#### 5.2 CompositionLocalActivity - CompositionLocal
- compositionLocalOf
- CompositionLocalProvider
- 实践：主题切换

#### 5.3 PerformanceActivity - 性能优化
- 重组优化技巧
- key() 使用
- derivedStateOf
- @Stable / @Immutable

#### 5.4 InteropActivity - 互操作
- AndroidView 嵌入 View
- View 中嵌入 Compose
- Mixed View/Compose 界面

## 依赖配置

```kotlin
// build.gradle.kts
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.peter.compose.demo"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.peter.compose.demo"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
}

dependencies {
    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // ViewModel Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Activity Compose
    implementation(libs.androidx.activity.compose)

    // Koin
    implementation(libs.koin.core)
    implementation(libs.koin.android)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Debug
    debugImplementation(libs.androidx.compose.ui.tooling)
}
```

## 需要添加的版本目录条目

在 `gradle/libs.versions.toml` 中添加：

```toml
[versions]
composeBom = "2024.12.01"

[libraries]
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-compose-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activity" }
```

## 成功标准

- 所有 20 个示例可正常运行
- 代码注释详细，每个示例有清晰说明
- 入口列表展示所有示例，点击可进入
- 与项目现有 demo 模块结构保持一致
