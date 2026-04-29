package com.peter.compose.demo.level3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

/**
 * SnapshotFlowActivity - SnapshotFlow 与状态桥接
 *
 * 学习目标：
 * 1. snapshotFlow: 将 Compose State 转为 Kotlin Flow
 * 2. 监听滚动位置变化
 * 3. Flow.collectAsState 桥接
 * 4. debounce 搜索输入
 */
class SnapshotFlowActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    SnapshotFlowScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(FlowPreview::class)
@Composable
fun SnapshotFlowScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "SnapshotFlow",
            style = MaterialTheme.typography.headlineMedium
        )

        SnapshotFlowBasicsSection()
        ScrollToTopSection()
        DebounceSearchSection()
    }
}

@Composable
fun SnapshotFlowBasicsSection() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "snapshotFlow 基础",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "将 Compose State 转为 Flow，可在协程中监听变化",
                style = MaterialTheme.typography.bodyMedium
            )

            var counter by remember { mutableStateOf(0) }
            var changeLog by remember { mutableStateOf("等待变化...") }

            LaunchedEffect(Unit) {
                snapshotFlow { counter }
                    .distinctUntilChanged()
                    .collect { value ->
                        changeLog = "counter 变为 $value (时间: ${System.currentTimeMillis() % 100000}ms)"
                    }
            }

            Text("counter: $counter", style = MaterialTheme.typography.bodyLarge)
            Text(changeLog, style = MaterialTheme.typography.bodySmall)

            androidx.compose.material3.Button(onClick = { counter++ }) {
                Text("counter++")
            }

            Text(
                text = """LaunchedEffect(Unit) {
    snapshotFlow { counter }
        .distinctUntilChanged()
        .collect { value -> println("值变为: ${'$'}value") }
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

@Composable
fun ScrollToTopSection() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "滚动监听 — 回到顶部按钮",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "滚动超过 5 项后显示回到顶部按钮",
                style = MaterialTheme.typography.bodyMedium
            )

            val listState = rememberLazyListState()
            val scope = rememberCoroutineScope()

            val showScrollToTop by remember {
                derivedStateOf { listState.firstVisibleItemIndex > 5 }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(50) { index ->
                        Text(
                            text = "Item #$index",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        )
                    }
                }

                if (showScrollToTop) {
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                listState.animateScrollToItem(0)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "回到顶部")
                    }
                }
            }

            Text(
                text = """val showScrollToTop by remember {
    derivedStateOf { listState.firstVisibleItemIndex > 5 }
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

@OptIn(FlowPreview::class)
@Composable
fun DebounceSearchSection() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "防抖搜索 (debounce)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "输入后等待 300ms 才触发搜索，避免每次按键都请求",
                style = MaterialTheme.typography.bodyMedium
            )

            var searchText by remember { mutableStateOf("") }
            var searchResult by remember { mutableStateOf("输入关键词开始搜索") }

            LaunchedEffect(Unit) {
                snapshotFlow { searchText }
                    .debounce(300)
                    .distinctUntilChanged()
                    .filter { it.isNotBlank() }
                    .collect { query ->
                        searchResult = "搜索: \"$query\" → 找到 ${(query.length * 7) % 50 + 1} 条结果"
                    }
            }

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("搜索关键词") },
                modifier = Modifier.fillMaxWidth()
            )

            Text(searchResult, style = MaterialTheme.typography.bodyMedium)

            Text(
                text = """LaunchedEffect(Unit) {
    snapshotFlow { searchText }
        .debounce(300)
        .distinctUntilChanged()
        .filter { it.isNotBlank() }
        .collect { query -> performSearch(query) }
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
fun SnapshotFlowScreenPreview() {
    MaterialTheme {
        SnapshotFlowScreen()
    }
}
