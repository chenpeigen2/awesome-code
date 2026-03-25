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
 * 系统信息 Fragment
 */
class SystemFragment : Fragment(R.layout.fragment_info_list) {

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
        val info = SystemInfoHelper.getSystemInfo()

        val items = listOf(
            InfoItem("Android 版本", info.androidVersion),
            InfoItem("API Level", "${info.apiLevel}"),
            InfoItem("安全补丁", info.securityPatch),
            InfoItem("设备型号", info.model),
            InfoItem("设备代号", info.device),
            InfoItem("品牌", info.brand),
            InfoItem("制造商", info.manufacturer),
            InfoItem("产品名称", info.product),
            InfoItem("主板", info.board),
            InfoItem("硬件", info.hardware),
            InfoItem("Build ID", info.buildId),
            InfoItem("引导程序", info.bootloader),
            InfoItem("基带版本", info.radioVersion),
            InfoItem("内核版本", info.kernelVersion),
            InfoItem("是否模拟器", if (info.isEmulator) "是" else "否")
        )

        adapter.submitList(items)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = SystemFragment()
    }
}
