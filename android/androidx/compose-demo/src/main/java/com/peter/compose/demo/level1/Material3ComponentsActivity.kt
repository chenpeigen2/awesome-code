package com.peter.compose.demo.level1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch

/**
 * Material3ComponentsActivity - Material3 完整组件演示
 *
 * 学习目标：
 * 1. 掌握 Material3 所有常用组件
 * 2. 理解各组件的状态管理方式
 * 3. 学会组件间的组合使用
 */
@OptIn(ExperimentalMaterial3Api::class)
class Material3ComponentsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    Material3ComponentsScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

/**
 * 通用区域标题组件
 */
@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

/**
 * 主滚动页面，展示所有 Material3 组件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Material3ComponentsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        // 页面标题
        Text(
            text = "Material3 组件大全",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        Text(
            text = "展示 Material3 所有常用组件的交互示例",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // 1. TopAppBar
        TopAppBarSection()

        // 2. NavigationBar
        NavigationBarSection()

        // 3. BottomSheetScaffold
        BottomSheetSection()

        // 4. Chips
        ChipsSection()

        // 5. Slider
        SliderSection()

        // 6. Switch / RadioButton / Checkbox
        ToggleSection()

        // 7. ProgressIndicator
        ProgressSection()

        // 8. Snackbar
        SnackbarSection()

        // 9. DatePicker
        DatePickerSection()

        // 10. Dialog
        DialogSection()

        // 11. FAB
        FabSection()

        // 12. Tooltip
        TooltipSection()

        // 13. ExposedDropdownMenu
        ExposedDropdownSection()

        // 底部间距
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ==================== 1. TopAppBar ====================

/**
 * TopAppBar - 顶部应用栏
 * 包含导航图标、标题、操作图标
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarSection() {
    SectionHeader(title = "1. TopAppBar 顶部应用栏")

    // 在 Box 中展示，限制高度，避免占满屏幕
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        TopAppBar(
            title = {
                Text(text = "示例标题")
            },
            navigationIcon = {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "导航菜单"
                    )
                }
            },
            actions = {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "搜索"
                    )
                }
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "通知"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
    }
}

// ==================== 2. NavigationBar ====================

/**
 * NavigationBar - 底部导航栏
 * 使用状态跟踪当前选中项
 */
@Composable
fun NavigationBarSection() {
    SectionHeader(title = "2. NavigationBar 底部导航栏")

    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("首页" to Icons.Default.Home, "搜索" to Icons.Default.Search, "设置" to Icons.Default.Settings)

    NavigationBar(
        modifier = Modifier.fillMaxWidth()
    ) {
        items.forEachIndexed { index, (label, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = selectedItem == index,
                onClick = { selectedItem = index }
            )
        }
    }

    Text(
        text = "当前选中: ${items[selectedItem].first}",
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(top = 8.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

// ==================== 3. BottomSheetScaffold ====================

/**
 * BottomSheetScaffold - 底部抽屉
 * 使用 rememberStandardBottomSheetState 控制展开/收起
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetSection() {
    SectionHeader(title = "3. BottomSheetScaffold 底部抽屉")

    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    // 使用局部 BottomSheetScaffold 嵌套展示，限制高度避免布局冲突
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "底部抽屉内容",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "这是 BottomSheetScaffold 的 sheetContent 区域，可以放置任意内容。",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "支持展开 (Expanded)、部分展开 (PartiallyExpanded)、收起 (Hidden) 三种状态。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = {
                    scope.launch { scaffoldState.bottomSheetState.expand() }
                }) {
                    Text("展开")
                }
                Button(onClick = {
                    scope.launch { scaffoldState.bottomSheetState.partialExpand() }
                }) {
                    Text("半展开")
                }
                Button(onClick = {
                    scope.launch { scaffoldState.bottomSheetState.hide() }
                }) {
                    Text("收起")
                }
            }
            Text(
                text = "当前状态: ${scaffoldState.bottomSheetState.currentValue}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ==================== 4. Chips ====================

/**
 * Chips - 各类标签/芯片
 * AssistChip: 辅助操作标签
 * FilterChip: 可选中的过滤标签
 * InputChip: 可删除的输入标签
 * SuggestionChip: 建议标签
 */
@Composable
fun ChipsSection() {
    SectionHeader(title = "4. Chips 标签组件")

    // FilterChip 选中状态
    var filterChecked by remember { mutableStateOf(false) }
    // InputChip 是否显示
    var showInputChip by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // AssistChip - 辅助操作，带图标
        AssistChip(
            onClick = { },
            label = { Text("Assist") },
            leadingIcon = {
                Icon(
                    Icons.Default.Build,
                    contentDescription = null,
                    modifier = Modifier.size(AssistChipDefaults.IconSize)
                )
            }
        )

        // FilterChip - 可选中的过滤标签
        FilterChip(
            selected = filterChecked,
            onClick = { filterChecked = !filterChecked },
            label = { Text("Filter") },
            leadingIcon = {
                if (filterChecked) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            }
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // InputChip - 可删除的输入标签
        if (showInputChip) {
            InputChip(
                selected = false,
                onClick = { showInputChip = false },
                label = { Text("Input") },
                avatar = {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = null,
                        modifier = Modifier.size(InputChipDefaults.AvatarSize)
                    )
                },
                trailingIcon = {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "删除",
                        modifier = Modifier.size(InputChipDefaults.IconSize)
                    )
                }
            )
        } else {
            Button(onClick = { showInputChip = true }) {
                Text("恢复 InputChip")
            }
        }

        // SuggestionChip - 建议标签
        SuggestionChip(
            onClick = { },
            label = { Text("Suggest") },
            icon = {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(AssistChipDefaults.IconSize)
                )
            }
        )
    }
}

// ==================== 5. Slider ====================

/**
 * Slider - 滑动选择器
 * Slider: 单值滑动
 * RangeSlider: 范围滑动（最小值和最大值）
 */
@Composable
fun SliderSection() {
    SectionHeader(title = "5. Slider 滑动选择器")

    // 单值 Slider
    var sliderValue by remember { mutableFloatStateOf(0.5f) }
    Text(
        text = "Slider 当前值: ${String.format("%.2f", sliderValue)}",
        style = MaterialTheme.typography.bodyMedium
    )
    Slider(
        value = sliderValue,
        onValueChange = { sliderValue = it },
        modifier = Modifier.fillMaxWidth(),
        steps = 4  // 将范围分为 5 段（0.0, 0.2, 0.4, 0.6, 0.8, 1.0）
    )

    Spacer(modifier = Modifier.height(8.dp))

    // RangeSlider - 范围选择
    var rangeStart by remember { mutableFloatStateOf(0.2f) }
    var rangeEnd by remember { mutableFloatStateOf(0.8f) }
    Text(
        text = "RangeSlider 范围: ${String.format("%.2f", rangeStart)} ~ ${String.format("%.2f", rangeEnd)}",
        style = MaterialTheme.typography.bodyMedium
    )
    RangeSlider(
        value = rangeStart..rangeEnd,
        onValueChange = { range ->
            rangeStart = range.start
            rangeEnd = range.endInclusive
        },
        modifier = Modifier.fillMaxWidth(),
        steps = 4
    )
}

// ==================== 6. Switch / RadioButton / Checkbox ====================

/**
 * Switch / RadioButton / Checkbox - 三种开关/选择组件
 * 每个组件独立管理自己的状态
 */
@Composable
fun ToggleSection() {
    SectionHeader(title = "6. Switch / RadioButton / Checkbox")

    var switchChecked by remember { mutableStateOf(true) }
    var radioSelected by remember { mutableStateOf("选项A") }
    var checkboxChecked by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Switch 开关
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Switch", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Switch(
                checked = switchChecked,
                onCheckedChange = { switchChecked = it }
            )
            Text(
                text = if (switchChecked) "开启" else "关闭",
                style = MaterialTheme.typography.bodySmall
            )
        }

        // RadioButton 单选按钮
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("RadioButton", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = radioSelected == "选项A",
                        onClick = { radioSelected = "选项A" }
                    )
                    Text("A", style = MaterialTheme.typography.bodySmall)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = radioSelected == "选项B",
                        onClick = { radioSelected = "选项B" }
                    )
                    Text("B", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Checkbox 复选框
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Checkbox", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Checkbox(
                checked = checkboxChecked,
                onCheckedChange = { checkboxChecked = it }
            )
            Text(
                text = if (checkboxChecked) "已选中" else "未选中",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

// ==================== 7. ProgressIndicator ====================

/**
 * ProgressIndicator - 进度指示器
 * LinearProgressIndicator: 线性进度条（确定/不确定）
 * CircularProgressIndicator: 圆形进度条（确定/不确定）
 */
@Composable
fun ProgressSection() {
    SectionHeader(title = "7. ProgressIndicator 进度指示器")

    // 确定性线性进度条
    var linearProgress by remember { mutableFloatStateOf(0.6f) }
    Text(
        text = "确定性 LinearProgressIndicator: ${String.format("%.0f%%", linearProgress * 100)}",
        style = MaterialTheme.typography.bodyMedium
    )
    LinearProgressIndicator(
        progress = { linearProgress },
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(4.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { linearProgress = (linearProgress + 0.1f).coerceAtMost(1f) }) {
            Text("+10%")
        }
        Button(onClick = { linearProgress = (linearProgress - 0.1f).coerceAtLeast(0f) }) {
            Text("-10%")
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // 不确定性线性进度条
    Text(
        text = "不确定性 LinearProgressIndicator (动画):",
        style = MaterialTheme.typography.bodyMedium
    )
    LinearProgressIndicator(
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(16.dp))

    // 确定性和不确定性圆形进度条
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(progress = { 0.75f })
            Spacer(modifier = Modifier.height(4.dp))
            Text("确定性 75%", style = MaterialTheme.typography.labelSmall)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(4.dp))
            Text("不确定性", style = MaterialTheme.typography.labelSmall)
        }
    }
}

// ==================== 8. Snackbar ====================

/**
 * Snackbar - 消息提示条
 * 通过 SnackbarHostState 控制显示
 * 支持不同时长和 Action 按钮
 */
@Composable
fun SnackbarSection() {
    SectionHeader(title = "8. Snackbar 消息提示")

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 嵌套一个局部的 Scaffold 来承载 SnackbarHost
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    modifier = Modifier.padding(8.dp)
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "短时 Snackbar (Short)",
                            duration = SnackbarDuration.Short
                        )
                    }
                }) {
                    Text("Short")
                }
                Button(onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "长时 Snackbar (Long)",
                            duration = SnackbarDuration.Long
                        )
                    }
                }) {
                    Text("Long")
                }
            }
            Button(onClick = {
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "带 Action 的 Snackbar",
                        actionLabel = "撤销",
                        duration = SnackbarDuration.Indefinite
                    )
                    when (result) {
                        SnackbarResult.ActionPerformed -> {
                            snackbarHostState.showSnackbar(
                                message = "已撤销操作",
                                duration = SnackbarDuration.Short
                            )
                        }
                        SnackbarResult.Dismissed -> { /* 自动消失 */ }
                    }
                }
            }) {
                Text("带 Action 的 Snackbar")
            }
        }
    }
}

// ==================== 9. DatePicker ====================

/**
 * DatePicker - 日期选择器
 * 使用 rememberDatePickerState 管理选择状态
 * 通过 DatePickerDialog 弹窗展示
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerSection() {
    SectionHeader(title = "9. DatePicker 日期选择器")

    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("未选择") }

    // 记录 DatePicker 状态，使用 Picker 模式
    val datePickerState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = { showDatePicker = true }) {
            Icon(
                Icons.Default.DateRange,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("选择日期")
        }
        Text(
            text = "已选: $selectedDate",
            style = MaterialTheme.typography.bodyMedium
        )
    }

    // DatePickerDialog 弹窗
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        // 将选中的 UTC 毫秒转换为可读日期字符串
                        datePickerState.selectedDateMillis?.let { millis ->
                            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                            selectedDate = sdf.format(java.util.Date(millis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// ==================== 10. Dialog ====================

/**
 * Dialog - 对话框
 * AlertDialog: 标准 Material 对话框
 * 自定义 Dialog: 带自定义内容的对话框
 */
@Composable
fun DialogSection() {
    SectionHeader(title = "10. Dialog 对话框")

    var showAlertDialog by remember { mutableStateOf(false) }
    var showCustomDialog by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = { showAlertDialog = true }) {
            Text("AlertDialog")
        }
        Button(onClick = { showCustomDialog = true }) {
            Text("自定义 Dialog")
        }
    }

    // AlertDialog - 标准确认对话框
    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { showAlertDialog = false },
            icon = { Icon(Icons.Default.Info, contentDescription = null) },
            title = { Text("确认操作") },
            text = {
                Text("这是一个标准的 AlertDialog，包含标题、内容文本和确认/取消按钮。你确定要继续吗？")
            },
            confirmButton = {
                TextButton(onClick = { showAlertDialog = false }) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAlertDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // 自定义 Dialog - 带自定义布局
    if (showCustomDialog) {
        Dialog(
            onDismissRequest = { showCustomDialog = false }
        ) {
            Surface(
                shape = MaterialTheme.shapes.large,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "自定义 Dialog",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "这是一个完全自定义布局的 Dialog，你可以在这里放置任意 Composable 内容。",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    // 在对话框内放置图标列表
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showCustomDialog = false }) {
                            Text("关闭")
                        }
                    }
                }
            }
        }
    }
}

// ==================== 11. FAB ====================

/**
 * FAB - 浮动操作按钮
 * FloatingActionButton: 基础 FAB
 * ExtendedFloatingActionButton: 带文字的扩展 FAB
 */
@Composable
fun FabSection() {
    SectionHeader(title = "11. FAB 浮动操作按钮")

    var fabClickCount by remember { mutableStateOf(0) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 基础 FAB
        FloatingActionButton(
            onClick = { fabClickCount++ }
        ) {
            Icon(Icons.Default.Add, contentDescription = "添加")
        }

        // 扩展 FAB
        ExtendedFloatingActionButton(
            onClick = { fabClickCount++ },
            icon = { Icon(Icons.Default.MailOutline, contentDescription = null) },
            text = { Text("发送邮件") }
        )
    }

    Text(
        text = "FAB 点击次数: $fabClickCount",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 8.dp)
    )
}

// ==================== 12. Tooltip ====================

/**
 * Tooltip - 工具提示
 * PlainTooltip: 简单文本提示
 * RichTooltip: 富文本提示，可包含标题和副标题
 * 使用 rememberTooltipState 控制显示
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TooltipSection() {
    SectionHeader(title = "12. Tooltip 工具提示")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // PlainTooltip - 简单文本提示
        TooltipBox(
            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
            tooltip = {
                PlainTooltip {
                    Text("这是一个简单提示")
                }
            },
            state = rememberTooltipState()
        ) {
            IconButton(onClick = { }) {
                Icon(Icons.Default.Info, contentDescription = "信息提示")
            }
        }

        // RichTooltip - 富文本提示
        val richTooltipState = rememberTooltipState(isPersistent = true)
        val richTooltipScope = rememberCoroutineScope()
        TooltipBox(
            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
            tooltip = {
                RichTooltip(
                    title = { Text("详细说明") },
                    action = {
                        TextButton(onClick = {
                            richTooltipScope.launch { richTooltipState.dismiss() }
                        }) {
                            Text("知道了")
                        }
                    }
                ) {
                    Text("这是富文本提示，可以包含标题、操作按钮和正文内容，适合展示更多细节信息。")
                }
            },
            state = richTooltipState
        ) {
            IconButton(onClick = { }) {
                Icon(Icons.Default.Settings, contentDescription = "设置提示")
            }
        }

        // 带图标的 PlainTooltip
        TooltipBox(
            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
            tooltip = {
                PlainTooltip {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text("点击收藏")
                    }
                }
            },
            state = rememberTooltipState()
        ) {
            IconButton(onClick = { }) {
                Icon(Icons.Default.Favorite, contentDescription = "收藏提示")
            }
        }
    }

    Text(
        text = "长按图标查看 Tooltip 提示",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .padding(top = 4.dp)
            .fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

// ==================== 13. ExposedDropdownMenu ====================

/**
 * ExposedDropdownMenu - 下拉菜单
 * 使用 ExposedDropdownMenuBox 包裹
 * Text Field + DropdownMenu 组合
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownSection() {
    SectionHeader(title = "13. ExposedDropdownMenu 下拉菜单")

    val options = listOf("Kotlin", "Java", "Python", "C++", "Rust", "Go", "Swift")
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,  // 只读，不可手动输入
            label = { Text("选择编程语言") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        selectedOption = option
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }

    Text(
        text = "当前选择: $selectedOption",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp)
    )
}
