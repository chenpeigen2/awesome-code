package com.peter.compose.demo.level4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIntoContainer
import androidx.compose.animation.slideOutOfContainer
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

/**
 * ComposeNavigationActivity - Compose Navigation 导航组件
 *
 * 学习目标：
 * 1. NavHost 和 NavController 的基本使用
 * 2. 路由定义和页面跳转
 * 3. 路径参数传递 (必选参数)
 * 4. 查询参数传递 (可选参数)
 * 5. Bottom Navigation 与 NavHost 结合
 * 6. 页面切换动画配置
 * 7. DeepLink 概念理解
 *
 * Navigation 是 Compose 中管理页面跳转的核心组件，替代了传统 Fragment-based Navigation。
 * 它基于路由字符串（route）来定义目标页面，支持参数传递、深层链接和转场动画。
 */
class ComposeNavigationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    NavigationDemoScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// ============================================================
// 导航路由常量定义
// ============================================================

/** 首页路由 */
private const val ROUTE_HOME = "home"

/** 搜索页路由 */
private const val ROUTE_SEARCH = "search"

/** 设置页路由 */
private const val ROUTE_SETTINGS = "settings"

/** 详情页路由 - 带必选参数 itemId */
private const val ROUTE_DETAIL = "detail/{itemId}"

/** 个人资料页路由 - 带可选查询参数 name */
private const val ROUTE_PROFILE = "profile?name={name}"

// ============================================================
// 主界面：上方概念概览 + 下方实时导航演示
// ============================================================

/**
 * 导航演示主屏幕
 *
 * 布局分为两部分：
 * - 上方：滚动展示 Navigation 核心概念卡片
 * - 下方（约 60%）：实际运行的导航演示区域
 */
@Composable
fun NavigationDemoScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // 第一部分：概念概览区域（可滚动）
        OverviewSection(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f)
        )

        // 分隔线
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )

        // 第二部分：实时导航演示区域
        LiveNavigationDemo(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
        )
    }
}

// ============================================================
// Part 1: 概念概览区域
// ============================================================

/**
 * 概念概览区域
 *
 * 以卡片形式展示 Navigation 的核心概念，
 * 帮助理解每个组件的作用和用法。
 */
@Composable
fun OverviewSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 区域标题
        Text(
            text = "Navigation 核心概念",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Jetpack Compose Navigation 是基于路由的导航框架，取代了传统 Fragment 导航。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // 概念卡片1：NavHost
        ConceptCard(
            title = "NavHost",
            description = "NavHost 是导航的容器组件，它管理所有路由页面。需要提供 navController 和起始路由。" +
                    "\n\n用法：NavHost(navController, startDestination = \"home\") { ... }",
            codeExample = "NavHost(\n  navController,\n  startDestination = \"home\"\n) {\n  composable(\"home\") { ... }\n}",
            color = MaterialTheme.colorScheme.primaryContainer,
            onColor = MaterialTheme.colorScheme.onPrimaryContainer
        )

        // 概念卡片2：路由定义
        ConceptCard(
            title = "Route 路由定义",
            description = "路由是字符串，用于标识一个目标页面。可以在路由中嵌入参数。" +
                    "\n\n简单路由：\"home\", \"settings\"" +
                    "\n带参数路由：\"detail/{itemId}\"",
            codeExample = "// 定义路由\ncomposable(\"home\") { HomeScreen() }\n\n// 带参数的路由\ncomposable(\n  \"detail/{itemId}\",\n  arguments = listOf(\n    navArgument(\"itemId\") {\n      type = NavType.StringType\n    }\n  )\n) { backStackEntry ->\n  val id = backStackEntry.arguments\n    ?.getString(\"itemId\")\n  DetailScreen(id)\n}",
            color = MaterialTheme.colorScheme.secondaryContainer,
            onColor = MaterialTheme.colorScheme.onSecondaryContainer
        )

        // 概念卡片3：参数传递
        ConceptCard(
            title = "参数传递",
            description = "Navigation 支持两种参数传递方式：" +
                    "\n1. 路径参数（必选）：\"detail/{itemId}\"，导航时必须提供值" +
                    "\n2. 查询参数（可选）：\"profile?name={name}\"，有默认值" +
                    "\n\n路径参数是 URL 的一部分，查询参数跟在 ? 后面。",
            codeExample = "// 路径参数（必选）\ncomposable(\n  route = \"detail/{itemId}\",\n  arguments = listOf(\n    navArgument(\"itemId\") {\n      type = NavType.StringType\n    }\n  )\n) { /* ... */ }\n\n// 查询参数（可选）\ncomposable(\n  route = \"profile?name={name}\",\n  arguments = listOf(\n    navArgument(\"name\") {\n      type = NavType.StringType\n      defaultValue = \"Guest\"\n    }\n  )\n) { /* ... */ }",
            color = MaterialTheme.colorScheme.tertiaryContainer,
            onColor = MaterialTheme.colorScheme.onTertiaryContainer
        )

        // 概念卡片4：DeepLink
        ConceptCard(
            title = "DeepLink 深层链接",
            description = "DeepLink 允许外部（如浏览器、其他应用）直接跳转到应用内的特定页面。" +
                    "\n\n通过 URI pattern 匹配，可以将网页链接映射到对应的 Composable 页面。" +
                    "\n\n在 composable() 中添加 deepLinks 参数即可支持。",
            codeExample = "composable(\n  route = \"detail/{itemId}\",\n  deepLinks = listOf(\n    navDeepLink {\n      uriPattern =\n        \"https://example.com/item/{itemId}\"\n    }\n  )\n) { /* ... */ }",
            color = MaterialTheme.colorScheme.errorContainer,
            onColor = MaterialTheme.colorScheme.onErrorContainer
        )

        // 概念卡片5：NavController
        ConceptCard(
            title = "NavController",
            description = "NavController 是导航的核心控制器，负责管理回退栈和页面跳转。" +
                    "\n\n主要方法：" +
                    "\n- navigate(route): 跳转到指定路由" +
                    "\n- navigateUp(): 返回上一页" +
                    "\n- popBackStack(): 弹出回退栈" +
                    "\n\n在 Compose 中通过 rememberNavController() 创建。",
            codeExample = "val navController =\n  rememberNavController()\n\n// 跳转\nnavController.navigate(\"detail/42\")\n\n// 返回\nnavController.navigateUp()\nnavController.popBackStack()",
            color = MaterialTheme.colorScheme.inverseSurface,
            onColor = MaterialTheme.colorScheme.inverseOnSurface
        )
    }
}

/**
 * 概念卡片组件
 *
 * 用于展示一个 Navigation 核心概念，包含标题、描述和代码示例。
 *
 * @param title 概念标题
 * @param description 概念描述
 * @param codeExample 代码示例
 * @param color 卡片背景色
 * @param onColor 卡片上的文字颜色
 */
@Composable
fun ConceptCard(
    title: String,
    description: String,
    codeExample: String,
    color: Color,
    onColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = onColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 描述文字
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = onColor.copy(alpha = 0.85f),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 代码示例区域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E1E1E))
                    .padding(12.dp)
            ) {
                Text(
                    text = codeExample,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9CDCFE), // VS Code 浅蓝色
                    lineHeight = 16.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        }
    }
}

// ============================================================
// Part 2: 实时导航演示区域
// ============================================================

/**
 * 实时导航演示区域
 *
 * 包含一个完整的 NavHost 和底部导航栏，
 * 演示页面跳转、参数传递和转场动画。
 *
 * @param modifier 修饰符
 */
@Composable
fun LiveNavigationDemo(modifier: Modifier = Modifier) {
    // 创建 NavController，它是导航的核心控制器
    val navController = rememberNavController()

    // 监听当前路由状态，用于底部导航栏高亮
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Column(modifier = modifier) {
        // 导航演示标题栏
        LiveDemoHeader(modifier = Modifier.fillMaxWidth())

        // NavHost 容器 - 管理所有页面和导航逻辑
        // modifier 中使用 weight(1f) 让 NavHost 占据剩余空间
        NavHost(
            navController = navController,
            startDestination = ROUTE_HOME,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            // 配置页面切换动画
            // 进入动画：从右侧滑入
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                )
            },
            // 退出动画：向左侧滑出
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                )
            },
            // 返回时进入动画：从左侧滑入
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300)
                )
            },
            // 返回时退出动画：向右侧滑出
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300)
                )
            }
        ) {
            // ---- 首页路由 ----
            // 展示一个列表，点击列表项可跳转到详情页
            composable(ROUTE_HOME) {
                HomeDestination(
                    onItemClick = { itemId ->
                        // 使用路径参数跳转：拼接 itemId 到路由字符串中
                        navController.navigate("detail/$itemId")
                    }
                )
            }

            // ---- 搜索路由 ----
            composable(ROUTE_SEARCH) {
                SearchDestination()
            }

            // ---- 设置路由 ----
            composable(ROUTE_SETTINGS) {
                SettingsDestination(
                    onViewProfile = { name ->
                        // 使用查询参数跳转：name 作为可选参数拼接到路由中
                        navController.navigate("profile?name=$name")
                    }
                )
            }

            // ---- 详情路由（带必选路径参数 itemId） ----
            // 路由格式：detail/{itemId}
            // itemId 是路径的一部分，导航时必须提供
            composable(
                route = ROUTE_DETAIL,
                arguments = listOf(
                    navArgument("itemId") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                // 从回退栈条目中提取参数
                val itemId = backStackEntry.arguments?.getString("itemId") ?: "unknown"
                DetailDestination(
                    itemId = itemId,
                    onBack = {
                        // 返回上一页
                        navController.navigateUp()
                    }
                )
            }

            // ---- 个人资料路由（带可选查询参数 name） ----
            // 路由格式：profile?name={name}
            // name 是查询参数，有默认值 "Guest"
            composable(
                route = ROUTE_PROFILE,
                arguments = listOf(
                    navArgument("name") {
                        type = NavType.StringType
                        // 设置默认值，使参数变为可选
                        defaultValue = "Guest"
                    }
                )
            ) { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: "Guest"
                ProfileDestination(
                    name = name,
                    onBack = {
                        navController.navigateUp()
                    }
                )
            }
        }

        // 底部导航栏 - 只在主 tab 页面显示
        // 详情页和个人资料页不属于底部 tab，不需要显示
        if (currentRoute in listOf(ROUTE_HOME, ROUTE_SEARCH, ROUTE_SETTINGS)) {
            BottomNavigationBar(
                currentRoute = currentRoute ?: ROUTE_HOME,
                onTabSelected = { route ->
                    // 使用 saveState 和 restoreState 来保持 tab 页面状态
                    navController.navigate(route) {
                        // 从导航的起始位置开始，避免多层嵌套
                        popUpTo(ROUTE_HOME) {
                            saveState = true
                        }
                        // 避免多次创建同一个页面实例
                        launchSingleTop = true
                        // 恢复之前保存的状态
                        restoreState = true
                    }
                }
            )
        }
    }
}

// ============================================================
// 导航演示标题栏
// ============================================================

/**
 * 演示区域顶部标题
 */
@Composable
fun LiveDemoHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "实时导航演示",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "点击底部导航切换页面 | 点击列表项查看详情",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

// ============================================================
// 底部导航栏
// ============================================================

/**
 * 底部导航栏数据类
 *
 * @property route 路由路径
 * @property label 显示标签
 * @property icon 图标
 */
private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

/**
 * 底部导航栏配置列表
 */
private val bottomNavItems = listOf(
    BottomNavItem(
        route = ROUTE_HOME,
        label = "首页",
        icon = Icons.Default.Home
    ),
    BottomNavItem(
        route = ROUTE_SEARCH,
        label = "搜索",
        icon = Icons.Default.Search
    ),
    BottomNavItem(
        route = ROUTE_SETTINGS,
        label = "设置",
        icon = Icons.Default.Settings
    )
)

/**
 * 底部导航栏组件
 *
 * 使用 NavigationBar 组件实现底部 Tab 切换，
 * 通过 currentRoute 判断当前选中项并高亮显示。
 *
 * @param currentRoute 当前激活的路由
 * @param onTabSelected Tab 被选中时的回调
 */
@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                // 判断当前路由是否匹配
                selected = currentRoute == item.route,
                onClick = {
                    onTabSelected(item.route)
                },
                // 自定义选中/未选中的颜色
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

// ============================================================
// 各个导航目的页面 Composable
// ============================================================

/**
 * 首页目的页面
 *
 * 展示一个包含 5 个列表项的简单列表。
 * 点击任意列表项会导航到详情页，并传递对应的 itemId。
 *
 * @param onItemClick 列表项点击回调，参数为 itemId
 */
@Composable
fun HomeDestination(
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // 首页数据 - 5 个演示项目
    val items = remember {
        listOf(
            HomeItem("1", "Jetpack Compose 基础", "学习 Compose 的核心概念和基本组件"),
            HomeItem("2", "State 状态管理", "理解 remember、mutableStateOf 和状态提升"),
            HomeItem("3", "Navigation 导航", "掌握 NavHost、路由和参数传递"),
            HomeItem("4", "Animation 动画", "探索 Compose 中的各种动画 API"),
            HomeItem("5", "Material Design 3", "使用 MD3 组件构建现代 UI")
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 页面标题
        Text(
            text = "首页",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "点击任意项目查看详情（带参数跳转）",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 列表展示
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { item ->
                HomeItemCard(
                    item = item,
                    onClick = { onItemClick(item.id) }
                )
            }
        }
    }
}

/**
 * 首页列表项数据类
 */
private data class HomeItem(
    val id: String,
    val title: String,
    val description: String
)

/**
 * 首页列表项卡片
 *
 * @param item 数据项
 * @param onClick 点击回调
 */
@Composable
fun HomeItemCard(
    item: HomeItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 序号标识
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.id,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 标题和描述
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 箭头指示
            Text(
                text = ">",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 18.sp
            )
        }
    }
}

/**
 * 详情目的页面
 *
 * 接收从路由中传递的 itemId 参数，展示详情内容。
 * 演示了 Navigation 路径参数的接收和使用方式。
 *
 * @param itemId 从路由路径参数中获取的项目 ID
 * @param onBack 返回按钮回调
 */
@Composable
fun DetailDestination(
    itemId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // 页面标题
        Text(
            text = "详情页",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 展示接收到的参数
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "接收到的参数",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 参数展示
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "itemId = ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = itemId,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "此参数通过路由 \"detail/{itemId}\" 传递",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 参数传递说明卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "参数传递流程",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "1. 首页点击列表项\n" +
                            "2. 调用 navController.navigate(\"detail/$itemId\")\n" +
                            "3. NavHost 匹配路由 \"detail/{itemId}\"\n" +
                            "4. 从 backStackEntry.arguments 中提取参数值\n" +
                            "5. 将参数传递给 DetailDestination Composable",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 返回按钮
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "返回上一页")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 返回说明
        Text(
            text = "使用 navController.navigateUp() 实现",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 搜索目的页面
 *
 * 展示搜索功能界面，包含搜索输入框和结果列表。
 * 演示 Navigation 中普通页面的构建方式。
 */
@Composable
fun SearchDestination(modifier: Modifier = Modifier) {
    // 搜索关键词状态
    var searchQuery by rememberSaveable { mutableStateOf("") }

    // 模拟搜索数据源
    val allItems = remember {
        listOf(
            "Jetpack Compose 入门教程",
            "Compose State 状态管理指南",
            "Navigation 导航最佳实践",
            "Material Design 3 组件库",
            "Compose Animation 动画详解",
            "Compose Layout 布局系统",
            "Compose Theming 主题定制",
            "ViewModel 与 Compose 集成",
            "Compose Side Effects 副作用",
            "Compose Performance 优化技巧",
            "Compose Testing 测试策略",
            "Compose Accessibility 无障碍"
        )
    }

    // 根据搜索关键词过滤结果
    val searchResults = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            allItems
        } else {
            allItems.filter { it.contains(searchQuery, ignoreCase = true) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 页面标题
        Text(
            text = "搜索",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 搜索输入框
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("搜索教程...") },
            placeholder = { Text("输入关键词") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 搜索结果统计
        Text(
            text = if (searchQuery.isBlank()) "全部教程 (${searchResults.size})" else "搜索 \"$searchQuery\" 的结果 (${searchResults.size})",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 搜索结果列表
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(searchResults) { result ->
                SearchResultItem(text = result)
            }
        }
    }
}

/**
 * 搜索结果列表项
 */
@Composable
fun SearchResultItem(
    text: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * 设置目的页面
 *
 * 展示设置选项，包含跳转到个人资料页的按钮。
 * 演示 Navigation 中可选查询参数的使用方式。
 *
 * @param onViewProfile 查看个人资料的回调，参数为用户名
 */
@Composable
fun SettingsDestination(
    onViewProfile: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 页面标题
        Text(
            text = "设置",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 设置选项列表
        SettingsItem(title = "通知设置", subtitle = "管理推送通知偏好")
        SettingsItem(title = "主题设置", subtitle = "选择深色或浅色主题")
        SettingsItem(title = "语言设置", subtitle = "当前：简体中文")
        SettingsItem(title = "缓存管理", subtitle = "清除缓存数据")

        Spacer(modifier = Modifier.height(24.dp))

        // 分隔区域
        Text(
            text = "查询参数演示",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 跳转到个人资料页的按钮
        // 演示可选查询参数传递
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "点击下方按钮跳转到个人资料页",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "将使用查询参数传递 name=Compose",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { onViewProfile("Compose") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "查看个人资料")
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "navController.navigate(\"profile?name=Compose\")",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.5f),
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        }
    }
}

/**
 * 设置列表项组件
 */
@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = ">",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp
            )
        }
    }
}

/**
 * 个人资料目的页面
 *
 * 接收可选的查询参数 name，展示参数值。
 * 演示 Navigation 中可选参数（查询参数）的接收和使用方式。
 *
 * @param name 从查询参数中获取的用户名，默认为 "Guest"
 * @param onBack 返回按钮回调
 */
@Composable
fun ProfileDestination(
    name: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // 页面标题
        Text(
            text = "个人资料",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 用户头像占位
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(1).uppercase(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 展示接收到的参数
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "接收到的可选参数",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "name = ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "此参数通过路由 \"profile?name={name}\" 传递\n默认值为 \"Guest\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 可选参数说明卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "可选参数 vs 必选参数",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "必选参数（路径参数）:\n" +
                            "  路由: \"detail/{itemId}\"\n" +
                            "  导航: navigate(\"detail/42\")\n" +
                            "  特点: 不提供值则无法匹配\n\n" +
                            "可选参数（查询参数）:\n" +
                            "  路由: \"profile?name={name}\"\n" +
                            "  导航: navigate(\"profile?name=Compose\")\n" +
                            "  特点: 有默认值，不提供则使用默认值",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    lineHeight = 18.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 返回按钮
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "返回设置页")
        }
    }
}

// ============================================================
// Preview 预览
// ============================================================

@Preview(showBackground = true, name = "Navigation Demo")
@Composable
fun NavigationDemoScreenPreview() {
    MaterialTheme {
        NavigationDemoScreen()
    }
}

@Preview(showBackground = true, name = "Home Destination")
@Composable
fun HomeDestinationPreview() {
    MaterialTheme {
        HomeDestination(
            onItemClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Detail Destination")
@Composable
fun DetailDestinationPreview() {
    MaterialTheme {
        DetailDestination(
            itemId = "42",
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Search Destination")
@Composable
fun SearchDestinationPreview() {
    MaterialTheme {
        SearchDestination()
    }
}

@Preview(showBackground = true, name = "Settings Destination")
@Composable
fun SettingsDestinationPreview() {
    MaterialTheme {
        SettingsDestination(
            onViewProfile = {}
        )
    }
}

@Preview(showBackground = true, name = "Profile Destination")
@Composable
fun ProfileDestinationPreview() {
    MaterialTheme {
        ProfileDestination(
            name = "Compose",
            onBack = {}
        )
    }
}
