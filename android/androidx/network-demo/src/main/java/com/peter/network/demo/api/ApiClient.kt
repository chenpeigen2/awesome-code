package com.peter.network.demo.api

import android.content.Context
import com.google.gson.GsonBuilder
import com.peter.network.demo.interceptor.CacheInterceptor
import com.peter.network.demo.interceptor.HeaderInterceptor
import com.peter.network.demo.interceptor.LoggingInterceptor
import com.peter.network.demo.interceptor.RetryInterceptor
import java.io.File
import kotlin.time.Duration.Companion.seconds
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    fun create(
        context: Context,
        connectTimeoutSeconds: Long = 15,
        readTimeoutSeconds: Long = 15,
        addCache: Boolean = true,
        addHeaders: Boolean = true,
        addRetry: Boolean = true
    ): JsonPlaceholderApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(
                newClient(
                    context = context,
                    connectTimeoutSeconds = connectTimeoutSeconds,
                    readTimeoutSeconds = readTimeoutSeconds,
                    addCache = addCache,
                    addHeaders = addHeaders,
                    addRetry = addRetry
                )
            )
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().setLenient().create()
                )
            )
            .build()
            .create(JsonPlaceholderApi::class.java)
    }

    fun newClient(
        context: Context,
        connectTimeoutSeconds: Long = 15,
        readTimeoutSeconds: Long = 15,
        addCache: Boolean = true,
        addHeaders: Boolean = true,
        addRetry: Boolean = true
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(connectTimeoutSeconds.seconds)
            .readTimeout(readTimeoutSeconds.seconds)
            .writeTimeout(readTimeoutSeconds.seconds)
            .addInterceptor(LoggingInterceptor())

        if (addHeaders) {
            builder.addInterceptor(HeaderInterceptor())
        }
        if (addRetry) {
            builder.addInterceptor(RetryInterceptor(maxRetries = 2))
        }
        if (addCache) {
            builder.cache(Cache(File(context.cacheDir, "network_demo_cache"), 10L * 1024 * 1024))
            builder.addInterceptor(CacheInterceptor(context))
        }

        return builder.build()
    }
}
