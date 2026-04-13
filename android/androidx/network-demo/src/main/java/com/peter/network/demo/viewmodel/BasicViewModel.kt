package com.peter.network.demo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.peter.network.demo.model.NetworkResult
import com.peter.network.demo.model.Post
import com.peter.network.demo.repository.PostRepository
import kotlinx.coroutines.launch

class BasicViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PostRepository(application)

    val status = MutableLiveData("点击按钮开始体验 Retrofit 基础请求")
    val posts = MutableLiveData<List<Post>>(emptyList())

    fun loadPosts() {
        status.value = "Loading GET /posts ..."
        viewModelScope.launch {
            when (val result = repository.getPosts()) {
                is NetworkResult.Success -> {
                    posts.value = result.data.take(20)
                    status.value = "GET /posts 成功，展示前 ${posts.value?.size ?: 0} 条"
                }

                is NetworkResult.Error -> status.value = result.message
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun loadSinglePost() {
        status.value = "Loading GET /posts/1 ..."
        viewModelScope.launch {
            when (val result = repository.getPost(1)) {
                is NetworkResult.Success -> {
                    posts.value = listOf(result.data)
                    status.value = "GET /posts/1 成功"
                }

                is NetworkResult.Error -> status.value = result.message
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun createPost() {
        status.value = "Loading POST /posts ..."
        viewModelScope.launch {
            when (val result = repository.createPost()) {
                is NetworkResult.Success -> {
                    posts.value = listOf(result.data) + (posts.value ?: emptyList())
                    status.value = "POST /posts 成功，新建帖子 id=${result.data.id ?: 101}"
                }

                is NetworkResult.Error -> status.value = result.message
                NetworkResult.Loading -> Unit
            }
        }
    }
}
