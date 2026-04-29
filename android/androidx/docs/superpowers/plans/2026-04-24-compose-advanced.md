# Compose Advanced Demos Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add 8 new Activity demos to compose-demo covering state restoration, SnapshotFlow, infinite/physics animations, GraphicsLayer shaders, Compose stability, AnnotatedString, Scaffold advanced patterns, and custom Modifier.Node.

**Architecture:** Each demo is a standalone Activity following the existing pattern: `ComponentActivity` + `enableEdgeToEdge()` + `MaterialTheme { Scaffold { ... } }`. Screen composables use `Column(verticalScroll)` with section composables in `Card` containers. No new dependencies required — all APIs are in the existing Compose BOM.

**Tech Stack:** Jetpack Compose (BOM 2026.03.01), Material3, Kotlin 2.2.21

---

### Task 1: StateRestoreActivity — rememberSaveable & State Restoration

**Files:**
- Create: `compose-demo/src/main/java/com/peter/compose/demo/level3/StateRestoreActivity.kt`
- Modify: `compose-demo/src/main/AndroidManifest.xml`
- Modify: `compose-demo/src/main/java/com/peter/compose/demo/MainActivity.kt`

- [ ] **Step 1: Create StateRestoreActivity.kt**

```kotlin
package com.peter.compose.demo.level3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * StateRestoreActivity - 状态恢复
 *
 * 学习目标：
 * 1. remember vs rememberSaveable 的区别
 * 2. 自定义 Saver 对象
 * 3. mapSaver / listSaver 快捷方式
 * 4. 进程死亡后的状态恢复
 */
class StateRestoreActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    StateRestoreScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

/**
 * 自定义数据类，用于演示自定义 Saver
 */
data class UserProfile(
    val name: String,
    val age: Int,
    val city: String
)

@Composable
fun StateRestoreScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "状态恢复 (rememberSaveable)",
            style = MaterialTheme.typography.headlineMedium
        )

        RememberVsRememberSaveableSection()
        CustomSaverSection()
        MapSaverSection()
        ListSaverSection()
    }
}

/**
 * 对比 remember vs rememberSaveable
 * 旋转设备后，remember 的值会丢失，rememberSaveable 的值会保留
 */
@Composable
fun RememberVsRememberSaveableSection() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "remember vs rememberSaveable",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "旋转设备观察：remember 的计数器会重置，rememberSaveable 不会",
                style = MaterialTheme.typography.bodyMedium
            )

            // remember — 旋转后丢失
            var rememberCount by remember { mutableIntStateOf(0) }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("remember: $rememberCount", style = MaterialTheme.typography.bodyLarge)
                Button(onClick = { rememberCount++ }) {
                    Text("+1")
                }
            }

            // rememberSaveable — 旋转后保留
            var saveableCount by rememberSaveable { mutableIntStateOf(0) }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("rememberSaveable: $saveableCount", style = MaterialTheme.typography.bodyLarge)
                Button(onClick = { saveableCount++ }) {
                    Text("+1")
                }
            }

            Text(
                text = """// remember — 不保存到 Bundle
var count by remember { mutableIntStateOf(0) }

// rememberSaveable — 保存到 Bundle
var count by rememberSaveable { mutableIntStateOf(0) }""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

/**
 * 自定义 Saver — 复杂对象的保存与恢复
 * 当对象不是基本类型或 data class 时，需要自定义 Saver
 */
@Composable
fun CustomSaverSection() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "自定义 Saver",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "对于非基本类型，需要自定义 Saver 来定义序列化逻辑",
                style = MaterialTheme.typography.bodyMedium
            )

            val userProfileSaver = Saver<UserProfile, Bundle>(
                save = { profile ->
                    Bundle().apply {
                        putString("name", profile.name)
                        putInt("age", profile.age)
                        putString("city", profile.city)
                    }
                },
                restore = { bundle ->
                    UserProfile(
                        name = bundle.getString("name", ""),
                        age = bundle.getInt("age"),
                        city = bundle.getString("city", "")
                    )
                }
            )

            var profile by rememberSaveable(stateSaver = userProfileSaver) {
                mutableStateOf(UserProfile("张三", 25, "北京"))
            }

            Text("姓名: ${profile.name}", style = MaterialTheme.typography.bodyLarge)
            Text("年龄: ${profile.age}", style = MaterialTheme.typography.bodyLarge)
            Text("城市: ${profile.city}", style = MaterialTheme.typography.bodyLarge)

            Button(onClick = {
                profile = UserProfile("李四", 30, "上海")
            }) {
                Text("切换用户")
            }

            Text(
                text = """val saver = Saver<UserProfile, Bundle>(
    save = { bundle -> ... },
    restore = { bundle -> UserProfile(...) }
)
var profile by rememberSaveable(stateSaver = saver) {
    mutableStateOf(UserProfile("张三", 25, "北京"))
}""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

/**
 * mapSaver — 基于 Map 的快捷 Saver
 */
@Composable
fun MapSaverSection() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "mapSaver 快捷方式",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            val profileMapSaver = mapSaver<UserProfile>(
                save = { mapOf("name" to it.name, "age" to it.age, "city" to it.city) },
                restore = { map ->
                    UserProfile(
                        name = map["name"] as String,
                        age = map["age"] as Int,
                        city = map["city"] as String
                    )
                }
            )

            var profile by rememberSaveable(stateSaver = profileMapSaver) {
                mutableStateOf(UserProfile("王五", 28, "深圳"))
            }

            Text("${profile.name} · ${profile.age}岁 · ${profile.city}")

            Button(onClick = {
                profile = UserProfile("赵六", 35, "杭州")
            }) {
                Text("切换用户")
            }

            Text(
                text = """val saver = mapSaver<UserProfile>(
    save = { mapOf("name" to it.name, ...) },
    restore = { map -> UserProfile(...) }
)""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

/**
 * listSaver — 基于 List 的快捷 Saver
 */
@Composable
fun ListSaverSection() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "listSaver 快捷方式",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            val profileListSaver = listSaver<UserProfile, Any>(
                save = { listOf(it.name, it.age, it.city) },
                restore = { list ->
                    UserProfile(
                        name = list[0] as String,
                        age = list[1] as Int,
                        city = list[2] as String
                    )
                }
            )

            var profile by rememberSaveable(stateSaver = profileListSaver) {
                mutableStateOf(UserProfile("孙七", 22, "成都"))
            }

            Text("${profile.name} · ${profile.age}岁 · ${profile.city}")

            Button(onClick = {
                profile = UserProfile("周八", 40, "广州")
            }) {
                Text("切换用户")
            }

            Text(
                text = """val saver = listSaver<UserProfile, Any>(
    save = { listOf(it.name, it.age, it.city) },
    restore = { list -> UserProfile(...) }
)""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StateRestoreScreenPreview() {
    MaterialTheme {
        StateRestoreScreen()
    }
}
```

- [ ] **Step 2: Register in AndroidManifest.xml**

Add after the existing Level 3 activities (after `DependencyInjectionActivity`):

```xml
        <activity
            android:name=".level3.StateRestoreActivity"
            android:exported="false"
            android:theme="@style/Theme.Compose" />
```

- [ ] **Step 3: Add to MainActivity getDemoLevels()**

Add to the Level 3 "状态与交互" `items` list (after the "手势处理" DemoItem):

```kotlin
                DemoItem(
                    title = "状态恢复",
                    description = "rememberSaveable, 自定义 Saver",
                    activityClass = com.peter.compose.demo.level3.StateRestoreActivity::class.java,
                    color = Color(0xFFAD1457)
                ),
```

- [ ] **Step 4: Build and verify**

Run: `./gradlew :compose-demo:assembleDebug`
Expected: BUILD SUCCESSFUL

---

### Task 2: SnapshotFlowActivity — SnapshotFlow & State-to-Flow Bridge

**Files:**
- Create: `compose-demo/src/main/java/com/peter/compose/demo/level3/SnapshotFlowActivity.kt`
- Modify: `compose-demo/src/main/AndroidManifest.xml`
- Modify: `compose-demo/src/main/java/com/peter/compose/demo/MainActivity.kt`

- [ ] **Step 1: Create SnapshotFlowActivity.kt**

```kotlin
package com.peter.compose.demo.level3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

/**
 * SnapshotFlowActivity - SnapshotFlow 与状态桥接
 *
 * 学习目标：
 * 1. snapshotFlow: 将 Compose State 转为 Kotlin Flow
 * 2. 监听滚动位置变化
 * 3. Flow.collectAsState 桥接
 * 4. debounce 搜索输入
 */
class SnapshotFlowActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    SnapshotFlowScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(FlowPreview::class)
@Composable
fun SnapshotFlowScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "SnapshotFlow",
            style = MaterialTheme.typography.headlineMedium
        )

        SnapshotFlowBasicsSection()
        ScrollToTopSection()
        DebounceSearchSection()
    }
}

/**
 * SnapshotFlow 基础用法
 * 将 Compose 的 State 转换为 Kotlin Flow，可以在 LaunchedEffect 中收集
 */
@Composable
fun SnapshotFlowBasicsSection() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "snapshotFlow 基础",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "将 Compose State 转为 Flow，可在协程中监听变化",
                style = MaterialTheme.typography.bodyMedium
            )

            var counter by remember { mutableStateOf(0) }
            var changeLog by remember { mutableStateOf("等待变化...") }

            // snapshotFlow 监听 counter 变化
            LaunchedEffect(Unit) {
                snapshotFlow { counter }
                    .distinctUntilChanged()
                    .collect { value ->
                        changeLog = "counter 变为 $value (时间: ${System.currentTimeMillis() % 100000}ms)"
                    }
            }

            Text("counter: $counter", style = MaterialTheme.typography.bodyLarge)
            Text(changeLog, style = MaterialTheme.typography.bodySmall)

            androidx.compose.material3.Button(onClick = { counter++ }) {
                Text("counter++")
            }

            Text(
                text = """LaunchedEffect(Unit) {
    snapshotFlow { counter }
        .distinctUntilChanged()
        .collect { value -> println("值变为: $value") }
}""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

/**
 * 滚动监听 — 显示/隐藏"回到顶部"按钮
 * 使用 snapshotFlow 监听 LazyListState 的滚动位置
 */
@Composable
fun ScrollToTopSection() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "滚动监听 — 回到顶部按钮",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "滚动超过 5 项后显示回到顶部按钮",
                style = MaterialTheme.typography.bodyMedium
            )

            val listState = rememberLazyListState()
            val scope = rememberCoroutineScope()

            // 使用 snapshotFlow 监听滚动位置
            val showScrollToTop by remember {
                derivedStateOf { listState.firstVisibleItemIndex > 5 }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(50) { index ->
                        Text(
                            text = "Item #$index",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        )
                    }
                }

                if (showScrollToTop) {
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                listState.animateScrollToItem(0)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "回到顶部")
                    }
                }
            }

            Text(
                text = """val showScrollToTop by remember {
    derivedStateOf { listState.firstVisibleItemIndex > 5 }
}""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

/**
 * 防抖搜索 — snapshotFlow + debounce
 * 输入文本后等待 300ms 才触发搜索，避免频繁请求
 */
@OptIn(FlowPreview::class)
@Composable
fun DebounceSearchSection() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "防抖搜索 (debounce)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "输入后等待 300ms 才触发"搜索"，避免每次按键都请求",
                style = MaterialTheme.typography.bodyMedium
            )

            var searchText by remember { mutableStateOf("") }
            var searchResult by remember { mutableStateOf("输入关键词开始搜索") }

            LaunchedEffect(Unit) {
                snapshotFlow { searchText }
                    .debounce(300)
                    .distinctUntilChanged()
                    .filter { it.isNotBlank() }
                    .collect { query ->
                        searchResult = "搜索: \"$query\" → 找到 ${(query.length * 7) % 50 + 1} 条结果"
                    }
            }

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("搜索关键词") },
                modifier = Modifier.fillMaxWidth()
            )

            Text(searchResult, style = MaterialTheme.typography.bodyMedium)

            Text(
                text = """LaunchedEffect(Unit) {
    snapshotFlow { searchText }
        .debounce(300)
        .distinctUntilChanged()
        .filter { it.isNotBlank() }
        .collect { query -> performSearch(query) }
}""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SnapshotFlowScreenPreview() {
    MaterialTheme {
        SnapshotFlowScreen()
    }
}
```

- [ ] **Step 2: Register in AndroidManifest.xml**

Add after StateRestoreActivity:

```xml
        <activity
            android:name=".level3.SnapshotFlowActivity"
            android:exported="false"
            android:theme="@style/Theme.Compose" />
```

- [ ] **Step 3: Add to MainActivity getDemoLevels()**

Add to Level 3 items after the StateRestoreActivity DemoItem:

```kotlin
                DemoItem(
                    title = "SnapshotFlow",
                    description = "State 转 Flow, 防抖搜索",
                    activityClass = com.peter.compose.demo.level3.SnapshotFlowActivity::class.java,
                    color = Color(0xFF880E4F)
                ),
```

- [ ] **Step 4: Build and verify**

Run: `./gradlew :compose-demo:assembleDebug`
Expected: BUILD SUCCESSFUL

---

### Task 3: InfiniteTransitionActivity — Infinite & Physics-based Animations

**Files:**
- Create: `compose-demo/src/main/java/com/peter/compose/demo/level6/InfiniteTransitionActivity.kt`
- Modify: `compose-demo/src/main/AndroidManifest.xml`
- Modify: `compose-demo/src/main/java/com/peter/compose/demo/MainActivity.kt`

- [ ] **Step 1: Create InfiniteTransitionActivity.kt**

```kotlin
package com.peter.compose.demo.level6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * InfiniteTransitionActivity - 无限动画与物理动画
 *
 * 学习目标：
 * 1. rememberInfiniteTransition: 旋转、脉冲、颜色循环
 * 2. spring(): 弹簧动画的 dampingRatio / stiffness 参数
 * 3. animateDecay: 惯性衰减动画
 * 4. 手势驱动动画: 拖拽 + 释放后弹簧回弹
 */
class InfiniteTransitionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    InfiniteTransitionScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun InfiniteTransitionScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "无限动画 & 物理动画",
            style = MaterialTheme.typography.headlineMedium
        )

        InfiniteTransitionBasics()
        SpringAnimationDemo()
        GestureSpringDemo()
    }
}

/**
 * InfiniteTransition 基础 — 旋转、脉冲、颜色循环
 */
@Composable
fun InfiniteTransitionBasics() {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "InfiniteTransition",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "无限循环动画：旋转加载、脉冲呼吸、颜色渐变",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 旋转动画
                val rotation by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = LinearEasing)
                    ),
                    label = "rotation"
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .rotate(rotation)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(8.dp)
                            )
                    )
                    Text("旋转", style = MaterialTheme.typography.bodySmall)
                }

                // 脉冲动画
                val scale by infiniteTransition.animateFloat(
                    initialValue = 0.8f,
                    targetValue = 1.2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulse"
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .scale(scale)
                            .background(
                                MaterialTheme.colorScheme.secondary,
                                CircleShape
                            )
                    )
                    Text("脉冲", style = MaterialTheme.typography.bodySmall)
                }

                // 颜色循环
                val colorFraction by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(3000, easing = LinearEasing)
                    ),
                    label = "color"
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                Color(
                                    red = colorFraction,
                                    green = 1f - colorFraction,
                                    blue = 0.5f
                                ),
                                CircleShape
                            )
                    )
                    Text("颜色", style = MaterialTheme.typography.bodySmall)
                }
            }

            Text(
                text = """val infiniteTransition = rememberInfiniteTransition()
val rotation by infiniteTransition.animateFloat(
    initialValue = 0f, targetValue = 360f,
    animationSpec = infiniteRepeatable(
        animation = tween(2000, easing = LinearEasing)
    )
)""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

/**
 * spring() 弹簧动画 — dampingRatio / stiffness 参数对比
 * 可通过 Slider 交互调节参数
 */
@Composable
fun SpringAnimationDemo() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "spring() 弹簧动画",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "调节 dampingRatio（阻尼比）观察弹性差异",
                style = MaterialTheme.typography.bodyMedium
            )

            var dampingRatio by remember { mutableFloatStateOf(Spring.DampingRatioMediumBouncy) }
            var targetState by remember { mutableStateOf(false) }

            Text("dampingRatio: %.2f".format(dampingRatio), style = MaterialTheme.typography.bodySmall)

            Slider(
                value = dampingRatio,
                onValueChange = { dampingRatio = it },
                valueRange = 0.1f..1.5f,
                modifier = Modifier.fillMaxWidth()
            )

            val offsetX by animateFloatAsState(
                targetValue = if (targetState) 200f else 0f,
                animationSpec = spring(
                    dampingRatio = dampingRatio,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "spring"
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(offsetX.roundToInt(), 0) }
                        .size(50.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
            }

            androidx.compose.material3.Button(onClick = { targetState = !targetState }) {
                Text(if (targetState) "弹回左侧" else "弹到右侧")
            }

            Text(
                text = """animateFloatAsState(
    targetValue = if (target) 200f else 0f,
    animationSpec = spring(
        dampingRatio = dampingRatio,  // 0.1=弹性, 1.0=临界
        stiffness = Spring.StiffnessMedium
    )
)""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

/**
 * 手势驱动弹簧动画 — 拖拽 + 释放后弹簧回弹
 */
@Composable
fun GestureSpringDemo() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "手势驱动弹簧动画",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "拖拽圆形，释放后弹簧回弹到原位",
                style = MaterialTheme.typography.bodyMedium
            )

            val scope = rememberCoroutineScope()
            var offsetX by remember { mutableFloatStateOf(0f) }
            var offsetY by remember { mutableFloatStateOf(0f) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                        .size(80.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    offsetX += dragAmount.x
                                    offsetY += dragAmount.y
                                },
                                onDragEnd = {
                                    // 释放后弹簧回弹
                                    scope.launch {
                                        animateDecay(
                                            initialValue = offsetX,
                                            initialVelocity = 0f,
                                            animationSpec = androidx.compose.animation.core.exponentialDecay(
                                                frictionMultiplier = 2f
                                            )
                                        ) { value, _ ->
                                            offsetX = value
                                        }
                                    }
                                    scope.launch {
                                        animateDecay(
                                            initialValue = offsetY,
                                            initialVelocity = 0f,
                                            animationSpec = androidx.compose.animation.core.exponentialDecay(
                                                frictionMultiplier = 2f
                                            )
                                        ) { value, _ ->
                                            offsetY = value
                                        }
                                    }
                                }
                            )
                        }
                )
            }

            Text(
                text = """Modifier.pointerInput(Unit) {
    detectDragGestures(
        onDrag = { change, dragAmount ->
            offsetX += dragAmount.x
            offsetY += dragAmount.y
        },
        onDragEnd = {
            scope.launch {
                animateDecay(offsetX, 0f, exponentialDecay()) { value, _ ->
                    offsetX = value
                }
            }
        }
    )
}""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InfiniteTransitionScreenPreview() {
    MaterialTheme {
        InfiniteTransitionScreen()
    }
}
```

- [ ] **Step 2: Register in AndroidManifest.xml**

Add after the existing Level 6 activity:

```xml
        <activity
            android:name=".level6.InfiniteTransitionActivity"
            android:exported="false"
            android:theme="@style/Theme.Compose" />
```

- [ ] **Step 3: Add to MainActivity getDemoLevels()**

Add to Level 6 "动画与图形" items after "Canvas 绘制":

```kotlin
                DemoItem(
                    title = "无限 & 物理动画",
                    description = "InfiniteTransition, spring, animateDecay",
                    activityClass = com.peter.compose.demo.level6.InfiniteTransitionActivity::class.java,
                    color = Color(0xFF1A237E)
                ),
```

- [ ] **Step 4: Build and verify**

Run: `./gradlew :compose-demo:assembleDebug`
Expected: BUILD SUCCESSFUL

---

### Task 4: GraphicsLayerActivity — RenderEffects & Shaders

**Files:**
- Create: `compose-demo/src/main/java/com/peter/compose/demo/level6/GraphicsLayerActivity.kt`
- Modify: `compose-demo/src/main/AndroidManifest.xml`
- Modify: `compose-demo/src/main/java/com/peter/compose/demo/MainActivity.kt`

- [ ] **Step 1: Create GraphicsLayerActivity.kt**

```kotlin
package com.peter.compose.demo.level6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.graphics.RenderEffect as AndroidRenderEffect

/**
 * GraphicsLayerActivity - GraphicsLayer 与视觉效果
 *
 * 学习目标：
 * 1. GraphicsLayer 基础 — renderEffect
 * 2. RenderEffect.createBlurEffect — 实时模糊
 * 3. drawBehind 自定义绘制
 * 4. 实战：毛玻璃效果卡片
 */
class GraphicsLayerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    GraphicsLayerScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun GraphicsLayerScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "GraphicsLayer & 视觉效果",
            style = MaterialTheme.typography.headlineMedium
        )

        BlurEffectDemo()
        FrostedGlassDemo()
        CustomDrawDemo()
    }
}

/**
 * 模糊效果 — RenderEffect.createBlurEffect
 * 通过 Slider 调节模糊半径
 */
@Composable
fun BlurEffectDemo() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "模糊效果 (BlurEffect)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "调节滑块改变模糊半径",
                style = MaterialTheme.typography.bodyMedium
            )

            var blurRadius by remember { mutableFloatStateOf(0f) }

            Slider(
                value = blurRadius,
                onValueChange = { blurRadius = it },
                valueRange = 0f..25f,
                modifier = Modifier.fillMaxWidth()
            )

            Text("模糊半径: %.1f".format(blurRadius), style = MaterialTheme.typography.bodySmall)

            // 带模糊效果的彩色方块
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .graphicsLayer {
                        if (blurRadius > 0f) {
                            renderEffect = AndroidRenderEffect
                                .createBlurEffect(blurRadius, blurRadius, android.graphics.Shader.TileMode.CLAMP)
                                .asComposeRenderEffect()
                        }
                    }
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "模糊我",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White
                )
            }

            Text(
                text = """Modifier.graphicsLayer {
    renderEffect = RenderEffect
        .createBlurEffect(radius, radius, TileMode.CLAMP)
        .asComposeRenderEffect()
}""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

/**
 * 毛玻璃效果 — 模糊 + 半透明叠加
 */
@Composable
fun FrostedGlassDemo() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "毛玻璃效果",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "模糊背景 + 半透明叠加 = 毛玻璃卡片",
                style = MaterialTheme.typography.bodyMedium
            )

            // 背景渐变 + 毛玻璃卡片
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF667eea),
                                Color(0xFF764ba2),
                                Color(0xFFf093fb)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // 毛玻璃卡片
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(100.dp)
                        .graphicsLayer {
                            renderEffect = AndroidRenderEffect
                                .createBlurEffect(15f, 15f, android.graphics.Shader.TileMode.CLAMP)
                                .asComposeRenderEffect()
                        }
                        .background(Color.White.copy(alpha = 0.3f))
                        .clip(RoundedCornerShape(12.dp))
                )

                // 文字在模糊层之上
                Text(
                    text = "毛玻璃卡片",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }

            Text(
                text = """// 模糊层
Modifier.graphicsLayer {
    renderEffect = blurEffect
}.background(Color.White.copy(alpha = 0.3f))

// 内容层（不受模糊影响）
Text("毛玻璃卡片")""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

/**
 * drawBehind 自定义绘制 — 渐变边框
 */
@Composable
fun CustomDrawDemo() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "drawBehind 自定义绘制",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "使用 drawBehind 绘制渐变边框",
                style = MaterialTheme.typography.bodyMedium
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .drawBehind {
                        // 绘制渐变边框
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF667eea),
                                    Color(0xFF764ba2),
                                    Color(0xFFf093fb)
                                )
                            ),
                            size = size
                        )
                        // 内部填充（模拟边框效果）
                        drawRect(
                            color = Color.White,
                            topLeft = androidx.compose.ui.geometry.Offset(4.dp.toPx(), 4.dp.toPx()),
                            size = androidx.compose.ui.geometry.Size(
                                size.width - 8.dp.toPx(),
                                size.height - 8.dp.toPx()
                            )
                        )
                    }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "渐变边框",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = """Modifier.drawBehind {
    drawRect(brush = gradientBrush, size = size)
    drawRect(
        color = Color.White,
        topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
        size = Size(size.width - 8.dp.toPx(), ...)
    )
}""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GraphicsLayerScreenPreview() {
    MaterialTheme {
        GraphicsLayerScreen()
    }
}
```

- [ ] **Step 2: Register in AndroidManifest.xml**

Add after InfiniteTransitionActivity:

```xml
        <activity
            android:name=".level6.GraphicsLayerActivity"
            android:exported="false"
            android:theme="@style/Theme.Compose" />
```

- [ ] **Step 3: Add to MainActivity getDemoLevels()**

Add to Level 6 items after InfiniteTransitionActivity:

```kotlin
                DemoItem(
                    title = "GraphicsLayer",
                    description = "模糊效果, 毛玻璃, 自定义绘制",
                    activityClass = com.peter.compose.demo.level6.GraphicsLayerActivity::class.java,
                    color = Color(0xFF283593)
                ),
```

- [ ] **Step 4: Build and verify**

Run: `./gradlew :compose-demo:assembleDebug`
Expected: BUILD SUCCESSFUL

---

### Task 5: StabilityActivity — Compose Compiler Stability

**Files:**
- Create: `compose-demo/src/main/java/com/peter/compose/demo/level7/StabilityActivity.kt`
- Modify: `compose-demo/src/main/AndroidManifest.xml`
- Modify: `compose-demo/src/main/java/com/peter/compose/demo/MainActivity.kt`

- [ ] **Step 1: Create StabilityActivity.kt**

```kotlin
package com.peter.compose.demo.level7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * StabilityActivity - Compose 编译器稳定性优化
 *
 * 学习目标：
 * 1. 不稳定类型导致的不必要重组
 * 2. @Stable 注解的作用
 * 3. @Immutable 注解的作用
 * 4. 如何检查 skippability
 */
class StabilityActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    StabilityScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// ===== 不稳定类型示例 =====

/**
 * 普通 class — Compose 编译器认为是不稳定的
 * 因为它可能有可变属性，编译器无法跳过重组
 */
class UnstableUser(
    val name: String,
    val age: Int,
    val hobbies: List<String>  // List 是不稳定类型
)

/**
 * @Stable — 告诉编译器这个类型是稳定的
 * 开发者承诺：公开属性不会在构造后改变
 */
@Stable
class StableUser(
    val name: String,
    val age: Int,
    val hobbies: List<String>
)

/**
 * data class + 不可变集合 — 编译器可推断为稳定
 * 使用 kotlinx.collections.immutable 更安全
 */
data class ImmutableUser(
    val name: String,
    val age: Int,
    val hobbies: List<String>  // 如果用 ImmutableList 更好
)

// ===== 计数器用于观察重组 =====

@Composable
fun RecompositionCounter(label: String) {
    var count by remember { mutableIntStateOf(0) }
    SideEffect { count++ }
    Text(
        text = "$label 重组次数: $count",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.error
    )
}

@Composable
fun StabilityScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Compose 稳定性优化",
            style = MaterialTheme.typography.headlineMedium
        )

        UnstableClassDemo()
        StableAnnotationDemo()
        StabilityComparison()
        SkippabilityTips()
    }
}

/**
 * 不稳定类型演示 — 每次父组件重组，子组件都会重组
 */
@Composable
fun UnstableClassDemo() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "不稳定类型 (Unstable)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "普通 class + List 参数 → 编译器无法跳过重组",
                style = MaterialTheme.typography.bodyMedium
            )

            var trigger by remember { mutableIntStateOf(0) }

            Button(onClick = { trigger++ }) {
                Text("触发父组件重组 ($trigger)")
            }

            // 每次父组件重组，这里都会重组（因为 UnstableUser 不稳定）
            val user = remember { UnstableUser("张三", 25, listOf("编程", "阅读")) }
            UnstableUserCard(user)

            Text(
                text = """class UnstableUser(  // 不稳定！
    val name: String,
    val age: Int,
    val hobbies: List<String>  // List 不可推断为不可变
)""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun UnstableUserCard(user: UnstableUser) {
    RecompositionCounter("UnstableUserCard")
    Text("${user.name} · ${user.age}岁 · ${user.hobbies.joinToString()}")
}

/**
 * @Stable 注解演示 — 手动承诺稳定性
 */
@Composable
fun StableAnnotationDemo() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "@Stable 注解",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "@Stable 告诉编译器：这个类的公开属性不会变化，可以跳过重组",
                style = MaterialTheme.typography.bodyMedium
            )

            var trigger by remember { mutableIntStateOf(0) }

            Button(onClick = { trigger++ }) {
                Text("触发父组件重组 ($trigger)")
            }

            val user = remember { StableUser("李四", 30, listOf("运动", "音乐")) }
            StableUserCard(user)

            Text(
                text = """@Stable
class StableUser(
    val name: String,
    val age: Int,
    val hobbies: List<String>  // 开发者承诺不会修改
)""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun StableUserCard(user: StableUser) {
    RecompositionCounter("StableUserCard")
    Text("${user.name} · ${user.age}岁 · ${user.hobbies.joinToString()}")
}

/**
 * 稳定性对比 — 同时展示稳定与不稳定组件的重组差异
 */
@Composable
fun StabilityComparison() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "稳定性对比",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "对比观察：点击按钮后哪个组件重组了？",
                style = MaterialTheme.typography.bodyMedium
            )

            var trigger by remember { mutableIntStateOf(0) }

            Button(onClick = { trigger++ }) {
                Text("触发重组 ($trigger)")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("不稳定", style = MaterialTheme.typography.labelLarge)
                    val unstable = remember { UnstableUser("A", 20, listOf("X")) }
                    UnstableUserCard(unstable)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("@Stable", style = MaterialTheme.typography.labelLarge)
                    val stable = remember { StableUser("B", 25, listOf("Y")) }
                    StableUserCard(stable)
                }
            }
        }
    }
}

/**
 * Skippability 检查技巧
 */
@Composable
fun SkippabilityTips() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "如何检查 Skippability",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = """1. 生成 Compose Compiler 报告：
   在 gradle.properties 添加：
   composeCompilerReports=true

2. 构建项目后查看报告：
   build/compose_compiler/report.txt

3. 报告中关注：
   - "skippable" = 可跳过（参数稳定）
   - "unstable" = 不稳定参数
   - "restartable" = 可重启

4. 最佳实践：
   - 使用 data class + 不可变类型
   - 使用 @Stable/@Immutable 注解
   - 避免在 Composable 参数中使用 var 属性
   - 使用 kotlinx.collections.immutable 替代 List""",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StabilityScreenPreview() {
    MaterialTheme {
        StabilityScreen()
    }
}
```

- [ ] **Step 2: Register in AndroidManifest.xml**

Add after existing Level 7 activities:

```xml
        <activity
            android:name=".level7.StabilityActivity"
            android:exported="false"
            android:theme="@style/Theme.Compose" />
```

- [ ] **Step 3: Add to MainActivity getDemoLevels()**

Add to Level 7 "高级进阶" items after "Compose Testing":

```kotlin
                DemoItem(
                    title = "稳定性优化",
                    description = "@Stable, @Immutable, skippability",
                    activityClass = com.peter.compose.demo.level7.StabilityActivity::class.java,
                    color = Color(0xFF1B5E20)
                ),
```

- [ ] **Step 4: Build and verify**

Run: `./gradlew :compose-demo:assembleDebug`
Expected: BUILD SUCCESSFUL

---

### Task 6: AnnotatedStringActivity — Rich Text & Inline Styling

**Files:**
- Create: `compose-demo/src/main/java/com/peter/compose/demo/level7/AnnotatedStringActivity.kt`
- Modify: `compose-demo/src/main/AndroidManifest.xml`
- Modify: `compose-demo/src/main/java/com/peter/compose/demo/MainActivity.kt`

- [ ] **Step 1: Create AnnotatedStringActivity.kt**

```kotlin
package com.peter.compose.demo.level7

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * AnnotatedStringActivity - 富文本与内联样式
 *
 * 学习目标：
 * 1. AnnotatedString.Builder — SpanStyle 应用
 * 2. ParagraphStyle — 段落样式混合
 * 3. 点击注解 — StringAnnotation + ClickableText
 * 4. 实战：协议文本中的可点击链接
 */
class AnnotatedStringActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    AnnotatedStringScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AnnotatedStringScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "AnnotatedString 富文本",
            style = MaterialTheme.typography.headlineMedium
        )

        SpanStyleDemo()
        MixedStyleDemo()
        ClickableAnnotationDemo()
        TermsOfServiceDemo()
    }
}

/**
 * SpanStyle 基础 — 粗体、斜体、颜色、字号
 */
@Composable
fun SpanStyleDemo() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "SpanStyle 基础",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // 粗体 + 颜色
            Text(
                text = buildAnnotatedString {
                    append("这是")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("粗体")
                    }
                    append("和")
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append("彩色")
                    }
                    append("文本")
                },
                style = MaterialTheme.typography.bodyLarge
            )

            // 斜体 + 下划线 + 字号
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic, fontSize = 20.sp)) {
                        append("斜体大字")
                    }
                    append(" 和 ")
                    withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append("下划线")
                    }
                },
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = """buildAnnotatedString {
    append("这是")
    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("粗体")
    }
    append("和")
    withStyle(SpanStyle(color = primary)) {
        append("彩色")
    }
    append("文本")
}""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

/**
 * 混合多种样式 — 高亮关键词
 */
@Composable
fun MixedStyleDemo() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "混合样式 — 高亮关键词",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            val keywords = listOf("Compose", "声明式", "状态")
            val text = "Jetpack Compose 是 Android 的声明式 UI 框架，核心概念是状态管理。"

            Text(
                text = buildAnnotatedString {
                    var remaining = text
                    for (keyword in keywords) {
                        val index = remaining.indexOf(keyword)
                        if (index >= 0) {
                            append(remaining.substring(0, index))
                            withStyle(
                                SpanStyle(
                                    color = Color.White,
                                    background = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append(keyword)
                            }
                            remaining = remaining.substring(index + keyword.length)
                        }
                    }
                    append(remaining)
                },
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

/**
 * 点击注解 — 可点击的文本片段
 */
@Composable
fun ClickableAnnotationDemo() {
    val context = LocalContext.current

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "点击注解 (StringAnnotation)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            val annotatedText = buildAnnotatedString {
                append("点击 ")
                pushStringAnnotation(tag = "LINK", annotation = "https://developer.android.com")
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Android 开发者文档")
                }
                pop()
                append(" 了解更多")
            }

            ClickableText(
                text = annotatedText,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                onClick = { offset ->
                    annotatedText.getStringAnnotations(
                        tag = "LINK", start = offset, end = offset
                    ).firstOrNull()?.let { annotation ->
                        Toast.makeText(context, "打开: ${annotation.item}", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            Text(
                text = """val annotatedText = buildAnnotatedString {
    append("点击 ")
    pushStringAnnotation(tag = "LINK", annotation = url)
    withStyle(SpanStyle(color = primary, underline)) {
        append("Android 开发者文档")
    }
    pop()
    append(" 了解更多")
}

ClickableText(text = annotatedText, onClick = { offset ->
    annotatedText.getStringAnnotations("LINK", offset, offset)
        .firstOrNull()?.let { openUrl(it.item) }
})""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

/**
 * 实战：用户协议文本
 * 包含可点击的"用户协议"和"隐私政策"链接
 */
@Composable
fun TermsOfServiceDemo() {
    val context = LocalContext.current

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "实战：用户协议",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            val annotatedText = buildAnnotatedString {
                append("注册即表示同意 ")
                pushStringAnnotation(tag = "TERMS", annotation = "terms")
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("《用户协议》")
                }
                pop()
                append(" 和 ")
                pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("《隐私政策》")
                }
                pop()
            }

            ClickableText(
                text = annotatedText,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                onClick = { offset ->
                    annotatedText.getStringAnnotations(
                        tag = "TERMS", start = offset, end = offset
                    ).firstOrNull()?.let {
                        Toast.makeText(context, "打开用户协议", Toast.LENGTH_SHORT).show()
                    }
                    annotatedText.getStringAnnotations(
                        tag = "PRIVACY", start = offset, end = offset
                    ).firstOrNull()?.let {
                        Toast.makeText(context, "打开隐私政策", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnnotatedStringScreenPreview() {
    MaterialTheme {
        AnnotatedStringScreen()
    }
}
```

- [ ] **Step 2: Register in AndroidManifest.xml**

Add after StabilityActivity:

```xml
        <activity
            android:name=".level7.AnnotatedStringActivity"
            android:exported="false"
            android:theme="@style/Theme.Compose" />
```

- [ ] **Step 3: Add to MainActivity getDemoLevels()**

Add to Level 7 items after StabilityActivity:

```kotlin
                DemoItem(
                    title = "富文本",
                    description = "AnnotatedString, 可点击链接",
                    activityClass = com.peter.compose.demo.level7.AnnotatedStringActivity::class.java,
                    color = Color(0xFF004D40)
                ),
```

- [ ] **Step 4: Build and verify**

Run: `./gradlew :compose-demo:assembleDebug`
Expected: BUILD SUCCESSFUL

---

### Task 7: ScaffoldAdvancedActivity — BottomSheet, Drawer, Snackbar

**Files:**
- Create: `compose-demo/src/main/java/com/peter/compose/demo/level7/ScaffoldAdvancedActivity.kt`
- Modify: `compose-demo/src/main/AndroidManifest.xml`
- Modify: `compose-demo/src/main/java/com/peter/compose/demo/MainActivity.kt`

- [ ] **Step 1: Create ScaffoldAdvancedActivity.kt**

```kotlin
package com.peter.compose.demo.level7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * ScaffoldAdvancedActivity - Scaffold 进阶模式
 *
 * 学习目标：
 * 1. BottomSheetScaffold — 底部弹出面板
 * 2. ModalNavigationDrawer — 侧滑抽屉
 * 3. SnackbarHost — 自定义 Snackbar
 * 4. TopAppBar + scrollBehavior — 联动滚动
 */
class ScaffoldAdvancedActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    ScaffoldAdvancedScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldAdvancedScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Scaffold 进阶",
            style = MaterialTheme.typography.headlineMedium
        )

        BottomSheetDemo()
        NavigationDrawerDemo()
        SnackbarDemo()
        TopAppBarScrollDemo()
    }
}

/**
 * BottomSheetScaffold — 底部弹出面板
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetDemo() {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "BottomSheetScaffold",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "底部弹出面板，可拖拽展开/收起",
                style = MaterialTheme.typography.bodyMedium
            )

            // 内嵌的 BottomSheetScaffold 演示
            BottomSheetScaffold(
                scaffoldState = scaffoldState,
                sheetContent = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "底部面板内容",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("这是从底部弹出的面板，可以上下拖拽")
                        Text("适合展示补充信息或操作选项")
                    }
                },
                sheetPeekHeight = 80.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("主内容区域")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            scope.launch {
                                if (scaffoldState.bottomSheetState.isVisible) {
                                    scaffoldState.bottomSheetState.hide()
                                } else {
                                    scaffoldState.bottomSheetState.expand()
                                }
                            }
                        }) {
                            Text("切换底部面板")
                        }
                    }
                }
            }

            Text(
                text = """BottomSheetScaffold(
    sheetContent = { /* 底部面板内容 */ },
    sheetPeekHeight = 80.dp  // 初始显示高度
) {
    // 主内容
}""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

/**
 * ModalNavigationDrawer — 侧滑抽屉
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawerDemo() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "ModalNavigationDrawer",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "侧滑抽屉导航，从左侧滑出",
                style = MaterialTheme.typography.bodyMedium
            )

            // 内嵌的 ModalNavigationDrawer 演示
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        Text(
                            "导航菜单",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleLarge
                        )
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Home, contentDescription = null) },
                            label = { Text("首页") },
                            selected = true,
                            onClick = { scope.launch { drawerState.close() } }
                        )
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Person, contentDescription = null) },
                            label = { Text("个人中心") },
                            selected = false,
                            onClick = { scope.launch { drawerState.close() } }
                        )
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                            label = { Text("设置") },
                            selected = false,
                            onClick = { scope.launch { drawerState.close() } }
                        )
                    }
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("主内容区域")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Text("打开抽屉")
                        }
                    }
                }
            }

            Text(
                text = """ModalNavigationDrawer(
    drawerState = drawerState,
    drawerContent = {
        ModalDrawerSheet {
            NavigationDrawerItem(
                icon = { Icon(...) },
                label = { Text("首页") },
                selected = true,
                onClick = { /* 导航 */ }
            )
        }
    }
) { /* 主内容 */ }""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

/**
 * Snackbar — 自定义样式与操作按钮
 */
@Composable
fun SnackbarDemo() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Snackbar",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "自定义 Snackbar 样式与操作按钮",
                style = MaterialTheme.typography.bodyMedium
            )

            // Snackbar 演示区域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                Column {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("这是一条消息")
                            }
                        }) {
                            Text("基础 Snackbar")
                        }

                        Button(onClick = {
                            scope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = "文件已删除",
                                    actionLabel = "撤销",
                                    withDismissAction = true
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    snackbarHostState.showSnackbar("已撤销删除")
                                }
                            }
                        }) {
                            Text("带操作按钮")
                        }
                    }
                }

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }

            Text(
                text = """val snackbarHostState = remember { SnackbarHostState() }

SnackbarHost(hostState = snackbarHostState)

// 显示
snackbarHostState.showSnackbar("消息")

// 带操作按钮
snackbarHostState.showSnackbar(
    message = "文件已删除",
    actionLabel = "撤销",
    withDismissAction = true
)""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

/**
 * TopAppBar + scrollBehavior — 滚动联动
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarScrollDemo() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "TopAppBar 滚动联动",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "TopAppBar 随滚动隐藏/显示",
                style = MaterialTheme.typography.bodyMedium
            )

            // 内嵌的 TopAppBar + scrollBehavior 演示
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

            Scaffold(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    TopAppBar(
                        title = { Text("滚动联动 TopAppBar") },
                        navigationIcon = {
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.Menu, contentDescription = "菜单")
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    repeat(20) { index ->
                        Text(
                            text = "滚动项 #$index",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }
            }

            Text(
                text = """val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

Scaffold(
    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
        TopAppBar(
            title = { Text("标题") },
            scrollBehavior = scrollBehavior
        )
    }
)""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScaffoldAdvancedScreenPreview() {
    MaterialTheme {
        ScaffoldAdvancedScreen()
    }
}
```

- [ ] **Step 2: Register in AndroidManifest.xml**

Add after AnnotatedStringActivity:

```xml
        <activity
            android:name=".level7.ScaffoldAdvancedActivity"
            android:exported="false"
            android:theme="@style/Theme.Compose" />
```

- [ ] **Step 3: Add to MainActivity getDemoLevels()**

Add to Level 7 items after AnnotatedStringActivity:

```kotlin
                DemoItem(
                    title = "Scaffold 进阶",
                    description = "BottomSheet, Drawer, Snackbar",
                    activityClass = com.peter.compose.demo.level7.ScaffoldAdvancedActivity::class.java,
                    color = Color(0xFF4E342E)
                ),
```

- [ ] **Step 4: Build and verify**

Run: `./gradlew :compose-demo:assembleDebug`
Expected: BUILD SUCCESSFUL

---

### Task 8: CustomModifierActivity — Modifier.Node & Custom Drawing

**Files:**
- Create: `compose-demo/src/main/java/com/peter/compose/demo/level7/CustomModifierActivity.kt`
- Modify: `compose-demo/src/main/AndroidManifest.xml`
- Modify: `compose-demo/src/main/java/com/peter/compose/demo/MainActivity.kt`

- [ ] **Step 1: Create CustomModifierActivity.kt**

```kotlin
package com.peter.compose.demo.level7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * CustomModifierActivity - 自定义 Modifier 深入
 *
 * 学习目标：
 * 1. drawBehind — 自定义背景绘制
 * 2. drawWithContent — 在内容前后绘制
 * 3. pointerInput — 自定义手势检测
 * 4. 组合：渐变边框 Modifier
 * 5. Modifier.composed vs Modifier.Node 对比说明
 */
class CustomModifierActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    CustomModifierScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CustomModifierScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "自定义 Modifier",
            style = MaterialTheme.typography.headlineMedium
        )

        DrawBehindDemo()
        DrawWithContentDemo()
        PointerInputDemo()
        GradientBorderDemo()
        ModifierNodeExplanation()
    }
}

/**
 * drawBehind — 在内容后面绘制
 * 常用于自定义背景
 */
@Composable
fun DrawBehindDemo() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "drawBehind — 自定义背景",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "在内容后面绘制自定义图形",
                style = MaterialTheme.typography.bodyMedium
            )

            // 渐变背景
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .drawBehind {
                        drawRoundRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF667eea),
                                    Color(0xFF764ba2)
                                )
                            ),
                            cornerRadius = CornerRadius(16.dp.toPx())
                        )
                    }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "渐变背景",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // 圆点背景
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .drawBehind {
                        val dotRadius = 4.dp.toPx()
                        val spacing = 24.dp.toPx()
                        var x = 0f
                        while (x < size.width) {
                            var y = 0f
                            while (y < size.height) {
                                drawCircle(
                                    color = Color.LightGray.copy(alpha = 0.5f),
                                    radius = dotRadius,
                                    center = Offset(x, y)
                                )
                                y += spacing
                            }
                            x += spacing
                        }
                    }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("圆点背景", style = MaterialTheme.typography.titleMedium)
            }

            Text(
                text = """Modifier.drawBehind {
    drawRoundRect(
        brush = gradientBrush,
        cornerRadius = CornerRadius(16.dp.toPx())
    )
}""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

/**
 * drawWithContent — 在内容前后绘制
 * 可以控制绘制顺序
 */
@Composable
fun DrawWithContentDemo() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "drawWithContent — 前后绘制",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "在内容前面或后面叠加绘制层",
                style = MaterialTheme.typography.bodyMedium
            )

            // 在内容后面画光晕效果
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .drawWithContent {
                        // 先画光晕
                        drawCircle(
                            color = Color(0xFF667eea).copy(alpha = 0.3f),
                            radius = size.minDimension * 0.6f,
                            center = center
                        )
                        // 再画内容
                        drawContent()
                    }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "光晕效果",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // 在内容前面画半透明遮罩
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .drawWithContent {
                        // 先画内容
                        drawContent()
                        // 再叠加半透明条纹
                        val stripeWidth = 20.dp.toPx()
                        var x = 0f
                        while (x < size.width) {
                            drawRect(
                                color = Color.White.copy(alpha = 0.1f),
                                topLeft = Offset(x, 0f),
                                size = Size(stripeWidth / 2, size.height)
                            )
                            x += stripeWidth
                        }
                    }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("条纹遮罩", style = MaterialTheme.typography.titleMedium)
            }

            Text(
                text = """Modifier.drawWithContent {
    // 先画背景效果
    drawCircle(color = glowColor, radius = ...)
    // 再画内容
    drawContent()
}""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

/**
 * pointerInput — 自定义手势检测
 * 长按检测 + 视觉反馈
 */
@Composable
fun PointerInputDemo() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "pointerInput — 手势检测",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "长按显示涟漪效果，点击显示坐标",
                style = MaterialTheme.typography.bodyMedium
            )

            var tapInfo by remember { mutableStateOf("点击或长按下方区域") }
            var rippleCenter by remember { mutableStateOf<Offset?>(null) }
            var showRipple by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { offset ->
                                tapInfo = "点击位置: (${offset.x.toInt()}, ${offset.y.toInt()})"
                            },
                            onLongPress = { offset ->
                                tapInfo = "长按位置: (${offset.x.toInt()}, ${offset.y.toInt()})"
                                rippleCenter = offset
                                showRipple = true
                            }
                        )
                    }
                    .drawBehind {
                        if (showRipple && rippleCenter != null) {
                            drawCircle(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                radius = 40.dp.toPx(),
                                center = rippleCenter!!
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(tapInfo)
            }

            Text(
                text = """Modifier.pointerInput(Unit) {
    detectTapGestures(
        onTap = { offset -> /* 点击 */ },
        onLongPress = { offset -> /* 长按 */ }
    )
}""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

/**
 * 渐变边框 Modifier — 组合 drawBehind + drawWithContent
 * 实战：可复用的渐变边框效果
 */
@Composable
fun GradientBorderDemo() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "渐变边框 Modifier",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "组合 drawBehind 实现可复用的渐变边框",
                style = MaterialTheme.typography.bodyMedium
            )

            // 使用渐变边框
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .gradientBorder(
                        colors = listOf(
                            Color(0xFF667eea),
                            Color(0xFF764ba2),
                            Color(0xFFf093fb)
                        ),
                        borderWidth = 4.dp,
                        cornerRadius = 16.dp
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("渐变边框效果", style = MaterialTheme.typography.titleMedium)
            }

            // 不同颜色的渐变边框
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .gradientBorder(
                        colors = listOf(
                            Color(0xFF00C853),
                            Color(0xFF00BFA5),
                            Color(0xFF00B0FF)
                        ),
                        borderWidth = 3.dp,
                        cornerRadius = 12.dp
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("绿色渐变边框", style = MaterialTheme.typography.titleMedium)
            }

            Text(
                text = """fun Modifier.gradientBorder(
    colors: List<Color>,
    borderWidth: Dp = 2.dp,
    cornerRadius: Dp = 8.dp
) = this.drawBehind {
    drawRoundRect(
        brush = Brush.linearGradient(colors),
        cornerRadius = CornerRadius(cornerRadius.toPx()),
        style = Stroke(width = borderWidth.toPx())
    )
}""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

/**
 * Modifier.Node 说明
 * 新一代 Modifier API 的优势与使用时机
 */
@Composable
fun ModifierNodeExplanation() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Modifier.Node vs composed",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = """旧 API: Modifier.composed {}
- 每次重组都会创建新的 Modifier 实例
- 有额外的内存分配开销
- 可以访问 Composition 的状态

新 API: Modifier.Node
- 只创建一次 Node 实例
- 通过 update {} 更新参数
- 性能更好，减少内存分配
- 推荐用于库作者和复杂自定义 Modifier

使用建议：
• 简单的绘制/布局 → drawBehind / drawWithContent
• 需要状态和副作用 → Modifier.Node
• 简单的参数变换 → 普通 Modifier 扩展函数
• 库级别的复杂 Modifier → Modifier.Node

Compose BOM 2024.02+ 已稳定支持 Modifier.Node""",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * 渐变边框 Modifier 扩展函数
 */
fun Modifier.gradientBorder(
    colors: List<Color>,
    borderWidth: androidx.compose.ui.unit.Dp = 2.dp,
    cornerRadius: androidx.compose.ui.unit.Dp = 8.dp
) = this.drawBehind {
    drawRoundRect(
        brush = Brush.linearGradient(colors),
        cornerRadius = CornerRadius(cornerRadius.toPx()),
        style = Stroke(width = borderWidth.toPx())
    )
}

@Preview(showBackground = true)
@Composable
fun CustomModifierScreenPreview() {
    MaterialTheme {
        CustomModifierScreen()
    }
}
```

- [ ] **Step 2: Register in AndroidManifest.xml**

Add after ScaffoldAdvancedActivity:

```xml
        <activity
            android:name=".level7.CustomModifierActivity"
            android:exported="false"
            android:theme="@style/Theme.Compose" />
```

- [ ] **Step 3: Add to MainActivity getDemoLevels()**

Add to Level 7 items after ScaffoldAdvancedActivity:

```kotlin
                DemoItem(
                    title = "自定义 Modifier",
                    description = "drawBehind, pointerInput, 渐变边框",
                    activityClass = com.peter.compose.demo.level7.CustomModifierActivity::class.java,
                    color = Color(0xFF3E2723)
                ),
```

- [ ] **Step 4: Update demo count in MainActivity**

In `MainScreen()`, update the subtitle text from "7 个层级 · 31 个示例" to "7 个层级 · 39 个示例":

```kotlin
                Text(
                    text = "7 个层级 · 39 个示例",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                )
```

- [ ] **Step 5: Build and verify**

Run: `./gradlew :compose-demo:assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 6: Final commit**

```bash
git add compose-demo/
git commit -m "feat(compose-demo): add 8 advanced Compose demos (L3/L6/L7)

New Activities:
- L3: StateRestoreActivity (rememberSaveable, custom Saver)
- L3: SnapshotFlowActivity (snapshotFlow, debounce search)
- L6: InfiniteTransitionActivity (infinite animation, spring physics)
- L6: GraphicsLayerActivity (blur, frosted glass, custom draw)
- L7: StabilityActivity (@Stable/@Immutable, skippability)
- L7: AnnotatedStringActivity (rich text, clickable annotations)
- L7: ScaffoldAdvancedActivity (BottomSheet, Drawer, Snackbar)
- L7: CustomModifierActivity (drawBehind, pointerInput, gradient border)

Total: 31 → 39 demo Activities"
```
