package com.example.aidl_client.fragment

import android.os.Bundle
import android.os.RemoteException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.aidl_client.databinding.FragmentBasicSyncBinding
import com.example.aidl_client.viewmodel.AidlViewModel

class BasicSyncFragment : Fragment() {
    private var _binding: FragmentBasicSyncBinding? = null
    private val viewModel: AidlViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBasicSyncBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.btnCalculate?.setOnClickListener { calculate() }
    }

    private fun calculate() {
        val service = viewModel.service.value
        if (service == null) {
            Toast.makeText(requireContext(), "服务未连接", Toast.LENGTH_SHORT).show()
            return
        }

        val num1Str = _binding?.etNum1?.text.toString()
        val num2Str = _binding?.etNum2?.text.toString()
        if (num1Str.isEmpty() || num2Str.isEmpty()) {
            Toast.makeText(requireContext(), "请输入两个数字", Toast.LENGTH_SHORT).show()
            return
        }

        val num1 = num1Str.toIntOrNull()
        val num2 = num2Str.toIntOrNull()
        if (num1 == null || num2 == null) {
            Toast.makeText(requireContext(), "请输入有效整数", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val result = service.add(num1, num2)
            _binding?.tvResult?.text = "结果: $num1 + $num2 = $result"
        } catch (e: RemoteException) {
            _binding?.tvResult?.text = "调用失败: ${e.message}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
