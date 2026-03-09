package com.peter.notification.demo.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.peter.notification.demo.MainActivity
import com.peter.notification.demo.R
import com.peter.notification.demo.databinding.FragmentPermissionBinding

/**
 * 权限管理 Fragment
 */
class PermissionFragment : Fragment(R.layout.fragment_permission) {

    private var _binding: FragmentPermissionBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPermissionBinding.bind(view)

        setupViews()
        updatePermissionStatus()
    }

    override fun onResume() {
        super.onResume()
        updatePermissionStatus()
    }

    private fun setupViews() {
        binding.btnRequestPermission.setOnClickListener {
            (requireActivity() as MainActivity).requestNotificationPermission()
        }

        binding.btnOpenSettings.setOnClickListener {
            openAppSettings()
        }

        binding.btnCheckStatus.setOnClickListener {
            updatePermissionStatus()
            (requireActivity() as MainActivity).showSnackbar("权限状态已更新")
        }
    }

    private fun updatePermissionStatus() {
        val context = requireContext()

        // 更新设备信息
        val deviceInfo = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\n" +
                "设备: ${Build.MANUFACTURER} ${Build.MODEL}"
        binding.tvDeviceInfo.text = deviceInfo

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            // 更新权限状态卡片
            if (isGranted) {
                binding.ivStatus.setImageResource(android.R.drawable.presence_online)
                binding.ivStatus.setColorFilter(
                    ContextCompat.getColor(context, android.R.color.holo_green_dark)
                )
                binding.tvStatus.text = "已授权"
                binding.tvStatus.setTextColor(
                    ContextCompat.getColor(context, android.R.color.holo_green_dark)
                )
                binding.tvDescription.text = "应用已获得通知权限，可以正常发送通知。"

                binding.btnRequestPermission.visibility = View.GONE
                binding.btnOpenSettings.visibility = View.GONE
            } else {
                binding.ivStatus.setImageResource(android.R.drawable.presence_busy)
                binding.ivStatus.setColorFilter(
                    ContextCompat.getColor(context, android.R.color.holo_red_dark)
                )
                binding.tvStatus.text = "未授权"
                binding.tvStatus.setTextColor(
                    ContextCompat.getColor(context, android.R.color.holo_red_dark)
                )
                binding.tvDescription.text = "应用未获得通知权限，无法发送通知。"

                // 检查是否可以请求权限
                val shouldShowRationale = shouldShowRequestPermissionRationale(
                    Manifest.permission.POST_NOTIFICATIONS
                )

                if (shouldShowRationale) {
                    binding.btnRequestPermission.visibility = View.VISIBLE
                    binding.btnOpenSettings.visibility = View.GONE
                    binding.tvRationale.visibility = View.VISIBLE
                    binding.tvRationale.text = "通知权限用于发送各种类型的通知演示。请授权以体验完整功能。"
                } else {
                    // 用户选择了"不再询问"
                    binding.btnRequestPermission.visibility = View.GONE
                    binding.btnOpenSettings.visibility = View.VISIBLE
                    binding.tvRationale.visibility = View.VISIBLE
                    binding.tvRationale.text = "您已禁止通知权限请求，请前往系统设置手动开启。"
                }
            }
        } else {
            // Android 13 以下不需要通知权限
            binding.ivStatus.setImageResource(android.R.drawable.presence_online)
            binding.ivStatus.setColorFilter(
                ContextCompat.getColor(context, android.R.color.holo_green_dark)
            )
            binding.tvStatus.text = "无需请求"
            binding.tvStatus.setTextColor(
                ContextCompat.getColor(context, android.R.color.holo_green_dark)
            )
            binding.tvDescription.text = "Android 13 以下版本默认拥有通知权限。"

            binding.btnRequestPermission.visibility = View.GONE
            binding.btnOpenSettings.visibility = View.GONE
            binding.tvRationale.visibility = View.GONE
        }
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", requireContext().packageName, null)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
