package com.peter.ipc.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.peter.ipc.demo.databinding.ActivityMainBinding
import com.peter.ipc.demo.fileshare.FileShareFragment
import com.peter.ipc.demo.localsocket.LocalSocketFragment
import com.peter.ipc.demo.sharedmemory.SharedMemoryFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        if (savedInstanceState == null) {
            binding.bottomNav.selectedItemId = R.id.nav_shared_memory
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_shared_memory -> {
                    switchFragment(SharedMemoryFragment::class.java)
                    true
                }
                R.id.nav_local_socket -> {
                    switchFragment(LocalSocketFragment::class.java)
                    true
                }
                R.id.nav_file_share -> {
                    switchFragment(FileShareFragment::class.java)
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
                SharedMemoryFragment::class.java -> SharedMemoryFragment()
                LocalSocketFragment::class.java -> LocalSocketFragment()
                FileShareFragment::class.java -> FileShareFragment()
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
