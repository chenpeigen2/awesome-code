package com.peter.viewpager2.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.peter.viewpager2.demo.R
import com.peter.viewpager2.demo.databinding.FragmentSimpleBinding
import com.peter.viewpager2.demo.databinding.ItemSimplePageBinding

/**
 * 基础用法 Fragment
 * 最简单的 ViewPager2 演示：左右滑动切换页面
 */
class SimpleFragment : Fragment() {

    private var _binding: FragmentSimpleBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = SimpleFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSimpleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupButtons()
    }

    private fun setupViewPager() {
        val adapter = SimplePagerAdapter()
        binding.viewPager.adapter = adapter

        // 设置预加载页面数量（默认是1，这里保持默认）
        // binding.viewPager.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT

        // 监听页面变化
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updatePageIndicator(position)
            }
        })

        // 初始化指示器
        updatePageIndicator(0)
    }

    private fun setupButtons() {
        binding.btnPrevious.setOnClickListener {
            val current = binding.viewPager.currentItem
            if (current > 0) {
                binding.viewPager.currentItem = current - 1
            }
        }

        binding.btnNext.setOnClickListener {
            val current = binding.viewPager.currentItem
            val total = binding.viewPager.adapter?.itemCount ?: 0
            if (current < total - 1) {
                binding.viewPager.currentItem = current + 1
            }
        }
    }

    private fun updatePageIndicator(position: Int) {
        val total = binding.viewPager.adapter?.itemCount ?: 0
        binding.tvPageIndicator.text = getString(R.string.page_indicator, position + 1, total)

        // 更新按钮状态
        binding.btnPrevious.isEnabled = position > 0
        binding.btnNext.isEnabled = position < total - 1
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * 简单的 ViewPager Adapter
     * 使用 RecyclerView.Adapter 实现
     */
    private inner class SimplePagerAdapter : RecyclerView.Adapter<SimplePagerAdapter.ViewHolder>() {

        private val pageColors = listOf(
            R.color.page_color_1,
            R.color.page_color_2,
            R.color.page_color_3,
            R.color.page_color_4,
            R.color.page_color_5,
            R.color.page_color_6
        )

        private val pageTitles = listOf(
            "基础用法",
            "滑动切换",
            "预加载机制",
            "页面监听",
            "按钮控制",
            "完成学习"
        )

        private val pageDescriptions = listOf(
            "ViewPager2 最基础的用法",
            "左右滑动切换页面",
            "了解 offscreenPageLimit",
            "OnPageChangeCallback 使用",
            "通过按钮控制页面切换",
            "恭喜你掌握了基础用法！"
        )

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemSimplePageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(position)
        }

        override fun getItemCount(): Int = pageColors.size

        inner class ViewHolder(
            private val binding: ItemSimplePageBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(position: Int) {
                binding.root.setBackgroundResource(pageColors[position])
                binding.tvPageNumber.text = "${position + 1}"
                binding.tvPageTitle.text = "${pageTitles[position]}\n${pageDescriptions[position]}"
            }
        }
    }
}
