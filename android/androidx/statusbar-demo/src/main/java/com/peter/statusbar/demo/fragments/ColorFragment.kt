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
import com.peter.statusbar.demo.databinding.FragmentColorBinding

/**
 * 状态栏颜色设置 Fragment
 */
class ColorFragment : Fragment() {

    private var _binding: FragmentColorBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = ColorFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentColorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val items = createColorOptions()
        val adapter = StatusBarAdapter(items) { option ->
            applyColorOption(option)
        }
        binding.recyclerView.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun createColorOptions(): List<StatusBarOption> {
        val context = requireContext()
        return listOf(
            // 预设颜色
            StatusBarOption(
                id = "color_red",
                title = getString(R.string.color_change),
                description = "红色",
                iconRes = R.drawable.ic_color,
                iconTint = ContextCompat.getColor(context, R.color.status_bar_red),
                colorPreview = ContextCompat.getColor(context, R.color.status_bar_red)
            ),
            StatusBarOption(
                id = "color_green",
                title = getString(R.string.color_change),
                description = "绿色",
                iconRes = R.drawable.ic_color,
                iconTint = ContextCompat.getColor(context, R.color.status_bar_green),
                colorPreview = ContextCompat.getColor(context, R.color.status_bar_green)
            ),
            StatusBarOption(
                id = "color_blue",
                title = getString(R.string.color_change),
                description = "蓝色",
                iconRes = R.drawable.ic_color,
                iconTint = ContextCompat.getColor(context, R.color.status_bar_blue),
                colorPreview = ContextCompat.getColor(context, R.color.status_bar_blue)
            ),
            StatusBarOption(
                id = "color_purple",
                title = getString(R.string.color_change),
                description = "紫色",
                iconRes = R.drawable.ic_color,
                iconTint = ContextCompat.getColor(context, R.color.status_bar_purple),
                colorPreview = ContextCompat.getColor(context, R.color.status_bar_purple)
            ),
            StatusBarOption(
                id = "color_orange",
                title = getString(R.string.color_change),
                description = "橙色",
                iconRes = R.drawable.ic_color,
                iconTint = ContextCompat.getColor(context, R.color.status_bar_orange),
                colorPreview = ContextCompat.getColor(context, R.color.status_bar_orange)
            ),
            StatusBarOption(
                id = "color_teal",
                title = getString(R.string.color_change),
                description = "青色",
                iconRes = R.drawable.ic_color,
                iconTint = ContextCompat.getColor(context, R.color.status_bar_teal),
                colorPreview = ContextCompat.getColor(context, R.color.status_bar_teal)
            ),
            StatusBarOption(
                id = "color_pink",
                title = getString(R.string.color_change),
                description = "粉色",
                iconRes = R.drawable.ic_color,
                iconTint = ContextCompat.getColor(context, R.color.status_bar_pink),
                colorPreview = ContextCompat.getColor(context, R.color.status_bar_pink)
            ),
            StatusBarOption(
                id = "color_indigo",
                title = getString(R.string.color_change),
                description = "靛蓝色",
                iconRes = R.drawable.ic_color,
                iconTint = ContextCompat.getColor(context, R.color.status_bar_indigo),
                colorPreview = ContextCompat.getColor(context, R.color.status_bar_indigo)
            ),
            // 特殊效果
            StatusBarOption(
                id = "color_transparent",
                title = getString(R.string.color_transparent),
                description = getString(R.string.color_transparent_desc),
                iconRes = R.drawable.ic_color,
                iconTint = ContextCompat.getColor(context, R.color.tab_color)
            ),
            StatusBarOption(
                id = "color_reset",
                title = getString(R.string.action_reset),
                description = "恢复默认透明状态栏",
                iconRes = R.drawable.ic_color,
                iconTint = ContextCompat.getColor(context, R.color.gray_500)
            )
        )
    }

    private fun applyColorOption(option: StatusBarOption) {
        when (option.id) {
            "color_transparent" -> {
                StatusBarHelper.setStatusBarTransparent(requireActivity())
                StatusBarHelper.setStatusBarLightIcon(requireActivity(), true)
            }
            "color_reset" -> {
                StatusBarHelper.resetStatusBar(requireActivity())
            }
            else -> {
                // 颜色选项
                option.colorPreview?.let { color ->
                    StatusBarHelper.setStatusBarColor(requireActivity(), color)
                    val isLightBackground = isLightColor(color)
                    StatusBarHelper.setStatusBarLightIcon(requireActivity(), isLightBackground)
                }
            }
        }
    }

    /**
     * 判断颜色是否为浅色
     */
    private fun isLightColor(color: Int): Boolean {
        val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness < 0.5
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
