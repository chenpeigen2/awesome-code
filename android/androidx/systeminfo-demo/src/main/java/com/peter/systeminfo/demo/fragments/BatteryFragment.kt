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
 * 电池状态信息 Fragment
 */
class BatteryFragment : Fragment(R.layout.fragment_info_list) {

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
        val info = SystemInfoHelper.getBatteryInfo(requireContext())

        val items = listOf(
            InfoItem("电量", "${info.level}%"),
            InfoItem("状态", info.status),
            InfoItem("健康状态", info.health),
            InfoItem("温度", String.format("%.1f °C", info.temperature)),
            InfoItem("电压", "${info.voltage} mV"),
            InfoItem("电池技术", info.technology),
            InfoItem("充电方式", info.plugged),
            InfoItem("容量", if (info.capacity > 0) "${info.capacity} mAh" else "N/A")
        )

        adapter.submitList(items)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = BatteryFragment()
    }
}
