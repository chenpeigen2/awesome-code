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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow

/**
 * FlowLiveDataActivity - Flow/LiveData 集成
 *
 * 学习目标：
 * 1. collectAsState(): 收集 Flow
 * 2. collectAsStateWithLifecycle(): 生命周期感知的 Flow 收集
 * 3. observeAsState(): LiveData 观察
 */
class FlowLiveDataActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    FlowLiveDataScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

/**
 * 数据流 ViewModel
 */
class FlowLiveDataViewModel : ViewModel() {

    // ========== StateFlow ==========
    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> = _counter.asStateFlow()

    fun incrementCounter() {
        _counter.value++
    }

    // ========== SharedFlow (一次性事件) ==========
    private val _events = MutableSharedFlow<String>()
    val events: Flow<String> = _events.asSharedFlow()

    suspend fun sendEvent(message: String) {
        _events.emit(message)
    }

    // ========== 冷流 Flow ==========
    val timerFlow: Flow<Int> = flow {
        var seconds = 0
        while (true) {
            emit(seconds)
            delay(1000)
            seconds++
        }
    }

    // ========== LiveData ==========
    private val _liveDataCounter = MutableLiveData(0)
    val liveDataCounter: LiveData<Int> = _liveDataCounter

    fun incrementLiveDataCounter() {
        _liveDataCounter.value = (_liveDataCounter.value ?: 0) + 1
    }
}

@Composable
fun FlowLiveDataScreen(
    modifier: Modifier = Modifier,
    viewModel: FlowLiveDataViewModel = viewModel()
) {
    // 收集 StateFlow
    val counter by viewModel.counter.collectAsStateWithLifecycle()

    // 观察 LiveData
    val liveDataCounter by viewModel.liveDataCounter.observeAsState(0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. StateFlow 示例
        StateFlowExample(
            counter = counter,
            onIncrement = { viewModel.incrementCounter() }
        )

        // 2. Flow 示例
        FlowExample()

        // 3. LiveData 示例
        LiveDataExample(
            counter = liveDataCounter,
            onIncrement = { viewModel.incrementLiveDataCounter() }
        )

        // 4. 数据流对比说明
        DataFlowComparison()
    }
}

@Composable
fun StateFlowExample(
    counter: Int,
    onIncrement: () -> Unit
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
                text = "1. StateFlow 集成",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "StateFlow 是热流，始终有值，适合表示 UI 状态。",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "使用 collectAsStateWithLifecycle() 收集:",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$counter",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Button(onClick = onIncrement) {
                    Text("增加")
                }
            }

            Text(
                text = "代码示例:",
                style = MaterialTheme.typography.labelSmall
            )

            Text(
                text = """val counter by viewModel.counter
    .collectAsStateWithLifecycle()""",
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
fun FlowExample() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "2. 冷流 Flow",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "冷流（Cold Flow）只有被收集时才会发射数据，每个收集者独立执行。",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "注意：实际使用需要在 ViewModel 中管理 Flow 的收集",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 显示 Flow 示例代码
            Text(
                text = """// ViewModel 中
val timerFlow: Flow<Int> = flow {
    var seconds = 0
    while (true) {
        emit(seconds)
        delay(1000)
        seconds++
    }
}

// Composable 中
val seconds by viewModel.timerFlow
    .collectAsStateWithLifecycle(initialValue = 0)""",
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
fun LiveDataExample(
    counter: Int,
    onIncrement: () -> Unit
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
                text = "3. LiveData 集成",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "LiveData 是 Android 的生命周期感知数据持有者，与 Compose 兼容。",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "使用 observeAsState() 观察:",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$counter",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Button(onClick = onIncrement) {
                    Text("增加")
                }
            }

            Text(
                text = "代码示例:",
                style = MaterialTheme.typography.labelSmall
            )

            Text(
                text = """val counter by viewModel.liveDataCounter
    .observeAsState(initial = 0)""",
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
fun DataFlowComparison() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "4. 数据流对比",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // 对比表格
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 表头
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "类型",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "特点",
                        modifier = Modifier.weight(2f),
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                // StateFlow
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = "StateFlow",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "热流，有初始值，适合 UI 状态",
                        modifier = Modifier.weight(2f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // SharedFlow
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = "SharedFlow",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "热流，无初始值，适合一次性事件",
                        modifier = Modifier.weight(2f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Flow
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Flow",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "冷流，按需创建，适合数据流",
                        modifier = Modifier.weight(2f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // LiveData
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = "LiveData",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "生命周期感知，适合与 View 层交互",
                        modifier = Modifier.weight(2f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // 推荐使用
            Text(
                text = "推荐：新项目优先使用 StateFlow + collectAsStateWithLifecycle()",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FlowLiveDataPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StateFlowExample(counter = 5, onIncrement = {})
            LiveDataExample(counter = 3, onIncrement = {})
        }
    }
}
