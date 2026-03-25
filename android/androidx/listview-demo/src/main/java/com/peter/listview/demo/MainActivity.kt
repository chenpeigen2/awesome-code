package com.peter.listview.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.peter.listview.demo.databinding.ActivityMainBinding
import com.peter.listview.demo.fragment.ArrayAdapterFragment
import com.peter.listview.demo.fragment.BaseAdapterFragment
import com.peter.listview.demo.fragment.CursorAdapterFragment
import com.peter.listview.demo.fragment.AdvancedFragment

/**
 * ListView Demo 主 Activity
 *
 * 使用 BottomNavigationView + Fragment 实现四个 Tab 切换
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // 当前显示的 Fragment
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        // 默认显示第一个 Tab
        if (savedInstanceState == null) {
            switchFragment(ArrayAdapterFragment::class.java)
            binding.bottomNav.selectedItemId = R.id.nav_array_adapter
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_array_adapter -> {
                    switchFragment(ArrayAdapterFragment::class.java)
                    true
                }
                R.id.nav_base_adapter -> {
                    switchFragment(BaseAdapterFragment::class.java)
                    true
                }
                R.id.nav_cursor_adapter -> {
                    switchFragment(CursorAdapterFragment::class.java)
                    true
                }
                R.id.nav_advanced -> {
                    switchFragment(AdvancedFragment::class.java)
                    true
                }
                else -> false
            }
        }
    }

    /**
     * 切换 Fragment
     * 每次创建新的 Fragment 实例，避免状态问题
     */
    private fun switchFragment(fragmentClass: Class<out Fragment>) {
        val transaction = supportFragmentManager.beginTransaction()

        // 隐藏当前 Fragment
        currentFragment?.let {
            transaction.hide(it)
        }

        // 查找是否已存在该 Fragment
        val tag = fragmentClass.simpleName
        var fragment = supportFragmentManager.findFragmentByTag(tag)

        if (fragment == null) {
            // 创建新 Fragment
            fragment = when (fragmentClass) {
                ArrayAdapterFragment::class.java -> ArrayAdapterFragment()
                BaseAdapterFragment::class.java -> BaseAdapterFragment()
                CursorAdapterFragment::class.java -> CursorAdapterFragment()
                AdvancedFragment::class.java -> AdvancedFragment()
                else -> throw IllegalArgumentException("Unknown fragment class: $fragmentClass")
            }
            transaction.add(R.id.fragment_container, fragment, tag)
        } else {
            // Fragment 已存在，直接显示
            transaction.show(fragment)
        }

        transaction.commitAllowingStateLoss()
        currentFragment = fragment
    }
}
