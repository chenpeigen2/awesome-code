package com.peter.compose.demo.level5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ============================================================
// 数据模型
// ============================================================

/**
 * 列表中的彩色数据项
 *
 * @param id 唯一标识
 * @param title 标题文本
 * @param color 背景颜色
 */
data class ColorItem(
    val id: Int,
    val title: String,
    val color: Color
)

/**
 * 带时间戳的数据项，用于刷新 + 数据更新演示
 *
 * @param id 唯一标识
 * @param timestamp 创建时间戳文本
 * @param description 描述信息
 */
data class TimestampItem(
    val id: Int,
    val timestamp: String,
    val description: String
)

// ============================================================
// Activity
// ============================================================

/**
 * PullToRefreshActivity - Material3 下拉刷新示例
 *
 * 学习目标：
 * 1. PullToRefreshBox 基础用法
 * 2. PullToRefreshState 状态追踪
 * 3. 自定义刷新指示器
 * 4. 刷新 + 数据更新的完整流程
 * 5. 嵌套滚动（PullToRefresh + StickyHeader）
 */
class PullToRefreshActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    PullToRefreshScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// ============================================================
// 主界面
// ============================================================

/**
 * PullToRefreshScreen - 下拉刷新示例主界面
 *
 * 使用 LazyColumn 包裹所有 Section，使整体可滚动
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ---- 标题区 ----
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Pull-to-Refresh 下拉刷新",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "使用 Material3 PullToRefreshBox 实现下拉刷新",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // ---- 1. PullToRefresh 基础 ----
        item {
            BasicPullToRefreshSection()
        }

        // ---- 2. PullToRefreshState ----
        item {
            PullToRefreshStateSection()
        }

        // ---- 3. 自定义刷新指示器 ----
        item {
            CustomIndicatorSection()
        }

        // ---- 4. 刷新 + 数据更新 ----
        item {
            RefreshWithDataSection()
        }

        // ---- 5. 嵌套刷新（StickyHeader） ----
        item {
            NestedRefreshSection()
        }

        // 底部间距
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ============================================================
// 1. PullToRefresh 基础
// ============================================================

/**
 * 基础 PullToRefreshBox 示例
 *
 * 核心要点：
 * - PullToRefreshBox 包裹可滚动内容（如 LazyColumn）
 * - isRefreshing 控制刷新指示器的显示/隐藏
 * - onRefresh 回调在用户触发下拉刷新时调用
 * - 刷新完成后需手动将 isRefreshing 设为 false
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicPullToRefreshSection() {
    // 刷新状态
    var isRefreshing by remember { mutableStateOf(false) }

    // 列表数据，使用 mutableStateListOf 以支持动态更新
    val items = remember { mutableStateListOf<ColorItem>() }
    // 记录下次新增数据的起始 id
    var nextId by remember { mutableIntStateOf(1) }

    // 初始化数据（仅在首次组合时执行）
    if (items.isEmpty()) {
        val colors = listOf(
            Color(0xFFE57373), Color(0xFF64B5F6), Color(0xFF81C784),
            Color(0xFFFFB74D), Color(0xFFBA68C8), Color(0xFF4DB6AC),
            Color(0xFFF06292), Color(0xFF7986CB)
        )
        colors.forEachIndexed { index, color ->
            items.add(ColorItem(id = nextId++, title = "初始项目 ${index + 1}", color = color))
        }
    }

    // 使用 LaunchedEffect 监听刷新状态，模拟异步加载
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            // 模拟网络请求延迟（2秒）
            delay(2000)
            // 在列表顶部添加新数据
            val newColor = Color(
                red = (100..255).random(),
                green = (100..255).random(),
                blue = (100..255).random()
            )
            items.add(0, ColorItem(id = nextId++, title = "新增项目 #${nextId - 1}", color = newColor))
            // 结束刷新状态
            isRefreshing = false
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader(title = "1. PullToRefresh 基础")

            Text(
                text = "下拉列表触发刷新，2秒后新增一条数据到顶部。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            // PullToRefreshBox 包裹 LazyColumn
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    // 用户触发下拉刷新，设置刷新状态为 true
                    isRefreshing = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(items, key = { it.id }) { item ->
                        BasicColorRow(item = item)
                    }
                }
            }
        }
    }
}

/**
 * 基础颜色行组件
 */
@Composable
fun BasicColorRow(item: ColorItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(item.color.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧圆形色块
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(item.color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${item.id}",
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

// ============================================================
// 2. PullToRefreshState
// ============================================================

/**
 * PullToRefreshState 状态追踪示例
 *
 * 核心要点：
 * - rememberPullToRefreshState() 创建状态对象
 * - state.isRefreshing: 当前是否正在刷新
 * - state.distanceFraction: 用户下拉距离占总距离的比例（0.0 ~ 1.0+）
 * - 这些值可用于构建自定义刷新动画
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshStateSection() {
    var isRefreshing by remember { mutableStateOf(false) }
    // 创建 PullToRefreshState 追踪下拉状态
    val state = rememberPullToRefreshState()

    // 监听刷新状态
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(2000)
            isRefreshing = false
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader(title = "2. PullToRefreshState 状态追踪")

            Text(
                text = "下拉列表观察 state 的实时变化。distanceFraction 表示下拉距离比例。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            // PullToRefreshBox 携带 state 参数
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { isRefreshing = true },
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(20) { index ->
                        StateDemoRow(index = index)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 实时显示 state 值
            StateInfoCard(
                isRefreshing = isRefreshing,
                distanceFraction = state.distanceFraction
            )
        }
    }
}

/**
 * 状态追踪示例的列表行
 */
@Composable
fun StateDemoRow(index: Int) {
    val backgroundColor = if (index % 2 == 0) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.surface
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = "列表项 ${index + 1}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * 显示 PullToRefreshState 的实时值
 */
@Composable
fun StateInfoCard(isRefreshing: Boolean, distanceFraction: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "PullToRefreshState 实时状态",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            // isRefreshing 状态
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "isRefreshing: ",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                val refreshColor = if (isRefreshing) Color(0xFF4CAF50) else Color(0xFF9E9E9E)
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(refreshColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isRefreshing) "true (刷新中)" else "false (空闲)",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            // distanceFraction 值
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "distanceFraction: ",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = String.format("%.2f", distanceFraction),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // distanceFraction 进度条可视化
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f))
            ) {
                val animatedColor by animateColorAsState(
                    targetValue = if (distanceFraction >= 1f) Color(0xFF4CAF50)
                    else MaterialTheme.colorScheme.primary,
                    label = "fractionColor"
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(distanceFraction.coerceIn(0f, 1f))
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(animatedColor)
                )
            }

            Text(
                text = "当 distanceFraction >= 1.0 时，松手即触发刷新。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

// ============================================================
// 3. 自定义刷新指示器
// ============================================================

/**
 * 自定义刷新指示器示例
 *
 * 核心要点：
 * - PullToRefreshBox 的 indicator 参数允许自定义刷新指示器
 * - 可以根据 distanceFraction 自定义动画效果
 * - 可以修改指示器的颜色、形状和内容
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomIndicatorSection() {
    var isRefreshing by remember { mutableStateOf(false) }

    // 自定义颜色用于指示器
    val customIndicatorColor = Color(0xFF6200EE)

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(1500)
            isRefreshing = false
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader(title = "3. 自定义刷新指示器")

            Text(
                text = "使用自定义颜色的刷新指示器。下拉观察指示器颜色变化。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 带自定义 indicator 的 PullToRefreshBox
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { isRefreshing = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                indicator = {
                    // 自定义指示器：带颜色和阴影的圆形进度指示器
                    if (isRefreshing) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(36.dp),
                                color = customIndicatorColor,
                                strokeWidth = 3.dp
                            )
                        }
                    }
                }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(8) { index ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(customIndicatorColor.copy(alpha = 0.1f))
                                .padding(horizontal = 12.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = "自定义指示器列表项 ${index + 1}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = customIndicatorColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 代码说明
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "自定义 indicator 参数说明:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = """PullToRefreshBox(
    indicator = {
        // 自定义指示器内容
        CircularProgressIndicator(
            color = CustomColor
        )
    }
) { ... }""",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 16.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

// ============================================================
// 4. 刷新 + 数据更新
// ============================================================

/**
 * 完整的刷新 + 数据更新示例
 *
 * 核心要点：
 * - 使用 LaunchedEffect(isRefreshing) 处理异步刷新逻辑
 * - 刷新时模拟网络请求（2秒延迟）
 * - 完成后在列表顶部添加带时间戳的新数据
 * - 通过 SnackbarHostState 显示刷新结果提示
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefreshWithDataSection() {
    // Snackbar 状态
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 刷新状态
    var isRefreshing by remember { mutableStateOf(false) }

    // 时间戳数据列表
    val timestampItems = remember { mutableStateListOf<TimestampItem>() }
    var nextTimestampId by remember { mutableIntStateOf(1) }

    // 时间格式化器
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    // 使用 LaunchedEffect 监听刷新状态
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            // 模拟网络请求延迟
            delay(2000)

            // 随机生成 1~3 条新数据
            val newCount = (1..3).random()
            val currentTime = timeFormat.format(Date())
            val newItems = (1..newCount).mapIndexed { index, _ ->
                TimestampItem(
                    id = nextTimestampId++,
                    timestamp = timeFormat.format(Date()),
                    description = "刷新获取的数据 (第${nextTimestampId - 1}条) - $currentTime +${index}s"
                )
            }

            // 将新数据添加到列表顶部
            timestampItems.addAll(0, newItems)

            // 结束刷新状态
            isRefreshing = false

            // 显示 Snackbar 提示
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "刷新成功，新增 $newCount 条数据"
                )
            }
        }
    }

    // 初始化：添加一些默认数据
    if (timestampItems.isEmpty()) {
        timestampItems.addAll(
            listOf(
                TimestampItem(
                    id = nextTimestampId++,
                    timestamp = "初始数据",
                    description = "这是初始加载的数据项，下拉获取更多"
                ),
                TimestampItem(
                    id = nextTimestampId++,
                    timestamp = "初始数据",
                    description = "下拉刷新即可看到新增的时间戳数据"
                )
            )
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader(title = "4. 刷新 + 数据更新")

            Text(
                text = "完整示例：下拉刷新模拟网络请求，新增带时间戳的数据，并显示 Snackbar。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 嵌套 SnackbarHost 和 PullToRefreshBox
            Box(modifier = Modifier.fillMaxWidth()) {
                Column {
                    // PullToRefreshBox 包裹列表
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = { isRefreshing = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // 列表顶部状态提示
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                        )
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = "共 ${timestampItems.size} 条数据 | 下拉刷新获取更多",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            // 数据项列表
                            items(timestampItems, key = { it.id }) { item ->
                                TimestampItemCard(item = item)
                            }
                        }
                    }

                    // Snackbar 宿主
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

/**
 * 时间戳数据项卡片
 */
@Composable
fun TimestampItemCard(item: TimestampItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧时间戳标识
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${item.id}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        // 右侧内容
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.timestamp,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ============================================================
// 5. 嵌套刷新（StickyHeader）
// ============================================================

/**
 * 分组数据类，用于 StickyHeader 演示
 */
data class GroupedItem(
    val id: Int,
    val group: String,
    val title: String
)

/**
 * 嵌套滚动示例：PullToRefreshBox + LazyColumn + StickyHeader
 *
 * 核心要点：
 * - PullToRefreshBox 与 LazyColumn 的嵌套滚动自动协调
 * - StickyHeader 在列表滚动时吸附在顶部
 * - 下拉刷新不会干扰 StickyHeader 的正常工作
 * - 两种滚动行为互不冲突
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NestedRefreshSection() {
    var isRefreshing by remember { mutableStateOf(false) }
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    // 分组数据
    val groupedItems = remember {
        val groups = listOf("水果", "蔬菜", "饮品", "零食")
        val items = mutableListOf<GroupedItem>()
        var id = 1
        groups.forEach { group ->
            // 每组 4 个项目
            val names = when (group) {
                "水果" -> listOf("苹果", "香蕉", "橘子", "葡萄")
                "蔬菜" -> listOf("番茄", "黄瓜", "胡萝卜", "菠菜")
                "饮品" -> listOf("咖啡", "绿茶", "橙汁", "可乐")
                else -> listOf("饼干", "薯片", "巧克力", "坚果")
            }
            names.forEach { name ->
                items.add(GroupedItem(id = id++, group = group, title = name))
            }
        }
        items
    }

    // 追踪刷新次数
    var refreshCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(2000)
            refreshCount++
            isRefreshing = false
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader(title = "5. 嵌套刷新（StickyHeader）")

            Text(
                text = "PullToRefreshBox + LazyColumn StickyHeader。下拉刷新不影响分组头吸附效果。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (refreshCount > 0) {
                Text(
                    text = "已刷新 $refreshCount 次 (最后刷新: ${timeFormat.format(Date())})",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // PullToRefreshBox 包裹带 StickyHeader 的 LazyColumn
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { isRefreshing = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            ) {
                // 按 group 分组
                val grouped = groupedItems.groupBy { it.group }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    grouped.forEach { (group, items) ->
                        // StickyHeader: 滚动时吸附在顶部
                        stickyHeader(key = "header_$group") {
                            StickyGroupHeader(group = group)
                        }

                        // 分组内的数据项
                        items(items, key = { it.id }) { item ->
                            GroupedItemRow(item = item)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 说明卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "嵌套滚动要点:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = """
                            |• PullToRefreshBox 内部的 NestedScrollConnection 自动处理嵌套滚动
                            |• LazyColumn 向下滚动时，内容正常滚动
                            |• LazyColumn 滚到顶部后继续下拉，触发 PullToRefresh
                            |• StickyHeader 的吸附效果不受下拉刷新影响
                        """.trimMargin(),
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

/**
 * 分组吸附头组件
 */
@Composable
fun StickyGroupHeader(group: String) {
    val groupColors = mapOf(
        "水果" to Color(0xFFFFCDD2),
        "蔬菜" to Color(0xFFC8E6C9),
        "饮品" to Color(0xFFBBDEFB),
        "零食" to Color(0xFFFFE0B2)
    )
    val groupIcon = mapOf(
        "水果" to "🍎",
        "蔬菜" to "🥬",
        "饮品" to "☕",
        "零食" to "🍪"
    )
    val backgroundColor = groupColors[group] ?: MaterialTheme.colorScheme.surfaceVariant
    val icon = groupIcon[group] ?: ""

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = "$icon $group",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * 分组数据项行
 */
@Composable
fun GroupedItemRow(item: GroupedItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "#${item.id}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ============================================================
// 通用组件
// ============================================================

/**
 * 通用区域标题组件
 */
@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

// ============================================================
// Preview
// ============================================================

@Preview(showBackground = true)
@Composable
fun PullToRefreshScreenPreview() {
    MaterialTheme {
        // 预览静态内容（不含 PullToRefresh 交互）
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Pull-to-Refresh 下拉刷新",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            StateInfoCard(
                isRefreshing = false,
                distanceFraction = 0f
            )

            TimestampItemCard(
                item = TimestampItem(
                    id = 1,
                    timestamp = "12:30:45",
                    description = "预览数据项"
                )
            )

            StickyGroupHeader(group = "水果")
            GroupedItemRow(
                item = GroupedItem(id = 1, group = "水果", title = "苹果")
            )
        }
    }
}
