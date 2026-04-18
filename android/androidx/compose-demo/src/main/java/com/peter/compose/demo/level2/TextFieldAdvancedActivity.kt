package com.peter.compose.demo.level2

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/**
 * TextFieldAdvancedActivity - TextField 进阶用法演示
 *
 * 学习目标：
 * 1. 掌握 TextField 与 OutlinedTextField 的区别和配置
 * 2. 理解 BasicTextField 的自定义能力
 * 3. 学会输入法交互、文本选择、输入验证
 */
class TextFieldAdvancedActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    TextFieldAdvancedScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TextFieldAdvancedScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. TextField vs OutlinedTextField 对比
        TextFieldComparisonExample()

        // 2. BasicTextField 自定义装饰
        BasicTextFieldDecoratorExample()

        // 3. KeyboardOptions / KeyboardActions
        KeyboardOptionsExample()

        // 4. 文本选择
        TextSelectionExample()

        // 5. ClickableText 带注释文本
        ClickableTextExample()

        // 6. 密码输入
        PasswordInputExample()

        // 7. 搜索框
        SearchBoxExample()

        // 8. 输入验证
        InputValidationExample()
    }
}

/**
 * 1. TextField vs OutlinedTextField 对比
 *
 * TextField: 带背景填充的输入框，适合表单场景
 * OutlinedTextField: 带边框的输入框，更现代的设计风格
 * 两者都支持 label、placeholder、leadingIcon、trailingIcon、colors 等配置
 */
@Composable
fun TextFieldComparisonExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "1. TextField vs OutlinedTextField",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "TextField 带背景填充，OutlinedTextField 带边框轮廓。两者都支持丰富的自定义配置。",
                style = MaterialTheme.typography.bodyMedium
            )

            // TextField 基础用法
            var textFieldValue by remember { mutableStateOf("") }
            Text(
                text = "TextField（填充背景）:",
                style = MaterialTheme.typography.labelMedium
            )
            TextField(
                value = textFieldValue,
                onValueChange = { textFieldValue = it },
                label = { Text("用户名") },
                placeholder = { Text("请输入用户名") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = "用户")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // OutlinedTextField 基础用法
            var outlinedValue by remember { mutableStateOf("") }
            Text(
                text = "OutlinedTextField（边框轮廓）:",
                style = MaterialTheme.typography.labelMedium
            )
            OutlinedTextField(
                value = outlinedValue,
                onValueChange = { outlinedValue = it },
                label = { Text("邮箱地址") },
                placeholder = { Text("example@mail.com") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = "邮箱")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 带自定义颜色的 OutlinedTextField
            var customColorValue by remember { mutableStateOf("") }
            Text(
                text = "自定义颜色 OutlinedTextField:",
                style = MaterialTheme.typography.labelMedium
            )
            OutlinedTextField(
                value = customColorValue,
                onValueChange = { customColorValue = it },
                label = { Text("自定义颜色") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Numbers,
                        contentDescription = "数字"
                    )
                },
                trailingIcon = {
                    if (customColorValue.isNotEmpty()) {
                        Text(
                            text = "${customColorValue.length}/10",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}

/**
 * 2. BasicTextField 自定义装饰
 *
 * BasicTextField 是最基础的文本输入组件，没有默认样式
 * 通过 TextFieldDefaults.decorator() 可以为其添加 Material Design 样式的装饰
 */
@Composable
fun BasicTextFieldDecoratorExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "2. BasicTextField 自定义装饰",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "BasicTextField 提供最基础的文本输入能力，通过 decorator 可自定义外观。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 使用 BasicTextField + decorator 创建自定义输入框
            var basicValue by remember { mutableStateOf("") }
            Text(
                text = "带装饰器的 BasicTextField:",
                style = MaterialTheme.typography.labelMedium
            )
            OutlinedTextField(
                value = basicValue,
                onValueChange = { basicValue = it },
                label = { Text("自定义样式输入框") },
                placeholder = { Text("请输入内容...") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            // 极简风格的输入框
            var minimalValue by remember { mutableStateOf("") }
            Text(
                text = "极简风格输入框:",
                style = MaterialTheme.typography.labelMedium
            )
            OutlinedTextField(
                value = minimalValue,
                onValueChange = { minimalValue = it },
                placeholder = {
                    Text(
                        "请输入...",
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            )

            // 多行输入框
            var multilineValue by remember { mutableStateOf("") }
            Text(
                text = "多行输入框:",
                style = MaterialTheme.typography.labelMedium
            )
            OutlinedTextField(
                value = multilineValue,
                onValueChange = { multilineValue = it },
                label = { Text("备注信息") },
                placeholder = { Text("请输入多行文本...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )
        }
    }
}

/**
 * 3. KeyboardOptions / KeyboardActions
 *
 * KeyboardOptions 控制键盘类型、自动大写、自动更正等
 * KeyboardActions 处理 IME 按钮的点击事件（搜索、下一步、完成等）
 * FocusRequester 和 FocusManager 用于编程式控制焦点
 */
@Composable
fun KeyboardOptionsExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "3. KeyboardOptions / KeyboardActions",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "通过 KeyboardOptions 配置键盘类型，KeyboardActions 处理 IME 按钮事件。",
                style = MaterialTheme.typography.bodyMedium
            )

            val focusManager = LocalFocusManager.current

            // 数字键盘
            var numberValue by remember { mutableStateOf("") }
            Text(
                text = "数字键盘 (KeyboardType.Number):",
                style = MaterialTheme.typography.labelMedium
            )
            OutlinedTextField(
                value = numberValue,
                onValueChange = { numberValue = it },
                label = { Text("数字输入") },
                placeholder = { Text("只能输入数字") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 邮箱键盘
            var emailValue by remember { mutableStateOf("") }
            Text(
                text = "邮箱键盘 (KeyboardType.Email):",
                style = MaterialTheme.typography.labelMedium
            )
            OutlinedTextField(
                value = emailValue,
                onValueChange = { emailValue = it },
                label = { Text("邮箱地址") },
                placeholder = { Text("输入邮箱") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = "邮箱")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 搜索操作 + 大写
            var searchValue by remember { mutableStateOf("") }
            val context = LocalContext.current
            Text(
                text = "搜索操作 (ImeAction.Search) + 自动大写:",
                style = MaterialTheme.typography.labelMedium
            )
            OutlinedTextField(
                value = searchValue,
                onValueChange = { searchValue = it },
                label = { Text("搜索") },
                placeholder = { Text("输入关键词后点击搜索按钮") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "搜索")
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        Toast
                            .makeText(
                                context,
                                "搜索: $searchValue",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 使用 FocusRequester 实现焦点控制
            val focusRequester1 = remember { FocusRequester() }
            val focusRequester2 = remember { FocusRequester() }
            var field1Value by remember { mutableStateOf("") }
            var field2Value by remember { mutableStateOf("") }

            Text(
                text = "焦点导航 (ImeAction.Next):",
                style = MaterialTheme.typography.labelMedium
            )
            OutlinedTextField(
                value = field1Value,
                onValueChange = { field1Value = it },
                label = { Text("字段 1 - 按 Next 跳转") },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusRequester2.requestFocus() }
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester1)
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = field2Value,
                onValueChange = { field2Value = it },
                label = { Text("字段 2 - 按 Done 完成") },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester2)
            )
        }
    }
}

/**
 * 4. 文本选择
 *
 * SelectionContainer 使其中的文本可以被选中/复制
 * DisableSelection 可以在 SelectionContainer 内排除部分文本不可选中
 */
@Composable
fun TextSelectionExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "4. 文本选择 (SelectionContainer)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "SelectionContainer 使文本可选中复制，DisableSelection 排除部分内容。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 可选择的文本
            Text(
                text = "可选择文本:",
                style = MaterialTheme.typography.labelMedium
            )
            SelectionContainer {
                Text(
                    text = "这段文字可以被长按选中并复制。尝试长按选择部分文字。" +
                        "SelectionContainer 会为其内部的所有 Text 组件启用文本选择功能。",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // 部分可选择
            Text(
                text = "部分可选择文本:",
                style = MaterialTheme.typography.labelMedium
            )
            SelectionContainer {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "这部分文本可以被选中复制。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    DisableSelection {
                        Text(
                            text = "这部分文本不可选中（DisableSelection 包裹）。",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    Text(
                        text = "这部分又可以被选中了。SelectionContainer 与 DisableSelection 可以灵活组合使用。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/**
 * 5. ClickableText 带注释文本
 *
 * ClickableText 可以响应文本点击事件
 * 配合 buildAnnotatedString 和 withStyle 可以创建富文本
 * 通过 pushStringAnnotation 可以为文本添加可点击的注释标记
 */
@Composable
fun ClickableTextExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "5. ClickableText 带注释文本",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "ClickableText 配合 AnnotatedString 实现富文本点击交互。",
                style = MaterialTheme.typography.bodyMedium
            )

            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            val snackbarHostState = remember { SnackbarHostState() }

            // 简单的 ClickableText
            Text(
                text = "基础 ClickableText:",
                style = MaterialTheme.typography.labelMedium
            )
            ClickableText(
                text = AnnotatedString("点击这段文字的任意位置，会弹出 Toast 提示点击位置。"),
                style = MaterialTheme.typography.bodyLarge,
                onClick = { offset ->
                    Toast
                        .makeText(
                            context,
                            "点击了位置: $offset",
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }
            )

            // 带样式的 AnnotatedString
            Text(
                text = "富文本样式:",
                style = MaterialTheme.typography.labelMedium
            )
            val styledText = buildAnnotatedString {
                append("这是普通文本，")
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                ) {
                    append("这是粗体文本，")
                }
                withStyle(
                    SpanStyle(
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    append("这是斜体文本，")
                }
                withStyle(
                    SpanStyle(
                        textDecoration = TextDecoration.Underline,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    append("这是下划线文本。")
                }
            }
            Text(text = styledText)

            // 带可点击链接的文本
            Text(
                text = "带可点击链接的文本:",
                style = MaterialTheme.typography.labelMedium
            )
            val annotatedLinkText = buildAnnotatedString {
                append("阅读我们的")
                pushStringAnnotation(
                    tag = "URL",
                    annotation = "https://example.com/terms"
                )
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("服务条款")
                }
                pop()
                append(" 和 ")
                pushStringAnnotation(
                    tag = "URL",
                    annotation = "https://example.com/privacy"
                )
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("隐私政策")
                }
                pop()
                append("。点击链接查看详情。")
            }
            ClickableText(
                text = annotatedLinkText,
                style = MaterialTheme.typography.bodyLarge,
                onClick = { offset ->
                    annotatedLinkText
                        .getStringAnnotations(
                            tag = "URL",
                            start = offset,
                            end = offset
                        )
                        .firstOrNull()
                        ?.let { annotation ->
                            Toast
                                .makeText(
                                    context,
                                    "链接: ${annotation.item}",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }
                }
            )

            // Snackbar 通知的 Host（用于展示内联反馈）
            SnackbarHost(hostState = snackbarHostState)
        }
    }
}

/**
 * 6. 密码输入
 *
 * 使用 PasswordVisualTransformation 隐藏密码内容
 * 通过切换 VisualTransformation 实现密码显示/隐藏
 * 可以自定义遮罩字符
 */
@Composable
fun PasswordInputExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "6. 密码输入",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "使用 PasswordVisualTransformation 隐藏密码，支持切换显示和自定义遮罩。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 标准密码输入框（带显示/隐藏切换）
            var password by remember { mutableStateOf("") }
            var passwordVisible by remember { mutableStateOf(false) }

            Text(
                text = "密码输入（带显示/隐藏切换）:",
                style = MaterialTheme.typography.labelMedium
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("密码") },
                placeholder = { Text("请输入密码") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = "锁")
                },
                trailingIcon = {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible }
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                            contentDescription = if (passwordVisible) "隐藏密码" else "显示密码"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 自定义遮罩字符
            var customPassword by remember { mutableStateOf("") }
            var customMaskVisible by remember { mutableStateOf(false) }

            Text(
                text = "自定义遮罩字符（使用 # 号）:",
                style = MaterialTheme.typography.labelMedium
            )
            OutlinedTextField(
                value = customPassword,
                onValueChange = { customPassword = it },
                label = { Text("自定义遮罩密码") },
                placeholder = { Text("请输入密码") },
                trailingIcon = {
                    IconButton(
                        onClick = { customMaskVisible = !customMaskVisible }
                    ) {
                        Icon(
                            imageVector = if (customMaskVisible) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                            contentDescription = if (customMaskVisible) "隐藏密码" else "显示密码"
                        )
                    }
                },
                visualTransformation = if (customMaskVisible) {
                    VisualTransformation.None
                } else {
                    // 自定义遮罩字符为 #
                    PasswordVisualTransformation(mask = '#')
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 密码强度提示
            Text(
                text = "密码强度提示:",
                style = MaterialTheme.typography.labelMedium
            )
            if (password.isNotEmpty()) {
                val strength = when {
                    password.length < 4 -> "弱"
                    password.length < 8 -> "中"
                    else -> "强"
                }
                val strengthColor = when (strength) {
                    "弱" -> MaterialTheme.colorScheme.error
                    "中" -> Color(0xFFFFA000)
                    else -> MaterialTheme.colorScheme.primary
                }
                Text(
                    text = "密码强度: $strength (${password.length} 个字符)",
                    style = MaterialTheme.typography.bodySmall,
                    color = strengthColor
                )
            } else {
                Text(
                    text = "请先输入上面的密码",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

/**
 * 7. 搜索框
 *
 * 搜索框模式：
 * - 搜索图标作为 leadingIcon
 * - 清除按钮作为 trailingIcon（仅在有内容时显示）
 * - 输入文本实时过滤列表
 */
@Composable
fun SearchBoxExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "7. 搜索框",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "搜索框模式：搜索图标 + 实时过滤 + 清除按钮。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 示例数据
            val allItems = remember {
                listOf(
                    "Kotlin", "Java", "Python", "JavaScript", "TypeScript",
                    "C++", "C#", "Go", "Rust", "Swift",
                    "Ruby", "PHP", "Scala", "Dart", "Lua",
                    "R", "Perl", "Haskell", "Elixir", "Clojure"
                )
            }

            var searchQuery by remember { mutableStateOf("") }

            // 搜索输入框
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("搜索编程语言") },
                placeholder = { Text("输入关键词搜索...") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "搜索"
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { searchQuery = "" }
                        ) {
                            Icon(
                                Icons.Rounded.Clear,
                                contentDescription = "清除"
                            )
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 过滤结果
            val filteredItems = if (searchQuery.isBlank()) {
                allItems
            } else {
                allItems.filter {
                    it.contains(searchQuery, ignoreCase = true)
                }
            }

            Text(
                text = "搜索结果 (${filteredItems.size}/${allItems.size}):",
                style = MaterialTheme.typography.labelMedium
            )

            if (filteredItems.isEmpty()) {
                Text(
                    text = "没有匹配的结果",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            } else {
                // 使用固定高度的 Column 代替 LazyColumn（因为外层已有滚动）
                Column(
                    modifier = Modifier.height(200.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    filteredItems.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        if (item != filteredItems.last()) {
                            Divider(
                                modifier = Modifier.padding(start = 40.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 8. 输入验证
 *
 * 实时输入验证：
 * - isError 控制错误样式
 * - supportingText 显示提示或错误信息
 * - 字符计数限制
 */
@Composable
fun InputValidationExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "8. 输入验证",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "实时输入验证：邮箱格式、密码强度、字符计数等。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 邮箱验证
            var emailValue by remember { mutableStateOf("") }
            val isEmailValid = emailValue.contains("@") && emailValue.contains(".") && emailValue.isNotBlank()
            val showEmailError = emailValue.isNotEmpty() && !isEmailValid

            Text(
                text = "邮箱验证:",
                style = MaterialTheme.typography.labelMedium
            )
            OutlinedTextField(
                value = emailValue,
                onValueChange = { emailValue = it },
                label = { Text("邮箱地址") },
                placeholder = { Text("example@mail.com") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = "邮箱")
                },
                isError = showEmailError,
                supportingText = {
                    if (showEmailError) {
                        Text("请输入有效的邮箱地址（需包含 @ 和 .）")
                    } else if (emailValue.isNotEmpty()) {
                        Text("邮箱格式正确", color = MaterialTheme.colorScheme.primary)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 密码强度验证
            var validationPassword by remember { mutableStateOf("") }
            val hasMinLength = validationPassword.length >= 8
            val hasUppercase = validationPassword.any { it.isUpperCase() }
            val hasDigit = validationPassword.any { it.isDigit() }
            val isPasswordStrong = hasMinLength && hasUppercase && hasDigit

            Text(
                text = "密码强度验证:",
                style = MaterialTheme.typography.labelMedium
            )
            OutlinedTextField(
                value = validationPassword,
                onValueChange = { validationPassword = it },
                label = { Text("密码") },
                placeholder = { Text("至少8位，包含大写字母和数字") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = "密码")
                },
                isError = validationPassword.isNotEmpty() && !isPasswordStrong,
                supportingText = {
                    if (validationPassword.isNotEmpty()) {
                        Column {
                            Text(
                                text = if (hasMinLength) "✓ 至少 8 个字符" else "✗ 至少 8 个字符",
                                color = if (hasMinLength) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = if (hasUppercase) "✓ 包含大写字母" else "✗ 包含大写字母",
                                color = if (hasUppercase) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = if (hasDigit) "✓ 包含数字" else "✗ 包含数字",
                                color = if (hasDigit) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 带字符计数的输入框
            var bioValue by remember { mutableStateOf("") }
            val maxBioLength = 20

            Text(
                text = "字符计数限制:",
                style = MaterialTheme.typography.labelMedium
            )
            OutlinedTextField(
                value = bioValue,
                onValueChange = {
                    if (it.length <= maxBioLength) {
                        bioValue = it
                    }
                },
                label = { Text("个人简介") },
                placeholder = { Text("最多 $maxBioLength 个字符") },
                isError = bioValue.length >= maxBioLength,
                supportingText = {
                    Text(
                        text = "${bioValue.length}/$maxBioLength",
                        color = if (bioValue.length >= maxBioLength) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.outline
                        }
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 用户名验证（不能为空，不能包含空格）
            var usernameValue by remember { mutableStateOf("") }
            val hasSpace = usernameValue.contains(" ")
            val isUsernameValid = usernameValue.isNotBlank() && !hasSpace && usernameValue.length >= 3

            Text(
                text = "用户名验证:",
                style = MaterialTheme.typography.labelMedium
            )
            OutlinedTextField(
                value = usernameValue,
                onValueChange = { usernameValue = it },
                label = { Text("用户名") },
                placeholder = { Text("至少3个字符，不能包含空格") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = "用户")
                },
                isError = usernameValue.isNotEmpty() && !isUsernameValid,
                supportingText = {
                    when {
                        usernameValue.isEmpty() -> {
                            Text("请输入用户名")
                        }
                        usernameValue.length < 3 -> {
                            Text("用户名至少需要 3 个字符")
                        }
                        hasSpace -> {
                            Text("用户名不能包含空格")
                        }
                        else -> {
                            Text(
                                "用户名可用",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TextFieldAdvancedPreview() {
    MaterialTheme {
        TextFieldAdvancedScreen()
    }
}
