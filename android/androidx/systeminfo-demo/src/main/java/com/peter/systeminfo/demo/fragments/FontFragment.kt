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
 * 字体配置信息 Fragment
 */
class FontFragment : Fragment(R.layout.fragment_info_list) {

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
        val info = SystemInfoHelper.getFontInfo(requireContext())

        val items = listOf(
            InfoItem("字体缩放", String.format("%.2f", info.fontScale)),
            InfoItem("屏幕密度", String.format("%.2f", info.density)),
            InfoItem("缩放密度", String.format("%.2f", info.scaledDensity)),
            InfoItem("小字体", "${info.smallFontSize} sp"),
            InfoItem("中等字体", "${info.mediumFontSize} sp"),
            InfoItem("大字体", "${info.largeFontSize} sp")
        )

        adapter.submitList(items)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FontFragment()
    }
}
