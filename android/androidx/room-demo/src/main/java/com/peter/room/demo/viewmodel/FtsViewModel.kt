package com.peter.room.demo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.peter.room.demo.db.entity.Article
import com.peter.room.demo.repository.ArticleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FtsViewModel(private val repository: ArticleRepository) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<Article>>(emptyList())
    val searchResults: StateFlow<List<Article>> = _searchResults

    private val _operationState = MutableStateFlow<String?>(null)
    val operationState: StateFlow<String?> = _operationState

    private val _searchMethod = MutableStateFlow("MATCH")
    val searchMethod: StateFlow<String> = _searchMethod

    fun seedData() {
        viewModelScope.launch {
            val articles = listOf(
                Article("Kotlin 协程入门", "Kotlin 协程是轻量级线程，用于异步编程。协程可以简化并发代码的编写。"),
                Article("Jetpack Compose 指南", "Compose 是 Android 的现代 UI 工具包，采用声明式方法构建界面。"),
                Article("Room 数据库最佳实践", "Room 是 Android 的持久化库，提供 SQLite 的抽象层。支持 Flow 响应式查询。"),
                Article("Android 性能优化", "性能优化包括内存优化、启动优化、布局优化、网络优化等多个方面。"),
                Article("Kotlin Flow 详解", "Flow 是冷流，只有在收集时才执行。StateFlow 和 SharedFlow 是热流。"),
                Article("Material Design 3", "Material Design 3 是 Google 最新的设计系统，支持动态颜色和 Material You。"),
                Article("Android 架构模式", "MVVM 和 MVI 是 Android 推荐的架构模式，配合 ViewModel 和 StateFlow 使用。"),
                Article("Gradle 构建优化", "Gradle 构建优化包括配置缓存、并行构建、增量编译等技术。")
            )
            repository.insertAll(articles)
            _operationState.value = "已插入 ${articles.size} 篇示例文章"
        }
    }

    fun searchFts(keyword: String) {
        viewModelScope.launch {
            _searchMethod.value = "MATCH"
            val results = repository.search(keyword)
            _searchResults.value = results
            _operationState.value = "MATCH 搜索 \"$keyword\"：找到 ${results.size} 条"
        }
    }

    fun searchLike(keyword: String) {
        viewModelScope.launch {
            _searchMethod.value = "LIKE"
            val results = repository.searchLike(keyword)
            _searchResults.value = results
            _operationState.value = "LIKE 搜索 \"$keyword\"：找到 ${results.size} 条"
        }
    }

    fun clearResults() {
        _searchResults.value = emptyList()
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteAll()
            _searchResults.value = emptyList()
            _operationState.value = "已清空所有文章"
        }
    }

    fun clearOperationState() { _operationState.value = null }
}

class FtsViewModelFactory(private val repository: ArticleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FtsViewModel(repository) as T
    }
}
