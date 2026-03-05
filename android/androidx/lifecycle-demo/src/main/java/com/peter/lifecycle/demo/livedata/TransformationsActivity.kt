package com.peter.lifecycle.demo.livedata

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.peter.lifecycle.demo.databinding.ActivityTransformationsBinding

/**
 * Transformations 示例
 * 
 * 知识点：
 * 1. map - 一对一转换
 * 2. switchMap - 一对多动态切换
 * 3. 延迟计算 - 只有在观察时才计算
 * 
 * 使用场景：
 * - 数据格式转换（日期格式化、字符串处理）
 * - 根据条件切换数据源
 * - 过滤和映射数据
 */
class TransformationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransformationsBinding
    private val viewModel: TransformationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransformationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeData()
    }

    private fun setupViews() {
        binding.btnFormat.setOnClickListener {
            val input = binding.etInput.text.toString()
            viewModel.setInputText(input)
        }
        
        binding.btnSearch.setOnClickListener {
            val query = binding.etSearch.text.toString()
            viewModel.search(query)
        }
        
        binding.btnSwitchUser.setOnClickListener {
            viewModel.switchUser()
        }
    }

    private fun observeData() {
        // 观察 map 转换后的数据
        viewModel.formattedText.observe(this) { formatted ->
            binding.tvFormatted.text = "格式化后: $formatted"
        }
        
        // 观察 switchMap 切换的数据源
        viewModel.searchResult.observe(this) { result ->
            binding.tvSearchResult.text = "搜索结果: $result"
        }
        
        viewModel.currentUser.observe(this) { user ->
            binding.tvUserInfo.text = "当前用户: ${user.name}"
        }
    }
}

/**
 * Transformations ViewModel
 */
class TransformationsViewModel : ViewModel() {

    // 原始数据
    private val _inputText = MutableLiveData<String>()
    
    // 使用 map 进行一对一转换
    // 只有当 _inputText 有观察者时，才会执行转换
    val formattedText: LiveData<String> = _inputText.map { input ->
        // 将输入转换为大写并添加前缀
        "【$input】".uppercase()
    }

    // 搜索关键字
    private val _searchQuery = MutableLiveData<String>()
    
    // 使用 switchMap 动态切换数据源
    // 类似于 flatMap，每次 _searchQuery 变化都会切换到新的 LiveData
    val searchResult: LiveData<String> = _searchQuery.switchMap { query ->
        // 根据查询条件返回不同的 LiveData
        if (query.startsWith("user:")) {
            searchUser(query.removePrefix("user:"))
        } else {
            searchContent(query)
        }
    }

    // 用户ID
    private val _userId = MutableLiveData<String>("user_1")
    
    // 根据用户ID动态切换用户数据源
    val currentUser: LiveData<User> = _userId.switchMap { id ->
        getUserById(id)
    }

    // 模拟搜索方法
    private fun searchUser(query: String): LiveData<String> {
        val result = MutableLiveData<String>()
        result.value = "搜索用户: $query (找到 1 个结果)"
        return result
    }

    private fun searchContent(query: String): LiveData<String> {
        val result = MutableLiveData<String>()
        result.value = "搜索内容: $query (找到 10 个结果)"
        return result
    }

    // 模拟获取用户
    private fun getUserById(id: String): LiveData<User> {
        val result = MutableLiveData<User>()
        result.value = User(id, "用户_$id", 20 + id.hashCode() % 30)
        return result
    }

    fun setInputText(text: String) {
        _inputText.value = text
    }

    fun search(query: String) {
        _searchQuery.value = query
    }

    fun switchUser() {
        val currentId = _userId.value?.removePrefix("user_")?.toIntOrNull() ?: 1
        _userId.value = "user_${(currentId % 5) + 1}"
    }
}

/**
 * 用户数据类
 */
data class User(
    val id: String,
    val name: String,
    val age: Int
)
