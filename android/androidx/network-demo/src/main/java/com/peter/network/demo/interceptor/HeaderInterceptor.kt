package com.peter.network.demo.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
            .header("X-Demo-Platform", "Android")
            .header("X-Demo-Module", "network-demo")
            .header("X-Demo-Version", "1.0")
            .build()
        InMemoryLogStore.add("header: injected demo headers")
        return chain.proceed(newRequest)
    }
}
