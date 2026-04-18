package com.peter.plugin

/**
 * 插件生命周期状态
 */
enum class PluginState {
    CREATED,    // 已创建
    STARTED,    // 已启动
    STOPPED,    // 已停止
    DESTROYED   // 已销毁
}

/**
 * 插件元数据
 * 描述插件的基本信息，用于插件发现和展示
 */
data class PluginMeta(
    val name: String,
    val version: String,
    val description: String,
    val author: String,
    val capabilities: List<String>
)

/**
 * 插件上下文
 * 提供插件运行所需的环境信息
 */
data class PluginContext(
    val classLoader: ClassLoader,
    val params: Map<String, String>
)

/**
 * 插件接口
 * 定义完整的插件生命周期和行为契约
 */
interface IPlugin {
    /**
     * 获取插件元数据
     */
    fun getMeta(): PluginMeta

    /**
     * 插件创建，初始化资源
     */
    fun onCreate(context: PluginContext)

    /**
     * 插件启动，开始工作
     */
    fun onStart()

    /**
     * 插件停止，暂停工作
     */
    fun onStop()

    /**
     * 插件销毁，释放资源
     */
    fun onDestroy()

    /**
     * 获取当前状态
     */
    fun getState(): PluginState

    /**
     * 执行插件主功能
     * @param params 运行时参数
     * @return 执行结果
     */
    fun execute(params: Map<String, String>): String
}
