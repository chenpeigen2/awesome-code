package com.peter.dagger.demo.scope

import java.util.UUID

/**
 * RequestService - 请求服务
 *
 * 无作用域示例
 * 每次请求都创建新实例
 */
class RequestService {

    val instanceId: String = UUID.randomUUID().toString()

    init {
        println("RequestService: 创建新实例 [$instanceId]")
    }

    override fun toString(): String {
        return "RequestService(hashCode=${hashCode()}, instanceId=$instanceId)"
    }
}
