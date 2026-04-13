package com.peter.security.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.peter.security.demo.databinding.ActivityMainBinding
import com.peter.security.demo.fragment.BiometricFragment
import com.peter.security.demo.fragment.CryptoFragment
import com.peter.security.demo.fragment.SplashFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // keepOnScreenCondition: 可在此控制 Splash 显示时长
        // splashScreen.setKeepOnScreenCondition { !dataReady }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        if (savedInstanceState == null) {
            binding.bottomNav.selectedItemId = R.id.nav_splash
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_splash -> {
                    switchFragment(SplashFragment::class.java)
                    true
                }
                R.id.nav_biometric -> {
                    switchFragment(BiometricFragment::class.java)
                    true
                }
                R.id.nav_crypto -> {
                    switchFragment(CryptoFragment::class.java)
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
                SplashFragment::class.java -> SplashFragment()
                BiometricFragment::class.java -> BiometricFragment()
                CryptoFragment::class.java -> CryptoFragment()
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
