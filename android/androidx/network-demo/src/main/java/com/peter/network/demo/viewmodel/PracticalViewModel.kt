package com.peter.network.demo.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.peter.network.demo.model.NetworkResult
import com.peter.network.demo.model.User
import com.peter.network.demo.repository.PostRepository
import kotlinx.coroutines.launch

class PracticalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PostRepository(application)

    val users = MutableLiveData<List<User>>(emptyList())
    val status = MutableLiveData("统一错误处理 + Repository 模式 + 网络状态检测")
    val networkStatus = MutableLiveData(readNetworkStatus(application))

    fun refreshNetworkStatus() {
        networkStatus.value = readNetworkStatus(getApplication())
    }

    fun loadUsers() {
        status.value = "Loading GET /users ..."
        viewModelScope.launch {
            when (val result = repository.getUsers()) {
                is NetworkResult.Success -> {
                    users.value = result.data
                    status.value = "GET /users 成功，共 ${result.data.size} 位用户"
                }

                is NetworkResult.Error -> status.value = result.message
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun loadErrorCase() {
        status.value = "请求一个不存在的资源，演示错误映射 ..."
        viewModelScope.launch {
            when (val result = repository.loadPostErrorCase()) {
                is NetworkResult.Success -> status.value = "异常演示未命中，返回 id=${result.data.id}"
                is NetworkResult.Error -> status.value = "错误映射: ${result.message}"
                NetworkResult.Loading -> Unit
            }
        }
    }

    private fun readNetworkStatus(context: Context): String {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return "当前网络: 不可用"
        val capabilities = connectivityManager.getNetworkCapabilities(network)
            ?: return "当前网络: 不可用"

        val transport = when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            else -> "Other"
        }
        return "当前网络: 可用 ($transport)"
    }
}
