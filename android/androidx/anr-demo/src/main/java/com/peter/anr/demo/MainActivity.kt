package com.peter.anr.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.anr.demo.databinding.ActivityMainBinding

/**
 * ANR Demo 主入口
 *
 * 本 Demo 包含以下内容：
 *
 * 一、ANR 基础介绍
 * 1. ANR 定义与原理
 * 2. 四种 ANR 类型和超时阈值
 * 3. ANR 触发流程
 * 4. ANR trace 文件解读
 *
 * 二、输入事件 ANR
 * 1. 主线程 Thread.sleep 阻塞
 * 2. 死循环
 * 3. synchronized 死锁
 * 4. 正确方式对照
 *
 * 三、广播 ANR
 * 1. onReceive 耗时
 * 2. goAsync() 方案
 *
 * 四、Service ANR
 * 1. onCreate/onStartCommand 阻塞
 * 2. 正确方式对照
 *
 * 五、常见 ANR 场景
 * 1. SP commit 大数据
 * 2. 主线程 SQLite
 * 3. 主线程文件 I/O
 * 4. Binder 通信
 *
 * 六、检测与解决
 * 1. StrictMode 配置
 * 2. 监控方案
 * 3. 预防措施
 * 4. 最佳实践
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = DemoAdapter(getDemoItems()) { item ->
                item.createIntent(this@MainActivity)?.let { startActivity(it) }
            }
        }
    }

    private fun getDemoItems(): List<DemoItem> = listOf(
        DemoItem(title = "一、ANR 基础", isHeader = true),
        DemoItem(
            title = getString(R.string.anr_info),
            description = getString(R.string.anr_info_desc),
            targetClass = AnrInfoActivity::class.java
        ),

        DemoItem(title = "二、输入事件 ANR", isHeader = true),
        DemoItem(
            title = getString(R.string.input_anr),
            description = getString(R.string.input_anr_desc),
            targetClass = InputAnrActivity::class.java
        ),

        DemoItem(title = "三、广播 ANR", isHeader = true),
        DemoItem(
            title = getString(R.string.broadcast_anr),
            description = getString(R.string.broadcast_anr_desc),
            targetClass = BroadcastAnrActivity::class.java
        ),

        DemoItem(title = "四、Service ANR", isHeader = true),
        DemoItem(
            title = getString(R.string.service_anr),
            description = getString(R.string.service_anr_desc),
            targetClass = ServiceAnrActivity::class.java
        ),

        DemoItem(title = "五、常见 ANR 场景", isHeader = true),
        DemoItem(
            title = getString(R.string.common_anr),
            description = getString(R.string.common_anr_desc),
            targetClass = CommonAnrActivity::class.java
        ),

        DemoItem(title = "六、检测与解决", isHeader = true),
        DemoItem(
            title = getString(R.string.anr_solution),
            description = getString(R.string.anr_solution_desc),
            targetClass = AnrSolutionActivity::class.java
        ),
    )
}
