package com.peter.compose.demo.level7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import kotlinx.coroutines.launch

/**
 * AdaptiveLayoutActivity - 自适应布局
 *
 * 学习目标：
 * 1. WindowSizeClass 概念与 API 使用
 * 2. 根据窗口宽度等级切换导航模式（NavigationBar / NavigationRail / NavigationDrawer）
 * 3. 根据窗口尺寸自适应内容列数（单列 / 两列 / 三列 + 详情）
 * 4. 实际响应式 UI 示例（类邮件应用）
 */

// ========== 数据模型 ==========

/**
 * 导航项数据
 */
data class NavItem(
    val label: String,
    val icon: ImageVector
)

/**
 * 邮件数据
 */
data class EmailItem(
    val id: Int,
    val sender: String,
    val subject: String,
    val preview: String,
    val body: String,
    val color: Color
)

// 导航项列表
val navItems = listOf(
    NavItem("收件箱", Icons.Default.Inbox),
    NavItem("星标", Icons.Default.Star),
    NavItem("搜索", Icons.Default.Search),
    NavItem("设置", Icons.Default.Settings)
)

// 示例邮件数据
val sampleEmails = listOf(
    EmailItem(1, "张三", "关于项目进度的更新",
        "你好，请查看附件中的项目进度报告...",
        "你好，\n\n请查看附件中的项目进度报告。本周我们完成了以下工作：\n\n1. 完成了用户界面的自适应布局设计\n2. 实现了 WindowSizeClass 的集成\n3. 测试了不同设备上的显示效果\n\n请尽快回复。\n\n谢谢！\n张三",
        Color(0xFFE91E63)),
    EmailItem(2, "李四", "会议通知：周五技术分享",
        "本周五下午3点在会议室B举行技术分享会...",
        "Hi 团队，\n\n本周五下午3点在会议室B举行技术分享会，主题为：\n\n「Jetpack Compose 自适应布局最佳实践」\n\n议程：\n- WindowSizeClass 概念介绍\n- 不同屏幕尺寸的布局策略\n- 实际案例分析\n\n欢迎大家参加！\n\n李四",
        Color(0xFF2196F3)),
    EmailItem(3, "王五", "代码审查请求",
        "请帮忙审查 PR #42，关于自适应导航的实现...",
        "你好，\n\n我提交了一个新的 Pull Request (#42)，主要改动包括：\n\n- 添加了 WindowSizeClass 计算\n- 实现了三种导航模式的切换\n- 添加了响应式邮件列表布局\n\n请帮忙审查代码，谢谢！\n\n王五",
        Color(0xFF4CAF50)),
    EmailItem(4, "赵六", "设计稿已更新",
        "最新版设计稿已上传到 Figma，请查收...",
        "团队好，\n\n最新版设计稿已上传到 Figma：\n\n- 手机端：底部导航 + 单列列表\n- 平板竖屏：侧边导航栏 + 双列网格\n- 平板横屏：抽屉导航 + 列表详情分屏\n\n请大家查看并提出意见。\n\n赵六",
        Color(0xFFFF9800)),
    EmailItem(5, "孙七", "Bug 修复报告",
        "已修复自适应布局在折叠屏上的显示问题...",
        "大家好，\n\n已修复以下问题：\n\n1. 折叠屏展开时布局没有及时更新\n2. 窗口尺寸变化时导航模式切换闪烁\n3. Expanded 模式下邮件详情区域宽度计算错误\n\n修复已合入 main 分支，请验证。\n\n孙七",
        Color(0xFF9C27B0)),
    EmailItem(6, "周八", "新功能提案：多窗口支持",
        "建议在下一版本中支持多窗口和自由窗口模式...",
        "团队好，\n\n我提议在下一版本中加入多窗口支持：\n\n1. 自由窗口模式下的布局适配\n2. 拖拽分屏时的平滑过渡\n3. 窗口尺寸变化的实时响应\n\n初步技术方案已附在邮件中，欢迎讨论。\n\n周八",
        Color(0xFF00BCD4))
)

// ========== Activity ==========

class AdaptiveLayoutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    AdaptiveLayoutScreen(
                        modifier = Modifier.padding(innerPadding),
                        activity = this@AdaptiveLayoutActivity
                    )
                }
            }
        }
    }
}

// ========== 主屏幕 ==========

@Composable
fun AdaptiveLayoutScreen(
    modifier: Modifier = Modifier,
    activity: ComponentActivity
) {
    // 计算当前窗口的 SizeClass
    val windowSizeClass = calculateWindowSizeClass(activity = activity)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 页面标题
        Text(
            text = "Adaptive Layout",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // 1. WindowSizeClass 概念说明
        WindowSizeClassConceptSection()

        // 2. 当前窗口信息
        CurrentWindowInfoSection(windowSizeClass = windowSizeClass)

        // 3. 自适应导航
        AdaptiveNavigationSection(windowSizeClass = windowSizeClass)

        // 4. 自适应内容布局
        AdaptiveContentLayoutSection(windowSizeClass = windowSizeClass)

        // 5. 响应式 UI 示例（类邮件应用）
        ResponsiveEmailUiSection(windowSizeClass = windowSizeClass)
    }
}

// ========== 1. WindowSizeClass 概念说明 ==========

@Composable
fun WindowSizeClassConceptSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "1. WindowSizeClass 概念",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "WindowSizeClass 是 Material3 提供的窗口尺寸分类系统，用于构建自适应布局。" +
                        "它将窗口尺寸划分为几个离散的等级，开发者可以根据不同等级提供不同的布局。",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "宽度等级（WindowWidthSizeClass）：",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            // 宽度等级表格
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SizeClassRow("Compact", "< 600dp", "手机竖屏", Color(0xFF4CAF50))
                SizeClassRow("Medium", "600dp - 840dp", "平板竖屏 / 折叠屏", Color(0xFFFF9800))
                SizeClassRow("Expanded", "> 840dp", "平板横屏 / 桌面", Color(0xFF2196F3))
            }

            Text(
                text = "高度等级（WindowHeightSizeClass）：",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SizeClassRow("Compact", "< 480dp", "横屏模式", Color(0xFF4CAF50))
                SizeClassRow("Medium", "480dp - 900dp", "大多数设备", Color(0xFFFF9800))
                SizeClassRow("Expanded", "> 900dp", "折叠屏 / 竖屏平板", Color(0xFF2196F3))
            }

            Text(
                text = "核心 API：calculateWindowSizeClass(activity) 返回当前窗口的尺寸分类。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 尺寸等级行组件
 */
@Composable
fun SizeClassRow(name: String, range: String, desc: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = range,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = desc,
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
    }
}

// ========== 2. 当前窗口信息 ==========

@Composable
fun CurrentWindowInfoSection(windowSizeClass: WindowSizeClass) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "2. 当前窗口信息",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            // 宽度等级
            InfoRow(
                label = "宽度等级",
                value = when (windowSizeClass.widthSizeClass) {
                    WindowWidthSizeClass.Compact -> "Compact"
                    WindowWidthSizeClass.Medium -> "Medium"
                    WindowWidthSizeClass.Expanded -> "Expanded"
                    else -> "Unknown"
                },
                description = when (windowSizeClass.widthSizeClass) {
                    WindowWidthSizeClass.Compact -> "适合使用底部导航和单列布局"
                    WindowWidthSizeClass.Medium -> "适合使用侧边导航和双列布局"
                    WindowWidthSizeClass.Expanded -> "适合使用抽屉导航和多列布局"
                    else -> ""
                }
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))

            // 高度等级
            InfoRow(
                label = "高度等级",
                value = when (windowSizeClass.heightSizeClass) {
                    WindowHeightSizeClass.Compact -> "Compact"
                    WindowHeightSizeClass.Medium -> "Medium"
                    WindowHeightSizeClass.Expanded -> "Expanded"
                    else -> "Unknown"
                },
                description = when (windowSizeClass.heightSizeClass) {
                    WindowHeightSizeClass.Compact -> "横屏模式，注意横向空间利用"
                    WindowHeightSizeClass.Medium -> "标准高度，大多数设备"
                    WindowHeightSizeClass.Expanded -> "竖屏模式，充分利用纵向空间"
                    else -> ""
                }
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))

            // 建议布局提示
            val suggestion = when (windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Compact -> "建议：使用 NavigationBar + 单列列表"
                WindowWidthSizeClass.Medium -> "建议：使用 NavigationRail + 双列网格"
                WindowWidthSizeClass.Expanded -> "建议：使用 NavigationDrawer + 列表详情分屏"
                else -> ""
            }
            Text(
                text = suggestion,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/**
 * 信息行组件
 */
@Composable
fun InfoRow(label: String, value: String, description: String) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.onPrimaryContainer)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

// ========== 3. 自适应导航 ==========

@Composable
fun AdaptiveNavigationSection(windowSizeClass: WindowSizeClass) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "3. 自适应导航 (Adaptive Navigation)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "根据 WindowWidthSizeClass 自动选择合适的导航组件：\n" +
                        "- Compact: NavigationBar（底部导航）\n" +
                        "- Medium: NavigationRail（侧边导航栏）\n" +
                        "- Expanded: PermanentNavigationDrawer（永久抽屉）",
                style = MaterialTheme.typography.bodyMedium
            )

            // 当前导航模式提示
            val currentMode = when (windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Compact -> "NavigationBar（底部导航）"
                WindowWidthSizeClass.Medium -> "NavigationRail（侧边导航）"
                WindowWidthSizeClass.Expanded -> "PermanentNavigationDrawer（永久抽屉）"
                else -> "未知"
            }
            Text(
                text = "当前模式：$currentMode",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary
            )

            // 实际渲染对应的导航组件预览
            when (windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Compact -> NavigationBarPreview()
                WindowWidthSizeClass.Medium -> NavigationRailPreview()
                WindowWidthSizeClass.Expanded -> NavigationDrawerPreview()
            }
        }
    }
}

/**
 * NavigationBar（底部导航）预览 - Compact 模式
 */
@Composable
fun NavigationBarPreview() {
    var selectedIndex by remember { mutableIntStateOf(0) }

    Column {
        // 模拟页面内容
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "页面内容区域",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // NavigationBar
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
        ) {
            navItems.forEachIndexed { index, item ->
                NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                    selected = selectedIndex == index,
                    onClick = { selectedIndex = index }
                )
            }
        }
    }
}

/**
 * NavigationRail（侧边导航）预览 - Medium 模式
 */
@Composable
fun NavigationRailPreview() {
    var selectedIndex by remember { mutableIntStateOf(0) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        // NavigationRail（侧边）
        NavigationRail {
            navItems.forEachIndexed { index, item ->
                NavigationRailItem(
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                    selected = selectedIndex == index,
                    onClick = { selectedIndex = index }
                )
            }
        }

        // 内容区域
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "内容区域",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * PermanentNavigationDrawer（永久抽屉）预览 - Expanded 模式
 */
@Composable
fun NavigationDrawerPreview() {
    var selectedIndex by remember { mutableIntStateOf(0) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        // 模拟永久抽屉
        Column(
            modifier = Modifier
                .width(140.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "应用名称",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
            )
            navItems.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .then(
                            if (selectedIndex == index) Modifier.background(
                                MaterialTheme.colorScheme.primaryContainer
                            ) else Modifier
                        )
                        .clickable { selectedIndex = index }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(18.dp),
                        tint = if (selectedIndex == index) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (selectedIndex == index) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 内容区域
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "内容区域",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ========== 4. 自适应内容布局 ==========

@Composable
fun AdaptiveContentLayoutSection(windowSizeClass: WindowSizeClass) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "4. 自适应内容布局",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // 当前布局模式提示
            val layoutDesc = when (windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Compact -> "单列垂直列表 - 适合小屏幕逐项浏览"
                WindowWidthSizeClass.Medium -> "双列网格 - 充分利用横向空间"
                WindowWidthSizeClass.Expanded -> "三列网格 + 详情面板 - 列表与详情并排"
                else -> ""
            }
            Text(
                text = "当前布局：$layoutDesc",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary
            )

            // 示例数据
            val gridItems = listOf("Alpha", "Beta", "Gamma", "Delta", "Epsilon", "Zeta")

            // 根据宽度等级选择不同布局
            when (windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Compact -> {
                    // 单列垂直列表
                    CompactListLayout(items = gridItems)
                }
                WindowWidthSizeClass.Medium -> {
                    // 双列网格
                    MediumGridLayout(items = gridItems)
                }
                WindowWidthSizeClass.Expanded -> {
                    // 三列网格 + 详情
                    ExpandedListDetailLayout(items = gridItems)
                }
            }
        }
    }
}

/**
 * Compact 模式：单列列表
 */
@Composable
fun CompactListLayout(items: List<String>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(items) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.first().toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

/**
 * Medium 模式：双列网格
 */
@Composable
fun MediumGridLayout(items: List<String>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { item ->
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.first().toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**
 * Expanded 模式：三列网格 + 详情面板
 */
@Composable
fun ExpandedListDetailLayout(items: List<String>) {
    var selectedItem by remember { mutableStateOf(items.firstOrNull() ?: "") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        // 左侧列表（40% 宽度）
        LazyColumn(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(items) { item ->
                val isSelected = selectedItem == item
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (isSelected) Modifier.background(
                                MaterialTheme.colorScheme.primaryContainer
                            ) else Modifier
                        )
                        .clickable { selectedItem = item }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.first().toString(),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // 右侧详情面板（60% 宽度）
        Column(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = selectedItem,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "这是 $selectedItem 的详情内容。在 Expanded 模式下，列表和详情面板并排显示，" +
                        "用户可以在左侧选择项目，右侧实时查看详情。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ========== 5. 响应式 UI 示例（类邮件应用）==========

@Composable
fun ResponsiveEmailUiSection(windowSizeClass: WindowSizeClass) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "5. 响应式 UI 示例（类邮件应用）",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // 当前邮件布局提示
            val emailLayoutDesc = when (windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Compact -> "全屏邮件列表，点击查看详情"
                WindowWidthSizeClass.Medium -> "左侧邮件列表(40%) + 右侧详情(60%)"
                WindowWidthSizeClass.Expanded -> "导航抽屉 + 邮件列表 + 邮件详情三栏布局"
                else -> ""
            }
            Text(
                text = "当前布局：$emailLayoutDesc",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary
            )

            // 根据宽度等级选择邮件布局
            when (windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Compact -> CompactEmailLayout()
                WindowWidthSizeClass.Medium -> MediumEmailLayout()
                WindowWidthSizeClass.Expanded -> ExpandedEmailLayout()
            }
        }
    }
}

/**
 * Compact 模式：全屏邮件列表，点击查看详情
 */
@Composable
fun CompactEmailLayout() {
    var selectedEmailId by remember { mutableIntStateOf(-1) }

    if (selectedEmailId == -1) {
        // 邮件列表视图
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(sampleEmails) { email ->
                EmailListItem(
                    email = email,
                    onClick = { selectedEmailId = email.id }
                )
            }
        }
    } else {
        // 邮件详情视图
        val email = sampleEmails.find { it.id == selectedEmailId }
        if (email != null) {
            EmailDetailView(
                email = email,
                onBack = { selectedEmailId = -1 }
            )
        }
    }
}

/**
 * Medium 模式：左右分栏
 */
@Composable
fun MediumEmailLayout() {
    var selectedEmailId by remember { mutableIntStateOf(sampleEmails.first().id) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        // 左侧邮件列表（40%）
        LazyColumn(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(sampleEmails) { email ->
                val isSelected = selectedEmailId == email.id
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (isSelected) Modifier.background(
                                MaterialTheme.colorScheme.primaryContainer
                            ) else Modifier
                        )
                        .clickable { selectedEmailId = email.id }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = email.sender,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                    Text(
                        text = email.subject,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // 右侧邮件详情（60%）
        val email = sampleEmails.find { it.id == selectedEmailId }
        if (email != null) {
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
                    .padding(12.dp)
            ) {
                Text(
                    text = email.subject,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "来自：${email.sender}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = email.body,
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 16.sp,
                    maxLines = 8,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Expanded 模式：三栏布局（导航抽屉 + 邮件列表 + 邮件详情）
 */
@Composable
fun ExpandedEmailLayout() {
    var selectedEmailId by remember { mutableIntStateOf(sampleEmails.first().id) }
    var selectedNavIndex by remember { mutableIntStateOf(0) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        // 第一栏：导航抽屉（约20%宽度）
        Column(
            modifier = Modifier
                .width(100.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(6.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "邮件",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp)
            )
            navItems.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .then(
                            if (selectedNavIndex == index) Modifier.background(
                                MaterialTheme.colorScheme.primaryContainer
                            ) else Modifier
                        )
                        .clickable { selectedNavIndex = index }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(14.dp),
                        tint = if (selectedNavIndex == index) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // 第二栏：邮件列表（约30%宽度）
        LazyColumn(
            modifier = Modifier
                .weight(0.3f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surface),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(sampleEmails) { email ->
                val isSelected = selectedEmailId == email.id
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (isSelected) Modifier.background(
                                MaterialTheme.colorScheme.primaryContainer
                            ) else Modifier
                        )
                        .clickable { selectedEmailId = email.id }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = email.sender,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = email.subject,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // 第三栏：邮件详情（约50%宽度）
        val email = sampleEmails.find { it.id == selectedEmailId }
        if (email != null) {
            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxHeight()
                    .padding(10.dp)
            ) {
                Text(
                    text = email.subject,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(email.color),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = email.sender.first().toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = email.sender,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = email.body,
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 14.sp,
                    maxLines = 10,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ========== 通用组件 ==========

/**
 * 邮件列表项组件
 */
@Composable
fun EmailListItem(
    email: EmailItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 头像
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(email.color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = email.sender.first().toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 邮件内容
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = email.sender,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = email.subject,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = email.preview,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * 邮件详情视图组件
 */
@Composable
fun EmailDetailView(
    email: EmailItem,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        // 返回按钮
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onBack)
                .padding(vertical = 8.dp)
        ) {
            Icon(
                Icons.Default.Menu,
                contentDescription = "返回",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "返回列表",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // 发件人信息
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(email.color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = email.sender.first().toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Column {
                Text(
                    text = email.sender,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = email.subject,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 邮件正文
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = email.body,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )
        }
    }
}
