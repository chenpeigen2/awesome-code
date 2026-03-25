package com.peter.listview.demo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.peter.listview.demo.R
import com.peter.listview.demo.adapter.GroupListAdapter
import com.peter.listview.demo.databinding.FragmentAdvancedBinding
import com.peter.listview.demo.model.GroupedItem
import com.peter.listview.demo.model.SimpleItem
import com.peter.listview.demo.viewmodel.ListViewDemoViewModel

/**
 * Tab 4: 进阶功能演示
 *
 * 展示内容：
 * 1. 下拉刷新 - SwipeRefreshLayout
 * 2. 上拉加载 - OnScrollListener
 * 3. Header/Footer - addHeaderView() / addFooterView()
 * 4. 分组列表 - SectionIndexer
 */
class AdvancedFragment : Fragment() {

    private var _binding: FragmentAdvancedBinding? = null
    private val viewModel: ListViewDemoViewModel by activityViewModels()
    private lateinit var groupListAdapter: GroupListAdapter

    // 加载状态
    private var isLoading = false
    private var hasMoreData = true
    private var currentPage = 1

    // Header View
    private var headerView: View? = null

    // Footer View
    private var footerView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdvancedBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSwipeRefresh()
        setupListView()
        setupHeaderFooter()
        observeData()
    }

    private fun setupSwipeRefresh() {
        _binding?.swipeRefresh?.setOnRefreshListener {
            // 下拉刷新
            onRefresh()
        }

        // 设置刷新指示器颜色
        _binding?.swipeRefresh?.setColorSchemeResources(
            R.color.primary,
            R.color.accent
        )
    }

    private fun setupListView() {
        // 初始化分组列表 Adapter
        groupListAdapter = GroupListAdapter(requireContext(), emptyList())
        _binding?.listView?.adapter = groupListAdapter

        // 设置空视图
        _binding?.listView?.emptyView = _binding?.emptyView?.root

        // 设置滚动监听 - 加载更多
        _binding?.listView?.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                // 滚动停止时检查是否需要加载更多
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    checkLoadMore()
                }
            }

            override fun onScroll(
                view: AbsListView?,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                // 滚动过程中也检查，提高响应速度
            }
        })

        // 点击事件
        _binding?.listView?.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val item = groupListAdapter.getItem(position) as? GroupedItem<*>
            item?.let {
                Toast.makeText(context, "点击: ${it.data}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupHeaderFooter() {
        // 添加 Header View
        headerView = layoutInflater.inflate(R.layout.item_group_header, _binding?.listView, false)
        val headerTitle = headerView?.findViewById<TextView>(R.id.header_text)
        headerTitle?.text = "分组列表示例"
        _binding?.listView?.addHeaderView(headerView)

        // 添加 Footer View（加载更多）
        footerView = layoutInflater.inflate(R.layout.loading_footer, _binding?.listView, false)
        _binding?.listView?.addFooterView(footerView)
        footerView?.visibility = View.GONE
    }

    private fun onRefresh() {
        // 重置状态
        currentPage = 1
        hasMoreData = true
        isLoading = false

        // 模拟刷新延迟
        _binding?.swipeRefresh?.isRefreshing = true
        _binding?.listView?.postDelayed({
            _binding?.swipeRefresh?.isRefreshing = false
            // 重新加载数据
            viewModel.loadGroupedItems()
            Toast.makeText(context, "刷新完成", Toast.LENGTH_SHORT).show()
        }, 1500)
    }

    private fun checkLoadMore() {
        if (isLoading || !hasMoreData) {
            return
        }

        val listView = _binding?.listView ?: return
        val lastVisiblePosition = listView.lastVisiblePosition
        val totalCount = groupListAdapter.count

        // 滚动到倒数第 3 项时开始加载
        if (lastVisiblePosition >= totalCount - 3) {
            loadMore()
        }
    }

    private fun loadMore() {
        isLoading = true
        showLoadingFooter()

        // 模拟加载延迟
        _binding?.listView?.postDelayed({
            currentPage++
            viewModel.loadMoreGroupedItems(currentPage)
            isLoading = false
            hideLoadingFooter()
        }, 1000)
    }

    private fun showLoadingFooter() {
        footerView?.visibility = View.VISIBLE
    }

    private fun hideLoadingFooter() {
        footerView?.visibility = View.GONE
    }

    private fun observeData() {
        // 观察分组数据
        viewModel.groupedItems.observe(viewLifecycleOwner) { items ->
            items?.let {
                groupListAdapter.updateItems(it)
                if (it.isEmpty()) {
                    hasMoreData = false
                }
            }
        }

        // 初始加载
        viewModel.loadGroupedItems()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
