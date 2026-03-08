package com.peter.touch.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.touch.demo.databinding.ActivityMainBinding
import com.peter.touch.demo.level1.BasicDispatchActivity
import com.peter.touch.demo.level2.InterceptConsumeActivity
import com.peter.touch.demo.level3.ScrollConflictActivity
import com.peter.touch.demo.level4.GestureDetectActivity
import com.peter.touch.demo.level4.MultiTouchActivity
import com.peter.touch.demo.level4.NestedScrollingActivity

/**
 * 入口列表页
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TouchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)
    }

    private fun setupRecyclerView() {
        adapter = TouchAdapter { item ->
            navigateToItem(item)
        }
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
        
        adapter.submitList(getItems())
    }

    private fun navigateToItem(item: TouchItem) {
        val intent = Intent(this, item.activityClass)
        startActivity(intent)
    }

    private fun getItems(): List<TouchItem> = listOf(
        // 基础
        TouchItem(
            id = "level1",
            title = getString(R.string.level1_title),
            description = getString(R.string.level1_desc),
            category = TouchItem.Category.BASIC,
            activityClass = BasicDispatchActivity::class.java
        ),
        
        // 进阶
        TouchItem(
            id = "level2",
            title = getString(R.string.level2_title),
            description = getString(R.string.level2_desc),
            category = TouchItem.Category.INTERMEDIATE,
            activityClass = InterceptConsumeActivity::class.java
        ),
        TouchItem(
            id = "level3",
            title = getString(R.string.level3_title),
            description = getString(R.string.level3_desc),
            category = TouchItem.Category.INTERMEDIATE,
            activityClass = ScrollConflictActivity::class.java
        ),
        
        // 高级
        TouchItem(
            id = "level4_multi",
            title = getString(R.string.level4_multi_title),
            description = getString(R.string.level4_multi_desc),
            category = TouchItem.Category.ADVANCED,
            activityClass = MultiTouchActivity::class.java
        ),
        TouchItem(
            id = "level4_gesture",
            title = getString(R.string.level4_gesture_title),
            description = getString(R.string.level4_gesture_desc),
            category = TouchItem.Category.ADVANCED,
            activityClass = GestureDetectActivity::class.java
        ),
        TouchItem(
            id = "level4_nested",
            title = getString(R.string.level4_nested_title),
            description = getString(R.string.level4_nested_desc),
            category = TouchItem.Category.ADVANCED,
            activityClass = NestedScrollingActivity::class.java
        )
    )
}
