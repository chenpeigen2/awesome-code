package com.peter.compose.demo.level2

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * StateBasicsActivity - 状态基础
 *
 * 学习目标：
 * 1. mutableStateOf 创建可观察状态
 * 2. remember 和 rememberSaveable 的区别
 * 3. 状态读写触发重组
 */
class StateBasicsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    StateBasicsScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun StateBasicsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. mutableStateOf 基础
        MutableStateExample()

        // 2. remember 示例
        RememberExample()

        // 3. rememberSaveable 示例
        RememberSaveableExample()

        // 4. 状态列表
        StateListExample()
    }
}

/**
 * mutableStateOf 基础示例
 *
 * mutableStateOf 创建一个可观察的状态对象
 * 当状态值改变时，所有读取该状态的 Composable 会自动重组
 */
@Composable
fun MutableStateExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "1. mutableStateOf 基础",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "mutableStateOf 创建可观察状态，状态改变时自动触发重组。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 计数器示例
            var count by remember { mutableIntStateOf(0) }

            Text(
                text = "计数器示例:",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { count-- }
                ) {
                    Text("-")
                }

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$count",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Button(
                    onClick = { count++ }
                ) {
                    Text("+")
                }
            }

            // 文本输入示例
            var text by remember { mutableStateOf("") }

            Text(
                text = "文本输入示例:",
                style = MaterialTheme.typography.labelMedium
            )

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("输入文字") },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "输入内容: \"$text\"",
                style = MaterialTheme.typography.bodySmall
            )

            // 开关示例
            var checked by remember { mutableStateOf(false) }

            Text(
                text = "开关示例:",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Switch(
                    checked = checked,
                    onCheckedChange = { checked = it }
                )
                Text(text = if (checked) "开启状态" else "关闭状态")
            }
        }
    }
}

/**
 * remember 示例
 *
 * remember 用于在重组过程中保持状态
 * 状态只在当前 Composable 的生命周期内保持
 * 配置变更（如屏幕旋转）会导致状态丢失
 */
@Composable
fun RememberExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "2. remember",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "remember 在重组过程中保持状态，但配置变更（如屏幕旋转）会丢失状态。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 计算值缓存
            var input by remember { mutableStateOf(10) }

            Text(
                text = "remember 用于缓存计算结果:",
                style = MaterialTheme.typography.labelMedium
            )

            // 使用 remember 缓存计算结果
            val squared = remember(input) { input * input }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { input-- }) { Text("-") }
                Text(
                    text = "$input² = $squared",
                    style = MaterialTheme.typography.titleMedium
                )
                Button(onClick = { input++ }) { Text("+") }
            }

            // 计算重组次数
            var recompositionCount by remember { mutableIntStateOf(0) }
            recompositionCount++

            Text(
                text = "重组次数: $recompositionCount (每次状态改变都会重组)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * rememberSaveable 示例
 *
 * rememberSaveable 在配置变更后也能恢复状态
 * 使用 Bundle 机制保存状态
 */
@Composable
fun RememberSaveableExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "3. rememberSaveable",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "rememberSaveable 在配置变更（如屏幕旋转）后仍能恢复状态。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 使用 rememberSaveable 保存状态
            var savedCount by rememberSaveable { mutableIntStateOf(0) }

            Text(
                text = "旋转屏幕后此计数不会丢失:",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { savedCount-- }) { Text("-") }

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$savedCount",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Button(onClick = { savedCount++ }) { Text("+") }
            }

            // 滑块示例
            var sliderValue by rememberSaveable { mutableFloatStateOf(0.5f) }

            Text(
                text = "滑块值也会被保存:",
                style = MaterialTheme.typography.labelMedium
            )

            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "当前值: ${String.format("%.2f", sliderValue)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/**
 * 状态列表示例
 *
 * mutableStateListOf 创建可观察的列表状态
 * 列表元素的增删改都会触发重组
 */
@Composable
fun StateListExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "4. 状态列表 (mutableStateListOf)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "mutableStateListOf 创建可观察的列表，增删改都会触发重组。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 创建可观察列表
            val items = remember { mutableStateListOf<String>() }
            var newItemText by remember { mutableStateOf("") }

            Text(
                text = "待办事项列表:",
                style = MaterialTheme.typography.labelMedium
            )

            // 输入框
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = newItemText,
                    onValueChange = { newItemText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("添加新事项") }
                )
                Button(
                    onClick = {
                        if (newItemText.isNotBlank()) {
                            items.add(newItemText)
                            newItemText = ""
                        }
                    }
                ) {
                    Text("添加")
                }
            }

            // 列表显示
            if (items.isEmpty()) {
                Text(
                    text = "暂无事项，请添加",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${index + 1}. $item",
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = { items.removeAt(index) }
                            ) {
                                Text("删除")
                            }
                        }
                    }
                }
            }

            // 统计信息
            Text(
                text = "共 ${items.size} 个事项",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StateBasicsPreview() {
    MaterialTheme {
        StateBasicsScreen()
    }
}
