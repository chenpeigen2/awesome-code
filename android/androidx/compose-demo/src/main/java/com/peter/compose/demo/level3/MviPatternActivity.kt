package com.peter.compose.demo.level3

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * MviPatternActivity - MVI 模式
 *
 * 学习目标：
 * 1. 单向数据流概念
 * 2. State + Action + Effect 模式
 * 3. 实践：计数器 MVI 示例
 */

// ========== MVI 架构组件 ==========

/**
 * UI 状态 - 表示屏幕的完整状态
 */
data class CounterState(
    val count: Int = 0,
    val minValue: Int = 0,
    val maxValue: Int = 100,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * 意图/动作 - 用户触发的操作
 */
sealed interface CounterAction {
    data object Increment : CounterAction
    data object Decrement : CounterAction
    data object Reset : CounterAction
    data class SetRange(val min: Int, val max: Int) : CounterAction
}

/**
 * 副作用 - 一次性事件（如导航、Toast）
 */
sealed interface CounterEffect {
    data class ShowToast(val message: String) : CounterEffect
    data object NavigateBack : CounterEffect
}

/**
 * MVI ViewModel
 *
 * 职责：
 * 1. 接收 Action
 * 2. 处理业务逻辑
 * 3. 更新 State
 * 4. 发送 Effect
 */
class MviCounterViewModel : ViewModel() {

    // 状态流
    private val _state = MutableStateFlow(CounterState())
    val state: StateFlow<CounterState> = _state.asStateFlow()

    // 副作用流（一次性事件）
    private val _effects = MutableSharedFlow<CounterEffect>()
    val effects: SharedFlow<CounterEffect> = _effects.asSharedFlow()

    /**
     * 处理用户动作
     */
    fun onAction(action: CounterAction) {
        when (action) {
            is CounterAction.Increment -> handleIncrement()
            is CounterAction.Decrement -> handleDecrement()
            is CounterAction.Reset -> handleReset()
            is CounterAction.SetRange -> handleSetRange(action.min, action.max)
        }
    }

    private fun handleIncrement() {
        val currentState = _state.value
        if (currentState.count >= currentState.maxValue) {
            // 达到最大值，发送提示
            // 实际应用中可以使用 _effects.emit(CounterEffect.ShowToast("已达最大值"))
            return
        }
        _state.update { it.copy(count = it.count + 1) }
    }

    private fun handleDecrement() {
        val currentState = _state.value
        if (currentState.count <= currentState.minValue) {
            // 达到最小值
            return
        }
        _state.update { it.copy(count = it.count - 1) }
    }

    private fun handleReset() {
        _state.update { CounterState() }
    }

    private fun handleSetRange(min: Int, max: Int) {
        _state.update { it.copy(minValue = min, maxValue = max) }
    }
}

// ========== Activity ==========

class MviPatternActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    MviPatternScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MviPatternScreen(
    modifier: Modifier = Modifier,
    viewModel: MviCounterViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. MVI 架构图
        MviArchitectureExplanation()

        // 2. 计数器示例
        CounterMviExample(
            state = state,
            onAction = { viewModel.onAction(it) }
        )

        // 3. 代码结构说明
        MviCodeStructure()
    }
}

@Composable
fun MviArchitectureExplanation() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "MVI 架构",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "MVI (Model-View-Intent) 是一种单向数据流架构模式。",
                style = MaterialTheme.typography.bodyMedium
            )

            // 数据流图示
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Action
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Action (意图)",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Text("↓", style = MaterialTheme.typography.titleLarge)

                // ViewModel
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "ViewModel (处理)",
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }

                Text("↓", style = MaterialTheme.typography.titleLarge)

                // State
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.tertiary)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "State (状态)",
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }

                Text("↓", style = MaterialTheme.typography.titleLarge)

                // UI
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("UI (渲染)")
                }
            }

            // 特点
            Text(
                text = "特点:",
                style = MaterialTheme.typography.labelMedium
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "• 单向数据流：状态变化可预测",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• 状态不可变：每次更新创建新状态",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• 状态集中：便于调试和测试",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• 副作用分离：一次性事件独立处理",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun CounterMviExample(
    state: CounterState,
    onAction: (CounterAction) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "MVI 计数器示例",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // 范围显示
            Text(
                text = "范围: ${state.minValue} ~ ${state.maxValue}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 计数器显示
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            when {
                                state.count <= state.minValue -> MaterialTheme.colorScheme.errorContainer
                                state.count >= state.maxValue -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.secondaryContainer
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${state.count}",
                        style = MaterialTheme.typography.displayMedium,
                        color = when {
                            state.count <= state.minValue -> MaterialTheme.colorScheme.onErrorContainer
                            state.count >= state.maxValue -> MaterialTheme.colorScheme.onPrimaryContainer
                            else -> MaterialTheme.colorScheme.onSecondaryContainer
                        }
                    )
                }
            }

            // 操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                // 减少
                IconButton(
                    onClick = { onAction(CounterAction.Decrement) },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "减少",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // 增加
                IconButton(
                    onClick = { onAction(CounterAction.Increment) },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "增加",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            // 重置按钮
            Button(
                onClick = { onAction(CounterAction.Reset) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("重置")
            }

            // 状态信息
            if (state.count <= state.minValue) {
                Text(
                    text = "已达最小值",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            } else if (state.count >= state.maxValue) {
                Text(
                    text = "已达最大值",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun MviCodeStructure() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "MVI 代码结构",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // State
            Text(
                text = "State (状态):",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = """data class CounterState(
    val count: Int = 0,
    val isLoading: Boolean = false
)""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )

            // Action
            Text(
                text = "Action (意图):",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = """sealed interface CounterAction {
    data object Increment : CounterAction
    data object Decrement : CounterAction
}""",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )

            // Effect
            Text(
                text = "Effect (副作用):",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = """sealed interface CounterEffect {
    data class ShowToast(val msg: String)
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

@Preview(showBackground = true)
@Composable
fun MviPatternPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CounterMviExample(
                state = CounterState(count = 50),
                onAction = {}
            )
        }
    }
}
