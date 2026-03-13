package com.peter.room.demo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.peter.room.demo.db.entity.User
import com.peter.room.demo.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * 基础用法 ViewModel
 */
class BasicViewModel(
    private val repository: UserRepository
) : ViewModel() {
    
    val users: StateFlow<List<User>> = repository.allUsers
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    private val _operationState = MutableStateFlow<String?>(null)
    val operationState: StateFlow<String?> = _operationState.asStateFlow()
    
    fun addUser(name: String, age: Int, email: String) {
        viewModelScope.launch {
            try {
                val id = repository.addUser(name, age, email)
                _operationState.value = if (id > 0) "添加成功" else "添加失败"
            } catch (e: Exception) {
                _operationState.value = "添加失败: ${e.message}"
            }
        }
    }
    
    fun deleteUser(user: User) {
        viewModelScope.launch {
            try {
                repository.deleteUser(user)
                _operationState.value = "删除成功"
            } catch (e: Exception) {
                _operationState.value = "删除失败: ${e.message}"
            }
        }
    }
    
    fun deleteAllUsers() {
        viewModelScope.launch {
            try {
                repository.deleteAllUsers()
                _operationState.value = "已删除所有用户"
            } catch (e: Exception) {
                _operationState.value = "删除失败: ${e.message}"
            }
        }
    }
    
    fun clearOperationState() {
        _operationState.value = null
    }
}

class BasicViewModelFactory(
    private val repository: UserRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BasicViewModel::class.java)) {
            return BasicViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}