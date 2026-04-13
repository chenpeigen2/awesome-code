package com.peter.network.demo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.peter.network.demo.model.NetworkResult
import com.peter.network.demo.repository.PostRepository
import kotlinx.coroutines.launch

class MediaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PostRepository(application)

    val imageUrl = MutableLiveData(randomImageUrl())
    val status = MutableLiveData("Coil 加载图片，OkHttp 下载 JSON 到缓存目录")

    fun refreshImage() {
        imageUrl.value = randomImageUrl()
        status.value = "已刷新图片地址，重新触发 Coil 加载"
    }

    fun downloadSampleJson() {
        status.value = "正在下载 posts/1 ..."
        viewModelScope.launch {
            when (val result = repository.downloadSampleJson()) {
                is NetworkResult.Success -> {
                    status.value = "下载完成: ${result.data.absolutePath}"
                }

                is NetworkResult.Error -> status.value = result.message
                NetworkResult.Loading -> Unit
            }
        }
    }

    private fun randomImageUrl(): String = "https://picsum.photos/seed/${System.currentTimeMillis()}/960/540"
}
