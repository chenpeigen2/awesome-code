package com.peter.network.demo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.peter.network.demo.databinding.FragmentInterceptorBinding
import com.peter.network.demo.viewmodel.InterceptorViewModel

class InterceptorFragment : Fragment() {

    private var _binding: FragmentInterceptorBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<InterceptorViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInterceptorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnHeaderDemo.setOnClickListener { viewModel.runHeaderDemo() }
        binding.btnCallbackDemo.setOnClickListener { viewModel.runCallbackDemo() }
        binding.btnRefreshLogs.setOnClickListener { viewModel.refreshLogs() }
        binding.btnClearLogs.setOnClickListener { viewModel.clearLogs() }

        viewModel.status.observe(viewLifecycleOwner) { binding.tvStatus.text = it }
        viewModel.logs.observe(viewLifecycleOwner) { binding.tvLogs.text = it }
        viewModel.refreshLogs()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
