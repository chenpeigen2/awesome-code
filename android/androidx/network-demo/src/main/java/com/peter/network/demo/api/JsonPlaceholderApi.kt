package com.peter.network.demo.api

import com.peter.network.demo.model.Comment
import com.peter.network.demo.model.Post
import com.peter.network.demo.model.User
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface JsonPlaceholderApi {

    @GET("posts")
    suspend fun getPosts(@Query("userId") userId: Int? = null): List<Post>

    @GET("posts/{id}")
    suspend fun getPost(@Path("id") id: Int): Post

    @GET("posts")
    fun getPostsCall(@Header("X-Call-Demo") callDemo: String = "callback"): Call<List<Post>>

    @GET("posts/{id}/comments")
    suspend fun getComments(@Path("id") postId: Int): List<Comment>

    @GET("users")
    suspend fun getUsers(): List<User>

    @POST("posts")
    suspend fun createPost(@Body post: Post): Post

    @PUT("posts/{id}")
    suspend fun updatePost(@Path("id") id: Int, @Body post: Post): Post

    @DELETE("posts/{id}")
    suspend fun deletePost(@Path("id") id: Int): ResponseBody

    @GET("posts")
    @Headers("Cache-Control: max-age=60")
    suspend fun getPostsWithHeader(): List<Post>

    @Multipart
    @POST("upload")
    suspend fun uploadFile(@Part file: MultipartBody.Part): ResponseBody
}
