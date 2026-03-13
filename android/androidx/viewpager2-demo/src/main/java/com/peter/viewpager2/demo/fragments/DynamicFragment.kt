package com.peter.viewpager2.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.peter.viewpager2.demo.R
import com.peter.viewpager2.demo.databinding.FragmentDynamicBinding
import com.peter.viewpager2.demo.databinding.ItemDynamicPageBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * 动态页面演示
 * 展示如何动态添加和删除 ViewPager2 页面
 */
class DynamicFragment : Fragment() {

    private var _binding: FragmentDynamicBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DynamicPagerAdapter
    private val pages = mutableListOf<PageData>()

    private val pageColors = listOf(
        R.color.page_color_1,
        R.color.page_color_2,
        R.color.page_color_3,
        R.color.page_color_4,
        R.color.page_color_5,
        R.color.page_color_6
    )

    companion object {
        fun newInstance() = DynamicFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDynamicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化页面数据（默认3个页面）
        for (i in 1..3) {
            pages.add(createPageData(i))
        }

        setupViewPager()
        setupButtons()
        updatePageCount()
    }

    private fun createPageData(pageNumber: Int): PageData {
        return PageData(
            number = pageNumber,
            title = "动态页面 #$pageNumber",
            timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()),
            colorRes = pageColors[(pageNumber - 1) % pageColors.size]
        )
    }

    private fun setupViewPager() {
        adapter = DynamicPagerAdapter(pages)
        binding.viewPager.adapter = adapter
    }

    private fun setupButtons() {
        binding.btnAdd.setOnClickListener {
            addPage()
        }

        binding.btnRemove.setOnClickListener {
            removeCurrentPage()
        }
    }

    private fun addPage() {
        val newPageNumber = pages.size + 1
        val newPage = createPageData(newPageNumber)
        pages.add(newPage)
        adapter.notifyItemInserted(pages.size - 1)
        binding.viewPager.currentItem = pages.size - 1
        updatePageCount()
    }

    private fun removeCurrentPage() {
        if (pages.size <= 1) {
            // 至少保留一个页面
            return
        }

        val currentPosition = binding.viewPager.currentItem
        pages.removeAt(currentPosition)
        adapter.notifyItemRemoved(currentPosition)

        // 更新剩余页面的编号
        for (i in pages.indices) {
            pages[i] = pages[i].copy(number = i + 1)
        }
        adapter.notifyItemRangeChanged(0, pages.size)

        updatePageCount()
    }

    private fun updatePageCount() {
        binding.tvPageCount.text = getString(R.string.dynamic_page_count, pages.size)

        // 更新删除按钮状态
        binding.btnRemove.isEnabled = pages.size > 1
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * 页面数据类
     */
    data class PageData(
        val number: Int,
        val title: String,
        val timestamp: String,
        val colorRes: Int
    )

    /**
     * 动态 ViewPager Adapter
     */
    private inner class DynamicPagerAdapter(
        private val pages: MutableList<PageData>
    ) : RecyclerView.Adapter<DynamicPagerAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemDynamicPageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(pages[position])
        }

        override fun getItemCount(): Int = pages.size

        inner class ViewHolder(
            private val binding: ItemDynamicPageBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(page: PageData) {
                binding.root.setBackgroundColor(
                    ContextCompat.getColor(binding.root.context, page.colorRes)
                )
                binding.tvPageNumber.text = "${page.number}"
                binding.tvTitle.text = page.title
                binding.tvTimestamp.text = "创建时间: ${page.timestamp}"
            }
        }
    }
}
