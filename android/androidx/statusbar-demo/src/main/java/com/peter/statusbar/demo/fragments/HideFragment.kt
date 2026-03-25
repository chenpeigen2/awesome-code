package com.peter.statusbar.demo.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.statusbar.demo.FullscreenActivity
import com.peter.statusbar.demo.R
import com.peter.statusbar.demo.StatusBarAdapter
import com.peter.statusbar.demo.StatusBarHelper
import com.peter.statusbar.demo.StatusBarOption
import com.peter.statusbar.demo.databinding.FragmentHideBinding

/**
 * 隐藏状态栏 Fragment
 */
class HideFragment : Fragment() {

    private var _binding: FragmentHideBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = HideFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val items = createHideOptions()
        val adapter = StatusBarAdapter(items) { option ->
            applyHideOption(option)
        }
        binding.recyclerView.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun createHideOptions(): List<StatusBarOption> {
        val context = requireContext()
        return listOf(
            StatusBarOption(
                id = "hide_low_profile",
                title = getString(R.string.hide_low_profile),
                description = getString(R.string.hide_low_profile_desc),
                iconRes = R.drawable.ic_hide,
                iconTint = ContextCompat.getColor(context, R.color.tab_hide)
            ),
            StatusBarOption(
                id = "hide_temporary",
                title = getString(R.string.hide_temporary),
                description = getString(R.string.hide_temporary_desc),
                iconRes = R.drawable.ic_hide,
                iconTint = ContextCompat.getColor(context, R.color.tab_hide)
            ),
            StatusBarOption(
                id = "hide_permanent",
                title = getString(R.string.hide_permanent),
                description = getString(R.string.hide_permanent_desc),
                iconRes = R.drawable.ic_hide,
                iconTint = ContextCompat.getColor(context, R.color.tab_hide)
            ),
            StatusBarOption(
                id = "hide_fullscreen",
                title = getString(R.string.hide_fullscreen),
                description = getString(R.string.hide_fullscreen_desc),
                iconRes = R.drawable.ic_hide,
                iconTint = ContextCompat.getColor(context, R.color.tab_hide)
            ),
            StatusBarOption(
                id = "show_statusbar",
                title = getString(R.string.hide_show),
                description = "显示之前隐藏的状态栏",
                iconRes = R.drawable.ic_hide,
                iconTint = ContextCompat.getColor(context, R.color.tab_hide)
            ),
            StatusBarOption(
                id = "hide_reset",
                title = getString(R.string.action_reset),
                description = "恢复默认状态栏显示",
                iconRes = R.drawable.ic_hide,
                iconTint = ContextCompat.getColor(context, R.color.gray_500)
            )
        )
    }

    private fun applyHideOption(option: StatusBarOption) {
        when (option.id) {
            "hide_low_profile" -> {
                StatusBarHelper.setLowProfileMode(requireActivity(), true)
                Toast.makeText(requireContext(), "低调模式已启用", Toast.LENGTH_SHORT).show()
            }
            "hide_temporary" -> {
                StatusBarHelper.setImmersiveSticky(requireActivity())
                Toast.makeText(requireContext(), "滑动边缘恢复状态栏", Toast.LENGTH_SHORT).show()
            }
            "hide_permanent" -> {
                StatusBarHelper.hideStatusBar(requireActivity())
                Toast.makeText(requireContext(), "状态栏已隐藏，点击显示恢复", Toast.LENGTH_SHORT).show()
            }
            "hide_fullscreen" -> {
                // 启动全屏 Activity
                val intent = Intent(requireContext(), FullscreenActivity::class.java)
                startActivity(intent)
            }
            "show_statusbar" -> {
                StatusBarHelper.showStatusBar(requireActivity())
                StatusBarHelper.setLowProfileMode(requireActivity(), false)
                StatusBarHelper.resetStatusBar(requireActivity())
                Toast.makeText(requireContext(), "状态栏已显示", Toast.LENGTH_SHORT).show()
            }
            "hide_reset" -> {
                StatusBarHelper.resetStatusBar(requireActivity())
                StatusBarHelper.showStatusBar(requireActivity())
                StatusBarHelper.setLowProfileMode(requireActivity(), false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
