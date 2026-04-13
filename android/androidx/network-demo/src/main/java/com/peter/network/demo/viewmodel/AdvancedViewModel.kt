package com.peter.network.demo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.peter.network.demo.model.Comment
import com.peter.network.demo.model.NetworkResult
import com.peter.network.demo.model.Post
import com.peter.network.demo.repository.PostRepository
import kotlinx.coroutines.launch

class AdvancedViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PostRepository(application)

    val status = MutableLiveData("PUT / DELETE / Comments / Multipart Upload")
    val resultPost = MutableLiveData<Post?>()
    val comments = MutableLiveData<List<Comment>>(emptyList())

    fun updatePost() {
        status.value = "Loading PUT /posts/1 ..."
        resultPost.value = null
        comments.value = emptyList()
        viewModelScope.launch {
            val updated = Post(
                userId = 1,
                id = 1,
                title = "updated title ${System.currentTimeMillis() % 1000}",
                body = "updated body via PUT"
            )
            when (val result = repository.updatePost(1, updated)) {
                is NetworkResult.Success -> {
                    resultPost.value = result.data
                    status.value = "PUT 成功: id=${result.data.id}, title=${result.data.title}"
                }
                is NetworkResult.Error -> status.value = "PUT 失败: ${result.message}"
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun deletePost() {
        status.value = "Loading DELETE /posts/1 ..."
        resultPost.value = null
        comments.value = emptyList()
        viewModelScope.launch {
            when (val result = repository.deletePost(1)) {
                is NetworkResult.Success -> {
                    status.value = "DELETE 成功: ${result.data}"
                }
                is NetworkResult.Error -> status.value = "DELETE 失败: ${result.message}"
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun loadComments() {
        status.value = "Loading GET /posts/1/comments ..."
        resultPost.value = null
        viewModelScope.launch {
            when (val result = repository.getComments(1)) {
                is NetworkResult.Success -> {
                    comments.value = result.data
                    status.value = "GET Comments 成功，共 ${result.data.size} 条评论"
                }
                is NetworkResult.Error -> status.value = "GET Comments 失败: ${result.message}"
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun uploadDemo() {
        status.value = "演示 Multipart Upload（构造请求）..."
        resultPost.value = null
        comments.value = emptyList()
        viewModelScope.launch {
            when (val result = repository.uploadDemo()) {
                is NetworkResult.Success -> {
                    status.value = "Upload 构造成功（jsonplaceholder 不支持 upload，但请求已发出）"
                }
                is NetworkResult.Error -> status.value = "Upload: ${result.message}"
                NetworkResult.Loading -> Unit
            }
        }
    }
}
