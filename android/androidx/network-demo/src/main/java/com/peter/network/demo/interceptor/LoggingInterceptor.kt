package com.peter.network.demo.interceptor

import java.io.IOException
import okhttp3.Interceptor
import okhttp3.Response

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val start = System.currentTimeMillis()
        InMemoryLogStore.add("-> ${request.method} ${request.url.encodedPath}")
        return try {
            val response = chain.proceed(request)
            val cost = System.currentTimeMillis() - start
            InMemoryLogStore.add("<- ${response.code} ${request.url.encodedPath} (${cost}ms)")
            response
        } catch (e: IOException) {
            val cost = System.currentTimeMillis() - start
            InMemoryLogStore.add("xx ${request.url.encodedPath} failed in ${cost}ms: ${e.message}")
            throw e
        }
    }
}
