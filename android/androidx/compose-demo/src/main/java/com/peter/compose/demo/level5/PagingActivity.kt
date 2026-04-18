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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ItemSnapshotList
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingSource.LoadParams
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// ============================================================
// 数据模型
// ============================================================

/**
 * 分页列表中的单个数据项
 *
 * @param id 唯一标识
 * @param title 标题
 * @param description 描述
 */
data class PageItem(
    val id: Int,
    val title: String,
    val description: String
)

// ============================================================
// ViewModel
// ============================================================

/**
 * PagingViewModel - 管理 Paging 3 分页数据的 ViewModel
 *
 * 核心组件说明：
 * - PagingSource: 定义数据源，负责单页数据的加载逻辑
 * - Pager: 根据 PagingConfig 创建 PagingData 流
 * - PagingData: 包含分页数据的数据容器
 * - PagingConfig: 配置分页参数（每页大小、占位符、预取距离等）
 * - cachedIn: 将 PagingData 缓存在 CoroutineScope 中，避免配置变更时重新加载
 */
class PagingViewModel : ViewModel() {

    // 当前加载状态，用于 UI 展示 LoadState 详情
    private val _loadStates = MutableStateFlow<LoadStates?>(null)
    val loadStates: StateFlow<LoadStates?> = _loadStates.asStateFlow()

    /**
     * 自定义 PagingSource，模拟网络分页数据加载
     *
     * PagingSource<Int, PageItem> 中：
     * - Int: 页码类型（使用页码作为 key）
     * - PageItem: 每条数据的类型
     */
    inner class ItemPagingSource : PagingSource<Int, PageItem>() {

        /**
         * 提供加载提示信息，用于确定刷新时的 key
         * 通过 state.anchorPosition 获取用户当前浏览位置，
         * 然后计算对应的页码
         */
        override fun getRefreshKey(state: PagingState<Int, PageItem>): Int? {
            // 获取用户当前浏览到最近的位置
            val anchorPosition = state.anchorPosition ?: return null
            // 将位置转换为对应的页码
            val closestPage = state.closestPageToPosition(anchorPosition)
            return closestPage?.prevKey?.plus(1) ?: closestPage?.nextKey?.minus(1)
        }

        /**
         * 执行数据加载
         *
         * params.key: 当前页码，null 表示首次加载
         * params.loadSize: 请求加载数据量
         *
         * 返回 LoadResult：
         * - LoadResult.Page: 成功，包含数据和前后页码
         * - LoadResult.Error: 失败，包含异常信息
         */
        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PageItem> {
            val page = params.key ?: 0

            // 模拟网络延迟（800ms）
            delay(800)

            // 根据请求大小生成对应数量的数据
            val pageSize = params.loadSize.coerceAtMost(20)
            val startId = page * 20
            val items = (startId until startId + pageSize).map { index ->
                PageItem(
                    id = index,
                    title = "Item $index",
                    description = "这是第 ${index + 1} 个数据项的详细描述"
                )
            }

            // 模拟网络错误：第 4 页（索引从 0 开始）故意返回错误
            // 这样可以演示 Paging 3 的错误处理和重试机制
            if (page == 4) {
                return LoadResult.Error(
                    Exception("模拟网络错误：加载第 $page 页时发生异常")
                )
            }

            // 成功返回数据页
            return LoadResult.Page(
                data = items,
                prevKey = if (page == 0) null else page - 1,  // 第一页没有上一页
                nextKey = if (items.size < 20) null else page + 1  // 数据不足一页说明没有下一页
            )
        }
    }

    /**
     * PagingData 流
     *
     * Pager 配置说明：
     * - pageSize = 20: 每页加载 20 条数据
     * - enablePlaceholders = true: 启用占位符（显示 null 项代替尚未加载的数据）
     * - initialLoadSize = 40: 首次加载 40 条数据（通常是 pageSize 的 2 倍）
     *
     * .flow.cachedIn(viewModelScope):
     * - 将流转换为 SharedFlow，多个收集者共享同一份数据
     * - 在 viewModelScope 中缓存，屏幕旋转等配置变更时不会丢失数据
     */
    val pagingItems: Flow<PagingData<PageItem>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = true,
            initialLoadSize = 40
        )
    ) {
        ItemPagingSource()
    }.flow.cachedIn(viewModelScope)
}

// ============================================================
// Activity
// ============================================================

/**
 * PagingActivity - Paging 3 分页加载示例
 *
 * 学习目标：
 * 1. PagingSource 自定义数据源
 * 2. PagingConfig 配置分页参数
 * 3. collectAsLazyPagingItems() 在 Compose 中使用分页数据
 * 4. LoadState 处理：加载中、加载失败、加载完成
 * 5. 占位符（Placeholders）的使用
 * 6. 错误重试机制
 */
class PagingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { innerPadding ->
                    PagingScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// ============================================================
// 主界面
// ============================================================

/**
 * PagingScreen - 分页示例主界面
 *
 * 界面布局：
 * 1. 顶部标题区
 * 2. Paging 概念说明卡片
 * 3. PagingConfig 参数展示卡片
 * 4. 分页列表示例（核心部分）
 * 5. LoadState 实时状态展示卡片
 */
@Composable
fun PagingScreen(modifier: Modifier = Modifier) {
    // 创建 ViewModel
    val viewModel: PagingViewModel = remember { PagingViewModel() }

    // 收集分页数据为 LazyPagingItems
    val pagingItems = viewModel.pagingItems.collectAsLazyPagingItems()

    // 收集 LoadState 实时状态
    val loadStates by viewModel.loadStates.collectAsState()

    // 使用 LazyColumn 包裹所有内容，使整体可滚动
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ---- 标题区 ----
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Paging 3 分页加载",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "使用 Paging 3 库实现高效的分页数据加载",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // ---- 1. Paging 概念说明 ----
        item {
            PagingConceptCard()
        }

        // ---- 2. PagingConfig 参数展示 ----
        item {
            PagingConfigCard()
        }

        // ---- 3. 分页列表示例 ----
        // 刷新状态加载指示器
        item {
            val refreshState = pagingItems.loadState.refresh
            if (refreshState is LoadState.Loading) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "正在加载数据...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 刷新错误展示
        item {
            val refreshState = pagingItems.loadState.refresh
            if (refreshState is LoadState.Error) {
                ErrorCard(
                    message = "刷新失败: ${refreshState.error.localizedMessage}",
                    onRetry = { pagingItems.retry() }
                )
            }
        }

        // 空数据状态
        item {
            val refreshState = pagingItems.loadState.refresh
            if (refreshState is LoadState.NotLoading && pagingItems.itemCount == 0) {
                EmptyStateCard()
            }
        }

        // 分页数据项
        items(count = pagingItems.itemCount) { index ->
            val item = pagingItems[index]
            if (item != null) {
                // 有数据，展示正常卡片
                PageItemCard(item = item)
            } else {
                // null 表示占位符（enablePlaceholders = true 时）
                PlaceholderCard(index = index)
            }
        }

        // 追加加载指示器（加载更多时底部显示）
        item {
            val appendState = pagingItems.loadState.append
            when (appendState) {
                is LoadState.Loading -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "正在加载更多...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                is LoadState.Error -> {
                    ErrorCard(
                        message = "加载更多失败: ${appendState.error.localizedMessage}",
                        onRetry = { pagingItems.retry() }
                    )
                }
                is LoadState.NotLoading -> {
                    // 追加完毕，判断是否全部加载完成
                    if (pagingItems.loadState.append.endOfPaginationReached) {
                        Text(
                            text = "-- 已经到底了 --",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // ---- 4. LoadState 详解 ----
        item {
            LoadStateDetailCard(pagingItems = pagingItems)
        }

        // 底部间距
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ============================================================
// UI 组件：概念说明卡片
// ============================================================

/**
 * Paging 概念说明卡片
 *
 * 介绍 Paging 3 的核心组件及其作用
 */
@Composable
fun PagingConceptCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Paging 概念说明",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            // PagingSource 说明
            ConceptItem(
                name = "PagingSource",
                description = "数据源，定义单页数据的加载逻辑。继承 PagingSource<Key, Value>，" +
                    "实现 load() 方法从网络或数据库获取数据。"
            )

            // Pager 说明
            ConceptItem(
                name = "Pager",
                description = "分页器，根据 PagingConfig 创建 PagingData 流。" +
                    "通过 .flow 获取 Flow<PagingData<T>>。"
            )

            // PagingData 说明
            ConceptItem(
                name = "PagingData",
                description = "分页数据容器，包含一页数据及加载状态。" +
                    "支持通过 map、filter 等操作符转换数据。"
            )

            // PagingConfig 说明
            ConceptItem(
                name = "PagingConfig",
                description = "分页配置，定义每页大小、占位符开关、预取距离、" +
                    "初始加载数量等参数。"
            )

            // cachedIn 说明
            ConceptItem(
                name = "cachedIn",
                description = "将 PagingData 缓存在指定 CoroutineScope 中，" +
                    "屏幕旋转等配置变更时数据不会丢失。"
            )
        }
    }
}

/**
 * 单个概念项
 */
@Composable
fun ConceptItem(name: String, description: String) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = "\u2022 ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

// ============================================================
// UI 组件：PagingConfig 参数展示卡片
// ============================================================

/**
 * PagingConfig 参数展示卡片
 *
 * 展示当前 PagingConfig 的配置参数及说明
 */
@Composable
fun PagingConfigCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "PagingConfig 参数展示",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // 参数列表
            ConfigParamItem(
                name = "pageSize",
                value = "20",
                description = "每页加载的数据量，决定每次从数据源获取多少条数据"
            )

            ConfigParamItem(
                name = "enablePlaceholders",
                value = "true",
                description = "启用占位符。当数据尚未加载时，列表中对应位置显示 null，" +
                    "可以在 UI 中渲染骨架屏或加载占位符"
            )

            ConfigParamItem(
                name = "initialLoadSize",
                value = "40",
                description = "首次加载的数据量，通常是 pageSize 的 2~3 倍，" +
                    "确保用户首次看到足够多的内容"
            )

            ConfigParamItem(
                name = "prefetchDistance",
                value = "pageSize (默认)",
                description = "预取距离，距离底部多远时开始预加载下一页。" +
                    "默认等于 pageSize"
            )

            // 代码示例
            Text(
                text = "示例代码:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = """Pager(
    config = PagingConfig(
        pageSize = 20,
        enablePlaceholders = true,
        initialLoadSize = 40
    )
) { ItemPagingSource() }
.flow.cachedIn(viewModelScope)""",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace
                ),
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
 * 单个配置参数项
 */
@Composable
fun ConfigParamItem(name: String, value: String, description: String) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "\u2022 ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$name = ",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Monospace
                )
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ============================================================
// UI 组件：数据项卡片
// ============================================================

/**
 * 正常数据项卡片
 *
 * 展示每条分页数据的 id、标题和描述
 */
@Composable
fun PageItemCard(item: PageItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧圆形编号
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${item.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 右侧内容
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * 占位符卡片
 *
 * 当 enablePlaceholders = true 且数据尚未加载时，
 * pagingItems[index] 返回 null，此时显示占位符
 */
@Composable
fun PlaceholderCard(index: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 骨架屏圆形
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 骨架屏文本条
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // 标题骨架
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                )
                // 描述骨架
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                )
            }
        }
    }
}

// ============================================================
// UI 组件：错误卡片
// ============================================================

/**
 * 错误状态卡片
 *
 * 显示错误信息并提供重试按钮
 */
@Composable
fun ErrorCard(message: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "加载出错",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text(text = "重试 (Retry)")
            }
        }
    }
}

// ============================================================
// UI 组件：空状态卡片
// ============================================================

/**
 * 空状态卡片
 *
 * 数据加载完成但列表为空时显示
 */
@Composable
fun EmptyStateCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "暂无数据",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Text(
                text = "没有可显示的分页数据项",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

// ============================================================
// UI 组件：LoadState 详解卡片
// ============================================================

/**
 * LoadState 详情卡片
 *
 * 实时展示当前各加载状态（refresh/append/prepend）的值，
 * 帮助理解 Paging 3 的加载状态机制
 */
@Composable
fun LoadStateDetailCard(pagingItems: LazyPagingItems<PageItem>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "LoadState 详解",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Paging 3 通过 LoadState 表示数据加载的当前状态，" +
                    "包括 refresh（刷新）、append（追加）、prepend（前向加载）三种。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Refresh 状态
            LoadStateRow(
                label = "Refresh",
                state = pagingItems.loadState.refresh,
                description = "初始加载或刷新数据时的状态"
            )

            // Append 状态
            LoadStateRow(
                label = "Append",
                state = pagingItems.loadState.append,
                description = "向后追加加载更多数据时的状态"
            )

            // Prepend 状态
            LoadStateRow(
                label = "Prepend",
                state = pagingItems.loadState.prepend,
                description = "向前加载历史数据时的状态（本例未使用）"
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 数据统计信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "当前已加载: ${pagingItems.itemCount} 项",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // LoadState 类型说明
            Text(
                text = "LoadState 类型:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = """
                    |LoadState.NotLoading - 加载完成（无错误）
                    |LoadState.Loading - 正在加载中
                    |LoadState.Error - 加载失败，可调用 retry() 重试
                """.trimMargin(),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 18.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp)
            )
        }
    }
}

/**
 * 单个 LoadState 行
 *
 * 以颜色标签展示当前加载状态
 */
@Composable
fun LoadStateRow(
    label: String,
    state: LoadState,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 标签
        Box(
            modifier = Modifier
                .width(70.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // 状态指示器
        val (stateText, stateColor) = when (state) {
            is LoadState.Loading -> "Loading" to MaterialTheme.colorScheme.tertiary
            is LoadState.Error -> "Error" to MaterialTheme.colorScheme.error
            is LoadState.NotLoading -> "NotLoading" to MaterialTheme.colorScheme.primary
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(stateColor.copy(alpha = 0.15f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = stateText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = stateColor
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 状态说明
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.weight(1f)
        )
    }
}

// ============================================================
// Preview
// ============================================================

@Preview(showBackground = true)
@Composable
fun PagingScreenPreview() {
    MaterialTheme {
        // 仅预览概念说明卡片和配置卡片（不需要实际分页数据）
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PagingConceptCard()
            PagingConfigCard()
        }
    }
}
