package com.peter.camerax.demo

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.peter.camerax.demo.fragments.AnalysisFragment
import com.peter.camerax.demo.fragments.BasicFragment
import com.peter.camerax.demo.fragments.CaptureFragment
import com.peter.camerax.demo.fragments.VideoFragment

class ViewPagerAdapter(
    private val activity: AppCompatActivity
) : FragmentStateAdapter(activity) {

    private val titles = listOf(
        "Basic",
        "Capture",
        "Video",
        "Analysis"
    )

    override fun getItemCount(): Int = titles.size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BasicFragment.newInstance()
            1 -> CaptureFragment.newInstance()
            2 -> VideoFragment.newInstance()
            3 -> AnalysisFragment.newInstance()
            else -> BasicFragment.newInstance()
        }
    }

    fun getTitle(position: Int): String = titles[position]
}
