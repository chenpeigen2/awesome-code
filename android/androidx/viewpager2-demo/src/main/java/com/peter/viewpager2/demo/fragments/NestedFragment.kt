package com.peter.viewpager2.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.peter.viewpager2.demo.R
import com.peter.viewpager2.demo.databinding.FragmentNestedBinding
import com.peter.viewpager2.demo.databinding.FragmentNestedPageBinding
import com.peter.viewpager2.demo.databinding.ItemNestedChildBinding

/**
 * ViewPager2 嵌套演示
 * 外层垂直滑动，内层水平滑动
 */
class NestedFragment : Fragment() {

    private var _binding: FragmentNestedBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = NestedFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNestedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    private fun setupViewPager() {
        val adapter = NestedPagerAdapter(requireActivity() as AppCompatActivity)
        binding.viewPager.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * 外层垂直 ViewPager Adapter
     */
    private inner class NestedPagerAdapter(
        activity: AppCompatActivity
    ) : FragmentStateAdapter(activity) {

        private val pageTitles = listOf(
            "第一层 - 上",
            "第一层 - 下"
        )

        override fun getItemCount(): Int = pageTitles.size

        override fun createFragment(position: Int): Fragment {
            return NestedPageFragment.newInstance(
                title = pageTitles[position],
                pageNumber = position + 1
            )
        }
    }
}

/**
 * 嵌套页面 Fragment
 * 包含一个水平 ViewPager
 */
class NestedPageFragment : Fragment() {

    private var _binding: FragmentNestedPageBinding? = null
    private val binding get() = _binding!!

    private var title: String? = null
    private var pageNumber: Int = 1

    companion object {
        private const val ARG_TITLE = "arg_title"
        private const val ARG_PAGE_NUMBER = "arg_page_number"

        fun newInstance(title: String, pageNumber: Int): NestedPageFragment {
            return NestedPageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putInt(ARG_PAGE_NUMBER, pageNumber)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_TITLE)
            pageNumber = it.getInt(ARG_PAGE_NUMBER, 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNestedPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTitle.text = "$title (上下滑动切换)"

        setupInnerViewPager()
    }

    private fun setupInnerViewPager() {
        val adapter = InnerPagerAdapter(pageNumber)
        binding.viewPager.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * 内层水平 ViewPager Adapter
     */
    private inner class InnerPagerAdapter(
        private val parentPageNumber: Int
    ) : RecyclerView.Adapter<InnerPagerAdapter.ViewHolder>() {

        private val pageColors = listOf(
            R.color.page_color_1,
            R.color.page_color_2,
            R.color.page_color_3,
            R.color.page_color_4
        )

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemNestedChildBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(parentPageNumber, position + 1)
        }

        override fun getItemCount(): Int = pageColors.size

        inner class ViewHolder(
            private val binding: ItemNestedChildBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(parentPage: Int, childPage: Int) {
                binding.root.setBackgroundColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        pageColors[childPage - 1]
                    )
                )
                binding.tvPageNumber.text = "$parentPage-$childPage"
            }
        }
    }
}
