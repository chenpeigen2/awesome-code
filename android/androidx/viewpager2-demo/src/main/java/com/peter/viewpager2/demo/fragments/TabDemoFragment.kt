package com.peter.viewpager2.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.peter.viewpager2.demo.R
import com.peter.viewpager2.demo.databinding.FragmentTabDemoBinding

/**
 * TabLayout + ViewPager2 演示
 * 展示如何使用 TabLayoutMediator 绑定 Tab 和 ViewPager
 */
class TabDemoFragment : Fragment() {

    private var _binding: FragmentTabDemoBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = TabDemoFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTabDemoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    private fun setupViewPager() {
        val adapter = TabPagerAdapter(requireActivity() as AppCompatActivity)
        binding.viewPager.adapter = adapter

        // 使用 TabLayoutMediator 绑定 TabLayout 和 ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Tab ViewPager Adapter
     * 使用 FragmentStateAdapter
     */
    private inner class TabPagerAdapter(
        activity: AppCompatActivity
    ) : FragmentStateAdapter(activity) {

        private val tabTitles = listOf(
            getString(R.string.tab_home),
            getString(R.string.tab_discover),
            getString(R.string.tab_message),
            getString(R.string.tab_profile)
        )

        private val tabDescriptions = listOf(
            "首页展示了应用的主要内容\n通常是用户最常访问的页面",
            "发现页面用于推荐内容\n帮助用户探索新的功能",
            "消息页面显示通知和聊天\n方便用户查看最新消息",
            "我的页面包含个人设置\n用户可以管理自己的账户"
        )

        private val tabColors = listOf(
            R.color.page_color_1,
            R.color.page_color_2,
            R.color.page_color_3,
            R.color.page_color_4
        )

        override fun getItemCount(): Int = tabTitles.size

        override fun createFragment(position: Int): Fragment {
            return TabContentFragment.newInstance(
                title = tabTitles[position],
                description = tabDescriptions[position],
                backgroundColor = tabColors[position]
            )
        }

        fun getTabTitle(position: Int): String = tabTitles[position]
    }
}
