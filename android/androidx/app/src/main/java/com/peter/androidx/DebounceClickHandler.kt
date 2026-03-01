package com.peter.androidx

import android.view.View
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

// 1. 定义一个 InvocationHandler
class DebounceClickHandler(
    private val target: View.OnClickListener // 真实的点击监听器
) : InvocationHandler {

    private var lastClickTime = 0L
    private val threshold = 1500L // 防抖时间间隔

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
        // 只拦截 onClick 方法
        if (method?.name == "onClick") {
            val now = System.currentTimeMillis()
            if (now - lastClickTime < threshold) {
                return null // 时间太短，直接拦截，不执行真实逻辑
            }
            lastClickTime = now
        }
        // 执行真实的点击逻辑
        return method?.invoke(target, *(args ?: emptyArray()))
    }
}

// 2. 定义扩展函数，方便调用
fun View.setDebouncedClickListener(listener: View.OnClickListener) {
    // 创建代理对象
    val proxyListener = Proxy.newProxyInstance(
        listener.javaClass.classLoader,
        arrayOf(View.OnClickListener::class.java),
        DebounceClickHandler(listener)
    ) as View.OnClickListener

    this.setOnClickListener(proxyListener)
}
