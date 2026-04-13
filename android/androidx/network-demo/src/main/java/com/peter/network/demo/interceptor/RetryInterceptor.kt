package com.peter.network.demo.interceptor

import java.io.IOException
import okhttp3.Interceptor
import okhttp3.Response

class RetryInterceptor(
    private val maxRetries: Int = 2
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var currentTry = 0
        var lastError: IOException? = null

        while (currentTry <= maxRetries) {
            try {
                if (currentTry > 0) {
                    InMemoryLogStore.add("retry: attempt ${currentTry + 1} for ${request.url.encodedPath}")
                }
                return chain.proceed(request)
            } catch (e: IOException) {
                lastError = e
                currentTry++
            }
        }

        throw lastError ?: IOException("Unknown retry error")
    }
}
