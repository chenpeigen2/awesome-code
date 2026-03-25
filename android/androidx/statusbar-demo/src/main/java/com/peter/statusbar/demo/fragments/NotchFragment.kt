package com.peter.statusbar.demo.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.peter.statusbar.demo.R
import com.peter.statusbar.demo.StatusBarHelper
import com.peter.statusbar.demo.databinding.FragmentNotchBinding

/**
 * 刘海屏适配 Fragment
 */
class NotchFragment : Fragment() {

    private var _binding: FragmentNotchBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = NotchFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayNotchInfo()
        setupButtons()
    }

    private fun displayNotchInfo() {
        val context = requireContext()

        // 检查是否有刘海屏
        val hasNotch = StatusBarHelper.hasDisplayCutout(requireActivity())
        binding.tvHasNotch.text = getString(R.string.notch_has_notch, if (hasNotch) "是" else "否")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val cutoutInfo = StatusBarHelper.getDisplayCutoutInfo(requireActivity())
            if (cutoutInfo != null) {
                binding.tvSafeInsetTop.text = getString(R.string.notch_safe_inset_top, cutoutInfo.safeInsetTop)
                binding.tvSafeInsetBottom.text = getString(R.string.notch_safe_inset_bottom, cutoutInfo.safeInsetBottom)
                binding.tvSafeInsetLeft.text = getString(R.string.notch_safe_inset_left, cutoutInfo.safeInsetLeft)
                binding.tvSafeInsetRight.text = getString(R.string.notch_safe_inset_right, cutoutInfo.safeInsetRight)
            } else {
                binding.tvSafeInsetTop.text = getString(R.string.notch_safe_inset_top, 0)
                binding.tvSafeInsetBottom.text = getString(R.string.notch_safe_inset_bottom, 0)
                binding.tvSafeInsetLeft.text = getString(R.string.notch_safe_inset_left, 0)
                binding.tvSafeInsetRight.text = getString(R.string.notch_safe_inset_right, 0)
            }
        } else {
            binding.tvSafeInsetTop.text = getString(R.string.notch_safe_inset_top, 0)
            binding.tvSafeInsetBottom.text = getString(R.string.notch_safe_inset_bottom, 0)
            binding.tvSafeInsetLeft.text = getString(R.string.notch_safe_inset_left, 0)
            binding.tvSafeInsetRight.text = getString(R.string.notch_safe_inset_right, 0)
        }
    }

    private fun setupButtons() {
        // 短边模式
        binding.btnShortEdges.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                StatusBarHelper.setLayoutInDisplayCutoutMode(requireActivity(), true)
                StatusBarHelper.setImmersiveMode(requireActivity(), false)
                StatusBarHelper.setStatusBarTransparent(requireActivity())
                android.widget.Toast.makeText(requireContext(), "短边模式已启用", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                android.widget.Toast.makeText(requireContext(), "需要 Android 9.0 及以上版本", android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        // 重置
        binding.btnReset.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                StatusBarHelper.setLayoutInDisplayCutoutMode(requireActivity(), false)
            }
            StatusBarHelper.resetStatusBar(requireActivity())
            android.widget.Toast.makeText(requireContext(), getString(R.string.msg_reset), android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // 每次显示时更新刘海屏信息
        displayNotchInfo()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
