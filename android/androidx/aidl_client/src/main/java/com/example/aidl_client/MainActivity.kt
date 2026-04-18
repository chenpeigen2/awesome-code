package com.example.aidl_client

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.aidl_client.databinding.ActivityMainBinding
import com.example.aidl_client.fragment.AsyncDownloadFragment
import com.example.aidl_client.fragment.BasicSyncFragment
import com.example.aidl_client.fragment.CallbackDownloadFragment
import com.example.aidl_client.fragment.CallbackRegisterFragment
import com.example.aidl_client.viewmodel.AidlViewModel
import androidx.activity.viewModels

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: AidlViewModel by viewModels()
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
        bindAidlService()

        if (savedInstanceState == null) {
            switchFragment(BasicSyncFragment::class.java)
            binding.bottomNav.selectedItemId = R.id.nav_sync
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_sync -> switchFragment(BasicSyncFragment::class.java)
                R.id.nav_async -> switchFragment(AsyncDownloadFragment::class.java)
                R.id.nav_callback -> switchFragment(CallbackDownloadFragment::class.java)
                R.id.nav_register -> switchFragment(CallbackRegisterFragment::class.java)
            }
            true
        }
    }

    private fun switchFragment(fragmentClass: Class<out Fragment>) {
        val transaction = supportFragmentManager.beginTransaction()
        currentFragment?.let { transaction.hide(it) }

        val tag = fragmentClass.simpleName
        var fragment = supportFragmentManager.findFragmentByTag(tag)

        if (fragment == null) {
            fragment = when (fragmentClass) {
                BasicSyncFragment::class.java -> BasicSyncFragment()
                AsyncDownloadFragment::class.java -> AsyncDownloadFragment()
                CallbackDownloadFragment::class.java -> CallbackDownloadFragment()
                CallbackRegisterFragment::class.java -> CallbackRegisterFragment()
                else -> throw IllegalArgumentException("Unknown fragment: $fragmentClass")
            }
            transaction.add(R.id.fragment_container, fragment, tag)
        } else {
            transaction.show(fragment)
        }

        transaction.commitAllowingStateLoss()
        currentFragment = fragment
    }

    private fun bindAidlService() {
        val connection = viewModel.createConnection()
        val result = bindService(viewModel.getServiceIntent(), connection, BIND_AUTO_CREATE)
        if (!result) {
            Toast.makeText(this, "绑定服务失败，请先启动服务端", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.serviceConnection?.let {
            try {
                unbindService(it)
            } catch (_: Exception) {}
        }
    }
}
