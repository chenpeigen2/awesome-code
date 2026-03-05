package com.peter.workmanager.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.workmanager.demo.databinding.ActivityMainBinding

/**
 * WorkManager Demo 主入口
 * 
 * 本 Demo 包含以下内容：
 * 
 * 一、WorkManager 基础
 * 1. 基础 Worker - 最基本的 Worker 使用
 * 2. 一次性任务 - OneTimeWorkRequest 使用
 * 3. 周期性任务 - PeriodicWorkRequest 使用
 * 4. 任务数据传递 - Data 对象使用
 * 5. 任务状态监听 - WorkInfo 和 LiveData
 * 
 * 二、WorkManager 进阶
 * 1. 任务约束条件 - Constraints 使用
 * 2. 链式任务 - WorkContinuation 使用
 * 3. 唯一任务 - UniqueWork 使用
 * 4. 任务标签 - Tag 使用
 * 
 * 三、WorkManager 高级
 * 1. CoroutineWorker - 协程 Worker
 * 2. 异常处理 - 重试策略
 * 
 * 四、WorkManager 实战
 * 1. 文件下载 - 使用 WorkManager 下载文件
 * 2. 图片压缩 - 使用 WorkManager 压缩图片
 * 3. 数据同步 - 使用 WorkManager 同步数据
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
            adapter = MainAdapter(getMenuItems()) { item ->
                item.intent?.let { startActivity(it) }
            }
        }
    }

    private fun getMenuItems(): List<MenuItem> {
        return listOf(
            // WorkManager 基础
            MenuItem(
                title = getString(R.string.basic_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.basic_worker),
                description = getString(R.string.basic_worker_desc),
                intent = createBasicWorkerIntent(this)
            ),
            MenuItem(
                title = getString(R.string.one_time_work),
                description = getString(R.string.one_time_work_desc),
                intent = createOneTimeWorkIntent(this)
            ),
            MenuItem(
                title = getString(R.string.periodic_work),
                description = getString(R.string.periodic_work_desc),
                intent = createPeriodicWorkIntent(this)
            ),
            MenuItem(
                title = getString(R.string.work_data),
                description = getString(R.string.work_data_desc),
                intent = createWorkDataIntent(this)
            ),
            MenuItem(
                title = getString(R.string.work_status),
                description = getString(R.string.work_status_desc),
                intent = createWorkStatusIntent(this)
            ),

            // WorkManager 进阶
            MenuItem(
                title = getString(R.string.advanced_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.work_constraints),
                description = getString(R.string.work_constraints_desc),
                intent = createWorkConstraintsIntent(this)
            ),
            MenuItem(
                title = getString(R.string.work_chain),
                description = getString(R.string.work_chain_desc),
                intent = createWorkChainIntent(this)
            ),
            MenuItem(
                title = getString(R.string.unique_work),
                description = getString(R.string.unique_work_desc),
                intent = createUniqueWorkIntent(this)
            ),
            MenuItem(
                title = getString(R.string.work_tag),
                description = getString(R.string.work_tag_desc),
                intent = createWorkTagIntent(this)
            ),

            // WorkManager 高级
            MenuItem(
                title = getString(R.string.expert_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.coroutine_worker),
                description = getString(R.string.coroutine_worker_desc),
                intent = createCoroutineWorkerIntent(this)
            ),
            MenuItem(
                title = getString(R.string.work_exception),
                description = getString(R.string.work_exception_desc),
                intent = createWorkExceptionIntent(this)
            ),

            // WorkManager 实战
            MenuItem(
                title = getString(R.string.practice_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.download_file),
                description = getString(R.string.download_file_desc),
                intent = createDownloadFileIntent(this)
            ),
            MenuItem(
                title = getString(R.string.image_compress),
                description = getString(R.string.image_compress_desc),
                intent = createImageCompressIntent(this)
            ),
            MenuItem(
                title = getString(R.string.data_sync),
                description = getString(R.string.data_sync_desc),
                intent = createDataSyncIntent(this)
            )
        )
    }
}
