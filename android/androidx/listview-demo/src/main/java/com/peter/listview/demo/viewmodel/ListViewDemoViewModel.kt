package com.peter.listview.demo.viewmodel

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peter.listview.demo.adapter.IconItem
import com.peter.listview.demo.adapter.IconType
import com.peter.listview.demo.helper.DatabaseHelper
import com.peter.listview.demo.model.GroupedItem
import com.peter.listview.demo.model.MultiTypeItem
import com.peter.listview.demo.model.SimpleItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ListView Demo 的共享 ViewModel
 *
 * 管理所有 Tab 的列表数据，提供 LiveData 供 Fragment 观察
 */
class ListViewDemoViewModel : ViewModel() {

    // ========== ArrayAdapter Tab 数据 ==========

    private val _simpleStringItems = MutableLiveData<List<String>>()
    val simpleStringItems: LiveData<List<String>> = _simpleStringItems

    private val _mapItems = MutableLiveData<List<Map<String, Any>>>()
    val mapItems: LiveData<List<Map<String, Any>>> = _mapItems

    // ========== BaseAdapter Tab 数据 ==========

    private val _iconItems = MutableLiveData<List<IconItem>>()
    val iconItems: LiveData<List<IconItem>> = _iconItems

    private val _multiTypeItems = MutableLiveData<List<MultiTypeItem>>()
    val multiTypeItems: LiveData<List<MultiTypeItem>> = _multiTypeItems

    // ========== CursorAdapter Tab 数据 ==========

    private val _userCursor = MutableLiveData<Cursor?>()
    val userCursor: LiveData<Cursor?> = _userCursor

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery

    // ========== Advanced Tab 数据 ==========

    private val _groupedItems = MutableLiveData<List<GroupedItem<SimpleItem>>>()
    val groupedItems: LiveData<List<GroupedItem<SimpleItem>>> = _groupedItems

    private val _isLoadingMore = MutableLiveData<Boolean>()
    val isLoadingMore: LiveData<Boolean> = _isLoadingMore

    private val _hasMoreData = MutableLiveData<Boolean>()
    val hasMoreData: LiveData<Boolean> = _hasMoreData

    // 加载状态
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // 数据库引用（由 Fragment 设置）
    private var databaseHelper: DatabaseHelper? = null

    // 当前加载页数（用于分页加载）
    private var currentPage = 0
    private val pageSize = 20

    init {
        loadInitialData()
    }

    /**
     * 设置数据库辅助类
     */
    fun setDatabaseHelper(helper: DatabaseHelper) {
        this.databaseHelper = helper
    }

    /**
     * 加载初始数据
     */
    fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true

            // ArrayAdapter 数据
            _simpleStringItems.value = generateSimpleStrings()
            _mapItems.value = generateMapItems()

            // BaseAdapter 数据
            _iconItems.value = generateIconItems()
            _multiTypeItems.value = generateMultiTypeItems()

            // Advanced 数据
            _groupedItems.value = generateGroupedItems()
            currentPage = 1
            _hasMoreData.value = true

            _isLoading.value = false
        }
    }

    /**
     * 加载 Cursor 数据
     */
    fun loadUserCursor() {
        viewModelScope.launch(Dispatchers.IO) {
            val cursor = databaseHelper?.queryAllUsers()
            withContext(Dispatchers.Main) {
                _userCursor.value = cursor
            }
        }
    }

    /**
     * 搜索用户
     */
    fun searchUsers(query: String) {
        _searchQuery.value = query
        viewModelScope.launch(Dispatchers.IO) {
            val cursor = if (query.isBlank()) {
                databaseHelper?.queryAllUsers()
            } else {
                databaseHelper?.searchUsers(query)
            }
            withContext(Dispatchers.Main) {
                _userCursor.value = cursor
            }
        }
    }

    /**
     * 添加用户
     */
    fun addUser(name: String, phone: String) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseHelper?.addUser(name, phone)
            // 刷新 Cursor
            val query = _searchQuery.value ?: ""
            searchUsers(query)
        }
    }

    /**
     * 删除用户
     */
    fun deleteUser(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseHelper?.deleteUser(id)
            // 刷新 Cursor
            val query = _searchQuery.value ?: ""
            searchUsers(query)
        }
    }

    /**
     * 加载更多数据
     */
    fun loadMore() {
        if (_isLoadingMore.value == true || _hasMoreData.value == false) {
            return
        }

        viewModelScope.launch {
            _isLoadingMore.value = true

            // 模拟网络延迟
            kotlinx.coroutines.delay(1000)

            // 生成新数据
            val newItems = generateGroupedItemsPage(currentPage + 1)
            val currentList = _groupedItems.value?.toMutableList() ?: mutableListOf()

            if (newItems.isEmpty()) {
                _hasMoreData.value = false
            } else {
                currentList.addAll(newItems)
                _groupedItems.value = currentList
                currentPage++
            }

            _isLoadingMore.value = false
        }
    }

    /**
     * 刷新数据
     */
    fun refresh() {
        currentPage = 0
        _hasMoreData.value = true
        loadInitialData()
    }

    /**
     * 加载分组列表数据
     */
    fun loadGroupedItems() {
        viewModelScope.launch {
            _isLoading.value = true
            _groupedItems.value = generateGroupedItems()
            currentPage = 1
            _hasMoreData.value = true
            _isLoading.value = false
        }
    }

    /**
     * 加载更多分组列表数据
     */
    fun loadMoreGroupedItems(page: Int) {
        if (_isLoadingMore.value == true) {
            return
        }

        viewModelScope.launch {
            _isLoadingMore.value = true

            // 模拟网络延迟
            kotlinx.coroutines.delay(500)

            val newItems = generateGroupedItemsPage(page)
            val currentList = _groupedItems.value?.toMutableList() ?: mutableListOf()

            if (newItems.isEmpty()) {
                _hasMoreData.value = false
            } else {
                currentList.addAll(newItems)
                _groupedItems.value = currentList
                currentPage = page
            }

            _isLoadingMore.value = false
        }
    }

    // ========== 数据生成方法 ==========

    private fun generateSimpleStrings(): List<String> {
        return (1..50).map { "Item $it" }
    }

    private fun generateMapItems(): List<Map<String, Any>> {
        val iconTypes = IconType.values()
        return (1..20).map { index ->
            mapOf(
                "id" to index.toLong(),
                "icon" to iconTypes[index % iconTypes.size],
                "title" to "标题 $index",
                "description" to "这是第 $index 项的描述"
            )
        }
    }

    private fun generateIconItems(): List<IconItem> {
        val iconTypes = IconType.values()
        return (1..30).map { index ->
            IconItem(
                id = index.toLong(),
                iconType = iconTypes[index % iconTypes.size],
                title = "项目 $index",
                description = "这是项目 $index 的详细描述信息"
            )
        }
    }

    private fun generateMultiTypeItems(): List<MultiTypeItem> {
        val items = mutableListOf<MultiTypeItem>()

        // 添加标题和内容
        for (i in 1..5) {
            items.add(MultiTypeItem.Header("分组 $i"))
            for (j in 1..3) {
                items.add(MultiTypeItem.Content(
                    id = (i * 10 + j).toLong(),
                    title = "项目 $i-$j",
                    description = "这是分组 $i 中的第 $j 项"
                ))
            }
        }

        return items
    }

    private fun generateGroupedItems(): List<GroupedItem<SimpleItem>> {
        return generateGroupedItemsPage(1)
    }

    private fun generateGroupedItemsPage(page: Int): List<GroupedItem<SimpleItem>> {
        val items = mutableListOf<GroupedItem<SimpleItem>>()
        val letters = ('A'..'Z').toList()
        val startIndex = (page - 1) * pageSize

        for (i in 0 until pageSize) {
            val letter = letters[(startIndex + i) % letters.size]
            items.add(
                GroupedItem(
                    section = letter.toString(),
                    data = SimpleItem(
                        id = (startIndex + i).toLong(),
                        title = "${letter}项目 ${startIndex + i + 1}",
                        subtitle = "子标题 ${startIndex + i + 1}"
                    )
                )
            )
        }

        // 模拟数据结束（超过 100 项）
        if (startIndex >= 100) {
            return emptyList()
        }

        return items
    }

    override fun onCleared() {
        super.onCleared()
        // 关闭 Cursor
        _userCursor.value?.close()
    }
}
