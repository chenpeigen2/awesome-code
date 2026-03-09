package com.peter.notification.demo.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.peter.notification.demo.MainActivity
import com.peter.notification.demo.R
import com.peter.notification.demo.databinding.FragmentPermissionBinding

/**
 * 权限管理 Fragment
 */
class PermissionFragment : Fragment() {

    private var _binding: FragmentPermissionBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = PermissionFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

        binding.btnCheckPermission.setOnClickListener {
            updatePermissionStatus()
            (requireActivity() as MainActivity).showSnackbar("权限状态已更新")
        }
    }

    private fun updatePermissionStatus() {
        val context = requireContext()

        // 更新设备信息
        binding.tvAndroidVersion.text = Build.VERSION.RELEASE
        binding.tvApiLevel.text = Build.VERSION.SDK_INT.toString()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            // 更新权限状态卡片
            if (isGranted) {
                binding.cardPermission.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.secondary_container)
                )
                binding.ivPermissionIcon.setImageResource(android.R.drawable.ic_dialog_info)
                binding.ivPermissionIcon.setColorFilter(
                    ContextCompat.getColor(context, R.color.permission_granted)
                )
                binding.tvPermissionStatus.text = "通知权限：已授权"
                binding.tvPermissionDesc.visibility = View.GONE

                binding.btnRequestPermission.visibility = View.GONE
                binding.btnOpenSettings.visibility = View.GONE

                binding.tvPermissionState.text = "已授权"
                binding.tvPermissionState.setTextColor(
                    ContextCompat.getColor(context, R.color.permission_granted)
                )
                
                // 设置绿色阴影
                binding.viewPermissionShadow.setBackgroundResource(R.drawable.bg_card_shadow_green)
            } else {
                binding.cardPermission.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.tertiary_container)
                )
                binding.ivPermissionIcon.setImageResource(android.R.drawable.ic_dialog_alert)
                binding.ivPermissionIcon.setColorFilter(
                    ContextCompat.getColor(context, R.color.permission_denied)
                )
                binding.tvPermissionStatus.text = "通知权限：未授权"
                binding.tvPermissionDesc.visibility = View.VISIBLE
                binding.tvPermissionDesc.text = "需要通知权限才能发送通知"

                val shouldShowRationale = shouldShowRequestPermissionRationale(
                    Manifest.permission.POST_NOTIFICATIONS
                )

                if (shouldShowRationale) {
                    binding.btnRequestPermission.visibility = View.VISIBLE
                    binding.btnOpenSettings.visibility = View.GONE
                } else {
                    binding.btnRequestPermission.visibility = View.GONE
                    binding.btnOpenSettings.visibility = View.VISIBLE
                }

                binding.tvPermissionState.text = "未授权"
                binding.tvPermissionState.setTextColor(
                    ContextCompat.getColor(context, R.color.permission_denied)
                )
                
                // 设置红色阴影
                binding.viewPermissionShadow.setBackgroundResource(R.drawable.bg_card_shadow_progress)
            }
        } else {
            // Android 13 以下不需要通知权限
            binding.cardPermission.setCardBackgroundColor(
                ContextCompat.getColor(context, R.color.secondary_container)
            )
            binding.ivPermissionIcon.setImageResource(android.R.drawable.ic_dialog_info)
            binding.ivPermissionIcon.setColorFilter(
                ContextCompat.getColor(context, R.color.permission_granted)
            )
            binding.tvPermissionStatus.text = "通知权限：无需请求"
            binding.tvPermissionDesc.visibility = View.VISIBLE
            binding.tvPermissionDesc.text = "Android 13 以下版本默认拥有通知权限"

            binding.btnRequestPermission.visibility = View.GONE
            binding.btnOpenSettings.visibility = View.GONE

            binding.tvPermissionState.text = "无需请求"
            binding.tvPermissionState.setTextColor(
                ContextCompat.getColor(context, R.color.permission_granted)
            )
            
            // 设置绿色阴影
            binding.viewPermissionShadow.setBackgroundResource(R.drawable.bg_card_shadow_green)
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
