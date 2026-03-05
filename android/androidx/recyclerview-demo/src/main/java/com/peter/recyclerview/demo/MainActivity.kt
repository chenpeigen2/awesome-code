package com.peter.recyclerview.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.recyclerview.demo.advanced.DiffUtilActivity
import com.peter.recyclerview.demo.advanced.MultiTypeActivity
import com.peter.recyclerview.demo.advanced.PagingSimulationActivity
import com.peter.recyclerview.demo.basic.DragSwipeActivity
import com.peter.recyclerview.demo.basic.GridListActivity
import com.peter.recyclerview.demo.basic.HeaderFooterActivity
import com.peter.recyclerview.demo.basic.SimpleListActivity
import com.peter.recyclerview.demo.basic.StaggeredGridActivity
import com.peter.recyclerview.demo.conflict.CoordinatorActivity
import com.peter.recyclerview.demo.conflict.CustomConflictActivity
import com.peter.recyclerview.demo.conflict.NestedScrollActivity
import com.peter.recyclerview.demo.conflict.RecyclerViewNestedActivity
import com.peter.recyclerview.demo.conflict.ViewPager2NestedActivity
import com.peter.recyclerview.demo.databinding.ActivityMainBinding

/**
 * RecyclerView Demo 主入口
 * 
 * 本 Demo 包含以下内容：
 * 
 * 一、基础示例
 * 1. 简单列表 - LinearLayoutManager 基本使用
 * 2. 网格列表 - GridLayoutManager 网格布局
 * 3. 瀑布流布局 - StaggeredGridLayoutManager
 * 4. 头部与尾部 - 添加 Header 和 Footer
 * 5. 拖拽与滑动删除 - ItemTouchHelper
 * 
 * 二、进阶示例
 * 1. 多类型 Item - sealed class + 多 ViewHolder
 * 2. DiffUtil 数据更新 - 高效更新数据
 * 3. 分页加载模拟 - 滚动加载更多
 * 
 * 三、滑动冲突示例（重点）
 * 1. ViewPager2 嵌套 - 同方向/不同方向滑动
 * 2. RecyclerView 嵌套 - 外层垂直 + 内层水平
 * 3. NestedScrollView 嵌套 - 问题与解决方案
 * 4. CoordinatorLayout 联动 - AppBarLayout 联动
 * 5. 自定义冲突处理 - 手动处理滑动冲突
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
                startActivity(item.intent)
            }
        }
    }

    private fun getMenuItems(): List<MenuItem> {
        return listOf(
            // 基础示例
            MenuItem(
                title = getString(R.string.basic_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.simple_list),
                description = getString(R.string.simple_list_desc),
                intent = createSimpleListIntent(this)
            ),
            MenuItem(
                title = getString(R.string.grid_list),
                description = getString(R.string.grid_list_desc),
                intent = createGridListIntent(this)
            ),
            MenuItem(
                title = getString(R.string.staggered_grid),
                description = getString(R.string.staggered_grid_desc),
                intent = createStaggeredGridIntent(this)
            ),
            MenuItem(
                title = getString(R.string.header_footer),
                description = getString(R.string.header_footer_desc),
                intent = createHeaderFooterIntent(this)
            ),
            MenuItem(
                title = getString(R.string.drag_swipe),
                description = getString(R.string.drag_swipe_desc),
                intent = createDragSwipeIntent(this)
            ),

            // 进阶示例
            MenuItem(
                title = getString(R.string.advanced_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.multi_type),
                description = getString(R.string.multi_type_desc),
                intent = createMultiTypeIntent(this)
            ),
            MenuItem(
                title = getString(R.string.diff_util),
                description = getString(R.string.diff_util_desc),
                intent = createDiffUtilIntent(this)
            ),
            MenuItem(
                title = getString(R.string.paging_simulation),
                description = getString(R.string.paging_simulation_desc),
                intent = createPagingSimulationIntent(this)
            ),

            // 滑动冲突示例
            MenuItem(
                title = getString(R.string.conflict_title),
                isHeader = true
            ),
            MenuItem(
                title = getString(R.string.viewpager2_nested),
                description = getString(R.string.viewpager2_nested_desc),
                intent = createViewPager2NestedIntent(this)
            ),
            MenuItem(
                title = getString(R.string.rv_nested),
                description = getString(R.string.rv_nested_desc),
                intent = createRecyclerViewNestedIntent(this)
            ),
            MenuItem(
                title = getString(R.string.nested_scroll),
                description = getString(R.string.nested_scroll_desc),
                intent = createNestedScrollIntent(this)
            ),
            MenuItem(
                title = getString(R.string.coordinator),
                description = getString(R.string.coordinator_desc),
                intent = createCoordinatorIntent(this)
            ),
            MenuItem(
                title = getString(R.string.custom_conflict),
                description = getString(R.string.custom_conflict_desc),
                intent = createCustomConflictIntent(this)
            )
        )
    }
}
