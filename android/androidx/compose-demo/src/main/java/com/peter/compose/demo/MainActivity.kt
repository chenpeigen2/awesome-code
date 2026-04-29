package com.peter.compose.demo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * MainActivity - Compose Demo 入口列表页
 *
 * 展示所有示例的入口列表，按层级分组
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

/**
 * 示例数据类
 */
data class DemoItem(
    val title: String,
    val description: String,
    val activityClass: Class<*>,
    val color: Color
)

data class DemoLevel(
    val level: Int,
    val title: String,
    val description: String,
    val items: List<DemoItem>,
    val color: Color
)

/**
 * 获取所有示例数据
 */
fun getDemoLevels(): List<DemoLevel> {
    return listOf(
        DemoLevel(
            level = 1,
            title = "基础组件",
            description = "Compose 基础概念与组件",
            color = Color(0xFF6200EE),
            items = listOf(
                DemoItem(
                    title = "Preview 预览",
                    description = "@Preview 注解与多预览模式",
                    activityClass = com.peter.compose.demo.level1.PreviewActivity::class.java,
                    color = Color(0xFFBB86FC)
                ),
                DemoItem(
                    title = "基础组件",
                    description = "Text, Button, Image, Icon",
                    activityClass = com.peter.compose.demo.level1.BasicComponentsActivity::class.java,
                    color = Color(0xFF3700B3)
                ),
                DemoItem(
                    title = "Material3 组件",
                    description = "TopAppBar, NavigationBar, Chip, DatePicker, Dialog",
                    activityClass = com.peter.compose.demo.level1.Material3ComponentsActivity::class.java,
                    color = Color(0xFF03DAC5)
                ),
                DemoItem(
                    title = "布局组件",
                    description = "Column, Row, Box, Card",
                    activityClass = com.peter.compose.demo.level1.LayoutComponentsActivity::class.java,
                    color = Color(0xFF018786)
                )
            )
        ),
        DemoLevel(
            level = 2,
            title = "样式与主题",
            description = "修饰符、阴影、主题与输入",
            color = Color(0xFF795548),
            items = listOf(
                DemoItem(
                    title = "修饰符",
                    description = "padding, size, background, border",
                    activityClass = com.peter.compose.demo.level1.ModifiersActivity::class.java,
                    color = Color(0xFF8D6E63)
                ),
                DemoItem(
                    title = "Elevation 详解",
                    description = "shadow, translationZ, 传统View方式",
                    activityClass = com.peter.compose.demo.level1.ElevationActivity::class.java,
                    color = Color(0xFF795548)
                ),
                DemoItem(
                    title = "动态主题",
                    description = "DynamicColor, 自定义 ColorScheme, Typography",
                    activityClass = com.peter.compose.demo.level2.DynamicThemeActivity::class.java,
                    color = Color(0xFF6D4C41)
                ),
                DemoItem(
                    title = "TextField 进阶",
                    description = "BasicTextField2, 输入法交互, 文本选择",
                    activityClass = com.peter.compose.demo.level2.TextFieldAdvancedActivity::class.java,
                    color = Color(0xFF5D4037)
                )
            )
        ),
        DemoLevel(
            level = 3,
            title = "状态与交互",
            description = "状态管理与手势处理",
            color = Color(0xFFE91E63),
            items = listOf(
                DemoItem(
                    title = "状态基础",
                    description = "mutableStateOf, remember",
                    activityClass = com.peter.compose.demo.level2.StateBasicsActivity::class.java,
                    color = Color(0xFFF06292)
                ),
                DemoItem(
                    title = "状态提升",
                    description = "状态提升模式与最佳实践",
                    activityClass = com.peter.compose.demo.level2.StateHoistingActivity::class.java,
                    color = Color(0xFFEC407A)
                ),
                DemoItem(
                    title = "副作用",
                    description = "LaunchedEffect, DisposableEffect",
                    activityClass = com.peter.compose.demo.level2.SideEffectsActivity::class.java,
                    color = Color(0xFFD81B60)
                ),
                DemoItem(
                    title = "手势处理",
                    description = "clickable, transformable, 多点触控, 拖拽",
                    activityClass = com.peter.compose.demo.level2.GesturesActivity::class.java,
                    color = Color(0xFFC2185B)
                ),
                DemoItem(
                    title = "状态恢复",
                    description = "rememberSaveable, 自定义 Saver",
                    activityClass = com.peter.compose.demo.level3.StateRestoreActivity::class.java,
                    color = Color(0xFFAD1457)
                ),
                DemoItem(
                    title = "SnapshotFlow",
                    description = "State 转 Flow, 防抖搜索",
                    activityClass = com.peter.compose.demo.level3.SnapshotFlowActivity::class.java,
                    color = Color(0xFF880E4F)
                ),
            )
        ),
        DemoLevel(
            level = 4,
            title = "导航与架构",
            description = "导航、ViewModel、Flow 与依赖注入",
            color = Color(0xFF009688),
            items = listOf(
                DemoItem(
                    title = "Compose Navigation",
                    description = "NavHost, 导航参数, 深层链接",
                    activityClass = com.peter.compose.demo.level4.ComposeNavigationActivity::class.java,
                    color = Color(0xFF4DB6AC)
                ),
                DemoItem(
                    title = "ViewModel 集成",
                    description = "viewModel() 与状态持有",
                    activityClass = com.peter.compose.demo.level3.ViewModelIntegrationActivity::class.java,
                    color = Color(0xFF00897B)
                ),
                DemoItem(
                    title = "Flow/LiveData",
                    description = "collectAsState, observeAsState",
                    activityClass = com.peter.compose.demo.level3.FlowLiveDataActivity::class.java,
                    color = Color(0xFF00796B)
                ),
                DemoItem(
                    title = "MVI 模式",
                    description = "单向数据流架构",
                    activityClass = com.peter.compose.demo.level3.MviPatternActivity::class.java,
                    color = Color(0xFF00695C)
                ),
                DemoItem(
                    title = "依赖注入",
                    description = "Koin 集成与 ViewModel 注入",
                    activityClass = com.peter.compose.demo.level3.DependencyInjectionActivity::class.java,
                    color = Color(0xFF004D40)
                )
            )
        ),
        DemoLevel(
            level = 5,
            title = "列表与数据",
            description = "懒加载列表、分页与刷新",
            color = Color(0xFFFF9800),
            items = listOf(
                DemoItem(
                    title = "懒加载列表",
                    description = "LazyColumn, LazyRow, LazyGrid",
                    activityClass = com.peter.compose.demo.level4.LazyListsActivity::class.java,
                    color = Color(0xFFFFB74D)
                ),
                DemoItem(
                    title = "Paging 分页",
                    description = "Paging 3, PagingSource, LoadState",
                    activityClass = com.peter.compose.demo.level5.PagingActivity::class.java,
                    color = Color(0xFFF57C00)
                ),
                DemoItem(
                    title = "下拉刷新",
                    description = "PullToRefreshBox, 刷新状态管理",
                    activityClass = com.peter.compose.demo.level5.PullToRefreshActivity::class.java,
                    color = Color(0xFFEF6C00)
                ),
                DemoItem(
                    title = "CompositionLocal",
                    description = "作用域数据传递与主题切换",
                    activityClass = com.peter.compose.demo.level5.CompositionLocalActivity::class.java,
                    color = Color(0xFFE65100)
                )
            )
        ),
        DemoLevel(
            level = 6,
            title = "动画与图形",
            description = "动画效果与自定义绘制",
            color = Color(0xFF3F51B5),
            items = listOf(
                DemoItem(
                    title = "动画基础",
                    description = "animate*AsState, AnimatedVisibility",
                    activityClass = com.peter.compose.demo.level4.AnimationsActivity::class.java,
                    color = Color(0xFF7986CB)
                ),
                DemoItem(
                    title = "内容转换",
                    description = "Crossfade, AnimatedContent",
                    activityClass = com.peter.compose.demo.level4.ContentTransformActivity::class.java,
                    color = Color(0xFF5C6BC0)
                ),
                DemoItem(
                    title = "共享元素转场",
                    description = "SharedTransitionLayout, sharedElement",
                    activityClass = com.peter.compose.demo.level6.SharedElementTransitionActivity::class.java,
                    color = Color(0xFF3F51B5)
                ),
                DemoItem(
                    title = "Canvas 绘制",
                    description = "自定义绘制与图形",
                    activityClass = com.peter.compose.demo.level4.CanvasDrawingActivity::class.java,
                    color = Color(0xFF303F9F)
                ),
                DemoItem(
                    title = "无限 & 物理动画",
                    description = "InfiniteTransition, spring, animateDecay",
                    activityClass = com.peter.compose.demo.level6.InfiniteTransitionActivity::class.java,
                    color = Color(0xFF1A237E)
                ),
                DemoItem(
                    title = "GraphicsLayer",
                    description = "模糊效果, 毛玻璃, 自定义绘制",
                    activityClass = com.peter.compose.demo.level6.GraphicsLayerActivity::class.java,
                    color = Color(0xFF283593)
                )
            )
        ),
        DemoLevel(
            level = 7,
            title = "高级进阶",
            description = "自定义布局、性能优化与测试",
            color = Color(0xFF455A64),
            items = listOf(
                DemoItem(
                    title = "自定义布局",
                    description = "Layout, MeasurePolicy",
                    activityClass = com.peter.compose.demo.level5.CustomLayoutActivity::class.java,
                    color = Color(0xFF78909C)
                ),
                DemoItem(
                    title = "自适应布局",
                    description = "WindowSizeClass, 响应式UI",
                    activityClass = com.peter.compose.demo.level7.AdaptiveLayoutActivity::class.java,
                    color = Color(0xFF607D8B)
                ),
                DemoItem(
                    title = "拖拽排序",
                    description = "LazyColumn 拖拽, 视觉反馈",
                    activityClass = com.peter.compose.demo.level7.DragAndDropActivity::class.java,
                    color = Color(0xFF546E7A)
                ),
                DemoItem(
                    title = "性能优化",
                    description = "重组优化, derivedStateOf",
                    activityClass = com.peter.compose.demo.level5.PerformanceActivity::class.java,
                    color = Color(0xFF455A64)
                ),
                DemoItem(
                    title = "互操作",
                    description = "AndroidView 与 View 互操作",
                    activityClass = com.peter.compose.demo.level5.InteropActivity::class.java,
                    color = Color(0xFF37474F)
                ),
                DemoItem(
                    title = "Compose Testing",
                    description = "createComposeRule, 语义测试",
                    activityClass = com.peter.compose.demo.level7.ComposeTestingActivity::class.java,
                    color = Color(0xFF263238)
                ),
                DemoItem(
                    title = "稳定性优化",
                    description = "@Stable, @Immutable, skippability",
                    activityClass = com.peter.compose.demo.level7.StabilityActivity::class.java,
                    color = Color(0xFF1B5E20)
                ),
                DemoItem(
                    title = "富文本",
                    description = "AnnotatedString, 可点击链接",
                    activityClass = com.peter.compose.demo.level7.AnnotatedStringActivity::class.java,
                    color = Color(0xFF004D40)
                ),
                DemoItem(
                    title = "Scaffold 进阶",
                    description = "BottomSheet, Drawer, Snackbar",
                    activityClass = com.peter.compose.demo.level7.ScaffoldAdvancedActivity::class.java,
                    color = Color(0xFF4E342E)
                ),
                DemoItem(
                    title = "自定义 Modifier",
                    description = "drawBehind, pointerInput, 渐变边框",
                    activityClass = com.peter.compose.demo.level7.CustomModifierActivity::class.java,
                    color = Color(0xFF3E2723)
                )
            )
        )
    )
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val demoLevels = getDemoLevels()
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // 标题
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Jetpack Compose",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "从入门到进阶的学习示例",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "7 个层级 · 39 个示例",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                )
            }
        }

        // 列表
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            demoLevels.forEach { level ->
                // 层级标题
                item {
                    LevelHeader(
                        level = level.level,
                        title = level.title,
                        description = level.description,
                        color = level.color
                    )
                }

                // 层级下的示例
                items(level.items, key = { it.title }) { item ->
                    DemoItemCard(
                        item = item,
                        onClick = {
                            context.startActivity(
                                Intent(context, item.activityClass)
                            )
                        }
                    )
                }

                // 层级间距
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun LevelHeader(
    level: Int,
    title: String,
    description: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 层级标识
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "L$level",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = color
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoItemCard(
    item: DemoItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 颜色标识
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(item.color)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 箭头
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "进入",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MaterialTheme {
        MainScreen()
    }
}
