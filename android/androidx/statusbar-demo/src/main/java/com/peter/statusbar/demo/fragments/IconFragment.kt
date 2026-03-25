package com.peter.statusbar.demo.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.statusbar.demo.R
import com.peter.statusbar.demo.StatusBarAdapter
import com.peter.statusbar.demo.StatusBarHelper
import com.peter.statusbar.demo.StatusBarOption
import com.peter.statusbar.demo.databinding.FragmentIconBinding

/**
 * 状态栏图标设置 Fragment
 */
class IconFragment : Fragment() {

    private var _binding: FragmentIconBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = IconFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIconBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val items = createIconOptions()
        val adapter = StatusBarAdapter(items) { option ->
            applyIconOption(option)
        }
        binding.recyclerView.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun createIconOptions(): List<StatusBarOption> {
        val context = requireContext()
        return listOf(
            StatusBarOption(
                id = "icon_dark",
                title = getString(R.string.icon_dark),
                description = getString(R.string.icon_dark_desc),
                iconRes = R.drawable.ic_icon,
                iconTint = ContextCompat.getColor(context, R.color.tab_icon)
            ),
            StatusBarOption(
                id = "icon_light",
                title = getString(R.string.icon_light),
                description = getString(R.string.icon_light_desc),
                iconRes = R.drawable.ic_icon,
                iconTint = ContextCompat.getColor(context, R.color.tab_icon)
            ),
            StatusBarOption(
                id = "icon_auto_demo_dark",
                title = "演示: 浅色背景 + 深色图标",
                description = "自动适配示例 - 浅色背景自动使用深色图标",
                iconRes = R.drawable.ic_icon,
                iconTint = ContextCompat.getColor(context, R.color.tab_icon)
            ),
            StatusBarOption(
                id = "icon_auto_demo_light",
                title = "演示: 深色背景 + 浅色图标",
                description = "自动适配示例 - 深色背景自动使用浅色图标",
                iconRes = R.drawable.ic_icon,
                iconTint = ContextCompat.getColor(context, R.color.tab_icon)
            ),
            StatusBarOption(
                id = "icon_reset",
                title = getString(R.string.action_reset),
                description = "恢复默认图标设置",
                iconRes = R.drawable.ic_icon,
                iconTint = ContextCompat.getColor(context, R.color.gray_500)
            )
        )
    }

    private fun applyIconOption(option: StatusBarOption) {
        when (option.id) {
            "icon_dark" -> {
                // 深色图标 = 适用于浅色背景
                StatusBarHelper.setStatusBarLightIcon(requireActivity(), true)
                // 设置浅色背景以演示效果
                StatusBarHelper.setStatusBarColor(requireActivity(), Color.parseColor("#F5F5F5"))
            }
            "icon_light" -> {
                // 浅色图标 = 适用于深色背景
                StatusBarHelper.setStatusBarLightIcon(requireActivity(), false)
                // 设置深色背景以演示效果
                StatusBarHelper.setStatusBarColor(requireActivity(), Color.parseColor("#1A1A1A"))
            }
            "icon_auto_demo_dark" -> {
                StatusBarHelper.setStatusBarColor(requireActivity(), Color.WHITE)
                StatusBarHelper.setStatusBarLightIcon(requireActivity(), true)
            }
            "icon_auto_demo_light" -> {
                StatusBarHelper.setStatusBarColor(requireActivity(), Color.parseColor("#6750A4"))
                StatusBarHelper.setStatusBarLightIcon(requireActivity(), false)
            }
            "icon_reset" -> {
                StatusBarHelper.resetStatusBar(requireActivity())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
