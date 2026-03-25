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

    // 缓存 Fragment 实例
    private var arrayAdapterFragment: ArrayAdapterFragment? = null
    private var baseAdapterFragment: BaseAdapterFragment? = null
    private var cursorAdapterFragment: CursorAdapterFragment? = null
    private var advancedFragment: AdvancedFragment? = null

    // 当前显示的 Fragment
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        // 默认显示第一个 Tab
        if (savedInstanceState == null) {
            showFragment(ArrayAdapterFragment::class.java)
            binding.bottomNav.selectedItemId = R.id.nav_array_adapter
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_array_adapter -> {
                    showFragment(ArrayAdapterFragment::class.java)
                    true
                }
                R.id.nav_base_adapter -> {
                    showFragment(BaseAdapterFragment::class.java)
                    true
                }
                R.id.nav_cursor_adapter -> {
                    showFragment(CursorAdapterFragment::class.java)
                    true
                }
                R.id.nav_advanced -> {
                    showFragment(AdvancedFragment::class.java)
                    true
                }
                else -> false
            }
        }
    }

    /**
     * 显示指定的 Fragment
     * 使用 show/hide 方式管理 Fragment，避免重复创建
     */
    private fun showFragment(fragmentClass: Class<out Fragment>) {
        val transaction = supportFragmentManager.beginTransaction()

        // 隐藏当前 Fragment
        currentFragment?.let {
            transaction.hide(it)
        }

        // 获取或创建目标 Fragment
        val fragment = getOrCreateFragment(fragmentClass)

        // 显示 Fragment
        if (fragment.isAdded) {
            transaction.show(fragment)
        } else {
            transaction.add(R.id.fragment_container, fragment)
        }

        transaction.commitAllowingStateLoss()
        currentFragment = fragment
    }

    /**
     * 获取或创建 Fragment 实例
     */
    private fun getOrCreateFragment(fragmentClass: Class<out Fragment>): Fragment {
        return when (fragmentClass) {
            ArrayAdapterFragment::class.java -> {
                arrayAdapterFragment ?: ArrayAdapterFragment().also {
                    arrayAdapterFragment = it
                }
            }
            BaseAdapterFragment::class.java -> {
                baseAdapterFragment ?: BaseAdapterFragment().also {
                    baseAdapterFragment = it
                }
            }
            CursorAdapterFragment::class.java -> {
                cursorAdapterFragment ?: CursorAdapterFragment().also {
                    cursorAdapterFragment = it
                }
            }
            AdvancedFragment::class.java -> {
                advancedFragment ?: AdvancedFragment().also {
                    advancedFragment = it
                }
            }
            else -> throw IllegalArgumentException("Unknown fragment class: $fragmentClass")
        }
    }
}
