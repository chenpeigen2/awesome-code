package com.peter.compose.demo.level2

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * StateHoistingActivity - 状态提升模式
 *
 * 学习目标：
 * 1. 状态提升模式的概念
 * 2. 无状态组件 vs 有状态组件
 * 3. 封装可复用组件的最佳实践
 */
class StateHoistingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    StateHoistingScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun StateHoistingScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. 无状态 vs 有状态组件对比
        StatelessVsStatefulExample()

        // 2. 状态提升示例
        StateHoistingExample()

        // 3. 实际应用：表单验证
        FormValidationExample()

        // 4. 实际应用：搜索框
        SearchFieldExample()
    }
}

/**
 * 无状态 vs 有状态组件对比
 */
@Composable
fun StatelessVsStatefulExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "1. 无状态 vs 有状态组件",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // 有状态组件 (不推荐复用)
            Text(
                text = "有状态组件 (内部管理状态，难以复用):",
                style = MaterialTheme.typography.labelMedium
            )

            StatefulCounter()

            Spacer(modifier = Modifier.height(8.dp))

            // 无状态组件 (推荐)
            Text(
                text = "无状态组件 (状态由外部管理，易于复用):",
                style = MaterialTheme.typography.labelMedium
            )

            // 状态提升到父组件
            var count by remember { mutableStateOf(0) }

            StatelessCounter(
                count = count,
                onIncrement = { count++ },
                onDecrement = { count-- }
            )
        }
    }
}

/**
 * 有状态计数器组件
 * 状态在组件内部管理，外部无法控制
 */
@Composable
fun StatefulCounter() {
    var count by remember { mutableStateOf(0) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "计数: $count",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "-",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )
        Text(
            text = "+",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

/**
 * 无状态计数器组件
 * 状态由外部管理，通过参数传入和回调传出
 */
@Composable
fun StatelessCounter(
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "计数: $count",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "-",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )
        Text(
            text = "+",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

/**
 * 状态提升示例
 */
@Composable
fun StateHoistingExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "2. 状态提升模式",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "状态提升原则：状态应该提升到使用该状态的所有组件的最低共同父组件。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 父组件管理共享状态
            var sharedText by remember { mutableStateOf("") }

            Text(
                text = "两个组件共享同一个状态:",
                style = MaterialTheme.typography.labelMedium
            )

            // 输入组件
            HoistedTextField(
                value = sharedText,
                onValueChange = { sharedText = it },
                label = "输入内容"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 显示组件
            TextDisplay(
                text = sharedText,
                label = "实时显示"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 字符计数组件
            CharCounter(text = sharedText)
        }
    }
}

/**
 * 提升状态的文本输入框
 */
@Composable
fun HoistedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * 文本显示组件
 */
@Composable
fun TextDisplay(
    text: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text.ifEmpty { "暂无内容" },
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * 字符计数组件
 */
@Composable
fun CharCounter(text: String, modifier: Modifier = Modifier) {
    Text(
        text = "字符数: ${text.length}",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

/**
 * 表单验证示例
 */
@Composable
fun FormValidationExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "3. 表单验证示例",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "状态提升使得表单验证逻辑可以集中在父组件中。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 表单状态
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            // 验证逻辑
            val isEmailValid = email.contains("@")
            val isPasswordValid = password.length >= 6
            val isFormValid = isEmailValid && isPasswordValid

            // 邮箱输入
            ValidatedTextField(
                value = email,
                onValueChange = { email = it },
                label = "邮箱",
                isError = email.isNotEmpty() && !isEmailValid,
                errorMessage = "请输入有效的邮箱地址"
            )

            // 密码输入
            ValidatedTextField(
                value = password,
                onValueChange = { password = it },
                label = "密码",
                isError = password.isNotEmpty() && !isPasswordValid,
                errorMessage = "密码至少6个字符",
                isPassword = true
            )

            // 提交按钮状态
            Text(
                text = if (isFormValid) "表单验证通过，可以提交" else "请完成表单验证",
                style = MaterialTheme.typography.bodySmall,
                color = if (isFormValid) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * 带验证的文本输入框
 */
@Composable
fun ValidatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    errorMessage: String,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            isError = isError,
            visualTransformation = if (isPassword) PasswordVisualTransformation()
                                    else VisualTransformation.None,
            modifier = Modifier.fillMaxWidth()
        )
        if (isError) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * 搜索框示例
 */
@Composable
fun SearchFieldExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "4. 搜索框示例",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "搜索框组件通过状态提升实现与搜索结果的联动。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 搜索状态
            var searchQuery by remember { mutableStateOf("") }

            // 搜索框组件
            SearchField(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "搜索..."
            )

            // 模拟搜索结果
            val allItems = listOf(
                "Kotlin", "Java", "Python", "JavaScript",
                "Swift", "Rust", "Go", "C++"
            )

            val filteredItems = if (searchQuery.isEmpty()) {
                allItems
            } else {
                allItems.filter { it.contains(searchQuery, ignoreCase = true) }
            }

            // 显示搜索结果
            Text(
                text = "搜索结果:",
                style = MaterialTheme.typography.labelMedium
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                filteredItems.forEach { item ->
                    Text(
                        text = "• $item",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (filteredItems.isEmpty()) {
                    Text(
                        text = "未找到匹配结果",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * 搜索框组件
 */
@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Text("🔍", style = MaterialTheme.typography.bodyLarge)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                Text(
                    text = "✕",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp)
                )
            }
        },
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun StateHoistingPreview() {
    MaterialTheme {
        StateHoistingScreen()
    }
}
