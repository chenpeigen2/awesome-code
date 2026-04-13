package com.peter.network.demo

import kotlinx.coroutines.test.runTest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import mockwebserver3.RecordedRequest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.peter.network.demo.api.JsonPlaceholderApi
import com.peter.network.demo.model.Post

class JsonPlaceholderApiTest {

    private lateinit var server: MockWebServer
    private lateinit var api: JsonPlaceholderApi

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JsonPlaceholderApi::class.java)
    }

    @After
    fun tearDown() {
        server.close()
    }

    @Test
    fun getPosts_returnsPostList() = runTest {
        server.enqueue(
            MockResponse(
                code = 200,
                body = """
                    [
                        {"userId":1,"id":1,"title":"test title","body":"test body"},
                        {"userId":2,"id":2,"title":"another title","body":"another body"}
                    ]
                """.trimIndent()
            )
        )

        val posts = api.getPosts()
        assertEquals(2, posts.size)
        assertEquals("test title", posts[0].title)
        assertEquals(1, posts[0].id)
    }

    @Test
    fun getPost_singlePost() = runTest {
        server.enqueue(
            MockResponse(
                code = 200,
                body = """{"userId":1,"id":1,"title":"hello","body":"world"}"""
            )
        )

        val post = api.getPost(1)
        assertEquals(1, post.id)
        assertEquals("hello", post.title)
    }

    @Test
    fun getPosts_sendsCorrectPath() = runTest {
        server.enqueue(MockResponse(code = 200, body = "[]"))

        api.getPosts()
        val request: RecordedRequest = server.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/posts", request.target)
    }

    @Test
    fun getPost_withUserId_sendsCorrectQuery() = runTest {
        server.enqueue(MockResponse(code = 200, body = "[]"))

        api.getPosts(userId = 7)
        val request: RecordedRequest = server.takeRequest()
        assertTrue(request.target.contains("userId=7"))
    }

    @Test
    fun createPost_sendsPostBody() = runTest {
        server.enqueue(
            MockResponse(
                code = 201,
                body = """{"userId":7,"id":101,"title":"new post","body":"content"}"""
            )
        )

        val post = Post(userId = 7, title = "new post", body = "content")
        val result = api.createPost(post)

        val request: RecordedRequest = server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals(101, result.id)
    }
}
