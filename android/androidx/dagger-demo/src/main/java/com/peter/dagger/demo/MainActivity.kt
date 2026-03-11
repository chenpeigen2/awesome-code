package com.peter.dagger.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.peter.dagger.demo.databinding.ActivityMainBinding
import com.peter.dagger.demo.ui.fragment.AndroidFragment
import com.peter.dagger.demo.ui.fragment.BasicFragment
import com.peter.dagger.demo.ui.fragment.QualifierFragment
import com.peter.dagger.demo.ui.fragment.ScopeFragment
import com.peter.dagger.demo.ui.fragment.SubcomponentFragment

/**
 * Dagger2 Demo 主界面
 *
 * 使用 BottomNavigationView 切换不同的学习页面
 * 每个页面展示 Dagger2 的一个核心概念
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

        // 默认显示基础注入页面
        if (savedInstanceState == null) {
            switchFragment(BasicFragment.newInstance())
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_basic -> {
                    switchFragment(BasicFragment.newInstance())
                    true
                }
                R.id.nav_scope -> {
                    switchFragment(ScopeFragment.newInstance())
                    true
                }
                R.id.nav_qualifier -> {
                    switchFragment(QualifierFragment.newInstance())
                    true
                }
                R.id.nav_subcomponent -> {
                    switchFragment(SubcomponentFragment.newInstance())
                    true
                }
                R.id.nav_android -> {
                    switchFragment(AndroidFragment.newInstance())
                    true
                }
                else -> false
            }
        }
    }

    private fun switchFragment(fragment: Fragment) {
        if (currentFragment == fragment) return

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        currentFragment = fragment
    }
}
