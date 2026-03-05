package com.peter.lifecycle.demo.viewmodel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.peter.lifecycle.demo.databinding.ActivityViewModelFactoryBinding

/**
 * ViewModelProvider.Factory 示例
 * 
 * 知识点：
 * 1. 自定义 Factory 创建带参数的 ViewModel
 * 2. AbstractSavedStateViewModelFactory - 支持 SavedStateHandle
 * 3. CreationExtras - 通过 extras 传递参数
 * 
 * 使用场景：
 * - ViewModel 需要 Repository 等依赖注入
 * - ViewModel 需要运行时参数
 * - 配合 Dagger/Hilt/Koin 使用
 */
class ViewModelFactoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewModelFactoryBinding
    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewModelFactoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 方式1：使用自定义 Factory
        val factory = UserViewModelFactory(
            userId = "user_123",
            repository = UserRepository()
        )
        viewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]

        // 方式2：使用 CreationExtras（推荐）
        // viewModel = ViewModelProvider(this, UserViewModelFactory2)[UserViewModel::class.java]

        setupViews()
        observeData()
    }

    private fun setupViews() {
        binding.btnLoadUser.setOnClickListener {
            viewModel.loadUser()
        }
        
        binding.btnUpdateName.setOnClickListener {
            val newName = binding.etName.text.toString()
            viewModel.updateName(newName)
        }
    }

    private fun observeData() {
        viewModel.user.observe(this) { user ->
            binding.tvUserName.text = "用户名: ${user.name}"
            binding.tvUserId.text = "用户ID: ${user.id}"
        }
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

/**
 * 用户 Repository
 */
class UserRepository {
    fun getUser(userId: String): User {
        // 模拟网络请求
        return User(userId, "张三", 25)
    }
    
    fun updateUser(user: User): Boolean {
        // 模拟更新用户
        return true
    }
}

/**
 * 用户 ViewModel
 */
class UserViewModel(
    private val userId: String,
    private val repository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _user = savedStateHandle.getLiveData<User>("user")
    val user = _user

    init {
        // 从 SavedStateHandle 恢复数据
        savedStateHandle.get<User>("user")?.let {
            _user.value = it
        }
    }

    fun loadUser() {
        val userData = repository.getUser(userId)
        _user.value = userData
        savedStateHandle["user"] = userData
    }

    fun updateName(newName: String) {
        _user.value?.let { currentUser ->
            val updatedUser = currentUser.copy(name = newName)
            if (repository.updateUser(updatedUser)) {
                _user.value = updatedUser
                savedStateHandle["user"] = updatedUser
            }
        }
    }
}

/**
 * 方式1：传统 Factory
 */
class UserViewModelFactory(
    private val userId: String,
    private val repository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(userId, repository, SavedStateHandle()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * 方式2：AbstractSavedStateViewModelFactory（推荐）
 * 
 * 优点：
 * - 自动处理 SavedStateHandle
 * - 支持 Activity/Fragment 的状态保存
 */
class UserViewModelFactory2(
    private val userId: String,
    private val repository: UserRepository
) : AbstractSavedStateViewModelFactory() {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(userId, repository, handle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * 方式3：使用 CreationExtras（最新推荐）
 */
class UserViewModelFactory3(
    private val userId: String,
    private val repository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        // 从 CreationExtras 获取 SavedStateHandle
        val savedStateHandle = extras.createSavedStateHandle()
        
        // 获取 Application
        val application = extras[APPLICATION_KEY]
        
        @Suppress("UNCHECKED_CAST")
        return UserViewModel(userId, repository, savedStateHandle) as T
    }
}
