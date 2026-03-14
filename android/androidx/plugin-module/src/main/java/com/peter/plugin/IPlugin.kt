package com.peter.plugin

/**
 * 插件接口
 * 定义插件的基本行为，用于动态加载演示
 */
interface IPlugin {
    /**
     * 获取插件名称
     */
    fun getName(): String
    
    /**
     * 获取插件版本
     */
    fun getVersion(): String
    
    /**
     * 执行插件功能
     * @return 执行结果描述
     */
    fun execute(): String
}
