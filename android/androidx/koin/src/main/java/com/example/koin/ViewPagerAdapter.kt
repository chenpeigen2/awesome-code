package com.example.koin

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.koin.fragments.AdvancedFragment
import com.example.koin.fragments.DefinitionFragment
import com.example.koin.fragments.ScopeFragment
import com.example.koin.fragments.TestFragment
import com.example.koin.fragments.ViewModelFragment

class ViewPagerAdapter(
    activity: AppCompatActivity
) : FragmentStateAdapter(activity) {

    private val fragments = listOf(
        DefinitionFragment.newInstance(),
        ScopeFragment.newInstance(),
        AdvancedFragment.newInstance(),
        ViewModelFragment.newInstance(),
        TestFragment.newInstance()
    )

    private val titles = listOf(
        "定义", "作用域", "高级功能", "ViewModel", "测试"
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

    fun getTitle(position: Int): String = titles[position]
}
