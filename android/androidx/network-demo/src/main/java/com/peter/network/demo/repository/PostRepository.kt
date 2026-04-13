package com.peter.network.demo.repository

import android.app.Application
import com.peter.network.demo.api.ApiClient
import com.peter.network.demo.interceptor.InMemoryLogStore
import com.peter.network.demo.model.Comment
import com.peter.network.demo.model.NetworkResult
import com.peter.network.demo.model.Post
import com.peter.network.demo.model.User
import java.io.File
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import kotlin.coroutines.resume

class PostRepository(
    application: Application
) {
    private val appContext = application.applicationContext
    private val api by lazy { ApiClient.create(appContext) }

    fun clearLogs() {
        InMemoryLogStore.clear()
    }

    fun readLogs(): String = InMemoryLogStore.snapshot().joinToString("\n")

    suspend fun getPosts(): NetworkResult<List<Post>> = safeApiCall { api.getPosts() }

    suspend fun getPost(id: Int): NetworkResult<Post> = safeApiCall { api.getPost(id) }

    suspend fun createPost(): NetworkResult<Post> {
        val draft = Post(
            userId = 7,
            title = "network-demo create post",
            body = "Created at ${System.currentTimeMillis()}"
        )
        return safeApiCall { api.createPost(draft) }
    }

    suspend fun getUsers(): NetworkResult<List<User>> = safeApiCall { api.getUsers() }

    suspend fun getComments(postId: Int): NetworkResult<List<Comment>> =
        safeApiCall { api.getComments(postId) }

    suspend fun updatePost(id: Int, post: Post): NetworkResult<Post> =
        safeApiCall { api.updatePost(id, post) }

    suspend fun deletePost(id: Int): NetworkResult<String> = safeApiCall {
        api.deletePost(id)
        "deleted successfully"
    }

    suspend fun uploadDemo(): NetworkResult<String> = safeApiCall {
        val content = "upload demo content from network-demo"
        val body = content.toRequestBody("text/plain".toMediaType())
        val part = MultipartBody.Part.createFormData("file", "demo.txt", body)
        try {
            api.uploadFile(part).string()
        } catch (e: Exception) {
            "upload endpoint not available on jsonplaceholder"
        }
    }

    // ？？？
    suspend fun runCallbackDemo(): NetworkResult<List<Post>> = suspendCancellableCoroutine { continuation ->
        api.getPostsCall().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (!continuation.isActive) return
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    InMemoryLogStore.add("callback: Retrofit Call<T> success with ${body.size} posts")
                    continuation.resume(NetworkResult.Success(body))
                } else {
                    continuation.resume(
                        NetworkResult.Error(
                            code = response.code(),
                            message = "Call<T> 请求失败"
                        )
                    )
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                if (!continuation.isActive) return
                continuation.resume(NetworkResult.Error(message = t.message ?: "Call<T> 请求失败", throwable = t))
            }
        })
    }

    suspend fun downloadSampleJson(): NetworkResult<File> = withContext(Dispatchers.IO) {
        try {
            val client = ApiClient.newClient(appContext)
            val request = Request.Builder()
                .url("https://jsonplaceholder.typicode.com/posts/1")
                .build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(response.code, "下载失败: HTTP ${response.code}")
                }
                val target = File(appContext.cacheDir, "post_1.json")
                target.writeText(response.body?.string().orEmpty())
                NetworkResult.Success(target)
            }
        } catch (e: IOException) {
            NetworkResult.Error(message = e.message ?: "下载失败", throwable = e)
        }
    }

    suspend fun loadPostErrorCase(): NetworkResult<Post> = safeApiCall { api.getPost(999999) }

    suspend fun runHeaderDemo(): NetworkResult<List<Post>> = safeApiCall { api.getPostsWithHeader() }

    private suspend fun <T> safeApiCall(block: suspend () -> T): NetworkResult<T> {
        return withContext(Dispatchers.IO) {
            try {
                NetworkResult.Success(block())
            } catch (e: HttpException) {
                NetworkResult.Error(
                    code = e.code(),
                    message = "HTTP ${e.code()}: ${e.message()}",
                    throwable = e
                )
            } catch (e: IOException) {
                NetworkResult.Error(message = "网络异常: ${e.message}", throwable = e)
            } catch (e: Exception) {
                NetworkResult.Error(message = e.message ?: "未知异常", throwable = e)
            }
        }
    }
}
