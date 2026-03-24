package com.peter.file.demo

import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.peter.file.demo.fragments.ExternalFragment
import com.peter.file.demo.fragments.InternalFragment
import com.peter.file.demo.fragments.PreferencesFragment
import com.peter.file.demo.fragments.ScopedFragment

class ViewPagerAdapter(
    private val activity: AppCompatActivity
) : FragmentStateAdapter(activity) {

    private val fragments = listOf(
        InternalFragment.newInstance(),
        ExternalFragment.newInstance(),
        PreferencesFragment.newInstance(),
        ScopedFragment.newInstance()
    )

    private val titles = listOf(
        "内部存储",
        "外部存储",
        "偏好设置",
        "分区存储"
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int) = fragments[position]

    fun getTitle(position: Int): String = titles[position]
}
