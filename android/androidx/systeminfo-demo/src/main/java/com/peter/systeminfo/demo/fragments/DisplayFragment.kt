package com.peter.systeminfo.demo.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.systeminfo.demo.R
import com.peter.systeminfo.demo.adapter.InfoAdapter
import com.peter.systeminfo.demo.adapter.InfoItem
import com.peter.systeminfo.demo.databinding.FragmentInfoListBinding
import com.peter.systeminfo.demo.helper.SystemInfoHelper

/**
 * 屏幕显示信息 Fragment
 */
class DisplayFragment : Fragment(R.layout.fragment_info_list) {

    private var _binding: FragmentInfoListBinding? = null
    private val binding get() = _binding!!

    private val adapter = InfoAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentInfoListBinding.bind(view)

        setupRecyclerView()
        loadData()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun loadData() {
        val activity = requireActivity()
        val info = SystemInfoHelper.getDisplayInfo(activity)

        val items = listOf(
            InfoItem("分辨率", info.resolution),
            InfoItem("宽度 (px)", "${info.widthPixels}"),
            InfoItem("高度 (px)", "${info.heightPixels}"),
            InfoItem("宽度 (dp)", String.format("%.1f", info.screenWidthDp)),
            InfoItem("高度 (dp)", String.format("%.1f", info.screenHeightDp)),
            InfoItem("密度 (density)", String.format("%.2f", info.density)),
            InfoItem("DPI (densityDpi)", "${info.densityDpi}"),
            InfoItem("缩放密度 (scaledDensity)", String.format("%.2f", info.scaledDensity)),
            InfoItem("物理 X DPI", String.format("%.1f", info.xdpi)),
            InfoItem("物理 Y DPI", String.format("%.1f", info.ydpi)),
            InfoItem("刷新率", String.format("%.1f Hz", info.refreshRate)),
            InfoItem("圆形屏幕", if (info.isScreenRound) "是" else "否"),
            InfoItem("HDR 支持", if (info.isHdrSupported) "是" else "否")
        )

        adapter.submitList(items)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = DisplayFragment()
    }
}
