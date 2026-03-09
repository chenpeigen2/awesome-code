package com.peter.compose.demo.level2

import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * SideEffectsActivity - 副作用
 *
 * 学习目标：
 * 1. LaunchedEffect: 在 Composable 中安全执行协程
 * 2. DisposableEffect: 处理需要清理的副作用
 * 3. SideEffect: Compose 完成重组后的回调
 * 4. rememberCoroutineScope: 获取 CoroutineScope
 */
class SideEffectsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    SideEffectsScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SideEffectsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. LaunchedEffect 示例
        LaunchedEffectExample()

        // 2. DisposableEffect 示例
        DisposableEffectExample()

        // 3. SideEffect 示例
        SideEffectExample()

        // 4. rememberCoroutineScope 示例
        CoroutineScopeExample()
    }
}

/**
 * LaunchedEffect 示例
 *
 * LaunchedEffect 在 Composable 进入 Composition 时启动协程
 * 当 key 变化时，会取消之前的协程并重新启动
 * 当 Composable 离开 Composition 时，协程会被取消
 */
@Composable
fun LaunchedEffectExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "1. LaunchedEffect",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "LaunchedEffect 用于在 Composable 中安全执行协程操作，如网络请求、延迟操作等。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 倒计时示例
            var countdown by remember { mutableIntStateOf(10) }
            var isRunning by remember { mutableStateOf(false) }

            Text(
                text = "倒计时示例:",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 显示倒计时
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$countdown",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Column {
                    Button(
                        onClick = {
                            isRunning = !isRunning
                            if (!isRunning) countdown = 10
                        }
                    ) {
                        Text(if (isRunning) "重置" else "开始")
                    }
                }
            }

            // 使用 LaunchedEffect 执行倒计时
            LaunchedEffect(key1 = isRunning) {
                if (isRunning) {
                    while (countdown > 0) {
                        delay(1000)
                        countdown--
                    }
                    isRunning = false
                }
            }

            // 加载示例
            var isLoading by remember { mutableStateOf(false) }
            var data by remember { mutableStateOf<String?>(null) }

            Text(
                text = "模拟网络请求:",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        isLoading = true
                        data = null
                    },
                    enabled = !isLoading
                ) {
                    Text("加载数据")
                }

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }

            // 加载完成后显示数据
            data?.let {
                Text(
                    text = "加载结果: $it",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // 使用 LaunchedEffect 模拟网络请求
            LaunchedEffect(key1 = isLoading) {
                if (isLoading) {
                    // 模拟网络延迟
                    delay(2000)
                    data = "这是从服务器加载的数据"
                    isLoading = false
                }
            }
        }
    }
}

/**
 * DisposableEffect 示例
 *
 * DisposableEffect 用于需要清理资源的副作用
 * 当 Composable 离开 Composition 或 key 变化时，会调用 onDispose
 */
@Composable
fun DisposableEffectExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "2. DisposableEffect",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "DisposableEffect 用于需要清理资源的副作用，如监听器、广播接收器等。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 生命周期监听示例
            Text(
                text = "生命周期监听示例:",
                style = MaterialTheme.typography.labelMedium
            )

            var lifecycleState by remember { mutableStateOf("UNKNOWN") }
            val lifecycleOwner = LocalLifecycleOwner.current

            // 使用 DisposableEffect 监听生命周期
            DisposableEffect(key1 = lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    lifecycleState = when (event) {
                        Lifecycle.Event.ON_CREATE -> "ON_CREATE"
                        Lifecycle.Event.ON_START -> "ON_START"
                        Lifecycle.Event.ON_RESUME -> "ON_RESUME"
                        Lifecycle.Event.ON_PAUSE -> "ON_PAUSE"
                        Lifecycle.Event.ON_STOP -> "ON_STOP"
                        Lifecycle.Event.ON_DESTROY -> "ON_DESTROY"
                        else -> event.name
                    }
                }

                lifecycleOwner.lifecycle.addObserver(observer)

                // 清理：移除观察者
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            Text(
                text = "当前生命周期状态: $lifecycleState",
                style = MaterialTheme.typography.bodyMedium
            )

            // 模拟资源管理
            var resourceId by remember { mutableIntStateOf(0) }
            var isResourceAcquired by remember { mutableStateOf(false) }

            Text(
                text = "资源管理示例:",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        resourceId++
                        isResourceAcquired = true
                    },
                    enabled = !isResourceAcquired
                ) {
                    Text("获取资源")
                }

                Button(
                    onClick = { isResourceAcquired = false },
                    enabled = isResourceAcquired
                ) {
                    Text("释放资源")
                }
            }

            // 使用 DisposableEffect 管理资源
            if (isResourceAcquired) {
                DisposableEffect(key1 = resourceId) {
                    Log.d("DisposableEffect", "资源 $resourceId 已获取")

                    onDispose {
                        Log.d("DisposableEffect", "资源 $resourceId 已释放")
                    }
                }

                Text(
                    text = "资源 $resourceId 已获取",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * SideEffect 示例
 *
 * SideEffect 在 Composition 成功完成后执行
 * 用于需要与非 Compose 代码共享状态的情况
 */
@Composable
fun SideEffectExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "3. SideEffect",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "SideEffect 在每次成功重组后执行，用于与非 Compose 代码共享状态。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 计数器状态
            var count by remember { mutableIntStateOf(0) }

            // 使用 SideEffect 将状态同步到外部变量
            var externalCount by remember { mutableIntStateOf(0) }

            SideEffect {
                externalCount = count
            }

            Text(
                text = "计数器示例:",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { count++ }) {
                    Text("计数: $count")
                }
            }

            Text(
                text = "Compose 内部状态: $count",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "通过 SideEffect 同步的外部状态: $externalCount",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 主题颜色同步示例
            var isDarkMode by remember { mutableStateOf(false) }

            Text(
                text = "主题同步示例:",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { isDarkMode = !isDarkMode }) {
                    Text(if (isDarkMode) "切换到浅色模式" else "切换到深色模式")
                }
            }

            // 使用 SideEffect 同步主题状态到外部（模拟）
            SideEffect {
                // 这里可以同步到非 Compose 代码，如：
                // analyticsService.setDarkMode(isDarkMode)
                Log.d("SideEffect", "主题模式: ${if (isDarkMode) "深色" else "浅色"}")
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isDarkMode) Color(0xFF1a1a1a) else Color(0xFFf5f5f5)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isDarkMode) "深色模式" else "浅色模式",
                    color = if (isDarkMode) Color.White else Color.Black
                )
            }
        }
    }
}

/**
 * rememberCoroutineScope 示例
 *
 * rememberCoroutineScope 返回一个绑定到 Composition 生命周期的 CoroutineScope
 * 用于在事件处理（如点击）中启动协程
 */
@Composable
fun CoroutineScopeExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "4. rememberCoroutineScope",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "rememberCoroutineScope 返回的 CoroutineScope 绑定到 Composition 生命周期，用于在事件处理器中启动协程。",
                style = MaterialTheme.typography.bodyMedium
            )

            val coroutineScope = rememberCoroutineScope()

            // 进度条示例
            var progress by remember { mutableStateOf(0f) }
            var isProgressRunning by remember { mutableStateOf(false) }

            Text(
                text = "进度条示例:",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 进度条
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = progress))
                    )
                }

                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        isProgressRunning = true
                        coroutineScope.launch {
                            while (progress < 1f) {
                                delay(50)
                                progress += 0.01f
                            }
                            isProgressRunning = false
                        }
                    },
                    enabled = !isProgressRunning && progress < 1f
                ) {
                    Text("开始")
                }

                Button(
                    onClick = {
                        progress = 0f
                        isProgressRunning = false
                    }
                ) {
                    Text("重置")
                }
            }

            // 多任务示例
            var taskCount by remember { mutableIntStateOf(0) }

            Text(
                text = "并发任务示例:",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val currentTask = ++taskCount
                            delay(2000)
                            withContext(Dispatchers.Main) {
                                Log.d("CoroutineScope", "任务 $currentTask 完成")
                            }
                        }
                    }
                ) {
                    Text("启动任务")
                }

                Text(
                    text = "已启动: $taskCount 个任务",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // 协程取消示例
            var cancelableJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
            var jobStatus by remember { mutableStateOf("未启动") }

            Text(
                text = "任务取消示例:",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        jobStatus = "运行中..."
                        cancelableJob = coroutineScope.launch {
                            try {
                                repeat(10) { i ->
                                    delay(1000)
                                    jobStatus = "运行中: ${i + 1}/10"
                                }
                                jobStatus = "完成"
                            } catch (e: kotlinx.coroutines.CancellationException) {
                                jobStatus = "已取消"
                            }
                        }
                    },
                    enabled = cancelableJob == null || !cancelableJob!!.isActive
                ) {
                    Text("启动可取消任务")
                }

                Button(
                    onClick = {
                        cancelableJob?.cancel()
                        cancelableJob = null
                    },
                    enabled = cancelableJob?.isActive == true
                ) {
                    Text("取消")
                }
            }

            Text(
                text = "状态: $jobStatus",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SideEffectsPreview() {
    MaterialTheme {
        SideEffectsScreen()
    }
}
