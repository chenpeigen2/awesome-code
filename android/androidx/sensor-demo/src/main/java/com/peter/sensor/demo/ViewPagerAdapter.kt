package com.peter.sensor.demo

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.peter.sensor.demo.fragments.DeviceFragment
import com.peter.sensor.demo.fragments.EnvironmentFragment
import com.peter.sensor.demo.fragments.MotionFragment
import com.peter.sensor.demo.fragments.PositionFragment
import com.peter.sensor.demo.fragments.ProximityFragment

class ViewPagerAdapter(
    private val activity: AppCompatActivity
) : FragmentStateAdapter(activity) {

    private val titles = listOf(
        "Motion",
        "Position",
        "Environment",
        "Proximity",
        "Device"
    )

    override fun getItemCount(): Int = titles.size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MotionFragment.newInstance()
            1 -> PositionFragment.newInstance()
            2 -> EnvironmentFragment.newInstance()
            3 -> ProximityFragment.newInstance()
            4 -> DeviceFragment.newInstance()
            else -> MotionFragment.newInstance()
        }
    }

    fun getTitle(position: Int): String = titles[position]
}
