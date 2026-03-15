package com.peter.os.demo.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.os.UserManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.peter.os.demo.databinding.FragmentUserBinding

/**
 * UserManager 用户管理示例
 */
class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var userManager: UserManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        userManager = requireContext().getSystemService(android.content.Context.USER_SERVICE) as UserManager
        setupViews()
        loadUserInfo()
    }

    private fun setupViews() {
        binding.btnRefresh.setOnClickListener {
            loadUserInfo()
        }
    }

    private fun loadUserInfo() {
        val sb = StringBuilder()
        
        // 用户基本信息
        sb.appendLine("=== 用户信息 ===")
        
        try {
            sb.appendLine("系统用户: ${if (userManager.isSystemUser) "是" else "否"}")
        } catch (e: Exception) {
            sb.appendLine("系统用户: (无法获取)")
        }
        
        try {
            val isAdmin = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                userManager.isAdminUser
            } else {
                false
            }
            sb.appendLine("管理员用户: ${if (isAdmin) "是" else "否"}")
        } catch (e: Exception) {
            sb.appendLine("管理员用户: (无法获取)")
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                sb.appendLine("演示用户: ${if (userManager.isDemoUser) "是" else "否"}")
            } catch (e: Exception) {
                sb.appendLine("演示用户: (无法获取)")
            }
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                sb.appendLine("用户已解锁: ${if (userManager.isUserUnlocked) "是" else "否"}")
            } catch (e: Exception) {
                sb.appendLine("用户已解锁: (无法获取)")
            }
            
            try {
                sb.appendLine("用户配置文件: ${if (userManager.isManagedProfile) "受管理" else "主配置"}")
            } catch (e: Exception) {
                sb.appendLine("用户配置文件: (无法获取)")
            }
        }
        sb.appendLine()
        
        // 用户限制 - 使用字符串常量
        sb.appendLine("=== 用户限制 ===")
        try {
            val restrictions = userManager.userRestrictions
            
            val restrictionList = listOf(
                Pair("禁止安装应用", UserManager.DISALLOW_INSTALL_APPS),
                Pair("禁止卸载应用", UserManager.DISALLOW_UNINSTALL_APPS),
                Pair("禁止配置WiFi", UserManager.DISALLOW_CONFIG_WIFI),
                Pair("禁止配置蓝牙", UserManager.DISALLOW_CONFIG_BLUETOOTH),
                Pair("禁止相机", "no_camera"),
                Pair("禁止麦克风", "no_record_audio"),
                Pair("禁止截图", "no_screen_capture"),
                Pair("禁止USB文件传输", UserManager.DISALLOW_USB_FILE_TRANSFER),
                Pair("禁止定位", UserManager.DISALLOW_SHARE_LOCATION),
                Pair("禁止安装未知来源", UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES),
                Pair("禁止调试功能", UserManager.DISALLOW_DEBUGGING_FEATURES),
                Pair("禁止VPN配置", UserManager.DISALLOW_CONFIG_VPN),
                Pair("禁止修改账户", UserManager.DISALLOW_MODIFY_ACCOUNTS),
                Pair("禁止工厂重置", UserManager.DISALLOW_FACTORY_RESET)
            )
            
            for ((name, key) in restrictionList) {
                val restricted = restrictions.getBoolean(key, false)
                if (restricted) {
                    sb.appendLine("✗ $name")
                }
            }
            
            val restrictedCount = restrictionList.count { restrictions.getBoolean(it.second, false) }
            if (restrictedCount == 0) {
                sb.appendLine("(无限制)")
            } else {
                sb.appendLine()
                sb.appendLine("共 $restrictedCount 项限制")
            }
        } catch (e: Exception) {
            sb.appendLine("(无法获取用户限制)")
        }
        sb.appendLine()
        
        // 用户属性
        sb.appendLine("=== 用户属性 ===")
        
        try {
            val serialNumber = userManager.getSerialNumberForUser(Process.myUserHandle())
            sb.appendLine("用户序列号: $serialNumber")
        } catch (e: Exception) {
            sb.appendLine("用户序列号: (需要 INTERACT_ACROSS_USERS 权限)")
        }
        
        try {
            @Suppress("MissingPermission")
            val userName = userManager.userName
            sb.appendLine("用户名: $userName")
        } catch (e: Exception) {
            sb.appendLine("用户名: (需要权限)")
        }
        
        try {
            val userHandle = Process.myUserHandle()
            sb.appendLine("UserHandle: $userHandle")
        } catch (e: Exception) {
            sb.appendLine("UserHandle: (无法获取)")
        }
        
        try {
            sb.appendLine("应用限制: ${if (userManager.hasUserRestriction(UserManager.DISALLOW_APPS_CONTROL)) "是" else "否"}")
        } catch (e: Exception) {
            sb.appendLine("应用限制: (无法获取)")
        }
        sb.appendLine()
        
        // 多用户支持
        sb.appendLine("=== 多用户支持 ===")
        try {
            sb.appendLine("支持多用户: ${if (UserManager.supportsMultipleUsers()) "是" else "否"}")
        } catch (e: Exception) {
            sb.appendLine("支持多用户: (无法获取)")
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                sb.appendLine("用户配置文件数: ${userManager.userProfiles?.size ?: 0}")
            } catch (e: Exception) {
                sb.appendLine("用户配置文件数: (无法获取)")
            }
        }
        
        binding.tvUserInfo.text = sb.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = UserFragment()
    }
}
