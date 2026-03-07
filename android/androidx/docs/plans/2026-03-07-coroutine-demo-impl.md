# Kotlin 协程 Android Demo 实现计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 创建一个从基础到高级的 Kotlin 协程 Android demo，包含详细注释和文档。

**Architecture:** 单模块 (`coroutine-demo`) 多包结构，使用 View + ViewBinding，遵循现有 demo 的代码风格。

**Tech Stack:** Kotlin Coroutines, Flow, Channel, Room, Lifecycle, ViewModel, ViewBinding

---

## Task 1: 创建模块基础结构

**Files:**
- Create: `coroutine-demo/build.gradle.kts`
- Create: `coroutine-demo/src/main/AndroidManifest.xml`
- Modify: `settings.gradle.kts`

**Step 1: 创建模块目录结构**

```bash
mkdir -p coroutine-demo/src/main/java/com/peter/coroutine/demo
mkdir -p coroutine-demo/src/main/res/layout
mkdir -p coroutine-demo/src/main/res/values
mkdir -p coroutine-demo/docs
```

**Step 2: 创建 build.gradle.kts**

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "com.peter.coroutine.demo"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.peter.coroutine.demo"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    annotationProcessor(libs.androidx.room.compiler)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.core.testing)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
```

**Step 3: 添加 Room 依赖到 libs.versions.toml**

在 `[versions]` 添加:
```toml
room = "2.6.1"
```

在 `[libraries]` 添加:
```toml
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
```

**Step 4: 创建 AndroidManifest.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Coroutine"
        tools:targetApi="31">

        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

    </application>

</manifest>
```

**Step 5: 更新 settings.gradle.kts**

在文件末尾添加:
```kotlin
// Coroutine Demo
include(":coroutine-demo")
```

**Step 6: 验证编译**

```bash
./gradlew :coroutine-demo:assembleDebug
```

Expected: BUILD SUCCESSFUL

**Step 7: Commit**

```bash
git add settings.gradle.kts coroutine-demo/
git commit -m "feat: add coroutine-demo module structure"
```

---

## Task 2: 创建公共组件和数据模型

**Files:**
- Create: `coroutine-demo/src/main/java/com/peter/coroutine/demo/MenuItem.kt`
- Create: `coroutine-demo/src/main/java/com/peter/coroutine/demo/MainAdapter.kt`
- Create: `coroutine-demo/src/main/java/com/peter/coroutine/demo/data/model/User.kt`
- Create: `coroutine-demo/src/main/res/layout/item_menu.xml`
- Create: `coroutine-demo/src/main/res/layout/item_menu_header.xml`

**Step 1: 创建 MenuItem.kt**

```kotlin
package com.peter.coroutine.demo

import android.content.Context
import android.content.Intent

/**
 * 菜单项数据模型
 */
data class MenuItem(
    val title: String,
    val description: String = "",
    val isHeader: Boolean = false,
    val intent: Intent? = null
)
```

**Step 2: 创建 MainAdapter.kt**

```kotlin
package com.peter.coroutine.demo

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * 主页菜单 Adapter
 */
class MainAdapter(
    private val items: List<MenuItem>,
    private val onItemClick: (MenuItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].isHeader) TYPE_HEADER else TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(parent)
            else -> ItemViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is HeaderViewHolder -> holder.bind(item)
            is ItemViewHolder -> holder.bind(item, onItemClick)
        }
    }

    override fun getItemCount(): Int = items.size

    class HeaderViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_menu_header, parent, false)
    ) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)

        fun bind(item: MenuItem) {
            tvTitle.text = item.title
        }
    }

    class ItemViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
    ) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvDesc: TextView = itemView.findViewById(R.id.tvDesc)

        fun bind(item: MenuItem, onClick: (MenuItem) -> Unit) {
            tvTitle.text = item.title
            tvDesc.text = item.description
            itemView.setOnClickListener { onClick(item) }
        }
    }
}
```

**Step 3: 创建 User.kt**

```kotlin
package com.peter.coroutine.demo.data.model

/**
 * 用户数据模型 - 用于 Room 示例
 *
 * @property id 用户 ID
 * @property name 用户名
 * @property email 邮箱
 * @property createdAt 创建时间戳
 */
data class User(
    val id: Long = 0,
    val name: String,
    val email: String,
    val createdAt: Long = System.currentTimeMillis()
)
```

**Step 4: 创建 item_menu.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="?attr/selectableItemBackground"
    android:orientation="vertical"
    android:paddingStart="16dp"
    android:paddingTop="12dp"
    android:paddingEnd="16dp"
    android:paddingBottom="12dp">

    <TextView
        android:id="@+id/tvTitle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textColor="@color/black"
        android:textSize="16sp"
        tools:text="suspend 函数" />

    <TextView
        android:id="@+id/tvDesc"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="4dp"
        android:textColor="@color/gray"
        android:textSize="13sp"
        tools:text="挂起函数基础用法" />

    <View
        android:layout_width="match_parent"
        android:layout_height="1dp"
        android:layout_marginTop="12dp"
        android:background="@color/divider" />

</LinearLayout>
```

**Step 5: 创建 item_menu_header.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical">

    <TextView
        android:id="@+id/tvTitle"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@color/gray_light"
        android:paddingStart="16dp"
        android:paddingTop="12dp"
        android:paddingEnd="16dp"
        android:paddingBottom="8dp"
        android:textColor="@color/purple_700"
        android:textSize="14sp"
        android:textStyle="bold"
        tools:text="协程基础" />

</LinearLayout>
```

**Step 6: Commit**

```bash
git add coroutine-demo/
git commit -m "feat: add common components and data model"
```

---

## Task 3: 创建 MainActivity 和主布局

**Files:**
- Create: `coroutine-demo/src/main/res/layout/activity_main.xml`
- Create: `coroutine-demo/src/main/res/values/strings.xml`
- Create: `coroutine-demo/src/main/res/values/colors.xml`
- Create: `coroutine-demo/src/main/java/com/peter/coroutine/demo/MainActivity.kt`

**Step 1: 创建 activity_main.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true">

    <com.google.android.material.appbar.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:fitsSystemWindows="true">

        <com.google.android.material.appbar.MaterialToolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            app:title="@string/app_name" />

    </com.google.android.material.appbar.AppBarLayout>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior="@string/appbar_scrolling_view_behavior" />

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

**Step 2: 创建 strings.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">Coroutine Demo</string>

    <!-- 协程基础 -->
    <string name="basics_title">一、协程基础</string>
    <string name="suspend_function">suspend 函数</string>
    <string name="suspend_function_desc">挂起函数的原理与使用</string>
    <string name="launch_async">launch 与 async</string>
    <string name="launch_async_desc">协程启动方式对比</string>
    <string name="dispatchers">Dispatchers 调度器</string>
    <string name="dispatchers_desc">四种调度器的使用场景</string>
    <string name="job">Job 与协程控制</string>
    <string name="job_desc">Job 状态、取消与等待</string>
    <string name="coroutine_scope">CoroutineScope 作用域</string>
    <string name="coroutine_scope_desc">结构化并发与作用域管理</string>

    <!-- Flow -->
    <string name="flow_title">二、Kotlin Flow</string>
    <string name="flow_basics">Flow 基础</string>
    <string name="flow_basics_desc">Flow 构建器与 collect</string>
    <string name="flow_operators">Flow 操作符</string>
    <string name="flow_operators_desc">map/filter/transform/combine</string>
    <string name="state_flow">StateFlow</string>
    <string name="state_flow_desc">状态流与 UI 状态管理</string>
    <string name="cold_hot_flow">冷流与热流</string>
    <string name="cold_hot_flow_desc">Flow、SharedFlow、StateFlow 对比</string>

    <!-- Channel -->
    <string name="channel_title">三、Channel 通道</string>
    <string name="channel_basics">Channel 基础</string>
    <string name="channel_basics_desc">Channel 的创建与使用</string>
    <string name="produce_consume">produce 与 consume</string>
    <string name="produce_consume_desc">生产者消费者模式</string>
    <string name="select_expression">select 表达式</string>
    <string name="select_expression_desc">多路复用与选择</string>

    <!-- 异常处理 -->
    <string name="error_title">四、异常处理</string>
    <string name="try_catch">try/catch 异常捕获</string>
    <string name="try_catch_desc">协程中的异常捕获</string>
    <string name="exception_handler">CoroutineExceptionHandler</string>
    <string name="exception_handler_desc">全局异常处理器</string>
    <string name="supervisor_job">SupervisorJob</string>
    <string name="supervisor_job_desc">子协程独立失败</string>

    <!-- Android 集成 -->
    <string name="android_title">五、Android 集成</string>
    <string name="lifecycle_scope">lifecycleScope</string>
    <string name="lifecycle_scope_desc">生命周期感知的协程</string>
    <string name="viewmodel_scope">viewModelScope</string>
    <string name="viewmodel_scope_desc">ViewModel 协程作用域</string>
    <string name="collect_flow">Flow 收集最佳实践</string>
    <string name="collect_flow_desc">repeatOnLifecycle 与 flowWithLifecycle</string>

    <!-- 进阶原理 -->
    <string name="advanced_title">六、进阶原理</string>
    <string name="continuation">Continuation 原理</string>
    <string name="continuation_desc">挂起与恢复机制</string>
    <string name="state_machine">状态机原理</string>
    <string name="state_machine_desc">协程的编译器转换</string>
    <string name="custom_scope">自定义 CoroutineScope</string>
    <string name="custom_scope_desc">创建自定义作用域</string>
    <string name="room_example">Room + 协程</string>
    <string name="room_example_desc">数据库协程操作</string>

    <!-- 测试 -->
    <string name="testing_title">七、协程测试</string>
    <string name="test_dispatcher">TestDispatcher</string>
    <string name="test_dispatcher_desc">测试调度器</string>
    <string name="time_control">时间控制</string>
    <string name="time_control_desc">虚拟时间与 delay 控制</string>
    <string name="flow_test">Flow 测试</string>
    <string name="flow_test_desc">测试 Flow 发射与收集</string>
</resources>
```

**Step 3: 创建 colors.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="purple_200">#FFBB86FC</color>
    <color name="purple_500">#FF6200EE</color>
    <color name="purple_700">#FF3700B3</color>
    <color name="teal_200">#FF03DAC5</color>
    <color name="teal_700">#FF018786</color>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>
    <color name="gray">#FF888888</color>
    <color name="gray_light">#FFF5F5F5</color>
    <color name="divider">#FFE0E0E0</color>
</resources>
```

**Step 4: 创建 MainActivity.kt**

```kotlin
package com.peter.coroutine.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.coroutine.demo.databinding.ActivityMainBinding

/**
 * Kotlin 协程 Demo 主入口
 *
 * 本 Demo 从基础到高级，全面介绍 Kotlin 协程在 Android 开发中的应用。
 *
 * ## 内容概览
 *
 * ### 一、协程基础
 * 1. suspend 函数 - 挂起函数的原理与使用
 * 2. launch 与 async - 协程启动方式对比
 * 3. Dispatchers - 四种调度器的使用场景
 * 4. Job - 协程状态、取消与等待
 * 5. CoroutineScope - 结构化并发与作用域管理
 *
 * ### 二、Kotlin Flow
 * 1. Flow 基础 - Flow 构建器与 collect
 * 2. Flow 操作符 - map/filter/transform/combine
 * 3. StateFlow - 状态流与 UI 状态管理
 * 4. 冷流与热流 - Flow、SharedFlow、StateFlow 对比
 *
 * ### 三、Channel 通道
 * 1. Channel 基础 - Channel 的创建与使用
 * 2. produce 与 consume - 生产者消费者模式
 * 3. select 表达式 - 多路复用与选择
 *
 * ### 四、异常处理
 * 1. try/catch - 协程中的异常捕获
 * 2. CoroutineExceptionHandler - 全局异常处理器
 * 3. SupervisorJob - 子协程独立失败
 *
 * ### 五、Android 集成
 * 1. lifecycleScope - 生命周期感知的协程
 * 2. viewModelScope - ViewModel 协程作用域
 * 3. Flow 收集 - repeatOnLifecycle 与 flowWithLifecycle
 *
 * ### 六、进阶原理
 * 1. Continuation - 挂起与恢复机制
 * 2. 状态机 - 协程的编译器转换
 * 3. 自定义 Scope - 创建自定义作用域
 * 4. Room + 协程 - 数据库协程操作
 *
 * ### 七、协程测试
 * 1. TestDispatcher - 测试调度器
 * 2. 时间控制 - 虚拟时间与 delay 控制
 * 3. Flow 测试 - 测试 Flow 发射与收集
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = MainAdapter(getMenuItems()) { item ->
                item.intent?.let { startActivity(it) }
            }
        }
    }

    private fun getMenuItems(): List<MenuItem> {
        return listOf(
            // 协程基础
            MenuItem(title = getString(R.string.basics_title), isHeader = true),
            MenuItem(
                title = getString(R.string.suspend_function),
                description = getString(R.string.suspend_function_desc),
                intent = createSuspendFunctionIntent(this)
            ),
            MenuItem(
                title = getString(R.string.launch_async),
                description = getString(R.string.launch_async_desc),
                intent = createLaunchAsyncIntent(this)
            ),
            MenuItem(
                title = getString(R.string.dispatchers),
                description = getString(R.string.dispatchers_desc),
                intent = createDispatchersIntent(this)
            ),
            MenuItem(
                title = getString(R.string.job),
                description = getString(R.string.job_desc),
                intent = createJobIntent(this)
            ),
            MenuItem(
                title = getString(R.string.coroutine_scope),
                description = getString(R.string.coroutine_scope_desc),
                intent = createCoroutineScopeIntent(this)
            ),

            // Flow
            MenuItem(title = getString(R.string.flow_title), isHeader = true),
            MenuItem(
                title = getString(R.string.flow_basics),
                description = getString(R.string.flow_basics_desc),
                intent = createFlowBasicsIntent(this)
            ),
            MenuItem(
                title = getString(R.string.flow_operators),
                description = getString(R.string.flow_operators_desc),
                intent = createFlowOperatorsIntent(this)
            ),
            MenuItem(
                title = getString(R.string.state_flow),
                description = getString(R.string.state_flow_desc),
                intent = createStateFlowIntent(this)
            ),
            MenuItem(
                title = getString(R.string.cold_hot_flow),
                description = getString(R.string.cold_hot_flow_desc),
                intent = createColdHotFlowIntent(this)
            ),

            // Channel
            MenuItem(title = getString(R.string.channel_title), isHeader = true),
            MenuItem(
                title = getString(R.string.channel_basics),
                description = getString(R.string.channel_basics_desc),
                intent = createChannelBasicsIntent(this)
            ),
            MenuItem(
                title = getString(R.string.produce_consume),
                description = getString(R.string.produce_consume_desc),
                intent = createProduceConsumeIntent(this)
            ),
            MenuItem(
                title = getString(R.string.select_expression),
                description = getString(R.string.select_expression_desc),
                intent = createSelectExpressionIntent(this)
            ),

            // 异常处理
            MenuItem(title = getString(R.string.error_title), isHeader = true),
            MenuItem(
                title = getString(R.string.try_catch),
                description = getString(R.string.try_catch_desc),
                intent = createTryCatchIntent(this)
            ),
            MenuItem(
                title = getString(R.string.exception_handler),
                description = getString(R.string.exception_handler_desc),
                intent = createExceptionHandlerIntent(this)
            ),
            MenuItem(
                title = getString(R.string.supervisor_job),
                description = getString(R.string.supervisor_job_desc),
                intent = createSupervisorJobIntent(this)
            ),

            // Android 集成
            MenuItem(title = getString(R.string.android_title), isHeader = true),
            MenuItem(
                title = getString(R.string.lifecycle_scope),
                description = getString(R.string.lifecycle_scope_desc),
                intent = createLifecycleScopeIntent(this)
            ),
            MenuItem(
                title = getString(R.string.viewmodel_scope),
                description = getString(R.string.viewmodel_scope_desc),
                intent = createViewModelScopeIntent(this)
            ),
            MenuItem(
                title = getString(R.string.collect_flow),
                description = getString(R.string.collect_flow_desc),
                intent = createCollectFlowIntent(this)
            ),

            // 进阶原理
            MenuItem(title = getString(R.string.advanced_title), isHeader = true),
            MenuItem(
                title = getString(R.string.continuation),
                description = getString(R.string.continuation_desc),
                intent = createContinuationIntent(this)
            ),
            MenuItem(
                title = getString(R.string.state_machine),
                description = getString(R.string.state_machine_desc),
                intent = createStateMachineIntent(this)
            ),
            MenuItem(
                title = getString(R.string.custom_scope),
                description = getString(R.string.custom_scope_desc),
                intent = createCustomScopeIntent(this)
            ),
            MenuItem(
                title = getString(R.string.room_example),
                description = getString(R.string.room_example_desc),
                intent = createRoomExampleIntent(this)
            ),

            // 测试
            MenuItem(title = getString(R.string.testing_title), isHeader = true),
            MenuItem(
                title = getString(R.string.test_dispatcher),
                description = getString(R.string.test_dispatcher_desc),
                intent = createTestDispatcherIntent(this)
            ),
            MenuItem(
                title = getString(R.string.time_control),
                description = getString(R.string.time_control_desc),
                intent = createTimeControlIntent(this)
            ),
            MenuItem(
                title = getString(R.string.flow_test),
                description = getString(R.string.flow_test_desc),
                intent = createFlowTestIntent(this)
            )
        )
    }
}
```

**Step 5: Commit**

```bash
git add coroutine-demo/
git commit -m "feat: add MainActivity and main layout"
```

---

## Task 4: 创建协程基础模块 (01-Basics)

**Files:**
- Create: `coroutine-demo/src/main/java/com/peter/coroutine/demo/basics/SuspendFunctionActivity.kt`
- Create: `coroutine-demo/src/main/java/com/peter/coroutine/demo/basics/LaunchAsyncActivity.kt`
- Create: `coroutine-demo/src/main/java/com/peter/coroutine/demo/basics/DispatchersActivity.kt`
- Create: `coroutine-demo/src/main/java/com/peter/coroutine/demo/basics/JobActivity.kt`
- Create: `coroutine-demo/src/main/java/com/peter/coroutine/demo/basics/CoroutineScopeActivity.kt`
- Create: Intent helper functions in `MenuItem.kt`
- Update: `AndroidManifest.xml`
- Create: Layout files for each Activity

**Step 1: 创建通用日志布局 activity_log.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true">

    <com.google.android.material.appbar.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <com.google.android.material.appbar.MaterialToolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize" />

    </com.google.android.material.appbar.AppBarLayout>

    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">

        <TextView
            android:id="@+id/tvLog"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:fontFamily="monospace"
            android:padding="16dp"
            android:textSize="12sp" />

    </ScrollView>

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

**Step 2: 创建 SuspendFunctionActivity.kt**

```kotlin
package com.peter.coroutine.demo.basics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.peter.coroutine.demo.databinding.ActivityLogBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ## suspend 函数
 *
 * ### 概念说明
 * `suspend` 是 Kotlin 协程的核心关键字，用于标记可以挂起执行的函数。
 *
 * **什么是挂起？**
 * - 挂起是指协程在执行到某个点时暂停，释放底层线程
 * - 挂起不会阻塞线程，线程可以执行其他任务
 * - 当条件满足时，协程会在挂起点恢复执行
 *
 * **suspend 函数的特点：**
 * 1. 只能在协程中或其他 suspend 函数中调用
 * 2. 函数执行可能被挂起，但不会阻塞线程
 * 3. 挂起和恢复对调用者是透明的
 *
 * ### 关键点
 * - suspend 关键字标记可挂起的函数
 * - delay() 是挂起函数，不会阻塞线程
 * - 挂起函数内部可能不真正挂起（如 delay(0)）
 * - 普通函数不能调用 suspend 函数
 *
 * ### 运行结果
 * 观察日志输出，可以看到：
 * 1. 顺序执行的代码会等待 delay 完成
 * 2. 但 delay 不会阻塞主线程
 */
class SuspendFunctionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogBinding
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = "suspend 函数"

        runExamples()
    }

    private fun log(message: String) {
        logBuilder.append(message).append("\n")
        binding.tvLog.text = logBuilder.toString()
    }

    private fun runExamples() {
        log("=== 示例 1: 基本 suspend 函数 ===\n")

        lifecycleScope.launch {
            log("开始执行协程")
            log("当前线程: ${Thread.currentThread().name}")

            // 调用 suspend 函数
            fetchData()

            log("fetchData 完成")
        }

        // 主线程继续执行
        log("主线程继续执行（协程外）")
    }

    /**
     * 模拟网络请求的 suspend 函数
     * 使用 delay 模拟耗时操作
     */
    private suspend fun fetchData(): String {
        log("  fetchData: 开始获取数据...")
        delay(1000) // 挂起 1 秒，但不阻塞线程
        log("  fetchData: 数据获取完成")
        return "数据结果"
    }
}
```

**Step 3: 创建 LaunchAsyncActivity.kt**

```kotlin
package com.peter.coroutine.demo.basics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.peter.coroutine.demo.databinding.ActivityLogBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ## launch 与 async
 *
 * ### 概念说明
 * `launch` 和 `async` 是启动协程的两种主要方式，它们都返回 Job。
 *
 * **launch:**
 * - 返回 Job，不返回结果
 * - 适用于"发射后不管"的场景
 * - 用于执行不需要返回值的后台任务
 *
 * **async:**
 * - 返回 Deferred<T>，可以获取结果
 * - 通过 await() 获取返回值
 * - 用于需要并行执行并获取结果的场景
 *
 * ### 关键点
 * - launch 返回 Job，async 返回 Deferred
 * - Deferred.await() 会挂起直到结果就绪
 * - async 可以实现并行执行
 * - await() 会重新抛出协程中的异常
 *
 * ### 运行结果
 * 观察 launch 和 async 的执行顺序和返回值处理方式。
 */
class LaunchAsyncActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogBinding
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = "launch 与 async"

        runExamples()
    }

    private fun log(message: String) {
        logBuilder.append(message).append("\n")
        binding.tvLog.text = logBuilder.toString()
    }

    private fun runExamples() {
        lifecycleScope.launch {
            // === 示例 1: launch 基本用法 ===
            log("=== 示例 1: launch 基本用法 ===\n")

            val launchJob = launch {
                log("launch: 开始执行")
                delay(500)
                log("launch: 执行完成")
            }

            log("launch 返回的 Job: $launchJob")
            launchJob.join() // 等待完成
            log("launch Job 已完成\n")

            // === 示例 2: async 基本用法 ===
            log("=== 示例 2: async 基本用法 ===\n")

            val deferred = async {
                log("async: 开始计算")
                delay(500)
                log("async: 计算完成")
                42 // 返回结果
            }

            log("async 返回的 Deferred: $deferred")
            val result = deferred.await() // 获取结果
            log("async 结果: $result\n")

            // === 示例 3: 并行执行 ===
            log("=== 示例 3: 并行执行 ===\n")

            val startTime = System.currentTimeMillis()

            val deferred1 = async {
                delay(1000)
                "结果1"
            }

            val deferred2 = async {
                delay(1000)
                "结果2"
            }

            // 并行等待两个结果
            val result1 = deferred1.await()
            val result2 = deferred2.await()

            val elapsed = System.currentTimeMillis() - startTime
            log("结果1: $result1, 结果2: $result2")
            log("总耗时: ${elapsed}ms（并行执行，约1秒）")
        }
    }
}
```

**Step 4: 创建 DispatchersActivity.kt**

```kotlin
package com.peter.coroutine.demo.basics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.peter.coroutine.demo.databinding.ActivityDispatchersBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ## Dispatchers 调度器
 *
 * ### 概念说明
 * 调度器决定协程在哪个线程上执行。Kotlin 提供了四种主要调度器：
 *
 * **Dispatchers.Default:**
 * - CPU 密集型任务
 * - 使用共享线程池，线程数约等于 CPU 核心数
 * - 适合排序、过滤、计算等
 *
 * **Dispatchers.IO:**
 * - I/O 密集型任务
 * - 按需创建线程，可以扩展
 * - 适合网络请求、文件读写、数据库操作
 *
 * **Dispatchers.Main:**
 * - Android 主线程
 * - 用于 UI 更新
 * - 需要添加依赖才能使用
 *
 * **Dispatchers.Unconfined:**
 * - 不限制执行线程
 * - 在调用者线程启动，挂起后在恢复线程继续
 * - 一般不推荐使用
 *
 * ### 关键点
 * - withContext 可以切换调度器
 * - Default 和 IO 会共享线程池
 * - Main 调度器用于 UI 操作
 *
 * ### 运行结果
 * 点击按钮观察不同调度器执行的线程。
 */
class DispatchersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDispatchersBinding
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDispatchersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = "Dispatchers 调度器"

        setupButtons()
    }

    private fun log(message: String) {
        logBuilder.append(message).append("\n")
        binding.tvLog.text = logBuilder.toString()
    }

    private fun clearLog() {
        logBuilder.clear()
        binding.tvLog.text = ""
    }

    private fun setupButtons() {
        binding.btnDefault.setOnClickListener {
            clearLog()
            testDefault()
        }

        binding.btnIO.setOnClickListener {
            clearLog()
            testIO()
        }

        binding.btnMain.setOnClickListener {
            clearLog()
            testMain()
        }

        binding.btnSwitch.setOnClickListener {
            clearLog()
            testSwitchContext()
        }
    }

    private fun testDefault() {
        log("=== Dispatchers.Default ===\n")

        lifecycleScope.launch(Dispatchers.Default) {
            log("执行线程: ${Thread.currentThread().name}")
            log("适合 CPU 密集型任务：排序、计算、JSON 解析等")
        }
    }

    private fun testIO() {
        log("=== Dispatchers.IO ===\n")

        lifecycleScope.launch(Dispatchers.IO) {
            log("执行线程: ${Thread.currentThread().name}")
            log("适合 I/O 密集型任务：网络请求、文件读写、数据库操作")
        }
    }

    private fun testMain() {
        log("=== Dispatchers.Main ===\n")

        lifecycleScope.launch(Dispatchers.Main) {
            log("执行线程: ${Thread.currentThread().name}")
            log("用于 UI 更新，可以安全操作 View")
        }
    }

    private fun testSwitchContext() {
        log("=== withContext 切换调度器 ===\n")

        lifecycleScope.launch(Dispatchers.Main) {
            log("开始: ${Thread.currentThread().name}")

            // 切换到 IO 线程执行耗时操作
            val result = withContext(Dispatchers.IO) {
                log("IO 操作: ${Thread.currentThread().name}")
                delay(500)
                "数据结果"
            }

            log("回到: ${Thread.currentThread().name}")
            log("结果: $result")
        }
    }
}
```

**Step 5: 创建 activity_dispatchers.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true">

    <com.google.android.material.appbar.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <com.google.android.material.appbar.MaterialToolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize" />

    </com.google.android.material.appbar.AppBarLayout>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:padding="8dp">

            <Button
                android:id="@+id/btnDefault"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:layout_margin="4dp"
                android:text="Default"
                android:textAllCaps="false"
                android:textSize="12sp" />

            <Button
                android:id="@+id/btnIO"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:layout_margin="4dp"
                android:text="IO"
                android:textAllCaps="false"
                android:textSize="12sp" />

            <Button
                android:id="@+id/btnMain"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:layout_margin="4dp"
                android:text="Main"
                android:textAllCaps="false"
                android:textSize="12sp" />

            <Button
                android:id="@+id/btnSwitch"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:layout_margin="4dp"
                android:text="切换"
                android:textAllCaps="false"
                android:textSize="12sp" />

        </LinearLayout>

        <ScrollView
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="1">

            <TextView
                android:id="@+id/tvLog"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:fontFamily="monospace"
                android:padding="16dp"
                android:textSize="12sp" />

        </ScrollView>

    </LinearLayout>

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

**Step 6: 创建 JobActivity.kt**

```kotlin
package com.peter.coroutine.demo.basics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.peter.coroutine.demo.databinding.ActivityJobBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ## Job 与协程控制
 *
 * ### 概念说明
 * Job 是协程的句柄，用于控制协程的生命周期。
 *
 * **Job 状态:**
 * - New: 新建
 * - Active: 活跃（执行中）
 * - Completing: 正在完成
 * - Completed: 已完成
 * - Cancelling: 正在取消
 * - Cancelled: 已取消
 * - Failed: 失败
 *
 * **常用方法:**
 * - join(): 等待协程完成
 * - cancel(): 取消协程
 * - cancelAndJoin(): 取消并等待
 *
 * ### 关键点
 * - Job 可以有父子关系
 * - 取消父 Job 会取消所有子 Job
 * - isActive 属性检查是否活跃
 *
 * ### 运行结果
 * 点击按钮观察 Job 的取消和等待行为。
 */
class JobActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJobBinding
    private val logBuilder = StringBuilder()
    private var countingJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = "Job 与协程控制"

        setupButtons()
    }

    private fun log(message: String) {
        logBuilder.append(message).append("\n")
        binding.tvLog.text = logBuilder.toString()
    }

    private fun clearLog() {
        logBuilder.clear()
        binding.tvLog.text = ""
    }

    private fun setupButtons() {
        binding.btnStart.setOnClickListener {
            clearLog()
            startCounting()
        }

        binding.btnCancel.setOnClickListener {
            cancelCounting()
        }

        binding.btnJoin.setOnClickListener {
            testJoin()
        }

        binding.btnStates.setOnClickListener {
            clearLog()
            showJobStates()
        }
    }

    private fun startCounting() {
        log("=== 启动计数协程 ===\n")

        countingJob = lifecycleScope.launch(Dispatchers.Default) {
            var count = 0
            while (isActive) { // 检查协程是否仍然活跃
                log("计数: ${count++}")
                delay(500)
            }
            log("协程被取消")
        }

        log("Job 状态: ${countingJob?.isActive}")
    }

    private fun cancelCounting() {
        log("\n=== 取消协程 ===")

        lifecycleScope.launch {
            countingJob?.cancelAndJoin()
            log("协程已取消并等待完成")
            log("Job 状态: isActive=${countingJob?.isActive}, isCancelled=${countingJob?.isCancelled}")
        }
    }

    private fun testJoin() {
        clearLog()
        log("=== join() 等待完成 ===\n")

        lifecycleScope.launch {
            val job = launch {
                log("子协程开始")
                delay(1000)
                log("子协程完成")
            }

            log("等待子协程...")
            job.join()
            log("子协程已结束，继续执行")
        }
    }

    private fun showJobStates() {
        log("=== Job 状态流转 ===\n")

        lifecycleScope.launch {
            val job = launch {
                log("Job isActive: $isActive")
                log("Job isCancelled: $isCancelled")
                log("Job isCompleted: $isCompleted")
                delay(100)
            }

            log("启动后 isActive: ${job.isActive}")
            log("启动后 isCancelled: ${job.isCancelled}")
            log("启动后 isCompleted: ${job.isCompleted}")

            job.join()

            log("\n完成后 isActive: ${job.isActive}")
            log("完成后 isCancelled: ${job.isCancelled}")
            log("完成后 isCompleted: ${job.isCompleted}")
        }
    }
}
```

**Step 7: 创建 activity_job.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true">

    <com.google.android.material.appbar.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <com.google.android.material.appbar.MaterialToolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize" />

    </com.google.android.material.appbar.AppBarLayout>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:padding="8dp">

            <Button
                android:id="@+id/btnStart"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:layout_margin="4dp"
                android:text="启动"
                android:textSize="12sp" />

            <Button
                android:id="@+id/btnCancel"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:layout_margin="4dp"
                android:text="取消"
                android:textSize="12sp" />

            <Button
                android:id="@+id/btnJoin"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:layout_margin="4dp"
                android:text="join"
                android:textAllCaps="false"
                android:textSize="12sp" />

            <Button
                android:id="@+id/btnStates"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:layout_margin="4dp"
                android:text="状态"
                android:textSize="12sp" />

        </LinearLayout>

        <ScrollView
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="1">

            <TextView
                android:id="@+id/tvLog"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:fontFamily="monospace"
                android:padding="16dp"
                android:textSize="12sp" />

        </ScrollView>

    </LinearLayout>

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

**Step 8: 创建 CoroutineScopeActivity.kt**

```kotlin
package com.peter.coroutine.demo.basics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.peter.coroutine.demo.databinding.ActivityLogBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ## CoroutineScope 作用域
 *
 * ### 概念说明
 * CoroutineScope 定义了协程的作用域，管理其中所有协程的生命周期。
 *
 * **结构化并发:**
 * - 协程有父子关系
 * - 父协程等待所有子协程完成
 * - 取消父协程会取消所有子协程
 * - 子协程异常会取消父协程（默认行为）
 *
 * **常用作用域:**
 * - CoroutineScope(Dispatchers.Default): 自定义作用域
 * - lifecycleScope: Activity/Fragment 生命周期作用域
 * - viewModelScope: ViewModel 作用域
 * - GlobalScope: 全局作用域（不推荐）
 *
 * ### 关键点
 * - scope.launch 创建的协程都是该 scope 的子协程
 * - scope.cancel() 会取消所有子协程
 * - 结构化并发避免协程泄漏
 *
 * ### 运行结果
 * 观察父子协程的关系和取消传播。
 */
class CoroutineScopeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogBinding
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = "CoroutineScope 作用域"

        runExamples()
    }

    private fun log(message: String) {
        logBuilder.append(message).append("\n")
        binding.tvLog.text = logBuilder.toString()
    }

    private fun runExamples() {
        log("=== 结构化并发示例 ===\n")

        // 创建自定义作用域
        val customScope = CoroutineScope(Dispatchers.Default)

        log("启动父协程")
        val parentJob = customScope.launch {
            log("父协程开始: ${coroutineContext[Job]}")

            // 启动子协程 1
            launch {
                log("子协程 1 开始")
                delay(2000)
                log("子协程 1 完成")
            }

            // 启动子协程 2
            launch {
                log("子协程 2 开始")
                delay(1500)
                log("子协程 2 完成")
            }

            // 启动子协程 3
            launch {
                log("子协程 3 开始")
                delay(1000)
                log("子协程 3 完成")
            }

            log("父协程等待子协程...")
        }

        // 模拟取消
        customScope.launch {
            delay(500)
            log("\n=== 取消父协程 ===")
            parentJob.cancel()

            delay(100)
            log("父协程状态: isCancelled=${parentJob.isCancelled}")
            log("所有子协程也会被取消")

            // 清理
            customScope.cancel()
        }
    }
}
```

**Step 9: 添加 Intent 工具函数到 MenuItem.kt**

在 `MenuItem.kt` 末尾添加:

```kotlin
// === 协程基础 ===
fun createSuspendFunctionIntent(context: Context) = Intent(context, SuspendFunctionActivity::class.java)
fun createLaunchAsyncIntent(context: Context) = Intent(context, LaunchAsyncActivity::class.java)
fun createDispatchersIntent(context: Context) = Intent(context, DispatchersActivity::class.java)
fun createJobIntent(context: Context) = Intent(context, JobActivity::class.java)
fun createCoroutineScopeIntent(context: Context) = Intent(context, CoroutineScopeActivity::class.java)
```

**Step 10: 更新 AndroidManifest.xml**

添加 Activity 声明:

```xml
<!-- 协程基础 -->
<activity android:name=".basics.SuspendFunctionActivity" />
<activity android:name=".basics.LaunchAsyncActivity" />
<activity android:name=".basics.DispatchersActivity" />
<activity android:name=".basics.JobActivity" />
<activity android:name=".basics.CoroutineScopeActivity" />
```

**Step 11: 验证编译**

```bash
./gradlew :coroutine-demo:assembleDebug
```

**Step 12: Commit**

```bash
git add coroutine-demo/
git commit -m "feat(coroutine-demo): add basics module - suspend, launch/async, dispatchers, job, scope"
```

---

## Task 5: 创建 Flow 模块 (02-Flow)

**Files:**
- Create: `coroutine-demo/src/main/java/com/peter/coroutine/demo/flow/FlowBasicsActivity.kt`
- Create: `coroutine-demo/src/main/java/com/peter/coroutine/demo/flow/FlowOperatorsActivity.kt`
- Create: `coroutine-demo/src/main/java/com/peter/coroutine/demo/flow/StateFlowActivity.kt`
- Create: `coroutine-demo/src/main/java/com/peter/coroutine/demo/flow/ColdHotFlowActivity.kt`
- Create: Layout files and update manifest

(详细代码省略，遵循相同模式)

---

## Task 6: 创建 Channel 模块 (03-Channel)

**Files:**
- Create: Channel 相关 Activity
- Update manifest and Intent functions

---

## Task 7: 创建异常处理模块 (04-ErrorHandling)

**Files:**
- Create: TryCatchActivity, ExceptionHandlerActivity, SupervisorJobActivity
- Update manifest

---

## Task 8: 创建 Android 集成模块 (05-Android)

**Files:**
- Create: LifecycleScopeActivity, ViewModelScopeActivity, CollectFlowActivity
- Create: ViewModel classes
- Update manifest

---

## Task 9: 创建进阶原理模块 (06-Advanced)

**Files:**
- Create: ContinuationActivity, StateMachineActivity, CustomScopeActivity, RoomExampleActivity
- Create: Room database classes
- Update manifest

---

## Task 10: 创建测试模块 (07-Testing)

**Files:**
- Create: test/java/.../testing/BasicsTest.kt
- Create: test/java/.../testing/FlowTest.kt
- Create: test/java/.../testing/TestDispatcherTest.kt

---

## Task 11: 创建文档

**Files:**
- Create: `coroutine-demo/README.md`
- Create: `coroutine-demo/docs/01-basics.md`
- Create: `coroutine-demo/docs/02-flow.md`
- Create: `coroutine-demo/docs/03-channel.md`
- Create: `coroutine-demo/docs/04-error-handling.md`
- Create: `coroutine-demo/docs/05-android-integration.md`
- Create: `coroutine-demo/docs/06-advanced.md`
- Create: `coroutine-demo/docs/07-testing.md`

---

## Task 12: 最终验证和提交

**Step 1: 完整编译**

```bash
./gradlew :coroutine-demo:assembleDebug
```

**Step 2: 运行测试**

```bash
./gradlew :coroutine-demo:test
```

**Step 3: 最终提交**

```bash
git add .
git commit -m "feat: complete coroutine demo with docs"
```

---

## 文件清单总览

| 类型 | 文件数 | 说明 |
|------|--------|------|
| Kotlin 文件 | ~30 | Activity + ViewModel + Data |
| XML 布局 | ~20 | 各 Activity 布局 |
| 文档 | 8 | README + 7 个模块文档 |
| 配置 | 3 | build.gradle.kts, AndroidManifest, proguard |

## 注意事项

1. Room 使用 annotationProcessor 而非 KSP（简化配置）
2. 所有示例使用 lifecycleScope 作为协程入口
3. 日志输出使用 monospace 字体便于阅读
4. 每个示例都有完整的 KDoc 注释
