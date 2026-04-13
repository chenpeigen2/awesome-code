package com.peter.network.demo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.peter.network.demo.model.NetworkResult
import com.peter.network.demo.repository.PostRepository
import kotlinx.coroutines.launch

class InterceptorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PostRepository(application)

    val status = MutableLiveData("这里展示 Header / Retry / Cache / Call<T> 日志")
    val logs = MutableLiveData("")

    fun runHeaderDemo() {
        status.value = "触发 Header + Logging demo ..."
        viewModelScope.launch {
            when (val result = repository.runHeaderDemo()) {
                is NetworkResult.Success -> status.value = "Header demo 完成，共返回 ${result.data.size} 条 posts"
                is NetworkResult.Error -> status.value = result.message
                NetworkResult.Loading -> Unit
            }
            refreshLogs()
        }
    }

    fun runCallbackDemo() {
        status.value = "触发 Retrofit Call<T> demo ..."
        viewModelScope.launch {
            when (val result = repository.runCallbackDemo()) {
                is NetworkResult.Success -> status.value = "Call<T> demo 完成，共返回 ${result.data.size} 条 posts"
                is NetworkResult.Error -> status.value = result.message
                NetworkResult.Loading -> Unit
            }
            refreshLogs()
        }
    }

    fun clearLogs() {
        repository.clearLogs()
        logs.value = ""
        status.value = "日志已清空"
    }

    fun refreshLogs() {
        logs.value = repository.readLogs().ifBlank { "暂无日志，先点击上面的按钮触发请求。" }
    }
}
