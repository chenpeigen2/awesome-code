# Network Demo 设计规范

## 概述

创建一个独立的 `network-demo` 模块，用于学习 Android 网络请求的核心技术栈：Retrofit + OkHttp + Gson + Coil。通过 JSONPlaceholder 公共 REST API 演示从基础 CRUD 到拦截器、图片加载、文件操作和错误处理的完整网络开发流程。

## 技术方案

**方案**：Retrofit + OkHttp + Gson + Coil 经典组合

**API**：JSONPlaceholder（`https://jsonplaceholder.typicode.com/`）

**理由**：免费、无需 API Key、支持完整 CRUD、资源丰富（posts/comments/users/todos）

## 项目架构

**模块名称**：`network-demo`

**包名**：`com.peter.network.demo`

**UI 模式**：BottomNavigationView + 4 Tab Fragment

**目录结构**：
```
network-demo/
├── build.gradle.kts
├── proguard-rules.pro
└── src/main/
    ├── AndroidManifest.xml
    ├── java/com/peter/network/demo/
    │   ├── MainActivity.kt                    # BottomNavigationView + 4 Tab
    │   ├── api/
    │   │   ├── JsonPlaceholderApi.kt          # Retrofit 接口定义
    │   │   └── ApiClient.kt                   # Retrofit + OkHttpClient 单例
    │   ├── model/
    │   │   ├── Post.kt                        # 帖子数据模型
    │   │   ├── Comment.kt                     # 评论数据模型
    │   │   ├── User.kt                        # 用户数据模型
    │   │   ├── Todo.kt                        # 待办数据模型
    │   │   └── NetworkResult.kt               # 统一结果封装
    │   ├── interceptor/
    │   │   ├── LoggingInterceptor.kt          # 日志拦截器
    │   │   ├── CacheInterceptor.kt            # 缓存拦截器
    │   │   ├── RetryInterceptor.kt            # 重试拦截器
    │   │   └── HeaderInterceptor.kt           # 自定义 Header 拦截器
    │   ├── repository/
    │   │   └── PostRepository.kt              # Repository 模式封装
    │   ├── viewmodel/
    │   │   ├── BasicViewModel.kt              # Tab 1: 基础请求
    │   │   ├── InterceptorViewModel.kt        # Tab 2: 拦截器
    │   │   ├── MediaViewModel.kt             # Tab 3: 图片 & 文件
    │   │   └── PracticalViewModel.kt          # Tab 4: 错误处理 & 实战
    │   ├── fragment/
    │   │   ├── BasicFragment.kt               # Tab 1: 基础请求
    │   │   ├── InterceptorFragment.kt         # Tab 2: 拦截器
    │   │   ├── MediaFragment.kt              # Tab 3: 图片 & 文件
    │   │   └── PracticalFragment.kt           # Tab 4: 错误处理 & 实战
    │   └── adapter/
    │       ├── PostAdapter.kt                 # 帖子列表 Adapter
    │       └── UserAdapter.kt                 # 用户列表 Adapter
    └── res/
        ├── layout/
        │   ├── activity_main.xml
        │   ├── fragment_basic.xml
        │   ├── fragment_interceptor.xml
        │   ├── fragment_media.xml
        │   ├── fragment_practical.xml
        │   ├── item_post.xml
        │   └── item_user.xml
        ├── menu/
        │   └── bottom_nav_menu.xml
        ├── drawable/                          # Tab 图标
        ├── mipmap-*/                          # App 图标
        └── values/
            ├── strings.xml
            ├── colors.xml
            └── themes.xml
```

## 四个 Tab 详细内容

### Tab 1: 基础请求 (BasicFragment + BasicViewModel)

使用 `/posts`、`/users` 资源演示 Retrofit 基础操作：

- **GET 请求**：获取帖子列表、查询单个帖子（`@Path`）、按条件筛选（`@Query`）
- **POST 请求**：创建新帖子（`@Body`）
- **PUT 请求**：更新帖子
- **DELETE 请求**：删除帖子
- **Header 操作**：`@Headers`、`@Header` 注解
- **Call vs suspend**：对比 `Call<T>` 回调和 `suspend` 协程两种调用方式
- **Gson Converter**：JSON 序列化与反序列化

### Tab 2: 拦截器 (InterceptorFragment + InterceptorViewModel)

演示 OkHttp 拦截器机制：

- **日志拦截器**：自定义日志格式，记录请求/响应详情
- **缓存拦截器**：离线缓存策略，对比有无缓存时的行为差异
- **重试拦截器**：失败自动重试，可配置重试次数
- **Header 拦截器**：统一添加公共 Header（如 token、设备信息）
- **Interceptor vs NetworkInterceptor**：应用拦截器与网络拦截器的区别演示

### Tab 3: 图片 & 文件 (MediaFragment + MediaViewModel)

- **Coil 图片加载**：基础加载、占位图、圆形裁剪、缓存策略
- **文件下载**：下载文件到本地存储，显示下载进度
- **文件上传**：`@Multipart` 上传文件（使用 MockWebServer 模拟服务端）
- **进度监听**：通过 OkHttp 拦截器监听上传/下载进度

### Tab 4: 错误处理 & 实战 (PracticalFragment + PracticalViewModel)

- **NetworkResult 封装**：`Success / Error / Loading` 密封类统一结果处理
- **网络状态监听**：ConnectivityManager 检测网络连接变化
- **超时配置**：OkHttp connectTimeout / readTimeout / writeTimeout 设置
- **Repository 模式**：整合 ViewModel + Repository + API 三层架构
- **统一错误处理**：HTTP 错误码映射、JSON 解析异常、网络不可用等场景

## 依赖项

在 `gradle/libs.versions.toml` 中新增：

```toml
[versions]
retrofit = "2.11.0"
okhttp = "4.12.0"
coil = "2.7.0"

[libraries]
# Retrofit
retrofit = { module = "com.squareup.retrofit2:retrofit", version.ref = "retrofit" }
retrofit-converter-gson = { module = "com.squareup.retrofit2:converter-gson", version.ref = "retrofit" }

# OkHttp
okhttp = { module = "com.squareup.okhttp3:okhttp", version.ref = "okhttp" }
okhttp-logging = { module = "com.squareup.okhttp3:logging-interceptor", version.ref = "okhttp" }
okhttp-mockwebserver = { module = "com.squareup.okhttp3:mockwebserver", version.ref = "okhttp" }

# Coil
coil = { module = "io.coil-kt:coil", version.ref = "coil" }
```

模块 `build.gradle.kts` 额外依赖：
```kotlin
// Network
implementation(libs.retrofit)
implementation(libs.retrofit.converter.gson)
implementation(libs.okhttp)
implementation(libs.okhttp.logging)
debugImplementation(libs.okhttp.mockwebserver)
implementation(libs.coil)
```

## API 接口设计

```kotlin
interface JsonPlaceholderApi {
    // GET
    @GET("posts")
    suspend fun getPosts(@Query("userId") userId: Int? = null): List<Post>

    @GET("posts/{id}")
    suspend fun getPost(@Path("id") id: Int): Post

    @GET("posts/{id}/comments")
    suspend fun getComments(@Path("id") postId: Int): List<Comment>

    @GET("users")
    suspend fun getUsers(): List<User>

    // POST
    @POST("posts")
    suspend fun createPost(@Body post: Post): Post

    // PUT
    @PUT("posts/{id}")
    suspend fun updatePost(@Path("id") id: Int, @Body post: Post): Post

    // DELETE
    @DELETE("posts/{id}")
    suspend fun deletePost(@Path("id") id: Int)

    // Header 演示
    @GET("posts")
    @Headers("Cache-Control: max-age=60")
    suspend fun getPostsWithHeader(): List<Post>

    // 文件上传 (MockWebServer)
    @Multipart
    @POST("upload")
    suspend fun uploadFile(@Part file: MultipartBody.Part): ResponseBody
}
```

## 核心数据模型

```kotlin
data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)

data class Comment(
    val postId: Int,
    val id: Int,
    val name: String,
    val email: String,
    val body: String
)

data class User(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val website: String
)

data class Todo(
    val userId: Int,
    val id: Int,
    val title: String,
    val completed: Boolean
)
```

## NetworkResult 封装

```kotlin
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val code: Int?, val message: String) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
}
```

## 构建配置

遵循项目现有约定：
- **SDK**：compileSdk=36, targetSdk=36, minSdk=33
- **Java**：VERSION_11
- **Kotlin**：JVM_11
- **ViewBinding**：启用
- **INTERNET 权限**：AndroidManifest 中声明
- **模块注册**：在 `settings.gradle.kts` 中 `include(":network-demo")`
