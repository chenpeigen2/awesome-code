package com.peter.motion.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.peter.motion.demo.databinding.ActivityMainBinding
import com.peter.motion.demo.fragment.BasicFragment
import com.peter.motion.demo.fragment.KeyframeFragment
import com.peter.motion.demo.fragment.TransitionFragment

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
                R.id.nav_keyframe -> {
                    switchFragment(KeyframeFragment::class.java)
                    true
                }
                R.id.nav_transition -> {
                    switchFragment(TransitionFragment::class.java)
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
                KeyframeFragment::class.java -> KeyframeFragment()
                TransitionFragment::class.java -> TransitionFragment()
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
