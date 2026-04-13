package com.peter.network.demo.interceptor

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response

class CacheInterceptor(
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        if (!isNetworkAvailable()) {
            requestBuilder.cacheControl(CacheControl.FORCE_CACHE)
            InMemoryLogStore.add("cache: offline mode, forcing cache")
        }

        val response = chain.proceed(requestBuilder.build())
        return if (isNetworkAvailable()) {
            InMemoryLogStore.add("cache: network available, cache for 60s")
            response.newBuilder()
                .header("Cache-Control", "public, max-age=60")
                .build()
        } else {
            response
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
