package com.peter.dagger.demo.multibinding

/**
 * Plugin - 插件接口
 *
 * 用于演示 @Multibindings 的 Set 注入
 */
interface Plugin {
    /**
     * 插件名称
     */
    fun name(): String

    /**
     * 执行插件
     */
    fun execute(): String
}
