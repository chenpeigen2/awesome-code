package com.peter.lifecycle.demo.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * UI 状态封装
 */
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

/**
 * ViewModel 层
 * 
 * 职责：
 * 1. 持有 UI 状态
 * 2. 处理业务逻辑
 * 3. 调用 Repository 获取数据
 * 4. 暴露 LiveData/Flow 给 UI 层
 */
class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {

    // 用户列表状态
    private val _usersState = MutableLiveData<UiState<List<User>>>()
    val usersState: LiveData<UiState<List<User>>> = _usersState

    // 当前选中用户
    private val _selectedUser = MutableLiveData<User?>()
    val selectedUser: LiveData<User?> = _selectedUser

    // 刷新状态
    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    /**
     * 加载用户列表
     */
    fun loadUsers() {
        viewModelScope.launch {
            _usersState.value = UiState.Loading
            
            val result = repository.getUsers()
            
            result.fold(
                onSuccess = { users ->
                    _usersState.value = UiState.Success(users)
                },
                onFailure = { error ->
                    _usersState.value = UiState.Error(error.message ?: "未知错误")
                }
            )
        }
    }

    /**
     * 刷新用户列表
     */
    fun refreshUsers() {
        viewModelScope.launch {
            _isRefreshing.value = true
            
            val result = repository.getUsers()
            
            result.fold(
                onSuccess = { users ->
                    _usersState.value = UiState.Success(users)
                },
                onFailure = { error ->
                    _usersState.value = UiState.Error(error.message ?: "未知错误")
                }
            )
            
            _isRefreshing.value = false
        }
    }

    /**
     * 选择用户
     */
    fun selectUser(user: User) {
        _selectedUser.value = user
    }

    /**
     * 清除选中
     */
    fun clearSelection() {
        _selectedUser.value = null
    }

    /**
     * 更新用户
     */
    fun updateUser(user: User) {
        viewModelScope.launch {
            val result = repository.updateUser(user)
            
            result.fold(
                onSuccess = { updatedUser ->
                    // 更新列表中的用户
                    val currentState = _usersState.value
                    if (currentState is UiState.Success) {
                        val updatedList = currentState.data.map {
                            if (it.id == updatedUser.id) updatedUser else it
                        }
                        _usersState.value = UiState.Success(updatedList)
                    }
                    
                    // 更新选中用户
                    if (_selectedUser.value?.id == updatedUser.id) {
                        _selectedUser.value = updatedUser
                    }
                },
                onFailure = { error ->
                    // 可以发送错误事件
                }
            )
        }
    }
}

/**
 * ViewModel Factory
 */
class UserViewModelFactory(
    private val repository: UserRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
