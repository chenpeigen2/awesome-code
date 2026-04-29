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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
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
