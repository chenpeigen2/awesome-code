package com.peter.compose.demo.level5

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * PerformanceActivity - 性能优化
 *
 * 学习目标：
 * 1. 重组优化技巧
 * 2. key() 使用
 * 3. derivedStateOf
 * 4. @Stable / @Immutable
 */
class PerformanceActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    PerformanceScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PerformanceScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. 重组优化基础
        RecompositionBasics()

        // 2. derivedStateOf 示例
        DerivedStateExample()

        // 3. @Stable / @Immutable 示例
        StabilityExample()

        // 4. key() 使用示例
        KeyExample()
    }
}

/**
 * 重组优化基础
 */
@Composable
fun RecompositionBasics() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "1. 重组优化基础",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Compose 通过重组更新 UI，优化重组可以提升性能。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 重组次数统计
            var recompositionCount by remember { mutableIntStateOf(0) }
            recompositionCount++

            Text(
                text = "当前组件重组次数: $recompositionCount",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 状态提升减少重组
            Text(
                text = "优化技巧:",
                style = MaterialTheme.typography.labelMedium
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "• 状态提升到最小作用域",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• 使用 remember 缓存计算结果",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• 避免不必要的 Lambda 创建",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• 使用 key() 优化列表",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• 使用 derivedStateOf 避免重复计算",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/**
 * derivedStateOf 示例
 *
 * derivedStateOf 用于创建派生状态
 * 只有当派生结果变化时才会触发重组
 */
@Composable
fun DerivedStateExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "2. derivedStateOf",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "derivedStateOf 创建派生状态，避免不必要的重组。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 不使用 derivedStateOf
            var count by remember { mutableIntStateOf(0) }
            // 每次都重新计算
            val isEven = count % 2 == 0

            Text(
                text = "不使用 derivedStateOf:",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("计数: $count")
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isEven) Color(0xFF4CAF50) else Color(0xFFF44336))
                )
            }

            // 使用 derivedStateOf
            val isEvenDerived by remember {
                derivedStateOf { count % 2 == 0 }
            }

            Text(
                text = "使用 derivedStateOf:",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = "只有奇偶性变化时才触发使用该状态的重组",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 列表过滤示例
            val items = remember {
                mutableStateListOf<String>().apply {
                    repeat(100) { add("Item $it") }
                }
            }

            var filterText by remember { mutableStateOf("") }

            // 使用 derivedStateOf 过滤
            val filteredItems by remember {
                derivedStateOf {
                    if (filterText.isEmpty()) {
                        items
                    } else {
                        items.filter { it.contains(filterText, ignoreCase = true) }
                    }
                }
            }

            Text(
                text = "列表过滤示例 (filteredItems 只在结果变化时更新):",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = "过滤结果: ${filteredItems.size} 个项目",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = """// 使用 derivedStateOf
val filteredItems by remember {
    derivedStateOf {
        items.filter { it.contains(query) }
    }
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

/**
 * @Stable / @Immutable 示例
 */
@Composable
fun StabilityExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "3. @Stable / @Immutable",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "使用 @Stable 或 @Immutable 注解帮助 Compose 优化重组。",
                style = MaterialTheme.typography.bodyMedium
            )

            // @Immutable 示例
            Text(
                text = "@Immutable - 不可变数据类:",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = """@Immutable
data class User(
    val id: Int,
    val name: String,
    val email: String
)

// Compose 知道 User 是不可变的
// 可以跳过对相等 User 的重组""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )

            // @Stable 示例
            Text(
                text = "@Stable - 可变但稳定的数据类:",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = """@Stable
data class UIState(
    val isLoading: Boolean = false,
    val data: List<String> = emptyList(),
    val error: String? = null
)

// Compose 会使用 equals 判断是否相等
// 相等则跳过重组""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )

            Text(
                text = "使用原则:",
                style = MaterialTheme.typography.labelMedium
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "• @Immutable: 完全不可变的数据类",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• @Stable: 可变但实现稳定 equals 的类",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• List, Set, Map 默认不稳定，考虑使用 @Immutable 包装",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/**
 * key() 使用示例
 */
@Composable
fun KeyExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "4. key() 优化列表",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "在 LazyColumn 中使用 key() 可以优化列表更新。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 示例数据
            val items = remember {
                (1..20).map { ListItem(id = it, name = "项目 $it") }
            }

            Text(
                text = "不使用 key (问题):",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = "列表顺序变化时，所有项目都会重组",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "使用 key (优化):",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = """LazyColumn {
    items(
        items = list,
        key = { item -> item.id }  // 稳定的 key
    ) { item ->
        ItemRow(item)
    }
}""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )

            Text(
                text = "key() 的好处:",
                style = MaterialTheme.typography.labelMedium
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "• 正确追踪项目移动",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• 避免不必要的重组",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• 支持项目动画效果",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• 保持滚动位置和焦点状态",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

// 示例数据类
@Immutable
data class ListItem(
    val id: Int,
    val name: String
)

@Preview(showBackground = true)
@Composable
fun PerformancePreview() {
    MaterialTheme {
        PerformanceScreen()
    }
}
