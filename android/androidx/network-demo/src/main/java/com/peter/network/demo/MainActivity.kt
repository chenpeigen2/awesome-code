package com.peter.network.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.peter.network.demo.databinding.ActivityMainBinding
import com.peter.network.demo.fragment.BasicFragment
import com.peter.network.demo.fragment.InterceptorFragment
import com.peter.network.demo.fragment.MediaFragment
import com.peter.network.demo.fragment.PracticalFragment
import com.peter.network.demo.fragment.AdvancedFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        if (savedInstanceState == null) {
            binding.bottomNav.selectedItemId = R.id.nav_basic
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_basic -> {
                    switchFragment(BasicFragment::class.java)
                    true
                }

                R.id.nav_interceptor -> {
                    switchFragment(InterceptorFragment::class.java)
                    true
                }

                R.id.nav_media -> {
                    switchFragment(MediaFragment::class.java)
                    true
                }

                R.id.nav_practical -> {
                    switchFragment(PracticalFragment::class.java)
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

    private fun switchFragment(fragmentClass: Class<out Fragment>) {
        val transaction = supportFragmentManager.beginTransaction()
        currentFragment?.let(transaction::hide)

        val tag = fragmentClass.simpleName
        var fragment = supportFragmentManager.findFragmentByTag(tag)
        if (fragment == null) {
            fragment = when (fragmentClass) {
                BasicFragment::class.java -> BasicFragment()
                InterceptorFragment::class.java -> InterceptorFragment()
                MediaFragment::class.java -> MediaFragment()
                PracticalFragment::class.java -> PracticalFragment()
                AdvancedFragment::class.java -> AdvancedFragment()
                else -> error("Unknown fragment: $fragmentClass")
            }
            transaction.add(R.id.fragment_container, fragment, tag)
        } else {
            transaction.show(fragment)
        }

        transaction.commitAllowingStateLoss()
        currentFragment = fragment
    }
}
